package cli;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import course.Course;

public abstract class CommandRegistry {
    protected final List<Course> courses;
    protected final Session session;
    private final Map<String, Command> commands = new HashMap<>();
    private CommandRegistry nextRegistry = null;
    
    protected CommandRegistry(List<Course> courses, Session session) {
        this.courses = courses;
        this.session = session;
        registerCommands();
    }

    protected abstract void registerCommands();

    public boolean execute(String rawCommandName, String[] args) {
        String commandName = normalize(rawCommandName);
        Command command = commands.get(commandName);
        if (command == null) {
            return false;
        }
        command.execute(args == null ? new String[0] : args);
        return true;
    }

    protected final void register(String name, Command command) {
        commands.put(normalize(name), command);
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }
        return value.trim().toLowerCase(Locale.ROOT).replaceFirst("^--+", "");
    }

    public void switchTo(CommandRegistry registry) {
        this.nextRegistry = registry;
    }

    public CommandRegistry getNextRegistry() {
        CommandRegistry next = this.nextRegistry;
        this.nextRegistry = null; // consume it so we don't switch again next loop
        return next;
    }
}