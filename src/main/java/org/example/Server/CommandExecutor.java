package org.example.Server;

import org.example.Server.Commands.Command;

public class CommandExecutor {
    Command command;

    public CommandExecutor(Command command) {
        this.command = command;
    }

    public String run() {
        return command.execute();
    }
}
