package cli;

import ai.controller.AppFacade;
import ai.factory.AIFactory;
import course.Course;

public class Session {
    private Course currentCourse;
    private AppFacade facade;

    public Course getCurrentCourse() {
        return currentCourse;
    }

    public void setCurrentCourse(Course course) {
        this.currentCourse = course;
        if (course != null) {
            // Create it ONCE when entering the course
            this.facade = new AppFacade(AIFactory.createDefault(), course);
        } else {
            this.facade = null;
        }
    }

    public AppFacade getFacade() {
        return facade;
    }

    public boolean hasCourse() {
        return currentCourse != null && facade != null;
    }

    public void clearCurrentCourse() {
        setCurrentCourse(null);
    }
}