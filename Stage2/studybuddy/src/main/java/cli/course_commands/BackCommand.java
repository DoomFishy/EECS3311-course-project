package cli.course_commands;

import java.util.List;

import cli.Command;
import cli.CommandRegistry;
import cli.MenuCommandRegistry;
import cli.Session;
import course.Course;

public class BackCommand implements Command {
    private final List<Course> courses;
    private final Session session;
    private final CommandRegistry registry;

    public BackCommand(List<Course> courses, Session session, CommandRegistry registry) {
        this.courses = courses;
        this.session = session;
        this.registry = registry;
    }

    @Override
    public void execute(String[] args) {
        session.clearCurrentCourse(); // leave the course
        System.out.println("Returned to main menu.");
        registry.switchTo(new MenuCommandRegistry(courses, session));
    }
}