all: subdirs

srcdir ?= $(CURDIR)
export srcdir

CXXFLAGS+=-I$(srcdir) -I$(srcdir)/vecmath
GCJFLAGS+=-I$(srcdir) -I$(srcdir)/vecmath
CLASSPATH+=$(srcdir):$(srcdir)/vecmath:

# we want all submakes to inherit those variables
export CXXFLAGS GCJFLAGS CLASSPATH


CLASSPATH_ENV = CLASSPATH=$(top_srcdir):$(top_srcdir)/vecmath


SUBDIRS = example server vecmath org/jgachine tutorial

