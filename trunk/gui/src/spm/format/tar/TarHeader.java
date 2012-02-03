package spm.format.tar;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import spm.format.InvalidPackageException;

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

/**
 * Header of a TAR archive record.
 * 
 * @author Zachary Scott <cthug.zs@gmail.com>
 */
public final class TarHeader {
    
    // name of the contained file
    public static final int FILENAME_SIZE = 100;
    private final char[] filename = new char[FILENAME_SIZE];
    
    // mode
    public static final int FILE_MODE_SIZE = 8;
    private final char[] mode = new char[FILE_MODE_SIZE];
    
    // owner user ID
    public static final int UID_SIZE = 8;
    private final char[] uid = new char[UID_SIZE];
    
    // group user ID
    public static final int GUID_SIZE = 8;
    private final char[] guid = new char[GUID_SIZE];
    
    // size of the file in bytes
    public static final int FILE_SIZE = 12;
    private final char[] size = new char[FILE_SIZE];
    
    // last modification time
    public static final int MOD_TIME_SIZE = 12;
    private final char[] modTime = new char[MOD_TIME_SIZE];
    
    // redundant checksum for header block
    public static final int CHECKSUM_SIZE = 8;
    private final char[] checksum = new char[CHECKSUM_SIZE];
    
    // type of file
    public static final int TYPE_FLAG_SIZE = 1;
    private final char[] typeFlag = new char[TYPE_FLAG_SIZE];
    
    // name of the linked file
    public static final int LINKNAME_SIZE = 100;
    private final char[] linkName = new char[LINKNAME_SIZE];
    
    // magic "ustar" indicator field
    public static final int MAGIC_SIZE = 6;
    private final char[] magic = { 'u', 's', 't', 'a', 'r', 0};
    
    // ustar version = "00"
    public static final int VERSION_SIZE = 2;
    private final char[] version = { '0', '0'};
    
    // owner user name
    public static final int USER_NAME_SIZE = 32;
    private final char[] userName = new char[USER_NAME_SIZE];
    
    // owner group name
    public static final int GROUP_NAME_SIZE = 32;
    private final char[] groupName = new char[GROUP_NAME_SIZE];
    
    // device major number
    public static final int DEV_MAJOR_SIZE = 8;
    private final char[] devMajor = new char[DEV_MAJOR_SIZE];
    
    // device minor number
    public static final int DEV_MINOR_SIZE = 8;
    private final char[] devMinor = new char[DEV_MINOR_SIZE];
    
    // additional prefix to the files name
    public static final int FILENAME_PREFIX_SIZE = 155;
    private final char[] filenamePrefix = new char[FILENAME_PREFIX_SIZE];
    
    /** Size of a header block in bytes (512). */
    public static final int BLOCK_SIZE = 512;
    
    // static user info
    private static String USER_NAME, GROUP_NAME;
    private static long UID, GUID;
    
    // get user information from OS
    static {
        
        USER_NAME = System.getProperty("user.name");
        
        // a lovely hack to get the user id, group name and group id from the OS
        try {
            
            InputStream input;
            int ch;
            
            StringBuilder buffer;
            
            Runtime runtime = Runtime.getRuntime();
            
            // get group name
            input = runtime.exec("id -gn " + USER_NAME).getInputStream();
            buffer = new StringBuilder();
            while ((ch = input.read()) != -1) 
                buffer.append((char) ch);
            GROUP_NAME = buffer.toString();
            
            // get user id
            input = runtime.exec("id -u " + USER_NAME).getInputStream();
            buffer = new StringBuilder();
            while ((ch = input.read()) != -1) 
                buffer.append((char) ch);
            UID = Long.parseLong(buffer.toString());
            
            // get group id
            input = runtime.exec("id -g " + USER_NAME).getInputStream();
            buffer = new StringBuilder();
            while ((ch = input.read()) != -1) 
                buffer.append((char) ch);
            GUID = Long.parseLong(buffer.toString());
            
            
        } catch (Exception ex) { // ignore exceptions
            
            GROUP_NAME = null;
            UID = 0;
            GUID = 0;
            
        }
        
    }
    
