package com.surenot.raytracer;

/**
 * Created by m.clauss on 1/12/2016.
 */
public interface Object3D {
    boolean isHit(Ray ray);
    int getColor();
}
