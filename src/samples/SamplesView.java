/*
 * SamplesView.java
 */

package samples;

import java.awt.Desktop;
import java.awt.Graphics;
import java.awt.Graphics2D;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.TaskMonitor;
import org.jdesktop.application.Task;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.RollbackException;
import javax.swing.Timer;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.jdesktop.beansbinding.AbstractBindingListener;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.PropertyStateEvent;

/**
 * The application's main frame.
 */
public class SamplesView extends FrameView {
    
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    Date date = new Date();
    
    
    public SamplesView(SingleFrameApplication app) {
        super(app);

        initComponents();

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

        // tracking table selection
        masterTable.getSelectionModel().addListSelectionListener(
            new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    firePropertyChange("recordSelected", !isRecordSelected(), isRecordSelected());
                }
            });

        // tracking changes to save
        bindingGroup.addBindingListener(new AbstractBindingListener() {
            @Override
            public void targetChanged(Binding binding, PropertyStateEvent event) {
                // save action observes saveNeeded property
                setSaveNeeded(true);
            }
        });

        // have a transaction started
        entityManager.getTransaction().begin();
    }


    public boolean isSaveNeeded() {
        return saveNeeded;
    }

    private void setSaveNeeded(boolean saveNeeded) {
        if (saveNeeded != this.saveNeeded) {
            this.saveNeeded = saveNeeded;
            firePropertyChange("saveNeeded", !saveNeeded, saveNeeded);
        }
    }

    public boolean isRecordSelected() {
        return masterTable.getSelectedRow() != -1;
    }
    

    @Action
    public void newRecord() {
        //dateCreatField.setText(String.valueOf(dateFormat.format(date)));
        dateCreatField.setDate(date);
        samples.Samples s = new samples.Samples();
        entityManager.persist(s);
        list.add(s);
        int row = list.size()-1;
        masterTable.setRowSelectionInterval(row, row);
        masterTable.scrollRectToVisible(masterTable.getCellRect(row, 0, true));
        setSaveNeeded(true);
}

    @Action(enabledProperty = "recordSelected")
    public void deleteRecord() {
        int[] selected = masterTable.getSelectedRows();
        List<samples.Samples> toRemove = new ArrayList<samples.Samples>(selected.length);
        for (int idx=0; idx<selected.length; idx++) {
            samples.Samples s = list.get(masterTable.convertRowIndexToModel(selected[idx]));
            toRemove.add(s);
            entityManager.remove(s);
        }
        list.removeAll(toRemove);
        setSaveNeeded(true);
    }
    

    @Action(enabledProperty = "saveNeeded")
    public Task save() {
        
        return new SaveTask(getApplication());
    }

    private class SaveTask extends Task {
        SaveTask(org.jdesktop.application.Application app) {
            super(app);
//String path = sampleField.getText();
//String supplier_name = supplierField.getText();
//String or_item_n = itemSField.getText();
//
//String path1=path.replace("/","_");
//path1=path1.replaceAll("\\s","");
//
//File mainpath = new File("R:/Quality Management/Samples");
//
//File file = new File(mainpath+"/"+path1);
//Desktop desktop = null;
//
//    if (Desktop.isDesktopSupported())
//    {
//    desktop = Desktop.getDesktop();
//    }
//   File mainfolder = new File(file+"_"+"("+or_item_n+")"+"_"+supplier_name);
//
//            if (mainfolder.exists())
//            {}
//            else
//            {
//                    mainfolder.mkdir();
//            }
        }
        @Override protected Void doInBackground() {
            try {
                entityManager.getTransaction().commit();
                entityManager.getTransaction().begin();
            } catch (RollbackException rex) {
                rex.printStackTrace();
                entityManager.getTransaction().begin();
                List<samples.Samples> merged = new ArrayList<samples.Samples>(list.size());
                for (samples.Samples s : list) {
                    merged.add(entityManager.merge(s));
                }
                list.clear();
                list.addAll(merged);
            }
            return null;
        }
        @Override protected void finished() {
            setSaveNeeded(false);
        }
        
    }

    /**
     * An example action method showing how to create asynchronous tasks
     * (running on background) and how to show their progress. Note the
     * artificial 'Thread.sleep' calls making the task long enough to see the
     * progress visualization - remove the sleeps for real application.
     */
    @Action
    public Task refresh() {
       return new RefreshTask(getApplication());
    }

    private class RefreshTask extends Task {
        RefreshTask(org.jdesktop.application.Application app) {
            super(app);
        }
        @SuppressWarnings("unchecked")
        @Override protected Void doInBackground() {
//           try {
                setProgress(0, 0, 4);
                setMessage("Rolling back the current changes...");
                setProgress(1, 0, 4);
                entityManager.getTransaction().rollback();
                //Thread.sleep(1000L); // remove for real app
                setProgress(2, 0, 4);

                setMessage("Starting a new transaction...");
                entityManager.getTransaction().begin();
                //Thread.sleep(500L); // remove for real app
                setProgress(3, 0, 4);

                setMessage("Fetching new data...");
                
                if (jCheckBoxMenuItem1.isSelected())
                    {query = entityManager.createQuery("SELECT s FROM Samples s ORDER BY s.sample ASC");
                    java.util.Collection data = query.getResultList();
                    for (Object entity : data) {
                        entityManager.refresh(entity);
                    }
                    //Thread.sleep(1300L); // remove for real app
                    setProgress(4, 0, 4);

                    //Thread.sleep(150L); // remove for real app
                    list.clear();
                    list.addAll(data);
                    }
                else
                    {query = entityManager.createQuery("SELECT s FROM Samples s WHERE s.hidden=0 ORDER BY s.sample ASC");
                    java.util.Collection data = query.getResultList();
                    for (Object entity : data) {
                        entityManager.refresh(entity);
                    }
                    //Thread.sleep(1300L); // remove for real app
                    setProgress(4, 0, 4);

                    //Thread.sleep(150L); // remove for real app
                    list.clear();
                    list.addAll(data);
                    }    
//            } catch(InterruptedException ignore) { }
            return null;
        }
        @Override protected void finished() {
            setMessage("Done.");
            setSaveNeeded(false);
        }
    }

    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = SamplesApp.getApplication().getMainFrame();
            aboutBox = new SamplesAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        SamplesApp.getApplication().show(aboutBox);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        mainPanel = new javax.swing.JPanel();
        masterScrollPane = new javax.swing.JScrollPane();
        masterTable = new javax.swing.JTable();
        sampleLabel = new javax.swing.JLabel();
        itemSLabel = new javax.swing.JLabel();
        buyerLabel = new javax.swing.JLabel();
        contactLabel = new javax.swing.JLabel();
        supplierLabel = new javax.swing.JLabel();
        dateUpdateLabel = new javax.swing.JLabel();
        issuesLabel = new javax.swing.JLabel();
        dateCheckLabel = new javax.swing.JLabel();
        dateCreatLabel = new javax.swing.JLabel();
        replacementItemField = new javax.swing.JTextField();
        sampleField = new javax.swing.JFormattedTextField();
        replCheckBox = new javax.swing.JCheckBox();
        buyerComboBox = new javax.swing.JComboBox();
        assortRadioButton = new javax.swing.JRadioButton();
        promoRadioButton = new javax.swing.JRadioButton();
        checkDateChooser = new com.toedter.calendar.JDateChooser();
        updateDateChooser = new com.toedter.calendar.JDateChooser();
        checkCeCheckBox = new javax.swing.JCheckBox();
        checkTechCheckBox = new javax.swing.JCheckBox();
        checkFunCheckBox = new javax.swing.JCheckBox();
        lvdStComboBox = new javax.swing.JComboBox();
        emcStComboBox = new javax.swing.JComboBox();
        rfStComboBox = new javax.swing.JComboBox();
        rohsStComboBox = new javax.swing.JComboBox();
        erpLStComboBox = new javax.swing.JComboBox();
        psLvdStComboBox = new javax.swing.JComboBox();
        psEmcStComboBox = new javax.swing.JComboBox();
        psRohsStComboBox = new javax.swing.JComboBox();
        psErpStComboBox = new javax.swing.JComboBox();
        other2TextField = new javax.swing.JTextField();
        other2StComboBox = new javax.swing.JComboBox();
        photoStComboBox = new javax.swing.JComboBox();
        pahStComboBox = new javax.swing.JComboBox();
        other1TextField = new javax.swing.JTextField();
        other1StComboBox = new javax.swing.JComboBox();
        batStComboBox = new javax.swing.JComboBox();
        cpdStComboBox = new javax.swing.JComboBox();
        jCheckBox1 = new javax.swing.JCheckBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        dateCreatField = new com.toedter.calendar.JDateChooser();
        pahTrRadioButton = new javax.swing.JRadioButton();
        photoTrRadioButton = new javax.swing.JRadioButton();
        cpdCeRadioButton = new javax.swing.JRadioButton();
        batRadioButton = new javax.swing.JRadioButton();
        other1RadioButton = new javax.swing.JRadioButton();
        psLvdRadioButton = new javax.swing.JRadioButton();
        psEmcRadioButton = new javax.swing.JRadioButton();
        psRohsRadioButton = new javax.swing.JRadioButton();
        psErpRadioButton = new javax.swing.JRadioButton();
        other2RadioButton = new javax.swing.JRadioButton();
        erpTrButton = new javax.swing.JRadioButton();
        rohsTrRadioButton = new javax.swing.JRadioButton();
        rfCeRadioButton = new javax.swing.JRadioButton();
        rfTrRadioButton = new javax.swing.JRadioButton();
        emcCeRadioButton = new javax.swing.JRadioButton();
        emcTrRadioButton = new javax.swing.JRadioButton();
        lvdCeRadioButton = new javax.swing.JRadioButton();
        lvdTrRadioButton = new javax.swing.JRadioButton();
        jLabel1 = new javax.swing.JLabel();
        other3RadioButton = new javax.swing.JRadioButton();
        other4RadioButton = new javax.swing.JRadioButton();
        other5RadioButton = new javax.swing.JRadioButton();
        other3TextField = new javax.swing.JTextField();
        other3StComboBox = new javax.swing.JComboBox();
        other4TextField = new javax.swing.JTextField();
        other4StComboBox = new javax.swing.JComboBox();
        other5TextField = new javax.swing.JTextField();
        other5StComboBox = new javax.swing.JComboBox();
        descrLabel = new javax.swing.JLabel();
        monsterLabel = new javax.swing.JLabel();
        contactLabel1 = new javax.swing.JLabel();
        sampleField1 = new javax.swing.JFormattedTextField();
        monsterField = new javax.swing.JFormattedTextField();
        emailField = new javax.swing.JFormattedTextField();
        itemSField = new javax.swing.JFormattedTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        dropboxTextArea = new javax.swing.JTextArea();
        issuesLabel1 = new javax.swing.JLabel();
        supplierField = new javax.swing.JFormattedTextField();
        contactField = new javax.swing.JFormattedTextField();
        newitemRadioButton = new javax.swing.JRadioButton();
        printButton = new javax.swing.JButton();
        folderButton = new javax.swing.JButton();
        newButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        refreshButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();
        jFilter = new javax.swing.JTextField();
        jCheckBox2 = new javax.swing.JCheckBox();
        jScrollPane4 = new javax.swing.JScrollPane();
        IssuesTextPane = new javax.swing.JTextPane();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem newRecordMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenuItem deleteRecordMenuItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        javax.swing.JMenuItem saveMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenuItem refreshMenuItem = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        jCheckBoxMenuItem1 = new javax.swing.JCheckBoxMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(samples.SamplesApp.class).getContext().getResourceMap(SamplesView.class);
        entityManager = java.beans.Beans.isDesignTime() ? null : javax.persistence.Persistence.createEntityManagerFactory(resourceMap.getString("entityManager.persistenceUnit")).createEntityManager(); // NOI18N
        query = java.beans.Beans.isDesignTime() ? null : entityManager.createQuery(resourceMap.getString("query.query")); // NOI18N
        list = java.beans.Beans.isDesignTime() ? java.util.Collections.emptyList() : org.jdesktop.observablecollections.ObservableCollections.observableList(query.getResultList());
        buttonGroup1 = new javax.swing.ButtonGroup();
        rowSorterToStringConverter1 = new samples.RowSorterToStringConverter();

        mainPanel.setName("mainPanel"); // NOI18N
        mainPanel.setPreferredSize(new java.awt.Dimension(550, 1326));
        mainPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        masterScrollPane.setName("masterScrollPane"); // NOI18N

        masterTable.setName("masterTable"); // NOI18N
        masterTable.setRowHeight(24);

        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, list, masterTable);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${sample}"));
        columnBinding.setColumnName("Sample");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${descr}"));
        columnBinding.setColumnName("Descr");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();
        masterScrollPane.setViewportView(masterTable);
        masterTable.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("masterTable.columnModel.title0")); // NOI18N
        masterTable.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("masterTable.columnModel.title1")); // NOI18N

        mainPanel.add(masterScrollPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 150, 50));

        sampleLabel.setFont(resourceMap.getFont("issuesLabel.font")); // NOI18N
        sampleLabel.setText(resourceMap.getString("sampleLabel.text")); // NOI18N
        sampleLabel.setName("sampleLabel"); // NOI18N
        mainPanel.add(sampleLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 70, -1, 28));

        itemSLabel.setFont(resourceMap.getFont("issuesLabel.font")); // NOI18N
        itemSLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        itemSLabel.setText(resourceMap.getString("itemSLabel.text")); // NOI18N
        itemSLabel.setName("itemSLabel"); // NOI18N
        mainPanel.add(itemSLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 70, -1, 28));

        buyerLabel.setFont(resourceMap.getFont("issuesLabel.font")); // NOI18N
        buyerLabel.setText(resourceMap.getString("buyerLabel.text")); // NOI18N
        buyerLabel.setName("buyerLabel"); // NOI18N
        mainPanel.add(buyerLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 70, -1, 28));

        contactLabel.setFont(resourceMap.getFont("issuesLabel.font")); // NOI18N
        contactLabel.setText(resourceMap.getString("contactLabel.text")); // NOI18N
        contactLabel.setName("contactLabel"); // NOI18N
        mainPanel.add(contactLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(890, 100, -1, 28));

        supplierLabel.setFont(resourceMap.getFont("issuesLabel.font")); // NOI18N
        supplierLabel.setText(resourceMap.getString("supplierLabel.text")); // NOI18N
        supplierLabel.setName("supplierLabel"); // NOI18N
        mainPanel.add(supplierLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 70, -1, 28));

        dateUpdateLabel.setFont(resourceMap.getFont("issuesLabel.font")); // NOI18N
        dateUpdateLabel.setText(resourceMap.getString("dateUpdateLabel.text")); // NOI18N
        dateUpdateLabel.setName("dateUpdateLabel"); // NOI18N
        mainPanel.add(dateUpdateLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 200, -1, 28));

        issuesLabel.setFont(resourceMap.getFont("issuesLabel.font")); // NOI18N
        issuesLabel.setText(resourceMap.getString("issuesLabel.text")); // NOI18N
        issuesLabel.setName("issuesLabel"); // NOI18N
        mainPanel.add(issuesLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 590, -1, -1));

        dateCheckLabel.setFont(resourceMap.getFont("issuesLabel.font")); // NOI18N
        dateCheckLabel.setText(resourceMap.getString("dateCheckLabel.text")); // NOI18N
        dateCheckLabel.setName("dateCheckLabel"); // NOI18N
        mainPanel.add(dateCheckLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 200, -1, 28));

        dateCreatLabel.setFont(resourceMap.getFont("issuesLabel.font")); // NOI18N
        dateCreatLabel.setText(resourceMap.getString("dateCreatLabel.text")); // NOI18N
        dateCreatLabel.setName("dateCreatLabel"); // NOI18N
        mainPanel.add(dateCreatLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(930, 200, -1, 28));

        replacementItemField.setFont(resourceMap.getFont("itemSField.font")); // NOI18N
        replacementItemField.setName("replacementItemField"); // NOI18N
        replacementItemField.setPreferredSize(new java.awt.Dimension(6, 20));

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.replacementItem}"), replacementItemField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.replacement}"), replacementItemField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        mainPanel.add(replacementItemField, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 130, 240, 28));

        try {
            sampleField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("********************")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        sampleField.setFont(resourceMap.getFont("sampleField.font")); // NOI18N
        sampleField.setName("sampleField"); // NOI18N
        sampleField.setPreferredSize(new java.awt.Dimension(126, 20));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.sample}"), sampleField, org.jdesktop.beansbinding.BeanProperty.create("value"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement != null}"), sampleField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        binding.setSourceUnreadableValue(null);
        mainPanel.add(sampleField, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 70, 240, 28));

        replCheckBox.setFont(resourceMap.getFont("issuesLabel.font")); // NOI18N
        replCheckBox.setText(resourceMap.getString("replCheckBox.text")); // NOI18N
        replCheckBox.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        replCheckBox.setName("replCheckBox"); // NOI18N
        replCheckBox.setPreferredSize(new java.awt.Dimension(131, 20));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.replacement}"), replCheckBox, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        mainPanel.add(replCheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 130, -1, 28));

        buyerComboBox.setFont(resourceMap.getFont("buyerComboBox.font")); // NOI18N
        buyerComboBox.setMaximumRowCount(17);
        buyerComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "Ad", "Arian", "Artur", "Christy", "Dennis", "Dirk", "Fanny", "Jasmine", "Kit", "Marco", "Marijn", "Niels", "Sigrid", "Sven", "Rob", "Ron" }));
        buyerComboBox.setName("buyerComboBox"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ObjectProperty.create(), buyerComboBox, org.jdesktop.beansbinding.BeanProperty.create("elements"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.buyer}"), buyerComboBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        binding.setSourceUnreadableValue(null);
        mainPanel.add(buyerComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 70, 120, 28));

        buttonGroup1.add(assortRadioButton);
        assortRadioButton.setFont(resourceMap.getFont("issuesLabel.font")); // NOI18N
        assortRadioButton.setText(resourceMap.getString("assortRadioButton.text")); // NOI18N
        assortRadioButton.setName("assortRadioButton"); // NOI18N
        assortRadioButton.setPreferredSize(new java.awt.Dimension(115, 28));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.assortment}"), assortRadioButton, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        mainPanel.add(assortRadioButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 170, -1, -1));

        buttonGroup1.add(promoRadioButton);
        promoRadioButton.setFont(resourceMap.getFont("issuesLabel.font")); // NOI18N
        promoRadioButton.setText(resourceMap.getString("promoRadioButton.text")); // NOI18N
        promoRadioButton.setName("promoRadioButton"); // NOI18N
        promoRadioButton.setPreferredSize(new java.awt.Dimension(107, 28));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.promotion}"), promoRadioButton, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        mainPanel.add(promoRadioButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 170, -1, -1));

        checkDateChooser.setDateFormatString(resourceMap.getString("checkDateChooser.dateFormatString")); // NOI18N
        checkDateChooser.setFont(resourceMap.getFont("updateDateChooser.font")); // NOI18N
        checkDateChooser.setName("checkDateChooser"); // NOI18N
        checkDateChooser.setPreferredSize(new java.awt.Dimension(100, 28));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.dateCheck}"), checkDateChooser, org.jdesktop.beansbinding.BeanProperty.create("date"));
        bindingGroup.addBinding(binding);

        binding.setSourceUnreadableValue(null);
        mainPanel.add(checkDateChooser, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 200, 130, -1));

        updateDateChooser.setDateFormatString(resourceMap.getString("updateDateChooser.dateFormatString")); // NOI18N
        updateDateChooser.setFont(resourceMap.getFont("updateDateChooser.font")); // NOI18N
        updateDateChooser.setName("updateDateChooser"); // NOI18N
        updateDateChooser.setPreferredSize(new java.awt.Dimension(100, 28));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.dateUpdate}"), updateDateChooser, org.jdesktop.beansbinding.BeanProperty.create("date"));
        bindingGroup.addBinding(binding);

        binding.setSourceUnreadableValue(null);
        mainPanel.add(updateDateChooser, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 200, 130, -1));

        checkCeCheckBox.setFont(resourceMap.getFont("issuesLabel.font")); // NOI18N
        checkCeCheckBox.setText(resourceMap.getString("checkCeCheckBox.text")); // NOI18N
        checkCeCheckBox.setName("checkCeCheckBox"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.checkCe}"), checkCeCheckBox, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        mainPanel.add(checkCeCheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 240, -1, -1));

        checkTechCheckBox.setFont(resourceMap.getFont("issuesLabel.font")); // NOI18N
        checkTechCheckBox.setText(resourceMap.getString("checkTechCheckBox.text")); // NOI18N
        checkTechCheckBox.setName("checkTechCheckBox"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.checkTech}"), checkTechCheckBox, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        mainPanel.add(checkTechCheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 240, -1, -1));

        checkFunCheckBox.setFont(resourceMap.getFont("issuesLabel.font")); // NOI18N
        checkFunCheckBox.setText(resourceMap.getString("checkFunCheckBox.text")); // NOI18N
        checkFunCheckBox.setName("checkFunCheckBox"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.checkFunc}"), checkFunCheckBox, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        mainPanel.add(checkFunCheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 240, -1, -1));

        lvdStComboBox.setFont(resourceMap.getFont("erpLStComboBox.font")); // NOI18N
        lvdStComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "Bad copy", "Correct", "Draft", "Missing", "Not valid", "Wrong" }));
        lvdStComboBox.setName("lvdStComboBox"); // NOI18N
        lvdStComboBox.setPreferredSize(new java.awt.Dimension(78, 28));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ObjectProperty.create(), lvdStComboBox, org.jdesktop.beansbinding.BeanProperty.create("elements"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.lvdStatus}"), lvdStComboBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        binding.setSourceUnreadableValue(null);
        mainPanel.add(lvdStComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 290, 130, -1));

        emcStComboBox.setFont(resourceMap.getFont("erpLStComboBox.font")); // NOI18N
        emcStComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "Bad copy", "Correct", "Draft", "Missing", "Not valid", "Wrong" }));
        emcStComboBox.setName("emcStComboBox"); // NOI18N
        emcStComboBox.setPreferredSize(new java.awt.Dimension(78, 28));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ObjectProperty.create(), emcStComboBox, org.jdesktop.beansbinding.BeanProperty.create("elements"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.emcStatus}"), emcStComboBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        binding.setSourceUnreadableValue(null);
        mainPanel.add(emcStComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 320, 130, -1));

        rfStComboBox.setFont(resourceMap.getFont("erpLStComboBox.font")); // NOI18N
        rfStComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "Bad copy", "Correct", "Draft", "Missing", "Not valid", "Wrong" }));
        rfStComboBox.setName("rfStComboBox"); // NOI18N
        rfStComboBox.setPreferredSize(new java.awt.Dimension(78, 28));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ObjectProperty.create(), rfStComboBox, org.jdesktop.beansbinding.BeanProperty.create("elements"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.rfStatus}"), rfStComboBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        binding.setSourceUnreadableValue(null);
        mainPanel.add(rfStComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 350, 130, -1));

        rohsStComboBox.setFont(resourceMap.getFont("erpLStComboBox.font")); // NOI18N
        rohsStComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "Bad copy", "Correct", "Draft", "Missing", "Not valid", "Wrong" }));
        rohsStComboBox.setName("rohsStComboBox"); // NOI18N
        rohsStComboBox.setPreferredSize(new java.awt.Dimension(78, 28));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ObjectProperty.create(), rohsStComboBox, org.jdesktop.beansbinding.BeanProperty.create("elements"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.rohsStatus}"), rohsStComboBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        binding.setSourceUnreadableValue(null);
        mainPanel.add(rohsStComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 380, 130, -1));

        erpLStComboBox.setFont(resourceMap.getFont("erpLStComboBox.font")); // NOI18N
        erpLStComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "Bad copy", "Correct", "Draft", "Missing", "Not valid", "Wrong" }));
        erpLStComboBox.setName("erpLStComboBox"); // NOI18N
        erpLStComboBox.setPreferredSize(new java.awt.Dimension(78, 28));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ObjectProperty.create(), erpLStComboBox, org.jdesktop.beansbinding.BeanProperty.create("elements"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.erpLightStatus}"), erpLStComboBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        binding.setSourceUnreadableValue(null);
        mainPanel.add(erpLStComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 410, 130, -1));

        psLvdStComboBox.setFont(resourceMap.getFont("erpLStComboBox.font")); // NOI18N
        psLvdStComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "Bad copy", "Correct", "Draft", "Missing", "Not valid", "Wrong" }));
        psLvdStComboBox.setName("psLvdStComboBox"); // NOI18N
        psLvdStComboBox.setPreferredSize(new java.awt.Dimension(78, 28));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ObjectProperty.create(), psLvdStComboBox, org.jdesktop.beansbinding.BeanProperty.create("elements"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.psLvdStatus}"), psLvdStComboBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        binding.setSourceUnreadableValue(null);
        mainPanel.add(psLvdStComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(1070, 290, 130, -1));

        psEmcStComboBox.setFont(resourceMap.getFont("erpLStComboBox.font")); // NOI18N
        psEmcStComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "Bad copy", "Correct", "Draft", "Missing", "Not valid", "Wrong" }));
        psEmcStComboBox.setName("psEmcStComboBox"); // NOI18N
        psEmcStComboBox.setPreferredSize(new java.awt.Dimension(78, 28));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ObjectProperty.create(), psEmcStComboBox, org.jdesktop.beansbinding.BeanProperty.create("elements"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.psEmcStatus}"), psEmcStComboBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        binding.setSourceUnreadableValue(null);
        mainPanel.add(psEmcStComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(1070, 320, 130, -1));

        psRohsStComboBox.setFont(resourceMap.getFont("erpLStComboBox.font")); // NOI18N
        psRohsStComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "Bad copy", "Correct", "Draft", "Missing", "Not valid", "Wrong" }));
        psRohsStComboBox.setName("psRohsStComboBox"); // NOI18N
        psRohsStComboBox.setPreferredSize(new java.awt.Dimension(78, 28));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ObjectProperty.create(), psRohsStComboBox, org.jdesktop.beansbinding.BeanProperty.create("elements"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.psRohsStatus}"), psRohsStComboBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        binding.setSourceUnreadableValue(null);
        mainPanel.add(psRohsStComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(1070, 350, 130, -1));

        psErpStComboBox.setFont(resourceMap.getFont("erpLStComboBox.font")); // NOI18N
        psErpStComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "Bad copy", "Correct", "Draft", "Missing", "Not valid", "Wrong" }));
        psErpStComboBox.setName("psErpStComboBox"); // NOI18N
        psErpStComboBox.setPreferredSize(new java.awt.Dimension(78, 28));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ObjectProperty.create(), psErpStComboBox, org.jdesktop.beansbinding.BeanProperty.create("elements"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.psErpStatus}"), psErpStComboBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        binding.setSourceUnreadableValue(null);
        mainPanel.add(psErpStComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(1070, 380, 130, -1));

        other2TextField.setFont(resourceMap.getFont("other2TextField.font")); // NOI18N
        other2TextField.setName("other2TextField"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.other2Text}"), other2TextField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement != null}"), other2TextField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        binding.setSourceUnreadableValue(null);
        mainPanel.add(other2TextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(930, 410, 130, -1));

        other2StComboBox.setFont(resourceMap.getFont("erpLStComboBox.font")); // NOI18N
        other2StComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "Bad copy", "Correct", "Draft", "Missing", "Not valid", "Wrong" }));
        other2StComboBox.setName("other2StComboBox"); // NOI18N
        other2StComboBox.setPreferredSize(new java.awt.Dimension(78, 28));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ObjectProperty.create(), other2StComboBox, org.jdesktop.beansbinding.BeanProperty.create("elements"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.other2Status}"), other2StComboBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        binding.setSourceUnreadableValue(null);
        mainPanel.add(other2StComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(1070, 410, 130, -1));

        photoStComboBox.setFont(resourceMap.getFont("erpLStComboBox.font")); // NOI18N
        photoStComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "Bad copy", "Correct", "Draft", "Missing", "Not valid", "Wrong" }));
        photoStComboBox.setName("photoStComboBox"); // NOI18N
        photoStComboBox.setPreferredSize(new java.awt.Dimension(78, 28));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ObjectProperty.create(), photoStComboBox, org.jdesktop.beansbinding.BeanProperty.create("elements"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.photoStatus}"), photoStComboBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        binding.setSourceUnreadableValue(null);
        mainPanel.add(photoStComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 290, 130, -1));

        pahStComboBox.setFont(resourceMap.getFont("erpLStComboBox.font")); // NOI18N
        pahStComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "Bad copy", "Correct", "Draft", "Missing", "Not valid", "Wrong" }));
        pahStComboBox.setName("pahStComboBox"); // NOI18N
        pahStComboBox.setPreferredSize(new java.awt.Dimension(78, 28));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ObjectProperty.create(), pahStComboBox, org.jdesktop.beansbinding.BeanProperty.create("elements"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.pahStatus}"), pahStComboBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        binding.setSourceUnreadableValue(null);
        mainPanel.add(pahStComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 320, 130, -1));

        other1TextField.setFont(resourceMap.getFont("other2TextField.font")); // NOI18N
        other1TextField.setName("other1TextField"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.other1Text}"), other1TextField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement != null}"), other1TextField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        binding.setSourceUnreadableValue(null);
        mainPanel.add(other1TextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 410, 150, -1));

        other1StComboBox.setFont(resourceMap.getFont("erpLStComboBox.font")); // NOI18N
        other1StComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "Bad copy", "Correct", "Draft", "Missing", "Not valid", "Wrong" }));
        other1StComboBox.setName("other1StComboBox"); // NOI18N
        other1StComboBox.setPreferredSize(new java.awt.Dimension(78, 28));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ObjectProperty.create(), other1StComboBox, org.jdesktop.beansbinding.BeanProperty.create("elements"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.other1Status}"), other1StComboBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        binding.setSourceUnreadableValue(null);
        mainPanel.add(other1StComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 410, 130, -1));

        batStComboBox.setFont(resourceMap.getFont("erpLStComboBox.font")); // NOI18N
        batStComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "Bad copy", "Correct", "Draft", "Missing", "Not valid", "Wrong" }));
        batStComboBox.setName("batStComboBox"); // NOI18N
        batStComboBox.setPreferredSize(new java.awt.Dimension(78, 28));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ObjectProperty.create(), batStComboBox, org.jdesktop.beansbinding.BeanProperty.create("elements"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.batteryStatus}"), batStComboBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        binding.setSourceUnreadableValue(null);
        mainPanel.add(batStComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 380, 130, -1));

        cpdStComboBox.setFont(resourceMap.getFont("erpLStComboBox.font")); // NOI18N
        cpdStComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "Bad copy", "Correct", "Draft", "Missing", "Not valid", "Wrong" }));
        cpdStComboBox.setName("cpdStComboBox"); // NOI18N
        cpdStComboBox.setPreferredSize(new java.awt.Dimension(78, 28));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ObjectProperty.create(), cpdStComboBox, org.jdesktop.beansbinding.BeanProperty.create("elements"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.cpdStatus}"), cpdStComboBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        binding.setSourceUnreadableValue(null);
        mainPanel.add(cpdStComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 350, 130, -1));

        jCheckBox1.setFont(resourceMap.getFont("issuesLabel.font")); // NOI18N
        jCheckBox1.setText(resourceMap.getString("jCheckBox1.text")); // NOI18N
        jCheckBox1.setName("jCheckBox1"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.conclusion}"), jCheckBox1, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        mainPanel.add(jCheckBox1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 700, -1, -1));

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jTextArea1.setColumns(20);
        jTextArea1.setFont(resourceMap.getFont("dropboxTextArea.font")); // NOI18N
        jTextArea1.setRows(5);
        jTextArea1.setName("jTextArea1"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.conclusionText}"), jTextArea1, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.conclusion}"), jTextArea1, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jScrollPane1.setViewportView(jTextArea1);

        mainPanel.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 700, 1100, 50));

        dateCreatField.setEnabled(false);
        dateCreatField.setFont(resourceMap.getFont("updateDateChooser.font")); // NOI18N
        dateCreatField.setName("dateCreatField"); // NOI18N
        dateCreatField.setPreferredSize(new java.awt.Dimension(100, 28));
        mainPanel.add(dateCreatField, new org.netbeans.lib.awtextra.AbsoluteConstraints(1050, 200, 130, -1));

        pahTrRadioButton.setFont(resourceMap.getFont("issuesLabel.font")); // NOI18N
        pahTrRadioButton.setText(resourceMap.getString("pahTrRadioButton.text")); // NOI18N
        pahTrRadioButton.setName("pahTrRadioButton"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.pahCe}"), pahTrRadioButton, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        mainPanel.add(pahTrRadioButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 320, -1, 26));

        photoTrRadioButton.setFont(resourceMap.getFont("issuesLabel.font")); // NOI18N
        photoTrRadioButton.setText(resourceMap.getString("photoTrRadioButton.text")); // NOI18N
        photoTrRadioButton.setName("photoTrRadioButton"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.photoTr}"), photoTrRadioButton, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        mainPanel.add(photoTrRadioButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 290, -1, 26));

        cpdCeRadioButton.setFont(resourceMap.getFont("issuesLabel.font")); // NOI18N
        cpdCeRadioButton.setText(resourceMap.getString("cpdCeRadioButton.text")); // NOI18N
        cpdCeRadioButton.setName("cpdCeRadioButton"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.cpdCe}"), cpdCeRadioButton, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        mainPanel.add(cpdCeRadioButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 350, -1, 26));

        batRadioButton.setFont(resourceMap.getFont("issuesLabel.font")); // NOI18N
        batRadioButton.setText(resourceMap.getString("batRadioButton.text")); // NOI18N
        batRadioButton.setName("batRadioButton"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.batteryCe}"), batRadioButton, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        mainPanel.add(batRadioButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 380, -1, 26));

        other1RadioButton.setText(resourceMap.getString("other1RadioButton.text")); // NOI18N
        other1RadioButton.setName("other1RadioButton"); // NOI18N
        other1RadioButton.setPreferredSize(new java.awt.Dimension(20, 20));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.other1}"), other1RadioButton, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        mainPanel.add(other1RadioButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 410, -1, 28));

        psLvdRadioButton.setFont(resourceMap.getFont("issuesLabel.font")); // NOI18N
        psLvdRadioButton.setText(resourceMap.getString("psLvdRadioButton.text")); // NOI18N
        psLvdRadioButton.setName("psLvdRadioButton"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.psLvd}"), psLvdRadioButton, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        mainPanel.add(psLvdRadioButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(910, 290, -1, 26));

        psEmcRadioButton.setFont(resourceMap.getFont("issuesLabel.font")); // NOI18N
        psEmcRadioButton.setText(resourceMap.getString("psEmcRadioButton.text")); // NOI18N
        psEmcRadioButton.setName("psEmcRadioButton"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.psEmc}"), psEmcRadioButton, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        mainPanel.add(psEmcRadioButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(910, 320, -1, 26));

        psRohsRadioButton.setFont(resourceMap.getFont("issuesLabel.font")); // NOI18N
        psRohsRadioButton.setText(resourceMap.getString("psRohsRadioButton.text")); // NOI18N
        psRohsRadioButton.setName("psRohsRadioButton"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.psRohs}"), psRohsRadioButton, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        mainPanel.add(psRohsRadioButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(910, 350, -1, 26));

        psErpRadioButton.setFont(resourceMap.getFont("issuesLabel.font")); // NOI18N
        psErpRadioButton.setText(resourceMap.getString("psErpRadioButton.text")); // NOI18N
        psErpRadioButton.setName("psErpRadioButton"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.psErp}"), psErpRadioButton, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        mainPanel.add(psErpRadioButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(910, 380, -1, 26));

        other2RadioButton.setText(resourceMap.getString("other2RadioButton.text")); // NOI18N
        other2RadioButton.setName("other2RadioButton"); // NOI18N
        other2RadioButton.setPreferredSize(new java.awt.Dimension(20, 20));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.other2}"), other2RadioButton, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        mainPanel.add(other2RadioButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(910, 410, -1, 28));

        erpTrButton.setFont(resourceMap.getFont("issuesLabel.font")); // NOI18N
        erpTrButton.setText(resourceMap.getString("erpTrButton.text")); // NOI18N
        erpTrButton.setName("erpTrButton"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.erpLightTr}"), erpTrButton, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        mainPanel.add(erpTrButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 410, -1, 26));

        rohsTrRadioButton.setFont(resourceMap.getFont("issuesLabel.font")); // NOI18N
        rohsTrRadioButton.setText(resourceMap.getString("rohsTrRadioButton.text")); // NOI18N
        rohsTrRadioButton.setName("rohsTrRadioButton"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.rohsCe}"), rohsTrRadioButton, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        mainPanel.add(rohsTrRadioButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 380, -1, 26));

        rfCeRadioButton.setFont(resourceMap.getFont("issuesLabel.font")); // NOI18N
        rfCeRadioButton.setText(resourceMap.getString("rfCeRadioButton.text")); // NOI18N
        rfCeRadioButton.setName("rfCeRadioButton"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.rfCe}"), rfCeRadioButton, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        mainPanel.add(rfCeRadioButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 350, -1, 26));

        rfTrRadioButton.setFont(resourceMap.getFont("issuesLabel.font")); // NOI18N
        rfTrRadioButton.setText(resourceMap.getString("rfTrRadioButton.text")); // NOI18N
        rfTrRadioButton.setName("rfTrRadioButton"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.rfTr}"), rfTrRadioButton, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        mainPanel.add(rfTrRadioButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 350, -1, 26));

        emcCeRadioButton.setFont(resourceMap.getFont("issuesLabel.font")); // NOI18N
        emcCeRadioButton.setText(resourceMap.getString("emcCeRadioButton.text")); // NOI18N
        emcCeRadioButton.setName("emcCeRadioButton"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.emcCe}"), emcCeRadioButton, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        mainPanel.add(emcCeRadioButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 320, -1, 26));

        emcTrRadioButton.setFont(resourceMap.getFont("issuesLabel.font")); // NOI18N
        emcTrRadioButton.setText(resourceMap.getString("emcTrRadioButton.text")); // NOI18N
        emcTrRadioButton.setName("emcTrRadioButton"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.emcTr}"), emcTrRadioButton, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        mainPanel.add(emcTrRadioButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 320, -1, 26));

        lvdCeRadioButton.setFont(resourceMap.getFont("issuesLabel.font")); // NOI18N
        lvdCeRadioButton.setText(resourceMap.getString("lvdCeRadioButton.text")); // NOI18N
        lvdCeRadioButton.setName("lvdCeRadioButton"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.lvdCe}"), lvdCeRadioButton, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        mainPanel.add(lvdCeRadioButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 290, -1, 26));

        lvdTrRadioButton.setFont(resourceMap.getFont("issuesLabel.font")); // NOI18N
        lvdTrRadioButton.setText(resourceMap.getString("lvdTrRadioButton.text")); // NOI18N
        lvdTrRadioButton.setName("lvdTrRadioButton"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.lvdTr}"), lvdTrRadioButton, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        mainPanel.add(lvdTrRadioButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 290, -1, 26));

        jLabel1.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N
        mainPanel.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 0, -1, -1));

        other3RadioButton.setName("other3RadioButton"); // NOI18N
        other3RadioButton.setPreferredSize(new java.awt.Dimension(20, 21));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.other3}"), other3RadioButton, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        mainPanel.add(other3RadioButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 440, -1, 28));

        other4RadioButton.setName("other4RadioButton"); // NOI18N
        other4RadioButton.setPreferredSize(new java.awt.Dimension(20, 21));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.other4}"), other4RadioButton, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        mainPanel.add(other4RadioButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 440, -1, 30));

        other5RadioButton.setName("other5RadioButton"); // NOI18N
        other5RadioButton.setPreferredSize(new java.awt.Dimension(20, 21));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.other5}"), other5RadioButton, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        mainPanel.add(other5RadioButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(910, 440, -1, 30));

        other3TextField.setFont(resourceMap.getFont("other2TextField.font")); // NOI18N
        other3TextField.setName("other3TextField"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.other3Text}"), other3TextField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement != null}"), other3TextField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        binding.setSourceUnreadableValue(null);
        mainPanel.add(other3TextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 440, 150, 28));

        other3StComboBox.setFont(resourceMap.getFont("erpLStComboBox.font")); // NOI18N
        other3StComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "Bad copy", "Correct", "Draft", "Missing", "Not valid", "Wrong" }));
        other3StComboBox.setName("other3StComboBox"); // NOI18N
        other3StComboBox.setPreferredSize(new java.awt.Dimension(78, 28));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ObjectProperty.create(), other3StComboBox, org.jdesktop.beansbinding.BeanProperty.create("elements"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.other3Status}"), other3StComboBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        binding.setSourceUnreadableValue(null);
        mainPanel.add(other3StComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 440, 130, -1));

        other4TextField.setFont(resourceMap.getFont("other2TextField.font")); // NOI18N
        other4TextField.setName("other4TextField"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.other4Text}"), other4TextField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement != null}"), other4TextField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        binding.setSourceUnreadableValue(null);
        mainPanel.add(other4TextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 440, 150, 28));

        other4StComboBox.setFont(resourceMap.getFont("erpLStComboBox.font")); // NOI18N
        other4StComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "Bad copy", "Correct", "Draft", "Missing", "Not valid", "Wrong" }));
        other4StComboBox.setName("other4StComboBox"); // NOI18N
        other4StComboBox.setPreferredSize(new java.awt.Dimension(78, 28));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ObjectProperty.create(), other4StComboBox, org.jdesktop.beansbinding.BeanProperty.create("elements"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.other4Status}"), other4StComboBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        binding.setSourceUnreadableValue(null);
        mainPanel.add(other4StComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 440, 130, -1));

        other5TextField.setFont(resourceMap.getFont("other2TextField.font")); // NOI18N
        other5TextField.setName("other5TextField"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.other5Text}"), other5TextField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement != null}"), other5TextField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        binding.setSourceUnreadableValue(null);
        mainPanel.add(other5TextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(930, 440, 130, 28));

        other5StComboBox.setFont(resourceMap.getFont("erpLStComboBox.font")); // NOI18N
        other5StComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "Bad copy", "Correct", "Draft", "Missing", "Not valid", "Wrong" }));
        other5StComboBox.setName("other5StComboBox"); // NOI18N
        other5StComboBox.setPreferredSize(new java.awt.Dimension(78, 28));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ObjectProperty.create(), other5StComboBox, org.jdesktop.beansbinding.BeanProperty.create("elements"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.other5Status}"), other5StComboBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        binding.setSourceUnreadableValue(null);
        mainPanel.add(other5StComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(1070, 440, 130, -1));

        descrLabel.setFont(resourceMap.getFont("descrLabel.font")); // NOI18N
        descrLabel.setText(resourceMap.getString("descrLabel.text")); // NOI18N
        descrLabel.setName("descrLabel"); // NOI18N
        mainPanel.add(descrLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 100, -1, 28));

        monsterLabel.setFont(resourceMap.getFont("monsterLabel.font")); // NOI18N
        monsterLabel.setText(resourceMap.getString("monsterLabel.text")); // NOI18N
        monsterLabel.setName("monsterLabel"); // NOI18N
        mainPanel.add(monsterLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 130, -1, 28));

        contactLabel1.setFont(resourceMap.getFont("contactLabel1.font")); // NOI18N
        contactLabel1.setText(resourceMap.getString("contactLabel1.text")); // NOI18N
        contactLabel1.setName("contactLabel1"); // NOI18N
        mainPanel.add(contactLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 130, -1, 28));

        try {
            sampleField1.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("**************************************************")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        sampleField1.setFont(resourceMap.getFont("sampleField1.font")); // NOI18N
        sampleField1.setName("sampleField1"); // NOI18N
        sampleField1.setPreferredSize(new java.awt.Dimension(126, 20));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.descr}"), sampleField1, org.jdesktop.beansbinding.BeanProperty.create("value"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement != null}"), sampleField1, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        binding.setSourceUnreadableValue(null);
        mainPanel.add(sampleField1, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 100, 770, 28));

        try {
            monsterField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("********************")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        monsterField.setFont(resourceMap.getFont("monsterField.font")); // NOI18N
        monsterField.setName("monsterField"); // NOI18N
        monsterField.setPreferredSize(new java.awt.Dimension(126, 20));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.monster}"), monsterField, org.jdesktop.beansbinding.BeanProperty.create("value"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement != null}"), monsterField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        binding.setSourceUnreadableValue(null);
        mainPanel.add(monsterField, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 130, 210, 30));

        try {
            emailField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("******************************")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        emailField.setFont(resourceMap.getFont("emailField.font")); // NOI18N
        emailField.setName("emailField"); // NOI18N
        emailField.setPreferredSize(new java.awt.Dimension(126, 20));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.email}"), emailField, org.jdesktop.beansbinding.BeanProperty.create("value"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement != null}"), emailField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        binding.setSourceUnreadableValue(null);
        mainPanel.add(emailField, new org.netbeans.lib.awtextra.AbsoluteConstraints(1020, 130, 210, 28));

        try {
            itemSField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("********************")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        itemSField.setFont(resourceMap.getFont("itemSField.font")); // NOI18N
        itemSField.setName("itemSField"); // NOI18N
        itemSField.setPreferredSize(new java.awt.Dimension(126, 20));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.itemS}"), itemSField, org.jdesktop.beansbinding.BeanProperty.create("value"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement != null}"), itemSField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        binding.setSourceUnreadableValue(null);
        mainPanel.add(itemSField, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 70, 210, 28));

        jScrollPane3.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane3.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        jScrollPane3.setName("jScrollPane3"); // NOI18N
        jScrollPane3.setPreferredSize(new java.awt.Dimension(706, 91));

        dropboxTextArea.setColumns(100);
        dropboxTextArea.setFont(resourceMap.getFont("dropboxTextArea.font")); // NOI18N
        dropboxTextArea.setRows(5);
        dropboxTextArea.setName("dropboxTextArea"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.dropbox}"), dropboxTextArea, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane3.setViewportView(dropboxTextArea);

        mainPanel.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 490, 1100, 90));

        issuesLabel1.setFont(resourceMap.getFont("issuesLabel1.font")); // NOI18N
        issuesLabel1.setText(resourceMap.getString("issuesLabel1.text")); // NOI18N
        issuesLabel1.setName("issuesLabel1"); // NOI18N
        mainPanel.add(issuesLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 500, -1, -1));

        try {
            supplierField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("********************")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        supplierField.setFont(resourceMap.getFont("supplierField.font")); // NOI18N
        supplierField.setName("supplierField"); // NOI18N
        supplierField.setPreferredSize(new java.awt.Dimension(126, 20));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.supplier}"), supplierField, org.jdesktop.beansbinding.BeanProperty.create("value"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement != null}"), supplierField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        binding.setSourceUnreadableValue(null);
        mainPanel.add(supplierField, new org.netbeans.lib.awtextra.AbsoluteConstraints(1020, 70, 210, 28));

        try {
            contactField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("********************")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        contactField.setFont(resourceMap.getFont("contactField.font")); // NOI18N
        contactField.setName("contactField"); // NOI18N
        contactField.setPreferredSize(new java.awt.Dimension(126, 20));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.contact}"), contactField, org.jdesktop.beansbinding.BeanProperty.create("value"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement != null}"), contactField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        binding.setSourceUnreadableValue(null);
        mainPanel.add(contactField, new org.netbeans.lib.awtextra.AbsoluteConstraints(1020, 100, 210, 28));

        buttonGroup1.add(newitemRadioButton);
        newitemRadioButton.setFont(resourceMap.getFont("newitemRadioButton.font")); // NOI18N
        newitemRadioButton.setText(resourceMap.getString("newitemRadioButton.text")); // NOI18N
        newitemRadioButton.setName("newitemRadioButton"); // NOI18N
        newitemRadioButton.setPreferredSize(new java.awt.Dimension(107, 28));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.newitem}"), newitemRadioButton, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        mainPanel.add(newitemRadioButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(750, 170, -1, -1));

        printButton.setFont(resourceMap.getFont("printButton.font")); // NOI18N
        printButton.setText(resourceMap.getString("printButton.text")); // NOI18N
        printButton.setName("printButton"); // NOI18N
        printButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printButtonActionPerformed(evt);
            }
        });
        mainPanel.add(printButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 760, -1, 25));

        folderButton.setText(resourceMap.getString("folderButton.text")); // NOI18N
        folderButton.setName("folderButton"); // NOI18N
        folderButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                folderButtonActionPerformed(evt);
            }
        });
        mainPanel.add(folderButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 760, -1, 25));

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(samples.SamplesApp.class).getContext().getActionMap(SamplesView.class, this);
        newButton.setAction(actionMap.get("newRecord")); // NOI18N
        newButton.setName("newButton"); // NOI18N
        mainPanel.add(newButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 760, 71, 25));

        deleteButton.setAction(actionMap.get("deleteRecord")); // NOI18N
        deleteButton.setName("deleteButton"); // NOI18N
        mainPanel.add(deleteButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 760, -1, 25));

        refreshButton.setAction(actionMap.get("refresh")); // NOI18N
        refreshButton.setName("refreshButton"); // NOI18N
        mainPanel.add(refreshButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 760, -1, 25));

        saveButton.setAction(actionMap.get("save")); // NOI18N
        saveButton.setName("saveButton"); // NOI18N
        mainPanel.add(saveButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(780, 760, 71, 25));

        jFilter.setFont(resourceMap.getFont("jFilter.font")); // NOI18N
        jFilter.setName("jFilter"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${rowSorter}"), jFilter, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setConverter(rowSorterToStringConverter1);
        bindingGroup.addBinding(binding);

        mainPanel.add(jFilter, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 10, 150, 30));

        jCheckBox2.setFont(resourceMap.getFont("jCheckBox2.font")); // NOI18N
        jCheckBox2.setText(resourceMap.getString("jCheckBox2.text")); // NOI18N
        jCheckBox2.setName("jCheckBox2"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.hidden}"), jCheckBox2, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        mainPanel.add(jCheckBox2, new org.netbeans.lib.awtextra.AbsoluteConstraints(1000, 240, -1, -1));

        jScrollPane4.setName("jScrollPane4"); // NOI18N

        IssuesTextPane.setFont(resourceMap.getFont("IssuesTextPane.font")); // NOI18N
        IssuesTextPane.setName("IssuesTextPane"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.issues}"), IssuesTextPane, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        IssuesTextPane.addMouseListener(new ContextMenuMouseListener());
        jScrollPane4.setViewportView(IssuesTextPane);

        mainPanel.add(jScrollPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 590, 1100, 100));

        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        newRecordMenuItem.setAction(actionMap.get("newRecord")); // NOI18N
        newRecordMenuItem.setName("newRecordMenuItem"); // NOI18N
        fileMenu.add(newRecordMenuItem);

        deleteRecordMenuItem.setAction(actionMap.get("deleteRecord")); // NOI18N
        deleteRecordMenuItem.setName("deleteRecordMenuItem"); // NOI18N
        fileMenu.add(deleteRecordMenuItem);

        jSeparator1.setName("jSeparator1"); // NOI18N
        fileMenu.add(jSeparator1);

        saveMenuItem.setAction(actionMap.get("save")); // NOI18N
        saveMenuItem.setName("saveMenuItem"); // NOI18N
        fileMenu.add(saveMenuItem);

        refreshMenuItem.setAction(actionMap.get("refresh")); // NOI18N
        refreshMenuItem.setName("refreshMenuItem"); // NOI18N
        fileMenu.add(refreshMenuItem);

        jSeparator2.setName("jSeparator2"); // NOI18N
        fileMenu.add(jSeparator2);

        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        jMenu1.setText(resourceMap.getString("jMenu1.text")); // NOI18N
        jMenu1.setName("jMenu1"); // NOI18N

        jCheckBoxMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_H, java.awt.event.InputEvent.CTRL_MASK));
        jCheckBoxMenuItem1.setSelected(false);
        jCheckBoxMenuItem1.setText(resourceMap.getString("jCheckBoxMenuItem1.text")); // NOI18N
        jCheckBoxMenuItem1.setName("jCheckBoxMenuItem1"); // NOI18N
        jMenu1.add(jCheckBoxMenuItem1);

        menuBar.add(jMenu1);

        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        statusPanel.setName("statusPanel"); // NOI18N
        statusPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N
        statusPanel.add(statusPanelSeparator, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1200, -1));

        statusMessageLabel.setName("statusMessageLabel"); // NOI18N
        statusPanel.add(statusMessageLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 8, -1, -1));

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N
        statusPanel.add(statusAnimationLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(1180, 5, -1, -1));

        progressBar.setName("progressBar"); // NOI18N
        statusPanel.add(progressBar, new org.netbeans.lib.awtextra.AbsoluteConstraints(1030, 5, -1, -1));

        rowSorterToStringConverter1.setTable(masterTable);

        setComponent(mainPanel);
        setMenuBar(menuBar);
        setStatusBar(statusPanel);

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void folderButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_folderButtonActionPerformed

