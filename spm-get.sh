#!/bin/bash
# spm-get - Manages remote packages.
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

# directory packages are downloaded to
GET_DIR=~/.spm

# Prints usage information.
usage() {
    
    echo "
Usage: $pname <mode> <package url ...>

Mode:
    get       - only download package (to $GET_DIR).
    install   - install the package.
    uninstall - un-install the package.
    update    - update an existing package.
    list      - list all of the downloaded packages
    delete    - delete the given package file (not un-install).
    purge     - delete all downloaded packages (in $GET_DIR).
" >&2

    true ; exit

}

# Returns the name of the package for the given URL
# getPackageName <package url>
getPackageName() {
    echo "${1##*/}"
}

# Gets the given package from the given URL, if it has not already been 
# downloaded
# get <package url>
get() {

    local package
    
    package=`getPackageName "$1"`
    if [ ! -e "$GET_DIR/$package" ]; then

        ask "Do you want to download the package $package?"
        if isTrue $?; then

            curl -o "$GET_DIR/$package" "$1"
            if isFalse $?; then
                error "Cannot get package $package!"
                false ; exit
            fi

        fi

    fi

}

# ensure GET_DIR directory exists
if [ -e "$GET_DIR" ]; then
    if [ ! -d "$GET_DIR" ]; then
        error "$GET_DIR must be a directory!"
        false ; exit
    fi
else
    mkdir "$GET_DIR"
fi

# ensure that there is enough arguments
[ $# -lt 1 ] && usage

# get mode and package name
mode="$1"
shift
package=`getPackageName "$1"`

# handle 
case $mode in

    # only download package
    get)
   
        [ $# -lt 1 ] && usage

        get "$1"

        # check package for errors
        openPackage "$GET_DIR/$package" && { false ; exit ; }
        closePackage

        ;;

    # download and install package
    install)

        [ $# -lt 1 ] && usage

        get "$1"
        spm-install "$GET_DIR/$package"

        ;;

    # download and un-install package
    uninstall)

        [ $# -lt 1 ] && usage

        get "$1"
        spm-uninstall "$GET_DIR/$package"

        ;;

    # download and update package
    update)

        [ $# -lt 1 ] && usage

        get "$1"
        spm-update "$GET_DIR/$package"

        ;;

	# list all of the downloaded packages
	list)
	
		[ $# -gt 0 ] && usage
		
		packages=`ls "$GET_DIR"`
		
		if [ -z "$packages" ]; then
			echo "There are no packages!"
		else
			echo "$packages"
		fi
	
		;;

    # delete the given package
    delete)

        [ $# -lt 1 ] && usage

        if [ -e "$package" ]; then
            
            ask "Are you sure you want to delete \"$package\"?"
            if isTrue $?; then
                rm "$GET_DIR/$package"
            fi

        else
            error "No such package $package!"
        fi

        ;;

    # delete all downloaded packages
    purge)

        [ $# -gt 0 ] && usage

        ask "Are you sure you want to delete all downloaded packages?"
        if isTrue $?; then
            rm "$GET_DIR"/*
        fi

        ;;

    # unknown mode
    *)

        error "Unknown mode $mode"
        false ; exit

        ;;

esac

# handle remaining command line arguments
shift
if [ $# -ge 1 ]; then
    exec $mode "$2"
    exit
else
    true ; exit
fi

# EOF
