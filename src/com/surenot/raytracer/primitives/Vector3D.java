package com.surenot.raytracer.primitives;

/**
 * Created by m.clauss on 1/13/2016.
 */
public class Vector3D {

    private final Point3D origin;
    private final Point3D direction;
    // Actual caches, non-final fields, not to be used in hashcode and equals
    private Point3D normalized = null;
    private double length;

    public Vector3D(final Point3D direction){
        this(Point3D.ORIGIN, direction);
    }

    public Vector3D(final Point3D origin, final Point3D direction){
        if ( origin == null || direction == null ){
            throw new IllegalArgumentException();
        }
        this.origin = origin;
        this.direction = direction;
        // TODO try to replace with Double.NaN ?
        this.length = Double.MAX_VALUE;
    }

    public Vector3D substract(Vector3D v){
        return new Vector3D(origin.substract(v.getOrigin()), direction.substract(v.getDirection()));
    }

    public Vector3D multiply(double x){
        return new Vector3D(origin, new Point3D(direction.getX() * x, direction.getY() * x, direction.getZ() * x));
    }

    public double scalarProduct(Vector3D v){
        double x = getDirection().getX() - origin.getX();
        double y = getDirection().getY() - origin.getY();
        double z = getDirection().getZ() - origin.getZ();
        double xx = v.getDirection().getX() - v.getOrigin().getX();
        double yy = v.getDirection().getY() - v.getOrigin().getY();
        double zz = v.getDirection().getZ() - v.getOrigin().getZ();
        return x * xx + y * yy + z * zz;
    }

    public Point3D getOrigin() {
        return origin;
    }

    public Point3D getDirection() {
        return direction;
    }

    public double getLength() {
        return length == Double.MAX_VALUE ?
                (length = Math.sqrt(
                    Math.pow(direction.getX() - origin.getX(), 2) +
                    Math.pow(direction.getY() - origin.getY(), 2) +
                    Math.pow(direction.getZ() - origin.getZ(), 2))) :
                length;
    }

    public Point3D normalize(){
        if ( normalized == null ){
            double x = direction.getX() - origin.getX();
            double y = direction.getY() - origin.getY();
            double z = direction.getZ() - origin.getZ();
            double length = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
            if ( length == 1 ) normalized = new Point3D(x, y, z);
            else normalized = new Point3D(x / length, y / length, z / length);
        }
        return normalized;
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
}
