package com.surenot.raytracer.shapes;

import com.surenot.raytracer.primitives.Ray;
import com.surenot.raytracer.primitives.RayImpact;

/**
 * Created by m.clauss on 1/12/2016.
 */
public interface Shape3D {
    RayImpact isHit(Ray ray);
    int getColor();
}
