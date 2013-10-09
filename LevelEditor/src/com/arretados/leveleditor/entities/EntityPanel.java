/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arretados.leveleditor.entities;

import javax.swing.JPanel;

public abstract class EntityPanel<T extends Entity> extends JPanel {
    
    private T entity;
    
    public interface ItemPropertyChangedListener {
        
        public void onPropertyChanged(String propertyName, String newValue);
        
    }

    public void setEntity(T entity) {
        this.entity = entity;
    }

    public T getEntity() {
        return entity;
    }
    
    protected ItemPropertyChangedListener listener;
    
    public void setOnPropertyChangedListener(ItemPropertyChangedListener listener) {
        this.listener = listener;
    }
    
    protected void notifyListener(String propertyName, String value) {
        if (listener != null)
            listener.onPropertyChanged(propertyName, value);
    }
}
