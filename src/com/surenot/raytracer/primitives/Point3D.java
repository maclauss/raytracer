package com.surenot.raytracer.primitives;

public final class Point3D {

    public final static Point3D ORIGIN = new Point3D(0, 0, 0);

    private final double x, y, z;

    public Point3D(final double x, final double y, final double z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return x;
    }

    public double getY() { return y; }

    public double getZ() { return z; }

    public Point3D multiply(double a){
        return new Point3D(x * a, y * a, z * a);
    }

    public Point3D substract(Point3D p){
        if ( p == null ) throw new IllegalArgumentException();
        return new Point3D(x - p.getX(), y - p.getY(), z - p.getZ());
    }

    public double scalarProduct(Point3D p){
        return x * p.getX() + y * p.getY() + z * p.getZ();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Point3D point3D = (Point3D) o;

        if (Double.compare(point3D.x, x) != 0) return false;
        if (Double.compare(point3D.y, y) != 0) return false;
        return Double.compare(point3D.z, z) == 0;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(x);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(z);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Point3D{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
