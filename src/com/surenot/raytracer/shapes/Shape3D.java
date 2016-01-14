package com.surenot.raytracer.shapes;

import com.surenot.raytracer.primitives.Point3D;
import com.surenot.raytracer.primitives.Ray;
import com.surenot.raytracer.primitives.RayImpact;
import com.surenot.raytracer.primitives.Vector3D;

/**
 * Created by m.clauss on 1/12/2016.
 */
public interface Shape3D {

    RayImpact isHit(Ray ray);
    Vector3D getNormal(Point3D p);
    int getColor();

}
