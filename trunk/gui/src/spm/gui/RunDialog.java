package spm.gui;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.awt.*;
import javax.swing.*;

import spm.format.*;
import static spm.gui.Util.*;

/**
 * User interface for install, un-installing and updating a package.
 * 
 * @author Zachary Scott <cthug.zs@gmail.com>
 */
public final class RunDialog extends JDialog {

    private final static Logger logger = Logger.getLogger(PrimaryFrame.class.getName());
    
    private final SPMPackage packageArchive;
    
    private Frame parent;
    
    /**
     * Create a new instance of {@code RunDialog} for the given package.
     * 
     * @param parent parent frame of this dialog.
     * @param packageArchive package to manipulate (must NOT be {@code null}).
     */
    public RunDialog(Frame parent, SPMPackage packageArchive) {
        
        super(parent, true);
        this.parent = parent;
        
        if (packageArchive == null)
            throw new IllegalArgumentException("Package must not be null!");
        
        this.packageArchive = packageArchive;
        
        initComponents();
        setLocationRelativeTo(parent);
        
    }
    
    /**
     * Installs the package to the system.
     * 
     */
    public void install() {
        
        // install package and display the output
        try {
            
            SPMExecutor executor = packageArchive.getInstallExecutor();
            txtOutput.setText(executor.getOutput());
            
            if (executor.failed()) {
                showErrorDialog(parent, "Package failed to install!");
                setVisible(true);
            } else {
                showInfoDialog(parent, "Package was installed successfully!");
            }
            
        } catch (FileNotFoundException ex) {
            
            String msg = "Installation files are missing from the package!";
            
            showErrorDialog(parent, msg);
            logger.log(Level.INFO, msg, ex);
            
        } catch (InvalidPackageException ex) {
            
            StringBuilder msg = new StringBuilder();
            
            msg.append("The package \"");
            msg.append(packageArchive.getName());
            msg.append("\" is not valid! \n");
            msg.append(ex.getMessage());
            
            showErrorDialog(parent, msg.toString());
            logger.log(Level.INFO, msg.toString(), ex);
            
        } catch (IOException ex) {
            
            StringBuilder msg = new StringBuilder();
            
            msg.append("Cannot execute installation files from the package \"");
            msg.append(packageArchive.getName());
            msg.append("\"! \n");
            msg.append(ex.getMessage());
            
            showErrorDialog(parent, msg.toString());
            logger.log(Level.INFO, msg.toString(), ex);
            
        }
        
    }
    
    /**
     * Un-installs the package from the system.
     * 
     */
    public void uninstall() {
        
        // uninstall package and display the output
        try {
            
            SPMExecutor executor = packageArchive.getUninstallExecutor();
            txtOutput.setText(executor.getOutput());
            
            if (executor.failed()) {
                showErrorDialog(parent, "Package failed to un-install!");
                setVisible(true);
            } else {
                showInfoDialog(parent, "Package was un-installed successfully!");
            }
            
        } catch (FileNotFoundException ex) {
            
            String msg = "Un-installation files are missing from the package!";
            
            showErrorDialog(parent, msg);
            logger.log(Level.INFO, msg, ex);
            
        } catch (InvalidPackageException ex) {
            
            StringBuilder msg = new StringBuilder();
            
            msg.append("The package \"");
            msg.append(packageArchive.getName());
            msg.append("\" is not valid! \n");
            msg.append(ex.getMessage());
            
            showErrorDialog(parent, msg.toString());
            logger.log(Level.INFO, msg.toString(), ex);
            
        } catch (IOException ex) {
            
            StringBuilder msg = new StringBuilder();
            
            msg.append("Cannot execute un-installation files from the package \"");
            msg.append(packageArchive.getName());
            msg.append("\"! \n");
            msg.append(ex.getMessage());
            
            showErrorDialog(parent, msg.toString());
            logger.log(Level.INFO, msg.toString(), ex);
            
        }
        
    }
    
    /**
     * Updates the package on the system.
     * 
     */
    public void update() {
        
        // update package and display the output
        try {
            
            SPMExecutor executor = packageArchive.getUpdateExecutor();
            txtOutput.setText(executor.getOutput());
            
            if (executor.failed()) {
                showErrorDialog(parent, "Package failed to update!");
                setVisible(true);
            } else {
                showInfoDialog(parent, "Package was updated successfully!");
            }
            
        } catch (FileNotFoundException ex) {
            
            String msg = "Update files are missing from the package!";
            
            showErrorDialog(parent, msg);
            logger.log(Level.INFO, msg, ex);
            
        } catch (InvalidPackageException ex) {
            
            StringBuilder msg = new StringBuilder();
            
            msg.append("The package \"");
            msg.append(packageArchive.getName());
            msg.append("\" is not valid! \n");
            msg.append(ex.getMessage());
            
            showErrorDialog(parent, msg.toString());
            logger.log(Level.INFO, msg.toString(), ex);
            
        } catch (IOException ex) {
            
            StringBuilder msg = new StringBuilder();
            
            msg.append("Cannot execute update files from the package \"");
            msg.append(packageArchive.getName());
            msg.append("\"! \n");
            msg.append(ex.getMessage());
            
            showErrorDialog(parent, msg.toString());
            logger.log(Level.INFO, msg.toString(), ex);
            
        }
        
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        txtOutput = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(packageArchive.getName());

        jScrollPane1.setToolTipText("");

        txtOutput.setColumns(60);
        txtOutput.setEditable(false);
        txtOutput.setFont(new java.awt.Font("Liberation Mono", 0, 13)); // NOI18N
        txtOutput.setRows(12);
        jScrollPane1.setViewportView(txtOutput);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 482, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 284, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea txtOutput;
    // End of variables declaration//GEN-END:variables

}

// EOF
