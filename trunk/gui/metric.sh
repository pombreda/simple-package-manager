#!/bin/bash
# metric.sh - displays some simple metrics about the project.

# getFiles <file extension> <directory>
getFiles() {

    for file in `ls "$2"`; do
        
        if [ -d "$2/$file" ]; then
            echo `getFiles $1 $2/$file`
        elif [ "${file##*.}" == "$1" ]; then
            echo "$2/$file"
        fi

    done

}

# write line count and number of bytes
wc -lc `getFiles java .`

# EOF

