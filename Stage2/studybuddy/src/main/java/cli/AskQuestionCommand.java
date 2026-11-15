package cli;

import java.util.List;

import ai.controller.AppFacade;
import ai.factory.AIFactory;
import course.Course;

public class AskQuestionCommand implements Command {
    private final List<Course> courses;

    public AskQuestionCommand(List<Course> courses) {
        this.courses = courses;
    }

    @Override
    public void execute(String[] args) {
        if (args == null || args.length == 0) {
            System.out.println("Usage: --ask \"your question\"");
            return;
        }

        String question = joinArgs(args, 0);
        Course target = getDefaultCourse(courses);
        AppFacade facade = new AppFacade(AIFactory.createDefault(), target);
        try {
            System.out.println(facade.askQuestion(question));
        } catch (Exception ex) {
            System.out.println("AI request failed: " + ex.getMessage());
        }
    }
}
