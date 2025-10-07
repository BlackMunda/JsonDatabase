package org.example.Server.commands;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public interface Command {
    ReadWriteLock lock = new ReentrantReadWriteLock(true);
    String execute();
}
