package ai.controller;

import ai.template.AITemplate;
import course.GradeReport;

public class GradeController extends AITemplate {
    @Override
    public String execute() {
        if (assessments == null || assessments.isEmpty()) {
            return "No assessments available for grade computation.";
        }

        GradeReport report = new GradeReport("course-goal", 0f, assessments);
        return "Current course average: " + report.calculateAverage();
    }
}
