#ifndef JGACHINE_ERROR_H
#define JGACHINE_ERROR_H

#include <iostream>
#include <java/lang/RuntimeException.h>
#include <gcj/cni.h>

#define JGACHINE_ERRORSTR "errno="<<errno<<":"<<strerror(errno)
#define JGACHINE_FUNCTIONNAME __PRETTY_FUNCTION__
#define JGACHINE_HERE __FILE__ << ":" << __LINE__ << ":" << JGACHINE_FUNCTIONNAME
#define JGACHINE_MSG(jgachineLevelP, jgachineMsgP) do{std::cerr << jgachineLevelP << JGACHINE_HERE << ": " << jgachineMsgP << "(" << JGACHINE_ERRORSTR << ")\n";}while(0)
#define JGACHINE_WARN(msg) JGACHINE_MSG("WARNING: ",msg)

/*
  \def JGACHINE_STR(s)
  \brief JGACHINE_STR converts the macro argument to a string constant
*/
#define JGACHINE_STR(s) #s

/*
  \def JGACHINE_CHECK(expr)
  \brief JGACHINE_CHECK is like assert but takes also effect when NDEBUG is defined
  
  it seems there is no assert which is not disabled by a \#define NDEBUG 
*/
#define JGACHINE_CHECK(expr) (static_cast<void> ((expr) ? 0 : jgachine_fatal(__FILE__, __LINE__, JGACHINE_FUNCTIONNAME, "assertion failed: " JGACHINE_STR(expr))))

/*!
  \def JGACHINE_FATAL(msg)
  \brief fatal error - exit with a short message
*/
#define JGACHINE_FATAL(msg) do{jgachine_fatal(__FILE__, __LINE__, JGACHINE_FUNCTIONNAME, msg);}while(0)

inline int jgachine_fatal(const char *file,int line,const char *func, const char *msg) 
{
  std::cerr << "FATAL: " << file << ":" << line << ":" << func << ": " << msg << "(" << JGACHINE_ERRORSTR <<")\n";
  std::terminate();
  return 0;
}

//! throw an exception that could be caught by java code
#define JGACHINE_THROW(msg) throw new java::lang::RuntimeException(JvNewStringLatin1(msg))


#endif
