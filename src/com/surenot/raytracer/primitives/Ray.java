package com.surenot.raytracer.primitives;

public final class Ray {

    private final Vector3D vector;

    public Ray(Vector3D vector){
        if ( vector == null ){
            throw new IllegalArgumentException();
        }
        this.vector = Vector3D.getNormalizedVector3D(vector);
    }

    public Vector3D getVector() {
        return vector;
    }

    @Override
    public String toString() {
        return "Ray{" +
                "vector=" + vector +
                '}';
    }
}
