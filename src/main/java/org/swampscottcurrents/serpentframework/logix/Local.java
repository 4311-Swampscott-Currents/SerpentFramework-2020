package org.swampscottcurrents.serpentframework.logix;

/** Used to "close over" local variables in lambda expressions, allowing for access to objects by reference as opposed to final value. */
public class Local<T> {
    /** The value of the local variable. */
    public T value;

    /** Creates a new local variable instance initialized to null. */
    public Local() {}

    /** Creates a new local variable instance initialized to the specified value. */
    public Local(T initialValue) {
        value = initialValue;
    }
}