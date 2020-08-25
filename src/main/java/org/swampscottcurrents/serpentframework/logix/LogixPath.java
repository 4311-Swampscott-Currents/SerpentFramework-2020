package org.swampscottcurrents.serpentframework.logix;

import java.util.*;
import java.util.function.*;

/** Represents a chain of code which can be executed asynchronously. Using decorator methods, one can easily compose a LogixPath for any situation that can be executed/paused at will. */
public class LogixPath extends LogixNode{
    protected ArrayList<LogixNode> children = new ArrayList<LogixNode>();
    private int CurrentPosition = 0;

    /** Creates a new LogixPath instance. */
    public LogixPath() {
        super(null);
    }

    /** Creates a new LogixPath instance with the specified parent path. */
    public LogixPath(LogixPath parent) {
        super(parent);
    }

    @Override
    public void start() {
        CurrentPosition = 0;
        if(children.size() > 0) {
            children.get(CurrentPosition).start();
        }
    }

    @Override
    public boolean execute()
    {
        if(children.size() == 0) {
            return true;
        }
        while(children.get(CurrentPosition).execute()) {
            CurrentPosition++;
            if(CurrentPosition >= children.size()) {
                return true;
            }
            else {
                children.get(CurrentPosition).start();
            }
        }
        return false;
    }

    /** Runs the specified function synchronously. */
    public LogixPath run(Runnable action) {
        LogixRun runner = new LogixRun(this, action);
        children.add(runner);
        return this;
    }

    /** Returns control flow to the parent path. */
    public LogixPath then() {
        return parent;
    }

    /** Acts as an asynchronous while loop. During checks the specified condition and executes the following sub-block of code if it is true. After execution, during suspends its execution, waiting for the next iteration to check its condition again. */
    public LogixLoop during(BooleanSupplier condition)
    {
        return during(condition, LoopExecution.SYNCHRONOUS);
    }

    /** Acts as an asynchronous while loop. During checks the specified condition and executes the following sub-block of code if it is true. After execution, during suspends its execution, waiting for the next iteration to check its condition again. The settings parameter may be used to specify whether the during should check its condition and exit after a sub-block suspends, or only after a sub-block completes. */
    public LogixLoop during(BooleanSupplier condition, LoopExecution settings) {
        LogixDuring toReturn = new LogixDuring(this, condition, settings);
        children.add(toReturn);
        return toReturn;
    }

    /** Suspends execution of the current sub-block for the specified number of seconds. */
    public LogixPath pause(double seconds) {
        during(seconds);
        return this;
    }

    /** Acts as an asynchronous while loop. During executes the following sub-block of code repeatedly for the specified number of seconds. After execution, during suspends its execution, waiting for the next iteration to check its condition again. */
    public LogixLoop during(double seconds)
    {
        return during(seconds, LoopExecution.SYNCHRONOUS);
    }

    /** Acts as an asynchronous while loop. During executes the following sub-block of code repeatedly for the specified number of seconds. After execution, during suspends its execution, waiting for the next iteration to check its condition again. The settings parameter may be used to specify whether the during should check its condition and exit after a sub-block suspends, or only after a sub-block completes. */
    public LogixLoop during(double seconds, LoopExecution settings) {
        return during(() -> true, settings).andSecondsArePassing(seconds);
    }

    /** Runs the following sub-block of code synchronously if the given condition is true. */
    public LogixIfTrue ifTrue(BooleanSupplier condition) {
        LogixIfTrue toReturn = new LogixIfTrue(this, condition);
        children.add(toReturn);
        return toReturn;
    }

    /** Asynchronously waits until the given condition is true, then executes the following sub-block of code. */
    public LogixWhen when(BooleanSupplier condition) {
        return when(condition, LoopExecution.SYNCHRONOUS, WhenExecution.DEFAULT);
    }

    /** Asynchronously waits until the given condition is true, then executes the following sub-block of code. The settings parameter may be used to specify whether the when should check its condition and exit after the following sub-block suspends, or only after it completes. */
    public LogixWhen when(BooleanSupplier condition, LoopExecution settings)
    {
        return when(condition, settings, WhenExecution.DEFAULT);
    }

    /** Asynchronously waits until the given condition is true, then executes the following sub-block of code. The settings parameter may be used to specify what the when should do when the following sub-block completes. */
    public LogixWhen when(BooleanSupplier condition, WhenExecution settings) {
        return when(condition, LoopExecution.SYNCHRONOUS, settings);
    }

    /** Runs the following sub-block of code synchronously if the previous ifTrue block does not execute. */
    public LogixPath otherwise() {
        LogixIfTrue statement = (LogixIfTrue)this;
        return statement.otherwisePath = new LogixPath(parent);
    }

    /** Executes each node of the following code sub-block at the same time. */
    public LogixPath simultaneously() {
        return simultaneously(SimultaneousExecution.ONCE);
    }

    /** Executes each node of the following code sub-block at the same time. The settings parameter may be used to specify whether the nodes should be executed again once they finish execution. */
    public LogixPath simultaneously(SimultaneousExecution settings) {
        SimultaneousPath toReturn = new SimultaneousPath(this, settings);
        children.add(toReturn);
        return toReturn;
    }

    /** Asynchronously waits until the given condition is true, then executes the following sub-block of code. The settings parameters may be used to specify what the when should do when the following sub-block completes, or whether to check its condition again if the sub-block suspends. */
    public LogixWhen when(BooleanSupplier condition, LoopExecution loop, WhenExecution settings) {
        LogixWhen toReturn = new LogixWhen(this, condition, loop, settings);
        children.add(toReturn);
        return toReturn;
    }
}