/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.arretados.leveleditor;

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

    private List<int[]> positions = new ArrayList<int[]>();
    private List<int[]> groundPos = new ArrayList<int[]>();
    private List<int[]> circlePos = new ArrayList<int[]>();
    private boolean modeBox = false;
    private boolean modeGround = false;
    private boolean modeApple = false;

    public GameCanvas() {
        addMouseListener(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(Color.white);
        g.fillRect(0, 0, g.getClipBounds().width, g.getClipBounds().height);
        g.setColor(Color.red);

        int posX,posY;
        for (int i = 0; i < positions.size(); i++){
            posX = positions.get(i)[0];
            posY = positions.get(i)[1];
            g.drawRect(posX, posY, 50, 50);
        }
        
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
        
        posX = 0;
        posY = 0;
        for (int i = 0; i < circlePos.size(); i++){
            posX = circlePos.get(i)[0];
            posY = circlePos.get(i)[1];
            g.drawOval(posX, posY, 25, 25);
        }
    }

    public void drawBox(int x,int y){
        //lista de caixas
        //positions.add(new int[]{x- boxWidth/2 ...});
        positions.add(new int[]{x-25, y-25});
        repaint();
    }
    
    public void drawGroundLine(int x,int y){
        //lista de pontos
        groundPos.add(new int[]{x, y});
        repaint();
    }
    
    public void drawApple(int x, int y){
        circlePos.add(new int[]{x-12, y-12});
        repaint();
    }

    public void mouseClicked(MouseEvent e) {
        if (modeBox){
            drawBox(e.getX(), e.getY());
        }
        if (modeGround){
            drawGroundLine(e.getX(), e.getY());
        }
        if (modeApple){
            drawApple(e.getX(), e.getY());
        }
    }

    public void mousePressed(MouseEvent e) {

    }

    public void mouseReleased(MouseEvent e) {

    }

    public void mouseEntered(MouseEvent e) {

    }

    public void mouseExited(MouseEvent e) {

    }
    
    public boolean getModeBox(){
        return this.modeBox;
    }

    public boolean getModeGround(){
        return this.modeGround;
    }
    
    public boolean getModeCircle(){
        return this.modeApple;
    }
    
    public void setModeBox(boolean newMode){
        this.modeBox = newMode;
        this.modeGround = false;
        this.modeApple = false;
    }
    
    public void setModeGround(boolean newMode){
        this.modeGround = newMode;
        this.modeBox = false;
        this.modeApple = false;
    }
    
    public void setModeApple(boolean newMode){
        this.modeApple = newMode;
        this.modeGround = false;
        this.modeBox = false;
    }

}
