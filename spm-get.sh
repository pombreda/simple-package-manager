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
Usage: $pname <mode> [mode options ...]

Mode:

    get - only download package (to $GET_DIR).
        $pname get <package url ...>

    install - install the package.
        $pname install <package url> [package options ...]

    install-all - install all of the given packages (without arguments)
        $pname install-all <package url ...>

    uninstall - un-install the package.
        $pname uninstall [package url] [package options ...]

    uninstall-all - uninstall all of the given packages (without arguments)
        $pname uninstall-all <package url ...>

    update - update an existing package.
        $pname update [package url] [package options ...]

    update-all - update all given packages (without arguments)
        $pname update-all <package url ...>

    list - list all of the downloaded packages.
        $pname list

    delete - delete the given package file (not un-install).
        $pname delete <package url ...>

    purge - delete all downloaded packages (in $GET_DIR).
        $pname purge

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

# handle mode
case "$mode" in

    # only download package
    get)

        [ $# -lt 1 ] && usage

        # download each package
        for arg; do

            package=`getPackageName "$arg"`

            get "$arg"

            # check package for errors
            openPackage "$GET_DIR/$package" && { false ; exit ; }
            closePackage

        done

        ;;

    # download and install package
    install)

        [ $# -lt 1 ] && usage

        get "$1"
        shift

        spm-install "$GET_DIR/$package" "$@"

        ;;

    # download and install each of the given packages
    install-all)

        [ $# -lt 1 ] && usage

        # download and install each package
        for arg; do

            package=`getPackageName "$arg"`

            get "$arg"
            spm-install "$GET_DIR/$package"

        done

        ;;

    # download and un-install package
    uninstall)

        [ $# -lt 1 ] && usage

        get "$1"
        shift

        spm-uninstall "$GET_DIR/$package" "$@"

        ;;

    # download and uninstall each of the given packages
    uninstall-all)

        [ $# -lt 1 ] && usage

        # download and uninstall each package
        for arg; do

            package=`getPackageName "$arg"`

            get "$arg"
            spm-uninstall "$GET_DIR/$package"

        done

        ;;

    # download and update package
    update)

        [ $# -lt 1 ] && usage

        get "$1"
        shift

        spm-update "$GET_DIR/$package" "$@"

        ;;

    # download and update each of the given packages
    update-all)

        [ $# -lt 1 ] && usage

        # download and update each package
        for arg; do

            package=`getPackageName "$arg"`

            get "$arg"
            spm-update "$GET_DIR/$package"

        done

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

    # delete the given packages
    delete)

        [ $# -lt 1 ] && usage

        # delete each package
        for arg; do

            package=`getPackageName "$arg"`

            # delete the package
            if [ -e "$package" ]; then

                ask "Are you sure you want to delete \"$package\"?"
                if isTrue $?; then
                    rm "$GET_DIR/$package"
                fi

            else
                error "No such package $package!"
            fi

        done

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