String path = sampleField.getText();
//String supplier_name = supplierField.getText();
//String or_item_n = itemSField.getText();

String path1=path.replace("/","_");
path1=path1.replaceAll("\\s","");
//String supplier_name1=supplier_name.replaceAll("\\s","");
//String or_item_n1=or_item_n.replaceAll("\\s","");

File mainpath = new File("R:/Quality Management/Samples");

//File file = new File(mainpath+"/"+path1+"_("+or_item_n1+")_"+supplier_name1);
File file = new File(mainpath+"/"+path1);

Desktop desktop = null;

    if (Desktop.isDesktopSupported())
    {
    desktop = Desktop.getDesktop();
    }

    try 
    {
            if (file.exists())
            {
                desktop.open(file);
            }
            else
            {
             int n = JOptionPane.showConfirmDialog(null, "Would you like to create one?","Folder doesn't exist !!!",
             JOptionPane.YES_NO_OPTION);
             
             if (n==JOptionPane.OK_OPTION)
             {
                 String [] foldery ={"Inapplicable", "LVD_not correct", "EMC_not correct", "RF_not correct", "RoHS_not correct", 
                     "ErP_not correct", "Photobiological_not correct", "PAH_not correct", "CPD_not correct", 
                     "Battery_not correct", "AdaptorLVD_not correct", "AdaptorEMC_not correct", "AdaptorRoHS_not correct",
                     "AdaptorErP_not correct", "Pictures", "7-zip files"};
                 for (int i=0; i<foldery.length;i++)
                 {
                    File cert = new File (file+ "/(" +i +")"+ foldery[i]);
                     cert.mkdirs();
                     
                 }
                 
                 
               desktop.open(file);
             }
            
            }
            
    }
 catch (IOException e){}
    }//GEN-LAST:event_folderButtonActionPerformed

    private void printButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printButtonActionPerformed
