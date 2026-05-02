package com.smarttravel.command;

public interface Command {
    void execute();

    void undo();
}
