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

import java.net.*;
import java.util.logging.*;

/**
 * Configuration information for the program.
 * 
 * @author Zachary Scott <cthug.zs@gmail.com>
 */
public final class Config {
    
    private final static Logger logger = Logger.getLogger(Config.class.getName());
    
    /** name of the program. */
    public static final String CMD_NAME = "spm-gui";
    
    /** Location of the online wiki. */
    public static final String WIKI_LOCATION = 
            "http://code.google.com/p/simple-package-manager/wiki/TableOfContents";
    
    /** {@code URI} of the online wiki. */
    public static final URI WIKI_URI;
    
    /** Location of the spm home page. */
    public static final String HOMEPAGE_LOCATION = 
            "http://code.google.com/p/simple-package-manager/";
    
    /** {@code URI} of the spm home page. */
    public static final URI HOMEPAGE_URI;
    
    static {
        
        // set the URI of the homepage and the wiki
        URI wikiURI = null;
        URI homepageURI = null;
        try {
            
            wikiURI = new URI(WIKI_LOCATION);
            homepageURI = new URI(HOMEPAGE_LOCATION);
            
        } catch (Exception ex) {
            
            logger.log(Level.SEVERE, "Cannot set a URI!", ex);
            
        } finally {
            
            WIKI_URI = wikiURI;
            HOMEPAGE_URI = homepageURI;
            
        }
        
    }
            
}

// EOF
