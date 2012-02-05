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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.*;
import spm.gui.Util;

import spm.format.tar.*;

/**
 * Wraps a {@code TarArchive} to represent a SPM package.
 *
 * @author Zachary Scott <cthug.zs@gmail.com>
 */
public final class SPMPackage {
    
    private final static Logger logger = Logger.getLogger(SPMPackage.class.getName());
    
    private static final String FILE_PREFIX = "src" + System.getProperty("file.separator");
    
    private static final String LICENSE_NAME = "license";
    private static final String BUILD_NAME = "build";
    private static final String INSTALL_NAME = "install";
    private static final String UNINSTALL_NAME = "uninstall";
    private static final String DIGEST_NAME = "digest";
    
    // location of this package
    private File file = null;
    
    // the package archive
    private SPMPackageContainer archive = new SPMPackageContainer();
    
    // package file contents
    private byte[] license = null;
    private byte[] build = null;
    private byte[] install = null;
    private byte[] uninstall = null;
    
    // SHA1 digests file
    private SPMDigest digest = new SPMDigest();
    
    /**
     * Creates a new instance of {@code SPMPackage}.
     * 
     */
    public SPMPackage() {
        
    }
    
    /**
     * Creates a new instance of {@code SPMPackage} with the given name.
     * 
     * @param name name of the package (package path name).
     */
    public SPMPackage(final String name) {
        setFile(file);
    }
    
    /**
     * Creates a thread to read a package from the given file.
     * 
     * @param file the file to be read.
     * @return the thread that will read the file.
     */
    public Thread newReadThread(final File file) {
        
        Thread readThread = new Thread() {
        
            {
                setPriority(Thread.MAX_PRIORITY);
            }
            
            /** Runs the thread. */
            @Override
            public void run() {

                // attempt to read package archive
                try {

                    read(file);

                } catch (FileNotFoundException ex) {

                    StringBuilder msg = new StringBuilder();

                    msg.append("The file \"");
                    msg.append(file.getPath());
                    msg.append("\" does not exist!");

                    Util.showErrorDialog(null, msg.toString());
                    logger.log(Level.INFO, msg.toString(), ex);

                } catch (IOException ex) {

                    StringBuilder msg = new StringBuilder();

                    msg.append("Cannot read from \"");
                    msg.append(file.getPath());
                    msg.append("\"! \n");
                    msg.append(ex.getMessage());

                    Util.showErrorDialog(null, msg.toString());
                    logger.log(Level.INFO, msg.toString(), ex);

                } catch (InvalidPackageException ex) {

                    StringBuilder msg = new StringBuilder();

                    msg.append("The package \"");
                    msg.append(file.getPath());
                    msg.append("\" is not a valid package! \n");
                    msg.append(ex.getMessage());

                    Util.showErrorDialog(null, msg.toString());
                    logger.log(Level.INFO, msg.toString(), ex);

                } catch (SPMDigestException ex) {

                    StringBuilder msg = new StringBuilder();

                    msg.append("The package \"");
                    msg.append(file.getPath());
                    msg.append("\" indcates an error. This could mean that the package has become corrupted.");

                    Util.showErrorDialog(null, msg.toString());
                    logger.log(Level.INFO, msg.toString(), ex);

                }
                
            }
                
        };
        
        return readThread;
        
    }
    
    /**
     * Creates a thread to read a package from the given file.
     * 
     * @param filename the name of the file to read.
     * @return the thread that will read the file.
     */
    public Thread newReadThread(final String filename) {
        return newReadThread(new File(filename));
    }
    
