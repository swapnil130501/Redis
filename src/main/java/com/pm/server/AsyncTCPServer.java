package com.pm.server;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public class AsyncTCPServer {
    public static void main(String[] args) throws IOException {
        Selector selector = Selector.open();

        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.bind(new InetSocketAddress(7379));
        serverChannel.configureBlocking(false);
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        System.out.println("Listening on port 7379 (NIO)...");

        ByteBuffer buf = ByteBuffer.allocate(4096);

        while(true) {
            selector.select(); //epoll.wait(), blocking method

            Iterator<SelectionKey> keys = selector.selectedKeys().iterator();

            while(keys.hasNext()) {
                SelectionKey key = keys.next();
                keys.remove();

                if(key.isAcceptable()) {
                    SocketChannel client = serverChannel.accept();
                    client.configureBlocking(false);
                    client.register(selector, SelectionKey.OP_READ);
                    System.out.println("Accepted: " + client.getRemoteAddress());

                }

                else if(key.isReadable()) {
                    SocketChannel client = (SocketChannel) key.channel();
                    buf.clear();
                    int n = client.read(buf);

                    if(n == -1) {
                        System.out.println("Client disconnected");
                        key.cancel();
                        client.close();
                    }

                    else {
                        System.out.print("Received: " + new String(buf.array(), 0, n));
                        buf.flip();
                        client.write(buf);
                    }
                }
            }
        }
    }
}

