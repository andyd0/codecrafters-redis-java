package command;

import model.Data;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class CommandFactory {
    public Command createCommand(List<Object> command) {
        if (command == null || command.isEmpty()) {
            return null;
        }

        String commandType = ((String) command.getFirst()).toLowerCase();
        return switch (commandType) {
            case "ping" -> new PingCommand();
            case "echo" -> new EchoCommand(command);
            case "set" -> new SetCommand(command);
            case "get" -> new GetCommand(command);
            case "config" -> new ConfigCommand(command);
            default -> null;
        };
    }
}