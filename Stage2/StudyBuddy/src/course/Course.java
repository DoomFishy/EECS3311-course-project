package course;

import course.assessment.Assessment;
import course.material.Material;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Course {

    private final String id;
    private final String name;
    private GradeReport gradeReport;
    private final List<Assessment> assessment;
    private final List<Material> materials;
    private final List<CourseListener> observers;

    public Course(String id, String name) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Error - Course: ID required!");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Error - Course: Name required!");

        this.id = id;
        this.name = name;

        this.observers = new ArrayList<>();
        this.assessment = new ArrayList<>();
        this.materials = new ArrayList<>();
        this.gradeReport = new GradeReport(id, 0f, this.assessment);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public GradeReport getGradeReport() {
        return gradeReport;
    }

    public void setGradeReport() {
        this.gradeReport = new GradeReport(id, this.gradeReport == null ? 0f : this.gradeReport.getCourseGoal(), this.assessment);
    }

    public void setGradeReport(float gradeGoal) {
        this.gradeReport = new GradeReport(id, gradeGoal, this.assessment);
    }

    public float getAverage() {
        return gradeReport == null ? 0f : gradeReport.calculateAverage();
    }

    public List<Assessment> getAssessments() {
        return Collections.unmodifiableList(assessment);
    }

    public void addAssessment(Assessment a) {
        if (a == null) throw new IllegalArgumentException("Error - Course: Assessment required!");
        boolean alreadyExists = assessment.stream().anyMatch(existing -> existing.getName().equalsIgnoreCase(a.getName()));
        if (alreadyExists) {
            return;
        }
        assessment.add(a);
        setGradeReport(gradeReport == null ? 0f : gradeReport.getCourseGoal());
        notifyObservers();
    }

    public List<Material> getMaterials() {
        return Collections.unmodifiableList(materials);
    }

    public void addMaterial(Material a) {
        if (a == null) throw new IllegalArgumentException("Error - Course: Material required!");
        materials.add(a);
        notifyObservers();
    }

    public void addObservers(CourseListener a) {
        if (a == null) throw new IllegalArgumentException("Error - Course: Observer required!");
        if (!observers.contains(a)) {
            this.observers.add(a);
        }
    }

    public void removeObservers(CourseListener a) {
        if (a == null) return;
        this.observers.remove(a);
    }

    public void notify(String filepath) {
        for (CourseListener observer : this.observers) {
            observer.update(this);
        }
    }

    public void completeAssessment(Assessment assessment, double score) {
        if (assessment == null) {
            throw new IllegalArgumentException("Error - Course: Assessment required!");
        }
        assessment.setCompleted((float) score);
        notifyObservers();
    }

    private void notifyObservers() {
        notify(null);
    }
}
