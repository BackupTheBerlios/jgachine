javasrc = $(wildcard javax/vecmath/*.java)
created_h = $(javasrc:.java=.h)
created_o = $(javasrc:.java=.o)
created_class = $(javasrc:.java=.class)

all: $(created_class) vecmath.a $(created_h)

include $(srcdir)/Common.mk

vecmath.a: $(created_o)
	$(AR) cru $@ $(created_o)
	$(RANLIB) $@

clean: clean-common
	rm -f javax/vecmath/*.o javax/vecmath/*.class javax/vecmath/*.h


