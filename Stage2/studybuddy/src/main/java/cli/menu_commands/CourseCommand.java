package cli.menu_commands;

import java.util.List;

import cli.Command;
import cli.CommandRegistry;
import cli.CourseCommandRegistry;
import cli.Session;
import course.Course;

public class CourseCommand implements Command {
    private final List<Course> courses;
    private final Session session;
    private final CommandRegistry registry; // To handle the switchTo

    public CourseCommand(List<Course> courses, Session session, CommandRegistry registry) {
        this.courses = courses;
        this.session = session;
        this.registry = registry;
    }

    @Override
    public void execute(String[] args) {
        if (args == null || args.length == 0) {
            printUsage();
            return;
        }

        String flag = args[0].toLowerCase();

        String[] subArgs = new String[args.length - 1];
        System.arraycopy(args, 1, subArgs, 0, args.length - 1);

        switch (flag) {
            case "-list":
            case "list":
                handleList();
                break;
            case "-create":
            case "create":
                handleCreate(subArgs);
                break;
            case "-delete":
            case "delete":
                handleDelete(subArgs);
                break;
            case "-enter":
            case "enter":
                handleEnter(subArgs);
                break;
            default:
                System.out.println("Unknown flag: " + flag);
                printUsage();
                break;
        }
    }

    private void handleList() {
        if (courses == null || courses.isEmpty()) {
            System.out.println("No courses available yet.");
            return;
        }

        System.out.println("Available courses:");
        for (Course course : courses) {
            System.out.println("- " + course.getName());
        }
    }

    private void handleCreate(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: course -create <Course Name>");
            return;
        }

        String courseName = joinArgs(args, 0).trim();
        if (courseName.isEmpty()) {
            System.out.println("Course name cannot be empty.");
            return;
        }

        // Check if course already exists
        boolean exists = courses.stream().anyMatch(c -> c.getName().equalsIgnoreCase(courseName));
        if (exists) {
            System.out.println("Error: A course named \"" + courseName + "\" already exists.");
            return;
        }

        Course course = new Course("course-" + (courses.size() + 1), courseName);
        courses.add(course);
        System.out.println("Course created: " + course.getName());
    }

    private void handleDelete(String[] args) {
        if (courses == null || courses.isEmpty()) {
            System.out.println("No courses available to delete.");
            return;
        }

        if (args.length == 0) {
            System.out.println("Usage: course -delete <Course Name>");
            return;
        }

        String name = joinArgs(args, 0).trim();
        Course toDelete = null;
        for (Course course : courses) {
            if (course.getName().equalsIgnoreCase(name)) {
                toDelete = course;
                break;
            }
        }

        if (toDelete == null) {
            System.out.println("Course not found: \"" + name + "\"");
            return;
        }

        if (session.hasCourse() && session.getCurrentCourse().equals(toDelete)) {
            session.clearCurrentCourse();
            System.out.println("(Cleared active course because it was deleted)");
        }

        courses.remove(toDelete);
        System.out.println("Course deleted: " + toDelete.getName());
    }

    private void handleEnter(String[] args) {
        if (courses == null || courses.isEmpty()) {
            System.out.println("No courses available. Create one first with 'course -create'.");
            return;
        }

        if (args.length == 0) {
            System.out.println("Usage: course -enter <Course Name>");
            return;
        }

        String name = joinArgs(args, 0).trim();
        Course toEnter = null;
        for (Course course : courses) {
            if (course.getName().equalsIgnoreCase(name)) {
                toEnter = course;
                break;
            }
        }

        if (toEnter != null) {
            session.setCurrentCourse(toEnter);
            System.out.println("Entered course: " + toEnter.getName());
            System.out.println("Type 'help' to see course-specific commands.");
            
            // Switch to the course registry
            registry.switchTo(new CourseCommandRegistry(courses, session));
        } else {
            System.out.println("Course not found: \"" + name + "\"");
        }
    }

    private void printUsage() {
        System.out.println("Usage: course <flag> ...");
        System.out.println("  course -list                    (Show all courses)");
        System.out.println("  course -create <name>           (Create a new course)");
        System.out.println("  course -delete <name>           (Delete a course)");
        System.out.println("  course -enter <name>            (Enter a course)");
    }
}