package ai.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import ai.agent.EmbeddingTool;
import ai.factory.AIFactory;
import ai.template.AITemplate;
import ai.tool.Tool;
import ai.tool.ToolFactory;
import ai.vector.VectorStore;
import course.Course;
import course.assessment.Assessment;
import course.assessment.AssessmentType;
import course.material.Material;
import course.material.MaterialType;

public class AppFacade {
    private final AIFactory factory;
    private final Course course;
    private final List<String> conversationHistory = new ArrayList<>();

    public AppFacade(AIFactory factory, Course course) {
        this.factory = factory;
        this.course = course;
    }

    public Course getCourse() {
        return course;
    }

    public List<Assessment> getAssessments() {
        return course.getAssessments();
    }

    public void addAssessment(Assessment a) {
        course.addAssessment(a);
    }

    public List<Material> getMaterials() {
        return course.getMaterials();
    }

    public void addMaterial(Material m) {
        course.addMaterial(m);
    }

    public List<Material> getSelectedMaterials() {
        return course.getSelectedMaterials();
    }

    public void setSelectedMaterialsIds(List<String> ids) {
        course.setSelectedMaterialIds(ids);
    }

    public void setSelectedMaterials(List<Material> selectedMaterials) {
        List<String> ids = new ArrayList<>();
        if (selectedMaterials != null) {
            for (Material material : selectedMaterials) {
                if (material != null) {
                    ids.add(material.getId());
                }
            }
        }
        setSelectedMaterialsIds(ids);
    }

    public List<Assessment> getUpcomingAssessments() {
        LocalDate today = LocalDate.now();

        return getAssessments().stream()
                .filter(a -> a.getDeadline() != null)
                .filter(a -> !a.getDeadline().toLocalDate().isBefore(today))
                .sorted((a, b) -> a.getDeadline().compareTo(b.getDeadline()))
                .toList();
    }

    public List<Assessment> getThisWeekAssessments() {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = startOfWeek.plusDays(6);

        return getUpcomingAssessments().stream()
                .filter(a -> {
                    LocalDate dueDate = a.getDeadline().toLocalDate();
                    return (!dueDate.isBefore(startOfWeek)) && (!dueDate.isAfter(endOfWeek));
                })
                .toList();
    }

    public void clearConversationHistory() {
        conversationHistory.clear();
    }

    public String askQuestion(String question) {
        if (question == null || question.isBlank()) {
            throw new IllegalArgumentException("Question is required.");
        }

        StringBuilder promptBuilder = new StringBuilder();
        if (!conversationHistory.isEmpty()) {
            promptBuilder.append("Previous conversation:\n");
            for (String entry : conversationHistory) {
                promptBuilder.append(entry).append("\n");
            }
            promptBuilder.append("\n");
        }

        if (!getSelectedMaterials().isEmpty()) {
            promptBuilder.append("Course materials:\n");
            for (Material material : getSelectedMaterials()) {
                if (material == null) continue;
                String materialText = material.getContent();
                if (materialText == null || materialText.isBlank()) {
                    continue;
                }
                promptBuilder.append("--- ").append(material.getFilename()).append(" ---\n");
                int maxLength = 3000;
                if (materialText.length() > maxLength) {
                    promptBuilder.append(materialText.substring(0, maxLength)).append("\n...[truncated for length]...\n");
                } else {
                    promptBuilder.append(materialText.trim()).append("\n");
                }
            }
        }

        if (!getAssessments().isEmpty()) {
            promptBuilder.append("Assessments:\n");
            for (Assessment assessment : getAssessments()) {
                promptBuilder.append("- ").append(assessment.getName())
                        .append(" | ").append(assessment.getType())
                        .append(" | weight ").append(assessment.getWeight())
                        .append(" | due ").append(assessment.getDeadline().toLocalDate())
                        .append("\n");
            }
        }

        if (promptBuilder.length() == 0) {
            promptBuilder.append("Course: ").append(course.getName()).append("\n");
        }

        promptBuilder.append("\nQuestion: ").append(question).append("\n");

        LanguageQAController controller = configureController(new LanguageQAController(), promptBuilder.toString());
        String response = controller.execute();
        conversationHistory.add("User: " + question);
        conversationHistory.add("AI: " + response);
        return response;
    }

    public String generateReport() {
        ReportController controller = configureController(
                new ReportController(),
                ""
        );
        return controller.execute();
    }

    public String generateDeadlineSummary() {
        StringBuilder sb = new StringBuilder();
        List<Assessment> thisWeek = getAssessments();

        if (thisWeek.isEmpty()) {
            return "No upcoming assessments for this week.";
        }

        sb.append("This week's deadlines:\n");
        for (Assessment assessment : thisWeek) {
            sb.append("- ").append(assessment.getName())
              .append(" (")
              .append(assessment.getType())
              .append(")")
              .append(" due ")
              .append(assessment.getDeadline().toLocalDate())
              .append(" | weight ")
              .append(assessment.getWeight())
              .append("\n");
        }
        return sb.toString();
    }

