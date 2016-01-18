package com.surenot.raytracer;

import com.surenot.raytracer.primitives.Dimension2D;
import com.surenot.raytracer.primitives.Point3D;
import com.surenot.raytracer.primitives.Surface;
import com.surenot.raytracer.primitives.Vector3D;
import com.surenot.raytracer.shapes.Light3D;
import com.surenot.raytracer.shapes.Plane;
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

    public static void main(String[] args) {
        final JFrame frame = new JFrame();
        final JLabel imageLabel = new JLabel();

        frame.setSize(new java.awt.Dimension(800, 600));
        frame.add(imageLabel);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Semaphore sem = new Semaphore(1);
        for (int i = 0; ; i += 5) {
            final long start = System.currentTimeMillis();
            try {
                sem.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // Scene objects
            final Point3D observer = new Point3D(-13, 0, 0);
            final Point3D screenOrigin = new Point3D(-10, -2, 1.5);
            final Dimension2D screenDimension = new Dimension2D(3, 4);
            final Collection<Shape3D> objects = new ArrayList();
            objects.add(new Plane(
                    new Vector3D(new Point3D(0, 0, -4), new Point3D(1, 0, 0), true),
                    new Vector3D(new Point3D(0, 0, -4), new Point3D(0, 0, 1), true),
                    new Surface(Color.LIGHT_GRAY.getRGB(), 0.2, 1, 0.2, 50)));
            objects.add(new Sphere3D(
                    new Point3D(20, -1, 1.5), 2,
                    new Surface(Color.RED.getRGB(), 1, 1, 0.8, 5)));
            objects.add(new Sphere3D(new Point3D(22, 2.5, 2.5), 2,
                    new Surface(Color.BLUE.getRGB(), 1, 0.5, 1, 50)));
            objects.add(new Sphere3D(new Point3D(18, 0.2, 0), 0.5,
                    new Surface(Color.GREEN.getRGB(), 1, 1, 0, 50)));
            objects.add(new Sphere3D(new Point3D(24, 2.5, 0), 1.5,
                    new Surface(Color.ORANGE.getRGB(), 1, 0.5, 0.5, 1)));
            objects.add(new Sphere3D(new Point3D(17, 0.5, 2), 0.5,
                    new Surface(Color.LIGHT_GRAY.getRGB(), 1, 1, 1, 500)));
            final int theta = i % 360;
            /*objects.add(new Light3D(new Sphere3D(
                    new Point3D(
                            -Math.cos(Math.toRadians(theta)) * 7 + 20,
                            Math.sin(Math.toRadians(theta)) * 7 + 3,
                            3),
                    1, new Surface(Color.YELLOW.getRGB(), 1, 1, 1, 50))));
            objects.add(new Light3D(new Sphere3D(
                    new Point3D(
                            Math.cos(Math.toRadians(theta)) * 4 + 20,
                            0,
                            Math.sin(Math.toRadians(theta)) * 4 + 2),
                    1, new Surface(Color.WHITE.getRGB(), 1, 1, 1, 50))));*/
            objects.add(new Light3D(new Sphere3D(
                    new Point3D(
                            15,
                            -3,
                            0),
                    0.5, new Surface(Color.WHITE.getRGB(), 1, 1, 1, 50))));
            objects.add(new Light3D(new Sphere3D(
                    new Point3D(
                            15,
                            3,
                            4),
                    0.5, new Surface(Color.RED.getRGB(), 1, 1, 1, 50))));
            final Scene scene = new Scene(observer, screenOrigin, screenDimension, (int) frame.getSize().getHeight(), (int) frame.getSize().getWidth(), objects);
            SwingUtilities.invokeLater(() -> {
                BufferedImage bi = scene.render();
                imageLabel.setIcon(new ImageIcon(bi));
                long stop = System.currentTimeMillis();
                frame.setTitle(String.valueOf(Math.ceil(1000 / (stop - start))));
                sem.release();
            });
            break;
        }
    }

}
