package command;

import java.util.List;

public class CommandFactory {
    public Command createCommand(List<Object> fullCommand) {
        if (fullCommand == null || fullCommand.isEmpty()) {
            return null;
        }

        String commandType = ((String) fullCommand.getFirst()).toLowerCase();
        return switch (commandType) {
            case "ping" -> new PingCommand();
            case "echo" -> new EchoCommand(fullCommand);
            default -> null;
        };
    }
}