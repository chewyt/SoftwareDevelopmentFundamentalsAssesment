package chewyt;

import java.net.Socket;
import java.net.*;
import java.io.*;

public class HttpClientConnection implements Runnable {

    private Socket socket;
    private BufferedReader br;
    private BufferedWriter bw;
    private OutputStream os;
    private String[] docRoots;

    HttpClientConnection(Socket socket, String[] docRoots) {
        try {
            this.socket = socket;

            this.os = socket.getOutputStream();

            this.bw = new BufferedWriter(new OutputStreamWriter(os));
            this.br = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));

            this.docRoots = docRoots;
        } catch (IOException e) {
            closeEverything(socket, br, bw);
        }

    }

    public void closeEverything(Socket socket, BufferedReader br, BufferedWriter bw) {

        try {
            if (br != null) {
                br.close();
            }
            if (bw != null) {
                bw.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean resourceExists(String[] docRoots, String resource) {

        for (String path : docRoots) {
            File html = new File(path + resource);
            if (html.exists()) {
                return true;
            }
        }
        return false;
    }

    public String htmlConvert(String[] docRoots, String resource) {

        String htmlScript = "";
        String line = null;

        for (String path : docRoots) {
            File html = new File(path + resource);
            if (html.exists()) {
                System.out.println("Copying resource...");
                try (FileReader fr = new FileReader(html)) {
                    BufferedReader reader = new BufferedReader(fr);
                    while ((line = reader.readLine()) != null) {
                        htmlScript += line + "\n";
                    }
                } catch (FileNotFoundException e) {
                    System.out.println("A File not found error occurred.");
                    closeEverything(socket, br, bw);
                } catch (IOException i) {
                    System.out.println("An IO error occurred.");
                    closeEverything(socket, br, bw);
                }
                return htmlScript;
            }
        }
        return htmlScript;
    }

    @Override
    public void run() {

        String request = "";
        String requestMethod = "";
        String resource = "";

        try {

            request = br.readLine(); // blocking operation
            // System.out.println(request);
            requestMethod = request.split(" ")[0];
            resource = request.split(" ")[1];

        } catch (IOException e) {
            closeEverything(socket, br, bw);
        }

        // ACTIONS to be performed
        // ACTION 1

        if (!requestMethod.equals("GET")) {
            try {

                HttpWriter writer = new HttpWriter(os);
                writer.writeString("HTTP/1.1 405 Method Not Allowed\r\n");
                writer.writeString("\r\n");
                writer.writeString("<" + requestMethod + "> not supported\r\n");
                writer.close();

                System.exit(1);
            } catch (IOException e) {
                closeEverything(socket, br, bw);
            } catch (Exception e) {
                e.printStackTrace();
                closeEverything(socket, br, bw);
            }
        } else {
            System.out.println("GET method is correct!");
        }
        // ACTION 2

        if (resource.equals("/")) {
            resource = "/index.html";
        }

        if (!resourceExists(docRoots, resource)) {

            try {
                HttpWriter writer = new HttpWriter(os);
                writer.writeString("HTTP/1.1 404 Not Found\r\n");
                writer.writeString("\r\n");
                writer.writeString("<" + resource + "> not found\r\n");
                writer.close();
                System.exit(1);
            } catch (IOException e) {
                closeEverything(socket, br, bw);
            } catch (Exception e) {
                closeEverything(socket, br, bw);
                e.printStackTrace();
            }

        } else {
            System.out.println("Resource exists!");
        }

        // ACTION 3
        String htmlStream = htmlConvert(docRoots, resource);

        try {
            HttpWriter writer = new HttpWriter(os);
            writer.writeString("HTTP/1.1 200 OK\r\n");
            writer.writeString("\r\n");
            writer.writeString(htmlStream);
            writer.close();
        } catch (IOException e) {
            closeEverything(socket, br, bw);
        } catch (Exception e) {
            closeEverything(socket, br, bw);
            e.printStackTrace();
        }

        // System.exit(0);
        /*
         * File directory =
         * if (!resource.equals("GET")) {
         * try {
         * bw.write("HTTP/1.1 404 Not Found\r\n\r\n<Resource name> not found\r\n");
         * bw.newLine();
         * bw.flush();
         * System.out.
         * println("HTTP/1.1 404 Not Found\r\n\r\n<Resource name> not found\r\n");
         * System.exit(1);
         * } catch (IOException e) {
         * closeEverything(socket, br, bw);
         * }
         * }
         */
        // while (socket.isConnected()) {

        // }

    }
}
