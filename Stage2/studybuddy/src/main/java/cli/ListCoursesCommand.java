package cli;

import java.util.List;

import course.Course;

public class ListCoursesCommand implements Command {
    private final List<Course> courses;

    public ListCoursesCommand(List<Course> courses) {
        this.courses = courses;
    }

    @Override
    public void execute(String[] args) {
        if (courses == null || courses.isEmpty()) {
            System.out.println("No courses available yet.");
            return;
        }

        for (Course course : courses) {
            System.out.println("- " + course.getName());
        }
    }
}
