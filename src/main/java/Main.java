import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args){
        ServerSocket serverSocket = null;
        int port = 6379;

        // Using ExecutorService to reuse threads
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setReuseAddress(true);

            while (!executorService.isShutdown()) {
                try {
                    // Wait for connection from client.
                    final Socket clientSocket = serverSocket.accept();
                    executorService.execute(() -> {
                        try {
                            processCommand(clientSocket);
                        } finally {
                            try {
                                clientSocket.close();
                            }
                            catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (IOException e) {
                    if (!executorService.isShutdown()) {
                        System.err.println("Error accepting client connection: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            executorService.shutdown();
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void processCommand(Socket clientSocket) {
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));

            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(clientSocket.getOutputStream()));

            String input;
            while ((input = reader.readLine()) != null) {
                if (input.toLowerCase().startsWith("ping")) {
                    writer.write("+PONG\r\n");
                    writer.flush();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
