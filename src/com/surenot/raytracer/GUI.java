package com.surenot.raytracer;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by m.clauss on 1/12/2016.
 */
public class GUI extends JFrame {

    public static void main(String[] args){
        Point3D observer = new Point3D(0, 2, 1.80);
        Point3D screenOrigin = new Point3D(5, 0, 4);
        Dimension screenDimension = new Dimension(3, 4);
        java.awt.Dimension screenPixelCount = new java.awt.Dimension(800, 600);

        Collection<Object3D> objects = new ArrayList();
        objects.add(new Sphere3D(new Point3D(10, 0, 0), 3));

        Scene scene = new Scene(observer, screenOrigin, screenDimension, (int)screenPixelCount.getWidth(), (int)screenPixelCount.getHeight(), objects);

        JFrame frame = new JFrame();
        ImageIcon icon = new ImageIcon(scene.render());
        JLabel label = new JLabel(icon);

        frame.setSize(screenPixelCount);
        frame.add(label);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

}
