all: libtimer.a

include $(srcdir)/Common.mk

cppsrc = $(wildcard *.cpp)
obj = $(cppsrc:.cpp=.o)

libtimer.a: $(obj) time.h
	$(AR) $@ $(obj)
	$(RANLIB) $@

clean: clean-common
