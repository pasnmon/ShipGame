package com.gameSchiff;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class Crate extends GameObject {

    public static double width = 32;
    public static double height = 32;
    private String imagePath = IMAGE_DIR + "crate.gif";
    private URL imgUrl = getClass().getResource(imagePath);
    private ImageIcon crate = new ImageIcon(imgUrl);
    public static int count = 0;

    public Crate (Coordinate position){

        super(position);

        setHeight(crate.getIconHeight());
        setWidth(crate.getIconWidth());
        setMovingAngel(0);
        setMovingDistance(0);
    }

    public void paintMe(Graphics g){
        if (!isDisabled()) {

            Graphics2D g2d = (Graphics2D) g;

            crate.paintIcon(null,g2d, (int) this.getObjectPosition().getX(), (int) this.getObjectPosition().getY());
        }

    }

    public void disable(){
        if (!isDisabled()){
            Crate.count--;
            super.disable();
        }
    }
}
