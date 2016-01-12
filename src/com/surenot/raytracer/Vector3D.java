package com.surenot.raytracer;

public final class Vector3D {
    private final Point3D direction;

    public Vector3D(final Point3D direction){
        this(new Point3D(0 ,0 ,0), direction);
    }

    public Vector3D(final Point3D origin, final Point3D destination){
        double x = destination.getX() - origin.getX();
        double y = destination.getY() - origin.getY();
        double z = destination.getZ() - origin.getZ();
        double length = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
        // TODO add a test, if length > 0.999999 and length < 1.000001, consider that it's normalized (check values)
        this.direction = new Point3D(x / length, y / length, z / length);
    }

    public Point3D getDirection() {
        return direction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vector3D vector3D = (Vector3D) o;

        return direction != null ? direction.equals(vector3D.direction) : vector3D.direction == null;
    }

    @Override
    public int hashCode() {
        return direction != null ? direction.hashCode() : 0;
    }
}
