package spm.gui;
/* Copyright (C) 2011, Zachary Scott <cthug.zs@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

/**
 * Displays a loading dialog while a a thread is running.
 * 
 * @author Zachary Scott <cthug.zs@gmail.com>
 */
public class LoadingDialog extends JDialog implements Runnable {

    // the thread that is loading
    private final Thread thread;
    
    /** 
     * Creates new form {@code LoadingDialog}.
     *
     * @param parent - the owner parent frame of this {@code LoadingDialog}.
     * @param thread - the thread that is loading.
     */
    public LoadingDialog(final Frame parent, final Thread thread) {
        
        super(parent, true);
        
        initComponents();
        setLocationRelativeTo(parent);
        
        this.thread = thread;
        
    }

    /**
     * Sets the text that is displayed while loading.
     * 
     * @param string text to be displayed.
     */
    public void setLoadingText(final String string) {
        setTitle(string);
        lblLoading.setText(string);
    }
    
    // whether or not this dialog has been closed
    private boolean closedFlag = false;
    
    /**
     * Waits for the loading thread, and then closes the loading dialog.
     * 
     */
    @Override
    public void run() {
        
        try {
            thread.join();
        } catch (InterruptedException ex) {
        }
        
        close();
        
    }
    
    /**
     * Closes this {@code LoadingDialog}.
     * 
     */
    public void close() {
        dispose();
        thread.stop();
        closedFlag = true;
    }
    
    /**
     * Returns whether this {@code LoadingDialog} has been closed or not.
     * 
     */
    public boolean isClosed() {
        return closedFlag;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        progressBar = new javax.swing.JProgressBar();
        lblLoading = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Loading ...");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

        progressBar.setIndeterminate(true);

        lblLoading.setText("Loading ...");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(progressBar, javax.swing.GroupLayout.DEFAULT_SIZE, 256, Short.MAX_VALUE)
                    .addComponent(lblLoading))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(lblLoading)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        close();
    }//GEN-LAST:event_formWindowClosed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblLoading;
    private javax.swing.JProgressBar progressBar;
    // End of variables declaration//GEN-END:variables

}

// EOF
