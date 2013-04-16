/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.arretados.leveleditor;

import com.arretados.leveleditor.entities.Box;
import com.arretados.leveleditor.entities.Fruit;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;

/**
 *
 * @author bruno
 */
public class GameCanvas extends JPanel implements MouseListener{
    
    private List<Box> boxPos = new ArrayList<Box>();
    private List<Fruit> circlePos = new ArrayList<Fruit>();
    private List<int[]> groundPos = new ArrayList<int[]>();
    DrawMode mode = DrawMode.BOX;

    public GameCanvas() {
        addMouseListener(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(Color.white);
        g.fillRect(0, 0, g.getClipBounds().width, g.getClipBounds().height);
        g.setColor(Color.red);

        for (int i = 0; i < boxPos.size(); i++){
            boxPos.get(i).drawMyself(g);
        }

        for (int i = 0; i < circlePos.size(); i++){
            circlePos.get(i).drawMyself(g);
        }

        int posX,posY;
        posX = 0;
        posY = 0;
        for (int i = 0; i < groundPos.size(); i++){
            posX = groundPos.get(i)[0];
            posY = groundPos.get(i)[1];
            if (i == 0)
                g.drawLine(0, 600, posX, posY);
            else
                g.drawLine( groundPos.get(i-1)[0], groundPos.get(i-1)[1], posX, posY);
        }
    }

    public void drawBox(int x,int y){
        boxPos.add(new Box(x, y));
        repaint();
    }
    
    public void drawApple(int x, int y){
        circlePos.add(new Fruit(x, y));
        repaint();
    }
    
    public void drawGroundLine(int x,int y){
        int lastPointX = 0;
        
        if (groundPos.size() > 0) {
            lastPointX = groundPos.get(groundPos.size()-1)[0];  //Gets the last point
        }
        
        if ( x >= lastPointX){  //Verify if the point that will be created comes after the last point
            groundPos.add(new int[]{x, y});     
        }
        repaint();
    }

    public void mouseClicked(MouseEvent e) {
        
        switch (mode) {
            
            case BOX:
                drawBox(e.getX(), e.getY());
            break;

            case FRUIT:
                drawApple(e.getX(), e.getY());
            break;

            case LINE:
                drawGroundLine(e.getX(), e.getY());
            break;

            default: 
                System.out.println("BUG");
            break;            
        }
    }
    
    public List<Box> getBoxPos(){
        return boxPos;
    }
    
    public List<Fruit> getFruitPos(){
        return circlePos;
    }
    
    public List<int[]> getLinesPos(){
        return groundPos;
    }

    public void mousePressed(MouseEvent e) { }

    public void mouseReleased(MouseEvent e) { }

    public void mouseEntered(MouseEvent e) { }

    public void mouseExited(MouseEvent e) { }
}


