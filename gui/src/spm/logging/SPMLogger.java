package spm.logging;
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

import java.io.File;
import java.io.IOException;
import java.util.logging.*;

import spm.Config;

/**
 * 
 * 
 * @author Zachary Scott <cthug.zs@gmail.com>
 */
public final class SPMLogger {
    
    private final static Logger logger = Logger.getLogger(SPMLogger.class.getName());
    
    /**
     * Initializes the logger system.
     * 
     */
    public static void init() {
        
        // initialise logger
        Logger rootLogger = Logger.getLogger("");
        rootLogger.setLevel(Level.CONFIG);
        
        // setup GUI handler
        GUIHandler guiHandler = new GUIHandler();
        rootLogger.addHandler(guiHandler);
        
        // setup console log handler
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new SimpleFormatter());
        rootLogger.addHandler(consoleHandler);
        
        try {
        
            // remove log file if it already exists
            File xmlLog = new File(Config.XML_LOG_FILE);
            if (xmlLog.exists())
                xmlLog.delete();
            
            // setup XML log handler
            FileHandler file = new FileHandler(Config.XML_LOG_FILE);
            file.setFormatter(new XMLFormatter());
            rootLogger.addHandler(file);
        
        } catch (IOException ex) {
            
            StringBuilder msg = new StringBuilder();
            
            msg.append("Cannot create XML log file \"");
            msg.append(Config.XML_LOG_FILE);
            msg.append("\".");
            
            logger.log(Level.INFO, msg.toString(), ex);
            
        }
        
    }
    
}

// EOF
