package cli.course_commands;

import cli.Command;

public class QuitCommand implements Command {
    @Override
    public void execute(String[] args) {
        System.out.println("Goodbye.");
        System.exit(0);
    }
}
