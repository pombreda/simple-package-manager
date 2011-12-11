package spm.format.tar;
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

import static spm.format.tar.TarHeader.BLOCK_SIZE;
import spm.format.InvalidPackageException;

/**
 * Represents a record in a TAR (Tape ARchive) file-system (USTAR).
 * 
 * @author Zachary Scott <cthug.zs@gmail.com>
 */
public final class TarRecord {
        
    // header for the file
    private TarHeader header = new TarHeader();
    
    // contents of the file
    private byte[] fileContents;
    
    /**
     * Creates a new instance of {@code TarRecord}.
     * 
     */
    public TarRecord() {
        
    }
    
    /**
     * Creates a new instance of {@code TarRecord}.
     * 
     * @param header header describing the file on the tar file system.
     * @param fileContents the contents of the file.
     */
    public TarRecord(final TarHeader header, final byte[] fileContents) {
        
        this.header = header;
        this.fileContents = fileContents;
        
    }
    
    // reads a block of input from a stream
    private static int readInput(final InputStream input, final byte[] block) throws IOException {
        
        int i, b;
        int size = block.length;
        
        // read block from input stream
        for (i = 0; i < size && (b = input.read()) != -1; i++) 
            block[i] = (byte) b;
        
        return i;
        
    }
    
    /**
     * Reads the next {@code TarRecord} from the given input stream into this {@code TarRecord}.
     * 
     * @param input The input stream to be read.
     * @throws InvalidPackageException if the input stream is not in a valid tar format.
     * @throws IOException upon failure to read from the input stream.
     */
    public void read(final InputStream input) throws InvalidPackageException, IOException {
        
        header = new TarHeader();
        
        byte[] block = new byte[BLOCK_SIZE];

        // read block from stream
        int num = readInput(input, block);

        // catch EOF
        if (num <= 0)
            return;

        // catch mis-aligned archive
        if ((num % BLOCK_SIZE) != 0) {

            StringBuilder msg = new StringBuilder();

            msg.append("Archive is not aligned to ");
            msg.append(BLOCK_SIZE);
            msg.append(" bytes!");

            throw new InvalidPackageException(msg.toString());

        }

        // decode the files tar header
        header.decode(block);
        
        // read file contents
        long size = header.getFileSize();
        if (!header.isEmpty() && size > 0) {
            
            fileContents = new byte[(int) size];
            
            // read file contents
            num = readInput(input, fileContents);
            
            // read remainer of block
            size %= BLOCK_SIZE;
            if (size != 0) {
                num = (int) input.skip(BLOCK_SIZE - size);
            }
            
        }
        
    }

    /**
     * Writes this record to the stream {@code output} (in USTAR format).
     * 
     * @param output stream to write to.
     * @throws IOException upon failure to write to stream.
     */
    public void write(final OutputStream output) throws IOException {
        
        byte[] block = header.encode();
        
        int fileIndex = 0;
        
        // get the size of the data to be written
        int size;
        if (fileContents == null) {
            size = BLOCK_SIZE;
        } else {
            size = fileContents.length + BLOCK_SIZE;
        }
        
        do {
            
            // write block to output stream
            output.write(block);
            
            size -= BLOCK_SIZE;
            
            // get next block to be read
            int i = 0;
            while (i < Math.min(size, BLOCK_SIZE))
                block[i++] = fileContents[fileIndex++];
            
            // pad last block
            while (i < BLOCK_SIZE)
                block[i++] = 0;
            
        } while (size > 0);
        
    }
    
    /**
     * Returns whether this record adequately represents a file.
     * 
     * @return whether this record represents a file.
     */
    public boolean isEmpty() {
        return header.isEmpty();
    }
    
    /**
     * Returns whether this record represents a directory.
     * 
     * @return whether this record represents a directory.
     * @throws InvalidPackageException if this record has become corrupted.
     */
    public boolean isDirectory() throws InvalidPackageException {
        return header.getType().equals(TarFileType.DIR);
    }
    
    public TarHeader getHeader() {
        return header;
    }

    public void setHeader(final TarHeader header) {
        this.header = header;
    }
    
    public byte[] getFileContents() {
        return fileContents;
    }

    public void setFileContents(final byte[] fileContents) {
        this.fileContents = fileContents;
    }
    
}

// EOF
