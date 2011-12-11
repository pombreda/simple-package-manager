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
import java.util.ArrayList;

import spm.format.InvalidPackageException;

/**
 * Represents a TAR archive.
 * 
 * @author Zachary Scott <cthug.zs@gmail.com>
 */
public class TarArchive extends ArrayList<TarRecord> {
    
    /**
     * Creates a new instance of {@code TarArchive}.
     * 
     */
    public TarArchive() {
        
    }
    
    /**
     * Reads a tar archive from the given input stream.
     * 
     * @param input input stream to read tar archive from.
     * @throws InvalidPackageException if the read archive is not a valid tar archive.
     * @throws IOException upon failure to read from input the stream.
     */
    public void read(final InputStream input) throws InvalidPackageException, IOException {
        
        TarRecord record;
        
        clear();
        
        do {
            
            record = new TarRecord();
            record.read(input);
            
            if (!record.isEmpty())
                add(record);
            
        } while (!record.isEmpty());
        
    }
    
    /**
     * Reads the given package file into this {@code TarArchive}.
     * 
     * @param file the package file.
     * @throws InvalidPackageException if the file is not a valid archive.
     * @throws IOException upon failure to read from input file.
     */
    public void read(final File file) throws InvalidPackageException, IOException {
        
        BufferedInputStream input = 
            new BufferedInputStream(
                new FileInputStream(file)
            );
        
        read(input);
        
        input.close();
        
    }
    
    /**
     * Reads the given package file named {@code file} into this {@code TarArchive}.
     * 
     * @param file the name of the package file.
     * @throws InvalidPackageException if the file is not a valid archive.
     * @throws IOException upon failure to read from input file.
     */
    public void read(final String file) throws InvalidPackageException, IOException {
        read(new File(file));
    }
    
    /**
     * Writes the entire archive to the given output stream.
     * 
     * @param output stream to write to.
     * @throws IOException upon failure to write to output stream.
     */
    public void write(final OutputStream output) throws IOException {
        
        for (TarRecord record : this)
            record.write(output);
        
    }
    
    /**
     * Writes this {@code TarArchive} to {@code file}.
     * 
     * @param file file to write to.
     * @throws InvalidPackageException if this package has become corrupted.
     * @throws IOException upon failure to write to the output file.
     */
    public void write(final File file) throws InvalidPackageException, IOException {
        
        BufferedOutputStream output = 
            new BufferedOutputStream(
                new FileOutputStream(file)
            );
        
        write(output);
        
        output.close();
        
    }
    
    /**
     * Writes this {@code TarArchive} to the file named {@code file}.
     * 
     * @param file name of the file to write to.
     * @throws InvalidPackageException if this package has become corrupted.
     * @throws IOException upon failure to write to the output file.
     */
    public void write(final String file) throws InvalidPackageException, IOException {
        write(new File(file));
    }
    
    // sets the unix file permissions for the given File
    private void setMode(final File file, int mode) {
        
        int userMode = mode & 7;
        int ownerMode = (mode >> 6) & 7;
        
        // set user permissions
        file.setReadable(((userMode >> 2) & 1) == 1, false);
        file.setWritable(((userMode >> 1) & 1) == 1, false);
        file.setExecutable((userMode >> 1 & 1) == 1, false);
        
        // set owner permissions
        file.setReadable(((ownerMode >> 2) & 1) == 1, true);
        file.setWritable(((ownerMode >> 1) & 1) == 1, true);
        file.setExecutable((ownerMode >> 1 & 1) == 1, true);
        
    }
    
    /**
     * Extracts this {@code TarArchive} to the given directory.
     * 
     * @param dir directory to extract the archive to.
     * @param deleteFlag whether or not to delete the package when the program finishes.
     */
    public void extract(final File dir, boolean deleteFlag) throws FileNotFoundException, 
                                                                   InvalidPackageException, 
                                                                   IOException {
        
        // extract each file
        for (TarRecord record : this) {
            
            TarHeader header = record.getHeader();
            
            File file = new File(dir, header.getFileName());
            
            // create files parent directories
            File parent = file.getParentFile();
            if (!parent.exists())
                parent.mkdirs();
            
            // create file
            if (record.isDirectory()) {
                file.mkdir();
            } else {
                file.createNewFile();
            }
            
            // write file contents to disk
            if (!record.isDirectory()) {
            
                OutputStream output = new BufferedOutputStream(new FileOutputStream(file));

                // write file contents to disk
                byte[] fileContents = record.getFileContents();
                if (fileContents != null) {
                    for (byte b : fileContents) {
                        output.write(b);
                    }
                }
                
                output.close();
            
            }
            
            // set file permissions
            setMode(file, (int) header.getMode());
            
            if (deleteFlag)
                file.deleteOnExit();
            
        }
        
    }
    
