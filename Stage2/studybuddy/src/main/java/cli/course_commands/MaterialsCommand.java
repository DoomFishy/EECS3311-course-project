package cli.course_commands;

import java.util.ArrayList;
import java.util.List;

import cli.Command;
import cli.Session;
import course.material.Material;

public class MaterialsCommand implements Command {
    private final Session session;

    public MaterialsCommand(Session session) {
        this.session = session;
    }

    @Override
    public void execute(String[] args) {
        if (!session.hasCourse()) {
            System.out.println("Enter a course first.");
            return;
        }

        if (args == null || args.length == 0) {
            printUsage();
            return;
        }

        String flag = args[0].toLowerCase();

        // Extract the arguments after the flag
        String[] subArgs = new String[args.length - 1];
        System.arraycopy(args, 1, subArgs, 0, args.length - 1);

        switch (flag) {
            case "-list":
            case "list":
                handleList();
                break;
            case "-upload":
            case "upload":
                handleUpload(subArgs);
                break;
            case "-set":
            case "set":
                handleSet(subArgs);
                break;
            default:
                System.out.println("Unknown flag: " + flag);
                printUsage();
                break;
        }
    }

    private void handleList() {
        List<Material> materials = session.getFacade().getMaterials();

        if (materials.isEmpty()) {
            System.out.println("No materials found for " + session.getCurrentCourse().getName() + ".");
            System.out.println("Upload some using 'material -upload <filepath>'.");
            return;
        }

        System.out.println("Materials for " + session.getCurrentCourse().getName() + ":");

        List<Material> selected = session.getFacade().getSelectedMaterials();
        if (selected == null) selected = List.of();

        for (Material m : materials) {
            String mId = m.getId();
            boolean isSelected = selected.stream().anyMatch(s -> s.getId().equalsIgnoreCase(mId));
            String marker = isSelected ? " [SELECTED]" : "";

            System.out.printf(" - [%-4s] %-30s (%s)%s\n",
                    mId, m.getFilename(), m.getType(), marker);
        }

        System.out.println("\nTip: Use 'material -set <id>' to select files.");
        System.out.println("     Use 'material -set -d <id>' to deselect.");
    }

    private void handleUpload(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: material -upload <filepath>");
            return;
        }

        String filepath = joinArgs(args, 0).trim();

        try {
            System.out.println("Uploading and processing file...");
            String result = session.getFacade().uploadFile(filepath);
            System.out.println(result);
        } catch (Exception ex) {
            System.out.println("Error uploading file: " + ex.getMessage());
        }
    }

    private void handleSet(String[] args) {
        List<Material> materials = session.getFacade().getMaterials();
        if (materials.isEmpty()) {
            System.out.println("No materials available. Upload some first using 'material -upload'.");
            return;
        }

        if (args.length == 0) {
            System.out.println("Usage: material -set <id> <id> ...  (To select)");
            System.out.println("       material -set -d <id> ...     (To deselect)");
            System.out.println("       material -set -d              (To clear all)");
            return;
        }

        boolean isDeselect = args[0].equalsIgnoreCase("-d") || args[0].equalsIgnoreCase("--deselect");

        if (isDeselect) {
            // If it's just "-d", clear all selections
            if (args.length == 1) {
                session.getFacade().setSelectedMaterialsIds(new ArrayList<>());
                System.out.println("Cleared all selected materials.");
                return;
            }

            // Otherwise, remove specific IDs from the current selection
            List<Material> currentSelection = session.getFacade().getSelectedMaterials();
            List<String> newIds = new ArrayList<>();
            
            for (Material m : currentSelection) {
                newIds.add(m.getId());
            }

            for (int i = 1; i < args.length; i++) {
                String idToRemove = args[i].toLowerCase();
                newIds.remove(idToRemove);
            }

            session.getFacade().setSelectedMaterialsIds(newIds);
            System.out.println("Deselected specified materials.");
            
        } else {
            // Normal selection logic
            List<String> selectedIds = new ArrayList<>();
            for (String arg : args) {
                boolean exists = materials.stream().anyMatch(m -> m.getId().equalsIgnoreCase(arg));
                
                if (exists) {
                    selectedIds.add(arg.toLowerCase());
                } else {
                    System.out.println("Warning: Ignored unknown material ID '" + arg + "'");
                }
            }

            if (selectedIds.isEmpty()) {
                System.out.println("No valid materials selected. Check the IDs and try again.");
                return;
            }

            session.getFacade().setSelectedMaterialsIds(selectedIds);
            System.out.println("Successfully selected " + selectedIds.size() + " material(s) for the AI to use.");
        }
    }

    private void printUsage() {
        System.out.println("Usage: material <flag> ...");
        System.out.println("  material -list                  (Show all materials)");
        System.out.println("  material -upload <filepath>     (Upload a new file)");
        System.out.println("  material -set <id> ...          (Select materials)");
        System.out.println("  material -set -d <id> ...       (Deselect materials)");
    }
}