    /**
     * Creates a new instance of {@code TarHeader}.
     * sets default values for the header, automatically filling in the Uid, Guid, user name, group name and
     * last modification time.
     * 
     */
    public TarHeader() {
        
        // set default values
        setFileName(null);
        setMode(0644); // rw-r--r--
        setUid(UID);
        setGuid(GUID);
        setFileSize(0);
        setModTime(System.currentTimeMillis() / 1000);
        setType(TarFileType.NORMAL);
        setLinkName(null);
        setUserName(USER_NAME);
        setGroupName(GROUP_NAME);
        setDevMajor(0);
        setDevMinor(0);
        
    }
    
    // returns the size of the given c-style string
    private static int strlen(char[] string) {
        
        int size = 0;
        
        while (size < string.length && string[size] != '\0')
            size++;
        
        return size;
        
    }
    
    // returns whether the header is of the USTAR format
    private boolean isUSTAR() {
        return new String(magic, 0, strlen(magic)).equals("ustar");
    }
    
    // sets a field to all NUL characters
    private void setNullField(final char[] field) {
        
        for (int i = 0; i < field.length; i++)
            field[i] = 0;
        
    }
    
    // parses a number field in tar header
    private long parseNumField(final char[] field) {
        
        long num;
        
        if (isUSTAR() && ((((byte) field[0]) >> 7) & 1) == 1) { // use star's base 256 encoding
            
            int shift = (field.length - 1) * 8;
            
            // read number from field
            num = (field[0] & 0x7F) << (shift--);
            for (int i = 1; i < field.length; i++) {
                num |= (field[0] & 0xFF) << (shift--);
            }
            
        } else { // parse ASCII octal format
            
            if (strlen(field) > 0) {
                
                String string = new String(field, 0, strlen(field));
                string = string.replaceAll("(0*)([0-7]+)", "$2"); // remove preceeding 0's
                
                num = Long.parseLong(string, 8);
                
            } else {
                num = 0;
            }
            
        }
        
        return num;
        
    }
    
    // sets the value in a number field in tar header
    private void setNumField(final char[] field, long value) {
        
        char[] octal = Long.toOctalString((int) value).toCharArray();
        
        // ensure number is short enough for the field
        if (octal.length >= field.length)
            throw new IllegalArgumentException("Number field is not large enough for the value " + value);
        
        // set field value
        int octalIndex = octal.length-1;
        int fieldIndex = field.length-2; // extra character for terminating NUL
        while (octalIndex >= 0)
            field[fieldIndex--] = octal[octalIndex--];
        
        // pad with zeros
        while (fieldIndex >= 0)
            field[fieldIndex--] = '0';
        
        // NUL terminate
        field[field.length-1] = 0;
        
    }
    
    /**
     * Returns whether or not this {@code TarHeader} is an empty record.
     * 
     * @return whether or not this {@code TarHeader} is an empty record.
     */
    public boolean isEmpty() {
        return filename[0] == 0;
    }
    
    /** 
     * Returns an array of all fields in the header.
     * 
     * @param header Header to be read.
     * @return n array of all fields in the header.
     */
    public char[][] getFields() {
        
        char[][] field = {
            filename, 
            mode, 
            uid, 
            guid,  
            size, 
            modTime, 
            checksum, 
            typeFlag, 
            linkName, 
            magic, 
            version, 
            userName,
            groupName, 
            devMajor, 
            devMinor, 
            filenamePrefix
        };
        
        return field;
        
    }
    
    /**
     * Encodes this {@code TarHeader} into a {@code BLOCK_SIZE} byte header block.
     * 
     * @return the encoded {@code BLOCK_SIZE} byte block.
     */
    public byte[] encode() {
        
        byte[] block = new byte[BLOCK_SIZE];
        
        char[][] fields = getFields();
        
        int i;
        
        // set checksum to initial value
        for (i = 0; i < checksum.length; i++)
            checksum[i] = ' ';
        
        int sum = 0;
        
        // calculate checksum
        for (char[] field : fields) {
            for (char ch : field) {
                sum += Math.abs((int) ch);
            }
        }
        
        // add checksum to block
        setNumField(checksum, sum);
        
        // encode each field
        int blockIndex = 0;
        for (char[] field : getFields()) {
            for (i = 0; i < field.length; ) {
                block[blockIndex++] = (byte) (field[i++] & 0xff);
            }
        }
        
        return block;
        
    }
    
