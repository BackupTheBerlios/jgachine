source = \
	Server.java ServerThread.java \
	Pong.java Client.java ClientState.java \
	Transform.java

noinst_JAVA = $(source)
# java sources are not distributed by default with my automake version
EXTRA_DIST = $(source)

classes = $(foreach a,$(source),$(subst /,.,$(a:.java=))) \
	Ball \
	BallSprite \
	RacketMover \
	RacketSprite

# include after classes is set
include $(top_srcdir)/Common.mk

jar_DATA = netpong.jar

MOSTLYCLEANFILES = netpong.jar

