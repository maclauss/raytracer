package com.surenot.raytracer.shapes;

import com.surenot.raytracer.primitives.Ray;

/**
 * Created by m.clauss on 1/12/2016.
 */
public interface Shape3D {
    boolean isHit(Ray ray);
    int getColor();
}
