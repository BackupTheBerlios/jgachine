CXX ?= c++
GCJ ?= gcj
GCJH ?= gcjh
JAVAC ?= javac

export srcdir

.PHONY: subdirs $(SUBDIRS)

subdirs: $(SUBDIRS)

$(SUBDIRS):
	$(MAKE) -C $@

# compile java to native
CJN = $(GCJ) $(CXXFLAGS) -c
# compile java to class (bytecode)
CJC = $(GCJ) -C $(CXXFLAGS)
# link "mixed" executable
LINK = $(GCJ) $(LDFLAGS)

# create archive
AR ?= ar cru

# ranlib
RANLIB ?= ranlib


%.o : %.java
	$(GCJ) -c $(CXXFLAGS) $< -o $@

%.class : %.java
	$(JAVAC) $(JAVACFLAGS) -classpath $(CLASSPATH) $<

%.h: %.class
	$(GCJH) $(GCJHFLAGS) $(<:.class=) -o $@

clean-common:
	rm -f *.o *.a *.so *.lo *.la *.class *~ gmon.out
	for DIR in $(SUBDIRS);do $(MAKE) -C $$DIR clean; done
