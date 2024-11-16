import model.Data;
import store.GlobalStore;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args){
        ServerSocket serverSocket = null;
        int port = 6379;

        String dir = "";
        String dbFileName = "";
        if (args.length > 0) {
            dir = args[1];
            dbFileName = args[3];
        };

        ConcurrentHashMap<String, String> config = GlobalStore.getInstance().getConfig();
        config.put("dir", dir);
        config.put("dbFileName", dbFileName);

        // Using ExecutorService to reuse threads
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setReuseAddress(true);
            while (!executorService.isShutdown()) {
                try {
                    // Wait for connection from client.
                    final Socket clientSocket = serverSocket.accept();
                    executorService.execute(new ClientHandler(clientSocket));
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
}
