package store;

import model.Data;

import java.util.concurrent.ConcurrentHashMap;

public class GlobalStore {
    private static final GlobalStore INSTANCE = new GlobalStore();

    private final ConcurrentHashMap<String, Data> dataStore = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> config = new ConcurrentHashMap<>();

    private GlobalStore() {}

    public static GlobalStore getInstance() {
        return INSTANCE;
    }

    public ConcurrentHashMap<String, Data> getDataStore() {
        return dataStore;
    }

    public ConcurrentHashMap<String, String> getConfig() {
        return config;
    }
}