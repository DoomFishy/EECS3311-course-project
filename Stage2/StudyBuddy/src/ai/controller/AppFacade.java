package ai.controller;

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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;


public class AppFacade {
    private final AIFactory factory;
    private final Course course;

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

    public String askQuestion(String question) {
        llmSource llm = factory.createLlmSource();
        Agent agent = new Agent();
        List<String> materials = course.getMaterials().stream()
                .map(Material::getContent)
                .filter(content -> content != null && !content.isBlank())
                .toList();
        List<String> assessments = course.getAssessments().stream()
                .map(Assessment::getName)
                .toList();

        StringBuilder context = new StringBuilder();
        if (!materials.isEmpty()) {
            context.append(String.join("\n", materials)).append("\n");
        }
        if (!assessments.isEmpty()) {
            context.append(String.join("\n", assessments));
        }

        return agent.execute(question, context.toString(), llm).getResult();
    }

    public String generateReport() {
        llmSource llm = factory.createLlmSource();
        return llm.run("Generate a report for course: " + course.getName());
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

        if (!course.getMaterials().isEmpty()) {
            sb.append("Course materials:\n");
            for (Material material : course.getMaterials()) {
                if (material == null) {
                    continue;
                }
                String materialText = material.getContent();
                if (materialText == null || materialText.isBlank()) {
                    sb.append("--- ").append(material.getFilename()).append(" ---\n");
                    sb.append("This uploaded file is in the course materials list, but no readable text content could be extracted.\n");
                    continue;
                }
                sb.append("--- ").append(material.getFilename()).append(" ---\n");
                sb.append(materialText.trim()).append("\n");
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

    public String generateDeadlineSummary() {
        DeadlineController controller = configureController(new DeadlineController(), "Summarize upcoming coursework deadlines and priorities.");
        return controller.execute();
    }

    public String generateTaskList() {
        TaskController controller = configureController(new TaskController(), "Create a task list for the next deadlines.");
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
            throw new IllegalArgumentException("Assessment date could not be parsed. Use yyyy-MM-dd or MM/dd/yyyy.");
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
        for (DateTimeFormatter formatter : Arrays.asList(
                DateTimeFormatter.ISO_LOCAL_DATE,
                DateTimeFormatter.ofPattern("MM/dd/yyyy"),
                DateTimeFormatter.ofPattern("M/d/yyyy"),
                DateTimeFormatter.ofPattern("yyyy/MM/dd"),
                DateTimeFormatter.ofPattern("MMM d, yyyy"),
                DateTimeFormatter.ofPattern("MMMM d, yyyy")
        )) {
            try {
                LocalDate date = LocalDate.parse(raw, formatter);
                return date.atTime(23, 59);
            } catch (DateTimeParseException ignored) {
                // Try the next supported format.
            }
        }
        return null;
    }
}
