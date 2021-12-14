import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import chewyt.HttpClientConnection;

public class HttpServer {

    private static final int port = 12345;
    private ServerSocket server;

    public HttpServer(ServerSocket server) {
        this.server = server;
    }

    public void startServer() {
        try {
            System.out.println("[SERVER] Server ready. Listening for client...");
            ExecutorService threadPool = Executors.newFixedThreadPool(2); // Third client join in--> Can Add to
                                                                          // clienthandler but thread

            while (!server.isClosed()) {
                Socket socket = server.accept();
                System.out.println("A new client has connected.");

                HttpClientConnection clientConnection = new HttpClientConnection(socket);

                // Code for auto running and scheduling of threads from Threadpool by Executor
                // Service
                threadPool.submit(clienthandler);

            }
        } catch (IOException e) {
            System.out.println(e.getStackTrace());
        }
    }

}
