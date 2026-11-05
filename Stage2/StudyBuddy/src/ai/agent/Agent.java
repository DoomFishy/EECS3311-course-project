package ai.agent;

import ai.llm.llmSource;

public class Agent {

    public AgentResult execute(String prompt, String data, llmSource llm) {
        if (prompt == null || prompt.isBlank()) {
            throw new IllegalArgumentException("Error - Agent: prompt required");
        }
        if (llm == null) {
            throw new IllegalArgumentException("Error - Agent: llm source required");
        }

        String combined = data == null || data.isBlank() ? prompt : prompt + "\nContext:\n" + data;
        String response = llm.run(combined);
        float confidence = calculateConfidence(response);

        return new AgentResult(confidence, data == null ? "n/a" : data, response);
    }

    private float calculateConfidence(String response) {
        if (response == null || response.isBlank()) {
            return 0f;
        }

        return Math.min(1f, response.length() / 200f);
    }
}
