package com.surenot.raytracer.primitives;

/**
 * Created by surenot on 1/15/16.
 */
public class Ray {

    private final int x;
    private final int y;
    private final Vector3D v;

    public Ray(int x, int y, Vector3D v){
        this.x = x;
        this.y = y;
        this.v = v;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Vector3D getVector() {
        return v;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Ray ray = (Ray) o;

        if (x != ray.x) return false;
        if (y != ray.y) return false;
        return !(v != null ? !v.equals(ray.v) : ray.v != null);

    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        result = 31 * result + (v != null ? v.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Ray{" +
                "x=" + x +
                ", y=" + y +
                ", vector=" + v +
                '}';
    }
}