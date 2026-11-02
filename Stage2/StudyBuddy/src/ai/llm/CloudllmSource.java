package ai.llm;

public class CloudllmSource implements llmSource {
    @Override
    public String run(String context) {
        System.out.println("[CloudQwenSource] Sending prompt to OpenRouter...");
        // TODO: In the future, put your HttpClient OpenRouter code here!
        // For now, return a mock response.
        return "Cloud AI Response (from OpenRouter) based on: " + context;
    }
}