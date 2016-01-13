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
    private final Point3D[][] screen;
    private final Collection<Shape3D> objects;

    public Scene(final Point3D observer, final Point3D origin,
                 final Dimension2D screenSize, final int pixelCountX, final int pixelCountY,
                 final Collection<Shape3D> objects){
        if ( observer == null || origin == null || screenSize == null || objects == null ) throw new InvalidParameterException();
        if ( screenSize.getX() <= 0 ) throw new InvalidParameterException();
        if ( screenSize.getY() <= 0 ) throw new InvalidParameterException();
        if ( pixelCountX <= 0 ) throw new InvalidParameterException();
        if ( pixelCountY <= 0 ) throw new InvalidParameterException();

        this.observer = observer;
        this.origin = origin;
        this.screenSize = screenSize;
        this.screen = new Point3D[pixelCountY][pixelCountX];
        // TODO defensive copy of the Collection to avoid reference leaks
        this.objects = objects;

        for ( int i = 0; i < pixelCountY; i++ ){
            for ( int j = 0; j < pixelCountX; j++ ){
                screen[i][j] = new Point3D(origin.getX(), origin.getY() + i * screenSize.getX() / pixelCountX, origin.getZ() - j * screenSize.getY() / pixelCountY);
            }
        }
    }

    public Scene addObject(Shape3D object){
        Collection<Shape3D> objects = this.objects.stream().collect(Collectors.toCollection(ArrayList::new));
        objects.add(object);
        return new Scene(observer, origin, screenSize, screen.length, screen[0].length, objects);
    }

    public BufferedImage render(){
        BufferedImage bi = new BufferedImage(screen.length, screen[0].length, BufferedImage.TYPE_INT_RGB);
        for ( int i = 0; i < screen.length; i++ ){
            for ( int j = 0; j < screen[0].length; j++ ){
                Ray ray = new Ray(observer, screen[i][j]);
                // TODO Add a background image and take the color of the pixel at pos i, j as default
                int color = Color.BLACK.getRGB();
                double impactDistance = Double.MAX_VALUE;;
                for ( Shape3D object : objects ){
                    double currentImpactDistance = Double.MAX_VALUE;
                    // TODO cache the rays in an array, normalization is expensive
                    if ( (currentImpactDistance = object.isHit(ray)) != Double.NaN && currentImpactDistance < impactDistance ){
                        impactDistance = currentImpactDistance;
                        color = object.getColor();
                    }
                }
                bi.setRGB(i, j, color);
            }
        }
        return bi;
    }
}
