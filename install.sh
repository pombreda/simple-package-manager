#!/bin/bash
# install.sh - installation script for spm package.
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

errorFlag=1

# Reports error
error() {
    
    echo "error: "
    for arg; do
        echo -n "$arg" >&2
    done
    echo

    errorFlag=0

}

# Checks for a dependancy
checkDep() {

    pathlist=`echo $PATH | sed 's/:/ /g'`

    # try to find given file
    for dir in $pathlist; do
        [ -f $dir/$1 ] && return 0
    done

    error "Missing dependancy \"$1\"!"
    return 1

}

# check for dependancies
checkDep "basename"
checkDep "dirname"
checkDep "less"
checkDep "tar"
checkDep "gzip"
checkDep "gunzip"
checkDep "sha1sum"
checkDep "curl"

# install package files
cd bin
if [ $errorFlag -ne 0 ]; then
    for file in *; do
        yes | rm /usr/bin/$file 2> /dev/null
        cp $file /usr/bin
    done
fi

# EOF

