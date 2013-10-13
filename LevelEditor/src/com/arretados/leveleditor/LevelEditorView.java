/*
 * LevelEditorView.java
 */

package com.arretados.leveleditor;

import com.arretados.leveleditor.entities.Box;
import com.arretados.leveleditor.entities.BoxPanel;
import com.arretados.leveleditor.entities.Breakable;
import com.arretados.leveleditor.entities.BreakablePanel;
import com.arretados.leveleditor.entities.Coin;
import com.arretados.leveleditor.entities.CoinPanel;
import com.arretados.leveleditor.entities.EntityPanel;
import com.arretados.leveleditor.entities.EntityPanel.ItemPropertyChangedListener;
import com.arretados.leveleditor.entities.Fluid;
import com.arretados.leveleditor.entities.FluidPanel;
import com.arretados.leveleditor.entities.Liana;
import com.arretados.leveleditor.entities.LianaPanel;
import com.arretados.leveleditor.entities.OneWayWall;
import com.arretados.leveleditor.entities.OneWayWallPanel;
import com.arretados.leveleditor.entities.Pulley;
import com.arretados.leveleditor.entities.PulleyPanel;
import com.arretados.leveleditor.entities.layer.Grass;
import com.arretados.leveleditor.entities.layer.GrassPanel;
import com.arretados.leveleditor.entities.layer.Shrub;
import com.arretados.leveleditor.entities.layer.ShrubPanel;
import com.arretados.leveleditor.entities.layer.Tree;
import com.arretados.leveleditor.entities.layer.TreePanel;
import com.arretados.leveleditor.parsers.JSONGenerator;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.event.ChangeEvent;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.TaskMonitor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Timer;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.event.ChangeListener;

/**
 * The application's main frame.
 */
public class LevelEditorView extends FrameView implements ItemPropertyChangedListener {

