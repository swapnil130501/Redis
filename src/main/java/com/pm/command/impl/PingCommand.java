package com.pm.command.impl;

import com.pm.command.Command;
import com.pm.resp.RespEncoder;

public class PingCommand implements Command {
    @Override
    public byte[] execute(String[] args) {
        // PING with no args → +PONG
        // PING "hello"     → +hello
        if(args.length > 1) {
            return RespEncoder.simpleString(args[1]);
        }

        return RespEncoder.simpleString("PONG");
    }
}