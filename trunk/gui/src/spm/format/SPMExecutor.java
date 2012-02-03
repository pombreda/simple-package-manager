package spm.format;
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

/**
 * Handle the execution of a {@code SPMPackage}'d files.
 * 
 * @author Zachary Scott <cthug.zs@gmail.com>
 */
public final class SPMExecutor {

    private final Process[] processes;
    
    SPMExecutor(final Process[] processes) {
        
        this.processes = processes;
        
    }
    
    /**
     * Returns whether the processes of this {@code SPMExecutor} failed or not.
     * 
     * @return whether the processes of this {@code SPMExecutor} failed or not.
     */
    public boolean failed() {
        
        boolean failedFlag = false;
        
        for (int i = 0; !failedFlag && i < processes.length; i++) {
        
            // wait for process to finish
            while (true) {
                
                try {
                    processes[i].waitFor();
                    break;
                } catch (InterruptedException ex) {
                }
                
            }
            
            // get return value
            failedFlag = failedFlag || processes[i].exitValue() != 0;
        
        }
        
        return failedFlag;
        
    }
    
    /**
     * Returns the output stream of this {@code SPMExecutor}'s processes.
     * 
     * @return the output stream of this {@code SPMExecutor}'s processes.
     * @throws IOException upon failure to read from a processes output stream.
     */
    public String getOutput() throws IOException {
        
        StringBuilder string = new StringBuilder();
        
        // concatenate each process's output streams
        for (Process process : processes) {
        
            InputStream out = process.getInputStream();
            InputStream err = process.getErrorStream();

            // read output streams
            int ch;
            while ((ch = out.read()) != -1)
                string.append((char) ch);
            while ((ch = err.read()) != -1)
                string.append((char) ch);
        
        }
        
        return string.toString();
        
    }
    
}

// EOF
