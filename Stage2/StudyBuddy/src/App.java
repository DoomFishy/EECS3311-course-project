import ai.controller.AppFacade;
import ai.factory.AIFactory;
import course.Course;
import course.assessment.Assessment;
import course.assessment.AssessmentType;
import course.material.Material;
import course.material.MaterialType;
import java.time.LocalDateTime;



public class App {
    public static void main(String[] args) {
        Course course = new Course("cs101", "StudyBuddy Intro");
        course.addAssessment(new Assessment(
                "a1",
                "Week 1 Quiz",
                100,
                LocalDateTime.now().plusDays(5),
                0.3f,
                AssessmentType.QUIZ
        ));
        course.addAssessment(new Assessment(
                "a2",
                "Midterm",
                100,
                LocalDateTime.now().plusDays(12),
                0.5f,
                AssessmentType.EXAM
        ));
        course.addMaterial(new Material("m1", "notes.txt", "C:/temp/notes.txt", MaterialType.Link));

        AppFacade appFacade = new AppFacade(AIFactory.createDefault(), course);
        javax.swing.SwingUtilities.invokeLater(() -> new MainMenuGUI(appFacade));
    }
}