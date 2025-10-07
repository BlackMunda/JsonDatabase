package org.example.Server.execution;

import org.example.Server.commands.Command;

public class CommandInvoker {
    private final Command command;

    public CommandInvoker(Command command) {
        this.command = command;
    }

    public String run() {
        return command.execute();
    }
}
