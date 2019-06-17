package com.gameSchiff;

import java.awt.*;

public abstract class GameObject {

    public static final String IMAGE_DIR = "images/";
    private Coordinate objectPosition;
    private double width;
    private double height;
    private double movingAngel;
    private double movingDistance;
    private boolean disable = false;

    public GameObject (Coordinate objectPosition, double width, double height){
        this.objectPosition = objectPosition;
        this.width = width;
        this.height = height;
        this.movingAngel = 0;
        this.movingDistance = 0;
    }


    public GameObject (Coordinate objectPosition){
        this.objectPosition = objectPosition;
    }

    public Coordinate getObjectPosition() {
        return objectPosition;
    }


    public void setObjectPosition(Coordinate objectPosition) {
        this.objectPosition = objectPosition;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getMovingAngel() {
        return movingAngel;
    }

    public void setMovingAngel(double movingAngel) {
        this.movingAngel = movingAngel;
    }

    public double getMovingDistance() {
        return movingDistance;
    }

    public void setMovingDistance(double movingDistance) {
        this.movingDistance = movingDistance;
    }

    public boolean touches(GameObject go){
        if (this.isLeftOf(go))      return false;
        if (go.isLeftOf(this))  return false;
        if (this.isAbove(go))       return false;
        if (go.isAbove(this))   return false;

        return true;
    }

    public boolean isLeftOf(GameObject go){
        return this.getObjectPosition().getX() + this.getWidth() < go.getObjectPosition().getX();
    }
    public boolean isAbove(GameObject go){
        return this.getObjectPosition().getY() + this.getHeight() < go.getObjectPosition().getY();
    }

    public static Coordinate polarToCartesianCoordinates(double angel){

        double x = Math.cos(angel);
        double y = Math.sin(angel);

        return new Coordinate(x,y);
    }

    public void moveGameObject(){

        Coordinate direction = polarToCartesianCoordinates(this.movingAngel);

        objectPosition.setX(objectPosition.getX() + direction.getX() * movingDistance);
        objectPosition.setY(objectPosition.getY() + direction.getY() * movingDistance);
    }

    public void makeMove(){
        moveGameObject();
    }

    public boolean isDisabled() {
        return disable;
    }

    public void disable() {
            this.disable = true;
    }

    protected abstract void paintMe(Graphics g);
}
