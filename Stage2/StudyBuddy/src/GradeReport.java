import java.util.*;

public class GradeReport {
    
    private String id;
    private float gradeGoal;
    private List<Assessment> assessments;


    public GradeReport(String id, float gradeGoal, List<Assessment> assessments) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Error - GradeReport: ID required");
        if (gradeGoal <= 0) throw new IllegalArgumentException("Error - GradeReport: Grade goal is < 0");
        if (assessments == null)  throw new IllegalArgumentException("Error - GradeReport: Assessment list is null");

        this.id = id;
        this.gradeGoal = gradeGoal;
        this.assessments = assessments;
    }

    public float calculateAverage() {
        float total = 0;
        for (int i = 0; i < assessments.size(); i++) {
            total += assessments.get(i).getScore();
        }

        return total / assessments.size();
    }

    public void setGradeGoal(float gradeGoal) {
        if (gradeGoal <= 0) throw new IllegalArgumentException("Error - GradeReport_setGradeGoal: Grade goal is < 0");

        this.gradeGoal = gradeGoal;
    }

}
