/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * BoxPanel.java
 *
 * Created on Aug 29, 2013, 11:50:14 PM
 */
package com.arretados.leveleditor.entities;

import com.arretados.leveleditor.entities.EntityPanel.ItemPropertyChangedListener;

/**
 *
 * @author Yuri
 */
public class BoxItemPanel extends EntityPanel<Box> implements ItemPropertyChangedListener {

    /** Creates new form BoxPanel */
    public BoxItemPanel(ItemPropertyChangedListener listener) {
        initComponents();
    }
    
    public void onPropertyChanged(String propertyName, String newValue) {
       
    }

    @Override
    public void setEntity(Box entity) {
        super.setEntity(entity);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setName("Form"); // NOI18N
        setLayout(new java.awt.GridLayout(10, 1));
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

}
