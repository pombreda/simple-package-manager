#!/bin/bash
# spm-install - installs a spm package.
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
Usage: $pname <package file> [package options ...]
" >&2
    true ; exit
}

# Prompts user to view and accepts the terms of the packages license.
handleLicense() {

    local license

    # get license file from archive
    license="$TMP_DIR/license"

    if [ -n "`cat "$license"`" ]; then

        ask "Do you want to view the license terms?"
        if isTrue $?; then
            less "$license"
        fi

        ask "Do you accept the license terms?"
        if isFalse $?; then
            error "License terms must be accepted to install package!"
            false ; exit
        fi

    fi

}

# ensure that there is enough arguments
[ $# -lt 1 ] && usage

# open the package
checkRegFile "$1" || { false ; exit ; }
openPackage "$1" || { false ; exit ; }
shift

handleLicense

# build the package
runPackage build "$@"
if isTrue $?; then
    echo "Package built successfully!" > /dev/tty
else
    error "Package build failed!"
    false ; exit
fi

# install the package
runPackage install "$@"
if isTrue $?; then
    echo "Package installed successfully!" > /dev/tty
else
    error "Package installation failed!"
    false ; exit
fi

closePackage

# EOF
