package chewyt;

import java.net.Socket;
import java.nio.file.Files;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
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
            closeEverything(socket, br, bw, os);
        }

    }

    public void closeEverything(Socket socket, BufferedReader br, BufferedWriter bw, OutputStream os) {

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
            if (os != null) {
                os.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String htmlConvert(String[] docRoots, String resource) {

        String htmlScript = "";
        String line = null;

        for (String path : docRoots) {
            File html = new File(path + resource);
            if (html.exists()) {
                // System.out.println("Copying resource...");
                try (FileReader fr = new FileReader(html)) {
                    BufferedReader reader = new BufferedReader(fr);
                    while ((line = reader.readLine()) != null) {
                        htmlScript += line + "\n";
                    }
                } catch (FileNotFoundException e) {
                    System.out.println("A File not found error occurred.");
                    closeEverything(socket, br, bw, os);
                } catch (IOException i) {
                    System.out.println("An IO error occurred.");
                    closeEverything(socket, br, bw, os);
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
            closeEverything(socket, br, bw, os);
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
                return;
                // System.exit(1);
            } catch (IOException e) {
                closeEverything(socket, br, bw, os);
            } catch (Exception e) {
                e.printStackTrace();
                closeEverything(socket, br, bw, os);
            }
        }
        // ACTION 2

        if (resource.equals("/")) {
            resource = "/index.html";
        }

        boolean resourceExists = false;
        File foundResource = null;
        for (String path : docRoots) {
            File file = new File(path + resource);
            if (file.exists() && file.isFile()) {
                foundResource = file;
                resourceExists = true;
                break;
            }
        }

        if (!resourceExists) {

            try {
                HttpWriter writer = new HttpWriter(os);
                writer.writeString("HTTP/1.1 404 Not Found\r\n");
                writer.writeString("\r\n");
                writer.writeString("<" + resource + "> not found\r\n");
                writer.close();
                // System.exit(1);
                return;
            } catch (IOException e) {
                closeEverything(socket, br, bw, os);
            } catch (Exception e) {
                closeEverything(socket, br, bw, os);
                e.printStackTrace();
            }

        }

        // ACTION 3

        // ACTION 4 added before ACTION 3 for checking condition

        // System.out.println("resource path: " + foundResource);
        if (!resource.endsWith(".png")) {
            // DO ACTION 3
            String htmlStream = htmlConvert(docRoots, resource);

            try {
                HttpWriter writer = new HttpWriter(os);
                writer.writeString("HTTP/1.1 200 OK\r\n");
                writer.writeString("\r\n");
                writer.writeString(htmlStream);
                writer.close();
            } catch (IOException e) {
                closeEverything(socket, br, bw, os);
            } catch (Exception e) {
                closeEverything(socket, br, bw, os);
                e.printStackTrace();
            }

        } else {
            // Resource is a PNG image

            try {

                String mimetype = Files.probeContentType(foundResource.toPath());
                // System.out.println("Mimetype: " + mimetype);
                HttpWriter writer = new HttpWriter(os);
                writer.writeString("HTTP/1.1 200 OK\r\n" +
                        "Content-Type: " + mimetype + "\r\n");
                writer.writeBytes(Files.readAllBytes(foundResource.toPath()));
                writer.close();
            } catch (IOException e1) {
                closeEverything(socket, br, bw, os);
                e1.printStackTrace();
            } catch (Exception e) {
                closeEverything(socket, br, bw, os);
                e.printStackTrace();
            }
        }
        closeEverything(socket, br, bw, os);
        return;
    }
}
