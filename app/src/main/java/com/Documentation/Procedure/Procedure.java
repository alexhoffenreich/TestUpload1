package com.Documentation.Procedure;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alex-lenovi on 5/24/2016.
 */
public class Procedure {
    private String id;
    private String title;
    private List<Step> steps;
    private Step cur_step;
    private ProcedureEventHandler procedure_event;
    private Element procedure_element;


    public Procedure(Element procedure_element) {
        this.procedure_element = procedure_element;
        steps = new ArrayList<>();
        id = procedure_element.getAttribute("id");
        title = procedure_element.getElementsByTagName("title").item(0).getTextContent();
        NodeList steps_node_list = procedure_element.getElementsByTagName("steps");
        for (int i = 0; i < steps_node_list.getLength(); i++) {
            steps.add(new Step((Element)steps_node_list.item(i),this));
        }

    }


    public boolean moveToNextStep (){
        if (!isLastStep()){
            cur_step = steps.get(steps.lastIndexOf(cur_step)+1);
            cur_step.moveToFirstOperation();
            return true;
        } else
        {
            return false;
        }
    }

    public boolean moveToPreviousStep (){
        if (!isFirstStep()){
            cur_step = steps.get(steps.lastIndexOf(cur_step)-1);
            cur_step.moveToFirstOperation();
            return true;
        } else
        {
            return false;
        }

    }

    public void moveToStep(String step_id){

    }
    public void moveToFirst(){

    }
    public void moveToLast(){

    }
    public boolean isFirstStep(){
        return cur_step.equals(steps.get(steps.size()-1));
    }
    public boolean isLastStep(){
        return cur_step.equals(steps.get(0));
    }


    public void handleEndOfStep() {
        if (procedure_event!= null){
            procedure_event.onEndOfStep(cur_step, this);
            if (isLastStep()) procedure_event.onEndOfProcedure(this);
        }
    }

    public void handleEndOfOperation() {
        procedure_event.onEndOfOperation(cur_step.getCurrentOperation(), cur_step, this);
    }
}
