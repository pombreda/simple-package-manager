package spm.util;
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

import java.io.UnsupportedEncodingException;
import java.util.logging.*;
import java.util.Arrays;
import java.security.*;

/**
 * Provides the SHA1 cryptographic hash function.
 * 
 * @author Zachary Scott <cthug.zs@gmail.com>
 */
public final class SHA1 {
 
    private final static Logger logger = Logger.getLogger(SHA1.class.getName());
    
    private static final char[] HEX_TABLE = "0123456789abcdef".toCharArray();
    
    /** Size of the digest in bytes. */
    public static final int DIGEST_SIZE = 20;
    
    /** Size of the digest in hexadecimal digits. */
    public static final int DIGEST_HEX_SIZE = DIGEST_SIZE * 2;
    
    private byte[] digest = new byte[DIGEST_SIZE];
    
    /**
     * Creates a new instance of {@code SHA1}.
     * 
     */
    public SHA1() {
        
    }
    
    /**
     * Creates a new instance of {@code SHA1} with the given digest value.
     * 
     * @param digest digest array (must be 20 bytes long).
     * @throws IllegalArgumentException if {@code digest} is not 20 bytes long.
     */
    public SHA1(final byte[] digest) {
        
        if (digest.length == DIGEST_SIZE) {
            this.digest = digest;
        } else {
            
            StringBuilder msg = new StringBuilder();
            
            msg.append("SHA1 message digest is supposed to be ");
            msg.append(DIGEST_SIZE);
            msg.append(" bytes long but instead was ");
            msg.append(digest.length);
            msg.append("!");
            
            throw new IllegalArgumentException(msg.toString());
            
        }
        
    }
    
    /**
     * Creates a new instance of {@code SHA1} with the given digest value.
     * 
     * @param digest digest string (must be 40 hexadecimal characters long).
     * @throws IllegalArgumentException if {@code digest} is not 40 hexadecimal characters long.
     */
    public SHA1(final String digest) {
        
        if (digest.length() != DIGEST_HEX_SIZE) {
        
            StringBuilder msg = new StringBuilder();
            
            msg.append("SHA1 message digest is supposed to be ");
            msg.append(DIGEST_HEX_SIZE);
            msg.append(" hexadecimal characters long but instead was ");
            msg.append(digest.length());
            msg.append("!");
            
            throw new IllegalArgumentException(msg.toString());
        
        }
        
        // parse hexadecimal string
        for (int i = 0; i < digest.length(); i += 2)
            this.digest[i / 2] = (byte) ((hexValue(digest.charAt(i)) << 4) | hexValue(digest.charAt(i+1)));
        
    }
    
    // converts a hexadecimal character into
    private static byte hexValue(char ch) {
        
        if (ch >= '0' && ch <= '9') {
            return (byte) (ch - '0');
        } else if (ch >= 'a' && ch <= 'z') {
            return (byte) (ch - 'a' + 10);
        } else if (ch >= 'A' && ch <= 'Z') {
            return (byte) (ch - 'A' + 10);
        } else  {
         
            StringBuilder msg = new StringBuilder();
            
            msg.append("The character ");
            msg.append(ch);
            msg.append(" is not a valid hexadecimal character!");
            
            throw new NumberFormatException(msg.toString());
            
        }
        
    }
    
    /**
     * Calculates the SHA1 cryptographic hash of the given {{@code byte} array.
     * 
     * @param bytes array of {@code byte}'s to be hashed.
     * @return the {@code SHA1} representing the calculated hash.
     */
    public static SHA1 hash(byte[] bytes) { 
        
        if (bytes == null)
            bytes = new byte[0];
        
        try {
            
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(bytes, 0, bytes.length);

            return new SHA1(md.digest());
        
        } catch (NoSuchAlgorithmException ex) {
            
            logger.log(Level.SEVERE, 
                       "Cannot calculate SHA1 because it is not supported by MessageDigest!", 
                       ex);
            
            System.exit(1);
            
        }
        
        return null;
        
    }
    
    /**
     * Calculates the SHA1 cryptographic hash of the given {@code String}.
     * The string is first converted to a byte array according the the UTF-8 encoding scheme.
     * 
     * @param string string to be hashed.
     * @return the {@code SHA1} representing the calculated hash.
     */
    public static SHA1 hash(final String string) {
        
        try {
            
            return hash(string.getBytes("UTF-8"));
            
        } catch (UnsupportedEncodingException ex) {
            
            logger.log(Level.SEVERE, "Cannot calculate SHA1 due to UTF-8 being unsupported!", ex);
            System.exit(1);
            
        }
        
        return null;
            
    }
    
    /**
     * Returns the digest value of this {@code SHA1}.
     * 
     * @return the digest value of this {@code SHA1} (can be {@code null}).
     */
    public byte[] getDigest() {
        return digest;
    }
    
    /**
     * Converts this {@code SHA1} digest into a hexadecimal string.
     * 
     * @return this {@code SHA1} as a hexadecimal string.
     */
    @Override
    public String toString() {
        
        StringBuilder buffer = new StringBuilder();
        
        for (int i = 0; i < digest.length; i++) {
            buffer.append(HEX_TABLE[(digest[i] & 0xf0) >> 4]);
            buffer.append(HEX_TABLE[digest[i] & 0xf]);
        } 
        
        return buffer.toString();
        
    }
    
    @Override
    public boolean equals(final Object object) {
        
        if (object instanceof SHA1) {
            
            SHA1 sha1 = (SHA1) object;
            return Arrays.equals(sha1.getDigest(), digest);
            
        } else {
            return false;
        }
        
    }
    
}

// EOF
