package ai.controller;

import ai.template.AITemplate;

public class TaskController extends AITemplate {
    @Override
    public String execute() {
        if (assessments == null || assessments.isEmpty()) {
            return "No tasks to schedule.";
        }

        return assessments.stream()
            .sorted((a, b) -> a.getDeadline().compareTo(b.getDeadline()))
            .map(a -> "Task: " + a.getName() + " due " + a.getDeadline())
            .reduce("[TaskController] Turning deadlines into an execution task list.\n", (acc, item) -> acc + item + "\n", String::concat);
    }
}
