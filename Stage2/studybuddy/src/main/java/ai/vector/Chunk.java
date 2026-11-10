package ai.vector;

public class Chunk {
    private final String text;
    private final float[] vector;

    public Chunk(String text, float[] vector) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Error - Chunk: text required");
        }
        if (vector == null || vector.length == 0) {
            throw new IllegalArgumentException("Error - Chunk: vector required");
        }

        this.text = text;
        this.vector = vector;
    }

    public String getText() {
        return text;
    }

    public float[] getVector() {
        return vector;
    }
}