package com.gameSchiff;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;

public class Ship extends GameObject {

    public static final double TURNING_VELOCITY = 0.03;     //Turning velocity
    public static final int AMMO_LOADING_TIME = 50;         //Time it needs to reload the ammo
    public static final int AMMO_AMOUNT = 3;                //start ammo amount

    private final int ENERGY_START = 10;                    //Energy at the start

    private ImageIcon ship;                                 //only if i find an image
    private Shape transformedShip = new RoundRectangle2D.Double();  //the ship (only if i dont have an image)

    private double turningVelocity = TURNING_VELOCITY;

    private double deltaMovingAngel = 0;        //moving angel
    private int ammo = AMMO_AMOUNT;
    private int ammoLoadingTime = AMMO_LOADING_TIME;

    private int energy = ENERGY_START;

    public Ship (Coordinate position, double width, double height, double movingAngel, double movingDistance){

        super(position,width,height);

        setMovingAngel(movingAngel);
        setMovingDistance(movingDistance);
    }

    public Shape getTransformedShip() {
        return transformedShip;
    }

    public int getAmmo() {
        return ammo;
    }

    public void setAmmo(int ammo) {
        this.ammo = ammo;
    }

    public void setTransformedShip(Shape transformedShip) {
        this.transformedShip = transformedShip;
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public boolean isAbleToShoot() {
        return ammo > 0 && ammoLoadingTime <= 0;
    }

    public void turnShipRight(){
        deltaMovingAngel = turningVelocity;
    }

    public void turnShipLeft(){
        deltaMovingAngel = - turningVelocity;
    }

    public void stopTurningShip(){
        deltaMovingAngel = 0;
    }

    public void accelerateShip(){
        if (getMovingDistance() < 5.0) {
            setMovingDistance(getMovingDistance() + 1.0);
        }
    }

    public void decelerateShip(){
        if (getMovingDistance() > -5.0){
            setMovingDistance(getMovingDistance() - 0.5);
        }
    }

    public void makeMove(){
        double newMovingAngle = getMovingAngel() + deltaMovingAngel;
        if (newMovingAngle < 0 ) newMovingAngle = newMovingAngle + 2 * Math.PI;
        if (newMovingAngle > 2 * Math.PI) newMovingAngle = newMovingAngle - 2 * Math.PI;

        setMovingAngel(newMovingAngle);

        if (ammoLoadingTime > 0) ammoLoadingTime--;

        super.makeMove();
    }

    public Missile shoot(){ //shoots

        double shipCenterX = getObjectPosition().getX() + getWidth() * 0.5;
        double shipCenterY = getObjectPosition().getY() + getHeight() * 0.5;
        double cannonlength = getWidth()*0.5;

        double missileSize = getWidth() * 0.25;
        double missileAngle = getMovingAngel();

        Coordinate missileDirection = GameObject.polarToCartesianCoordinates(missileAngle);
        double shipEndX = missileDirection.getX() *cannonlength;
        double shipEndY = missileDirection.getY() * cannonlength;

        Coordinate missileStartPos = new Coordinate(shipCenterX +shipEndX - missileSize/2,shipCenterY + shipEndY - missileSize/6);

        Missile missile = new Missile(missileStartPos,missileSize,missileAngle,10,ammo);
        ammoLoadingTime = AMMO_LOADING_TIME;
        ammo = (ammo > 19 ? ammo-20 : (ammo > 9 ? ammo-9 : ammo-1));

        return missile;
    }

    public void paintMe(Graphics g){

        Graphics2D g2d = (Graphics2D) g;

        paintStatusBar(g2d);

        paintShip(g2d);
    }

    public void addAmmo(){
        ammo++;
    }

    public void paintShip (Graphics2D g2d){
        RoundRectangle2D ship = new RoundRectangle2D.Double(getObjectPosition().getX(),
                getObjectPosition().getY(),
                getWidth(), getHeight(), 15, 8);

        AffineTransform transform = new AffineTransform();
        transform.rotate(getMovingAngel(),ship.getCenterX(),ship.getCenterY());

        g2d.setColor(Color.red);
        Shape transformed = transform.createTransformedShape(ship);
        g2d.fill(transformed);

        setTransformedShip(transformed);
    }

    private void paintStatusBar(Graphics2D g2d) {

        double barOffsetY = getHeight()*0.6;

        // paint Tank Energy Bar
        g2d.setColor(Color.DARK_GRAY);
        RoundRectangle2D tankEnergyBarFrame  = new RoundRectangle2D.Double(Math.round(getObjectPosition().getX()) - 1,
                Math.round(getObjectPosition().getY() - barOffsetY) - 1,
                getWidth() + 1, 6, 0, 0);
        g2d.draw(tankEnergyBarFrame);
        if (getEnergy() > 3) {
            g2d.setColor(Color.GREEN);
        } else {
            g2d.setColor(Color.RED);
        }
        RoundRectangle2D tankEnergyBar  = new RoundRectangle2D.Double(Math.round(getObjectPosition().getX()),
                Math.round(getObjectPosition().getY() - barOffsetY),
                getWidth()/ENERGY_START*(energy), 5, 0, 0);
        g2d.fill(tankEnergyBar);

        // paint Ammo Loading Bar
        g2d.setColor(Color.DARK_GRAY);
        RoundRectangle2D ammoLoadingBar  = new RoundRectangle2D.Double(Math.round(getObjectPosition().getX()),
                Math.round(getObjectPosition().getY() - barOffsetY) - 5,
                getWidth()/AMMO_LOADING_TIME*(ammoLoadingTime), 2, 0, 0);
        if (!isAbleToShoot())   g2d.fill(ammoLoadingBar);

        //paint Ammo count
        g2d.setColor((ammo > 19 ? Color.RED : (ammo > 9 ? Color.ORANGE : Color.BLACK)));
        g2d.drawString(""+ammo,(int)getObjectPosition().getX() - 5,(int) (getObjectPosition().getY() - barOffsetY) - 5);
    }
}
