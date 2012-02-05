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
import java.util.*;
import java.util.logging.*;

import spm.util.SHA1;
import spm.format.tar.*;

/**
 * Represents a list of file SHA1 digests used in a SPM package.
 * The format is the same as the sha1sum utility.
 *
 * @author Zachary Scott <cthug.zs@gmail.com>
 */
public final class SPMDigest extends Hashtable<String, SHA1> {
    
    private final static Logger logger = Logger.getLogger(SPMDigest.class.getName());
    
    /**
     * Creates a new instance of {@code SPMDigest}.
     * 
     */
    public SPMDigest() {
        
    }
    
    /**
     * Creates a new instance of {@code SPMDigest} and adds the digests of each file in {@code archive}.
     * 
     * @param archive spm package to be read.
     */
    public SPMDigest(final TarArchive archive) {
        
        update(archive);
        
    }
    
    /**
     * Updates this {@code SPMDigest} with the digest values of the files in {@code archive}.
     * 
     * @param archive spm package to be read.
     */
    public void update(final TarArchive archive) {
        
        for (TarRecord record : archive)
            put(record.getHeader().getFileName(), SHA1.hash(record.getFileContents()));
        
    }
    
    /**
     * Reads a list of digests from the given {@code StringBuffer}.
     * 
     * @param buffer buffer to read digests from.
     * @throws SPMDigestException if the format is not correct (same as {@code sha1sum} utility).
     */
    public void read(final StringBuffer buffer) throws SPMDigestException {
        
        StringBuilder msg;
        
        char[] digest = new char[SHA1.DIGEST_HEX_SIZE];
        
        clear();
        
        // parse digest buffer
        for (int i = 0; i < buffer.length(); i++) {
            
            // read digest value
            if (buffer.length() < SHA1.DIGEST_HEX_SIZE) {

                msg = new StringBuilder();

                msg.append("Expected ");
                msg.append(SHA1.DIGEST_HEX_SIZE);
                msg.append(" hexadecimal character SHA1 hash, but instead found ");
                msg.append(buffer.length());
                msg.append(" characters!");
                
                
                throw new SPMDigestException(msg.toString());
                        
            }
            buffer.getChars(i, i + SHA1.DIGEST_HEX_SIZE, digest, 0);
            i += SHA1.DIGEST_HEX_SIZE;
            
            // read space and file type indicator
            while (buffer.charAt(i) == ' ' || buffer.charAt(i) == '*')
                i++;
            
            // read filename
            StringBuilder filename;
            if (buffer.length() > 0) {
                
                filename = new StringBuilder();
                for (char ch; (ch = buffer.charAt(i)) != '\n' && ch != -1; i++)
                    filename.append((char) ch);
                
            } else {
                
                throw new SPMDigestException("Expected filename but found end-of-file!");
                
            }
            
            // add hash filename pair to SPMDigest
            put(filename.toString(), new SHA1(new String(digest)));
            
        }
        
    }
    
    /**
     * Reads a list of digests from the given input stream.
     * 
     * @param input input stream to read from.
     * @throws IOException upon failure to read from {@code input}.
     * @throws SPMDigestException if the format is not correct (same as {@code sha1sum} utility).
     */
    public void read(final InputStream input) throws IOException, SPMDigestException {
        
        StringBuffer digest = new StringBuffer();
        
        // read digests stream
        int ch;
        while ((ch = input.read()) != -1)
            digest.append((char) ch);
        
        read(digest);
        
    }
    
    /**
     * Reads a list of digests from the given {@code File}.
     * 
     * @param file file to read from.
     * @throws FileNotFoundException if the given file could not be opened for reading.
     * @throws IOException upon failure to read from the file.
     * @throws SPMDigestException if the format is not correct (same as {@code sha1sum} utility).
     */
    public void read(final File file) throws FileNotFoundException, IOException, SPMDigestException {
        
        InputStream input = new BufferedInputStream(new FileInputStream(file));
        
        read(input);
        
        input.close();
        
    }
    
    /**
     * Reads a list of digests from the given named {@code filename}.
     * 
     * @param filename name of the file to read.
     * @throws FileNotFoundException if the given file could not be opened for reading.
     * @throws IOException upon failure to read from the file.
     * @throws SPMDigestException if the format is not correct (same as {@code sha1sum} utility).
     */
    public void read(final String filename) throws FileNotFoundException, IOException, SPMDigestException {
        read(new File(filename));
    }
    
