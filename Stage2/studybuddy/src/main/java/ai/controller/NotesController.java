package ai.controller;

import ai.template.AITemplate;

public class NotesController extends AITemplate {
    @Override
    public String execute() {
        if (llm == null) {
            throw new IllegalStateException("Error - NotesController: LLM not configured");
        }
        if (prompt == null || prompt.isBlank()) {
            throw new IllegalStateException("Error - NotesController: prompt required");
        }

        return llm.run(prompt + "\nReturn concise notes with headings and bullet points.");
    }
}
