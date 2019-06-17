package com.gameSchiff;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.net.URL;

public class Missile extends GameObject {

    private int range = 35;     //max range
    private Explosion exp;      //explosion
    private Shape transformedMissile;   //missile
    private int bigBang;

    public Missile (Coordinate position, double size, double movingAngel, double movingDistance,int bigBang){
        super(position,size,size/2);

        this.bigBang = bigBang;
        setMovingAngel(movingAngel);
        setMovingDistance(movingDistance);
        setTransformedMissile();
        setExp();
    }

    private void setExp(){

        double missileCenterX = getObjectPosition().getX() + getWidth() * 0.5;
        double missileCenterY = getObjectPosition().getY() + getHeight() * 0.5;

        exp = new Explosion(new Coordinate(missileCenterX, missileCenterY), getWidth(),getHeight(), getMovingAngel(),bigBang);

    }

    private void setTransformedMissile(){
        AffineTransform transform = new AffineTransform();
        RoundRectangle2D missileShape = new RoundRectangle2D.Double(getObjectPosition().getX(),
                getObjectPosition().getY(),getWidth(), getHeight(), 3, 3);

        transform.rotate(getMovingAngel(),missileShape.getCenterX(),missileShape.getCenterY());
        transformedMissile = transform.createTransformedShape(missileShape);
    }
    public int getRange (){
        return range;
    }

    public void setRange (int range){
        this.range = range;
    }

    public void makeMove(){
        if (this.range > 0) super.makeMove();

        this.range--;
    }

    public void paintMe(Graphics g){

        Graphics2D g2d = (Graphics2D) g;
        if (!isDisabled()) {

            if (range > 1) {
                g2d.setColor(Color.BLACK);
                setTransformedMissile();
                g2d.fill(transformedMissile);
            }else {
                paintExplosion(g);
            }
        }
    }

    public void paintExplosion(Graphics g){
        if (!exp.explode(g,getObjectPosition(),getWidth(),getHeight())) this.disable();
    }

    public boolean touches(GameObject other){

        if (range > 1 && !other.isDisabled()){

            if (super.touches(other)) setRange(0);
                return super.touches(other);

        } else if(range < 1 && !other.isDisabled()){
            return exp.touches(other);
        }
        return false;
    }

}

class Explosion extends GameObject{

    private int expTime = 50;    //time
    private int whichExp;   //which explosion picture
    private URL imgURL;
    private ImageIcon explosion;
    private int bigBang;

    public Explosion(Coordinate position, double width,double height,double movingAngle,int bigBang){

        super(position,width,height);

        this.setMovingAngel(movingAngle);
        this.bigBang = bigBang;
        setImgURL();
    }

    public boolean explode(Graphics g, Coordinate position, double width, double height){

        if (expTime > 0) {
            setExpTime(expTime);
            setExplosionObjectPosition(position,width,height);
            paintMe(g);
            expTime--;
            return true;
        }
        return false;

    }

    public void paintMe(Graphics g){
            Graphics2D g2d = (Graphics2D) g;

            explosion.paintIcon(null, g2d, (int) getObjectPosition().getX(), (int) getObjectPosition().getY());
            expTime--;
    }

    public void setExplosionObjectPosition(Coordinate position,double width,double height){
        //rearranges the position of the explosion icons

        setHeight(height);
        setWidth(width);
        super.setObjectPosition(position);

        double rocketCenterX = getObjectPosition().getX() + getWidth()/2;
        double rocketCenterY = getObjectPosition().getY() + getHeight()/2;

        setWidth(explosion.getIconWidth());
        setHeight(explosion.getIconHeight());

        setObjectPosition(new Coordinate(rocketCenterX - explosion.getIconWidth()/2,rocketCenterY- explosion.getIconHeight()/2));
    }

    private void setImgURL(){
        imgURL = getClass().getResource(IMAGE_DIR +"explosion1.png");
    }

    public void setExpTime(int expTime){
        this.expTime = expTime;
        whichExp = (int) Math.ceil(expTime/10.0)-1; //whichExp = 0-4 , gets the number according to the expTime
        setExplosion();     //changes the img
    }

    public void setExplosion(){
        //gets the img according to the whichExp

        switch (whichExp){
            case 0: whichExp = 192 + (64*8); break;
            case 1: whichExp = 192 + (64*6); break;
            case 2: whichExp = 192 + (64*4); break;
            case 3: whichExp = 192 + (64*2); break;
            case 4: whichExp = 192 + (128*0); break;
            default: whichExp = 192 + (128*0); break;
        }

        try {
            BufferedImage source = ImageIO.read(imgURL);
            if (bigBang > 19) {
                explosion = new ImageIcon(source.getSubimage(whichExp, 0, 64, 64).getScaledInstance(1024, 1024, Image.SCALE_SMOOTH));
            }else if( bigBang >9){
                explosion = new ImageIcon(source.getSubimage(whichExp, 0, 64, 64).getScaledInstance(256, 256, Image.SCALE_SMOOTH));
            } else{
                explosion = new ImageIcon(source.getSubimage(whichExp, 0, 64, 64));
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

}