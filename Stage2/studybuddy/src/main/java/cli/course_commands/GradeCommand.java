package cli.course_commands;

import cli.Command;
import cli.Session;

public class GradeCommand implements Command {
    private final Session session;

    public GradeCommand(Session session) {
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

        switch (flag) {
            case "-set":
                handleSet(args);
                break;
            case "-view":
            case "-list":
                handleView();
                break;
            default:
                System.out.println("Unknown flag: " + flag);
                printUsage();
                break;
        }
    }

    private void handleSet(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: grade -set <percentage>");
            System.out.println("Example: grade -set 85");
            return;
        }

        try {
            float goal = Float.parseFloat(args[1]);
            session.getFacade().setGradeGoal(goal);
            System.out.printf("Grade goal set to: %.1f%%\n", goal);
        } catch (NumberFormatException ex) {
            System.out.println("Error: Please enter a valid number (e.g., 85 or 85.5).");
        } catch (Exception ex) {
            System.out.println("Could not set grade goal: " + ex.getMessage());
        }
    }

    private void handleView() {
        float goal = session.getFacade().getGradeGoal();
        
        // If the goal is 0, it means no goal has been set yet
        if (goal == 0f) {
            System.out.println("No grade goal set yet. Use 'grade -set <percentage>' to set one.");
            return;
        }

        System.out.println("Current grade goal: " + goal + "%");
        System.out.println(session.getFacade().getCurrentGradeSummary());
    }

    private void printUsage() {
        System.out.println("Usage: grade <flag> ...");
        System.out.println("  grade -set <percentage>   Set a target grade (e.g., 85)");
        System.out.println("  grade -view               View your current grade and goal");
    }
}