package com.surenot.raytracer.shapes;

import com.surenot.raytracer.primitives.Impact3D;
import com.surenot.raytracer.primitives.Point3D;
import com.surenot.raytracer.primitives.Vector3D;

public final class Light3D implements Shape3D {

    private final Shape3D shape;

    public Light3D(Shape3D shape) {
        if (shape == null) {
            throw new IllegalArgumentException();
        }
        this.shape = shape;
    }

    @Override
    public Impact3D isHit(final Vector3D v) {
        Impact3D impact = shape.isHit(v);
        return impact.equals(Impact3D.NONE) ?
                Impact3D.NONE :
                new Impact3D(v, impact.getPoint(), this, impact.getDistance());
    }

    @Override
    public Vector3D getNormal(final Point3D p) {
        return shape.getNormal(p);
    }

    @Override
    public Point3D getCenter() {
        return shape.getCenter();
    }

    @Override
    public int getColor() {
        return shape.getColor();
    }

    @Override
    public double getAmbiantReflectionCoefficient() {
        return 1;
    }

    @Override
    public double getDiffuseReflectionCoefficient() {
        return 1;
    }

    @Override
    public double getSpecularReflectionCoefficient() {
        return 1;
    }

    @Override
    public double getSpecularReflectionExponent() {
        return 10;
    }

    public double getConstantAttenuationCoefficient(){
        return 0;
    }

    public double getLinearAttenuationCoefficient(){
        return 0.1;
    }

    public double getQuadraticAttenuationCoefficient(){
        return 0.00005;
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
