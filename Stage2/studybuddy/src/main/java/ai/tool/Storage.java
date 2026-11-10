package ai.tool;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Storage {
    private final String root;

    public Storage(String root) {
        if (root == null || root.isBlank()) {
            throw new IllegalArgumentException("Error - Storage: root required");
        }
        this.root = root;
    }

    public byte[] read(String path) throws IOException {
        return Files.readAllBytes(Path.of(root, path));
    }

    public List<String> list(String folder) throws IOException {
        Path dir = Path.of(root, folder);
        if (!Files.exists(dir)) {
            return new ArrayList<>();
        }

        List<String> names = new ArrayList<>();
        Files.list(dir).forEach(p -> names.add(p.getFileName().toString()));
        return names;
    }

    public boolean exists(String path) {
        return Files.exists(Path.of(root, path));
    }

    public void write(String path, byte[] data) throws IOException {
        Path target = Path.of(root, path);
        Path parent = target.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Files.write(target, data);
    }
}
