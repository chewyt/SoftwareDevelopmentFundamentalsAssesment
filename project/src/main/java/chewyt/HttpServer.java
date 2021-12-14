package chewyt;

import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer {

    private int port;
    private ServerSocket server;
    private String[] docRoots;

    public HttpServer(String[] docRoots, int port) throws IOException {

        this.docRoots = docRoots;
        this.port = port;
        this.server = new ServerSocket(port);
    }

    public void startServer() {
        try {

            ExecutorService threadPool = Executors.newFixedThreadPool(5); // Third client join in--> Can Add to
                                                                          // clienthandler but thread

            while (!server.isClosed()) {

                // Test conditions before listening for client connection
                for (int i = 0; i < docRoots.length; i++) {
                    File directory = new File(docRoots[i]);
                    System.out.println(
                            "Checking Path " + (i + 1) + "/" + docRoots.length + " [" + docRoots[i] + "] ....");
                    if (!directory.exists()) {
                        System.out.println("Path [" + docRoots[i] + "] does not exists");
                        System.out.println("Program aborted at Test check " + (i + 1) + "/" + docRoots.length);
                        System.exit(1);
                    }
                    if (!directory.isDirectory()) {
                        System.out.println("Path [" + docRoots[i] + "] is not a directory");
                        System.out.println("Program aborted at Test check " + (i + 1) + "/" + docRoots.length);
                        System.exit(1);
                    }
                    if (!directory.canRead()) {
                        System.out.println("Path [" + docRoots[i] + "] is not readable by server");
                        System.out.println("Program aborted at Test check " + (i + 1) + "/" + docRoots.length);
                        System.exit(1);
                    }
                    System.out.println("Path [" + docRoots[i] + "] passed test conditions");
                }

                System.out.println("[SERVER] Server ready. Listening for client... Port " + port);
                Socket socket = server.accept();
                System.out.println("A new client has connected.");

                HttpClientConnection clienthandler = new HttpClientConnection(socket, docRoots);

                // Code for auto running and scheduling of threads from Threadpool by Executor
                // Service
                threadPool.submit(clienthandler);

            }
        } catch (IOException e) {
            closeServerSocket();
        }
    }

    public void closeServerSocket() {

        try {
            if (server != null) {
                server.close();
            }

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (IOException i) {
            i.printStackTrace();
        }
    }
}