    /**
     * Extracts this {@code TarArchive} to the directory named {@code dirname}.
     * 
     * @param dirname name of the directory to extract the archive to.
     */
    public void extract(final String dirname, boolean deleteFlag) throws FileNotFoundException, 
                                                                         InvalidPackageException, 
                                                                         IOException {
        extract(new File(dirname), deleteFlag);
    }
    
    /**
     * Adds a new file to this {@code TarArchive}.
     * The new file is appended to the end of the list and will therefore saved at the end of the archive.
     * 
     * @param filename name of the new file.
     */
    public final void newFile(final String filename) {
        
        TarRecord record = new TarRecord();
        
        TarHeader header = record.getHeader();
        header.setFileName(filename);
        
        add(record);
        
    }
    
    /**
     * Deletes the first record which name matches the given pattern.
     * 
     * @param pattern regex pattern to match.
     */
    public final void deleteFile(final String pattern) {
        
        // find and delete first record matching the given pattern
        for (TarRecord record : this) {
            
            if (record.getHeader().getFileName().matches(pattern)) {
                remove(record);
                return;
            }
            
        }
        
    }
    
    /**
     * Deletes all of the records which name matches the given pattern.
     * 
     * @param pattern regex pattern to match.
     */
    public final void deleteFiles(final String pattern) {
        
        // find and delete all records matching the given pattern
        for (TarRecord record : this) {
            
            if (record.getHeader().getFileName().matches(pattern))
                remove(record);
            
        }
        
    }
    
    /**
     * Adds a new file named {@code filename}, and loads its contents from {@code fileContents}.
     * 
     * @param filename name of the new file.
     * @param fileContents contents of the new file.
     */
    public void addFile(final String filename, final byte[] fileContents) {
        
        TarRecord record = new TarRecord();
        
        record.getHeader().setFileName(filename);
        record.setFileContents(fileContents);
        
    }
    
    /**
     * Adds a the new file {@code file} and loads its contents from disk.
     * 
     * @param file the file to be added.
     * @throws IOException upon failure to read from file.
     * @throws FileNotFoundException upon failure to open the given file.
     */
    public void addFile(final File file) throws FileNotFoundException, IOException {
        
        BufferedInputStream input = new BufferedInputStream(new FileInputStream(file));
        
        byte[] buffer = new byte[(int) file.length()];
        
        // read in the file
        int b;
        for (int i = 0; (b = input.read()) != -1; i++)
            buffer[i] = (byte) b;
        
        addFile(file.getName(), buffer);
        
        input.close();
        
    }
    
    /**
     * Adds a new file named {@code filename} and loads its contents from disk.
     * 
     * @param filename name of the file to be added.
     * @throws IOException upon failure to read from the file.
     * @throws FileNotFoundException upon failure to open the given file.
     */
    public void addFile(final String filename) throws IOException, FileNotFoundException {
        addFile(new File(filename));
    }
    
    /**
     * Returns the first record which name matches the given pattern.
     * Equivalent to {@code getAllRecords(pattern)[0]}.
     * 
     * @param pattern regex pattern to be matched.
     * @return the first record which name matches the given pattern, or {@code null} if it does not exist.
     */
    public final TarRecord getRecord(final String pattern) {
        
        // find first record matching the given pattern
        for (TarRecord record : this) {
            
            if (record.getHeader().getFileName().matches(pattern))
                return record;
            
        }
        
        return null;
        
    }
    
    /**
     * Returns the all records which name matches the given pattern.
     * 
     * @param pattern regex pattern to be matched.
     * @return the all records which name matches the given pattern, or {@code null} if none match.
     */
    public final TarRecord[] getRecords(final String pattern) {
        
        ArrayList<TarRecord> records = new ArrayList<TarRecord>();
        
        // find all records matching the given pattern
        for (TarRecord record : this) {
            
            if (record.getHeader().getFileName().matches(pattern))
                records.add(record);
            
        }
        
        if (records.isEmpty())
            return null;
        
        return (TarRecord[]) records.toArray();
        
    }
    
    /**
     * Returns the contents of the file named {@code filename}.
     * 
     * @param filename the name of the file to get.
     * @return the contents of the file named {@code filename}, or {@code null} if it doesn't exist.
     */
    public final byte[] getFileContents(final String filename) {
        
        TarRecord record = getRecord(filename);
        
        if (record == null) {
            return null;
        } else {
            return record.getFileContents();
        }
            
    }
    
    /**
     * Sets the contents of the file named {@code filename} to {@code fileContents}.
     * If {@code filename} is {@code null}, nothing is done.
     * 
     * @param filename name of the file to have its contents changed.
     * @param fileContents the buffer to set the file's contents to.
     */
    public final void setFileContents(final String filename, final byte[] fileContents) {
        
        TarRecord record = getRecord(filename);
        
        if (record == null)
            return;
        
        record.setFileContents(fileContents);
        
    }
    
}

// EOF
