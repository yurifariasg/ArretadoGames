/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * BoxPanel.java
 *
 * Created on Aug 29, 2013, 11:50:14 PM
 */
package com.arretados.leveleditor.entities.layer;

import com.arretados.leveleditor.entities.*;
import com.arretados.leveleditor.EntityPropertyDocumentListener;
import com.arretados.leveleditor.entities.EntityPanel.ItemPropertyChangedListener;
import javax.swing.JSpinner;
import javax.swing.JSpinner.NumberEditor;
import javax.swing.SpinnerNumberModel;
import javax.swing.text.DefaultFormatter;

/**
 *
 * @author Yuri
 */
public class GrassPanel extends EntityPanel<Grass> implements ItemPropertyChangedListener {

    /** Creates new form BoxPanel */
    public GrassPanel(ItemPropertyChangedListener listener) {
        initComponents();
        
        JSpinner.NumberEditor jsEditor = (JSpinner.NumberEditor)jSpinner1.getEditor();
        DefaultFormatter formatter = (DefaultFormatter) jsEditor.getTextField().getFormatter();
        formatter.setAllowsInvalid(false);
        ((NumberEditor)jSpinner1.getEditor()).getTextField().getDocument().
                addDocumentListener(new EntityPropertyDocumentListener("grassType", this));
        ((NumberEditor)jSpinner1.getEditor()).getTextField().getDocument().
                addDocumentListener(new EntityPropertyDocumentListener("grassType", listener));
        jSpinner1.setValue(0.0);
        
    }
    
    public float getCurrentGrassType() {
        return (float) ((Double) jSpinner1.getValue()).doubleValue();
    }
    
    public void onPropertyChanged(String propertyName, String newValue) {
        if (getEntity() == null)
            return;
        
        if (propertyName.equals("grassType")) {
            getEntity().setGrassType((int) Float.parseFloat(newValue));
        }
    }

    @Override
    public void setEntity(Grass entity) {
        super.setEntity(entity);
        jSpinner1.setValue((double) getEntity().getGrassType());
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jSpinner1 = new javax.swing.JSpinner();

        setName("Form"); // NOI18N
        setLayout(new java.awt.GridLayout(10, 1));

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(com.arretados.leveleditor.LevelEditorApp.class).getContext().getResourceMap(GrassPanel.class);
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N
        add(jLabel1);

        jSpinner1.setModel(new SpinnerNumberModel(
            0.0, // value
            0.0, // min
            2.0, // max
            1.0 // step
        ));
        jSpinner1.setName("jSpinner1"); // NOI18N
        add(jSpinner1);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JSpinner jSpinner1;
    // End of variables declaration//GEN-END:variables

}