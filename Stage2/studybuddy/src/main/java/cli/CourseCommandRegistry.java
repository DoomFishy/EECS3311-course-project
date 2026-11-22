package cli;

import java.util.List;

import cli.course_commands.AgentCommand;
import cli.course_commands.AssessmentCommand;
import cli.course_commands.BackCommand;
import cli.course_commands.DeadlineCommand;
import cli.course_commands.GradeCommand;
import cli.course_commands.MaterialsCommand;
import cli.course_commands.QuitCommand;
import cli.course_commands.WeekCommand;
import course.Course;

public class CourseCommandRegistry extends CommandRegistry {
    public CourseCommandRegistry(List<Course> courses, Session session) {
        super(courses, session);
    }

    @Override
    protected void registerCommands() {
        register("help", new HelpCommand(session));
        register("week", new WeekCommand(courses, session));
        register("deadline", new DeadlineCommand(courses, session));
        
        register("assessment", new AssessmentCommand(session));
        register("material", new MaterialsCommand(session));
        register("grade", new GradeCommand(session));
        
        register("agent", new AgentCommand(session));
        register("back", new BackCommand(courses, session, this));
        register("quit", new QuitCommand());
        register("exit", new QuitCommand());

        // set grade goal
        // view grade goal
    }
}