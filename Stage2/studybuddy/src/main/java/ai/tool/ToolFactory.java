package ai.tool;

import java.util.ArrayList;
import java.util.List;

public class ToolFactory {
    public static List<Tool> defaultTools() {
        List<Tool> tools = new ArrayList<>();
        tools.add(new FileReaderTool());
        tools.add(new PDFReaderTool());
        return tools;
    }

    public static Tool findBestTool(String filePath) {
        if (filePath == null || filePath.isBlank()) {
            throw new IllegalArgumentException("Error - ToolFactory: file path required");
        }

        for (Tool tool : defaultTools()) {
            if (tool.supports(filePath)) {
                return tool;
            }
        }

        return new FileReaderTool();
    }
}
