package ai.controller;

import ai.template.AITemplate;

public class StudyController extends AITemplate {
    @Override
    public String execute() {
        if (llm == null) {
            throw new IllegalStateException("Error - StudyController: LLM not configured");
        }
        if (prompt == null || prompt.isBlank()) {
            throw new IllegalStateException("Error - StudyController: prompt required");
        }

        return llm.run(prompt + "\nCreate a focused study plan.");
    }
}
