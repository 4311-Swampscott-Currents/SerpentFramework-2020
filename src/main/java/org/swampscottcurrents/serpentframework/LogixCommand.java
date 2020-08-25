package org.swampscottcurrents.serpentframework;

import org.swampscottcurrents.serpentframework.logix.*;

/** Represents a command which uses a Logix controller in order to coordinate asynchronous behavior. To create custom LogixCommands, override the class and then use getLogixController() in the command constructor. */
public class LogixCommand extends SerpentCommand {

    private LogixPath commandPath = new LogixPath(null);
    private boolean isComplete = false;

    @Override
    public void initialize() {
        commandPath.start();
        isComplete = false;
    }

    @Override
    public void execute() {
        if(!isComplete) {
            isComplete = commandPath.execute();
        }
    }

    /** Retrieves the Logix controller for this command, allowing for the specification of custom behavior. */
    protected LogixPath getLogixController() {
        return commandPath;
    }

    @Override
    public final boolean isFinished() {
        return isComplete;
    }
}