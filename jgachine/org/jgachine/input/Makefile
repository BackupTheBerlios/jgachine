all: libinput.a

SUBDIRS = sdl

include $(srcdir)/Common.mk

CXXFLAGS+=`sdl-config --cflags`

libinput.a: sdl

clean: clean-common
