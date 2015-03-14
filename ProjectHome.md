# Simple Package Manager #

Simple Package Manager (SPM), as the name suggests is a simple package manager.
It is intended to operate in similar way to existing package managers (e.g apt,
dpkg, Pacman, etc). Instead of supporting a centralised package repository, SPM
allows remote packages to be downloaded and installed automatically given thier
URL (Universal Resource Locator). This allows a developer to package and
distribute their software via a package management system without having to
worry about the many different formats and Linux distrbution repositories.

## How to Install ##

First you will need to download and extract the current [source](http://simple-package-manager.googlecode.com/files/spm-0.1.1.tar.gz) archive, or checkout the current source via subversion.

In order to install this software you must have root privilege access. This might
mean that you have to get an administrator to install it for you. The software
can be built and install simply by using the command:
```
su
make install
```
alternativley you can use sudo if you have it configured correctly:
```
sudo make install
```

## What Next? ##

You can check out the [repository of packages](http://code.google.com/p/spm-package-repository/wiki/PackageList) currently available for SPM (It is quite limited at the moment but you can help by [expanding it](http://code.google.com/p/spm-package-repository/wiki/PackageList#Submit_a_package)). You can also [start packaging](HOWTOCreateAPackage.md) and distributing your own software or other project using SPM.

<table cellpadding='10' border='0' cellspacing='10' width='320'>
<tr>
<td>
<blockquote><wiki:gadget url="http://www.ohloh.net/p/587302/widgets/project_partner_badge.xml" height="53" border="0"/><br>
</td>
<td>
<wiki:gadget url="http://www.ohloh.net/p/587302/widgets/project_users_logo.xml" height="43" border="0"/><br>
</td>
<td>
<br>
</td>
</tr>
</table>