PrinterJob pj = PrinterJob.getPrinterJob();
    pj.setJobName(sampleField.getText()+" sheet");
    
     PageFormat documentPageFormat = new PageFormat();
    documentPageFormat.setOrientation(PageFormat.LANDSCAPE);
    
  pj.setPrintable (new Printable() {    
    public int print(Graphics pg, PageFormat pf, int pageNum)
    {
      if (pageNum > 0){
      return Printable.NO_SUCH_PAGE;
      }
    
        Graphics2D g2 = (Graphics2D) pg;
        g2.translate(pf.getImageableX(), pf.getImageableY());
            //g2.rotate(1.565, 450, 350);


        g2.scale(0.66, 0.7);
       
    
        mainPanel.paint(g2);
        //mainPanel.printAll(g2);
     
      return Printable.PAGE_EXISTS;
    }
  });
  if (pj.printDialog() == false)
  return;

  try {
        pj.print();
  } catch (PrinterException ex) {
        // handle exception
  }
    }//GEN-LAST:event_printButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextPane IssuesTextPane;
    private javax.swing.JRadioButton assortRadioButton;
    private javax.swing.JRadioButton batRadioButton;
    private javax.swing.JComboBox batStComboBox;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox buyerComboBox;
    private javax.swing.JLabel buyerLabel;
    private javax.swing.JCheckBox checkCeCheckBox;
    private com.toedter.calendar.JDateChooser checkDateChooser;
    private javax.swing.JCheckBox checkFunCheckBox;
    private javax.swing.JCheckBox checkTechCheckBox;
    private javax.swing.JFormattedTextField contactField;
    private javax.swing.JLabel contactLabel;
    private javax.swing.JLabel contactLabel1;
    private javax.swing.JRadioButton cpdCeRadioButton;
    private javax.swing.JComboBox cpdStComboBox;
    private javax.swing.JLabel dateCheckLabel;
    private com.toedter.calendar.JDateChooser dateCreatField;
    private javax.swing.JLabel dateCreatLabel;
    private javax.swing.JLabel dateUpdateLabel;
    private javax.swing.JButton deleteButton;
    private javax.swing.JLabel descrLabel;
    private javax.swing.JTextArea dropboxTextArea;
    private javax.swing.JFormattedTextField emailField;
    private javax.swing.JRadioButton emcCeRadioButton;
    private javax.swing.JComboBox emcStComboBox;
    private javax.swing.JRadioButton emcTrRadioButton;
    private javax.persistence.EntityManager entityManager;
    private javax.swing.JComboBox erpLStComboBox;
    private javax.swing.JRadioButton erpTrButton;
    private javax.swing.JButton folderButton;
    private javax.swing.JLabel issuesLabel;
    private javax.swing.JLabel issuesLabel1;
    private javax.swing.JFormattedTextField itemSField;
    private javax.swing.JLabel itemSLabel;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem1;
    private javax.swing.JTextField jFilter;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTextArea jTextArea1;
    private java.util.List<samples.Samples> list;
    private javax.swing.JRadioButton lvdCeRadioButton;
    private javax.swing.JComboBox lvdStComboBox;
    private javax.swing.JRadioButton lvdTrRadioButton;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JScrollPane masterScrollPane;
    private javax.swing.JTable masterTable;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JFormattedTextField monsterField;
    private javax.swing.JLabel monsterLabel;
    private javax.swing.JButton newButton;
    private javax.swing.JRadioButton newitemRadioButton;
    private javax.swing.JRadioButton other1RadioButton;
    private javax.swing.JComboBox other1StComboBox;
    private javax.swing.JTextField other1TextField;
    private javax.swing.JRadioButton other2RadioButton;
    private javax.swing.JComboBox other2StComboBox;
    private javax.swing.JTextField other2TextField;
    private javax.swing.JRadioButton other3RadioButton;
    private javax.swing.JComboBox other3StComboBox;
    private javax.swing.JTextField other3TextField;
    private javax.swing.JRadioButton other4RadioButton;
    private javax.swing.JComboBox other4StComboBox;
    private javax.swing.JTextField other4TextField;
    private javax.swing.JRadioButton other5RadioButton;
    private javax.swing.JComboBox other5StComboBox;
    private javax.swing.JTextField other5TextField;
    private javax.swing.JComboBox pahStComboBox;
    private javax.swing.JRadioButton pahTrRadioButton;
    private javax.swing.JComboBox photoStComboBox;
    private javax.swing.JRadioButton photoTrRadioButton;
    private javax.swing.JButton printButton;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JRadioButton promoRadioButton;
    private javax.swing.JRadioButton psEmcRadioButton;
    private javax.swing.JComboBox psEmcStComboBox;
    private javax.swing.JRadioButton psErpRadioButton;
    private javax.swing.JComboBox psErpStComboBox;
    private javax.swing.JRadioButton psLvdRadioButton;
    private javax.swing.JComboBox psLvdStComboBox;
    private javax.swing.JRadioButton psRohsRadioButton;
    private javax.swing.JComboBox psRohsStComboBox;
    private javax.persistence.Query query;
    private javax.swing.JButton refreshButton;
    private javax.swing.JCheckBox replCheckBox;
    private javax.swing.JTextField replacementItemField;
    private javax.swing.JRadioButton rfCeRadioButton;
    private javax.swing.JComboBox rfStComboBox;
    private javax.swing.JRadioButton rfTrRadioButton;
    private javax.swing.JComboBox rohsStComboBox;
    private javax.swing.JRadioButton rohsTrRadioButton;
    private samples.RowSorterToStringConverter rowSorterToStringConverter1;
    private javax.swing.JFormattedTextField sampleField;
    private javax.swing.JFormattedTextField sampleField1;
    private javax.swing.JLabel sampleLabel;
    private javax.swing.JButton saveButton;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JFormattedTextField supplierField;
    private javax.swing.JLabel supplierLabel;
    private com.toedter.calendar.JDateChooser updateDateChooser;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;

    private JDialog aboutBox;

    private boolean saveNeeded;
}
