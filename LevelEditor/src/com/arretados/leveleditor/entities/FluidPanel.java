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
public class FluidPanel extends EntityPanel<Fluid> implements ItemPropertyChangedListener {

    /** Creates new form BoxPanel */
    public FluidPanel(ItemPropertyChangedListener listener) {
        initComponents();
        
        JSpinner.NumberEditor jsEditor = (JSpinner.NumberEditor)jSpinner1.getEditor();
        DefaultFormatter formatter = (DefaultFormatter) jsEditor.getTextField().getFormatter();
        formatter.setAllowsInvalid(false);
        ((NumberEditor)jSpinner1.getEditor()).getTextField().getDocument().
                addDocumentListener(new EntityPropertyDocumentListener("Width", this));
        ((NumberEditor)jSpinner1.getEditor()).getTextField().getDocument().
                addDocumentListener(new EntityPropertyDocumentListener("Width", listener));
        jSpinner1.setValue(1.0);
        
        jsEditor = (JSpinner.NumberEditor)jSpinner2.getEditor();
        formatter = (DefaultFormatter) jsEditor.getTextField().getFormatter();
        formatter.setAllowsInvalid(false);
        ((NumberEditor)jSpinner2.getEditor()).getTextField().getDocument().
                addDocumentListener(new EntityPropertyDocumentListener("Height", this));
        ((NumberEditor)jSpinner2.getEditor()).getTextField().getDocument().
                addDocumentListener(new EntityPropertyDocumentListener("Height", listener));
        jSpinner2.setValue(1.0);
        
        jsEditor = (JSpinner.NumberEditor)jSpinner3.getEditor();
        formatter = (DefaultFormatter) jsEditor.getTextField().getFormatter();
        formatter.setAllowsInvalid(false);
        ((NumberEditor)jSpinner3.getEditor()).getTextField().getDocument().
                addDocumentListener(new EntityPropertyDocumentListener("Density", this));
        ((NumberEditor)jSpinner3.getEditor()).getTextField().getDocument().
                addDocumentListener(new EntityPropertyDocumentListener("Density", listener));
        jSpinner3.setValue(1.0);
        
    }
    
    public float getCurrentWidth() {
        return (float) ((Double) jSpinner1.getValue()).doubleValue();
    }
    
    public float getCurrentHeight() {
        return (float) ((Double) jSpinner2.getValue()).doubleValue();
    }
    
    public float getCurrentDensity() {
        return (float) ((Double) jSpinner3.getValue()).doubleValue();
    }
    
    public void onPropertyChanged(String propertyName, String newValue) {
        if (getEntity() == null)
            return;
        
        if (propertyName.equals("Width")) {
            getEntity().setWidth(Float.parseFloat(newValue));
        } else if (propertyName.equals("Height")) {
            getEntity().setHeight(Float.parseFloat(newValue));
        } else if (propertyName.equals("Density")) {
            getEntity().setDensity(Float.parseFloat(newValue));
        }
        
    }
    
    @Override
    public void setEntity(Fluid entity) {
        super.setEntity(entity);
        jSpinner1.setValue((double) getEntity().getWidth());
        jSpinner2.setValue((double) getEntity().getHeight());
        jSpinner3.setValue((double) getEntity().getDensity());
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
        jLabel2 = new javax.swing.JLabel();
        jSpinner2 = new javax.swing.JSpinner();
        jLabel3 = new javax.swing.JLabel();
        jSpinner3 = new javax.swing.JSpinner();

        setName("Form"); // NOI18N
        setLayout(new java.awt.GridLayout(10, 1));

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(com.arretados.leveleditor.LevelEditorApp.class).getContext().getResourceMap(FluidPanel.class);
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N
        add(jLabel1);

        jSpinner1.setModel(new SpinnerNumberModel(
            1.0, // value
            0.5, // min
            10.0, // max
            0.1 // step
        ));
        jSpinner1.setName("jSpinner1"); // NOI18N
        add(jSpinner1);

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N
        add(jLabel2);

        jSpinner2.setModel(new SpinnerNumberModel(
            1.0, // value
            0.5, // min
            10.0, // max
            0.1 // step
        ));
        jSpinner2.setName("jSpinner2"); // NOI18N
        add(jSpinner2);

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N
        add(jLabel3);

        jSpinner3.setModel(new SpinnerNumberModel(
            1.0, // value
            1.0, // min
            13.0, // max
            0.1 // step
        ));
        jSpinner3.setName("jSpinner3"); // NOI18N
        add(jSpinner3);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JSpinner jSpinner2;
    private javax.swing.JSpinner jSpinner3;
    // End of variables declaration//GEN-END:variables

}