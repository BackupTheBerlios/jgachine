include $(top_srcdir)/Common.mk

bin_SCRIPTS = jpong
CLEANFILES = $(bin_SCRIPTS)
EXTRA_DIST = jpong.in

# create script at compile time
# the autoconf manual suggests this

edit = sed \
	-e 's,@classdir\@,$(classdir),g' \
	-e 's,@datadir\@,$(pkgdatadir),g' \
	-e 's,@prefix\@,$(prefix),g'

jpong: Makefile $(srcdir)/jpong.in
	rm -f jpong jpong.tmp
	$(edit) $(srcdir)/jpong.in >jpong.tmp
	chmod +x jpong.tmp
	mv jpong.tmp jpong
