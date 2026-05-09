package com.pm.command.impl;

import com.pm.command.Command;
import com.pm.resp.RespEncoder;

public class EchoCommand implements Command {
    @Override
    public byte[] execute(String[] args) {
        if(args.length < 2) {
            return RespEncoder.error("wrong number of arguments for 'echo' command");
        }

        return RespEncoder.bulkString(args[1]);
    }
}