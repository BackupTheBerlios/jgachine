CXX ?= c++
GCJ ?= gcj
GCJH ?= gcjh
JAVAC ?= javac

export srcdir

.PHONY: subdirs $(SUBDIRS)

subdirs: $(SUBDIRS)

$(SUBDIRS):
	$(MAKE) -C $@

LDFLAGS ?=
# todo detect this automatically
LIBGL ?= -lGL
# todo this is only correct for the sdl backend
LIBS ?= -lstdc++ -lSDL_image `sdl-config --libs` $(LIBGL)

# link "mixed" executable
LINK = $(GCJ) $(LDFLAGS)

# create archive
AR ?= ar

# ranlib
RANLIB ?= ranlib


%.o : %.java
	$(GCJ) -c $(GCJFLAGS) $< -o $@

%.class : %.java
	$(JAVAC) $(JAVACFLAGS) -classpath $(CLASSPATH) $<

%.h: %.class
	$(GCJH) $(GCJHFLAGS) $(<:.class=) -o $@

clean-common:
	rm -f *.o *.a *.so *.lo *.la *.class *~ gmon.out
	for DIR in $(SUBDIRS);do $(MAKE) -C $$DIR clean; done
