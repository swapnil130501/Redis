package com.pm.command;

import com.pm.command.impl.GetCommand;
import com.pm.command.impl.PingCommand;
import com.pm.command.impl.SetCommand;
import com.pm.resp.RespEncoder;
import com.pm.store.Store;

import java.util.HashMap;
import java.util.Map;

public class CommandParser {
    private static final Map<String, Command> commands = new HashMap<>();

    static {
        Store store = new Store();
        commands.put("PING", new PingCommand());
        commands.put("SET", new SetCommand(store));
        commands.put("GET", new GetCommand(store));
    }

    public static byte[] dispatch(Object parsed) {
        if(!(parsed instanceof Object[] arr) || arr.length == 0) {
            return RespEncoder.error("invalid command format");
        }

        String[] cmdArgs = new String[arr.length];
        for(int i = 0; i < arr.length; i++) {
            cmdArgs[i] = (String) arr[i];
        }

        Command cmd = commands.get(cmdArgs[0].toUpperCase());
        if(cmd == null) {
            return RespEncoder.error("unknown command '" + cmdArgs[0] + "'");
        }

        return cmd.execute(cmdArgs);
    }
}