package org.swampscottcurrents.serpentframework.logix;

import java.util.function.BooleanSupplier;

public class LogixLoop extends LogixConditional
{
    public LogixLoop(LogixPath parent, BooleanSupplier condition) {
        super(parent, condition);
    }

    public LogixLoop orSecondsArePassing(double time) {
        BooleanSupplier originalCondition = condition;
        condition = () -> originalCondition.getAsBoolean() || (DefaultTimeProvider.getAsDouble() - startTime < time);
        return this;
    }

    public LogixLoop andSecondsArePassing(double time) {
        BooleanSupplier originalCondition = condition;
        condition = () -> originalCondition.getAsBoolean() && (DefaultTimeProvider.getAsDouble() - startTime < time);
        return this;
    }
}