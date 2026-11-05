package ai.template;

import ai.agent.Agent;
import ai.agent.EmbeddingTool;
import ai.llm.llmSource;
import course.assessment.Assessment;
import course.material.Material;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AITemplate {
    protected String prompt;
    protected List<Material> materials;
    protected List<Assessment> assessments;
    protected Agent agent;
    protected llmSource llm;
    protected EmbeddingTool embed;

    public AITemplate() {
        this.materials = new ArrayList<>();
        this.assessments = new ArrayList<>();
        this.agent = new Agent();
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public List<Material> getMaterials() {
        return Collections.unmodifiableList(materials);
    }

    public void setMaterials(List<Material> materials) {
        this.materials = materials == null ? new ArrayList<>() : new ArrayList<>(materials);
    }

    public List<Assessment> getAssessments() {
        return Collections.unmodifiableList(assessments);
    }

    public void setAssessments(List<Assessment> assessments) {
        this.assessments = assessments == null ? new ArrayList<>() : new ArrayList<>(assessments);
    }

    public void setEmbed(EmbeddingTool embed) {
        this.embed = embed;
    }

    public void setLLM(llmSource llm) {
        this.llm = llm;
    }

    public abstract String execute();
}
