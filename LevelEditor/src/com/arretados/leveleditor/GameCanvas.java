/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.arretados.leveleditor;

import com.arretados.leveleditor.entities.Box;
import com.arretados.leveleditor.entities.Breakable;
import com.arretados.leveleditor.entities.Coin;
import com.arretados.leveleditor.entities.Flag;
import com.arretados.leveleditor.entities.Fluid;
import com.arretados.leveleditor.entities.Fruit;
import com.arretados.leveleditor.entities.OneWayWall;
import com.arretados.leveleditor.entities.Player;
import com.arretados.leveleditor.entities.Pulley;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;

/**
 *
 * @author bruno
 */
public class GameCanvas extends JPanel implements MouseMotionListener, MouseListener{
    
    private List<Box> boxPos = new ArrayList<Box>();
    private List<Fruit> fruitPos = new ArrayList<Fruit>();
    private List<Coin> coinPos = new ArrayList<Coin>();
    private List<OneWayWall> oneWayPos = new ArrayList<OneWayWall>();
    private List<Pulley> pulleyPos = new ArrayList<Pulley>();
    private List<Fluid> fluidPos = new ArrayList<Fluid>();
    private List<Breakable> breakablePos = new ArrayList<Breakable>();
    private List<Player> playerPos = new ArrayList<Player>();
    private List<int[]> groundPos = new ArrayList<int[]>();
    public DrawMode mode = DrawMode.BOX;
    private Flag flag;

    public GameCanvas() {
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(Color.white);
        g.fillRect(0, 0, getWidth(), getHeight());

        for (int i = 0; i < playerPos.size(); i++)
            playerPos.get(i).drawMyself(g);
        
        for (int i = 0; i < boxPos.size(); i++)
            boxPos.get(i).drawMyself(g);

        for (int i = 0; i < fruitPos.size(); i++)
            fruitPos.get(i).drawMyself(g);
        
        for (int i = 0; i < oneWayPos.size(); i++)
            oneWayPos.get(i).drawMyself(g);
        
        for (int i = 0; i < pulleyPos.size(); i++)
            pulleyPos.get(i).drawMyself(g);
        
        for (int i = 0; i < fluidPos.size(); i++)
            fluidPos.get(i).drawMyself(g);
        
        for (int i = 0; i < breakablePos.size(); i++)
            breakablePos.get(i).drawMyself(g);

        for (int i = 0; i < coinPos.size(); i++)
            coinPos.get(i).drawMyself(g);
        
        if (flag != null)
            flag.drawMyself(g);

        int posX,posY;
        posX = 0;
        posY = 0;
        g.setColor(Color.green);
        for (int i = 0; i < groundPos.size(); i++){
            posX = groundPos.get(i)[0];
            posY = groundPos.get(i)[1];
            if (i == 0)
                g.drawLine(0, 950, posX, posY);
            else
                g.drawLine( groundPos.get(i-1)[0], groundPos.get(i-1)[1], posX, posY);
        }
    }
    
    public void drawPlayer(int x, int y){
        if (playerPos.isEmpty()){
            playerPos.add(new Player(x, y, 50, "player1"));
        }else if (playerPos.size() == 1){
            playerPos.add(new Player(x, y, 50, "player2"));
        }
        repaint();
    }

    public void drawBox(int x, int y, int size){
        boxPos.add(new Box(x, y, size));
        repaint();
    }
    
    public void drawApple(int x, int y){
        fruitPos.add(new Fruit(x, y, 25));
        repaint();
    }
    
    public void drawCoin(int x, int y){
        coinPos.add(new Coin(x, y, 25));
        repaint();
    }
    
    public void drawOneWay(int x, int y){
        oneWayPos.add(new OneWayWall(x, y, 300));
        repaint();
    }
    
    public void drawPulley(int x, int y, int size){
        pulleyPos.add(new Pulley(x, y, size));
        repaint();
    }
    
    public void drawFluid(int x, int y, int size){
        fluidPos.add(new Fluid(x, y, size));
        repaint();
    }
    
    public void drawBreakable(int x, int y){
        breakablePos.add(new Breakable(x, y));
        repaint();
    }
    
    public void drawGroundLine(int x,int y){
        int lastPointX = 0;
        
        if (groundPos.size() > 0) {
            lastPointX = groundPos.get(groundPos.size()-1)[0];  //Gets the last point
        }else{
            groundPos.add(new int[]{0, y});
        }
        
        if (x >= lastPointX){  //Verify if the point that will be created comes after the last point
            groundPos.add(new int[]{x, y}); 
        }
        repaint();
    }
    
