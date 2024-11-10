package command;


import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

class EchoCommand implements Command {
    private final List<Object> arguments;

    public EchoCommand(List<Object> arguments) {
        this.arguments = arguments.subList(1, arguments.size());
    }

    @Override
    public void execute(BufferedWriter writer) throws IOException {
        String response = String.join("\r\n", arguments.stream()
                .map(Object::toString)
                .toArray(String[]::new)) + "\r\n";
        writer.write(response);
    }
}