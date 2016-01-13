package com.surenot.raytracer.primitives;

public final class Ray {
    private final Vector3D vector;

    public Ray(Vector3D vector){
        double x = vector.getDirection().getX() - vector.getOrigin().getX();
        double y = vector.getDirection().getY() - vector.getOrigin().getY();
        double z = vector.getDirection().getZ() - vector.getOrigin().getZ();
        double length = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
        if ( length == 1 ) this.vector = vector;
        else this.vector = new Vector3D(Point3D.ORIGIN, new Point3D(x / length, y / length, z / length));
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
