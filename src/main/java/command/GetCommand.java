package command;

import model.Data;
import store.GlobalStore;
import utils.ResponseBuilder;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

class GetCommand implements Command {
    ConcurrentHashMap<String, Data> dataStore = GlobalStore.getInstance().getDataStore();
    private final List<Object> arguments;

    public GetCommand(List<Object> arguments) {
        this.arguments = arguments;
    }

    @Override
    public void execute(BufferedWriter writer) throws IOException {
        String key = (String) arguments.get(2);
        Data data = dataStore.get(key);

        if (data == null || data.getExpiry() < System.currentTimeMillis()) {
            writer.write(ResponseBuilder.buildNullBulkString());
        } else {
            writer.write(ResponseBuilder.buildBulkString(data.getValue()));
        }
    }
}
