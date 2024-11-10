package command;

import java.io.BufferedWriter;
import java.io.IOException;

class PingCommand implements Command {
    @Override
    public void execute(BufferedWriter writer) throws IOException {
        writer.write("+PONG\r\n");
    }
}
