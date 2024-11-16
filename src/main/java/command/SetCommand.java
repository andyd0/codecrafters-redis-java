package command;

import model.Data;
import store.GlobalStore;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

class SetCommand implements Command {
    ConcurrentHashMap<String, Data> dataStore = GlobalStore.getInstance().getDataStore();
    private final List<Object> arguments;

    public SetCommand(List<Object> arguments) {
        this.arguments = arguments;
    }

    @Override
    public void execute(BufferedWriter writer) throws IOException {
        String key = (String) arguments.get(2);
        String value = String.join("\r\n", arguments.stream()
                .skip(3)
                .limit(2)
                .map(Object::toString)
                .toArray(String[]::new)) + "\r\n";

        Data data;
        if (arguments.size() > 6 && ((String) arguments.get(6)).equalsIgnoreCase("PX")) {
            long expiry = Integer.parseInt((String) arguments.get(8)) + System.currentTimeMillis();
            data = new Data(value, expiry);
        } else {
            data = new Data(value);
        }

        dataStore.put(key, data);
        writer.write("+OK\r\n");
    }
}
