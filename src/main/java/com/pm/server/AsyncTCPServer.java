package com.pm.server;

import com.pm.command.CommandParser;
import com.pm.resp.RespParser;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public class AsyncTCPServer {
    public static void main(String[] args) throws IOException {
        // Create a selector (epoll instance in Linux)
        // ⤷ Under the hood: epoll_create()
        Selector selector = Selector.open();

        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.bind(new InetSocketAddress(7379));
        serverChannel.configureBlocking(false);

        // Register the server socket FD with the selector for ACCEPT events
        // ⤷ Under the hood: epoll_ctl(ADD, server_fd, EPOLLIN)
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        System.out.println("Listening on port 7379 (NIO)...");

        // Event loop — continuously wait for events (I/O readiness)
        while(true) {
            // ⤷ Under the hood: epoll_wait(epfd, events, MAX_EVENTS, -1)
            // Blocks until some file descriptors (channels) are ready
            selector.select();
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
                    ByteBuffer buf = ByteBuffer.allocate(4096);
                    buf.clear();
                    int n = client.read(buf);

                    if(n == -1) {
                        key.cancel();
                        client.close();
                    }

                    else {
                        try {
                            Object parsed = RespParser.parse(buf.array(), n);
                            byte[] response = CommandParser.dispatch(parsed);
                            client.write(ByteBuffer.wrap(response));
                        } catch (Exception e) {
                            e.printStackTrace();
                            String err = "-ERR parse error: " + e.getMessage() + "\r\n";
                            client.write(ByteBuffer.wrap(err.getBytes()));
                        }
                    }
                }
            }
        }
    }
}

