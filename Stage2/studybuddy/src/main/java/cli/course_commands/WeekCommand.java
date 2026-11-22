package cli.course_commands;

import java.util.List;

import cli.Command;
import cli.Session;
import course.Course;

public class WeekCommand implements Command {
    private final List<Course> courses;
    private final Session session;

    public WeekCommand(List<Course> courses, Session session) {
        this.courses = courses;
        this.session = session;
    }

    @Override
    public void execute(String[] args) {
        if (!session.hasCourse()) {
            System.out.println("Enter a course first.");
            return;
        }

        String summary = session.getFacade().generateTaskList();

        System.out.println(summary);
    }
}
