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
import course.material.Material;
import course.material.MaterialType;
import java.nio.file.Paths;
import java.util.List;

public class AppFacade {
    private final AIFactory factory;
    private final Course course;

    public AppFacade(AIFactory factory, Course course) {
        this.factory = factory;
        this.course = course;
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
        List<String> materials = course.getMaterials().stream().map(Material::getFilepath).toList();
        List<String> assessments = course.getAssessments().stream().map(Assessment::getName).toList();

        return agent.execute(question, String.join("\n", materials) + "\n" + String.join("\n", assessments), llm).getResult();
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
        String lowerCasePath = normalizedPath.toLowerCase();

        MaterialType type;
        if (lowerCasePath.endsWith(".pdf")) {
            type = MaterialType.PDF;
        } else if (lowerCasePath.endsWith(".ppt") || lowerCasePath.endsWith(".pptx") || lowerCasePath.endsWith(".key")) {
            type = MaterialType.Slide;
        } else {
            type = MaterialType.Link;
        }

        String id = "m" + (course.getMaterials().size() + 1);
        course.addMaterial(new Material(id, filename, normalizedPath, type));
    }

    private <T extends AITemplate> T configureController(T controller, String prompt) {
        controller.setMaterials(course.getMaterials());
        controller.setAssessments(course.getAssessments());
        controller.setLLM(factory.createLlmSource());
        controller.setPrompt(prompt == null ? "Course: " + course.getName() : prompt);
        return controller;
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
}
