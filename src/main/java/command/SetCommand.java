package command;

import model.Data;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

class SetCommand implements Command {
    private final ConcurrentHashMap<String, Data> dataStore;
    private final List<Object> arguments;

    public SetCommand(List<Object> arguments, ConcurrentHashMap<String, Data> dataStore) {
        this.arguments = arguments;
        this.dataStore = dataStore;
    }

    @Override
    public void execute(BufferedWriter writer) throws IOException {
        String key = (String) arguments.get(2);
        String value = String.join("\r\n", arguments.stream()
                .skip(3)
                .map(Object::toString)
                .toArray(String[]::new)) + "\r\n";

        Data data = new Data(value);

        dataStore.put(key, data);
        writer.write("+OK\r\n");
    }
}
