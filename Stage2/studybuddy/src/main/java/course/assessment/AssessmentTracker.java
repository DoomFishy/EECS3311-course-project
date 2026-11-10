package course.assessment;

import course.Course;
import course.CourseListener;
import java.util.List;

public class AssessmentTracker implements CourseListener {

    @Override
    public void update(Course course) {
        if (course == null) {
            return;
        }

        List<Assessment> assessments = course.getAssessments();
        if (assessments.isEmpty()) {
            System.out.println("AssessmentTracker: No assessments for course " + course.getId());
            return;
        }

        for (Assessment assessment : assessments) {
            System.out.println(
                "AssessmentTracker: " + assessment.getName() + " | type=" + assessment.getType()
                + " | deadline=" + assessment.getDeadline() + " | completed=" + assessment.isCompleted()
            );
        }
    }
}
