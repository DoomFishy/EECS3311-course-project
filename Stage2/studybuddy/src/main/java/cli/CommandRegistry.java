package cli;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import course.Course;

public class CommandRegistry {
    private final List<Course> courses;
    private final Map<String, Command> commands = new HashMap<>();

    public CommandRegistry(List<Course> courses) {
        this.courses = courses;
        register("help", new HelpCommand());
        register("h", new HelpCommand());
        register("list", new ListCoursesCommand(courses));
        register("week", new WeekCommand(courses));
        register("deadlines", new DeadlineCommand(courses));
        register("course", new CreateCourseCommand(courses));
        register("add-course", new AddCourseCommand(courses));
        register("add-assessment", new AddAssessmentCommand(courses));
        register("ask", new AskQuestionCommand(courses));
        register("quit", new QuitCommand());
        register("exit", new QuitCommand());
    }

    public boolean execute(String rawCommandName, String[] args) {
        String commandName = normalize(rawCommandName);
        Command command = commands.get(commandName);
        if (command == null) {
            return false;
        }
        command.execute(args == null ? new String[0] : args);
        return true;
    }

    private void register(String name, Command command) {
        commands.put(normalize(name), command);
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }
        return value.trim().toLowerCase(Locale.ROOT).replaceFirst("^--+", "");
    }
}
