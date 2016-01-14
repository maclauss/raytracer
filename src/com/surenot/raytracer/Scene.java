package com.surenot.raytracer;

import com.sun.javafx.UnmodifiableArrayList;
import com.sun.javafx.collections.UnmodifiableListSet;
import com.surenot.raytracer.primitives.*;
import com.surenot.raytracer.shapes.Light3D;
import com.surenot.raytracer.shapes.Shape3D;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public final class Scene {
    /*

    Z
               X
    |
    |        /
    |   ____/________________
    |  |        SCREEN       |
    |  |                     |
    |  |                     |
    |__|________x Observer   |
    |  |________|____________|
    | /         |
    |/__________|________________________  Y

    */

    public final static double AMBIENT_LIGHT = 0.1;
    public final static double DIFFUSED_LIGHT = 1 - AMBIENT_LIGHT;

    private final static ExecutorService executor = Executors.newFixedThreadPool(4);

    private final Point3D observer;
    private final Point3D origin;
    private final Dimension2D screenSize;
    private final Ray[][] screen;
    private final Collection<Shape3D> objects;
    private final Collection<Light3D> lights;

    public Scene(final Point3D observer,
                 final Point3D origin,
                 final Dimension2D screenSize,
                 final int pixelCountX, final int pixelCountY,
                 final Collection<Shape3D> objects) {
        if (observer == null || origin == null || screenSize == null || objects == null) {
            throw new IllegalArgumentException();
        }
        if (screenSize.getX() <= 0) throw new IllegalArgumentException();
        if (screenSize.getY() <= 0) throw new IllegalArgumentException();
        if (pixelCountX <= 0) throw new IllegalArgumentException();
        if (pixelCountY <= 0) throw new IllegalArgumentException();

        this.observer = observer;
        this.origin = origin;
        this.screenSize = screenSize;
        this.screen = new Ray[pixelCountY][pixelCountX];
        this.objects = new CopyOnWriteArrayList<>(objects);
        this.lights = new CopyOnWriteArrayList<>(objects.stream()
                .filter((shape) -> shape instanceof Light3D)
                .map(light -> (Light3D) light)
                .collect(Collectors.toList()));

        double pixelSizeY = screenSize.getX() / pixelCountX;
        double pixelSizeZ = screenSize.getY() / pixelCountY;
        for (int i = 0; i < pixelCountY; i++) {
            for (int j = 0; j < pixelCountX; j++) {
                screen[i][j] = new Ray(new Vector3D(observer, new Point3D(origin.getX(), origin.getY() + i * pixelSizeY, origin.getZ() - j * pixelSizeZ)));
            }
        }
    }

    public BufferedImage render() {
        BufferedImage bi = new BufferedImage(screen.length, screen[0].length, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < screen.length; i++) {
            for (int j = 0; j < screen[0].length; j++) {
                Ray ray = screen[i][j];
                int color = computeColor(ray);
                bi.setRGB(i, j, color);
            }
        }
        return bi;
    }

    private int computeColor(final Ray ray) {
        // TODO Expensive computation, spend some time to optimise
        RayImpact impact = objects.stream()
                //.filter // TODO Find a heuristic to remove objects for which we know they will not be hit
                .map(shape -> shape.isHit(ray))
                .filter(ri -> !ri.equals(RayImpact.NONE))
                .reduce(RayImpact.NONE, (a, b) -> a.getDistance() < b.getDistance() ? a : b);

        if (impact.equals(RayImpact.NONE)) return Color.BLACK.getRGB();
        if (impact.getImpactedObject().getClass() == Light3D.class) return impact.getImpactedObject().getColor();

        Vector3D normal = impact.getImpactedObject().getNormal(impact.getImpact());
        // TODO Quadratic to be replaced if possible
        double dLight = lights.stream()
                .map(light -> {
                    Vector3D lightVector = new Vector3D(light.getCenter(), impact.getImpact());
                    double sqd = lightVector.getOrigin().squareDistance(impact.getImpact());
                    if ( objects.stream()
                            .filter(shape -> shape != light && shape != impact.getImpactedObject())
                            .map(shape -> shape.isHit(new Ray(lightVector)))
                            .anyMatch(ri -> !ri.equals(RayImpact.NONE) &&
                                    !ri.getImpact().equals(impact.getImpactedObject()) &&
                                    ri.getSquareDistance() < sqd) ) return 0.0;

                    double theta = -normal.normalize().scalarProduct(lightVector.normalize());
                    return theta < 0 ? 0 : DIFFUSED_LIGHT * theta;
                })
                .reduce(0.0, (a, b) -> Math.min(a + b, DIFFUSED_LIGHT));

        Color color = new Color(impact.getImpactedObject().getColor());
        return new Color(
                (int) (color.getRed() * (AMBIENT_LIGHT + dLight)),
                (int) (color.getGreen() * (AMBIENT_LIGHT + dLight)),
                (int) (color.getBlue() * (AMBIENT_LIGHT + dLight)))
                .getRGB();
    }

    // I keep this because it is somehow faster than the stream example...
    private int computeColor2(final Ray ray) {
        // FIXME Too expensive computation, find a way to optimize (too many normalizations etc)
        RayImpact impact = getClosestImpact(ray, objects);

        if (impact.equals(RayImpact.NONE)) return Color.BLACK.getRGB();
        if (impact.getImpactedObject().getClass() == Light3D.class) return impact.getImpactedObject().getColor();

        Vector3D normal = impact.getImpactedObject().getNormal(impact.getImpact());
        double dLight = 0;
        for (Shape3D light : lights) {
            Vector3D lightVector = new Vector3D(light.getCenter(), impact.getImpact());
            if (isBlocked(lightVector, impact.getImpact(), objects, light, impact.getImpactedObject())) {
                continue;
            }
            // TODO Add power to light sources and ponderate with the distance |(light - impact)|
            double theta = -normal.normalize().scalarProduct(lightVector.normalize());
            dLight = Math.min(DIFFUSED_LIGHT, dLight + (theta < 0 ? 0 : DIFFUSED_LIGHT * theta));
            if (dLight == DIFFUSED_LIGHT) break;
        }

        Color color = new Color(impact.getImpactedObject().getColor());
        color = new Color((int) (color.getRed() * (AMBIENT_LIGHT + dLight)),
                (int) (color.getGreen() * (AMBIENT_LIGHT + dLight)),
                (int) (color.getBlue() * (AMBIENT_LIGHT + dLight)));
        return color.getRGB();
    }

    private RayImpact getClosestImpact(Ray ray, Collection<Shape3D> objects) {
        RayImpact impact = RayImpact.NONE;
        for (Shape3D object : objects) {
            RayImpact currentImpact;
            if ((currentImpact = object.isHit(ray)) != RayImpact.NONE &&
                impact.equals(RayImpact.NONE) || currentImpact.getDistance() < impact.getDistance()) {
                impact = currentImpact;
            }
        }
        return impact;
    }

    private static boolean isBlocked(final Vector3D v,
                                     final Point3D p,
                                     final Collection<Shape3D> c,
                                     final Shape3D sourceObject,
                                     final Shape3D impactedObject) {
        double sqd = v.getOrigin().squareDistance(p);
        for (Shape3D shape : c) {
            if (shape == sourceObject) continue;
            // TODO This solves some double accuracy issues, but will cause problems with complex shapes
            if (shape == impactedObject) continue;
            RayImpact ri = shape.isHit(new Ray(v));
            if (!ri.equals(RayImpact.NONE)) {
                if (!ri.getImpact().equals(p) && ri.getSquareDistance() < sqd) {
                    return true;
                }
            }
        }
        return false;
    }
}
