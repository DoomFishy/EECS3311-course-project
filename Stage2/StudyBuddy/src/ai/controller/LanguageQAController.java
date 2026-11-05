package ai.controller;

import ai.template.AITemplate;

public class LanguageQAController extends AITemplate {
    @Override
    public String execute() {
        if (llm == null) {
            throw new IllegalStateException("Error - LanguageQAController: LLM not configured");
        }
        if (prompt == null || prompt.isBlank()) {
            throw new IllegalStateException("Error - LanguageQAController: prompt required");
        }

        return llm.run(prompt + "\nAnswer clearly and briefly using the course context.");
    }
}
