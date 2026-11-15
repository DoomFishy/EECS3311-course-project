package cli;

import java.util.List;

import ai.controller.AppFacade;
import ai.factory.AIFactory;
import course.Course;

public class DeadlineCommand implements Command {
    private final List<Course> courses;

    public DeadlineCommand(List<Course> courses) {
        this.courses = courses;
    }

    @Override
    public void execute(String[] args) {
        Course target = getDefaultCourse(courses);
        AppFacade facade = new AppFacade(AIFactory.createDefault(), target);
        System.out.println(facade.generateDeadlineSummary());
    }
}