    /**
     * Decodes a {@code TarHeader} with the contents of the given 512 byte block.
     * 
     * @param block block to be read (must be 512 bytes).
     * @return the decoded {@code TarHeader}.
     */
    public void decode(final byte[] block) throws InvalidPackageException {
        
        if (block.length == BLOCK_SIZE) {
            
            // checksums
            int signedSum = 0, unsignedSum = 0;
            
            // read each field from the block
            int blockIndex = 0;
            for (char[] field : getFields()) {
                
                // fill each field
                for (int i = 0; i < field.length; ) {
                    
                    byte octet = block[blockIndex++];
                    
                    field[i++] = (char) octet;
                    
                    // handle checksum field
                    if (field == checksum)
                        octet = (byte) ' ';
                    
                    // calculate checksum
                    signedSum += octet;
                    unsignedSum += octet & 0xff;
                                        
                }
                
            }
            
            // check checksum
            if (!isEmpty()) {
            
                long checksum = parseNumField(this.checksum);
            
                if (checksum != signedSum && checksum != unsignedSum) {
                
                    StringBuilder msg = new StringBuilder();

                    msg.append("Header checksum for \"");
                    msg.append(getFileName());
                    msg.append("\" indicates an error. Source checksum is ");
                    msg.append(checksum);
                    msg.append(" the real calculate checksums are signed = ");
                    msg.append(signedSum);
                    msg.append(". unsigned = ");
                    msg.append(unsignedSum);
                    msg.append(".");

                    throw new InvalidPackageException(msg.toString());

                }
            
            }
            
        } else {
            
            StringBuilder msg = new StringBuilder();
            
            msg.append("Expected block of size ");
            msg.append(BLOCK_SIZE);
            msg.append(" but got block of size ");
            msg.append(block.length);
            msg.append(".");
            
            throw new IllegalArgumentException(msg.toString());
            
        }
        
    }
    
    /**
     * Returns the name of the file that {@code header} represents.
     * 
     * @param header Header to read.
     * @return Name of the file {@code header} represents.
     */
    public String getFileName() {
        
        StringBuilder name = new StringBuilder();
        
        name.append(new String(filename, 0, strlen(filename)));
        
        // add filename prefix
        if (isUSTAR() && strlen(filenamePrefix) > 0)
            name.append(new String(filenamePrefix, 0, strlen(filenamePrefix)));
        
        return name.toString();
        
    }
    
    /**
     * Sets the name of the file that is represented by this {@code TarHeader}.
     * 
     * @param name name of the file.
     */
    public void setFileName(final String name) {
        
        if (name == null) {
            setNullField(filename);
            return;
        }
        
        char[] nameChars = name.toCharArray();
        
        // initialise filename fields
        for (int i = 0; i < filename.length; i++)
            filename[i] = 0;
        for (int i = 0; i < filenamePrefix.length; i++)
            filenamePrefix[i] = 0;
        
        // break name into a base filename and its prefix
        boolean flag = isUSTAR() && nameChars.length > filename.length;
        if (flag) {
            int length = nameChars.length - filename.length;
            System.arraycopy(nameChars, filename.length, filenamePrefix, 0, length);
        }
        
        // copy filename
        System.arraycopy(nameChars, 0, filename, 0, Math.min(nameChars.length, FILENAME_SIZE));
        
    }
    
    /**
     * Returns the mode of the file.
     * 
     * @param header Header to be read.
     * @return the mode of the file.
     */
    public long getMode() {
        return parseNumField(mode);
    }
    
    /**
     * Sets the mode of the file represented by this {@code TarHeader}.
     * 
     * @param mode mode of the file.
     */
    public void setMode(long mode) {
        setNumField(this.mode, mode);
    }
    
