// resp/RespParser.java
package com.pm.resp;

import java.io.*;

public class RespParser {
    // Entry point — returns parsed value as Object
    // String   → simple string / error / bulk string
    // Long     → integer
    // Object[] → array
    public static Object parse(byte[] raw, int len) throws IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(new ByteArrayInputStream(raw, 0, len))
        );

        return parseValue(reader);
    }

    private static Object parseValue(BufferedReader reader) throws IOException {
        String line = reader.readLine();
        System.out.println(line);

        if(line == null) {
            throw new IOException("Unexpected end of input");
        }

        char type = line.charAt(0);
        String payload = line.substring(1);

        return switch (type) {
            case '+' -> parseSimpleString(payload);
            case '-' -> parseError(payload);
            case ':' -> parseInteger(payload);
            case '$' -> parseBulkString(payload, reader);
            case '*' -> parseArray(payload, reader);
            default  -> throw new IOException("Unknown RESP type: " + type);
        };
    }

    // +OK\r\n
    private static String parseSimpleString(String payload) {
        return payload;
    }

    // -ERR some message\r\n
    private static String parseError(String payload) {
        throw new RuntimeException("Redis error: " + payload);
    }

    // :42\r\n
    private static long parseInteger(String payload) {
        return Long.parseLong(payload);
    }

    // $6\r\nfoobar\r\n
    // $-1\r\n  → null bulk string (key miss)
    private static String parseBulkString(String payload, BufferedReader reader) throws IOException {
        int len = Integer.parseInt(payload);
        if(len == -1) {
            return null;
        }

        return reader.readLine();
    }

    // *2\r\n$3\r\nGET\r\n$5\r\nhello\r\n
    // *-1\r\n  → null array
    private static Object[] parseArray(String payload, BufferedReader reader) throws IOException {
        int count = Integer.parseInt(payload);
        if(count == -1) {
            return null;
        }

        Object[] elements = new Object[count];
        for(int i = 0; i < count; i++) {
            elements[i] = parseValue(reader);
        }

        return elements;
    }
}