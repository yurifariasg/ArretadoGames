/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arretados.leveleditor.entities;

import java.awt.Color;
import java.awt.Graphics;

/**
 *
 * @author Bruno
 */
public class Liana extends DrawableObject{
    private int x0;
    private int y0;
    private int x1;
    private int y1;
    private int size;
    
    public Liana(int x0, int y0, int x1, int  y1){
        this.x0 = x0;
        this.y0 = y0;
        this.x1 = x1;
        this.y1 = y1;
    }
    
    public int getX0(){
        return this.x0;
    }
    
    public void setX0(int x0){
        this.x0 = x0;
    }
    
    public int getX1(){
        return this.x1;
    }
    
    public void setX1(int x1){
        this.x1 = x1;
    }
    
    public int getY0(){
        return this.y0;
    }
    
    public void setY0(int y0){
        this.y0 = y0;
    }
    
    public int getY1(){
        return this.y1;
    }
    
    public void setY1(int y1){
        this.y1 = y1;
    }
    
    public int getSize(){
        return this.size;
    }

    @Override
    public void drawMyself(Graphics g) {
        g.setColor(Color.magenta);
        g.drawLine(this.x0, this.y0, this.x1, this.y1);
    }
    
}
