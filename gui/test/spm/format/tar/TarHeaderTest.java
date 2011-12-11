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

import org.junit.Test;
import spm.format.InvalidPackageException;
import static org.junit.Assert.*;

/**
 * Test class for {@code spm.format.TarHeader}.
 * 
 * @author Zachary Scott <cthug.zs@gmail.com>
 */
public class TarHeaderTest {
    
    private StringBuilder msg;
    
    private TarHeader header = new TarHeader();
    
    public TarHeaderTest() {
    }
    
    @Test
    public void testFileName() {
        
        // check that a small filename matches (i.e. no filename prefix)
        String filename = "test.filename";
        header.setFileName(filename);
        if (!header.getFileName().equals(filename)) {
            
            msg = new StringBuilder();
            
            msg.append("For filename without additional prefix - TarHeader.getFileName() [");
            msg.append(header.getFileName());
            msg.append("] != ");
            msg.append(filename);
            msg.append(".");
            
            fail(msg.toString());
            
        }
        
        // create a long filename
        StringBuilder longFilename = new StringBuilder();
        for (int i = 0; i < (TarHeader.FILENAME_SIZE + 1); i++) 
            longFilename.append('Z');
        filename = longFilename.toString();
        
        // check that a small filename matches (i.e. no filename prefix)
        header.setFileName(filename);
        if (!header.getFileName().equals(filename)) {
            
            msg = new StringBuilder();
            
            msg.append("For long filename with prefix - TarHeader.getFileName() [");
            msg.append(header.getFileName());
            msg.append("] != ");
            msg.append(filename);
            msg.append(".");
            
            fail(msg.toString());
            
        }
        
    }
        
    @Test
    public void testMode() {
        
        long mode = 0;
        header.setMode(mode);
        if (header.getMode() != mode) {
            
            msg = new StringBuilder();
            
            msg.append("TarHeader.getMode() [");
            msg.append(header.getMode());
            msg.append("] != ");
            msg.append(mode);
            msg.append(".");
            
            fail(msg.toString());
            
        }
        
    }
    
    @Test
    public void testUid() {
        
        long uid = 1234;
        header.setUid(uid);
        if (header.getUid() != uid) {
            
            msg = new StringBuilder();
            
            msg.append("TarHeader.getUid() [");
            msg.append(header.getUid());
            msg.append("] != ");
            msg.append(uid);
            msg.append(".");
            
            fail(msg.toString());
            
        }
        
    }
    
    @Test
    public void testGuid() {
        
        long guid = 1234;
        header.setGuid(guid);
        if (header.getGuid() != guid) {
            
            msg = new StringBuilder();
            
            msg.append("TarHeader.getGuid() [");
            msg.append(header.getGuid());
            msg.append("] != ");
            msg.append(guid);
            msg.append(".");
            
            fail(msg.toString());
            
        }
        
    }
    
    @Test
    public void testFileSize() {
        
        long size = 123456879;
        header.setFileSize(size);
        if (header.getFileSize() != size) {
            
            msg = new StringBuilder();
            
            msg.append("TarHeader.getFileSize() [");
            msg.append(header.getFileSize());
            msg.append("] != ");
            msg.append(size);
            msg.append(".");
            
            fail(msg.toString());
            
        }
        
    }
    
    @Test
    public void testModTime() {
        
        long modTime = 123456879;
        header.setModTime(modTime);
        if (header.getModTime() != modTime) {
            
            msg = new StringBuilder();
            
            msg.append("TarHeader.getModTime() [");
            msg.append(header.getModTime());
            msg.append("] != ");
            msg.append(modTime);
            msg.append(".");
            
            fail(msg.toString());
            
        }
        
    }
    
    @Test
    public void testType() {
        
        try {
        
            TarFileType type = TarFileType.NORMAL;
            header.setType(type);
            if (!header.getType().equals(type)) {
            
                msg = new StringBuilder();

                msg.append("TarHeader.getType() [");
                msg.append(header.getType());
                msg.append("] != ");
                msg.append(type);
                msg.append(".");

                fail(msg.toString());
            
            }
            
        } catch (InvalidPackageException ex) {
            
            msg = new StringBuilder();

            msg.append("Failed to set header file type to TarFileType.NORMAL (");
            msg.append(TarFileType.NORMAL);
            msg.append(") ");
            msg.append(ex.getMessage());

            fail(msg.toString());
            
        }
        
    }
    
