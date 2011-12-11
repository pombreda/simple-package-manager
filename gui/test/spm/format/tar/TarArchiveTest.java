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
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test for {@code spm.format.tar.TarArchive}
 * 
 * @author Zachary Scott <cthug.zs@gmail.com>
 */
public class TarArchiveTest {
    
    private static final String TEST_ARCHIVE = "test/test.tar"; // SHOULD be in project root
    private static final String TEST_STRING = "TEST-STRING";
    private static final String TEMP_ARCHIVE = "test/temp.tar";
    
    public TarArchiveTest() {
    }
    
    // creates and initialises the header for the test package
    private TarHeader getHeader() {
        
        TarHeader header = new TarHeader();
        
        header.setFileName("test.file");
        header.setMode(0644);
        
        header.setUid(1234);
        header.setGuid(1234);
        
        header.setFileSize(11);
        header.setModTime(1234567890);
        header.setType(TarFileType.NORMAL);
        
        header.setLinkName(null);
        
        header.setUserName("test-user");
        header.setGroupName("group-name");
        
        header.setDevMajor(0);
        header.setDevMinor(0);
        
        return header;
        
    }
    
    private void readArchive(final File file) throws Exception {
        
        // open test archive
        BufferedInputStream input = new BufferedInputStream(
                                        new FileInputStream(file)
                                    ); 
                    
        TarArchive archive = new TarArchive();
        archive.read(input);
        
        TarRecord record = archive.get(0);
        
        // check header contains correct info
        if (record.getHeader().equals(getHeader()))
             fail("Tar header for test archive is not correct");
        
        // check file contents are correct
        String fileContents = new String(record.getFileContents(), "UTF-8");
        if (!fileContents.equals(TEST_STRING)) {
            
            StringBuilder msg = new StringBuilder();
            
            msg.append("File contents of the test file in the test archive are not correct. It should be \"");
            msg.append(TEST_STRING);
            msg.append("\" but it was \"");
            msg.append(fileContents);
            msg.append("\".");
            
            fail(msg.toString());
            
        }
        
    }
    
    @Test
    public void testRead() throws Exception {
        
        readArchive(new File(TEST_ARCHIVE));
        
    }
    
    @Test
    public void testWrite() throws Exception {
        
        File temp = new File(TEMP_ARCHIVE);
        
        TarHeader header = getHeader();
        byte[] fileContents = TEST_STRING.getBytes("UTF-8");
        
        BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(temp));
        
        // create and write the archive to disk
        TarArchive archive = new TarArchive();
        archive.add(new TarRecord(header, fileContents));
        archive.write(output);
        
        output.close();
        
        // make sure that archive was created correctly
        readArchive(temp);
        
        temp.delete();
        
    }
    
}

// EOF
