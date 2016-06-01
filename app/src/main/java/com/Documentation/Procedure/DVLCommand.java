package com.Documentation.Procedure;


import com.DVL.DVL;
import com.DVL.DVLNode;

import java.util.List;

/**
 * Created by ADSL on 26/05/2016.
 */
public abstract class DVLCommand {
    protected List<DVLNode> nodes;
    private DVL dvl = DVL.getInstance();
    float progress;

    public DVLCommand(List<DVLNode> nodes) {
        this.nodes = nodes;
    }

    abstract public void reset ();
    abstract public void fullyExecute();
    abstract public boolean partiallyExecute();
}
