source = Server.java ServerThread.java

noinst_JAVA = $(source)
# java sources are not distributed by default with my automake version
EXTRA_DIST = $(source)

# todo
classes = $(foreach a,$(source),$(subst /,.,$(a:.java=)))

# include after classes is set
include $(top_srcdir)/Common.mk

jar_DATA = server.jar

MOSTLYCLEANFILES = server.jar

