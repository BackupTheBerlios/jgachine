source = Pong.java

# more than one class in one java file
# => list the classes manually 
classes = \
	Ball \
	BallSprite \
	BluredBallSprite \
	Pong \
	RacketMover \
	ShadowedSprite


noinst_JAVA = $(source)
# java sources are not distributed by default with my automake version
EXTRA_DIST = $(source)

# include after classes is set
include $(top_srcdir)/Common.mk

jar_DATA = pong.jar

MOSTLYCLEANFILES = pong.jar