    public String generateTaskList() {
        StringBuilder sb = new StringBuilder();
        sb.append("This week's tasks:\n");
        List<Assessment> thisWeek = getThisWeekAssessments();
        if (thisWeek.isEmpty()) {
            sb.append("- No tasks scheduled for this week.");
            return sb.toString();
        }

        for (Assessment assessment : thisWeek) {
            sb.append("- Finish ")
              .append(assessment.getName())
              .append(" before ")
              .append(assessment.getDeadline().toLocalDate())
              .append("\n");
        }
        return sb.toString();
    }

    public String uploadFile(String filepath) {
        if (filepath == null || filepath.isBlank()) {
            throw new IllegalArgumentException("Error - AppFacade: Filepath required!");
        }

        String normalizedPath = filepath.trim();
        if (getMaterials().stream().anyMatch(material -> material.getFilepath().equals(normalizedPath))) {
            return "File already uploaded: " + normalizedPath;
        }

        String filename = Paths.get(normalizedPath).getFileName() != null
                ? Paths.get(normalizedPath).getFileName().toString()
                : normalizedPath;
        String lowerCasePath = normalizedPath.toLowerCase(Locale.ROOT);

        MaterialType type;
        if (lowerCasePath.endsWith(".pdf")) {
            type = MaterialType.PDF;
        } else if (lowerCasePath.endsWith(".ppt") || lowerCasePath.endsWith(".pptx") || lowerCasePath.endsWith(".key")) {
            type = MaterialType.Slide;
        } else {
            type = MaterialType.Link;
        }

        Material material = new Material("m" + (getMaterials().size() + 1), filename, normalizedPath, type);
        addMaterial(material);

        try {
            String extractedText = readFileContent(normalizedPath);
            if (extractedText == null || extractedText.isBlank()) {
                material.setContent("Uploaded file: " + filename + ". No readable text content could be extracted from this file format. The file is available in the course materials list.");
            } else {
                material.setContent(extractedText);
            }
        } catch (Exception ignored) {
            material.setContent("Uploaded file: " + filename + ". No readable text content could be extracted from this file format. The file is available in the course materials list.");
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Uploaded file: ").append(normalizedPath).append("\n");
        sb.append("Course materials now available for AI controllers:\n");
        for (Material courseMaterial : course.getMaterials()) {
            sb.append("- ").append(courseMaterial.getFilename()).append("\n");
        }
        return sb.toString();
    }

    public void setGradeGoal(float goal) {
        if (goal < 0) {
            throw new IllegalArgumentException("Grade goal cannot be negative.");
        }
        course.setGradeReport(goal);
    }

    public float getGradeGoal() {
        return course.getGradeReport() == null ? 0f : course.getGradeReport().getCourseGoal();
    }

    public String getCurrentGradeSummary() {
        double average = getAssessments().stream().mapToDouble(Assessment::getGrade).average().orElse(0.0);
        return String.format(Locale.US, "Current average: %.2f%% | Grade goal: %.2f%%", average, getGradeGoal());
    }

    public void setAIMode(String mode) {
        AIFactory.setMode(mode);
    }

    public String getAIMode() {
        String mode = System.getProperty("studybuddy.ai.mode", System.getenv().getOrDefault("STUDYBUDDY_AI_MODE", "auto"));
        return mode == null ? "auto" : mode;
    }

    private String readFileContent(String filepath) {
        try {
            String raw = readMaterial(filepath);
            if (raw != null && !raw.isBlank()) {
                String normalized = raw.trim();
                if (normalized.toLowerCase(Locale.ROOT).contains("pdf not found")
                        || normalized.toLowerCase(Locale.ROOT).contains("unable to read pdf")) {
                    return "";
                }
                return normalized;
            }
        } catch (Exception ignored) {
            // Fall through to normal file read.
        }

        try {
            return Files.readString(Path.of(filepath), StandardCharsets.UTF_8);
        } catch (IOException ignored) {
            return "";
        }
    }

    private <T extends AITemplate> T configureController(T controller, String prompt) {
        controller.setMaterials(getMaterials());
        controller.setAssessments(getAssessments());
        controller.setLLM(factory.createLlmSource());
        String fullPrompt = buildControllerPrompt(prompt);
        controller.setPrompt(fullPrompt);
        return controller;
    }

    private String buildControllerPrompt(String prompt) {
        StringBuilder sb = new StringBuilder();
        if (prompt != null && !prompt.isBlank()) {
            sb.append(prompt).append("\n");
        }

        sb.append("Course: ").append(course.getName()).append("\n");

        if (!getSelectedMaterials().isEmpty()) {
            sb.append("Course materials:\n");
            for (Material material : getSelectedMaterials()) {
                if (material == null) continue;
                String materialText = material.getContent();
                if (materialText == null || materialText.isBlank()) {
                    continue;
                }
                sb.append("--- ").append(material.getFilename()).append(" ---\n");
                int maxLength = 3000;
                if (materialText.length() > maxLength) {
                    sb.append(materialText.substring(0, maxLength)).append("\n...[truncated for length]...\n");
                } else {
                    sb.append(materialText.trim()).append("\n");
                }
            }
        }

        if (!getAssessments().isEmpty()) {
            sb.append("Manual coursework assessments:\n");
            for (Assessment assessment : getAssessments()) {
                sb.append("- ").append(assessment.getName())
                        .append(" | ").append(assessment.getType())
                        .append(" | weight ").append(assessment.getWeight())
                        .append(" | due ").append(assessment.getDeadline().toLocalDate())
                        .append("\n");
            }
        }

        return sb.toString();
    }

    public String generateNotes() {
        NotesController controller = configureController(
                new NotesController(),
                ""
        );
        return controller.execute();    
    }

    public String generateQuiz() {
        QuizController controller = configureController(
                new QuizController(),
                ""
        );
        return controller.execute();    
    }

    public String generateStudyPlan() {
        StudyController controller = configureController(
                new StudyController(),
                ""
        );
        return controller.execute();        
    }

    public String generateCourseReport() {
        ReportController controller = configureController(
                new ReportController(),
                ""
        );
        return controller.execute();        
    }
    public EmbeddingTool createEmbeddingTool() {
        return new EmbeddingTool(factory.createEmbeddings(), factory.createVectorStore());
    }

    public VectorStore createVectorStore() {
        return factory.createVectorStore();
    }

    public String readMaterial(String materialPath) {
        Tool tool = ToolFactory.findBestTool(materialPath);
        return tool.execute(materialPath);
    }

    public Tool resolveTool(String materialPath) {
        return ToolFactory.findBestTool(materialPath);
    }

    public void addAssessment(String name, String dateText, String weightText, String typeText) {
        String cleanedName = name == null ? "" : name.trim();
        if (cleanedName.isBlank()) {
            throw new IllegalArgumentException("Assessment name is required.");
        }

        LocalDateTime deadline = parseManualDate(dateText);
        if (deadline == null) {
            throw new IllegalArgumentException("Assessment date could not be parsed. Use MM/dd, M/d, MMM d, or yyyy-MM-dd.");
        }

        float weight = 0.25f;
        try {
            if (weightText != null && !weightText.isBlank()) {
                weight = Float.parseFloat(weightText.trim()) / 100.0f;
            }
        } catch (NumberFormatException ignored) {
            weight = 0.25f;
        }

        AssessmentType type = AssessmentType.ASSIGNMENT;
        if (typeText != null && !typeText.isBlank()) {
            try {
                type = AssessmentType.valueOf(typeText.trim().toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException ignored) {
                type = AssessmentType.ASSIGNMENT;
            }
        }

        
        String normalizedName = name.trim();
        if (getAssessments().stream().anyMatch(existing -> existing.getName().equalsIgnoreCase(normalizedName))) {
            return;
        }

        float normalizedWeight = weight <= 0 ? 0.25f : weight;
        addAssessment(new Assessment(
                "a" + (getAssessments().size() + 1),
                normalizedName,
                100,
                deadline,
                normalizedWeight,
                type
        ));

    }

    private LocalDateTime parseManualDate(String dateText) {
        if (dateText == null || dateText.isBlank()) {
            return null;
        }

        String raw = dateText.trim();
        List<DateTimeFormatter> formatters = Arrays.asList(
                DateTimeFormatter.ISO_LOCAL_DATE,
                DateTimeFormatter.ofPattern("MM/dd/yyyy"),
                DateTimeFormatter.ofPattern("M/d/yyyy"),
                DateTimeFormatter.ofPattern("yyyy/MM/dd"),
                DateTimeFormatter.ofPattern("MMM d, yyyy"),
                DateTimeFormatter.ofPattern("MMMM d, yyyy"),
                DateTimeFormatter.ofPattern("MM/dd"),
                DateTimeFormatter.ofPattern("M/d"),
                DateTimeFormatter.ofPattern("MMM d"),
                DateTimeFormatter.ofPattern("MMMM d")
        );

        for (DateTimeFormatter formatter : formatters) {
            try {
                LocalDate date = LocalDate.parse(raw, formatter);
                return date.atTime(23, 59);
            } catch (DateTimeParseException ignored) {
                // Try the next supported format.
            }
        }

        for (DateTimeFormatter formatter : Arrays.asList(
                DateTimeFormatter.ofPattern("MM/dd"),
                DateTimeFormatter.ofPattern("M/d"),
                DateTimeFormatter.ofPattern("MMM d"),
                DateTimeFormatter.ofPattern("MMMM d"))) {
            try {
                LocalDate date = LocalDate.parse(raw, formatter);
                LocalDate currentYearDate = date.withYear(LocalDate.now().getYear());
                return currentYearDate.atTime(23, 59);
            } catch (DateTimeParseException ignored) {
                // Try the next supported format.
            }
        }
        return null;
    }
}
