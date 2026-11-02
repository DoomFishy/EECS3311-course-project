package ai.llm;

public class LocalllmSource implements llmSource {
    @Override
    public String run(String context) {
        System.out.println("[LocalQwenSource] Running local model...");
        return "Local AI Response based on: " + context;
    }
}