/*
 * Copyright (C) 2002 Jens Thiele <karme@berlios.de>
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */

/*!
   \file sdlgl.h
   \brief all files using OpenGL should include this file and never the OpenGL headers
   \author Jens Thiele
*/

#ifndef SDLGL_H
#define SDLGL_H

#include <errno.h>
#include <iostream>

#define DOPE_ERRORSTR "errno="<<errno<<":"<<strerror(errno)
#define DOPE_FUNCTIONNAME __PRETTY_FUNCTION__
#define DOPE_HERE __FILE__ << ":" << __LINE__ << ":" << DOPE_FUNCTIONNAME
#define DOPE_MSG(dopeLevelP, dopeMsgP) do{std::cerr << dopeLevelP << DOPE_HERE << ": " << dopeMsgP << "(" << DOPE_ERRORSTR << ")\n";}while(0)

#define DOPE_WARN(msg) DOPE_MSG("WARNING: ",msg)

inline int fatal(const char *file,int line,const char *func, const char *msg) 
{
  std::cerr << "FATAL: " << file << ":" << line << ":" << func << ": " << msg << "(" << DOPE_ERRORSTR <<")\n";
  std::terminate();
  return 0;
}

/*
  \def DOPE_STR(s)
  \brief DOPE_STR converts the macro argument to a string constant
*/
#define DOPE_STR(s) #s

/*
  \def DOPE_CHECK(expr)
  \brief DOPE_CHECK is like assert but takes also effect when NDEBUG is defined

  it seems there is no assert which is not disabled by a \#define NDEBUG 

*/
#define DOPE_CHECK(expr) (static_cast<void> ((expr) ? 0 : fatal(__FILE__, __LINE__, DOPE_FUNCTIONNAME, "assertion failed: " DOPE_STR(expr))))
//#define DOPE_CHECK(expr) (static_cast<void> (0))


/*!
  \def DOPE_FATAL(msg)
  \brief fatal error - exit with a short message
*/
#define DOPE_FATAL(msg) do{DOPE_MSG("FATAL: ",msg);std::terminate();}while(0)


#if defined(WINDOOF)
// define NOMINMAX - otherwise min and max are defined as macros !
#define NOMINMAX
#include <windows.h>
#undef NOMINMAX
#endif

#ifdef DLOPEN_OPENGL

#define FUNC(ret,name,parm) extern ret (* name ) parm
#include "minigl.h"
#include "glfunctions.h"
#undef FUNC

#else
#include <GL/gl.h>
#include <GL/glu.h>
#endif

#define DEBUG_GL(msg)
//#define DEBUG_GL(msg) DOPE_MSG("DEBUG: OpenGL: ", msg)

#define GL_ERRORS() do{if (printGLErrors()) std::cout << "gl error at - see above";}while(0)

//! print OpenGL errors
/*
  \return the number of errors occured
*/
int
printGLErrors();


#ifdef DLOPEN_OPENGL
//! load OpenGL library at runtime
void
loadGL(const std::string &libGL);
//! load GLU library at runtime
void
loadGLU(const std::string &libGLU);
//! lookup GL and GLU symbols
void
lookupGLSymbols();
//! deinit GL and GLU symbols
/*
  sets all function pointers to zero
  for easier debugging
*/
void
deinitGLSymbols();
#endif

#endif
