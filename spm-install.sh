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
. $(dirname $0)/spm-common.sh

# Prints usage information.
usage() {
    echo "
Usage: $pname <package file ...>
" >&2
    exit 0
}

# Prompts user to view and accepts the terms of the packages license.
handleLicense() {
   
    local license

    # get license file from archive
    license=$TMP_DIR/license

    if [ -n "`cat $license`" ]; then

        ask "Do you want to view the license terms?"
        if istrue $?; then
            less "$license"
        fi

        ask "Do you accept the license terms?"
        if isfalse $?; then
            error "License terms must be accepted to install package!"
            exit 1
        fi

    fi

}

# ensure that there is enough arguments
[ $# -lt 1 ] && usage

# install each package
for arg; do

    checkRegFile $arg || exit 1
    openPackage $arg && exit 1

    handleLicense
    
    runPackage build install
    if istrue $?; then
        echo "Successfully installed!" > /dev/tty
    else
        error "Installation failed!" > /dev/tty
        exit 1
    fi

    closePackage

done

# EOF

