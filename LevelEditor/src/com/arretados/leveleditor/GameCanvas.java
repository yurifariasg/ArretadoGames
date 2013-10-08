/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.arretados.leveleditor;

import com.arretados.leveleditor.entities.Box;
import com.arretados.leveleditor.entities.BoxPanel;
import com.arretados.leveleditor.entities.Breakable;
import com.arretados.leveleditor.entities.BreakablePanel;
import com.arretados.leveleditor.entities.Coin;
import com.arretados.leveleditor.entities.CoinPanel;
import com.arretados.leveleditor.entities.Entity;
import com.arretados.leveleditor.entities.Flag;
import com.arretados.leveleditor.entities.Fluid;
import com.arretados.leveleditor.entities.FluidPanel;
import com.arretados.leveleditor.entities.OneWayWall;
import com.arretados.leveleditor.entities.OneWayWallPanel;
import com.arretados.leveleditor.entities.Player;
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
    
    public static final float METER_TO_PIXELS = 50f; // px = 1meter
    
    private LevelEditorView mainView;
    private short playersAdded = 0;
    
    private List<Entity> entities = new ArrayList<Entity>();
    private List<int[]> groundPos = new ArrayList<int[]>();
    private Entity selectedEntity = null;
    private Flag flag;
    
    private DrawMode insertionMode = null;

    public GameCanvas() {
        addMouseListener(this);
        addMouseMotionListener(this);
    }
    
    public void setMainView(LevelEditorView mainView) {
        this.mainView = mainView;
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(Color.white);
        g.fillRect(0, 0, getWidth(), getHeight());
        
        if (selectedEntity != null)
            drawSelection(selectedEntity);

        for (int i = 0; i < entities.size(); i++)
            entities.get(i).drawMyself(g);
        
        if (flag != null)
            flag.drawMyself(g);
        
        drawGround(g);
    }
    
    public void drawGroundLine(int x,int y){
        int lastPointX = 0;
        
        if (groundPos.size() > 0)
            lastPointX = groundPos.get(groundPos.size()-1)[0];  //Gets the last point
        else
            groundPos.add(new int[]{0, y});
                
        if (x >= lastPointX)  //Verify if the point that will be created comes after the last point
            groundPos.add(new int[]{x, y}); 

        repaint();
    }
    
    private void drawFlag(int x, int y) {
        flag = new Flag(x,y,10);
        repaint();
    }
    
    public Flag getFlag(){
        return flag;
    }
    
    public List<int[]> getLinesPos(){
        return groundPos;
    }
    
    public List<Entity> getEntitiesPos() {
        return entities;
    }
    
    public void clearObjectsList(){
        this.groundPos.clear();
        this.entities.clear();
        this.flag = null;
    }
    
    private Entity checkClickOn(int x, int y) {
        
        for (int i = 0; i < entities.size() ; i++) {
            if (entities.get(i).collides(x, y))
                return entities.get(i);
        }
        
        return null;
    }

    public void mouseClicked(MouseEvent e) {
        
        Entity clickedEntity = checkClickOn(e.getX(), e.getY());
        
        if (clickedEntity != null) {
            
            selectedEntity = clickedEntity;
            mainView.switchEntityPanel(selectedEntity.getEntityPanel());
            mainView.getEntityPanel().setEntity(selectedEntity);
            
            return ;
            
        }
        
        if (insertionMode != null) {
            // Inserts..
            
            Entity entityToAdd = null;
            switch (insertionMode) {
                case BOX:
                    float sizeBox = ((BoxPanel)mainView.getEntityPanel()).getCurrentSize();
                    float weightBox = ((BoxPanel)mainView.getEntityPanel()).getCurrentWeight(); // TODO: set Weight
                    entityToAdd = new Box(e.getX(), e.getY(), sizeBox);
                    ((Box)entityToAdd).setWeight(weightBox);
                break;

                case FRUIT:
                    //drawApple(e.getX(), e.getY());
                break;

                case COIN:
                    float coinValue = ((CoinPanel)mainView.getEntityPanel()).getCurrentValue();
                    entityToAdd = new Coin(e.getX(), e.getY());
                    ((Coin)entityToAdd).setValue((int) coinValue);
                break;

                case ONEWAY_WALL:
                    float owwWidth = ((OneWayWallPanel)mainView.getEntityPanel()).getCurrentWidth();
                    float owwHeight = ((OneWayWallPanel)mainView.getEntityPanel()).getCurrentHeight();
                    entityToAdd = new OneWayWall(e.getX(), e.getY(), owwWidth, owwHeight);
                break;

                case PULLEY:
                    //drawPulley(e.getX(), e.getY(), 100);
                break;

                case FLUID:
                    float fluidWidth = ((FluidPanel)mainView.getEntityPanel()).getCurrentWidth();
                    float fluidHeight = ((FluidPanel)mainView.getEntityPanel()).getCurrentHeight();
                    float fluidDensity = ((FluidPanel)mainView.getEntityPanel()).getCurrentDensity();
                    entityToAdd = new Fluid(e.getX(), e.getY());
                    ((Fluid)entityToAdd).setWidth(fluidWidth);
                    ((Fluid)entityToAdd).setHeight(fluidHeight);
                    ((Fluid)entityToAdd).setDensity(fluidDensity);
                break;

                case BREAKABLE:
                    float widthBreakable = ((BreakablePanel)mainView.getEntityPanel()).getCurrentWidth();
                    float heightBreakable = ((BreakablePanel)mainView.getEntityPanel()).getCurrentHeight();
                    float hitUntilBreak = ((BreakablePanel)mainView.getEntityPanel()).getCurrentHitsUntilBreak();
                    entityToAdd = new Breakable(e.getX(), e.getY());
                    ((Breakable)entityToAdd).setWidth(widthBreakable);
                    ((Breakable)entityToAdd).setHeight(heightBreakable);
                    ((Breakable)entityToAdd).setHitsUntilBreak(hitUntilBreak);
                break;

                case LIANA:
                    //drawLiana(e.getX(), e.getY(), e.getX(), e.getY());
                break;

                case GROUND:
                    drawGroundLine(e.getX(), e.getY());
                break;

                case PLAYER:
                    //drawPlayer(e.getX(), e.getY());
                    if (playersAdded == 0) {
                        entityToAdd = new Player(e.getX(), e.getY(), "player1");
                        playersAdded++;
                    } else if (playersAdded == 1) {
                        entityToAdd = new Player(e.getX(), e.getY(), "player2");
                        playersAdded++;
                    }
                break;

                case FLAG:
                    drawFlag(e.getX(), e.getY());
                break;
            }
            
            if (entityToAdd != null) {
                entities.add(entityToAdd);
                if (mainView.getEntityPanel() != null)
                    mainView.getEntityPanel().setEntity(entityToAdd);
            }
            repaint();
            
        }
    }
    
    public void mousePressed(MouseEvent e) {
        selectedEntity = checkClickOn(e.getX(), e.getY());
    }
    
    public void mouseReleased(MouseEvent e) { }
    
    public void mouseEntered(MouseEvent e) { }

    public void mouseExited(MouseEvent e) { }

    public void mouseDragged(MouseEvent e) {
        if (selectedEntity != null) {
            selectedEntity.setX(e.getX());
            selectedEntity.setY(e.getY());
            repaint();
        }
        
        /*switch (mode) {
            
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
                
            case LIANA:
                lianaPos.get(lianaPos.size()-1).setX1(e.getX());
                lianaPos.get(lianaPos.size()-1).setY1(e.getY());
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
        }*/
        
    }

    public void mouseMoved(MouseEvent e) {
    }

    private void drawGround(Graphics g) {
        if (groundPos.size() < 2)
            return;
        
        int[] xPos = new int[groundPos.size() + 2];
        int[] yPos = new int[groundPos.size() + 2];
        
        int highestY = Integer.MIN_VALUE;
        
        for (int i = 0; i < groundPos.size(); i++){
            int posX = groundPos.get(i)[0];
            int posY = groundPos.get(i)[1];
            
            xPos[i + 1] = posX;
            yPos[i + 1] = posY;
            
            if (posY > highestY)
                highestY = posY;
        }
        
        xPos[0] = xPos[1];
        yPos[0] = highestY + 1000; // Draw the ground 1000 pixels below the lowest
        
        xPos[xPos.length - 1] = xPos[xPos.length - 2];
        yPos[yPos.length - 1] = highestY + 1000; // Draw the ground 1000 pixels below the lowest
        
        g.setColor(new Color(153, 76, 0));
        g.fillPolygon(xPos, yPos, xPos.length);
    }

    private void drawSelection(Entity selectedEntity) {
    }

    public void changeMode(DrawMode drawMode) {
        insertionMode = drawMode;
        
    }

}


