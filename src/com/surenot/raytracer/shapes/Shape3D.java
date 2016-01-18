package com.surenot.raytracer.shapes;

import com.surenot.raytracer.primitives.Impact3D;
import com.surenot.raytracer.primitives.Point3D;
import com.surenot.raytracer.primitives.Surface;
import com.surenot.raytracer.primitives.Vector3D;

/**
 * Created by m.clauss on 1/12/2016.
 */
public interface Shape3D {

    Impact3D isHit(Vector3D v);
    Vector3D getNormal(Point3D p);
    Point3D getCenter();
    Surface getSurface();

}
