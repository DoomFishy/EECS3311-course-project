import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import ai.controller.AppFacade;
import ai.factory.AIFactory;
import cli.CommandRegistry;
import course.Course;



public class App {
    private static final List<Course> courses = new ArrayList<>();

    public static void main(String[] args) {
        if (args == null || args.length == 0) {
            launchGui();
            return;
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

        CommandRegistry registry = new CommandRegistry(courses);
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
        CommandRegistry registry = new CommandRegistry(courses);

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
                System.out.println("Goodbye.");
                break;
            }

            if (!registry.execute(command, commandArgs)) {
                System.out.println("Unknown command. Type 'help' for usage.");
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
}