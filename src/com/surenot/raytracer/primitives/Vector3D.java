package com.surenot.raytracer.primitives;

/**
 * Created by m.clauss on 1/13/2016.
 */
public class Vector3D {

    private final Point3D origin;
    private final Point3D direction;

    public Vector3D(final Point3D origin, final Point3D direction, boolean normalize) {
        if (origin == null || direction == null) {
            throw new IllegalArgumentException();
        }
        this.origin = origin;
        if ( normalize ) {
            double length = Math.sqrt(Math.pow(direction.getX(), 2) +
                    Math.pow(direction.getY(), 2) +
                    Math.pow(direction.getZ(), 2));
            if (length == 1) this.direction = new Point3D(direction.getX(), direction.getY(), direction.getZ());
            else this.direction = new Point3D(direction.getX() / length,
                    direction.getY() / length,
                    direction.getZ() / length);
        } else {
            this.direction = direction;
        }
    }

    public Vector3D substract(Vector3D v) {
        return new Vector3D(origin, direction.substract(v.getDirection()), false);
    }

    public Vector3D multiply(double x) {
        return new Vector3D(origin, direction.multiply(x), false);
    }

    public double scalarProduct(Vector3D v) {
        double x = getDirection().getX();
        double y = getDirection().getY();
        double z = getDirection().getZ();
        double xx = v.getDirection().getX();
        double yy = v.getDirection().getY();
        double zz = v.getDirection().getZ();
        return x * xx + y * yy + z * zz;
    }

    public double scalarProduct(Point3D p) {
        double x = getDirection().getX();
        double y = getDirection().getY();
        double z = getDirection().getZ();
        double xx = p.getX();
        double yy = p.getY();
        double zz = p.getZ();
        return x * xx + y * yy + z * zz;
    }

    public Point3D getOrigin() {
        return origin;
    }

    public Point3D getDirection() {
        return direction;
    }

    public Vector3D normalize() {
        double length = Math.sqrt(Math.pow(direction.getX(), 2) +
                Math.pow(direction.getY(), 2) +
                Math.pow(direction.getZ(), 2));
        if (length == 1) return this;
        return new Vector3D(origin,
                new Point3D(direction.getX() / length,
                        direction.getY() / length,
                        direction.getZ() / length),
                false);
    }

    public Vector3D negate(){
        // TODO If all vectors are normalized, we can use "true" here
        return new Vector3D(origin,
                new Point3D(-direction.getX(),
                        -direction.getY(),
                        -direction.getZ()),
                true);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vector3D vector3D = (Vector3D) o;

        if (!origin.equals(vector3D.origin)) return false;
        return direction.equals(vector3D.direction);

    }

    @Override
    public int hashCode() {
        int result = origin.hashCode();
        result = 31 * result + direction.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Vector3D{" +
                "origin=" + origin +
                ", direction=" + direction +
                '}';
    }
}
