package cli;

import java.util.List;

import course.Course;

public class CreateCourseCommand implements Command {
    private final List<Course> courses;

    public CreateCourseCommand(List<Course> courses) {
        this.courses = courses;
    }

    @Override
    public void execute(String[] args) {
        if (args == null || args.length == 0) {
            System.out.println("Usage: --course \"Course Name\"");
            return;
        }

        String courseName = joinArgs(args, 0).trim();
        if (courseName.isEmpty()) {
            System.out.println("Course name cannot be empty.");
            return;
        }

        Course course = new Course("course-" + (courses.size() + 1), courseName);
        courses.add(course);
        System.out.println("Course created: " + course.getName());
    }
}
