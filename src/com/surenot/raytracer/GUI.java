package com.surenot.raytracer;

import com.surenot.raytracer.primitives.Dimension2D;
import com.surenot.raytracer.primitives.Point3D;
import com.surenot.raytracer.shapes.Shape3D;
import com.surenot.raytracer.shapes.Sphere3D;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Semaphore;

/**
 * Created by m.clauss on 1/12/2016.
 */
public class GUI extends JFrame {

    public static void main(String[] args){

        // Scene objects
        final Point3D observer = new Point3D(0, 0, 1.5);
        final Point3D screenOrigin = new Point3D(5, -2, 3);
        final Dimension2D screenDimension = new Dimension2D(3, 4);
        final java.awt.Dimension screenPixelCount = new java.awt.Dimension(800, 600);
        final Collection<Shape3D> objects = new ArrayList();
        objects.add(new Sphere3D(new Point3D(20, 0, 1.5), 2, Color.RED.getRGB()));
        objects.add(new Sphere3D(new Point3D(22, 3.5, 2.5), 2, Color.BLUE.getRGB()));
        objects.add(new Sphere3D(new Point3D(10, 1.2, -0.5), 0.5, Color.GREEN.getRGB()));
        objects.add(new Sphere3D(new Point3D(25, 3.5, 0), 2, Color.GRAY.getRGB()));

        final JFrame frame = new JFrame();
        final JLabel imageLabel = new JLabel();

        frame.setSize(screenPixelCount);
        frame.add(imageLabel);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        Semaphore sem = new Semaphore(1);
        for ( int i = 0; ; i+=5 ){
            final long start = System.currentTimeMillis();
            try {
                sem.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            final Collection<Point3D> lights = new ArrayList();
            final int theta1 = i % 360;
            final int theta2 = i * 3 % 360;
            lights.add(new Point3D(-Math.cos(Math.toRadians(theta1)) * 75, Math.sin(Math.toRadians(theta1)) * 75, 75));
            //lights.add(new Point3D(-Math.cos(Math.toRadians(theta2)) * 75, 0, Math.sin(Math.toRadians(theta2)) * 75));
            final Scene scene = new Scene(observer, screenOrigin, screenDimension, (int)screenPixelCount.getHeight(), (int)screenPixelCount.getWidth(), objects, lights);
            SwingUtilities.invokeLater(() -> {
                BufferedImage bi = scene.render();
                imageLabel.setIcon(new ImageIcon(bi));
                long stop = System.currentTimeMillis();
                frame.setTitle(String.valueOf(Math.ceil(1000 / (stop - start))));
                sem.release();
            });
        }
    }

}
