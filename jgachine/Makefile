all: subdirs

srcdir ?= $(CURDIR)
export srcdir

CXXFLAGS+=-I$(srcdir) -I$(srcdir)/vecmath
GCJFLAGS+=-I$(srcdir) -I$(srcdir)/vecmath
CLASSPATH+=$(srcdir):$(srcdir)/vecmath:

# we want all submakes to inherit those variables
export CXXFLAGS GCJFLAGS CLASSPATH

SUBDIRS = example server vecmath org/jgachine tutorial

include $(srcdir)/Common.mk

clean: clean-common



