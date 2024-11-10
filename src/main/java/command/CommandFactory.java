package command;

import model.Data;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class CommandFactory {
    private final ConcurrentHashMap<String, Data> dataStore;

    public CommandFactory(ConcurrentHashMap<String, Data> dataStore) {
        this.dataStore = dataStore;
    }

    public Command createCommand(List<Object> command) {
        if (command == null || command.isEmpty()) {
            return null;
        }

        String commandType = ((String) command.getFirst()).toLowerCase();
        return switch (commandType) {
            case "ping" -> new PingCommand();
            case "echo" -> new EchoCommand(command);
            case "set" -> new SetCommand(command, dataStore);
            case "get" -> new GetCommand(command, dataStore);
            default -> null;
        };
    }
}