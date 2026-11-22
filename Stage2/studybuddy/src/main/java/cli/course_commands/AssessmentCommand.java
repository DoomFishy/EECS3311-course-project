package cli.course_commands;

import java.util.List;

import cli.Command;
import cli.Session;
import course.assessment.Assessment;

public class AssessmentCommand implements Command {
    private final Session session;

    public AssessmentCommand(Session session) {
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

        String[] subArgs = new String[args.length - 1];
        System.arraycopy(args, 1, subArgs, 0, args.length - 1);

        switch (flag) {
            case "-add":
                handleAdd(subArgs);
                break;
            case "-complete":
                handleComplete(subArgs);
                break;
            default:
                System.out.println("Unknown flag: " + flag);
                printUsage();
                break;
        }
    }

    private void handleAdd(String[] args) {
        if (args.length < 4) {
            System.out.println("Usage: assessment -add <Name> <MM/DD/YYYY> <weightPercent> <TYPE>");
            System.out.println("Example: assessment -add Final Exam 12/15/2018 30 EXAM");
            return;
        }

        String type = args[args.length - 1];
        String weight = args[args.length - 2];
        String date = args[args.length - 3];
        
        StringBuilder nameBuilder = new StringBuilder();
        for (int i = 0; i < args.length - 3; i++) {
            if (i > 0) nameBuilder.append(" ");
            nameBuilder.append(args[i]);
        }
        String name = nameBuilder.toString().trim();

        boolean exists = session.getFacade().getAssessments().stream()
                .anyMatch(a -> a.getName().equalsIgnoreCase(name));

        if (exists) {
            System.out.println("Error: An assessment named \"" + name + "\" already exists.");
            return;
        }

        try {
            session.getFacade().addAssessment(name, date, weight, type);
            System.out.println("Assessment added: " + name + " on " + date);
        } catch (Exception ex) {
            System.out.println("Could not add assessment: " + ex.getMessage());
        }
    }

    private void handleComplete(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: assessment -complete <Name> <Score>");
            System.out.println("Example: assessment -complete Final Exam 95");
            return;
        }

        String score = args[args.length - 1];
        
        StringBuilder nameBuilder = new StringBuilder();
        for (int i = 0; i < args.length - 1; i++) {
            if (i > 0) nameBuilder.append(" ");
            nameBuilder.append(args[i]);
        }
        String name = nameBuilder.toString().trim();

        List<Assessment> assessments = session.getFacade().getAssessments();

        for (Assessment a : assessments) {
            if (a.getName().equalsIgnoreCase(name)) {
                try {
                    session.getFacade().setGradeAssessment(a, score);
                    System.out.println("Assessment '" + a.getName() + "' is completed with a score of " + score);
                } catch (Exception ex) {
                    System.out.println("Could not complete assessment: " + ex.getMessage());
                }
                return;
            }
        }

        System.out.println("Assessment not found: \"" + name + "\"");
    }

    private void printUsage() {
        System.out.println("Usage: assessment <flag> ...");
        System.out.println("  assessment -add <Name> <Date> <Weight> <Type>");
        System.out.println("  assessment -complete <Name> <Score>");
    }
}