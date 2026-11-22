package cli;

import java.util.List;

import cli.course_commands.QuitCommand;
import cli.menu_commands.CourseCommand;
import course.Course;

public class MenuCommandRegistry extends CommandRegistry {

    public MenuCommandRegistry(List<Course> courses, Session session) {
        super(courses, session);
    }

    @Override
    protected void registerCommands() {
        register("help", new HelpCommand(session));
        register("course", new CourseCommand(courses, session, this));

        register("quit", new QuitCommand());
        register("exit", new QuitCommand());
        
    }
}