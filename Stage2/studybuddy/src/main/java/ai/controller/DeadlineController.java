package ai.controller;

import ai.template.AITemplate;

public class DeadlineController extends AITemplate {
    @Override
    public String execute() {
        if (assessments == null || assessments.isEmpty()) {
            return "No assessments available to rank.";
        }

        return assessments.stream()
            .sorted((a, b) -> a.getDeadline().compareTo(b.getDeadline()))
            .map(a -> a.getName() + " -> " + a.getDeadline())
            .reduce("[DeadlineController] Ranking and prioritizing upcoming deadlines.\n", (acc, item) -> acc + item + "\n", String::concat);
    }
}
