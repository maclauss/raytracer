package com.surenot.raytracer.primitives;

import com.surenot.raytracer.shapes.Shape3D;

/**
 * Created by m.clauss on 1/13/2016.
 */
public class RayImpact {

    public static final RayImpact NONE = new RayImpact(null, null, Double.NaN);

    private final Point3D impact;
    private final Shape3D object;
    private final double distance;

    public RayImpact(final Point3D impact, final Shape3D object, final double distance){
        if ( impact == null && NONE != null ){
            throw new IllegalArgumentException();
        }
        this.impact = impact;
        this.object = object;
        this.distance = distance;
    }

    public Point3D getImpact() {
        return impact;
    }

    public Shape3D getObject() {
        return object;
    }

    public double getDistance() {
        return distance;
    }
}
