package spm;
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

import java.util.ArrayList;
import java.util.Arrays;
import org.junit.runner.*;
import org.junit.runner.notification.Failure;
import org.junit.runners.Suite;

import spm.util.*;
import spm.format.*;
import spm.format.tar.*;

/**
 * Runs all test classes.
 * 
 * @author Zachary Scott <cthug.zs@gmail.com>
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({TarHeaderTest.class, // FIXME create subclass of Suite
                     TarArchiveTest.class, 
                     SPMPackageTest.class,
                     SHA1Test.class,
                     SPMDigestTest.class
                    })
public class AllTests {
    
    public static final Class[] classes = {
        TarHeaderTest.class, 
        TarArchiveTest.class, 
        SPMPackageTest.class,
        SHA1Test.class,
        SPMDigestTest.class
    };
    
    public static void main(String[] args) {
        
        Result result = JUnitCore.runClasses(classes);
        for (Failure failure : result.getFailures()) {
            System.out.println(failure.toString() + System.getProperty("line.separator"));
        }
        
    }
    
}

// EOF

