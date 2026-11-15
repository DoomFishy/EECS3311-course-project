package cli;

import java.util.List;

import ai.controller.AppFacade;
import ai.factory.AIFactory;
import course.Course;

public class AddAssessmentCommand implements Command {
    private final List<Course> courses;

    public AddAssessmentCommand(List<Course> courses) {
        this.courses = courses;
    }

    @Override
    public void execute(String[] args) {
        if (args == null || args.length < 4) {
            System.out.println("Usage: --add-assessment \"Assessment Name\" \"MM/DD\" \"weightPercent\" \"TYPE\"");
            return;
        }

        String name = args[0];
        String date = args[1];
        String weight = args[2];
        String type = args[3];

        Course target = getDefaultCourse(courses);
        AppFacade facade = new AppFacade(AIFactory.createDefault(), target);
        try {
            facade.addAssessment(name, date, weight, type);
            System.out.println("Assessment added: " + name + " on " + date);
        } catch (Exception ex) {
            System.out.println("Could not add assessment: " + ex.getMessage());
        }
    }
}
