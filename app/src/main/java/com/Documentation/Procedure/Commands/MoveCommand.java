package com.Documentation.Procedure.Commands;


import com.DVL.DVLNode;
import com.Documentation.Procedure.DVLCommand;
import com.sap.ve.SDVLMatrix;

import java.util.List;

/**
 * Created by alex-lenovi on 5/26/2016.
 */
public class MoveCommand extends DVLCommand {

    float delta_x=0, delta_y=0, delta_z=0;

    public MoveCommand(List<DVLNode> dvl_nodes, String dir, float amplitude) {
        super(dvl_nodes);
        switch (dir){
            case "up":
                delta_y = amplitude;
                break;
            case "down":
                delta_y = -amplitude;
                break;
            case "left":
                delta_x = amplitude;
                break;
            case "right":
                delta_x = -amplitude;
                break;
            case "front":
                delta_z = amplitude;
                break;
            case "back":
                delta_z = -amplitude;
                break;
        }
    }


    @Override
    public void reset() {

    }

    @Override
    public void fullyExecute() {
        for (DVLNode node: nodes){

            node.move(delta_x,delta_y,delta_z);
        }

    }


    @Override
    public boolean partiallyExecute() {
        return false;
    }
}
