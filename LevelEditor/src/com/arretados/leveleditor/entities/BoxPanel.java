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
import com.arretados.leveleditor.Utils;
import com.arretados.leveleditor.entities.EntityPanel.ItemPropertyChangedListener;
import javax.swing.JSpinner;
import javax.swing.JSpinner.NumberEditor;
import javax.swing.SpinnerNumberModel;
import javax.swing.text.DefaultFormatter;

/**
 *
 * @author Yuri
 */
public class BoxPanel extends EntityPanel<Box> implements ItemPropertyChangedListener {

    /** Creates new form BoxPanel */
    public BoxPanel(ItemPropertyChangedListener listener) {
        initComponents();
        
        JSpinner.NumberEditor jsEditor = (JSpinner.NumberEditor)jSpinner1.getEditor();
        DefaultFormatter formatter = (DefaultFormatter) jsEditor.getTextField().getFormatter();
        formatter.setAllowsInvalid(false);
        ((NumberEditor)jSpinner1.getEditor()).getTextField().getDocument().
                addDocumentListener(new EntityPropertyDocumentListener("Size", this));
        ((NumberEditor)jSpinner1.getEditor()).getTextField().getDocument().
                addDocumentListener(new EntityPropertyDocumentListener("Size", listener));
        jSpinner1.setValue(1.0);
        
        jsEditor = (JSpinner.NumberEditor)jSpinner2.getEditor();
        formatter = (DefaultFormatter) jsEditor.getTextField().getFormatter();
        formatter.setAllowsInvalid(false);
        ((NumberEditor)jSpinner2.getEditor()).getTextField().getDocument().
                addDocumentListener(new EntityPropertyDocumentListener("Weight", this));
        ((NumberEditor)jSpinner2.getEditor()).getTextField().getDocument().
                addDocumentListener(new EntityPropertyDocumentListener("Weight", listener));
        jSpinner2.setValue(1.0);
        
    }
    
    public float getCurrentSize() {
        return Utils.parseValue(jSpinner1.getValue());
    }
    
    public float getCurrentWeight() {
        return Utils.parseValue(jSpinner2.getValue());
    }
    
    public void onPropertyChanged(String propertyName, String newValue) {
        if (getEntity() == null)
            return;
        
        if (propertyName.equals("Size")) {
            getEntity().setSize(Utils.parseValue(newValue));
        }
        
    }

    @Override
    public void setEntity(Box entity) {
        super.setEntity(entity);
        jSpinner1.setValue((double) getEntity().getSize());
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

        setName("Form"); // NOI18N
        setLayout(new java.awt.GridLayout(10, 1));

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(com.arretados.leveleditor.LevelEditorApp.class).getContext().getResourceMap(BoxPanel.class);
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N
        add(jLabel1);

        jSpinner1.setModel(new SpinnerNumberModel(
            1.0, // value
            0.1, // min
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
            0.1, // min
            50.0, // max
            0.1 // step
        ));
        jSpinner2.setName("jSpinner2"); // NOI18N
        add(jSpinner2);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JSpinner jSpinner2;
    // End of variables declaration//GEN-END:variables

}
