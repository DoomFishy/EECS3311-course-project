package ai.tool;

public interface Tool {
    String execute(String input);
    default boolean supports(String type) {
        return true;
    }
}
