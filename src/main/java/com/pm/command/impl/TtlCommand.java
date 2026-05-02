package com.pm.command.impl;

import com.pm.command.Command;
import com.pm.resp.RespEncoder;
import com.pm.store.Store;

public class TtlCommand implements Command {
    private final Store store;

    public TtlCommand(Store store) {
        this.store = store;
    }

    @Override
    public byte[] execute(String[] args) {
        if(args.length != 2) {
            return RespEncoder.error("wrong number of arguments for 'ttl' command");
        }

        return RespEncoder.integer(store.ttl(args[1]));
    }
}
