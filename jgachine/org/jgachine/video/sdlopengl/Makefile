all: ../libvideo.a

include $(srcdir)/Common.mk

CXXFLAGS+=`sdl-config --cflags`

cppsrc = $(wildcard *.cpp)
obj = $(cppsrc:.cpp=.o)

../libvideo.a: $(obj) ../video.h
	$(AR) $@ $(obj)
	$(RANLIB) $@

clean: clean-common



