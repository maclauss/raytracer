package com.surenot.raytracer;

import com.surenot.raytracer.primitives.*;
import com.surenot.raytracer.shapes.Shape3D;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.security.InvalidParameterException;
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
    private final Point3D observer;
    private final Point3D origin;
    private final Dimension2D screenSize;
    private final Vector3D[][] screen;
    private final Collection<Shape3D> objects;
    private final Collection<Point3D> lightSources;

    public Scene(final Point3D observer, final Point3D origin,
                 final Dimension2D screenSize, final int pixelCountX, final int pixelCountY,
                 final Collection<Shape3D> objects,
                 final Collection<Point3D> lightSources){
        if ( observer == null || origin == null || screenSize == null || objects == null || lightSources == null ) throw new InvalidParameterException();
        if ( screenSize.getX() <= 0 ) throw new InvalidParameterException();
        if ( screenSize.getY() <= 0 ) throw new InvalidParameterException();
        if ( pixelCountX <= 0 ) throw new InvalidParameterException();
        if ( pixelCountY <= 0 ) throw new InvalidParameterException();

        this.observer = observer;
        this.origin = origin;
        this.screenSize = screenSize;
        this.screen = new Vector3D[pixelCountY][pixelCountX];
        // TODO defensive copy of the Collections to avoid reference leaks
        this.objects = objects;
        this.lightSources = lightSources;

        for ( int i = 0; i < pixelCountY; i++ ){
            for ( int j = 0; j < pixelCountX; j++ ){
                screen[i][j] = new Vector3D(observer, new Point3D(origin.getX(), origin.getY() + i * screenSize.getX() / pixelCountX, origin.getZ() - j * screenSize.getY() / pixelCountY));
            }
        }
    }

    public Scene addObject(Shape3D object){
        Collection<Shape3D> objects = this.objects.stream().collect(Collectors.toCollection(ArrayList::new));
        objects.add(object);
        return new Scene(observer, origin, screenSize, screen.length, screen[0].length, objects, null);
    }

    public BufferedImage render(){
        BufferedImage bi = new BufferedImage(screen.length, screen[0].length, BufferedImage.TYPE_INT_RGB);
        for ( int i = 0; i < screen.length; i++ ){
            for ( int j = 0; j < screen[0].length; j++ ){
                // TODO cache the rays, normalization is expensive
                Ray ray = new Ray(screen[i][j]);
                // TODO Add a background image and take the color of the pixel at pos i, j as default
                int color = Color.BLACK.getRGB();
                RayImpact impact = RayImpact.NONE;
                for ( Shape3D object : objects ){
                    RayImpact currentImpact;
                    if ( (currentImpact = object.isHit(ray)) != RayImpact.NONE &&
                            impact.equals(RayImpact.NONE) || currentImpact.getDistance() < impact.getDistance() ){
                        impact = currentImpact;
                        color = object.getColor();
                        Vector3D normal = object.getNormal(impact.getImpact());
                    }
                }
                bi.setRGB(i, j, color);
            }
        }
        return bi;
    }
}
