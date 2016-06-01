package com.Documentation.Procedure;

import java.util.Stack;

/**
 * Created by ADSL on 01/06/2016.
 */
public class CommandSequence {
    private Stack<DVLCommand> dvl_commands;

    public CommandSequence() {
        dvl_commands = new Stack<>();
    }

    public void addCommand(DVLCommand cmd) {
        dvl_commands.push(cmd);
    }

    public void execute(){

    }

}
