package com.pm.command.impl;

import com.pm.command.Command;
import com.pm.resp.RespEncoder;
import com.pm.store.Store;

public class ExpireCommand implements Command {
    private final Store store;

    public ExpireCommand(Store store) {
        this.store = store;
    }

    @Override
    public byte[] execute(String[] args) {
        if(args.length < 3) {
            return RespEncoder.error("wrong number of arguments for 'expire' command");
        }

        String key = args[1];
        long ttlSeconds = Long.parseLong(args[2]);
        return RespEncoder.integer(store.setExpTime(key, ttlSeconds));
    }
}
