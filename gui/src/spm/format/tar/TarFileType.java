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

/**
 * The type of file represented by a {@code TarHeader}.
 * 
 * @author Zachary Scott <cthug.zs@gmail.com>
 */
public enum TarFileType {
    
    NORMAL(0), HARD_LINK(1), SYM_LINK(2), CHAR_SPEC(3), BLOCK_SPEC(4), DIR(5), FIFO(6), CONT(7);
    
    private final int type;
    
    private TarFileType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
    
    @Override
    public final String toString() {
        
        if (type == NORMAL.getType()) {
            return "Normal";
        } else if (type == HARD_LINK.getType()) {
            return "Hard link";
        } else if (type == SYM_LINK.getType()) {
            return "Symbolic link";
        } else if (type == CHAR_SPEC.getType()) {
            return "Character special";
        } else if (type == BLOCK_SPEC.getType()) {
            return "Block special";
        } else if (type == DIR.getType()) {
            return "Directory";
        } else if (type == FIFO.getType()) {
            return "FIFO";
        } else if (type == CONT.getType()) {
            return "Contiguous file";
        } else {
            return null; // should never happen
        }
        
    }
    
}

// EOF

