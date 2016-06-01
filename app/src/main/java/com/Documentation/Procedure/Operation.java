package com.Documentation.Procedure;

import com.DVL.DVL;
import com.DVL.DVLNode;
import com.Documentation.Procedure.Commands.MoveCommand;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alex-lenovi on 5/24/2016.
 */
public class Operation {
    private Step step;
    private Element operation_element;
    private List<DVLNode> dvl_nodes;
    private DVL dvl = DVL.getInstance();
    private CommandSequence sequence;
    public Operation(Step step, Element operation_element) {
        this.operation_element = operation_element;
        this.step = step;
        dvl_nodes = new ArrayList<>();
        NodeList parts_elements = operation_element.getElementsByTagName("part");
        for (int i=0; i<parts_elements.getLength();i++){
            dvl_nodes.addAll(DVL.getInstance().getNodesByName(parts_elements.item(i).getTextContent()));
        }
        sequence = new CommandSequence();
    }

    public void run() {


        switch (operation_element.getTagName().toLowerCase()){
            case "isolate-parent":
                break;
            case "highlight":
                break;
            case "remove":
                float amplitude =1;
                if (operation_element.hasAttribute("amplitude") ) amplitude = Float.valueOf(operation_element.getAttribute("amplitude"));
                sequence.addCommand(new MoveCommand(dvl_nodes,operation_element.getAttribute("dir"),amplitude));
                break;
            case "focus":
                
                break;
            case "hide":
                break;
            case "show":
                break;
            case "opacity":
                break;
        }
    }
}
