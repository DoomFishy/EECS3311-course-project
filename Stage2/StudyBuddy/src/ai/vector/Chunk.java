package ai.vector;

public class Chunk {
    private String text;
    private float[] vector;

    public Chunk(String text, float[] vector) {
        this.text = text;
        this.vector = vector;
    }

    public String getText() { return text; }
    public float[] getVector() { return vector; }
}