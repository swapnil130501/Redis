package com.pm.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class SyncTCPServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(7379);
        serverSocket.setReuseAddress(true);
        System.out.println("Listening on port 7379...");

        while(true) {
            Socket client = serverSocket.accept();
            System.out.println("Client connected: " + client.getRemoteSocketAddress());
            handleClient(client);
        }
    }

    static void handleClient(Socket client) {
        try (
                InputStream in = client.getInputStream();
                OutputStream out = client.getOutputStream()
        ) {
            byte[] buf = new byte[4096];
            int n;
            while((n = in.read(buf)) != -1) {
                System.out.print("Received: " + new String(buf, 0, n));
                out.write(buf, 0, n);
                out.flush();
            }
        } catch (IOException e) {
            System.err.println("Client error: " + e.getMessage());
        } finally {
            try { client.close(); } catch (IOException ignored) {}
        }
    }
}
