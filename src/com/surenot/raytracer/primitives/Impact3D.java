package com.surenot.raytracer.primitives;

import com.surenot.raytracer.shapes.Shape3D;

/**
 * Created by m.clauss on 1/13/2016.
 */
public class Impact3D {

    public static final Impact3D NONE = new Impact3D(null, null, null, Double.POSITIVE_INFINITY);
    public static final Impact3D INFINITY = new Impact3D(null, null, null, 0.0);

    private final Vector3D vector;
    private final Point3D impact;
    private final Shape3D object;
    private final double distance;
    private double squareDistance = Double.NaN;

    public Impact3D(final Vector3D vector, final Point3D impact, final Shape3D object, final double distance) {
        if ((vector == null || impact == null || object == null) && NONE != null && INFINITY != null)
            throw new IllegalArgumentException();
        if (impact != null && Double.isNaN(impact.getX())) {
            System.out.println("wtf");
        }
        this.vector = vector;
        this.impact = impact;
        this.object = object;
        this.distance = distance;
    }

    public Point3D getPoint() {
        return impact;
    }

    public Shape3D getImpactedObject() {
        return object;
    }

    public double getDistance() {
        return distance;
    }

    public double getSquareDistance() {
        return Double.isNaN(squareDistance) ?
                squareDistance = Math.pow(distance, 2) :
                squareDistance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Impact3D impact = (Impact3D) o;

        if (Double.compare(impact.distance, distance) != 0) return false;
        if (this.impact != null ? !this.impact.equals(impact.impact) : impact.impact != null) return false;
        return object != null ? object.equals(impact.object) : impact.object == null;

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
        return "Impact3D{" +
                "vector=" + vector +
                ", impact=" + impact +
                ", object=" + object +
                ", distance=" + distance +
                '}';
    }
}
