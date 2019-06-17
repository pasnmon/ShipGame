package com.gameSchiff;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;

public class Rock extends GameObject {

    public static double width = 64;
    public static double height = 64;

    private String imagePath = IMAGE_DIR + "process-scaled.png";
    private URL imgUrl = getClass().getResource(imagePath);
    private ImageIcon rock;
    public static int count = 0;

    public Rock (Coordinate position){

        super(position);

        setRockImg();
        setHeight(rock.getIconHeight());
        setWidth(rock.getIconWidth());
        setMovingAngel(0);
        setMovingDistance(0);
    }

    public void paintMe(Graphics g){
        if (!isDisabled()) {

            Graphics2D g2d = (Graphics2D) g;

            rock.paintIcon(null, g2d, (int) this.getObjectPosition().getX(), (int) this.getObjectPosition().getY());
        }
    }

    public void setRockImg(){
        try {
            final BufferedImage source = ImageIO.read(imgUrl);
            rock = new ImageIcon(source.getSubimage(source.getWidth() - 131, 0, 131, 128)
                            .getScaledInstance(64,64,Image.SCALE_SMOOTH));
        }catch (Exception e){

        }

    }

    public void disable(){
        if (!isDisabled()){
            Rock.count--;
            super.disable();
        }
    }
}
