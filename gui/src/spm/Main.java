package spm;
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
import java.util.ArrayList;
import java.util.logging.*;
import java.awt.EventQueue;
import javax.swing.UIManager;

import spm.gui.PrimaryFrame;

/**
 * Entry point of the program.
 *
 * @author Zachary Scott <cthug.zs@gmail.com>
 */
public final class Main {

    private final static Logger logger = Logger.getLogger(Main.class.getName());

    /** XML log output file. */
    public final static String XML_LOG_FILE = System.getProperty("user.home") + 
                                              File.separator + 
                                              Config.CMD_NAME +
                                              ".log.xml";
    
    // TODO add logo to window frames
    // TODO create makefile for installation
    
    // initialises the program
    private static void init() {
        
        // initialise logger
        Logger rootLogger = Logger.getLogger("");
        rootLogger.setLevel(Level.CONFIG);
        
        // setup console log handler
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new SimpleFormatter());
        rootLogger.addHandler(consoleHandler);
        
        try {
        
            // remove log file if it already exists
            File xmlLog = new File(XML_LOG_FILE);
            if (xmlLog.exists())
                xmlLog.delete();
            
            // setup XML log handler
            FileHandler file = new FileHandler(XML_LOG_FILE);
            file.setFormatter(new XMLFormatter());
            rootLogger.addHandler(file);
        
        } catch (IOException ex) {
            
            StringBuilder msg = new StringBuilder();
            
            msg.append("Cannot create XML log file \"");
            msg.append(XML_LOG_FILE);
            msg.append("\".");
            
            logger.log(Level.INFO, msg.toString(), ex);
            
        }
                
        try {
            // set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
           logger.log(Level.CONFIG, "Cannot set system look and feel!", ex);
        }
        
    }
    
    // show command line usage information
    private static void usage() {
        
        StringBuilder msg = new StringBuilder();
        
        msg.append('\n');
        msg.append("Usage: ");
        msg.append(Config.CMD_NAME);
        msg.append(" [package files ...] [options ...]");
        msg.append('\n');
        
        msg.append("Options: ");
        msg.append("    --help OR -h");
        msg.append("        Show this usage information.");
        msg.append("    --install OR -i");
        msg.append("        Open and install the given packages.");
        msg.append("    --update OR -u");
        msg.append("        Open and update the given packages.");
        msg.append("    --uninstall OR --remove OR -r");
        msg.append("        Open and remove the given packages.");
        
        msg.append('\n');
        
        System.out.print(msg.toString());
        
        System.exit(0);
        
    }
    
    // command line options
    private static boolean installFlag = false;
    private static boolean updateFlag = false;
    private static boolean uninstallFlag = false;
    private static File[] packages = null; // packages that are given on the command line
    
    // handle the command line arguments
    private static void handleCmdl(final String[] args) {
        
        ArrayList<File> packageFiles = new ArrayList<File>();
        
        // parse arguments
        for (int i = 1; i < args.length; i++) {
            
            // parse argument
            try {
            
                if (args[i].charAt(0) == '-') { // handle command line option

                    if (args[i].equals("--help") || args[i].equals("-h")) {

                        usage();

                    } else if (args[i].equals("--install") || args[i].equals("-i")) {

                        if (installFlag) {
                            throw new Exception("Ignoring multiple install flags.");
                        }

                        installFlag = true;

                    } else if (args[i].equals("--update") || args[i].equals("-u")) {

                        if (updateFlag) {
                            throw new Exception("Ignoring multiple update flags.");
                        }

                        updateFlag = true;

                    } else if (args[i].equals("--uninstall") || 
                               args[i].equals("--remove") || 
                               args[i].equals("-r")) {

                        if (uninstallFlag) {
                            throw new Exception("Ignoring multiple un-install flags.");
                        }

                        uninstallFlag = true;

                    }

                } else { // handle input package

                    packageFiles.add(new File(args[i]));

                }
            
            } catch (Exception ex) {
                
                StringBuilder msg = new StringBuilder();
                
                msg.append(Config.CMD_NAME);
                msg.append(": error: ");
                msg.append(ex.getMessage());
                msg.append('\n');
                
                logger.log(Level.INFO, msg.toString(), ex);
                
            }
            
        }
        
        // set packages to be opened
        File[] filesArray = new File[packageFiles.size()];
        packages = packageFiles.toArray(filesArray);
        
        // handle when no packages are given in the command line
        if (packages.length == 0) {
            packages = new File[1];
            packages[0] = null;
        }
        
    }
    
    /**
     * Entry point of the program.
     * 
     * @param args the command line arguments
     */
    public static void main(final String[] args) {
        
        init();
        
        // handle command line parameters
        handleCmdl(args);
        
        // start GUI
        for (File packageFile : packages) {
            
            // start each new PrimaryFrame
            PrimaryFrame.startNewInstance(packageFile);
            
        }
        
    }
    
}

// EOF
