
PACKAGE=test.spm

.PHONY: all clean install update uninstall

all: $(PACKAGE) install update uninstall

clean:
	rm $(PACKAGE)

$(PACKAGE):
	spm-create $(PACKAGE) test.sh test.sh test.sh LICENSE files

install:
	spm-accept | spm-install $(PACKAGE) arg1 arg2 arg3

update:
	spm-accept | spm-update $(PACKAGE) arg1 arg2 arg3

uninstall:
	spm-uninstall $(PACKAGE) arg1 arg2 arg3

# EOF
