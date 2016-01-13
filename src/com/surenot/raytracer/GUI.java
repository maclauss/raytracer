package com.surenot.raytracer;

import com.surenot.raytracer.primitives.Dimension2D;
import com.surenot.raytracer.primitives.Point3D;
import com.surenot.raytracer.shapes.Shape3D;
import com.surenot.raytracer.shapes.Sphere3D;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by m.clauss on 1/12/2016.
 */
public class GUI extends JFrame {

    public static void main(String[] args){
        Point3D observer = new Point3D(0, 0, 1.5);
        Point3D screenOrigin = new Point3D(5, -2, 3);
        Dimension2D screenDimension = new Dimension2D(3, 4);
        java.awt.Dimension screenPixelCount = new java.awt.Dimension(800, 600);

        Collection<Shape3D> objects = new ArrayList();
        objects.add(new Sphere3D(new Point3D(20, 0, 1.5), 2, Color.RED.getRGB()));
        objects.add(new Sphere3D(new Point3D(22, 3.5, 2.5), 2, Color.BLUE.getRGB()));
        objects.add(new Sphere3D(new Point3D(10, 1.2, 1.5), 0.5, Color.GREEN.getRGB()));
        objects.add(new Sphere3D(new Point3D(25, 3.5, 1), 2, Color.GRAY.getRGB()));

        Collection<Point3D> lights = new ArrayList();

        Scene scene = new Scene(observer, screenOrigin, screenDimension, (int)screenPixelCount.getHeight(), (int)screenPixelCount.getWidth(), objects, lights);

        JFrame frame = new JFrame();
        ImageIcon icon = new ImageIcon(scene.render());
        JLabel label = new JLabel(icon);

        frame.setSize(screenPixelCount);
        frame.add(label);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

}
