#include "sdlgl.h"

#include <SDL.h>
#include <iostream>
#include <iomanip>
#include <stdexcept>

int
printGLErrors()
{
  int res=0;
  int e;
  while ((e=glGetError())) {
    JGACHINE_MSG("GL Error: ", e);
    std::ios::fmtflags old=std::cerr.flags();
    std::cerr << "GL Error: " << e << "=0x" << std::hex << e << std::endl;
    std::cerr.flags(old);
    ++res;
    if (res>5) {
      JGACHINE_WARN("stop printing errors because there are too much (parhaps some other problem)");
      return res;
    }
  }
  return res;
}


#ifdef DLOPEN_OPENGL

// define function prototypes
#define FUNC(ret,name,parm) ret (* name ) parm=0;
#include "glfunctions.h"
#undef FUNC

//! load opengl library at runtime
/*!
  we use SDL to do this
*/
void
loadGL(const std::string &libGL)
{
  DEBUG_GL("dlopening opengl");
  // load the lib before we call setvidmode
  const char *lib=NULL;
  if (!libGL.empty()) lib=libGL.c_str();
  if (SDL_GL_LoadLibrary(lib)==-1) {
    DEBUG_GL("failed to load gl library");
    JGACHINE_THROW(std::string("Could not load OpenGL lib: \"")+libGL+"\": "+SDL_GetError()+". Try the --libGL switch".c_str());
  }
}

/* GLU hack since SDL does not get glu symbols
   \todo either remove this completely and try to find a solution within SDL
   or at least add a deinit - perhaps use libltdl
   
   \note this will have portability problems for sure
*/
#include <dlfcn.h>

static
void* gluHandle=NULL;

void
loadGLU(const std::string &libGLU)
{
  JGACHINE_CHECK(!gluHandle);
  std::string lib(libGLU);
  if (lib.empty()) lib="libGLU.so.1";
  gluHandle = dlopen (lib.c_str(), RTLD_LAZY);
  if (!gluHandle) {
    DEBUG_GL("failed to load glu library");
    JGACHINE_THROW(std::string("Could not load GLU lib: \"")+lib+"\". Try the --libGLU switch".c_str());
  }
}

static 
_GLUfuncptr
GetGluProcAddress(char *symbol)
{
  JGACHINE_ASSERT(gluHandle);
  return (_GLUfuncptr)dlsym(gluHandle,symbol);
}
// end GLU hack


void
lookupGLSymbols()
{
#define STRINGIFY(m) #m
  DEBUG_GL("looking up GL and GLU symbols");
#define FUNC(ret,name,parm) do{typedef ret (*T##name) parm ;name=(T##name)SDL_GL_GetProcAddress(STRINGIFY(name));if(!name) name=(T##name)GetGluProcAddress(STRINGIFY(name));JGACHINE_ASSERT(name);DEBUG_GL("got address of:" STRINGIFY(name) " it is at: " << ((void *)name));}while(0)
#include "glfunctions.h"
#undef FUNC
#undef STRINGIFY
}

void
deinitGLSymbols()
{
#define STRINGIFY(m) #m
  DEBUG_GL("deinit GL and GLU symbols");
#define FUNC(ret,name,parm) do{typedef ret (*T##name) parm ;name=(T##name)NULL;}while(0)
#include "glfunctions.h"
#undef FUNC
#undef STRINGIFY
  if (gluHandle) {
    if (dlclose(gluHandle)) {
      JGACHINE_WARN("GLU unload failed");
    }else{
      gluHandle=NULL;
    }
  }
}


#endif /* DLOPEN_OPENGL */

