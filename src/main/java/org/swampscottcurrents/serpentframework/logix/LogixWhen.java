package org.swampscottcurrents.serpentframework.logix;

import java.util.function.*;

public class LogixWhen extends LogixLoop {

    protected WhenState executionState;
    protected LoopExecution loopSettings;
    protected WhenExecution whenSettings;

    public LogixWhen(LogixPath parent, BooleanSupplier condition, LoopExecution loopOptions, WhenExecution whenOptions) {
        super(parent, condition);
        loopSettings = loopOptions;
        whenSettings = whenOptions;
    }

    public LogixWhen(LogixPath parent, double time) {
        super(parent, () -> true);
        andSecondsArePassing(time);
    }

    @Override
    public void start() {
        super.start();
        executionState = WhenState.WAITING_FOR_TRUE;
    }

    @Override
    public boolean execute() {
        if(executionState == WhenState.WAITING_FOR_TRUE) {
            if(IsTrue()) {
                executionState = WhenState.EXECUTING;
                return execute();
            }
            return false;
        }
        else if(executionState == WhenState.EXECUTING) {
            if(loopSettings == LoopExecution.INTERMEDIATE_TERMINATION) {
                if(!IsTrue()) {
                    return true;
                }
            }
            if(super.execute()) {
                if(whenSettings == WhenExecution.WAIT_FOR_FALSE) {
                    executionState = WhenState.WAITING_FOR_FALSE;
                    return false;
                }
                else if(whenSettings == WhenExecution.REPEAT) {
                    start();
                    return false;
                }
                else {
                    return true;
                }
            }
            return false;
        }
        else if(executionState == WhenState.WAITING_FOR_FALSE) {
            if(!IsTrue()) {
                return true;
            }
            else {
                return false;
            }
        }
        throw new IllegalStateException();
    }

    protected enum WhenState {
        WAITING_FOR_TRUE,
        EXECUTING,
        WAITING_FOR_FALSE
    }
}