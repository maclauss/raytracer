package com.surenot.raytracer;

import com.surenot.raytracer.primitives.*;
import com.surenot.raytracer.shapes.Light3D;
import com.surenot.raytracer.shapes.Shape3D;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
                 final Collection<Shape3D> objects,
                 final Collection<Point3D> lights){
        if ( observer == null || origin == null || screenSize == null || objects == null || lights == null ){
            throw new IllegalArgumentException();
        }
        if ( screenSize.getX() <= 0 ) throw new IllegalArgumentException();
        if ( screenSize.getY() <= 0 ) throw new IllegalArgumentException();
        if ( pixelCountX <= 0 ) throw new IllegalArgumentException();
        if ( pixelCountY <= 0 ) throw new IllegalArgumentException();

        this.observer = observer;
        this.origin = origin;
        this.screenSize = screenSize;
        this.screen = new Ray[pixelCountY][pixelCountX];
        this.objects = new ArrayList(objects.size());
        this.lights = new ArrayList();
        objects.forEach((shape) -> {
            this.objects.add(shape);
            if ( shape instanceof Light3D) this.lights.add((Light3D)shape);
        });

        double pixelSizeY = screenSize.getX() / pixelCountX;
        double pixelSizeZ = screenSize.getY() / pixelCountY;
        for ( int i = 0; i < pixelCountY; i++ ){
            for ( int j = 0; j < pixelCountX; j++ ){
                screen[i][j] = new Ray(new Vector3D(observer, new Point3D(origin.getX(), origin.getY() + i * pixelSizeY, origin.getZ() - j * pixelSizeZ)));
            }
        }
    }

    public BufferedImage render(){
        BufferedImage bi = new BufferedImage(screen.length, screen[0].length, BufferedImage.TYPE_INT_RGB);
        for ( int i = 0; i < screen.length; i++ ){
            for ( int j = 0; j < screen[0].length; j++ ){
                Ray ray = screen[i][j];
                int color = computeColor(ray);
                bi.setRGB(i, j, color);
            }
        }
        return bi;
    }

    private int computeColor(final Ray ray){
        // TODO Add a background texture and take the color hit by the ray as default
        Color color = Color.BLACK;
        // FIXME Too expensive computation, find a way to optimize (too many normalizations etc)
        RayImpact impact = getClosestImpact(ray, objects);
        if ( impact.equals(RayImpact.NONE)) return color.getRGB(); // No impact: return background color
        Shape3D object = impact.getObject();
        color = new Color(object.getColor());
        if ( object.getClass() == Light3D.class ) {
            return color.getRGB(); // First object impacted is a light: return light color
        }
        Vector3D normal = object.getNormal(impact.getImpact());
        double diffusedLight = 0;
        for ( Shape3D light : lights){
            Vector3D lightVector = light.getNormal(impact.getImpact());
            // TODO Add power to light sources and ponderate with the distance |(light - impact)|
            double theta = -normal.normalize().scalarProduct(lightVector.normalize());
            diffusedLight = Math.min(DIFFUSED_LIGHT, diffusedLight + (theta < 0 ? 0 : DIFFUSED_LIGHT * theta));
        }
        color = new Color((int)(color.getRed() * (AMBIENT_LIGHT + diffusedLight)),
                (int)(color.getGreen() * (AMBIENT_LIGHT + diffusedLight)),
                (int)(color.getBlue() * (AMBIENT_LIGHT + diffusedLight)));
        return color.getRGB();
    }

    private RayImpact getClosestImpact(Ray ray, Collection<Shape3D> objects){
        RayImpact impact = RayImpact.NONE;
        for ( Shape3D object : objects ) {
            RayImpact currentImpact;
            if ((currentImpact = object.isHit(ray)) != RayImpact.NONE &&
                    impact.equals(RayImpact.NONE) || currentImpact.getDistance() < impact.getDistance()) {
                impact = currentImpact;
            }
        }
        return impact;
    }
}
