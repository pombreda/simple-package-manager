#!/bin/bash
# $Id$
# spm-create - Creates a new spm package
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
. $(dirname $0)/spm-common.sh

# Prints usage information.
usage() {

    echo "
Usage: $pname <package name> <install script> <un-install script> 
              <build script> <license file> [package file ...]
    " >&2

    exit 0

}

# Create the specified package
# create <package name> <install script> <un-install script> <build script> 
#        <license file> [files ...]
create() {

    local package

    # create temporary directory
    if [ -e $TMP_DIR ]; then rm -r $TMP_DIR ; fi
    mkdir $TMP_DIR

    # get package name
    package=`getAbsoluteName $1`
    if [ "${package##*.}" != "spm" ]; then
        package=${package}.spm
    fi
    shift

    # add scripts and digest files
    for file in "install" "uninstall" "build" "license"; do

        if [ $1 == "-" ]; then
            touch $TMP_DIR/$file
        else

            # check that given file exists
            checkRegFile $1
            if isfalse $?; then 
                exit 1
            fi
        
            # create file
            cp $1 $TMP_DIR/$file 
            chmod +x $TMP_DIR/$file
        
        fi

        shift

    done

    # add files to package
    mkdir $TMP_DIR/data
    while [ $# -gt 0 ]; do

        newfile=$TMP_DIR/data/$1

        # make files directory path if nessesary
        dir=`dirname $newfile`
        if [ ! -d $dir ]; then
            mkdir -p $dir
        fi

        cp -r $1 $newfile
        
        shift

    done
    
    cd $TMP_DIR
    
    # create digest with each files sha1 hash
    sha1sum -b `find . -type f -name '*'` > $TMP_DIR/digest

    # create the package
    tar c * | gzip -c9 > $package
    
    cd - > /dev/null

    # remove temporary files and directory
    rm -r $TMP_DIR

}

# check that there is enough arguments
if [ $# -lt 2 ]; then
    usage
fi

# create the given package
create "$@"

# $Log$

# EOF

