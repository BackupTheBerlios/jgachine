all: subdirs

srcdir ?= $(CURDIR)
export srcdir

CXXFLAGS+=-I$(srcdir) -I$(srcdir)/vecmath
CLASSPATH+=$(srcdir):$(srcdir)/vecmath:

export CLASSPATH

SUBDIRS = example server vecmath org/jgachine tutorial

include $(srcdir)/Common.mk

clean: clean-common



