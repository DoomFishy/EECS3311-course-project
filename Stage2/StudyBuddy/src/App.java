
import ai.controller.AppFacade;
import ai.factory.AIFactory;
import course.Course;



public class App {
    public static void main(String[] args) {
        Course course = new Course("cs101", "StudyBuddy Intro");

        AppFacade appFacade = new AppFacade(AIFactory.createDefault(), course);
        javax.swing.SwingUtilities.invokeLater(() -> new MainMenuGUI(appFacade));
    }
}