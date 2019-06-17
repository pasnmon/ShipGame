package com.gameSchiff;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GameWindow extends JFrame {

    private final GamePanel shipGamePanel;

    public GameWindow() {

        this.shipGamePanel = new GamePanel();

        this.registerWindowListener();
        this.createMenu();

        this.add(shipGamePanel);
        this.pack();

        this.setTitle("Ship");
        this.setLocation(10,10);
        this.setResizable(false);

        this.setVisible(true);

    }

    private void createMenu(){
        //adds the menu bar

        JMenuBar menuBar = new JMenuBar();
        this.setJMenuBar(menuBar);

        menuBar.add(new JMenu("File"));
        menuBar.add(new JMenu("Game"));

        addFileMenuItems(menuBar.getMenu(0));
        addGameMenuItems(menuBar.getMenu(1));
    }

    private void addFileMenuItems(JMenu fileMenu){
        //adds the menu items

        JMenuItem quitItem = new JMenuItem("Quit");
        quitItem.addActionListener( e -> System.exit(0));
        fileMenu.add(quitItem);
    }

    private void addGameMenuItems(JMenu gameMenu){
        //adds the menu items

        JMenuItem pauseItem = new JMenuItem("Pause");
        gameMenu.add(pauseItem);
        pauseItem.addActionListener( e -> shipGamePanel.pauseGame());

        JMenuItem continueItem = new JMenuItem("Continue");
        gameMenu.add(continueItem);
        continueItem.addActionListener(e -> shipGamePanel.continueGame() );

        gameMenu.addSeparator();

        JMenuItem restartItem = new JMenuItem("Restart");
        gameMenu.add(restartItem);
        restartItem.addActionListener(e -> shipGamePanel.restartGame());

    }

    private void registerWindowListener(){
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
                shipGamePanel.pauseGame();
            }

            @Override
            public void windowActivated(WindowEvent e) {
                shipGamePanel.continueGame();
            }
        });
    }
}
