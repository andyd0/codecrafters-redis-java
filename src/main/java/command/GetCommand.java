package command;

import model.Data;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

class GetCommand implements Command {
    private final ConcurrentHashMap<String, Data> dataStore;
    private final List<Object> arguments;

    public GetCommand(List<Object> arguments, ConcurrentHashMap<String, Data> dataStore) {
        this.arguments = arguments;
        this.dataStore = dataStore;
    }

    @Override
    public void execute(BufferedWriter writer) throws IOException {
        String key = (String) arguments.get(2);
        Data data = dataStore.get(key);

        if (data == null || data.getExpiry() < System.currentTimeMillis()) {
            writer.write("$-1\r\n");
        } else {
            writer.write(data.getValue());
        }
    }
}
