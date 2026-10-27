import java.util.*;

public class Course {

    private String id;
    private String name;
    private GradeReport gradeReport;
    private List<Assessment> assessment;
    private List<Material> materials;
    private List<CourseListener> observers;
    //Tool reader;


    public Course(String id, String name) {
        this.id = id;
        this.name = name;

        this.observers = new ArrayList<>();
        this.assessment = new ArrayList<>();
        this.materials = new ArrayList<>();
    }

    public void setGradeReport() {
        gradeReport = new GradeReport(id, 0, this.assessment);
    }

    public float getAverage() {
        return gradeReport.calculateAverage();
    }

    public List<Assessment> getAssessments() {
        return assessment;
    }

    public void addAssessment(Assessment a) {
        assessment.add(a);
    }

    public List<Material> getMaterials() {
        return materials;
    }

    public void addMaterial(Material a) {
        materials.add(a);
    }
    
    public void addObservers(CourseListener a) {
        this.observers.add(a);
    }

    public void removeObservers(CourseListener a) {
        this.observers.remove(a);
    }

    public void notify(String filepath) {
        for (int i = 0; i < this.observers.size(); i++) {
            this.observers.remove(i);
        }
    }

}
