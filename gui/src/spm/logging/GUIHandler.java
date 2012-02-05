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

import java.util.logging.*;

import static spm.gui.Util.*;

/**
 * GUI log output handler.
 * 
 * @author Zachary Scott <cthug.zs@gmail.com>
 */
public class GUIHandler extends Handler {

    @Override
    public void publish(final LogRecord record) {
        
        Level level = record.getLevel();
        
        String logMsg = record.getMessage();
        
        String throwableMsg = null;
        if (record.getThrown() != null)
            throwableMsg = record.getThrown().getMessage();
        
        // create message to give to the user
        String msg = "";
        if (logMsg != null) {
            msg += logMsg;
        }
        if (throwableMsg != null) {
            msg += " ";
            msg += throwableMsg;
        }
        
        if (level == Level.SEVERE) {
            showErrorDialog(null, msg);
        } else if (level == Level.WARNING) {
            showWarningDialog(null, msg);
        } else if (level == Level.INFO) {
            showInfoDialog(null, msg);
        }
        
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() throws SecurityException {
    }
    
}

// EOF
