package command;

import java.io.BufferedWriter;
import java.io.IOException;

public interface Command {
    void execute(BufferedWriter writer) throws IOException;
}