    /**
     * Reads a package from the given input stream.
     * 
     * @param input stream to be read.
     * @throws IOException upon failure to read from the input stream.
     * @throws InvalidPackageException if the read package is not valid or has become corrupted.
     * @throws SPMDigestException if the SHA1 digests indicated an error in one of the files.
     */
    public void read(final InputStream input) throws IOException, InvalidPackageException, SPMDigestException {
        
        archive = new SPMPackageContainer();
        archive.read(input);
        
        // get special package files
        license = archive.getFileContents(LICENSE_NAME);
        build = archive.getFileContents(BUILD_NAME);
        install = archive.getFileContents(INSTALL_NAME);
        uninstall = archive.getFileContents(UNINSTALL_NAME);
        
        // load digest file
        byte[] digestBuffer = archive.getFileContents(DIGEST_NAME);
        if (digestBuffer != null) {
            
            digest.read(new StringBuffer(new String(digestBuffer, "UTF-8")));
            
            // check the SHA1 digest
            if (!digest.check(archive)) {
                
                StringBuilder msg = new StringBuilder();
                
                msg.append("The SHA1 digest for the SPMPackage \"");
                msg.append(file.getName());
                msg.append("\" does not match those given in the package.");
                
                throw new InvalidPackageException(msg.toString());
                
            }
            
        } else { // digest file does not yet exist
            newFile(DIGEST_NAME);
        }
        
    }
    
    /**
     * Reads a package from the given {@code File}.
     * 
     * @param file the file to be read.
     * @throws FileNotFoundException if the file could not be opened for reading.
     * @throws IOException upon failure to read from the input stream.
     * @throws InvalidPackageException if the read package is not valid or has become corrupted.
     * @throws SPMDigestException if the SHA1 digests indicated an error in one of the files.
     */
    public void read(final File file) throws FileNotFoundException, 
                                             IOException, 
                                             InvalidPackageException, 
                                             SPMDigestException {
        
        this.file = file;
        read(new BufferedInputStream(new FileInputStream(file)));
        
    }
    
    /**
     * Reads a package from the file named {@code filename}.
     * 
     * @param filename the name of the file to be read.
     * @throws FileNotFoundException if the file could not be opened for reading.
     * @throws IOException upon failure to read from the input stream.
     * @throws InvalidPackageException if the read package is not valid or has become corrupted.
     * @throws SPMDigestException if the SHA1 digests indicated an error in one of the files.
     */
    public void read(final String filename) throws FileNotFoundException, 
                                                   IOException, 
                                                   InvalidPackageException, 
                                                   SPMDigestException {
        
        setFile(filename);
        read(new File(filename));
        
    }
    
    /**
     * Writes this {@code SPMPackage} to the given output stream.
     * 
     * @param output the stream to write to.
     * @throws IOException upon failure to write to the output stream.
     * @throws InvalidPackageException if this package has become corrupted.
     */
    public void write(final OutputStream output) throws IOException, InvalidPackageException {
        
        // update digest file
        digest.update(archive);
        archive.setFileContents(DIGEST_NAME, digest.toString().getBytes("UTF-8"));
        
        archive.write(output);
        
    }
    
    /**
     * Writes this {@code SPMPackage} to the given output file.
     * 
     * @param file the file to write to.
     * @throws IOException upon failure to write to the output file.
     * @throws InvalidPackageException if this package has become corrupted.
     */
    public void write(final File file) throws IOException, InvalidPackageException {
        this.file = file;
        write(new BufferedOutputStream(new FileOutputStream(file)));
    }
    
    /**
     * Writes this {@code SPMPackage} to the output file named {@code filename}.
     * 
     * @param filename name of the file to write to.
     * @throws IOException upon failure to write to the output file.
     * @throws InvalidPackageException if this package has become corrupted.
     */
    public void write(final String filename) throws IOException, InvalidPackageException {
        setFile(filename);
        write(new File(filename));
    }
    
    // temporary directory for file extraction
    private final File tempDir = new File(System.getProperty("java.io.tmpdir") + 
                                          File.separator + 
                                          "spm-" + Integer.toHexString(hashCode()) +
                                          File.separator);
    
