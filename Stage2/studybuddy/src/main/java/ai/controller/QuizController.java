package ai.controller;

import ai.template.AITemplate;

public class QuizController extends AITemplate {
    @Override
    public String execute() {
        if (llm == null) {
            throw new IllegalStateException("Error - QuizController: LLM not configured");
        }
        if (prompt == null || prompt.isBlank()) {
            throw new IllegalStateException("Error - QuizController: prompt required");
        }

        return llm.run(prompt + "\nCreate a short quiz with answers.");
    }
}
