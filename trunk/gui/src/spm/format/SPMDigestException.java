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

/**
 * Indicates an error in a {@code SPMDigest}.
 *
 * @author Zachary Scott <cthug.zs@gmail.com>
 */
public class SPMDigestException extends Exception {

    /**
     * Creates a new instance of {@code SPMDigestException} without detail message.
     * 
     */
    public SPMDigestException() {
    
    }

    /**
     * Constructs an instance of {@code SPMDigestException} with the specified detail message.
     * 
     * @param msg the detail message.
     */
    public SPMDigestException(final String msg) {
        super(msg);
    }
    
}

// EOF
