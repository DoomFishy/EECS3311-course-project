package cli.course_commands;

import java.util.List;

import cli.Command;
import cli.Session;
import course.Course;

public class DeadlineCommand implements Command {
    private final List<Course> courses;
    private final Session session;

    public DeadlineCommand(List<Course> courses, Session session) {
        this.courses = courses;
        this.session = session;
    }

    @Override
    public void execute(String[] args) {
        if (!session.hasCourse()) {
            System.out.println("Enter a course first.");
            return;
        }

        String summary = session.getFacade().generateDeadlineSummary();

        System.out.println(summary);
    }
}
