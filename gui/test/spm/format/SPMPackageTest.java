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

import org.junit.Test;
import static org.junit.Assert.*;
import java.io.*;

/**
 * Test for {@code SPMPackage}, abstract SPM package class.
 * 
 * @author Zachary Scott <cthug.zs@gmail.com>
 */
public class SPMPackageTest {
    
    private static final String TEST_PACKAGE = "test/test.spm";
    SPMPackage archive = new SPMPackage();
    
    // terst archive file content values
    private static final String BUILD_CONTENTS = "echo \"Build script executed!\"\n";
    private static final String INSTALL_CONTENTS = "echo \"Install script executed!\"\n";
    private static final String UNINSTALL_CONTENTS = "echo \"Uninstall script executed!\"\n";
    private static final String LICENSE_CONTENTS = "\nTHIS IS THE LICENSE TEXT.\n";
    
    public SPMPackageTest() throws Exception {
        archive.read(TEST_PACKAGE);
    }
    
    private void testRead(final File file) throws Exception {
        
        // read package
        SPMPackage packageArchive = new SPMPackage();
        packageArchive.read(file.getPath());
        
        // check special files values
        assertEquals(packageArchive.getBuildString(), BUILD_CONTENTS);
        assertEquals(packageArchive.getInstallString(), INSTALL_CONTENTS);
        assertEquals(packageArchive.getUninstallString(), UNINSTALL_CONTENTS);
        assertEquals(packageArchive.getLicenseString(), LICENSE_CONTENTS);
        
    }
    
    @Test
    public void testRead() throws Exception {
        testRead(new File(TEST_PACKAGE));
    }
    
    // FIXME writing of package fails
    //@Test
    public void testWrite() throws Exception {
        
        File temp = new File("test/temp.spm");
        
        SPMPackage packageArchive = new SPMPackage();
        
        // set package values
        packageArchive.setBuildString(BUILD_CONTENTS);
        packageArchive.setInstallString(INSTALL_CONTENTS);
        packageArchive.setUninstallString(UNINSTALL_CONTENTS);
        packageArchive.setLicenseString(LICENSE_CONTENTS);
        
        // write package to disk
        packageArchive.write(temp.getPath());
        
        // check that package was written correctly
        testRead(temp);
        
        temp.delete();
        
    }
    
    @Test
    public void testBuild() throws Exception {
        System.out.println(archive.getBuildExecutor().getOutput());
    }
    
    @Test
    public void testInstall() throws Exception {
        System.out.println(archive.getInstallExecutor().getOutput());
    }
    
    @Test
    public void testUninstall() throws Exception {
        System.out.println(archive.getUninstallExecutor().getOutput());
    }
    
    @Test
    public void testUpdate() throws Exception {
        System.out.println(archive.getUpdateExecutor().getOutput());
    }
    
}

// EOF
