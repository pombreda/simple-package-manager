#!/bin/bash
# spm-upgrade - upgrades a package.
# Copyright (C) 2011, Zachary Scott
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.

# include common functions
. "`dirname $0`/spm-common.sh"

# Prints usage information.
usage() {
    echo "
Usage: $pname <package file ...>
" >&2
    true ; exit
}

# ensure that there is enough arguments
[ $# -lt 1 ] && usage

# update each package
for arg; do
    
    checkRegFile "$arg" || { false ; exit ; }
    openPackage "$arg" || { false ; exit ; }

    runPackage uninstall install
    if isTrue $?; then
        echo "Successfully updated!" > /dev/tty
    else
        error "Update failed!" > /dev/tty
        false ; exit
    fi

    closePackage

done

# EOF

