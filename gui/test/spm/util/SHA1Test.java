/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package spm.util;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test class for {@code SHA1} hash function class.
 * 
 * @author Zachary Scott <cthug.zs@gmail.com>
 */
public class SHA1Test {
    
    // generated using sha1sum
    private static final String[] testVectors = {
        "test-string", "4f49d69613b186e71104c7ca1b26c1e5b78c9193",
        "hello world", "2aae6c35c94fcfb415dbe95f408b9ce91ee846ed",
        "sha1 hash",   "8b30c7cb569b5c3d48df4c1a3509e9c169f4d319"
    };
    
    public SHA1Test() {
    }

    @Test
    public void testSHA1EncodeAndDecode() {
        
        String testString = "test-string";
        
        // decode calculated hash from string
        SHA1 hash1 = SHA1.hash(testString);
        SHA1 hash2 = new SHA1(hash1.toString());
        
        if (!hash1.equals(hash2)) {
            
            StringBuilder msg = new StringBuilder();
            
            msg.append("SHA1 hash not decoded from string correctly! SHA1 hash was ");
            msg.append(hash1.toString());
            msg.append(" but was decoded as ");
            msg.append(hash2.toString());
            msg.append(".");
            
            fail(msg.toString());
            
        }
        
    }
    
    @Test
    public void testSHA1() {
        
        for (int i = 0; i < testVectors.length; i += 2) {
            
            SHA1 hash = SHA1.hash(testVectors[i]);
            SHA1 realHash = new SHA1(testVectors[i+1]);
            
            // check computed hash
            if (!hash.equals(realHash)) {
                
                StringBuilder msg = new StringBuilder();
                
                msg.append("SHA1 hash did not match the test vector! Hash for ");
                msg.append(testVectors[i]);
                msg.append(" should have been ");
                msg.append(testVectors[i+1]);
                msg.append(". Instead it was computed as ");
                msg.append(hash.toString());
                msg.append(".");
                
                fail(msg.toString());
                
            }
            
        }
        
    }
    
}

// EOF
