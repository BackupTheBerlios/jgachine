all: ../libinput.a

include $(srcdir)/Common.mk

CXXFLAGS+=`sdl-config --cflags`

cppsrc = $(wildcard *.cpp)
obj = $(cppsrc:.cpp=.o)

../libinput.a: $(obj) ../input.h
	$(AR) $@ $(obj)
	$(RANLIB) $@

clean: clean-common