    /**
     * Returns the user ID of the files owner.
     * 
     * @param header Header to be read.
     * @return the user ID of the files owner.
     */
    public long getUid() {
        return parseNumField(uid);
    }
    
    /**
     * Sets the user ID of the owner of the file represented by this {@code TarHeader}.
     * 
     * @param uid user ID of the owner of the file.
     */
    public void setUid(long uid) {
        setNumField(this.uid, uid);
    }
    
    /**
     * Returns the group ID of the files owner.
     * 
     * @param header Header to be read.
     * @return the group ID of the files owner.
     */
    public long getGuid() {
        return parseNumField(guid);
    }
    
    /**
     * Sets the group ID of the owner of the file represented by this {@code TarHeader}.
     * 
     * @param guid group ID of the owner of the file.
     */
    public void setGuid(long guid) {
        setNumField(this.guid, guid);
    }
    
    /**
     * Returns the size of the file.
     * 
     * @param header Header to be read.
     * @return size of the file represented by {@code header}.
     */
    public long getFileSize() {
        return parseNumField(size);
    }
    
    /**
     * Sets the size of the file represented by this {@code TarHeader}.
     * 
     * @param size size of the file.
     */
    public void setFileSize(long size) {
        setNumField(this.size, size);
    }
    
    /**
     * Returns the last modification time of the file.
     * 
     * @param header Header to be read.
     * @return the last modification time of the file.
     */
    public long getModTime() {
        return parseNumField(modTime);
    }
    
    /**
     * Sets the last modification time of the file represented by this {@code TarHeader}.
     * 
     * @param modTime the last modification time of the file.
     */
    public void setModTime(long modTime) {
        setNumField(this.modTime, modTime);
    }
    
    /**
     * Returns the checksum of this {@code TarHeader}.
     * 
     * @param header Header to be read.
     * @return the checksum of this {@code TarHeader}.
     */
    public long getChecksum() {
        return parseNumField(checksum);
    }
    
    /**
     * Returns the type of the file.
     * 
     * @param header Header to be read.
     * @return the type of the file.
     */
    public TarFileType getType() throws InvalidPackageException {
        
        // return appropriate TarFileType
        switch (typeFlag[0]) {
            
            case '0': case 0:
                return TarFileType.NORMAL;
                
            case '1':
                return TarFileType.HARD_LINK;
         
            case '2':
                return TarFileType.SYM_LINK;
                
            case '3':
                return TarFileType.CHAR_SPEC;
                
            case '4':
                return TarFileType.BLOCK_SPEC;
                
            case '5':
                return TarFileType.DIR;
                
            case '6':
                return TarFileType.FIFO;
                
            case '7':
                return TarFileType.CONT;
                
            default:
                
                StringBuilder msg = new StringBuilder();
                
                msg.append("Unknown file type ");
                msg.append(typeFlag[0]);
                msg.append(" for TAR file system.");
                
                throw new InvalidPackageException(msg.toString());
                
        }
        
    }
    
    /**
     * Sets the type of file represented by this {@code TarHeader}.
     * 
     * @param type the type of file.
     */
    public void setType(TarFileType type) {
       this.typeFlag[0] = (char) ('0' + type.getType());
    }
    
    /**
     * Returns the name of the file that the header links to.
     * 
     * @param header Header to be read.
     * @return the name of the file that the header links to.
     */
    public String getLinkName() {
        return new String(linkName, 0, strlen(linkName));
    }
    
    /**
     * Sets the name of the file that this {@code TarHeader} links to.
     * 
     * @param linkName the name of the file that this {@code TarHeader} links to.
     */
    public void setLinkName(final String linkName) {
        
        if (linkName == null) {
            setNullField(this.linkName);
            return;
        }
        
        char[] charArray = linkName.toCharArray();
        System.arraycopy(charArray, 0, this.linkName, 0, charArray.length);
        
    }
    
