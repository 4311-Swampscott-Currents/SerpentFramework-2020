package org.swampscottcurrents.serpentframework.logix;

import java.util.function.*;

public class LogixConditional extends LogixPath {

    public static DoubleSupplier DefaultTimeProvider;
    protected BooleanSupplier condition;
    protected double startTime;

    public LogixConditional(LogixPath parent, BooleanSupplier initialCondition) {
        super(parent);
        condition = initialCondition;
    }

    @Override
    public void start() {
        startTime = DefaultTimeProvider.getAsDouble();
        super.start();
    }

    protected void resetWithoutChangingStartTime() {
        super.start();
    }

    protected boolean IsTrue() {
        return condition.getAsBoolean();
    }

    public LogixConditional or(BooleanSupplier orCondition) {
        BooleanSupplier originalCondition = condition;
        condition = () -> originalCondition.getAsBoolean() || orCondition.getAsBoolean();
        return this;
    }

    public LogixConditional not() {
        BooleanSupplier originalCondition = condition;
        condition = () -> !originalCondition.getAsBoolean();
        return this;
    }

    public LogixConditional and(BooleanSupplier andCondition) {
        BooleanSupplier originalCondition = condition;
        condition = () -> originalCondition.getAsBoolean() && andCondition.getAsBoolean();
        return this;
    }
}