    private void drawFlag(int x, int y) {
        flag = new Flag(x,y,10);
        repaint();
    }
    
    public Flag getFlag(){
        return flag;
    }
    
    public List<Box> getBoxPos(){
        return boxPos;
    }
    
    public List<Fruit> getFruitPos(){
        return fruitPos;
    }
    
    public List<Coin> getCoinsPos(){
        return coinPos;
    }
    
    public List<OneWayWall> getOneWayPos(){
        return oneWayPos;
    }
    
    public List<Pulley> getPulleyPos(){
        return pulleyPos;
    }
    
    public List<Fluid> getFluidPos(){
        return fluidPos;
    }
    
    public List<Breakable> getBreakablePos(){
        return breakablePos;
    }
    
    public List<int[]> getLinesPos(){
        return groundPos;
    }
    
    public List<Player> getPlayersPos(){
        return playerPos;
    }
    
    public void clearObjectsList(){
        this.groundPos = new ArrayList<int[]>();
        this.fruitPos = new ArrayList<Fruit>();
        this.coinPos = new ArrayList<Coin>();
        this.pulleyPos = new ArrayList<Pulley>();
        this.oneWayPos = new ArrayList<OneWayWall>();
        this.fluidPos = new ArrayList<Fluid>();
        this.breakablePos = new ArrayList<Breakable>();
        this.boxPos = new ArrayList<Box>();
        this.playerPos = new ArrayList<Player>();
        this.flag = null;
    }

    public void mouseClicked(MouseEvent e) {
        
        switch (mode) {
            
            case BOX:
                drawBox(e.getX(), e.getY(), 60);
            break;

            case FRUIT:
                drawApple(e.getX(), e.getY());
            break;
                
            case COIN:
                drawCoin(e.getX(), e.getY());
            break;
                
            case ONEWAY_WALL:
                drawOneWay(e.getX(), e.getY());
            break;
                
            case PULLEY:
                drawPulley(e.getX(), e.getY(), 100);
            break;
                
            case FLUID:
                drawFluid(e.getX(), e.getY(), 100);
            break;
                
            case BREAKABLE:
                drawBreakable(e.getX(), e.getY());
            break;

            case LINE:
                drawGroundLine(e.getX(), e.getY());
            break;
                
            case PLAYER:
                drawPlayer(e.getX(), e.getY());
            break;
                
            case FLAG:
                drawFlag(e.getX(), e.getY());
            break;

            default: 
                System.out.println("BUG");
            break;            
        }
    }
    
    public void mousePressed(MouseEvent e) { }
    
    public void mouseReleased(MouseEvent e) { }
    
    public void mouseEntered(MouseEvent e) { }

    public void mouseExited(MouseEvent e) { }

    public void mouseDragged(MouseEvent e) {
        switch (mode) {
            
            case BOX:
                boxPos.get(boxPos.size()-1).setX(e.getX());
                boxPos.get(boxPos.size()-1).setY(e.getY());
                repaint();
            break;

            case FRUIT:
                fruitPos.get(fruitPos.size()-1).setX(e.getX());
                fruitPos.get(fruitPos.size()-1).setY(e.getY());
                repaint();
            break;
                
            case COIN:
                coinPos.get(coinPos.size()-1).setX(e.getX());
                coinPos.get(coinPos.size()-1).setY(e.getY());
                repaint();
            break;
                
            case ONEWAY_WALL:
                oneWayPos.get(oneWayPos.size()-1).setX(e.getX());
                oneWayPos.get(oneWayPos.size()-1).setY(e.getY());
                repaint();
            break;
                
            case PULLEY:
                pulleyPos.get(pulleyPos.size()-1).setX(e.getX());
                pulleyPos.get(pulleyPos.size()-1).setY(e.getY());
                repaint();
            break;
            
            case FLUID:
                fluidPos.get(fluidPos.size()-1).setX(e.getX());
                fluidPos.get(fluidPos.size()-1).setY(e.getY());
                repaint();
            break;
                
            case BREAKABLE:
                breakablePos.get(breakablePos.size()-1).setX(e.getX());
                breakablePos.get(breakablePos.size()-1).setY(e.getY());
                repaint();
            break;
                
            case PLAYER:
                playerPos.get(playerPos.size()-1).setX(e.getX());
                playerPos.get(playerPos.size()-1).setY(e.getY());
                repaint();
            break;
                
            case FLAG:
                if (this.flag != null){
                    flag.setX(e.getX());
                    flag.setY(e.getY());
                    repaint();
                }
            break;

            default: 
                System.out.println("BUG");
            break;            
        }
        
    }

    public void mouseMoved(MouseEvent e) {
    }

}


