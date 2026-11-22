package cli;

import java.util.List;

import course.Course;

public interface Command {
    void execute(String[] args);

    default String joinArgs(String[] args, int startIndex) {
        if (args == null || startIndex >= args.length) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = startIndex; i < args.length; i++) {
            if (i > startIndex) {
                sb.append(" ");
            }
            sb.append(args[i]);
        }
        return sb.toString();
    }

    default Course getDefaultCourse(List<Course> courses) {
        return courses.isEmpty() ? null : courses.get(0);
    }
}
