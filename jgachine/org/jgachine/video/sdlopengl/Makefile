all: ../libvideo.a

include $(srcdir)/Common.mk

CXXFLAGS+=`sdl-config --cflags` -I.

cppsrc = $(wildcard *.cpp)
obj = $(cppsrc:.cpp=.o)

../libvideo.a: $(obj) ../video.h
	$(AR) cru $@ $(obj)
	$(RANLIB) $@

clean: clean-common



