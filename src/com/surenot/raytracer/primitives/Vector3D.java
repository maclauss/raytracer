package com.surenot.raytracer.primitives;

/**
 * Created by m.clauss on 1/13/2016.
 */
public class Vector3D {

    private final double accuracy = 0.000001;

    private final Point3D origin;
    private final Point3D direction;
    // Not final because its value can change from NaN to the real length to avoid costly computations,
    // however this doesn't impact the immutability of the class
    private double length;

    public Vector3D(final Point3D origin, final Point3D direction){
        this.origin = origin;
        this.direction = direction;
        // TODO try to replace with Double.NaN
        this.length = Double.MAX_VALUE;
    }

    public Vector3D multiply(double x){
        return new Vector3D(origin, new Point3D(direction.getX() * x, direction.getY() * x, direction.getZ() * x));
    }

    public Point3D getOrigin() {
        return origin;
    }

    public Point3D getDirection() {
        return direction;
    }

    public double getLength() {
        return length == Double.MAX_VALUE ?
                (length = Math.sqrt(
                    Math.pow(direction.getX() - origin.getX(), 2) +
                    Math.pow(direction.getY() - origin.getY(), 2) +
                    Math.pow(direction.getZ() - origin.getZ(), 2))) :
                length;
    }

    public boolean isNormalized(){
        return origin.equals(Point3D.ORIGIN) && length < 1 + accuracy && length > 1 - accuracy;
    }

    public static Vector3D getNormalizedVector3D(Point3D origin, Point3D direction){
        double x = direction.getX() - origin.getX();
        double y = direction.getY() - origin.getY();
        double z = direction.getZ() - origin.getZ();
        double length = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
        if ( length == 1 ) return new Vector3D(Point3D.ORIGIN, new Point3D(x, y, z));
        else return new Vector3D(Point3D.ORIGIN, new Point3D(x / length, y / length, z / length));
    }

    public static Vector3D getNormalizedVector3D(Vector3D vector){
        return getNormalizedVector3D(vector.getOrigin(), vector.getDirection());
    }
}
