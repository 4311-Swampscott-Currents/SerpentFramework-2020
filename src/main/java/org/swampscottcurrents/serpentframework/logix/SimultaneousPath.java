package org.swampscottcurrents.serpentframework.logix;

public class SimultaneousPath extends LogixPath {

    protected SimultaneousExecution executionSettings;
    protected boolean[] pathsComplete;

    public SimultaneousPath(LogixPath parent, SimultaneousExecution settings) {
        super(parent);
    }

    @Override
    public void start() {
        super.start();
        pathsComplete = new boolean[children.size()];
        for(LogixNode child : children) {
            child.start();
        }
    }

    @Override
    public boolean execute() {
        if(executionSettings == SimultaneousExecution.ONCE) {
            boolean incompletePaths = false;
            for(int x = 0; x < children.size(); x++) {
                if(!pathsComplete[x]) {
                    pathsComplete[x] = children.get(x).execute();
                    incompletePaths = true;
                }
            }
            return !incompletePaths;
        }
        else {
            for(LogixNode child : children) {
                child.execute();
            }
            return false;
        }
    }
}