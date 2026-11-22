package cli.course_commands;

import cli.Command;
import cli.Session;

public class AgentCommand implements Command {
    private final Session session;

    public AgentCommand(Session session) {
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

        // Extract the arguments after the flag (useful for the ask command)
        String[] subArgs = new String[args.length - 1];
        System.arraycopy(args, 1, subArgs, 0, args.length - 1);

        // Just to show the user that the AI is working
        System.out.println("🤖 AI is thinking... (this may take a moment)\n");

        switch (flag) {
            case "-ask":
                if (subArgs.length == 0) {
                    System.out.println("Usage: agent -ask <your question>");
                    return;
                }
                String question = joinArgs(subArgs, 0);
                System.out.println(session.getFacade().askQuestion(question));
                break;
                
            case "-report":
                System.out.println(session.getFacade().generateCourseReport());
                break;
                
            case "-plan":
                System.out.println(session.getFacade().generateStudyPlan());
                break;
                
            case "-notes":
                System.out.println(session.getFacade().generateNotes());
                break;
                
            case "-quiz":
                System.out.println(session.getFacade().generateQuiz());
                break;
                
            default:
                System.out.println("Unknown agent flag: " + flag);
                printUsage();
                break;
        }
    }

    private void printUsage() {
        System.out.println("Usage: agent <flag> ...");
        System.out.println("  agent -ask <question>   Ask the AI a question about the course");
        System.out.println("  agent -report           Generate a full course report");
        System.out.println("  agent -plan             Generate a study plan");
        System.out.println("  agent -notes            Generate study notes");
        System.out.println("  agent -quiz             Generate a practice quiz");
    }
}