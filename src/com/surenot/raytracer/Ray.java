package com.surenot.raytracer;

public final class Ray {
    private final Point3D origin;
    private final Point3D vector;

    public Ray(Point3D origin, Point3D vector){
        this.origin = origin;
        double x = vector.getX() - origin.getX();
        double y = vector.getY() - origin.getY();
        double z = vector.getZ() - origin.getZ();
        double length = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
        if ( length == 1 ) this.vector = vector;
        else this.vector = new Point3D(x / length, y / length, z / length);
    }

    public Point3D getOrigin() { return origin; }

    public Point3D getVector() {
        return vector;
    }
}
