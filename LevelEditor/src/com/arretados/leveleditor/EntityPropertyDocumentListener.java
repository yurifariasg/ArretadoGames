package com.arretados.leveleditor;

import com.arretados.leveleditor.entities.EntityPanel.ItemPropertyChangedListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class EntityPropertyDocumentListener implements DocumentListener {
    private final ItemPropertyChangedListener listener;
    private final String propertyName;
    
    public EntityPropertyDocumentListener(String propertyName, ItemPropertyChangedListener listener) {
        this.listener = listener;
        this.propertyName = propertyName;
    }

    @Override
    public void changedUpdate(DocumentEvent documentEvent) {
        printIt(documentEvent);
    }

    @Override
    public void insertUpdate(DocumentEvent documentEvent) {
        printIt(documentEvent);
    }

    @Override
    public void removeUpdate(DocumentEvent documentEvent) {
        printIt(documentEvent);
    }

    private void printIt(DocumentEvent documentEvent) {
        try {
            String text = documentEvent.getDocument().getText(0, documentEvent.getDocument().getLength());
            if (!text.isEmpty())
                listener.onPropertyChanged(propertyName, text);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
