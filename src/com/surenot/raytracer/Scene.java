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

    public final static double MAX_AMBIENT_LIGHT_INTENSITY = 0.1;
    public final static double MAX_DIFFUSE_LIGHT_INTENSITY = 1 - MAX_AMBIENT_LIGHT_INTENSITY;

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
        newScreen.parallelStream()
                .forEach(ray -> image.setRGB(ray.getX(), ray.getY(), computeColor(ray.getVector())));
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

        Vector3D n = impact.getImpactedObject().getNormal(impact.getPoint());

        /*r.normalize();
        System.out.println("v=" + v);
        System.out.println("impact=" + impact);
        System.out.println("r=" + r);
        System.out.println("n=" + n);
        //System.out.println("light=" + light);
        System.out.println();*/

        final double or = new Color(impact.getImpactedObject().getColor()).getRed() / 255;
        final double og = new Color(impact.getImpactedObject().getColor()).getGreen() / 255;
        final double ob = new Color(impact.getImpactedObject().getColor()).getBlue() / 255;

        final double lr = Color.WHITE.getRed() / 255;
        final double lg = Color.WHITE.getGreen() / 255;
        final double lb = Color.WHITE.getBlue() / 255;

        final double ambientIntensityR = MAX_AMBIENT_LIGHT_INTENSITY * impact.getImpactedObject().getAmbiantReflectionCoefficient() * or;
        final double ambientIntensityG = MAX_AMBIENT_LIGHT_INTENSITY * impact.getImpactedObject().getAmbiantReflectionCoefficient() * og;
        final double ambientIntensityB = MAX_AMBIENT_LIGHT_INTENSITY * impact.getImpactedObject().getAmbiantReflectionCoefficient() * ob;

        final double diffuseCoefficient = MAX_DIFFUSE_LIGHT_INTENSITY * impact.getImpactedObject().getDiffuseReflectionCoefficient();
        double diffuseIntensityR = 0;
        double diffuseIntensityG = 0;
        double diffuseIntensityB = 0;

        double specularIntensityR = 0;
        double specularIntensityG = 0;
        double specularIntensityB = 0;
        for ( Light3D light : lights ){
            Vector3D lightVector = new Vector3D(light.getCenter(), impact.getPoint());
            double sqd = lightVector.getOrigin().squareDistance(impact.getPoint());
            if (shapes.stream()
                    .filter(shape -> shape != light && shape != impact.getImpactedObject())
                    .anyMatch(shape -> {
                        Impact3D i = shape.isHit(lightVector);
                        return !i.equals(Impact3D.NONE) &&
                                !i.getPoint().equals(impact.getImpactedObject()) &&
                                i.getSquareDistance() < sqd;
                    })) continue;
            // FIXME Somehow this produces the opposite of what it should produce
            double theta = -n.normalize().scalarProduct(lightVector.normalize());
            double atmosphericAttenuation = Math.min(1,
                    (1 / ( light.getConstantAttenuationCoefficient() +
                            light.getLinearAttenuationCoefficient() *
                                    (lightVector.getLength() + impact.getDistance()) +
                            light.getQuadraticAttenuationCoefficient() *
                                    Math.pow(lightVector.getLength() + impact.getDistance(), 2) )));
            diffuseIntensityR += theta < 0 ? 0 : diffuseCoefficient * theta * atmosphericAttenuation * or;
            diffuseIntensityG += theta < 0 ? 0 : diffuseCoefficient * theta * atmosphericAttenuation * og;
            diffuseIntensityB += theta < 0 ? 0 : diffuseCoefficient * theta * atmosphericAttenuation * ob;

            Vector3D nn = new Vector3D(Point3D.ORIGIN, n.normalize());
            Vector3D r = lightVector.substract(nn.multiply(lightVector.scalarProduct(nn)).multiply(2));
            specularIntensityR += lr * atmosphericAttenuation *
                    impact.getImpactedObject().getSpecularReflectionCoefficient() *
                    Math.pow(r.scalarProduct(v.normalize()),
                            impact.getImpactedObject().getSpecularReflectionExponent());
            specularIntensityG += lg * atmosphericAttenuation *
                    impact.getImpactedObject().getSpecularReflectionCoefficient() *
                    Math.pow(r.normalize().scalarProduct(v.normalize()),
                            impact.getImpactedObject().getSpecularReflectionExponent());
            specularIntensityB += lb * atmosphericAttenuation *
                    impact.getImpactedObject().getSpecularReflectionCoefficient() *
                    Math.pow(r.normalize().scalarProduct(v.normalize()),
                            impact.getImpactedObject().getSpecularReflectionExponent());
        }
        final double intensityR = ambientIntensityR + diffuseIntensityR + specularIntensityR;
        final double intensityG = ambientIntensityG + diffuseIntensityG + specularIntensityG;
        final double intensityB = ambientIntensityB + diffuseIntensityB + specularIntensityB;

        Color color = new Color(impact.getImpactedObject().getColor());
        // The ambient component can be computed only once per object as it never changes
        return new Color(
                (int) (Math.max(Math.min(intensityR * 255, 255), 0)),
                (int) (Math.max(Math.min(intensityG * 255, 255), 0)),
                (int) (Math.max(Math.min(intensityB * 255, 255), 0)))
                .getRGB();
    }
}