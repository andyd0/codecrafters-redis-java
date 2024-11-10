import command.Command;
import command.CommandFactory;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final CommandFactory commandFactory;


    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.commandFactory = new CommandFactory();
    }

    @Override
    public void run() {
        try {
            processCommands();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void processCommands() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()))) {

            List<Object> fullCommand;
            while ((fullCommand = Parser.parserCommand(reader)) != null) {
                Command command = commandFactory.createCommand(fullCommand);
                if (command == null) {
                    break;
                }
                command.execute(writer);
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
