package command;

import model.Data;
import store.GlobalStore;
import utils.ResponseBuilder;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

class KeysCommand implements Command {
    ConcurrentHashMap<String, Data> dataStore = GlobalStore.getInstance().getDataStore();

    @Override
    public void execute(BufferedWriter writer) throws IOException {
        Set<String> keys = dataStore.keySet();
        String response = ResponseBuilder.buildBulkStringArray(keys.toArray(String[] ::new));
        writer.write(response);
    }
}
