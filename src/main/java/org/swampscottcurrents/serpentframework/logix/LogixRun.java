package org.swampscottcurrents.serpentframework.logix;

public class LogixRun extends LogixNode
{
    private Runnable action;

    public LogixRun(LogixPath parent, Runnable toRun) {
        super(parent);
        action = toRun;
    }

    @Override
    public boolean execute() {
        action.run();
        return true;
    }
}