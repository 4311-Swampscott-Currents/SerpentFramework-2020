package org.swampscottcurrents.serpentframework.logix;

public class LogixNode {

    protected LogixPath parent;

    public LogixNode(LogixPath parentNode) {
        parent = parentNode;
    }

    public void start() {}

    public boolean execute() {
        return true;
    }
}