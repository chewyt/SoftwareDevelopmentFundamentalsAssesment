package chewyt;

import java.net.Socket;

import java.util.ArrayList;
import java.net.*;
import java.io.*;

public class HttpClientConnection {

    private Socket socket;
    private BufferedReader br;
    private BufferedWriter bw;
    private String filename;

    HttpClientConnection(Socket socket) {
        this.socket = socket;
    }
}
