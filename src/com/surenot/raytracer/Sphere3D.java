package com.surenot.raytracer;

import java.awt.*;

public final class Sphere3D implements Object3D {

    private final Point3D origin;
    private final double radius;

    public Sphere3D(final Point3D origin, final double radius){
        this.origin = origin;
        this.radius = radius;
    }

    @Override
    public boolean isHit(Ray ray){

        return true;
    }

    @Override
    public int getColor() {
        return Color.RED.getRGB();
    }
}
