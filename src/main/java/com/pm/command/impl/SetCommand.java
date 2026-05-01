package com.pm.command.impl;

import com.pm.command.Command;
import com.pm.resp.RespEncoder;
import com.pm.store.Store;

public class SetCommand implements Command {
    private final Store store;

    public SetCommand(Store store) {
        this.store = store;
    }

    @Override
    public byte[] execute(String[] args) {
        if(args.length < 3) {
            return RespEncoder.error("wrong number of arguments for 'set' command");
        }

        String key = args[1];
        String value = args[2];
        long exDurationMs = -1;

        for (int i = 3; i < args.length; i++) {
            switch (args[i].toUpperCase()) {
                case "EX":
                    i++;
                    if (i >= args.length) return RespEncoder.error("syntax error");
                    exDurationMs = Long.parseLong(args[i]) * 1000;
                    break;
                case "PX":
                    i++;
                    if (i >= args.length) return RespEncoder.error("syntax error");
                    exDurationMs = Long.parseLong(args[i]);
                    break;
                default:
                    return RespEncoder.error("syntax error");
            }
        }

        store.set(key, value, exDurationMs);
        return RespEncoder.simpleString("OK");
    }
}
