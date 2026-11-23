package cli;

public class HelpCommand implements Command {
    private final Session session;

    public HelpCommand(Session session) {
        this.session = session;
    }

    @Override
    public void execute(String[] args) {
        System.out.println("Interactive CLI commands:");
        if (!session.hasCourse()) {
            // ── MENU COMMANDS ──
            System.out.println("-------------------------------------------");
            System.out.println("  course -list                   List all courses");
            System.out.println("  course -create <name>          Create a new course");
            System.out.println("  course -delete <name>          Delete a course");
            System.out.println("  course -enter <name>           Enter a course to access its tools");
            System.out.println("-------------------------------------------");
        
        } else {
            // ── COURSE COMMANDS ──
            System.out.println("Current Course: " + session.getCurrentCourse().getName());
            System.out.println("-------------------------------------------");
            System.out.println("  Assessment Tools:");
            System.out.println("    assessment -add <name> <date> <weight> <type>  Add an assessment");
            System.out.println("    assessment -complete <name> <score>            Grade an assessment");
            System.out.println("    assessment -list                               List all assessments");
            System.out.println();
            System.out.println("  Material Tools:");
            System.out.println("    material -list                                 List uploaded materials");
            System.out.println("    material -upload <filepath>                    Upload a new file");
            System.out.println("    material -set <id> ...                         Select materials for AI");
            System.out.println("    material -set -d <id> ...                      Deselect materials");
            System.out.println();
            System.out.println("  AI Agent Tools:");
            System.out.println("    agent -ask <question>                          Ask the AI a question");
            System.out.println("    agent -report                                  Generate a course report");
            System.out.println("    agent -plan                                    Generate a study plan");
            System.out.println("    agent -notes                                   Generate study notes");
            System.out.println("    agent -quiz                                    Generate a practice quiz");
            System.out.println();
            System.out.println("  General:");
            System.out.println("    week                                           Show this week's tasks");
            System.out.println("    deadline                                       Show upcoming deadlines");
            System.out.println("    grade -set <percentage>                        Set a target grade goal");
            System.out.println("    grade -view                                    View your current grade");
            System.out.println("    back                                           Return to the main menu");
            System.out.println("-------------------------------------------");
        }

        System.out.println("  help                        Show this help menu");
        System.out.println("  quit / exit                 Leave the app");
        System.out.println("-------------------------------------------");
        System.out.println();
    }
}