    // executes the given file in the package and returns the Process representing it
    private Process execute(final String filename) throws FileNotFoundException, 
                                                         InvalidPackageException, 
                                                         IOException {
        
        StringBuilder string = new StringBuilder();
        
        tempDir.deleteOnExit();
        
        // extract package files
        if (!tempDir.exists()) {
            archive.extract(tempDir, true);
        }
        
        String longFilename = tempDir.getPath() + File.separator + filename;
        File dataDir = new File(tempDir, "data");
        
        // ensure that the file can be executed
        new File(longFilename).setExecutable(true);
        
        return Runtime.getRuntime().exec(longFilename, null, dataDir);
        
    }
    
    /**
     * Builds the package (but does not install it).
     * 
     * @return the value of the processes stdout and stderr.
     * @throws FileNotFoundException if the build file does not exist.
     * @throws InvalidPackageException if this {@code SPMPackage} has become corrupted.
     * @throws IOException if file cannot be executed.
     */
    public Process[] build() throws FileNotFoundException, 
                                 InvalidPackageException, 
                                 IOException {
        
        Process[] processes = new Process[1];
        
        processes[0] = execute(BUILD_NAME);
        
        return processes;
        
    }
    
    /**
     * Returns the {@code SPMExecutor} for the build file in this package.
     * 
     * @return the {@code SPMExecutor} for the build file in this package.
     * @throws FileNotFoundException if the build file does not exist.
     * @throws InvalidPackageException if this {@Code SPMPackage} is not valid or has become corrupted.
     * @throws IOException upon failure to read the build file.
     */
    public SPMExecutor getBuildExecutor() throws FileNotFoundException, 
                                                 InvalidPackageException, 
                                                 IOException {
        
        return new SPMExecutor(build());
        
    }
    
    /**
     * Builds and installs the package.
     * 
     * @return the value of the processes stdout and stderr.
     * @throws FileNotFoundException if the install file does not exist.
     * @throws InvalidPackageException if this {@code SPMPackage} has become corrupted.
     * @throws IOException if file cannot be executed.
     */
    public Process[] install() throws FileNotFoundException, 
                                      InvalidPackageException, 
                                      IOException {
        
        ArrayList<Process> processes = new ArrayList<Process>();
        
        processes.addAll(Arrays.asList(build()));
        processes.add(execute(INSTALL_NAME));
        
        return processes.toArray(new Process[processes.size()]);
        
    }
    
    /**
     * Returns the {@code SPMExecutor} for the installation file in this package.
     * 
     * @return the {@code SPMExecutor} for the installation file in this package.
     * @throws FileNotFoundException if the installation file does not exist.
     * @throws InvalidPackageException if this {@Code SPMPackage} is not valid or has become corrupted.
     * @throws IOException upon failure to read the installation file.
     */
    public SPMExecutor getInstallExecutor() throws FileNotFoundException, 
                                                   InvalidPackageException, 
                                                   IOException {
        
        return new SPMExecutor(install());
        
    }
    
    /**
     * Un-installs the package.
     * 
     * @return the value of the processes stdout and stderr.
     * @throws FileNotFoundException if the un-install file does not exist.
     * @throws InvalidPackageException if this {@code SPMPackage} has become corrupted.
     * @throws IOException if file cannot be executed.
     */
    public Process[] uninstall() throws FileNotFoundException, 
                                        InvalidPackageException, 
                                        IOException {
        
        Process[] processes = new Process[1];
        
        processes[0] = execute(UNINSTALL_NAME);
        
        return processes;
        
    }
    
    /**
     * Returns the {@code SPMExecutor} for the un-installation file in this package.
     * 
     * @return the {@code SPMExecutor} for the un-installation file in this package.
     * @throws FileNotFoundException if the un-installation file does not exist.
     * @throws InvalidPackageException if this {@Code SPMPackage} is not valid or has become corrupted.
     * @throws IOException upon failure to read the un-installation file.
     */
    public SPMExecutor getUninstallExecutor() throws FileNotFoundException, 
                                                  InvalidPackageException, 
                                                  IOException {
        
        return new SPMExecutor(uninstall());
        
    }
    
