package org.swampscottcurrents.serpentframework.logix;

import java.util.function.BooleanSupplier;

public class LogixIfTrue extends LogixConditional {

    protected LogixPath otherwisePath = null;
    protected LogixIfTrueState executionState;

    public LogixIfTrue(LogixPath parent, BooleanSupplier condition) {
        super(parent, condition);
    }

    @Override
    public void start() {
        executionState = LogixIfTrueState.NOT_EXECUTING;
        super.start();
    }

    @Override
    public boolean execute()
    {
        if(executionState == LogixIfTrueState.EXECUTING) {
            return super.execute();
        }
        else {
            if(IsTrue()) {
                executionState = LogixIfTrueState.EXECUTING;
                return super.execute();
            }
            else {
                if(otherwisePath == null) {
                    return true;
                }
                else {
                    executionState = LogixIfTrueState.EXECUTING_OTHERWISE;
                    otherwisePath.start();
                    return otherwisePath.execute();
                }
            }
        }
    }

    protected enum LogixIfTrueState {
        NOT_EXECUTING,
        EXECUTING,
        EXECUTING_OTHERWISE
    }
}