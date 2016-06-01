package com.Documentation.Procedure;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alex-lenovi on 5/24/2016.
 */
public class Step {
    private String id;
    private String title;
    private String description;
    private List<Operation> operations;
    private Operation cur_operation;
    private Element step_element;
    private Procedure procedure;

    public Step(Element step_element, Procedure procedure) {
        this.step_element = step_element;
        this.procedure = procedure;
        operations = new ArrayList<>();
        NodeList op_nodes = step_element.getChildNodes();
        for (int i = 0; i < op_nodes.getLength(); i++) {
            operations.add(new Operation(this, (Element) op_nodes.item(i)));
        }
    }


    public void moveToFirstOperation() {
        cur_operation = operations.get(0);
        runCurrentOperation();
    }

    public void runCurrentOperation(){
        cur_operation.run();
        procedure.handleEndOfOperation();
        if (cur_operation.equals(operations.get(operations.size() - 1)))
            procedure.handleEndOfStep();
    }

    public Procedure getProcedure() {
        return procedure;
    }

    public Operation getCurrentOperation() {
        return cur_operation;
    }

    public void moveToNextOperation() {
        if (!cur_operation.equals(operations.get(operations.size() - 1)))
        {
            cur_operation = operations.get(operations.lastIndexOf(cur_operation) + 1);
            runCurrentOperation();
        }
    }
}
