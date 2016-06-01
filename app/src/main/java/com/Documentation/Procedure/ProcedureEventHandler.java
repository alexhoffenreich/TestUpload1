package com.Documentation.Procedure;

/**
 * Created by alex-lenovi on 5/25/2016.
 */
public class ProcedureEventHandler {
    public void onEndOfStep(Step step, Procedure procedure){
        procedure.moveToNextStep();
    }
    public void onEndOfProcedure(Procedure procedure){

    }
    public void onEndOfOperation (Operation operation, Step step, Procedure procedure){
        step.moveToNextOperation();
    }
}
