package cli;

public class HelpCommand implements Command {
    @Override
    public void execute(String[] args) {
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
