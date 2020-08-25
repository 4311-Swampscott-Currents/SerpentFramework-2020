package org.swampscottcurrents.serpentframework;

/** Represents a geometric angle using the angle's linear components. Quaternions are useful because they can be easily added/subtracted/interpolated without worrying about angles wrapping around from 0 to 359 degrees. Quaternions cannot store information about number of rotations - they can only describe rotation angle. Quaternions are immutable. */
public class Quaternion2D {

    /** The x-coordinate of the position on the unit circle to which this quaternion's angle points. */
    public final double x;
    /** The y-coordinate of the position on the unit circle to which this quaternion's angle points. */
    public final double y;

    /** Creates a new Quaternion2D instance with an angle of 0. */
    public Quaternion2D() {
        x = 1;
        y = 0;
    }

    private Quaternion2D(double xPos, double yPos) {
        x = xPos;
        y = yPos;
    }

    /** Returns the number of radians, between -pi and pi, represented by this quaternion. */
    public double toRadians() {
        return Math.atan2(y, x);
    }

    /** Returns the number of degrees, between -180 and 180, represented by this quaternion. */
    public double toDegrees() {
        return Math.toDegrees(toRadians());
    }

    /** Inverts this quaternion, so that it is pointing in the other direction. */
    public Quaternion2D invert() {
        return new Quaternion2D(x, -y);
    }

    /** Creates a new quaternion using the angle between the x-axis and the specified axis. */
    public static Quaternion2D fromAxis(double x, double y) {
        double distance = Math.sqrt(x * x + y * y);
        x = x / distance;
        y = y / distance;
        return new Quaternion2D(x, y);
    }

    /** Adds two quaternions, resulting in a new quaternion that is a combination of both their angles. This is the same as multiplying them. */
    public static Quaternion2D add(Quaternion2D a, Quaternion2D b) {
        return multiply(a, b);
    }

    /** Subtracts two quaternions. This is the same as multiplying one quaternion by the other's inverse. */
    public static Quaternion2D subtract(Quaternion2D a, Quaternion2D b) {
        return new Quaternion2D((a.x * b.x) + (a.y * b.y), (a.y * b.x) - (a.x * b.y));
    }

    /** Multiplies two quaternions, resulting in a new quaternion whose angle is the sum of the two input quaternions' angles. */
    public static Quaternion2D multiply(Quaternion2D a, Quaternion2D b) {
        return new Quaternion2D((a.x * b.x) - (a.y * b.y), (a.y * b.x) + (a.x * b.y));
    }

    /** Creates a new quaternion that corresponds to the specified angle, in radians. */
    public static Quaternion2D fromRadians(double angle) {
        return new Quaternion2D(Math.cos(angle), Math.sin(angle));
    }

    /** Creates a new quaternion that corresponds to the specified angle, in degrees. */
    public static Quaternion2D fromDegrees(double angle) {
        return fromRadians(Math.toRadians(angle));
    }

    /** Spherically interpolates between two quaternions. The result is a quaternion whose angle is somewhere between the angle of a and b. As t increases along the interval [0,1], the resultant angle linearly moves from angle a to b. */
    public static Quaternion2D slerp(Quaternion2D a, Quaternion2D b, double t) {
        t = Math.min(1, Math.max(t, 0));
        double cosDot = Math.acos(dot(a, b));
        double aCoeff = Math.sin((1 - t) * cosDot) / Math.sin(cosDot);
        double bCoeff = Math.sin(t * cosDot) / Math.sin(cosDot);
        return new Quaternion2D((aCoeff * a.x) + (bCoeff * b.x), (aCoeff * a.y) + (bCoeff * b.y));
    }

    /** Computes the dot product of two quaternions. The result is the cosine of the angle between the two quaternions - thus, it is positive if the quaternions are facing the same direction, and negative otherwise. */
    public static double dot(Quaternion2D a, Quaternion2D b) {
        return (a.x * b.x) + (a.y * b.y);
    }
}