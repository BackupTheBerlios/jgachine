//! Timer subsystem Interface
/*!
  unit for the TimeStamp is usec: 10^-6 sec
*/

//todo - one central definition for 64bit int types
//perhaps autoconf again
typedef long long int64_t;

namespace Timer
{
  void init();
  void deinit();
  
  int64_t getTimeStamp();
  void uSleep(int64_t usec);
}
