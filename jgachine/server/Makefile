all: Server subdirs

SUBDIRS = 

include $(srcdir)/Common.mk

javasrc = $(wildcard *.java)
java_class = $(javasrc:.java=.class)

.PHONY: Server

Server: $(java_class)

clean: clean-common


