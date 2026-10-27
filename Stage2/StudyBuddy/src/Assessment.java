import java.time.LocalDateTime;

public class Assessment {
    private String id;
    private String name;
    
    private LocalDateTime deadline;
    private float weight;
    private float priorityScore;
    private AssessmentType type;
    private double maxScore;

    private double score;
    private boolean completed;

    public Assessment(String id, String name, double maxScore, LocalDateTime deadline, float weight, AssessmentType type) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Error - Assessment: Name required!");
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Error - Assessment: ID required!");
        if (maxScore <= 0) throw new IllegalArgumentException("Error - Asssessment: Max Score must be > 0");
        if (deadline == null) throw new IllegalArgumentException("Error - Asssessment: Deadline is invalid");
        if (weight <= 0) throw new IllegalArgumentException("Error - Asssessment: Weight must be > 0");
        if (type == null) throw new IllegalArgumentException("Error - Asssessment: Type is invalid");

        this.id = id;
        this.name = name;
        this.maxScore = maxScore;
        this.deadline = deadline;
        this.weight = weight;
        this.type = type;
    }

    public double getGrade() {
        return (this.score / this.maxScore) * 100.0;
    }

    public void setScore(double score) {
        if (score < 0 || score > maxScore) throw new IllegalArgumentException("Error - Asssessment_setGrade: score is out of range");

        this.score = score;
    }

    public double getScore() {
        return this.score;
    }

    public void setCompleted(float score) {
        setScore(score);
        completed = true;
    }

}
