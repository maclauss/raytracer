package com.surenot.raytracer.primitives;

/**
 * Created by m.clauss on 1/13/2016.
 */
public class RayImpact {

    public static final RayImpact NONE = new RayImpact(null, Double.NaN);

    private final Point3D impact;
    private final double distance;

    public RayImpact(final Point3D impact, final double distance){
        this.impact = impact;
        this.distance = distance;
    }

    public Point3D getImpact() {
        return impact;
    }

    public double getDistance() {
        return distance;
    }
}