    @Test
    public void testLinkName() {
        
        String filename = "test.filename";
        header.setLinkName(filename);
        if (!header.getLinkName().equals(filename)) {
            
            msg = new StringBuilder();
            
            msg.append("TarHeader.getLinkName() [");
            msg.append(header.getLinkName());
            msg.append("] != ");
            msg.append(filename);
            msg.append(".");
            
            fail(msg.toString());
            
        }
        
    }
    
    @Test
    public void testMagic() {
        
        if (!header.getMagic().equals("ustar")) {
            
            msg = new StringBuilder();
            
            msg.append("Magic constant has been trashed; TarHeader.getMagic() = ");
            msg.append(header.getMagic());
            msg.append(".");
            
            fail(msg.toString());
            
        }
        
    }
    
    @Test
    public void testVersion() {
        
        if (!header.getVersion().equals("00")) {
            
            msg = new StringBuilder();
            
            msg.append("Version number have been trashed; TarHeader.getVersion = ");
            msg.append(header.getVersion());
            msg.append(".");
            
            fail(msg.toString());
            
        }
        
    }
        
    @Test
    public void testUserName() {
        
        String user = "test-user";
        header.setUserName(user);
        if (!header.getUserName().equals(user)) {
            
            msg = new StringBuilder();
            
            msg.append("TarHeader.getUserName() [");
            msg.append(header.getUserName());
            msg.append("] != ");
            msg.append(user);
            msg.append(".");
            
            fail(msg.toString());
            
        }
        
    }
    
    @Test
    public void testGroupName() {
        
        String group = "test-group";
        header.setGroupName(group);
        if (!header.getGroupName().equals(group)) {
            
            msg = new StringBuilder();
            
            msg.append("TarHeader.getGroupName() [");
            msg.append(header.getGroupName());
            msg.append("] != ");
            msg.append(group);
            msg.append(".");
            
            fail(msg.toString());
            
        }
        
    }
    
    @Test
    public void testDevMajor() {
        
        long devMajor = 1234;
        header.setDevMajor(devMajor);
        if (header.getDevMajor() != devMajor) {
            
            msg = new StringBuilder();
            
            msg.append("TarHeader.getDevMajor() [");
            msg.append(header.getDevMajor());
            msg.append("] != ");
            msg.append(devMajor);
            msg.append(".");
            
            fail(msg.toString());
            
        }
        
    }
    
    @Test
    public void testDevMinor() {
        
        long devMinor = 1234;
        header.setDevMinor(devMinor);
        if (header.getDevMinor() != devMinor) {
            
            msg = new StringBuilder();
            
            msg.append("TarHeader.getDevMinor() [");
            msg.append(header.getDevMinor());
            msg.append("] != ");
            msg.append(devMinor);
            msg.append(".");
            
            fail(msg.toString());
            
        }
        
    }
    
    private void initHeader() {
        
        header.setFileName("test.filename");
        header.setMode(0);
        
        header.setUid(1234);
        header.setGuid(1234);
        
        header.setFileSize(1234);
        header.setModTime(1234);
        header.setType(TarFileType.NORMAL);
        
        header.setLinkName(null);
        
        header.setUserName("test-user");
        header.setGroupName("group-name");
        
        header.setDevMajor(1234);
        header.setDevMinor(1234);
        
    }
    
    @Test
    public void testEquals() {
        assertEquals("header != header", header, header);
    }
    
    @Test
    public void testEncodeAndDecode() {
        
        initHeader();
        byte[] block = header.encode();
        
        // check encoded block is the correct length
        if (block.length != TarHeader.BLOCK_SIZE) {
            
            msg = new StringBuilder();
            
            msg.append("Encoded block is of length ");
            msg.append(block.length);
            msg.append(" bytes instead of ");
            msg.append(TarHeader.BLOCK_SIZE);
            msg.append(".");
            
            fail(msg.toString());
            
        }
        
        // attempt to decode the encoded header
        TarHeader decodedHeader = new TarHeader();
        try {
            
            decodedHeader.decode(block);
            
        } catch (InvalidPackageException ex) {
            
            msg = new StringBuilder();
            
            msg.append("InvalidPackageException thrown while decoding header block! ");
            msg.append(ex.getMessage());
            
            fail(msg.toString());
            
        }
        
        // check that encoded results and decoded results match
        if (!decodedHeader.equals(header)) {
            
            msg = new StringBuilder();
            
            msg.append("Encoded header cannot be decoded correctly! ");
            msg.append("Encoded header data is: ");
            
            msg.append("{ ");
            for (int i = 0; i < block.length; i++) {
                msg.append(block[i]);
                msg.append(", ");
            }
            msg.append("}.");
            
            fail(msg.toString());
            
        }
        
    }

}

// EOF
