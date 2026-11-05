package ai.agent;

public class AgentResult {
    private final float confidence;
    private final String citations;
    private final String result;

    public AgentResult(float confidence, String citations, String result) {
        this.confidence = confidence;
        this.citations = citations;
        this.result = result;
    }

    public float getConfidence() {
        return confidence;
    }

    public String getCitations() {
        return citations;
    }

    public String getResult() {
        return result;
    }
}
