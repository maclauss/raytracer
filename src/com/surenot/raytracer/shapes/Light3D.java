package com.surenot.raytracer.shapes;

import com.surenot.raytracer.primitives.Point3D;
import com.surenot.raytracer.primitives.Ray;
import com.surenot.raytracer.primitives.RayImpact;
import com.surenot.raytracer.primitives.Vector3D;

public final class Light3D implements Shape3D {

    private final Shape3D shape;

    public Light3D(Shape3D shape){
        if ( shape == null ){
            throw new IllegalArgumentException();
        }
        this.shape = shape;
    }

    @Override
    public RayImpact isHit(final Ray ray){
        RayImpact impact = shape.isHit(ray);
        return impact.equals(RayImpact.NONE) ?
                RayImpact.NONE :
                new RayImpact(impact.getImpact(), this, impact.getDistance());
    }

    @Override
    public Vector3D getNormal(final Point3D p) {
        return shape.getNormal(p);
    }

    @Override
    public int getColor() {
        return shape.getColor();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Light3D light3D = (Light3D) o;

        return shape != null ? shape.equals(light3D.shape) : light3D.shape == null;

    }

    @Override
    public int hashCode() {
        return shape != null ? shape.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Light3D{" +
                "shape=" + shape +
                '}';
    }
}
