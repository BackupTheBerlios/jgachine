all: Example subdirs

SUBDIRS = 

include $(srcdir)/Common.mk

javasrc = $(wildcard *.java)
java_class = $(javasrc:.java=.class)

.PHONY: Example

Example: $(java_class)


clean: clean-common

