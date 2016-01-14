package com.surenot.raytracer.primitives;

/**
 * Created by m.clauss on 1/12/2016.
 */
public final class Dimension2D {

    private final double x, y;

    public Dimension2D(final double x, final double y){
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Dimension2D dimension = (Dimension2D) o;

        if (Double.compare(dimension.x, x) != 0) return false;
        return Double.compare(dimension.y, y) == 0;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(x);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
