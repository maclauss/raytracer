package com.surenot.raytracer.primitives;

import com.surenot.raytracer.shapes.Shape3D;

/**
 * Created by m.clauss on 1/13/2016.
 */
public class RayImpact {

    public static final RayImpact NONE = new RayImpact(null, null, null, Double.POSITIVE_INFINITY);

    private final Vector3D vector;
    private final Point3D impact;
    private final Shape3D object;
    private final double distance;
    private double squareDistance = Double.MAX_VALUE;

    public RayImpact(final Vector3D vector, final Point3D impact, final Shape3D object, final double distance){
        if ( (vector == null || impact == null || object == null) && NONE != null ){
            throw new IllegalArgumentException();
        }
        if ( impact != null && impact.getX() == Double.NaN ){
            System.out.println("wtf");
        }
        this.vector = vector;
        this.impact = impact;
        this.object = object;
        this.distance = distance;
    }

    public Point3D getImpact() {
        return impact;
    }

    public Shape3D getImpactedObject() {
        return object;
    }

    public double getDistance() {
        return distance;
    }

    public double getSquareDistance() {
        return squareDistance == Double.MAX_VALUE ?
                squareDistance = Math.pow(distance, 2) :
                squareDistance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RayImpact rayImpact = (RayImpact) o;

        if (Double.compare(rayImpact.distance, distance) != 0) return false;
        if (impact != null ? !impact.equals(rayImpact.impact) : rayImpact.impact != null) return false;
        return object != null ? object.equals(rayImpact.object) : rayImpact.object == null;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = impact != null ? impact.hashCode() : 0;
        result = 31 * result + (object != null ? object.hashCode() : 0);
        temp = Double.doubleToLongBits(distance);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "RayImpact{" +
                "vector=" + vector +
                ", impact=" + impact +
                ", object=" + object +
                ", distance=" + distance +
                '}';
    }
}
