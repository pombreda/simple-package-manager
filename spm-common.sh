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
setTrue() {
    $1=0
}
setFalse() {
    $1=1
}
isTrue() {
    return `test "$1" -eq 0`
}
isFalse() {
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
        true ; return
    fi

    # file is not a regular file
    false ; return

}

# Asks the user a yes or no question.
# ask [question]
ask() {

    local a

    while true; do

        echo -n "$1 (y/n) " > /dev/tty
        read a

        if [ "$a" == "yes" -o "$a" == "y" ]; then
            true ; return
        elif [ "$a" == "no" -o "$a" == "n" ]; then
            false ; return
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
    if isFalse $?; then
        false ; return
    fi

    # create temporary directory
    if [ -e "$TMP_DIR" ]; then rm -r "$TMP_DIR" ; fi
    mkdir "$TMP_DIR"

    cd "$TMP_DIR"

    # extract package to temporary directory
    gunzip -c "$package" | tar x

    # check the integrity of the files
    sha1sum -c digest
    if isFalse $?; then
        error "File integrity check failed!"
        false ; return
    fi

    cd - > /dev/null

    true ; return

}

# Closes the package opened with openPackage
closePackage() {
    rm -r "$TMP_DIR"
}

# Runs the given file in a package
# runPackage <file to run> [file arguments]
runPackage() {

    cd "$TMP_DIR/data"

    # execute given files in the package
    if [ $# -gt 0 ]; then

        cmd="../$1"
        shift

        $cmd "$@"
        if isFalse $?; then
            false ; return
        fi

    fi

    cd - > /dev/null

    true ; return

}

# EOF
