#class path settings
# for javac
MYCLASSPATH = $(top_srcdir):$(top_srcdir)/vecmath
CLASSPATH_ENV = CLASSPATH=$(MYCLASSPATH)
# for gcj
AM_GCJFLAGS = --classpath=$(MYCLASSPATH)

# where to install classfiles
classdir = $(pkgdatadir)/java

# todo use automake detected setting
JAVAC ?= javac
JAR ?= jar

%.class : %.java
	$(JAVAC) $(JAVACFLAGS) -classpath $(MYCLASSPATH) -d $(JAVAROOT) $<

# todo detect in configure.ac
GCJH ?= gcjh
# todo += produced warning but we want to respect the user setting - do this in configure.ac
GCJHFLAGS=--classpath=$(top_builddir):$(top_builddir)/vecmath

%.h: %.class
	$(GCJH) $(GCJHFLAGS) $(<:.class=) -o $@

# generate jaropt from classes variable
jpkg = $(subst /,.,$(subdir))
classfiles = $(foreach a,$(classes),$(subst .,/,$(jpkg).$(a)).class)
jaropt = $(foreach a,$(classfiles),-C $(top_builddir) $(a))

# todo
%.jar: classnoinst.stamp
	$(JAR) cvf $@ $(jaropt)

jardir = $(classdir)
