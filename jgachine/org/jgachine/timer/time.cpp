#include "time.h"
#include <errno.h> // errno
#include <sys/time.h> // timeval
#include <unistd.h>

#if defined(__WIN32__) || defined(WIN32)

// need timeval

#define __USE_W32_SOCKETS
#include <windows.h>
#define WINDOOF 1

static int initialized=0;

#endif



#ifndef WINDOOF
// we assume non-win platforms all have gettimeofday
#define HAVE_GETTIMEOFDAY 1
#endif

void
Timer::init()
{
#ifdef WINDOOF
  if (initialized++)
    return;
  timeBeginPeriod(1);
#endif
}

void
Timer::deinit()
{
#ifdef WINDOOF
  if (initialized<=0) return;
  
  if (--initialized)
    return;
  timeEndPeriod(1);
#endif
}


int64_t
Timer::getTimeStamp()
{
#if defined(HAVE_GETTIMEOFDAY)
  timeval s;
  gettimeofday(&s,NULL);
  return int64_t(s.tv_sec)*1000000LL+s.tv_usec;
#else
#if defined (__WIN32__) || defined(WIN32)
  unsigned long n=timeGetTime();
  return int64_t(n)*1000LL;
#else
#error "you must implement this"
#endif
#endif

}

void
Timer::uSleep(int64_t usec)
{
  if (usec<=0) return;
#ifndef WINDOOF
  fd_set z;
  FD_ZERO(&z);
  timeval s;
  s.tv_sec=usec/1000000LL;
  s.tv_usec=usec%1000000LL;
  // todo: 
  // could be interrupted by a signal !!
  // on linux we could use the timeout paramter if select was interrupted by a signal
  // but this would be non-std
  // the error checks are because i once had a strange problem
  int ret;
  if (((ret=select(1,&z,&z,&z,&s))==-1)&&(errno!=EINTR)) {
    // todo: we are in trouble
    return;
  }
#else
  unsigned msec=usec/1000;
  Sleep(msec);
#endif  
}

