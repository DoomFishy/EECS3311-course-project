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

import ai.agent.Agent;
import ai.agent.EmbeddingTool;
import ai.factory.AIFactory;
import ai.llm.llmSource;
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

    public List<Material> getMaterials() {
        return course.getMaterials();
    }

    public List<Material> getSelectedMaterials() {
        return course.getSelectedMaterials();
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
        course.setSelectedMaterialIds(ids);
    }

    public List<Assessment> getUpcomingAssessments() {
        LocalDate today = LocalDate.now();

        return course.getAssessments().stream()
                .filter(a -> !a.isCompleted())
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
        llmSource llm = factory.createLlmSource();
        Agent agent = new Agent();

        List<String> materials = getSelectedMaterials().stream()
                .map(Material::getContent)
                .filter(content -> content != null && !content.isBlank())
                .toList();
        List<String> assessments = course.getAssessments().stream()
                .map(Assessment::getName)
                .toList();

        StringBuilder context = new StringBuilder();
        if (!conversationHistory.isEmpty()) {
            context.append("Previous conversation:\n");
            for (String entry : conversationHistory) {
                context.append(entry).append("\n");
            }
            context.append("\n");
        }
        if (!materials.isEmpty()) {
            context.append(String.join("\n", materials)).append("\n");
        }
        if (!assessments.isEmpty()) {
            context.append(String.join("\n", assessments));
        }

        if (context.isEmpty()) {
            context.append("Course: ").append(course.getName()).append("\n");
        }

        String response = agent.execute(question, context.toString(), llm).getResult();
        conversationHistory.add("User: " + question);
        conversationHistory.add("AI: " + response);
        return response;
    }

    public String generateReport() {
        llmSource llm = factory.createLlmSource();
        String prompt = buildCourseContextPrompt(
                "Generate a brief progress report for this course. Include new materials uploaded since last time, upcoming work this week, and any grade risks."
        );
        return llm.run(prompt);
    }

    public String generateDeadlineSummary() {
        StringBuilder sb = new StringBuilder();
        List<Assessment> deadlines = getAssessments().stream()
                .filter(a -> !a.isCompleted())
                .filter(a -> a.getDeadline() != null)
                .sorted((a, b) -> a.getDeadline().compareTo(b.getDeadline()))
                .toList();

        if (deadlines.isEmpty()) {
            return "No upcoming assessments.";
        }

        sb.append("Deadlines:\n");
        for (Assessment assessment : deadlines) {
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

    public void uploadFile(String filepath) {
        if (filepath == null || filepath.isBlank()) {
            throw new IllegalArgumentException("Error - AppFacade: Filepath required!");
        }

        String normalizedPath = filepath.trim();
        if (course.getMaterials().stream().anyMatch(material -> material.getFilepath().equals(normalizedPath))) {
            return;
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

        Material material = new Material("m" + (course.getMaterials().size() + 1), filename, normalizedPath, type);
        course.addMaterial(material);

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
    }

    public String uploadFileAndAnalyze(String filepath) {
        uploadFile(filepath);

        if (course.getMaterials().isEmpty()) {
            return "Uploaded file: " + filepath;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Uploaded file: ").append(filepath).append("\n");
        sb.append("Course materials now available for AI controllers:\n");
        for (Material material : course.getMaterials()) {
            sb.append("- ").append(material.getFilename()).append("\n");
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
        double average = course.getAssessments().stream().mapToDouble(Assessment::getGrade).average().orElse(0.0);
        return String.format(Locale.US, "Current average: %.2f%% | Grade goal: %.2f%%", average, getGradeGoal());
    }

    public void setAIMode(String mode) {
        AIFactory.setMode(mode);
    }

    public String getAIMode() {
        String mode = System.getProperty("studybuddy.ai.mode", System.getenv().getOrDefault("STUDYBUDDY_AI_MODE", "auto"));
        return mode == null ? "auto" : mode;
    }

    private String buildCourseContextPrompt(String instruction) {
        StringBuilder sb = new StringBuilder();
        sb.append(instruction).append("\n\n");
        sb.append("Course: ").append(course.getName()).append("\n");
        sb.append("Course grade goal: ").append(getGradeGoal()).append("%\n");

        if (!getSelectedMaterials().isEmpty()) {
            sb.append("Selected materials:\n");
            for (Material material : getSelectedMaterials()) {
                if (material.getContent() != null && !material.getContent().isBlank()) {
                    sb.append("--- ").append(material.getFilename()).append(" ---\n");
                    String text = material.getContent();
                    if (text.length() > 3000) {
                        text = text.substring(0, 3000) + "\n...[truncated]\n";
                    }
                    sb.append(text).append("\n");
                }
            }
        }

        if (!course.getAssessments().isEmpty()) {
            sb.append("Assessments:\n");
            for (Assessment assessment : course.getAssessments()) {
                sb.append("- ").append(assessment.getName())
                        .append(" | type: ").append(assessment.getType())
                        .append(" | due: ").append(assessment.getDeadline().toLocalDate())
                        .append(" | weight: ").append(assessment.getWeight())
                        .append(" | score: ").append(assessment.getScore())
                        .append("\n");
            }
        }

        return sb.toString();
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
        controller.setMaterials(course.getMaterials());
        controller.setAssessments(course.getAssessments());
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

        if (!course.getAssessments().isEmpty()) {
            sb.append("Manual coursework assessments:\n");
            for (Assessment assessment : course.getAssessments()) {
                sb.append("- ").append(assessment.getName())
                        .append(" | ").append(assessment.getType())
                        .append(" | weight ").append(assessment.getWeight())
                        .append(" | due ").append(assessment.getDeadline().toLocalDate())
                        .append("\n");
            }
        }

        return sb.toString();
    }

    public String generateNotes(String prompt) {
        NotesController controller = configureController(new NotesController(), prompt);
        return controller.execute();
    }

    public String generateQuiz(String prompt) {
        QuizController controller = configureController(new QuizController(), prompt);
        return controller.execute();
    }

    public String generateStudyPlan(String prompt) {
        StudyController controller = configureController(new StudyController(), prompt);
        return controller.execute();
    }

    public String generateCourseReport(String prompt) {
        ReportController controller = configureController(new ReportController(), prompt);
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

    public void addAssessment(String name, LocalDateTime deadline, float weight, AssessmentType type) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Assessment name is required.");
        }
        if (deadline == null) {
            throw new IllegalArgumentException("Assessment deadline is required.");
        }
        if (type == null) {
            throw new IllegalArgumentException("Assessment type is required.");
        }

        String normalizedName = name.trim();
        if (course.getAssessments().stream().anyMatch(existing -> existing.getName().equalsIgnoreCase(normalizedName))) {
            return;
        }

        float normalizedWeight = weight <= 0 ? 0.25f : weight;
        double existingWeight = course.getAssessments().stream().mapToDouble(Assessment::getWeight).sum();
        if (existingWeight + normalizedWeight > 1.0 + 1e-9) {
            throw new IllegalArgumentException("Total assessment weight cannot exceed 100%.");
        }
        course.addAssessment(new Assessment(
                "a" + (course.getAssessments().size() + 1),
                normalizedName,
                100,
                deadline,
                normalizedWeight,
                type
        ));
    }

    public void addManualAssessment(String name, String dateText, String weightText, String typeText) {
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

        addAssessment(cleanedName, deadline, weight, type);
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
