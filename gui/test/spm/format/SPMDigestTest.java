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

import java.io.File;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test for {@code SPMDigest}.
 * 
 * @author Zachary Scott <cthug.zs@gmail.com>
 */
public class SPMDigestTest {
    
    private static final String TEST_PACKAGE = "test/test.spm";
    private static final String TEST_DIGEST = "test/digest";
    private static final String TEMP_DIGEST = "test/tmp.digest";
    
    public SPMDigestTest() {
    }

    @Test
    public void testRead() throws Exception {
        
        SPMPackageContainer archive = new SPMPackageContainer();
        archive.read(TEST_PACKAGE);
        
        SPMDigest archiveDigest = new SPMDigest(archive);
        
        SPMDigest testDigest = new SPMDigest();
        testDigest.read(TEST_DIGEST);
        
        // check that the digest match those of the file in the archive
        if (!archiveDigest.check(testDigest)) {
            
            StringBuilder msg = new StringBuilder();
            
            msg.append("Digests for the file \"");
            msg.append(TEST_PACKAGE);
            msg.append("\" does not match those given in the precalculated \"");
            msg.append(TEST_DIGEST);
            msg.append("\" file!");
            
            fail(msg.toString());
            
        }
        
    }
    
    @Test
    public void testWrite() throws Exception {
        
        File tempDigest = new File(TEMP_DIGEST);
        
        SPMPackageContainer archive = new SPMPackageContainer();
        archive.read(TEST_PACKAGE);
        
        SPMDigest archiveDigest = new SPMDigest(archive);
        archiveDigest.write(tempDigest);
        
        SPMDigest readDigest = new SPMDigest();
        readDigest.read(TEMP_DIGEST);
        
        // check that the digest file read from disk is the same as computed in memory
        if (!archiveDigest.check(readDigest)) {
            
            StringBuilder msg = new StringBuilder();
            
            msg.append("The digest file for \"");
            msg.append(TEST_PACKAGE);
            msg.append(" was not written correctly to \"");
            msg.append(TEMP_DIGEST);
            msg.append(".");
            
            fail(msg.toString());
            
        }
        
        tempDigest.delete();
        
    }
    
}

// EOF
