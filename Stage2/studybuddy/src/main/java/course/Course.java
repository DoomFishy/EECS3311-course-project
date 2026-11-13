package course;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import course.assessment.Assessment;
import course.material.Material;

public class Course {

    private final String id;
    private final String name;
    private GradeReport gradeReport;
    private final List<Assessment> assessment;
    private final List<Material> materials;
    private final List<CourseListener> observers;
    private final Set<String> selectedMaterialIds;

    public Course(String id, String name) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Error - Course: ID required!");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Error - Course: Name required!");

        this.id = id;
        this.name = name;

        this.observers = new ArrayList<>();
        this.assessment = new ArrayList<>();
        this.materials = new ArrayList<>();
        this.selectedMaterialIds = new HashSet<>();
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
        double totalWeight = assessment.stream().mapToDouble(Assessment::getWeight).sum() + a.getWeight();
        if (totalWeight > 1.0 + 1e-9) {
            throw new IllegalArgumentException("Total assessment weight cannot exceed 100%.");
        }
        assessment.add(a);
        setGradeReport(gradeReport == null ? 0f : gradeReport.getCourseGoal());
        notifyObservers();
    }

    public List<Material> getMaterials() {
        return Collections.unmodifiableList(materials);
    }

    public List<Material> getSelectedMaterials() {
        if (selectedMaterialIds.isEmpty()) {
            return new ArrayList<>(materials);
        }
        return materials.stream()
                .filter(material -> selectedMaterialIds.contains(material.getId()))
                .toList();
    }

    public void setSelectedMaterialIds(List<String> ids) {
        selectedMaterialIds.clear();
        if (ids != null) {
            for (String materialId : ids) {
                if (materialId != null && !materialId.isBlank()) {
                    selectedMaterialIds.add(materialId);
                }
            }
        }
        notifyObservers();
    }

    public void toggleMaterialSelection(String materialId, boolean selected) {
        if (materialId == null || materialId.isBlank()) {
            return;
        }
        if (selected) {
            selectedMaterialIds.add(materialId);
        } else {
            selectedMaterialIds.remove(materialId);
        }
        notifyObservers();
    }

    public boolean isMaterialSelected(String materialId) {
        return selectedMaterialIds.isEmpty() || selectedMaterialIds.contains(materialId);
    }

    public void addMaterial(Material a) {
        if (a == null) throw new IllegalArgumentException("Error - Course: Material required!");
        materials.add(a);
        selectedMaterialIds.add(a.getId());
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
