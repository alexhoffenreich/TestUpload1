package com.Documentation.Procedure.Commands;

import com.DVL.DVLNode;
import com.Documentation.Procedure.DVLCommand;

import java.util.List;

/**
 * Created by ADSL on 01/06/2016.
 */
public class HighlightCommand extends DVLCommand {
    private int color;
    public HighlightCommand(List<DVLNode> nodes, int color) {
        super(nodes);
        this.color = color;
    }

    @Override
    public void reset() {
        for (DVLNode node: nodes){
            node.clearHighLight();
        }
    }

    @Override
    public void fullyExecute() {
        for (DVLNode node: nodes){
            node.setHighLight(color);
        }
    }

    @Override
    public boolean partiallyExecute() {
        return false;
    }
}
