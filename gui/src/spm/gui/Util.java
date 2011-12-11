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

import java.io.*;
import java.net.*;
import java.util.logging.*;
import java.awt.*;
import javax.swing.*;

/**
 * Utility methods for the GUI.
 * 
 * @author Zachary Scott <cthug.zs@gmail.com>
 */
public final class Util {
    
    private final static Logger logger = Logger.getLogger(Util.class.getName());
    
    /**
     * Shows a dialog with a message.
     * 
     * @param parent owner of the resulting dialog.
     * @param msg message to show the user.
     */
    public static void showInfoDialog(final Frame parent, final String msg) {
        
        JOptionPane.showMessageDialog(parent, msg);
        
    }
    
    /**
     * Shows a dialog with a message indicating a potential problem.
     * 
     * @param parent owner of the resulting dialog.
     * @param msg message to show the user.
     */
    public static void showWarningDialog(final Frame parent, final String msg) {
        
        JOptionPane.showMessageDialog(parent,
                                      msg,
                                      "Warning",
                                      JOptionPane.WARNING_MESSAGE);
        
    }
    
    /**
     * Shows a dialog with a message indicating a problem.
     * 
     * @param parent owner of the resulting dialog.
     * @param msg message to show the user.
     */
    public static void showErrorDialog(final Frame parent, final String msg) {
        
        JOptionPane.showMessageDialog(parent,
                                      msg,
                                      "Error",
                                      JOptionPane.ERROR_MESSAGE);
        
    }
    
    /**
     * Displays a dialog indicating that the program is loading something.
     * 
     * @param parent owner frame of the loading dialog.
     * @param string the message to display to the user.
     * @return the instance of the loading dialog that is created.
     */
    public static LoadingDialog showLoadingDialog(final Frame parent, final String string) {
        
        final LoadingDialog loading = new LoadingDialog(parent);
        loading.setLoadingText(string);
         
        return loading;
        
    }
    
    /**
     * Displays the web page represented by the given {@code URI}.
     * 
     * @param parent owner of the resulting windows.
     * @param uri location of the web page.
     */
    public static void showInWebBrowser(final Frame parent, final URI uri) {
        
        // attempt to display wiki page in web browser
        try {
            Desktop.getDesktop().browse(uri);
        } catch (IOException ex) {
            
            StringBuilder msg = new StringBuilder();
            
            msg.append("Cannot open a web browser to display a web page! ");
            msg.append("The address of the page is \"");
            msg.append(uri.getRawPath());
            msg.append("\".");
            
            logger.log(Level.INFO, msg.toString(), ex);
            showErrorDialog(parent, msg.toString());
            
        }
        
    }
    
}

// EOF
