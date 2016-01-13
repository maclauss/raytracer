package com.surenot.raytracer.shapes;

import com.surenot.raytracer.primitives.Point3D;
import com.surenot.raytracer.primitives.Ray;

import java.awt.*;

public final class Sphere3D implements Shape3D {

    private final Point3D center;
    private final double radius;

    public Sphere3D(final Point3D center, final double radius){
        this.center = center;
        this.radius = radius;
    }

    @Override
    public boolean isHit(Ray ray){
        Point3D no = ray.getOrigin().substract(center);
        // TODO a = 1 if the vector is normalized, avoid this computation
        double a = 1; //ray.getVector().scalarProduct(ray.getVector());
        double b = 2 * ray.getVector().scalarProduct(no);
        double c = no.scalarProduct(no) - Math.pow(radius, 2);

        double d = Math.pow(b, 2) - 4 * a * c;

        return d >= 0;
    }

    @Override
    public int getColor() {
        return Color.RED.getRGB();
    }

    @Override
    public String toString() {
        return "Sphere3D{" +
                "center=" + center +
                ", radius=" + radius +
                '}';
    }
}
