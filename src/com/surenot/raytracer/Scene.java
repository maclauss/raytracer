package com.surenot.raytracer;

import com.surenot.raytracer.primitives.Dimension2D;
import com.surenot.raytracer.primitives.Impact3D;
import com.surenot.raytracer.primitives.Point3D;
import com.surenot.raytracer.primitives.Ray;
import com.surenot.raytracer.primitives.Vector3D;
import com.surenot.raytracer.shapes.Light3D;
import com.surenot.raytracer.shapes.Shape3D;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
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

    private final Collection<Ray> newScreen;
    private final Collection<Shape3D> shapes;
    private final Collection<Light3D> lights;
    private final BufferedImage image;

    public Scene(final Point3D observer,
                 final Point3D origin,
                 final Dimension2D screenSize,
                 final int pixelCountX, final int pixelCountY,
                 final Collection<Shape3D> shapes) {
        if (observer == null || origin == null || screenSize == null || shapes == null) {
            throw new IllegalArgumentException();
        }
        if (screenSize.getX() <= 0) throw new IllegalArgumentException();
        if (screenSize.getY() <= 0) throw new IllegalArgumentException();
        if (pixelCountX <= 0) throw new IllegalArgumentException();
        if (pixelCountY <= 0) throw new IllegalArgumentException();

        this.image = new BufferedImage(pixelCountY, pixelCountX, BufferedImage.TYPE_INT_RGB);
        this.newScreen = new ArrayList<>(pixelCountX * pixelCountY);
        this.shapes = new ArrayList<>(shapes);
        this.lights = new ArrayList<>(shapes.stream()
                .filter((shape) -> shape instanceof Light3D)
                .map(light -> (Light3D) light)
                .collect(Collectors.toList()));

        double pixelSizeY = screenSize.getX() / pixelCountX;
        double pixelSizeZ = screenSize.getY() / pixelCountY;
        for (int i = 0; i < pixelCountY; i++) {
            for (int j = 0; j < pixelCountX; j++) {
                newScreen.add(new Ray(i, j, new Vector3D(observer,
                        new Point3D(origin.getX(), origin.getY() + i * pixelSizeY, origin.getZ() - j * pixelSizeZ))));
            }
        }
    }

    public BufferedImage render() {
        newScreen.parallelStream().forEach(ray -> image.setRGB(ray.getX(), ray.getY(), computeColor(ray.getVector())));
        return image;
    }

    private int computeColor(final Vector3D v) {
        // TODO Expensive computation, spend some time to optimise
        Impact3D impact = shapes.stream()
                //.filter // TODO Find a heuristic to remove shapes for which we know they will not be hit
                .map(shape -> shape.isHit(v))
                .filter(ri -> !ri.equals(Impact3D.NONE))
                .reduce(Impact3D.NONE, (a, b) -> a.getDistance() < b.getDistance() ? a : b);

        if (impact.equals(Impact3D.NONE)) return Color.BLACK.getRGB();
        if (impact.getImpactedObject().getClass() == Light3D.class) return impact.getImpactedObject().getColor();

        Vector3D normal = impact.getImpactedObject().getNormal(impact.getPoint());
        // TODO Quadratic to be replaced if possible
        double dLight = lights.stream()
                .map(light -> {
                    Vector3D lightVector = new Vector3D(light.getCenter(), impact.getPoint());
                    double sqd = lightVector.getOrigin().squareDistance(impact.getPoint());
                    if (shapes.stream()
                            .filter(shape -> shape != light && shape != impact.getImpactedObject())
                            .anyMatch(shape -> {
                                Impact3D i = shape.isHit(lightVector);
                                return !i.equals(Impact3D.NONE) &&
                                        !i.getPoint().equals(impact.getImpactedObject()) &&
                                        i.getSquareDistance() < sqd;
                            })) return 0.0;
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
    private int computeColor2(final Vector3D v) {
        // FIXME Too expensive computation, find a way to optimize (too many normalizations etc)
        Impact3D impact = getClosestImpact(v, shapes);

        if (impact.equals(Impact3D.NONE)) return Color.BLACK.getRGB();
        if (impact.getImpactedObject().getClass() == Light3D.class) return impact.getImpactedObject().getColor();

        Vector3D normal = impact.getImpactedObject().getNormal(impact.getPoint());
        double dLight = 0;
        for (Shape3D light : lights) {
            Vector3D lightVector = new Vector3D(light.getCenter(), impact.getPoint());
            if (isBlocked(lightVector, impact.getPoint(), shapes, light, impact.getImpactedObject())) {
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

    private Impact3D getClosestImpact(final Vector3D v, final Collection<Shape3D> objects) {
        Impact3D impact = Impact3D.NONE;
        for (Shape3D object : objects) {
            Impact3D currentImpact;
            if ((currentImpact = object.isHit(v)) != Impact3D.NONE &&
                    impact.equals(Impact3D.NONE) || currentImpact.getDistance() < impact.getDistance()) {
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
            Impact3D ri = shape.isHit(v);
            if (!ri.equals(Impact3D.NONE)) {
                if (!ri.getPoint().equals(p) && ri.getSquareDistance() < sqd) {
                    return true;
                }
            }
        }
        return false;
    }
}