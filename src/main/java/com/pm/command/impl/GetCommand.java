package com.pm.command.impl;

import com.pm.command.Command;
import com.pm.resp.RespEncoder;
import com.pm.store.Store;

public class GetCommand implements Command {
    private final Store store;

    public GetCommand(Store store) {
        this.store = store;
    }
    @Override
    public byte[] execute(String[] args) {
        if(args.length < 2) {
            return RespEncoder.error("wrong number of arguments for 'get' command");
        }

        String value = store.get(args[1]);
        return value == null ? RespEncoder.nullBulk() : RespEncoder.bulkString(value);
    }
}
