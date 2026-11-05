package ai.controller;

import ai.template.AITemplate;

public class ReportController extends AITemplate {
    @Override
    public String execute() {
        if (llm == null) {
            throw new IllegalStateException("Error - ReportController: LLM not configured");
        }
        if (prompt == null || prompt.isBlank()) {
            throw new IllegalStateException("Error - ReportController: prompt required");
        }

        return llm.run(prompt + "\nSummarize the student performance and next steps.");
    }
}