    public LevelEditorView(SingleFrameApplication app) {
        super(app);
        initComponents();
        itemComboBox.setSelectedItem(null);
        gameCanvas1.setMainView(this);
        
        jScrollPane1.getViewport().addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                jScrollPane1.repaint();
                gameCanvas1.repaint();
                jScrollPane1.revalidate();
                gameCanvas1.revalidate();

            }
        });
        jTextWidthValue.setText(String.valueOf(gameCanvas1.getPreferredSize().width));
        jTextHeigthValue.setText(String.valueOf(gameCanvas1.getPreferredSize().height));
        
        // Initialize Panels
        Box.box_panel = new BoxPanel(this);
        Breakable.breakable_panel = new BreakablePanel(this);
        Coin.coin_panel = new CoinPanel(this);
        Fluid.fluid_panel = new FluidPanel(this);
        Liana.liana_panel = new LianaPanel(this);
        OneWayWall.onewaywall_panel = new OneWayWallPanel(this);
        Pulley.pulley_panel = new PulleyPanel(this);
        Tree.tree_panel = new TreePanel(this);
        Grass.grass_panel = new GrassPanel(this);
        Shrub.shrub_panel = new ShrubPanel(this);

        // status bar initialization - message timeout, idle icon and busy animation, etc
        ResourceMap resourceMap = getResourceMap();
        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                statusMessageLabel.setText("");
            }
        });
        messageTimer.setRepeats(false);
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }
        });
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
        statusAnimationLabel.setIcon(idleIcon);
        progressBar.setVisible(false);

        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName)) {
                    if (!busyIconTimer.isRunning()) {
                        statusAnimationLabel.setIcon(busyIcons[0]);
                        busyIconIndex = 0;
                        busyIconTimer.start();
                    }
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(true);
                } else if ("done".equals(propertyName)) {
                    busyIconTimer.stop();
                    statusAnimationLabel.setIcon(idleIcon);
                    progressBar.setVisible(false);
                    progressBar.setValue(0);
                } else if ("message".equals(propertyName)) {
                    String text = (String)(evt.getNewValue());
                    statusMessageLabel.setText((text == null) ? "" : text);
                    messageTimer.restart();
                } else if ("progress".equals(propertyName)) {
                    int value = (Integer)(evt.getNewValue());
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(value);
                }
            }
        });
    }

    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = LevelEditorApp.getApplication().getMainFrame();
            aboutBox = new LevelEditorAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        LevelEditorApp.getApplication().show(aboutBox);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jScrollInternalPanel = new javax.swing.JPanel();
        gameCanvas1 = new com.arretados.leveleditor.GameCanvas();
        clearScrBtn = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jTextWidthValue = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jTextHeigthValue = new javax.swing.JTextField();
        itemComboBox = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        itemPanel = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();

        mainPanel.setName("mainPanel"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(com.arretados.leveleditor.LevelEditorApp.class).getContext().getResourceMap(LevelEditorView.class);
        jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
        jButton1.setName("jButton1"); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jScrollPane1.setName("jScrollPane1"); // NOI18N
        jScrollPane1.setPreferredSize(new java.awt.Dimension(1000, 1000));

        jScrollInternalPanel.setName("jScrollInternalPanel"); // NOI18N
        jScrollInternalPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        gameCanvas1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        gameCanvas1.setName("gameCanvas1"); // NOI18N
        gameCanvas1.setPreferredSize(new java.awt.Dimension(3000, 600));

        javax.swing.GroupLayout gameCanvas1Layout = new javax.swing.GroupLayout(gameCanvas1);
        gameCanvas1.setLayout(gameCanvas1Layout);
        gameCanvas1Layout.setHorizontalGroup(
            gameCanvas1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 2998, Short.MAX_VALUE)
        );
        gameCanvas1Layout.setVerticalGroup(
            gameCanvas1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 598, Short.MAX_VALUE)
        );

        jScrollInternalPanel.add(gameCanvas1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 10, -1, -1));

        jScrollPane1.setViewportView(jScrollInternalPanel);

        clearScrBtn.setText(resourceMap.getString("clearScrBtn.text")); // NOI18N
        clearScrBtn.setName("clearScrBtn"); // NOI18N
        clearScrBtn.setPreferredSize(new java.awt.Dimension(50, 20));
        clearScrBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearScrBtnActionPerformed(evt);
            }
        });

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jTextWidthValue.setText(resourceMap.getString("jTextWidthValue.text")); // NOI18N
        jTextWidthValue.setName("jTextWidthValue"); // NOI18N

        jButton2.setText(resourceMap.getString("jButton2.text")); // NOI18N
        jButton2.setName("jButton2"); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel2.setText("Heigth");
        jLabel2.setName("jLabel2"); // NOI18N

        jTextHeigthValue.setText(resourceMap.getString("jTextHeigthValue.text")); // NOI18N
        jTextHeigthValue.setName("jTextHeigthValue"); // NOI18N

        itemComboBox.setModel(new DefaultComboBoxModel(new DrawMode[] {DrawMode.BOX, DrawMode.BREAKABLE, DrawMode.COIN, DrawMode.FLAG, DrawMode.FLUID, DrawMode.LIANA, DrawMode.ONEWAY_WALL, DrawMode.PLAYER, DrawMode.PULLEY, DrawMode.TREE, DrawMode.GRASS, DrawMode.SHRUB}));
        itemComboBox.setName("itemComboBox"); // NOI18N
        itemComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                itemComboBoxItemStateChanged(evt);
            }
        });
        itemComboBox.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                itemComboBoxPropertyChange(evt);
            }
        });

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        itemPanel.setBackground(resourceMap.getColor("itemPropertiesPanel.background")); // NOI18N
        itemPanel.setName("itemPropertiesPanel"); // NOI18N
        itemPanel.setLayout(new java.awt.GridLayout(1, 1));

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N
        itemPanel.add(jLabel4);

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(itemPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 326, Short.MAX_VALUE)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 326, Short.MAX_VALUE)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, 326, Short.MAX_VALUE)
                    .addComponent(jTextHeigthValue, javax.swing.GroupLayout.DEFAULT_SIZE, 326, Short.MAX_VALUE)
                    .addComponent(jLabel2)
                    .addComponent(jTextWidthValue, javax.swing.GroupLayout.DEFAULT_SIZE, 326, Short.MAX_VALUE)
                    .addComponent(jLabel1)
                    .addComponent(itemComboBox, javax.swing.GroupLayout.Alignment.TRAILING, 0, 326, Short.MAX_VALUE)
                    .addComponent(jLabel3)
                    .addComponent(clearScrBtn, javax.swing.GroupLayout.DEFAULT_SIZE, 326, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1082, Short.MAX_VALUE)
                .addContainerGap())
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 655, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(itemComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(itemPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 343, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(clearScrBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextWidthValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextHeigthValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6))
        );

        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(com.arretados.leveleditor.LevelEditorApp.class).getContext().getActionMap(LevelEditorView.class, this);
        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        statusPanel.setName("statusPanel"); // NOI18N

        statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N

        statusMessageLabel.setName("statusMessageLabel"); // NOI18N

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

        progressBar.setName("progressBar"); // NOI18N

        javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(statusPanelSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, 444, Short.MAX_VALUE)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(statusMessageLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 424, Short.MAX_VALUE)
                .addComponent(statusAnimationLabel)
                .addContainerGap())
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addGap(137, 137, 137)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(161, Short.MAX_VALUE))
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addComponent(statusPanelSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(statusPanelLayout.createSequentialGroup()
                        .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(statusMessageLabel)
                            .addComponent(statusAnimationLabel))
                        .addGap(3, 3, 3))
                    .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        setComponent(mainPanel);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        JSONGenerator json = new JSONGenerator(
                gameCanvas1.getEntitiesPos(),
                gameCanvas1.getGroundHeight(),
                gameCanvas1.getHeight(), gameCanvas1.getFlag());
        
        String jsonString = json.generateJson().toJSONString();
        
        try {
          File file = new File("level.json");
          BufferedWriter output = new BufferedWriter(new FileWriter(file));
          output.write(jsonString);
          output.close();
        } catch ( IOException e ) {
           e.printStackTrace();
        }
        
        // Show Dialog
        JFrame frame = new JFrame();
        JTextArea textArea = new JTextArea(jsonString);
        frame.add(textArea);
        frame.setSize(new Dimension(500, 250));
        
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation((dim.width-frame.getWidth())/2, (dim.height-frame.getHeight())/2);
        
        frame.setVisible(true);
        
        System.out.println(jsonString);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void clearScrBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearScrBtnActionPerformed
        gameCanvas1.clearObjectsList();
        gameCanvas1.repaint();
    }//GEN-LAST:event_clearScrBtnActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        String wValue = jTextWidthValue.getText();
        String hValue = jTextHeigthValue.getText();
        
        int newW = 0;
        int newH = 0;
        try{
            if (wValue.length() > 0){
                newW = Integer.parseInt(wValue);
            }
            if (hValue.length() > 0){
                newH = Integer.parseInt(hValue);
            }
            
            Dimension d = new Dimension(newW, newH);
            
            jScrollInternalPanel.setPreferredSize(d);
            gameCanvas1.setPreferredSize(d);
            
            jScrollInternalPanel.revalidate();
            jScrollPane1.revalidate();
            gameCanvas1.revalidate();
            gameCanvas1.repaint();
        }catch(Exception e){ }        
    }//GEN-LAST:event_jButton2ActionPerformed

