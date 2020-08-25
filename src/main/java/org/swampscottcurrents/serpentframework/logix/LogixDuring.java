package org.swampscottcurrents.serpentframework.logix;

import java.util.function.BooleanSupplier;

public class LogixDuring extends LogixLoop
{
    protected LoopExecution loopSettings;
    protected boolean isExecuting = false;

    public LogixDuring(LogixPath parent, BooleanSupplier condition, LoopExecution settings) {
        super(parent, condition);
    }

    @Override
    public void start() {
        super.start();
        isExecuting = false;
    }

    @Override
    public boolean execute()
    {
        if(isExecuting) {
            isExecuting = !super.execute();
            if(!isExecuting) {
                resetWithoutChangingStartTime();
            }
            return false;
        }
        else {
            if(loopSettings == LoopExecution.INTERMEDIATE_TERMINATION) {
                if(IsTrue()) {
                    super.execute();
                    return false;
                }
                else {
                    return true;
                }
            }
            else {
                if(IsTrue()) {
                    isExecuting = !super.execute();
                    if(!isExecuting) {
                        resetWithoutChangingStartTime();
                    }
                    return false;
                }
                else {
                    return true;
                }
            }
        }        
    }
}