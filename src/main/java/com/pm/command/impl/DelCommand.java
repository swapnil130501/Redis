package com.pm.command.impl;

import com.pm.command.Command;
import com.pm.resp.RespEncoder;
import com.pm.store.Store;

import java.util.Arrays;

public class DelCommand implements Command {
    private final Store store;

    public DelCommand(Store store) {
        this.store = store;
    }

    @Override
    public byte[] execute(String[] args) {
        if(args.length <= 1){
            return RespEncoder.error("wrong number of arguments for 'del' command");
        }

        String[] keys = Arrays.copyOfRange(args, 1, args.length);
        return RespEncoder.integer(store.delete(keys));
    }
}
