package com.gameSchiff;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import static java.awt.event.KeyEvent.*;

public class GamePanel extends JPanel {

    private final int RESPAWN_ROCKS_CD = 75;        //Rock respawn time
    private final int RESPAWN_CRATES_CD = 50;       //Crate respawn time
    private final int MAX_ROCKS = 15;               //max Rocks
    private final int MAX_CRATES = 5;               //max Crates

    public static final String IMAGE_DIR = "images/";
    private final String[] backgroundImages = new String[] {"water.gif"};

    private final Dimension prefSize = new Dimension(1180,780);

    private Ship testShip;
    private ArrayList<GameObject> gameObjects = new ArrayList<>();
    private int maxRocks = MAX_ROCKS;
    private int maxCrate = MAX_CRATES;
    private int rockRespawn = RESPAWN_ROCKS_CD;
    private int crateRespawn = RESPAWN_CRATES_CD;

    private ImageIcon backgroundImage;

    private boolean gameOver = false;
    private int cratesCollected = 0;
    private int rocksDestroyed = 0;

    private Timer t;

    public GamePanel(){
        setFocusable(true);
        setPreferredSize(prefSize);
        setSize(prefSize);

        initGame();
        startGame();
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    private void initGame(){
        setBackgroundImage();
        createGameObjects();

        initShip();

        t = new Timer(20, e -> doOnTick());   //e -> doOnTick creates an actionlistener (lambda)

        addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e){

                switch (e.getKeyCode()){

                    case VK_SPACE:
                        if (testShip.isAbleToShoot()) gameObjects.add(testShip.shoot());
                        break;

                    case VK_A:
                    case VK_D:
                    case VK_LEFT :
                    case VK_RIGHT: testShip.stopTurningShip(); break;
                    case VK_W:
                    case VK_UP: testShip.accelerateShip();break;
                    case VK_S:
                    case VK_DOWN : testShip.decelerateShip();break;
                    case VK_Q : testShip.setAmmo(50);break;
                }
            }

            public void keyPressed(KeyEvent e){
                switch (e.getKeyCode()){
                    case VK_A:
                    case VK_LEFT : testShip.turnShipLeft(); break;
                    case VK_D:
                    case VK_RIGHT : testShip.turnShipRight(); break;
                }
            }
        });
    }

    private void createGameObjects(){
        //creates game objects
        for (int i = 0; i < maxRocks; i++){
            createRock();

        }
        for (int i = 0; i< maxCrate ;i++){
            createCrate();
        }
    }

    private Coordinate getRndCoordinate(double height, double width){   //generates rnd Coordinates

        int rndWidth;
        int rndHeight;
        do{
            rndWidth = (int)ThreadLocalRandom.current().nextDouble(width,(getWidth() != 0 ? getWidth() : prefSize.getWidth()));
            rndHeight =(int)ThreadLocalRandom.current().nextDouble(height, (getHeight() != 0 ? getHeight() : prefSize.getHeight()));
        }while (rndWidth+width > getWidth() || rndHeight+height > getHeight());

        return new Coordinate(rndWidth,rndHeight);
    }

    private void initShip(){
        //inits the players ship. Cant spawn on objects.
        boolean possibleStart = false;
        do {

            testShip = new Ship(getRndCoordinate(70, 45), 70, 45, Math.
                    toRadians(ThreadLocalRandom.current().nextInt(0,360)), 0);
            possibleStart = true;

            for (int i = 0; i < gameObjects.size();i++){
                if (testShip.touches(gameObjects.get(i))) possibleStart = false;
            }

        }while (!possibleStart);
    }

    private void setBackgroundImage(int imgNumber){
        String imgPath = IMAGE_DIR + backgroundImages[imgNumber];
        URL imageUrl = getClass().getResource(imgPath);
        backgroundImage = new ImageIcon(imageUrl);
    }

    private void setBackgroundImage(){
        setBackgroundImage(0);
    }

    private void startGame(){
        t.start();
    }

    public void pauseGame(){
        t.stop();
        repaint();
    }

    private boolean isPaused(){
        return !t.isRunning();
    }

    public void continueGame(){
        if (!isGameOver()) t.start();
    }

    public void restartGame(){
        cratesCollected = 0;
        rocksDestroyed = 0;
        setGameOver(false);
        deleteArrayList();
        createGameObjects();
        initShip();
        startGame();
    }

    private void endGame(){
        setGameOver(true);
        pauseGame();
    }

    private void doOnTick(){

        moveShip();
        moveMissile();
        addGameObject();

        checkForShipCollision();
        checkForMissileCollision();
        clearArrayList();

        if (testShip.getEnergy() < 1) endGame();

        repaint();

    }

    private void checkForShipCollision(){       //Checks if the ship collided with a crate/Rock

        gameObjects.forEach(y ->{
            if (y instanceof Crate || y instanceof Rock){

                if (y instanceof Crate && testShip.touches(y) && !y.isDisabled())  {
                    testShip.addAmmo();
                    cratesCollected++;
                }

                if (y instanceof Rock && testShip.touches(y) && !y.isDisabled())   testShip.setEnergy(testShip.getEnergy()-1);

                if (testShip.touches(y)) y.disable();
            }
        });

    }

    private void checkForMissileCollision(){    //Checks if the Missile collided with an GameObject

        gameObjects.forEach(x ->{
           if (x instanceof Missile){
               gameObjects.forEach(y -> {
                   if (y instanceof GameObject && !(y instanceof Missile)){
                       if (x.touches(y)) {
                           if (y instanceof Rock) rocksDestroyed++;
                           y.disable();
                       }
                   }
               });
           }
        });

    }

    private void clearArrayList (){         //deletes the disabled GameObjects
        for (int i = 0 ; i <gameObjects.size();i++){
            if (gameObjects.get(i).isDisabled()) gameObjects.remove(i);
        }
    }

    private void deleteArrayList(){         //deletes all GameObjects
        for (int i = 0 ; i <gameObjects.size();i++){
            gameObjects.remove(i);
        }
    }

    private void addGameObject (){      //adds Rocks/Crates with coordinates that dont touch the ship

        rockRespawn--;
        crateRespawn--;

        if (Rock.count < MAX_ROCKS && rockRespawn <1){
            createRock();
        }

        if (Crate.count < MAX_CRATES && crateRespawn < 1){
            createCrate();
        }
        rockRespawn = (rockRespawn <= 0) ? RESPAWN_ROCKS_CD : rockRespawn;
        crateRespawn = (crateRespawn <= 0) ? RESPAWN_CRATES_CD : crateRespawn;
    }

    private void createRock(){
        Rock tempRock;
        do{
            tempRock = new Rock(getRndCoordinate(Rock.height,Rock.width));
        }while ((testShip != null && testShip.touches(tempRock)) || (tempRock != null && !checkForUniqueSpawn(tempRock)));
        gameObjects.add(tempRock);
        Rock.count++;
    }

    private void createCrate(){
        Crate tempCrate;
        do {
            tempCrate = new Crate(getRndCoordinate(Crate.height,Crate.width));
        }while ( (testShip != null && testShip.touches(tempCrate)) || !checkForUniqueSpawn(tempCrate));
        gameObjects.add(tempCrate);
        Crate.count++;
    }

    private boolean checkForUniqueSpawn(GameObject y){
        for (GameObject x: gameObjects) {
            if (y.touches(x)) return false;
        }
        return true;
    }

    private void moveShip(){    //moves the Ship

        testShip.makeMove();
        shipOutOfField();
    }

    private void shipOutOfField(){      //If the ship exit's the field -> move it to the opposite side

        if (getParent().getHeight() > 0 && getParent().getWidth() > 0 ) {

            if (testShip.getObjectPosition().getX() > getParent().getWidth())
                testShip.setObjectPosition(new Coordinate(0 - testShip.getWidth(), testShip.getObjectPosition().getY()));

            if (testShip.getObjectPosition().getY() > getParent().getHeight())
                testShip.setObjectPosition(new Coordinate(testShip.getObjectPosition().getX(), 0 - testShip.getHeight()));

            if (testShip.getObjectPosition().getX() < 0 - testShip.getWidth())
                testShip.setObjectPosition(new Coordinate(getParent().getWidth(), testShip.getObjectPosition().getY()));

            if (testShip.getObjectPosition().getY() < 0 - testShip.getHeight())
                testShip.setObjectPosition(new Coordinate(testShip.getObjectPosition().getX(), getParent().getHeight()));

        }
    }

    private void moveMissile(){     //moves all missiles

        for (GameObject x: gameObjects) {
            x.makeMove();
        }
    }

    public void paintComponent (Graphics g){
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        paintBackground(g2d);
        paintPoints(g2d);
        paintGameObjects(g2d);
        paintGameOverOrPaused(g2d);
    }

    private void paintBackground(Graphics2D g){
        //Paints the background
        for (int y = 0; y< getParent().getHeight(); y+= backgroundImage.getIconHeight()){
            for (int x = 0; x < getParent().getWidth() ; x+= backgroundImage.getIconWidth()){
                backgroundImage.paintIcon(null,g,x,y);
            }
        }
    }

    private void paintPoints(Graphics2D g){
        //Paints Crates collected
        g.setFont(new Font(Font.MONOSPACED,Font.BOLD,19));
        g.setColor(Color.BLACK);
        g.drawString("Points: " + ((cratesCollected*5)+(rocksDestroyed*3)),22,prefSize.height-5);
    }

    private void paintGameObjects(Graphics2D g){
        testShip.paintMe(g);    //paints the ship
        for (GameObject x: gameObjects) {       //paints all GameObjects
            x.paintMe(g);
        }
    }

    private void paintGameOverOrPaused(Graphics2D g){
        if (isGameOver()){  //Paints Game Over
            paintBackground(g);
            g.setFont(new Font(Font.MONOSPACED,Font.BOLD,50));
            g.setColor(Color.red);
            g.drawString("GAME OVER!",prefSize.width / 2 - 130,prefSize.height/5);
        }else if (this.isPaused()){ //Paints Paused
            paintBackground(g);
            g.setFont(new Font(Font.MONOSPACED,Font.BOLD,50));
            g.setColor(Color.BLACK);
            g.drawString("GAME PAUSED!",prefSize.width / 2 - 130,prefSize.height/5);
        }
    }
}
