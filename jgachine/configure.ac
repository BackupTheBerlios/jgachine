dnl process this file with autoconf to produce a configure script
AC_PREREQ(2.53)
AC_INIT
AC_CONFIG_SRCDIR([org/jgachine/JGachineNative.cpp])


dnl
dnl Source packaging numbers
. ${srcdir}/VERSION

AC_DEFINE_UNQUOTED(PACKAGE_MAJOR_VERSION, $PACKAGE_MAJOR_VERSION, [major version])
AC_DEFINE_UNQUOTED(PACKAGE_MINOR_VERSION, $PACKAGE_MINOR_VERSION, [minor version])
AC_DEFINE_UNQUOTED(PACKAGE_MICRO_VERSION, $PACKAGE_MICRO_VERSION, [micro version])

dnl for automake
VERSION=$PACKAGE_VERSION
PACKAGE=jgachine

AC_CONFIG_AUX_DIR(config)
AC_CANONICAL_HOST
AC_CANONICAL_TARGET
AM_INIT_AUTOMAKE($PACKAGE, $VERSION)
AM_CONFIG_HEADER(config/config.h)
AC_PROG_MAKE_SET
dnl AC_PROG_LIBTOOL
AC_PROG_CXX
AM_PROG_GCJ
AC_PROG_RANLIB

dnl build options
AC_ARG_VAR(JGACHINE_PORT, the default port to use)

dnl checks for libraries

dnl Figure out which math and opengl library to use
dnl this is taken from SDL test directory
case "$target" in
    *-*-cygwin* | *-*-mingw32*)
	TIMELIB="-lwinmm"
        GL_LIB="-lopengl32"
        ;;
    *)
	TIMELIB=""
	GL_LIB="-lGL"
        ;;
esac

dnl check for sdl
AM_PATH_SDL(1.2.0, , [AC_MSG_ERROR([SDL required])])

dnl todo: check for SDL_image
SDL_LIBS="-lSDL_image $SDL_LIBS"

dnl Check for SDL OpenGL support
AC_MSG_CHECKING(for OpenGL support)
TMP=CFLAGS
CFLAGS="$CFLAGS $SDL_CFLAGS"
have_opengl=no
AC_COMPILE_IFELSE([AC_LANG_PROGRAM([[#include "SDL_opengl.h"
	]])],
	[have_opengl=yes],
	[AC_MSG_ERROR([not available])])
CFLAGS="$TMP"

AC_MSG_RESULT($have_opengl)

SDL_LIBS="$SDL_LIBS $GL_LIB $TIMELIB"

AC_CACHE_SAVE

dnl variable substitutions
if test "x$JGACHINE_PORT" = "x"; then
	JGACHINE_PORT="34444"
	echo JGACHINE_PORT is not set. I use default $JGACHINE_PORT
fi
AC_DEFINE_UNQUOTED(JGACHINE_PORT, $JGACHINE_PORT, [default port to use])

dnl output files
AC_CONFIG_FILES([
	Makefile
	config/Makefile
	scripts/Makefile
	doc/Makefile
	debian/Makefile
	data/Makefile
	tutorial/Makefile
	tutorial/demo/Makefile
	tutorial/pong1/Makefile
	tutorial/pong2/Makefile
	tutorial/server/Makefile
	org/Makefile
	org/jgachine/Makefile
	org/jgachine/timer/Makefile
	org/jgachine/input/Makefile
	org/jgachine/input/sdl/Makefile
	org/jgachine/video/Makefile
	org/jgachine/video/sdlopengl/Makefile
	org/jgachine/video/sdlopengl/boost/Makefile
	vecmath/Makefile
	])
AC_OUTPUT


