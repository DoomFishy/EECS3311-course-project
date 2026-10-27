
public class Material {

    private String id;
    private String filename;
    private String filepath;
    private MaterialType type;


    public Material(String id, String filename, String filepath, MaterialType type) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Error - Materials: ID required!");
        if (filename == null || filename.isBlank()) throw new IllegalArgumentException("Error - Materials: Filename required!");
        if (filepath == null || filepath.isBlank()) throw new IllegalArgumentException("Error - Materials: Filepath required!");
        if (type == null) throw new IllegalArgumentException("Error - Materials: Type is invalid");

        this.id = id;
        this.filename = filename;
        this.filepath = filepath;
        this.type = type;
    }

    public String getFilepath() {
        return filepath;
    }
}