    /**
     * Un-installs then re-installs the package.
     * 
     * @return the value of the processes stdout and stderr.
     * @throws FileNotFoundException if the update file does not exist.
     * @throws InvalidPackageException if this {@code SPMPackage} has become corrupted.
     * @throws IOException if file cannot be executed.
     */
    public Process[] update() throws FileNotFoundException, 
                                     InvalidPackageException, 
                                     IOException {
        
        ArrayList<Process> processes = new ArrayList<Process>();
        
        processes.addAll(Arrays.asList(uninstall()));
        processes.addAll(Arrays.asList(install()));
        
        return processes.toArray(new Process[processes.size()]);
        
    }
    
    /**
     * Returns the {@code SPMExecutor} for updating this package.
     * 
     * @return the {@code SPMExecutor} for the update files in this package.
     * @throws FileNotFoundException if the update files does not exist.
     * @throws InvalidPackageException if this {@Code SPMPackage} is not valid or has become corrupted.
     * @throws IOException upon failure to read the update files.
     */
    public SPMExecutor getUpdateExecutor() throws FileNotFoundException, 
                                               InvalidPackageException, 
                                               IOException {
        
        return new SPMExecutor(update());
        
    }
    
    /**
     * Creates a new file in the package named {@code filename}.
     * 
     * @param filename name of the new file.
     */
    public void newFile(final String filename) {
        archive.newFile(FILE_PREFIX + filename);
    }
    
    /**
     * Deletes the first file in the package which name matches {@code pattern}.
     * 
     * @param pattern regex pattern to be matched.
     */
    public void deleteFile(final String pattern) {
        archive.deleteFile(pattern);
    }
    
    /**
     * Deletes the all files in the package which name matches {@code pattern}.
     * 
     * @param pattern regex pattern to be matched.
     */
    public void deleteFiles(final String pattern) {
        archive.deleteFiles(pattern);
    }
    
    /**
     * Adds a new file named {@code filename} and loads its contents from {@code fileContents}.
     * 
     * @param filename name of the file to read.
     * @param fileContents the contents of the file.
     */
    public void addFile(final String filename, final byte[] fileContents) {
        archive.addFile(FILE_PREFIX + filename, fileContents);
    }
    
    /**
     * Adds a new file named {@code filename} and loads its contents from disk.
     * 
     * @param filename name of the file to read.
     * @throws IOException upon failure to read from the file.
     * @throws FileNotFoundException upon failure to open the given file.
     */
    public void addFile(final File file) throws IOException, FileNotFoundException {
        archive.addFile(FILE_PREFIX + file.getPath());
    }
    
    /**
     * Adds a new file named {@code filename} and loads its contents from disk.
     * 
     * @param filename name of the file to read.
     * @throws IOException upon failure to read from the file.
     * @throws FileNotFoundException upon failure to open the given file.
     */
    public void addFile(final String filename) throws IOException, FileNotFoundException {
        archive.addFile(FILE_PREFIX + filename);
    }
    
    /**
     * Returns the first file in the package which name matches the given pattern.
     * 
     * @param pattern regex pattern to be matched.
     * @return the first file in the package which name matches the given pattern.
     */
    public byte[] getFile(final String pattern) {
        return archive.getRecord(FILE_PREFIX + "(?:" + pattern + ")").getFileContents();
    }
    
    /**
     * Returns the all the files in the package which name matches the given pattern.
     * 
     * @param pattern regex pattern to be matched.
     * @return the all the files in the package which name matches the given pattern.
     */
    public byte[][] getFiles(final String pattern) {
        
        TarRecord[] records = archive.getRecords(FILE_PREFIX + "(?:" + pattern + ")");
        byte[][] files = new byte[records.length][];
        
        // get the contents of each of the files
        for (int i = 0; i < records.length ; i++)
            files[i] = records[i].getFileContents();
        
        return files;
        
    }
    
