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
    }

    public void drawBox(int x,int y){
        //lista de caixas
        //positions.add(new int[]{x- boxWidth/2 ...});
        positions.add(new int[]{x-25, y-25});
        repaint();
    }

    public void mouseClicked(MouseEvent e) {
        drawBox(e.getX(), e.getY());
    }

    public void mousePressed(MouseEvent e) {

    }

    public void mouseReleased(MouseEvent e) {

    }

    public void mouseEntered(MouseEvent e) {

    }

    public void mouseExited(MouseEvent e) {

    }

}