    /**
     * Writes this {@code SPMDigest} to the given output stream.
     * The format is the same as the {@code sha1sum} utility.
     * 
     * @param output the output stream to be written to.
     * @throws IOException upon failure to write to the output stream.
     */
    public void write(final OutputStream output) throws IOException {
        
        String digest = toString();
        
        // write digest file contents to output stream
        try {
            
            output.write(digest.getBytes("UTF-8"));
            
        } catch (UnsupportedEncodingException ex) {
            
            StringBuilder msg = new StringBuilder();
            
            msg.append("Failed to write SPMDigest ");
            msg.append(digest);
            msg.append(" to output stream because UTF-8 character encoding is not supported!");
            
            logger.log(Level.SEVERE, msg.toString(), ex);
            System.exit(1);
            
        }
        
    }
    
    /**
     * Writes this {@code SPMDigest} to the given output file.
     * The format is the same as the {@code sha1sum} utility.
     * 
     * @param file the output file to be written to.
     * @throws FileNotFoundException upon failure to open file for writing.
     * @throws IOException upon failure to write to file.
     */
    public void write(final File file) throws FileNotFoundException, IOException {
        
        OutputStream output = new BufferedOutputStream(new FileOutputStream(file));
        
        write(output);
        
        output.close();
        
    }
    
    /**
     * Writes this {@code SPMDigest} to the given output file.
     * The format is the same as the {@code sha1sum} utility.
     * 
     * @param file the name of the output file to be written to.
     * @throws FileNotFoundException upon failure to open file for writing.
     * @throws IOException upon failure to write to file.
     */
    public void write(final String filename) throws FileNotFoundException, IOException {
        write(new File(filename));
    }
    
    /**
     * Checks that the given {@code SPMDigest} matches this {@code SPMDigest}.
     * 
     * @param digest digest to match.
     * @return whether the given {@code SPMDigest} matches this {@code SPMDigest}.
     * @throws SPMDigestException if they don't match.
     */
    public boolean check(final SPMDigest digest) throws SPMDigestException {
        
        StringBuilder msg = new StringBuilder();
        boolean matchedFlag = true;
        
        // compare each file
        for (Enumeration<String> e = keys(); e.hasMoreElements();) {
            
            String filename = e.nextElement();
            SHA1 hash = digest.get(filename);
            
            if (hash != null) { // skip files without a SHA1 entry
                
                if (hash.equals(get(filename))) {
                    
                    matchedFlag = false;
                    
                    msg.append("The file \"");
                    msg.append(filename);
                    msg.append("\" is corrupted! The SHA1 hash is supposed to be ");
                    msg.append(hash.toString());
                    msg.append(" but was instead ");
                    msg.append(get(filename).toString());
                    msg.append(".");
                    msg.append(System.getProperty("line.separator"));
                    
                }
                
            }
            
        }
        
        // throw exception with a description of each corrupted file
        if (!matchedFlag) {
            throw new SPMDigestException(msg.toString());
        }
        
        return matchedFlag;
        
    }
    
    /**
     * Checks that the given {@code TarArchive} matches this {@code SPMDigest}.
     * 
     * @param archive archive to match against this {@code SPMDigest}.
     * @return whether the given {@code TarArchive} matches this {@code SPMDigest}.
     * @throws SPMDigestException if they don't match.
     */
    public boolean check(final TarArchive archive) throws SPMDigestException {
        
        SPMDigest digest = new SPMDigest(archive);
        return check(digest);
        
    }
    
    /**
     * Checks that the given {@code SPMdigest} matches this {@code SPMDigest}.
     * Differs from {@code check(final SPMDigest digest)} in that it ignores any Exception that are thrown.
     * 
     * @param object object to match against {@code this}.
     * @return whether the given {@code SPMdigest} matches this {@code SPMDigest}.
     */
    @Override
    public boolean equals(final Object object) {
        
        try {
            return object instanceof SPMDigest && check((SPMDigest) object);
        } catch (SPMDigestException ex) { // ignore
            return false;
        }
        
    }
    
    @Override
    public String toString() {
        
        StringBuilder string = new StringBuilder();
        
        for (Enumeration<String> e = keys(); e.hasMoreElements();) {
            
            String filename = e.nextElement();
            
            string.append(get(filename));
            string.append(" *");
            string.append(filename);
            string.append(System.getProperty("line.separator"));
            
        }
        
        return string.toString();
        
    }
    
}

// EOF