    /**
     * Returns the file representing this {@code SPMPackage}'s location on disk.
     * 
     * @return the file representing this packages location on disk (can be {@code null}).
     */
    public File getFile() {
        return file;
    }
    
    /**
     * Returns the location of this {@code SPMPackage}'s on disk.
     * 
     * @return the location of this {@code SPMPackage}'s on disk (can be {@code null}).
     */
    public String getPath() {
        return file.getPath();
    }
    
    /**
     * Returns the short name of this {@code SPMPackage}.
     * 
     * @return the short name of this {@code SPMPackage} (can be {@code null}).
     */
    public String getName() {
        return file.getName();
    }

    /**
     * Sets the location of this {@code SPMPackage} on disk.
     * 
     * @param file the abstract file name representing the location of this {@code SPMPackage} on disk (can be
     *        {@code null}).
     */
    public void setFile(final File file) {
        this.file = file;
    }
    
    /**
     * Sets the location of this {@code SPMPackage} on disk.
     * 
     * @param file the name of the location of this {@code SPMPackage} on disk (can be {@code null}).
     */
    public void setFile(final String filename) {
        
        if (filename == null) 
            file = null;
        else
            file = new File(filename);
        
    }

    // sets one of the special files contens
    private void setSpecialFileContents(final String filename, final byte[] fileContents) {
        
        // put file contents in the files record
        TarRecord record = archive.getRecord(filename);
        if (record == null) {
            
            // create the file if it does not exist
            archive.newFile(filename);
            setLicense(fileContents);
            
        } else {
            record.setFileContents(fileContents);
        }
        
    }
    
    // attempt to convert a byte array to a string
    private String getString(final byte[] bytes) {
        
        try {
            return new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            
            logger.log(Level.SEVERE, "Cannot convert byte array into UTF-8 string!", ex);
            System.exit(1);
            
        }
        
        return null;
        
    }
    
    // returns a string as an array of bytes
    private byte[] getBytes(final String string) {
        
        try {
            return string.getBytes("UTF-8");
        } catch (UnsupportedEncodingException ex) {
            
            logger.log(Level.SEVERE, "Cannot convert string to array of UTF-8 encoded bytes!", ex);
            System.exit(1);
            
        }
        
        return null;
        
    }
    
    public byte[] getLicense() {
        return license;
    }
    
    public String getLicenseString() {
        return getString(license);
    }

    public void setLicense(final byte[] license) {
        this.license = license;
        setSpecialFileContents(LICENSE_NAME, license); 
    }
    
    public void setLicenseString(final String license) {
        setLicense(getBytes(license));
    }
    
    public byte[] getBuild() {
        return build;
    }
    
    public String getBuildString() {
        return getString(build);
    }

    public void setBuild(final byte[] build) {
        this.build = build;
        setSpecialFileContents(BUILD_NAME, build); 
    }
    
    public void setBuildString(final String build) {
        setBuild(getBytes(build));
    }

    public byte[] getInstall() {
        return install;
    }
    
    public String getInstallString() {
        return getString(install);
    }

    public void setInstall(final byte[] install) {
        this.install = install;
        setSpecialFileContents(INSTALL_NAME, install); 
    }
    
    public void setInstallString(final String install) {
        setInstall(getBytes(install));
    }
    
    public byte[] getUninstall() {
        return uninstall;
    }
    
    public String getUninstallString() {
        return getString(uninstall);
    }
    
    public void setUninstall(final byte[] uninstall) {
        this.uninstall = uninstall;
        setSpecialFileContents(UNINSTALL_NAME, uninstall); 
    }
    
    public void setUninstallString(final String uninstall) {
        setUninstall(getBytes(uninstall));
    }
    
}

// EOF
