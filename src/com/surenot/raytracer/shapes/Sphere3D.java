package com.surenot.raytracer.shapes;

import com.surenot.raytracer.primitives.Point3D;
import com.surenot.raytracer.primitives.Ray;
import com.surenot.raytracer.primitives.RayImpact;
import com.surenot.raytracer.primitives.Vector3D;

import java.awt.*;
import java.security.InvalidParameterException;

public final class Sphere3D implements Shape3D {

    private final Point3D center;
    private final double radius;
    private final int color;

    public Sphere3D(final Point3D center, final double radius, final int color){
        if ( center == null ){
            throw new InvalidParameterException();
        }
        this.center = center;
        this.radius = radius;
        this.color = color;
    }

    @Override
    public RayImpact isHit(Ray ray){
        Point3D no = ray.getVector().getOrigin().substract(center);
        // TODO a = 1 if the vector is normalized, avoid this computation
        double a = 1; //ray.getVector().scalarProduct(ray.getVector());
        double b = 2 * ray.getVector().getDirection().scalarProduct(no);
        double c = no.scalarProduct(no) - Math.pow(radius, 2);

        double d = Math.pow(b, 2) - 4 * a * c;

        if ( d < 0 ) return RayImpact.NONE;

        // TODO interesting to check if the VM optimise this (Wild guess: yes)
        double t0 = (-b + Math.sqrt(d)) / 2 * a;
        double t1 = (-b - Math.sqrt(d)) / 2 * a;

        double impactDistance;
        if ( t0 < 0 ) {
            impactDistance = t1 < 0 ? Double.NaN : t1;
        }
        impactDistance = t1 < 0 ? t0 : Math.min(t0, t1);
        return new RayImpact(ray.getVector().multiply(impactDistance).getDirection(), impactDistance);
    }

    @Override
    public Vector3D getNormal(Point3D p) {
        return Vector3D.getNormalizedVector3D(center, p);
    }

    @Override
    public int getColor() {
        return color;
    }

    @Override
    public String toString() {
        return "Sphere3D{" +
                "center=" + center +
                ", radius=" + radius +
                '}';
    }
}