private void itemComboBoxPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_itemComboBoxPropertyChange
}//GEN-LAST:event_itemComboBoxPropertyChange

public void switchEntityPanel(EntityPanel entityPanel) {
   
    itemPanel.removeAll();
    if (entityPanel != null) {
        itemPanel.add(entityPanel);
    }
    itemPanel.validate();
    itemPanel.repaint();
}

public EntityPanel getEntityPanel() {
    if (itemPanel.getComponentCount() > 0)
        return (EntityPanel) itemPanel.getComponent(0);
    return null;
}

private void itemComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_itemComboBoxItemStateChanged
    if (evt.getItemSelectable().getSelectedObjects().length == 0)
        return;
    
    DrawMode newMode = (DrawMode) (evt.getItemSelectable().getSelectedObjects()[0]);
    gameCanvas1.changeMode(newMode);
    itemPanel.removeAll();
    
    // Add the panel related to the mode
    switch (newMode) {
        case BOX:
            itemPanel.add(Box.box_panel);
            break;
        case BREAKABLE:
            itemPanel.add(Breakable.breakable_panel);
        break;
        case COIN:
            itemPanel.add(Coin.coin_panel);
        break;
        case FLAG:
        break;
        case FLUID:
            itemPanel.add(Fluid.fluid_panel);
        break;
        case LIANA:
            itemPanel.add(Liana.liana_panel);
        break;
        case LINE:
            
        break;
        case ONEWAY_WALL:
            itemPanel.add(OneWayWall.onewaywall_panel);
        break;
        case PLAYER:
            
        break;
        case TREE:
            itemPanel.add(Tree.tree_panel);
        break;
        case GRASS:
            itemPanel.add(Grass.grass_panel);
        break;
        case SHRUB:
            itemPanel.add(Shrub.shrub_panel);
        break;
        case PULLEY:
            itemPanel.add(Pulley.pulley_panel);
        break;
    }
    itemPanel.validate();
    itemPanel.repaint();
}//GEN-LAST:event_itemComboBoxItemStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton clearScrBtn;
    private com.arretados.leveleditor.GameCanvas gameCanvas1;
    private javax.swing.JComboBox itemComboBox;
    private javax.swing.JPanel itemPanel;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jScrollInternalPanel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextHeigthValue;
    private javax.swing.JTextField jTextWidthValue;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    // End of variables declaration//GEN-END:variables

    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;

    private JDialog aboutBox;

    public void onPropertyChanged(String propertyName, String newValue) {
        gameCanvas1.repaint();
    }
}
