JAVAROOT = $(top_builddir)

SUBDIRS = video input timer

INCLUDES = -I $(top_builddir)/vecmath -I$(top_builddir)

bin_PROGRAMS = jgachine

javasrc = \
	Color.java \
	JGachine.java \
	LocalResourceLoader.java \
	MyClassLoader.java \
	MySecurityManager.java \
	NetResourceLoader.java \
	ResourceLoader.java \
	event/DevState.java \
	event/Resize.java \
	event/Resource.java \
	event/ResourceRequest.java \
	signal/Dispatcher.java \
	signal/FatalException.java \
	signal/Slot.java \
	scenegraph/AdjustColor.java \
	scenegraph/Camera.java \
	scenegraph/Node.java \
	scenegraph/Quad.java \
	scenegraph/Recolor.java \
	scenegraph/Rotate.java \
	scenegraph/Scale.java \
	scenegraph/Sprite.java \
	scenegraph/Text.java \
	scenegraph/Translate.java

created_class = $(javasrc:.java=.class)

# todo
classes = $(foreach a,$(javasrc),$(subst /,.,$(a:.java=)))

# include after classes is set
include $(top_srcdir)/Common.mk

%.h: %.class
	$(GCJH) $(GCJHFLAGS) org/jgachine/$(<:.class=) -o $@

created_h = $(javasrc:.java=.h)

# todo - this is a bad hack to get the headers created first
JGachineNative.o: $(created_h) JGachineNative.cpp

jgachine_SOURCES = \
	$(javasrc) \
	JGachineNative.cpp

libs = \
	input/sdl/libinput.a \
	timer/libtime.a \
	video/sdlopengl/libvideo.a \
	$(top_builddir)/vecmath/libvecmath.a

jgachine_LDADD = \
	$(libs) @SDL_LIBS@ -lstdc++ -lgcj

jgachine_LDFLAGS = --main=org.jgachine.JGachine

# todo
jgachine.jar: $(created_class)
	$(JAR) cvf $@ $(jaropt)

jar_DATA = jgachine.jar

MOSTLYCLEANFILES = jgachine.jar $(created_class) $(created_h)

