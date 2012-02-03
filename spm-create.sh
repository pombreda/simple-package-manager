#!/bin/bash
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
. "`dirname $0`/spm-common.sh"

# Prints usage information.
usage() {

    echo "
Usage: $pname <package name> <install script> <un-install script>
              <build script> <license file> [package file ...]
    " >&2

    true ; exit

}

# Create the specified package
# create <package name> <install script> <un-install script> <build script>
#        <license file> [files ...]
create() {

    # create temporary directory
    if [ -e "$TMP_DIR" ]; then rm -r "$TMP_DIR" ; fi
    mkdir "$TMP_DIR"

    # get package name
    package=`getAbsoluteName "$1"`
    if [ "${package##*.}" != "spm" ]; then
        package="${package}.spm"
    fi
    shift

    # add scripts and digest files
    for file in "install" "uninstall" "build" "license"; do

        if [ "$1" == "-" ]; then
            touch "$TMP_DIR/$file"
        else

            # check that given file exists
            checkRegFile "$1"
            if isFalse $?; then
                false ; exit
            fi

            # create file
            cp "$1" "$TMP_DIR/$file"

            # set permissions for file
            if [ "$file" == "license" ]; then
                # set read only permissions
                chmod 444 "$TMP_DIR/$file"
            else
                # set read and execute permissions
                chmod 555 "$TMP_DIR/$file"
            fi

        fi

        shift

    done

    # add files to package
    mkdir "$TMP_DIR/data"
    while [ $# -gt 0 ]; do

        newfile="$TMP_DIR/data/$1"

        # make files directory path if nessesary
        dir=`dirname "$newfile"`
        if [ ! -d "$dir" ]; then
            mkdir -p "$dir"
        fi

        cp -r "$1" "$newfile"

        shift

    done

    cd "$TMP_DIR"

    # create digest with each files sha1 hash
    echo -n "" > digest-tmp
    find -type f -exec sha1sum -b >> digest-tmp \{\} +
    cat digest-tmp | sed '/.*digest/ d' > digest # remove the digest files entry
    rm digest-tmp

    # create the package
    tar c * | gzip -c9 > "$package"

    cd - > /dev/null

    # remove temporary files and directory
    yes | rm -r "$TMP_DIR"

}

# check that there is enough arguments
[ $# -lt 2 ] && usage

# create the given package
create "$@"

# EOF

