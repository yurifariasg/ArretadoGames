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
import com.arretados.leveleditor.entities.Hole;
import com.arretados.leveleditor.entities.OneWayWall;
import com.arretados.leveleditor.entities.OneWayWallPanel;
import com.arretados.leveleditor.entities.Player;
import com.arretados.leveleditor.entities.Spike;
import com.arretados.leveleditor.entities.SpikePanel;
import com.arretados.leveleditor.entities.layer.Grass;
import com.arretados.leveleditor.entities.layer.GrassPanel;
import com.arretados.leveleditor.entities.layer.Shrub;
import com.arretados.leveleditor.entities.layer.ShrubPanel;
import com.arretados.leveleditor.entities.layer.Tree;
import com.arretados.leveleditor.entities.layer.TreePanel;
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
    private int groundHeight = 50;
    
    private List<Entity> entities = new ArrayList<Entity>();
    private List<int[]> groundPos = new ArrayList<int[]>();
    private Entity selectedEntity = null;
    private Flag flag;
    
    private DrawMode insertionMode = null;

    public GameCanvas() {
        addMouseListener(this);
        addMouseMotionListener(this);
    }
    
    public void setFlag(Flag f) {
        this.flag = f;
    }
    
    public void setMainView(LevelEditorView mainView) {
        this.mainView = mainView;
    }
    
    // Created to LevelLoader
    public void addEntities(Entity e){
        entities.add(e);
    }

    public int getGroundHeight() {
        return groundHeight;
    }

    public void setGroundHeight(int groundHeight) {
        this.groundHeight = groundHeight;
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(Color.white);
        g.fillRect(0, 0, getWidth(), getHeight());
        
        if (selectedEntity != null)
            drawSelection(selectedEntity);
        
        drawGround(g);
        
        for (Entity e : entities) {
            e.drawMyself(g);
        }
        
        if (flag != null) {
            flag.drawMyself(g);
        }
    }
    
    public void drawGroundLine(int x,int y){
        groundPos.add(new int[]{0, 1000});
        groundPos.add(new int[]{100000000, 1000});
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
        
        if (flag != null && flag.collides(x, y)) {
            return flag;
        }
        
        for (int i = 0; i < entities.size() ; i++) {
            if (entities.get(i).collides(x, y))
                return entities.get(i);
        }
        
        return null;
    }
    
    public void switchEntityPanelToSelectedEntity() {
        mainView.switchEntityPanel(selectedEntity.getEntityPanel());
        if (mainView.getEntityPanel() != null) {
            mainView.getEntityPanel().setEntity(selectedEntity);
        }
    }

    public void mouseClicked(MouseEvent e) {
        
        Entity clickedEntity = checkClickOn(e.getX(), e.getY());
        
        if (clickedEntity != null) {
            
            selectedEntity = clickedEntity;
            switchEntityPanelToSelectedEntity();
            
            return ;
            
        }
        
        if (insertionMode != null) {
            // Inserts..
            
            Entity entityToAdd = null;
            switch (insertionMode) {
                case BOX:
                    float sizeBox = ((BoxPanel)mainView.getEntityPanel()).getCurrentSize();
                    float weightBox = ((BoxPanel)mainView.getEntityPanel()).getCurrentWeight();
                    entityToAdd = new Box(e.getX(), e.getY(), sizeBox);
                    ((Box)entityToAdd).setWeight(weightBox);
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
                    
                case TREE:
                    float treeType = ((TreePanel)mainView.getEntityPanel()).getCurrentTreeType();
                    entityToAdd = new Tree(e.getX(), e.getY());
                    ((Tree)entityToAdd).setTreeType((int) treeType);
                    break;
                case GRASS:
                    float grassType = ((GrassPanel)mainView.getEntityPanel()).getCurrentGrassType();
                    entityToAdd = new Grass(e.getX(), e.getY());
                    ((Grass)entityToAdd).setGrassType((int) grassType);
                    break;
                case SHRUB:
                    float shrubType = ((ShrubPanel)mainView.getEntityPanel()).getCurrentShrubType();
                    entityToAdd = new Shrub(e.getX(), e.getY());
                    ((Shrub)entityToAdd).setShrubType((int) shrubType);
                    break;
                    
                case SPIKE:
                    float sizeSpike = ((SpikePanel)mainView.getEntityPanel()).getCurrentSize();
                    float weightSpike = ((SpikePanel)mainView.getEntityPanel()).getCurrentWeight(); // TODO: set Weight
                    entityToAdd = new Spike(e.getX(), e.getY(), sizeSpike);
                    ((Spike)entityToAdd).setWeight(weightSpike);

                case LIANA:
                    //drawLiana(e.getX(), e.getY(), e.getX(), e.getY());
                break;

                case PLAYER:
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
                case HOLE:
                    entityToAdd = new Hole(e.getX(), e.getY());
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
        if (selectedEntity != null)
            switchEntityPanelToSelectedEntity();
    }
    
    public void mouseReleased(MouseEvent e) { }
    
    public void mouseEntered(MouseEvent e) { }

    public void mouseExited(MouseEvent e) { }

    public void mouseDragged(MouseEvent e) {
        if (selectedEntity != null) {
            selectedEntity.setX(e.getX());
            selectedEntity.setY(e.getY());
            repaint();
            validate();
        }
    }

    public void mouseMoved(MouseEvent e) {
    }

    private void drawGround(Graphics g) {        
        g.setColor(new Color(153, 76, 0));
        
        g.fillPolygon(
                new int[] {0, 0, getWidth(), getWidth()},
                new int[] {getHeight(), getHeight() - groundHeight, getHeight() - groundHeight, getHeight()}, 4);
        
    }

    private void drawSelection(Entity selectedEntity) {
    }

    public void changeMode(DrawMode drawMode) {
        insertionMode = drawMode;
        
    }
    
    public List<Entity> getEntities(){
        return entities;
    }

}


