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

# distribution archive name
DIST_NAME=spm-0.1.1-rev2

.PHONY: all clean spm-dist dist install

all: bin/spm-create bin/spm-get bin/spm-install bin/spm-uninstall bin/spm-update

clean:
	-yes | rm bin/*
	-yes | rm $(DIST_NAME).spm $(DIST_NAME).tar.gz

spm-dist: all install.sh uninstall.sh build.sh
	bin/spm-create $(DIST_NAME).spm install.sh uninstall.sh build.sh COPYING README spm-*.sh Makefile

dist: spm-dist
	mkdir $(DIST_NAME)
	mkdir $(DIST_NAME)/bin $(DIST_NAME)/doc
	cp Makefile COPYING README TODO *.sh $(DIST_NAME)
	cp doc/* $(DIST_NAME)/doc
	tar c $(DIST_NAME) | gzip -c9 > $(DIST_NAME).tar.gz
	rm -r $(DIST_NAME)

install: all spm-dist
	bin/spm-install $(DIST_NAME).spm

bin/spm-common.sh: spm-common.sh
	cp spm-common.sh bin/spm-common.sh
	chmod +x bin/spm-common.sh

bin/spm-create: spm-create.sh bin/spm-common.sh
	cp spm-create.sh bin/spm-create
	chmod +x bin/spm-create

bin/spm-get: spm-get.sh bin/spm-common.sh
	cp spm-get.sh bin/spm-get
	chmod +x bin/spm-get

bin/spm-install: spm-install.sh bin/spm-common.sh
	cp spm-install.sh bin/spm-install
	chmod +x bin/spm-install

bin/spm-uninstall: spm-uninstall.sh bin/spm-common.sh
	cp spm-uninstall.sh bin/spm-uninstall
	chmod +x bin/spm-uninstall

bin/spm-update: spm-update.sh bin/spm-common.sh
	cp spm-update.sh bin/spm-update
	chmod +x bin/spm-update

# EOF

