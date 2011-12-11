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
import java.util.zip.*;

import spm.format.tar.*;


/**
 * Wraps a {@code TarArchive} to represent a SPM GZIP'd tar archive.
 *
 * @author Zachary Scott <cthug.zs@gmail.com>
 */
public final class SPMPackageContainer extends TarArchive {
    
    /**
     * Creates a new instance of {@code SPMPackageContainer}.
     * 
     */
    public SPMPackageContainer() {
        
    }
    
    /**
     * Reads the given stream into this {@code SPMPackageContainer}.
     * 
     * @param input stream to read from.
     * @throws InvalidPackageException if it is not a valid SPM package archive.
     * @throws IOException upon failure to read from input stream.
     */
    @Override
    public void read(final InputStream input) throws InvalidPackageException, IOException {
        
        GZIPInputStream gzinput = new GZIPInputStream(input);
        
        super.read(gzinput);
        
        gzinput.close();
        
    }
    
    /**
     * Reads the given stream into this {@code SPMPackageContainer}.
     * 
     * @param output stream to write to.
     * @throws InvalidPackageException if it is not a valid SPM package archive.
     * @throws IOException upon failure to read from input stream.
     */
    @Override
    public void write(final OutputStream output) throws IOException {
        
        GZIPOutputStream gzoutput = new GZIPOutputStream(output);
        
        super.write(gzoutput);
        
        gzoutput.close();
        
    }
    
}

// EOF
