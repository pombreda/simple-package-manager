#!/bin/bash
# spm-uninstall - uninstall's a spm package.
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

# ensure that there is enough arguments
[ $# -lt 1 ] && usage

# open the package
checkRegFile "$1" || { false ; exit ; }
openPackage "$1" || { false ; exit ; }
shift

# uninstall the program
runPackage uninstall "$@"
if isTrue $?; then
    echo "Package un-installed successfully!" > /dev/tty
else
    error "Package un-installation failed!"
    false ; exit
fi

closePackage

# EOF

