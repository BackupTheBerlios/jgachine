#include "../input.h"

#include <SDL.h>
#include <cassert>
#include <vector>

// all input devices
static std::vector<Input::DevState> *devState=NULL;
// map joystick number to input device number
static std::vector<int> *joyDevMap=NULL;
// map keyboard number to input device number
int keyDevMap[2]={0,1};
// keyboard definition
typedef SDLKey Keys[4];
Keys keys[2]={
  {SDLK_KP8, SDLK_KP2, SDLK_KP4, SDLK_KP6},
  {SDLK_w, SDLK_s, SDLK_a, SDLK_d}
};

void
Input::init()
{
  assert(!devState);
  devState=new std::vector<Input::DevState>();
  assert(!joyDevMap);
  int numj=SDL_NumJoysticks();
  joyDevMap=new std::vector<int>(numj);
  
  for (int i=0;i<numj;++i) {
    (*joyDevMap)[i]=-1;
    SDL_Joystick* joy=SDL_JoystickOpen(i);
    if (!joy) continue;
    if (SDL_JoystickNumAxes(joy)<2) {
      SDL_JoystickClose(joy);
      continue;
    }
    // found usable joy
    DevState s;
    s.devno=devState->size();
    devState->push_back(s);
    (*joyDevMap)[i]=s.devno;
  }
  // add 2 keyboard devices
  DevState s;
  s.devno=keyDevMap[0]=devState->size();
  devState->push_back(s);
  s.devno=keyDevMap[1]=devState->size();
  devState->push_back(s);
}

void
Input::charInput(bool active)
{
}

unsigned
Input::attachedDevices()
{
  if (!devState) return 0;
  return devState->size();
}

static
void toggleGrab()
{
  SDL_GrabMode mode;

  //  printf("Ctrl-G: toggling input grab!\n");
  mode = SDL_WM_GrabInput(SDL_GRAB_QUERY);
  if ((mode!=SDL_GRAB_OFF)&&(mode!=SDL_GRAB_ON))
    return;
  /*
    if ( mode == SDL_GRAB_ON ) {
    printf("Grab was on\n");
    } else {
    printf("Grab was off\n");
    }
  */
  mode = SDL_WM_GrabInput((mode==SDL_GRAB_ON)?SDL_GRAB_OFF:SDL_GRAB_ON);
  /*
    if ( mode == SDL_GRAB_ON ) {
    printf("Grab is now on\n");
    } else {
    printf("Grab is now off\n");
    }
  */
}


static
bool
handleKey(SDL_KeyboardEvent &e)
{
  bool pressed=e.state==SDL_PRESSED;
  if (!pressed) return false;
  switch (e.keysym.sym){
  case SDLK_g:
    if (e.keysym.mod & KMOD_CTRL) {
      toggleGrab();
      return true;
    }
    break;
  case SDLK_z:    
    if (e.keysym.mod & KMOD_CTRL) {
      Input::iconifyHandler();
      return true;
    }
    break;
  case SDLK_ESCAPE:
    Input::quitHandler();
    return true;
  case SDLK_RETURN:
    if (e.keysym.mod & KMOD_ALT){
      Input::toggleFullscreenHandler();
      return true;
    }
    break;
  }
  return false;
}

//! \return true if it was a device key (the key was handled)  
static
bool
handleDevKey(SDL_KeyboardEvent &e, int keyDev)
{
  // map keyboard dev no to real dev no
  assert((keyDev>=0)&&(keyDev<2));
  int dev=keyDevMap[keyDev];
  assert(devState);
  assert((dev>=0)&&(dev<(int)devState->size()));
  
  Input::DevState &m_state((*devState)[dev]);
  Input::DevState old(m_state);
  bool pressed=e.state==SDL_PRESSED;
  SDLKey k(e.keysym.sym);
  if (k==keys[keyDev][0]) {
    // up
    if (pressed) m_state.y=1;
    else if (m_state.y==1) m_state.y=0;
  }else if (k==keys[keyDev][1]) {
    // down
    if (pressed) m_state.y=-1;
    else if (m_state.y==-1) m_state.y=0;
  }else if (k==keys[keyDev][2]) {
    // left 
    if (pressed) m_state.x=-1;
    else if (m_state.x==-1) m_state.x=0;
  }else if (k==keys[keyDev][3]) {
    // right
    if (pressed) m_state.x=1;
    else if (m_state.x==1) m_state.x=0;
  }
  if (m_state!=old) {
    Input::devStateHandler(m_state);
    return true;
  }
  return false;
}

static
signed char
joyScale(Sint16 v) 
{
  Sint16 clearance=20000;
  if (v>clearance) return 1;
  if (v<-clearance) return -1;
  return 0;
}

static
void
handleJoyMotion(const SDL_JoyAxisEvent &event)
{
  int joy=event.which;
  assert(joyDevMap);
  if ((joy<0)||(joy>=(int)joyDevMap->size())) return;
  
  int dev=(*joyDevMap)[joy];
  assert(devState);
  if ((dev<0)||(dev>=(int)devState->size())) return;
  
  Input::DevState &m_state((*devState)[dev]);
  Input::DevState old(m_state);

  switch (event.axis) {
  case 0: 
    m_state.x=joyScale(event.value);
    break;
  case 1:
    m_state.y=joyScale(-event.value);
    break;
  }

  if (m_state!=old)
    Input::devStateHandler(m_state);
}

inline
static
void handleResize(const SDL_ResizeEvent &e)
{
  Input::resizeHandler(e.w,e.h);
}


void 
Input::poll()
{
  SDL_Event event;
  while ( SDL_PollEvent(&event) ) {
    switch (event.type) {
    case SDL_QUIT:
      Input::quitHandler();
      break;
    case SDL_KEYDOWN:
    case SDL_KEYUP:
      if (!handleDevKey(event.key,0))
	if (!handleDevKey(event.key,1))
	  handleKey(event.key);
      break;
    case SDL_MOUSEMOTION:
      //      mouseMotion.emit(event.motion);
      break;
    case SDL_MOUSEBUTTONDOWN:
    case SDL_MOUSEBUTTONUP:
      //      mouseButton.emit(event.button);
      break;
    case SDL_JOYAXISMOTION:
      handleJoyMotion(event.jaxis);
      break;
    case SDL_JOYBUTTONDOWN:
    case SDL_JOYBUTTONUP:
      //      joyButton.emit(event.jbutton);
      break;
    case SDL_VIDEORESIZE:
      handleResize(event.resize);
      break;
    default:
      break;
    }
  }
}

void
Input::deinit()
{
  if (devState) {
    delete devState;
    devState=NULL;
  }
  if (joyDevMap) {
    delete joyDevMap;
    joyDevMap=NULL;
  }
  // todo: close joysticks
}

