package com.Documentation.Procedure.Commands;

import com.DVL.DVLNode;
import com.Documentation.Procedure.DVLCommand;

import java.util.List;

/**
 * Created by ADSL on 01/06/2016.
 */
public class ZoomCommand extends DVLCommand {
    private final boolean isolate;
    private final float fade_time;

    public ZoomCommand(List<DVLNode> nodes, boolean isolate, float fade_time) {
        super(nodes);
        this.isolate = isolate;
        this.fade_time = fade_time;

    }

    @Override
    public void reset() {

    }

    @Override

    public void fullyExecute() {

        if (nodes.size()>0)
        {
            nodes.get(1).zoomTo(isolate,fade_time);
        }
    }

    @Override
    public boolean partiallyExecute() {
        return false;
    }
}
