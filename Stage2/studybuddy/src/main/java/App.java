import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import ai.controller.AppFacade;
import ai.factory.AIFactory;
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
        Course course = new Course("cs101", "StudyBuddy Intro");
        if (courses.isEmpty()) {
            courses.add(course);
        }

        AppFacade appFacade = new AppFacade(AIFactory.createDefault(), course);
        javax.swing.SwingUtilities.invokeLater(() -> new MainMenuGUI(appFacade));
    }

    private static void runCli(String[] args) {
        if (args == null || args.length == 0) {
            interactiveCli();
            return;
        }

        if (args.length >= 2 && "--course".equalsIgnoreCase(args[0])) {
            String courseName = args[1].trim();
            if (courseName.isEmpty()) {
                System.out.println("Course name cannot be empty.");
                return;
            }

            Course course = new Course("course-" + (courses.size() + 1), courseName);
            courses.add(course);
            System.out.println("Course created: " + course.getName());
            return;
        }

        if ("--list".equalsIgnoreCase(args[0])) {
            if (courses.isEmpty()) {
                System.out.println("No courses available yet.");
                return;
            }

            for (Course course : courses) {
                System.out.println("- " + course.getName());
            }
            return;
        }

        if ("--week".equalsIgnoreCase(args[0])) {
            Course target = getDefaultCourse();
            AppFacade facade = new AppFacade(AIFactory.createDefault(), target);
            System.out.println(facade.generateTaskList());
            return;
        }

        if ("--deadlines".equalsIgnoreCase(args[0])) {
            Course target = getDefaultCourse();
            AppFacade facade = new AppFacade(AIFactory.createDefault(), target);
            System.out.println(facade.generateDeadlineSummary());
            return;
        }

        if ("--ask".equalsIgnoreCase(args[0])) {
            if (args.length < 2) {
                System.out.println("Usage: --ask \"your question\"");
                return;
            }
            String question = String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length));
            Course target = getDefaultCourse();
            AppFacade facade = new AppFacade(AIFactory.createDefault(), target);
            try {
                System.out.println(facade.askQuestion(question));
            } catch (Exception ex) {
                System.out.println("AI request failed: " + ex.getMessage());
            }
            return;
        }

        if ("--add-assessment".equalsIgnoreCase(args[0])) {
            if (args.length < 5) {
                System.out.println("Usage: --add-assessment \"Assessment Name\" \"MM/DD\" \"weightPercent\" \"TYPE\"");
                return;
            }

            String name = args[1];
            String date = args[2];
            String weight = args[3];
            String type = args[4];
            Course target = getDefaultCourse();
            AppFacade facade = new AppFacade(AIFactory.createDefault(), target);
            try {
                facade.addAssessment(name, date, weight, type);
                System.out.println("Assessment added: " + name + " on " + date);
            } catch (Exception ex) {
                System.out.println("Could not add assessment: " + ex.getMessage());
            }
            return;
        }

        printUsage();
    }

    private static void interactiveCli() {
        System.out.println("StudyBuddy CLI");
        System.out.println("Type 'help' for commands or 'quit' to exit.");

        Scanner scanner = new Scanner(System.in);
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

            if ("quit".equals(command) || "exit".equals(command)) {
                System.out.println("Goodbye.");
                break;
            }

            if ("help".equals(command)) {
                printUsage();
                continue;
            }

            if ("list".equals(command)) {
                if (courses.isEmpty()) {
                    System.out.println("No courses available yet.");
                } else {
                    for (Course course : courses) {
                        System.out.println("- " + course.getName());
                    }
                }
                continue;
            }

            if ("week".equals(command)) {
                Course target = getDefaultCourse();
                AppFacade facade = new AppFacade(AIFactory.createDefault(), target);
                System.out.println(facade.generateTaskList());
                continue;
            }

            if ("deadlines".equals(command)) {
                Course target = getDefaultCourse();
                AppFacade facade = new AppFacade(AIFactory.createDefault(), target);
                System.out.println(facade.generateDeadlineSummary());
                continue;
            }

            if ("add-course".equals(command)) {
                if (parts.length < 2) {
                    System.out.println("Usage: add-course <Course Name>");
                    continue;
                }
                String courseName = String.join(" ", java.util.Arrays.copyOfRange(parts, 1, parts.length));
                Course course = new Course("course-" + (courses.size() + 1), courseName);
                courses.add(course);
                System.out.println("Course created: " + course.getName());
                continue;
            }

            if ("add-assessment".equals(command)) {
                if (parts.length < 5) {
                    System.out.println("Usage: add-assessment <Name> <MM/DD> <WeightPercent> <TYPE>");
                    continue;
                }
                String name = parts[1];
                String date = parts[2];
                String weight = parts[3];
                String type = parts[4];
                Course target = getDefaultCourse();
                AppFacade facade = new AppFacade(AIFactory.createDefault(), target);
                try {
                    facade.addAssessment(name, date, weight, type);
                    System.out.println("Assessment added: " + name + " on " + date);
                } catch (Exception ex) {
                    System.out.println("Could not add assessment: " + ex.getMessage());
                }
                continue;
            }

            if ("ask".equals(command)) {
                if (parts.length < 2) {
                    System.out.println("Usage: ask <question>");
                    continue;
                }
                String question = String.join(" ", java.util.Arrays.copyOfRange(parts, 1, parts.length));
                Course target = getDefaultCourse();
                AppFacade facade = new AppFacade(AIFactory.createDefault(), target);
                try {
                    System.out.println(facade.askQuestion(question));
                } catch (Exception ex) {
                    System.out.println("AI request failed: " + ex.getMessage());
                }
                continue;
            }

            System.out.println("Unknown command. Type 'help' for usage.");
        }
    }

    private static Course getDefaultCourse() {
        if (courses.isEmpty()) {
            Course defaultCourse = new Course("cs101", "StudyBuddy Intro");
            courses.add(defaultCourse);
            return defaultCourse;
        }
        return courses.get(0);
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