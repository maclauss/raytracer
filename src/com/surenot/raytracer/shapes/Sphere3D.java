package com.surenot.raytracer.shapes;

import com.surenot.raytracer.primitives.Impact3D;
import com.surenot.raytracer.primitives.Point3D;
import com.surenot.raytracer.primitives.Surface;
import com.surenot.raytracer.primitives.Vector3D;

import java.awt.*;

public final class Sphere3D implements Shape3D {

    private final Point3D center;
    private final double radius;
    private final double r2;
    private final Surface surface;

    public Sphere3D(final Point3D center, final double radius) {
        this(center, radius, new Surface(Color.LIGHT_GRAY.getRGB(), 1, 1, 1, 50));
    }

    public Sphere3D(final Point3D center, final double radius, Surface surface) {
        if (center == null) {
            throw new IllegalArgumentException();
        }
        this.center = center;
        this.radius = radius;
        this.r2 = Math.pow(radius, 2);
        this.surface = surface;
    }

    @Override
    public Impact3D isHit(final Vector3D v) {
        // Origin - center as we use the normalized v vector
        Point3D no = v.getOrigin().substract(center);
        // TODO a = 1 if the vector is normalized, avoid this computation
        double a = 1;
        double b = 2 * v.normalize().scalarProduct(no);
        double c = no.scalarProduct(no) - r2;

        double d = Math.pow(b, 2) - 4 * a * c;

        if (d < 0) return Impact3D.NONE;

        // TODO interesting to check if the VM optimise this (Wild guess: yes)
        double t0 = (-b + Math.sqrt(d)) / 2 * a;
        double t1 = (-b - Math.sqrt(d)) / 2 * a;

        double impactDistance;
        if (t0 < 0) {
            impactDistance = t1 < 0 ? Double.NaN : t1;
        } else {
            impactDistance = t1 < 0 ? t0 : Math.min(t0, t1);
        }
        if ( Double.isNaN(impactDistance)) return Impact3D.NONE;
        Vector3D impactPoint = v.normalize().multiply(impactDistance);
        return new Impact3D(v, impactPoint.getOrigin().add(impactPoint.getDirection()), this, impactDistance);
    }

    @Override
    public Vector3D getNormal(final Point3D p) {
        return new Vector3D(center, p.substract(center), false);
    }

    @Override
    public Point3D getCenter() {
        return center;
    }

    @Override
    public Surface getSurface(){
        return surface;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Sphere3D sphere3D = (Sphere3D) o;

        if (Double.compare(sphere3D.radius, radius) != 0) return false;
        if (Double.compare(sphere3D.r2, r2) != 0) return false;
        if (center != null ? !center.equals(sphere3D.center) : sphere3D.center != null) return false;
        return !(surface != null ? !surface.equals(sphere3D.surface) : sphere3D.surface != null);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = center != null ? center.hashCode() : 0;
        temp = Double.doubleToLongBits(radius);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(r2);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (surface != null ? surface.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Sphere3D{" +
                "center=" + center +
                ", radius=" + radius +
                ", surface=" + surface +
                '}';
    }
}
