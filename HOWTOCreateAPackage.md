# How to Create a Package using the Simple Package Manager #

First you need to install the Simple Package Manager on your system before you start. If you need this please read the HOWTOInstall wiki page.

## Step 1 - Create the package scripts ##

After you install spm, you need to create three scripts and one regular file. The scripts are as follows:
  * `build` - this script builds the software (i.e. compilation and linking the executable).
  * `install` - this script actually installs the compiled software onto the system (root privileges can be assumed).
  * `uninstall` - this script uninstalls the software from the system.
Note that the script can be called any thing and don't have to actually be a script (e.i. they can be an executable binary), but they will be referred to by the names given above.

### Build Script Example ###

```
#!/bin/bash

./configure
make

# EOF

```

Note that this is the first script executed when the spm-install command is called. As such dependencies should be checked by this script.

### Install Script Example ###

```
#!/bin/bash

make install

# EOF

```

### Uninstall Script Example ###

```
#!/bin/bash

files=<installed files>

for file in $files; do
    yes | rm $file
done

# EOF

```

## Step 2 - Create the license file ##

The license file simply contains the terms and conditions for copying the software. This should be a plain text document. Have a look [here](http://en.wikipedia.org/wiki/List_of_FSF_approved_software_licenses) for a list of some free / open source software licenses.

## Step 3 - Create the package ##

A package can simply be created once all of the aforementioned files have been created. the command syntax is as follows:

```
spm-create <name of the package file> <install script> <uninstall script>
           <build script> <license file> [Package files or directories ...]
```

For example:

```
spm-create test-0.1.1.spm install.sh uninstall.sh build.sh license test-project
```

Would create a package called `test.0.1.1.spm` that contained the given scripts and license and ALL the files and sub-directories (fully recursive) in the `test-project` directory.

# Further Information #

More information about each commands usage syntax can be found in the README file in the source tree. This can be accessed by clicking the linked named source at the top of the page.