    /**
     * Returns the value in the magic constant area for the ustar format.
     * 
     * @param header Header to be read.
     * @return the value in the magic constant area for the ustar format.
     */
    public String getMagic() {
        return new String(magic, 0, strlen(magic));
    }
    
    /**
     * Returns the version of the ustar format.
     * 
     * @param header Header to be read.
     * @return the version of the ustar format (should always be "00").
     */
    public String getVersion() {
        return new String(version, 0, strlen(version));
    }
        
    /**
     * Returns the user name of the files owner.
     * 
     * @param header Header to be read.
     * @return the user name of the files owner.
     */
    public String getUserName() {
        return new String(userName, 0, strlen(userName));
    }
    
    /**
     * Sets the user name of the owner of the file.
     * 
     * @param userName the user name of the owner of the file.
     */
    public void setUserName(final String userName) {
        
        if (userName == null) {
            setNullField(this.userName);
            return;
        }
        
        char[] charArray = userName.toCharArray();
        System.arraycopy(charArray, 0, this.userName, 0, charArray.length);
        
    }
    
    /**
     * Returns the group name of the files owner.
     * 
     * @param header Header to be read.
     * @return the group name of the files owner.
     */
    public String getGroupName() {
        return new String(groupName, 0, strlen(groupName));
    }
    
    /**
     * Sets the group name of the owner of the file.
     * 
     * @param linkName the group name of the owner of the file.
     */
    public void setGroupName(final String groupName) {
        
        if (groupName == null) {
            setNullField(this.groupName);
            return;
        }
        
        char[] charArray = groupName.toCharArray();
        System.arraycopy(charArray, 0, this.groupName, 0, charArray.length);
        
    }
    
    /**
     * Returns the major number of the device the archive is on (unused).
     * 
     * @param header Header to be read.
     * @return the major number of the device the archive is on.
     */
    public long getDevMajor() {
        return parseNumField(devMajor);
    }
    
    /**
     * Sets the major device number this {@code TarHeader} is on.
     * 
     * @param devNo the major device number.
     */
    public void setDevMajor(long devNo) {
        setNumField(devMajor, devNo);
    }
    
    /**
     * Returns the minor number of the device the archive is on (unused).
     * 
     * @param header Header to be read.
     * @return the minor number of the device the archive is on.
     */
    public long getDevMinor() {
        return parseNumField(devMinor);
    }
    
    /**
     * Sets the minor device number this {@code TarHeader} is on.
     * 
     * @param devNo the minor device number.
     */
    public void setDevMinor(long devNo) {
        setNumField(devMinor, devNo);
    }
    
    /**
     * Returns whether the given {@code TarHeader} is equivalent to this {@code TarHeader}.
     * 
     */
    @Override
    public boolean equals(final Object object) {
        
        boolean equalFlag = false;
        
        if (object instanceof TarHeader) {
            
            TarHeader header = (TarHeader) object;
            
            try {
                
                // compare header fields
                equalFlag = getFileName().equals(header.getFileName());
                equalFlag &= getMode() == header.getMode();
                equalFlag &= getUid() == header.getUid();
                equalFlag &= getGuid() == header.getGuid();
                equalFlag &= getFileSize() == header.getFileSize();
                equalFlag &= getModTime() == header.getModTime();
                equalFlag &= getChecksum() == header.getChecksum();
                equalFlag &= getType().equals(header.getType());
                equalFlag &= getLinkName().equals(header.getLinkName());
                equalFlag &= isUSTAR() == header.isUSTAR();
                
                // check USTAR fields
                if (isUSTAR() && header.isUSTAR()) {
                    equalFlag &= getMagic().equals(header.getMagic());
                    equalFlag &= getVersion().equals(header.getVersion());
                    equalFlag &= getUserName().equals(header.getUserName());
                    equalFlag &= getGroupName().equals(header.getGroupName());
                    equalFlag &= getDevMajor() == header.getDevMajor();
                    equalFlag &= getDevMinor() == header.getDevMinor();
                }
                
            } catch (InvalidPackageException ex) { // unknown TarFileType
                equalFlag = false;
            }
            
        }
        
        return equalFlag;
        
    }
    
}

// EOF
