package command;

import store.GlobalStore;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

class ConfigCommand implements Command {
    public static final String GET_SUBCOMMAND = "GET";
    public static final int SUBCOMMAND_INDEX = 2;
    public static final int CONFIG_KEY_INDEX = 4;
    private final List<Object> arguments;
    ConcurrentHashMap<String, String> config = GlobalStore.getInstance().getConfig();

    public ConfigCommand(List<Object> arguments) {
        this.arguments = arguments;
    }

    @Override
    public void execute(BufferedWriter writer) throws IOException {
        String response = "";
        if (arguments.get(SUBCOMMAND_INDEX).equals(GET_SUBCOMMAND)) {
            response = getSubcommand();
        }
        writer.write(response);
    }

    private String getSubcommand() {
        String configKey = (String) arguments.get(CONFIG_KEY_INDEX);
        String configValue = config.get(configKey);
        return buildResponse(configKey, configValue);
    }

    private String buildResponse(String configKey, String configValue) {
        List<String> response = new ArrayList<>();
        response.add("*2");
        response.add("$" + configKey.length());
        response.add(configKey);
        response.add("$" + configValue.length());
        response.add(configValue);

        return String.join("\r\n", response) + "\r\n";
    }
}
