all: Class subdirs JGachine

SUBDIRS = video input timer

GCJHFLAGS+=-classpath $(CLASSPATH)
JPACKAGE = org/jgachine

include $(srcdir)/Common.mk

signalsrc = $(wildcard signal/*.java)
eventsrc = $(wildcard event/*.java)
javasrc = $(wildcard *.java scenegraph/*.java) $(signalsrc) $(eventsrc)
java_obj = $(javasrc:.java=.o)
java_class = \
	$(eventsrc:.java=.class) \
	$(signalsrc:.java=.class) \
	ResourceLoader.class LocalResourceLoader.class

.PHONY: Class

Class: $(java_class)

# we have to overwrite our default rule because we are
# in the "wrong" directory
%.h: %.class
	$(GCJH) $(GCJHFLAGS) org/jgachine/$(<:.class=) -o $@


created_h = \
	$(eventsrc:.java=.h) \
	$(signalsrc:.java=.h) \
	Color.h JGachine.h

JGachine:  \
	$(java_obj) \
	JGachineNative.o \
	video/libvideo.a input/libinput.a timer/libtimer.a \
	$(srcdir)/vecmath/vecmath.a
	$(LINK) -o $@ --main=org.jgachine.JGachine $^ $(LIBS)


JGachineNative.o:  $(created_h) JGachineNative.cpp

#	$(CXXO) -o $@ JGachineNative.cpp

clean: clean-common
	rm -f $(created_h) $(java_obj) $(java_class) JGachine


