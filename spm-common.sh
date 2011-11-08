#!/bin/bash
# spm-common - simple package manager common functions
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

# program name
pname=${0##*/}

# temporary output directory name
TMP_DIR=/tmp/spm-$$

# hide bash's silly boolean values
istrue() { 
    return `test "$1" -eq 0`
}
isfalse() { 
    return `test "$1" -ne 0`
}

# Prints an error message
# error [error message ...]
error() {

    echo -n "$pname: error: " >&2
    for arg; do
        echo -n "$arg" >&2
    done
    echo >&2

}

# Returns whether the given file is a regular file
# checkRegFile <filename>
checkRegFile() {

    if [ ! -e "$1" ]; then
        error "$1 does not exist!"
    elif [ -d "$1" ]; then
        error "$1 is a directory!"
    elif [ ! -f "$1" ]; then
        error "$1 is not a regular file!"
    else
        # file is a regular file
        return 0
    fi

    # file is not a regular file
    return 1

}

# Asks the user a yes or no question.
# ask [question]
ask() {

    local a

    while true; do

        echo -n "$1 (y/n) " > /dev/tty
        read a

        if [ "$a" == "yes" -o "$a" == "y" ]; then
            return 0
        elif [ "$a" == "no" -o "$a" == "n" ]; then
            return 1
        fi

    done

}

# Returns the absolute file name of the given file
# getAbsoluteName <file>
getAbsoluteName() {
    cd `dirname "$1"`; echo "`pwd`/`basename "$1"`" ; cd - > /dev/null
}

# Opens an package for use with other commands
# openPackage <package>
openPackage() {

    local package

    # get package name
    package=`getAbsoluteName "$1"`
    shift

    # ensure package is a regular file
    if isfalse $?; then
        return 1
    fi

    # create temporary directory
    if [ -e "$TMP_DIR" ]; then rm -r "$TMP_DIR" ; fi
    mkdir "$TMP_DIR"

    cd "$TMP_DIR"

    # extract package to temporary directory
    gunzip -c "$package" | tar x 

    # check the integrity of the files
    sha1sum -c digest
    if isfalse $?; then
        error "File integrity check failed!"
        return 1
    fi

    cd - > /dev/null

    return 0

}

# Closes the package opened with openPackage
closePackage() {
    rm -r "$TMP_DIR"
}

# Runs the given file in a package
# runPackage [files to run ...]
runPackage() {

    cd "$TMP_DIR/data"

    # execute given files in the package
    while [ $# -gt 0 ]; do

        ../$1
        isfalse $? && return 1

        shift

    done

    cd - > /dev/null

    return 0 
    
}

# EOF

