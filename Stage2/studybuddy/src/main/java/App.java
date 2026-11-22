import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import ai.controller.AppFacade;
import ai.factory.AIFactory;
import cli.CommandRegistry;
import cli.MenuCommandRegistry;
import cli.Session;
import course.Course;



public class App {
    private static final List<Course> courses = new ArrayList<>();
     private static Session session = new Session();

    public static void main(String[] args) {
        if (args == null || args.length == 0) {
            launchGui();
            return;
        }

        for (int i = 0; i < args.length; i++) {
            if ("-LLMAPI".equals(args[i]) && i + 1 < args.length) {
                System.setProperty("OPENROUTER_API_KEY", args[i + 1]);
            }
        }

        // 2. Check for standalone agent commands
        if (args[0].startsWith("-")) {
            if (runStandaloneAgent(args)) {
                return; // Exit after running the agent
            }
        }

        String first = args[0].toLowerCase(Locale.ROOT);
        if ("--help".equals(first) || "-h".equals(first) || "help".equals(first)) {
            printUsage();
            return;
        }

        if ("--gui".equals(first) || "gui".equals(first)) {
            launchGui();
            return;
        }

        if ("--cli".equals(first) || "cli".equals(first)) {
            runCli(args.length > 1 ? java.util.Arrays.copyOfRange(args, 1, args.length) : new String[0]);
            return;
        }

        

        runCli(args);
    }


    private static void launchGui() {
        AppFacade appFacade = new AppFacade(AIFactory.createDefault(), null);
        javax.swing.SwingUtilities.invokeLater(() -> new MainMenuGUI(appFacade));
    }

    private static void runCli(String[] args) {
        if (args == null || args.length == 0) {
            interactiveCli();
            return;
        }

        CommandRegistry registry = new MenuCommandRegistry(courses, session);
        String commandName = args[0];
        String[] commandArgs = args.length > 1 ? java.util.Arrays.copyOfRange(args, 1, args.length) : new String[0];

        if (!registry.execute(commandName, commandArgs)) {
            printUsage();
        }
    }

    private static void interactiveCli() {
        System.out.println("StudyBuddy CLI");
        System.out.println("Type 'help' for commands or 'quit' to exit.");

        Scanner scanner = new Scanner(System.in);
        Session session = new Session();
        
        // ★ Start with the Menu registry
        CommandRegistry registry = new MenuCommandRegistry(courses, session);

        while (true) {
            System.out.print("> ");
            if (!scanner.hasNextLine()) {
                break;
            }

            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }

            String[] parts = line.split("\\s+");
            String command = parts[0].toLowerCase(Locale.ROOT);
            String[] commandArgs = parts.length > 1 ? java.util.Arrays.copyOfRange(parts, 1, parts.length) : new String[0];

            if ("quit".equals(command) || "exit".equals(command)) {
                if (registry instanceof MenuCommandRegistry) {
                    System.out.println("Goodbye.");
                    break;
                }
            }

            if (!registry.execute(command, commandArgs)) {
                System.out.println("Unknown command. Type 'help' for usage.");
            }

            CommandRegistry next = registry.getNextRegistry();
            if (next != null) {
                registry = next;
            }
        }
    }

    private static void printUsage() {
        System.out.println("StudyBuddy CLI Usage:");
        System.out.println("  java App --cli             Start interactive CLI");
        System.out.println("  java App --gui             Start the Swing GUI");
        System.out.println("  java App --week            Show this week's tasks for the default course");
        System.out.println("  java App --deadlines       Show this week's deadlines for the default course");
        System.out.println("  java App --ask \"question\" Ask the AI about the default course");
        System.out.println("  java App --add-assessment \"Name\" \"MM/DD\" \"25\" \"ASSIGNMENT\"");
        System.out.println("  java App --course \"Course Name\" Create a course");
        System.out.println("  java App --list            List available courses");
        System.out.println("  java App --help            Show this help");
        System.out.println();
        System.out.println("Interactive CLI commands:");
        System.out.println("  help, list, week, deadlines, add-course <name>, add-assessment <name> <MM/DD> <weight> <type>, ask <question>, quit");
    }

private static boolean runStandaloneAgent(String[] args) {
    String command = args[0].toLowerCase();
    String inputFile = null;

    // Find the -input filepath
    for (int i = 0; i < args.length; i++) {
        if ("-input".equals(args[i]) && i + 1 < args.length) {
            inputFile = args[i + 1];
        }
    }

    // If it's not one of our agent commands, return false so main() handles it
    if (!command.equals("-ask") && !command.equals("-notes") && 
        !command.equals("-plan") && !command.equals("-quiz") && !command.equals("-report")) {
        return false; 
    }

    if (inputFile == null) {
        System.err.println("Error: Missing -input <filepath> for command " + command);
        System.exit(1);
    }

    try {
        // Read the file
        String fileContent = java.nio.file.Files.readString(java.nio.file.Path.of(inputFile)).trim();
        
        // Setup temp course and facade
        course.Course tempCourse = new course.Course("temp", "Standalone");
        ai.controller.AppFacade facade = new ai.controller.AppFacade(ai.factory.AIFactory.createDefault(), tempCourse);
        
        String response = "";

        System.out.println("Running " + command + "...\n");

        // Route to the correct agent method based on the flag
        switch (command) {
            case "-ask":
                if (fileContent.isEmpty()) {
                    System.err.println("Error: File is empty. -ask requires a question in the file.");
                    System.exit(1);
                }
                response = facade.askQuestion(fileContent);
                break;
            case "-notes":
                response = facade.generateNotes();
                break;
            case "-plan":
                response = facade.generateStudyPlan();
                break;
            case "-quiz":
                response = facade.generateQuiz();
                break;
            case "-report":
                response = facade.generateCourseReport();
                break;
        }

        System.out.println("--- AI RESPONSE ---");
        System.out.println(response);
        System.out.println("-------------------");
        
        System.exit(0); // Success
        
    } catch (Exception e) {
        System.err.println("Agent execution failed: " + e.getMessage());
        System.exit(1);
    }
    
    return true;
}
}