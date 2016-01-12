package com.surenot.raytracer;

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
    |  |                     |
    |  |_____________________|
    | /
    |/_______________________________  Y

    */
    private final Point3D observer;
    private final Point3D origin;
    private final Dimension screenSize;
    private final Point3D[][] screen;
    private final Collection<Object3D> objects;

    public Scene(final Point3D observer, final Point3D origin,
                 final Dimension screenSize, final int pixelCountX, final int pixelCountY,
                 final Collection<Object3D> objects){
        if ( observer == null || origin == null || screenSize == null || objects == null ) throw new InvalidParameterException();
        if ( screenSize.getX() <= 0 ) throw new InvalidParameterException();
        if ( screenSize.getY() <= 0 ) throw new InvalidParameterException();
        if ( pixelCountX <= 0 ) throw new InvalidParameterException();
        if ( pixelCountY <= 0 ) throw new InvalidParameterException();

        this.observer = observer;
        this.origin = origin;
        this.screenSize = screenSize;
        this.screen = new Point3D[pixelCountX][pixelCountY];
        this.objects = objects;

        for ( int i = 0; i < pixelCountX; i++ ){
            for ( int j = 0; j < pixelCountY; j++ ){
                screen[i][j] = new Point3D(origin.getX(), origin.getY() + i * screenSize.getX() / pixelCountX, origin.getZ() + j * screenSize.getY() / pixelCountY);
            }
        }
    }

    public Scene addObject(Object3D object){
        Collection<Object3D> objects = this.objects.stream().collect(Collectors.toCollection(ArrayList::new));
        objects.add(object);
        return new Scene(observer, origin, screenSize, screen.length, screen[0].length, objects);
    }

    public BufferedImage render(){
        BufferedImage bi = new BufferedImage(screen.length, screen[0].length, BufferedImage.TYPE_INT_RGB);
        for ( int i = 0; i < screen.length; i++ ){
            for ( int j = 0; j < screen[0].length; j++ ){
                int color = Color.BLACK.getRGB();
                for ( Object3D object : objects ){
                    // TODO cache the rays in an array, normalization is expensive
                    if ( object.isHit(new Ray(screen[0][0], new Vector3D(observer, screen[0][0]) )) ){
                        color = object.getColor();
                    }
                }
                bi.setRGB(i, j, color);
            }
        }
        return bi;
    }
}
