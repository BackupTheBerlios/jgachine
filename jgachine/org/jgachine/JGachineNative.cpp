#include "JGachine.h"
#include <string>
#include <iostream>
#include "video/video.h"
#include "input/input.h"
#include "timer/time.h"
#include "event/DevState.h"
#include "event/Resize.h"
#include "event/Resource.h"
#include "signal/Dispatcher.h"
#include "Color.h"
#include <gcj/cni.h>
#include <javax/vecmath/Vector2f.h>
#include <vector>
#include <cassert>

static bool inited=false;


static
inline
Video::Color 
toCppColor(::org::jgachine::Color *c)
{
  return Video::Color(c->r,c->g,c->b,c->a);
}

//! stack class with special assumption that X has a set() method which is called on pop
template <typename X>
class Stack
{
 public:
  void push(const X &x)
    {
      stack.push_back(x);
    }
  void pop()
    {
      if (stack.empty()) {
	// todo
	std::cerr << "pop called but stack is empty\n";
	return;
      }
      stack.back().set();
      stack.pop_back();
    }
 protected:
  std::vector<X> stack;
};

//! container for our global data
/*!
  \note we do not make our complex global data structures static
  to prevent problems with memory debuggers, initialization and the like
  this is why we added State - to have the new/delete only once
*/
struct State
{
  typedef Stack<Video::Color> ColorStack;
  ColorStack colorStack;
  typedef Stack<Video::Rectangle> ViewportStack;
  ViewportStack viewportStack;
  typedef Stack<Video::ViewportCoordinates> ViewportCoordinatesStack;
  ViewportCoordinatesStack viewportCoordinates;
};

static
State* state=NULL;

void
Input::quitHandler()
{
  org::jgachine::JGachine::quit=true;
}

void
Input::toggleFullscreenHandler()
{
  Video::toggleFullscreen();
}

void
Input::iconifyHandler()
{
  Video::iconify();
}

void
Input::devStateHandler(const Input::DevState& devState)
{
  if (org::jgachine::JGachine::quit) return;
  if (!org::jgachine::JGachine::input) return;
  // translate c++ object to java object
  org::jgachine::event::DevState* s=new org::jgachine::event::DevState();
  s->x=devState.x;
  s->y=devState.y;
  s->buttons=devState.buttons;
  s->devno=devState.devno;
  org::jgachine::JGachine::input->dispatch(s);
}

//! input layer calls this if we should resize (f.e. the input layer got a message from a window manager)
void
Input::resizeHandler(int sx, int sy)
{
  // resize neccessary? prevent possible endless loop where video subsystem generates input event
  // hmm still shit 
  // 
  // UPDATE: i implemented a workaround in sdl/input.cpp (NEED_RESIZE_HACK)
  // 
  // the (sdlopengl) video subsystem generates resize requests - this is a real problem with wm's like sawfish....
  // corresponding discussion on SDL ML: http://www.libsdl.org/pipermail/sdl/2003-July/thread.html#55073
  // Idea: perhaps sawfish does also react on application requested resizes and tries to resize
  // to the application requested size in small steps, too - hmm (this would explain the alternating resizes)
  // 1. test with different wm's
  // 2. take a look at the sawfish source
  // 3. how did I do it in TUD? / read the Xlib manual
  // 4. read the SDL source
  //  if ((sx==org::jgachine::JGachine::width)&&(sy==org::jgachine::JGachine::height))
  //    return;

  // tell the video layer to actually resize
  Video::resize(sx,sy);

  // now tell the jgachine applications that we resized
  org::jgachine::JGachine::width=sx;
  org::jgachine::JGachine::height=sy;
  org::jgachine::event::Resize* e=new org::jgachine::event::Resize(sx,sy);
  org::jgachine::JGachine::input->dispatch(e);
}

void
Input::charHandler(Unicode)
{
}

void
org::jgachine::JGachine::init()
{
  if (inited) return;
  inited=true;
  
  assert(!state);
  state=new State();

  // at the moment video must always be initialized first (this is caused by the sdlopengl video implementation)
  Video::init();
  Input::init();
}

void org::jgachine::JGachine::deinit()
{
  if (!inited) return;
  
  if (state) {
    delete state;
    state=NULL;
  }
  Input::deinit();
  Video::deinit();
}

void
org::jgachine::JGachine::drawLine(jfloat x1, jfloat y1, jfloat x2, jfloat y2)
{
  Video::drawLine(x1,y1,x2,y2);
}

void
org::jgachine::JGachine::drawLines(JArray<javax::vecmath::Vector2f*>* pts)
{
  //! \todo extend the video interface and throw exception on error
  assert(pts);
  assert(!(pts->length % 2));
  for (int i=0;i<pts->length;i+=2) {
    assert(elements(pts)[i]&&elements(pts)[i+1]);
    Video::drawLine(elements(pts)[i]->x,elements(pts)[i]->y,elements(pts)[i+1]->x,elements(pts)[i+1]->x);
  }
}

void
org::jgachine::JGachine::drawLineStrip(JArray<javax::vecmath::Vector2f*>* pts)
{
  //! \todo extend the video interface and throw exception on error
  assert(pts);
  for (int i=0;i<pts->length-1;++i) {
    assert(elements(pts)[i]&&elements(pts)[i+1]);
    Video::drawLine(elements(pts)[i]->x,elements(pts)[i]->y,elements(pts)[i+1]->x,elements(pts)[i+1]->x);
  }
}

void
org::jgachine::JGachine::drawQuad()
{
  Video::drawQuad();
}

void
org::jgachine::JGachine::swapBuffers()
{
  Video::swapBuffers();
}

jint
org::jgachine::JGachine::createTexture(org::jgachine::event::Resource *imageData)
{
  return Video::createTexture(unsigned(imageData->data->length),
			      (const char *)elements(imageData->data));
}

void
org::jgachine::JGachine::drawTexture (jint tid)
{
  Video::drawTexture(tid);
}

void
org::jgachine::JGachine::drawText (::java::lang::String *text, jboolean hcentered, jboolean vcentered)
{
  if (!text) return;
  // todo BUG: what about the encoding?
  JArray<jbyte>* ja=text->getBytes();
  const char *s=(char *)elements(ja);
  // s is not null terminated !
  unsigned slen=ja->length;
  if (!s) return;
  std::string cpps(s,slen);
  assert(cpps.size()==slen);
  Video::drawText(cpps,bool(hcentered),bool(vcentered));
}



void
org::jgachine::JGachine::pushMatrix ()
{
  Video::pushMatrix();
}

void
org::jgachine::JGachine::popMatrix ()
{
  Video::popMatrix();
}

void
org::jgachine::JGachine::translate (::javax::vecmath::Vector2f *v)
{
  Video::translate(float(v->x),float(v->y));
}

void
org::jgachine::JGachine::scale (::javax::vecmath::Vector2f *v)
{
  Video::scale(float(v->x),float(v->y));
}

void
org::jgachine::JGachine::rotate (jfloat r)
{
  Video::rotate(float(r));
}

void
org::jgachine::JGachine::clear ()
{
  Video::clear();
}

void
org::jgachine::JGachine::poll()
{
  Input::poll();
}

jint
org::jgachine::JGachine::numDevices()
{
  return jint(Input::attachedDevices());
}

void 
org::jgachine::JGachine::setColor (::org::jgachine::Color *c)
{
  toCppColor(c).set();
}

void 
org::jgachine::JGachine::adjustColor (::org::jgachine::Color *c)
{
  Video::getColor().adjust(toCppColor(c)).set();
}

::org::jgachine::Color*
org::jgachine::JGachine::getColor()
{
  Video::Color c(Video::getColor());
  return new ::org::jgachine::Color(c.r,c.g,c.b,c.a);
}

void 
org::jgachine::JGachine::pushColor ()
{
  assert(state);
  state->colorStack.push(Video::getColor());
}

void 
org::jgachine::JGachine::popColor ()
{
  assert(state);
  state->colorStack.pop();
}

jlong 
org::jgachine::JGachine::time ()
{
  return Timer::getTimeStamp();
}

void 
org::jgachine::JGachine::uSleep (jlong usec)
{
  Timer::uSleep(usec);
}

void
org::jgachine::JGachine::setViewport(jint x, jint y, jint sx, jint sy)
{
  Video::setViewport(Video::Rectangle(static_cast<int>(x),
				      static_cast<int>(y),
				      static_cast<int>(sx),
				      static_cast<int>(sy))
		     );
}


void 
org::jgachine::JGachine::setViewportCoordinates(jfloat left, jfloat right,
			    jfloat bottom, jfloat top,
			    jfloat near, jfloat far)
{
  Video::setViewportCoordinates(Video::ViewportCoordinates(left,right,
							   bottom,top,
							   near,far));
}

void 
org::jgachine::JGachine::pushViewportCoordinates()
{
  assert(state);
  state->viewportCoordinates.push(Video::getViewportCoordinates());
}

void 
org::jgachine::JGachine::popViewportCoordinates()
{
  assert(state);
  state->viewportCoordinates.pop();
}




void
org::jgachine::JGachine::adjustViewport(::javax::vecmath::Vector2f * bottomLeft, ::javax::vecmath::Vector2f * topRight)
{
  // 1. calculate window coordinates
  const Video::Coord2i &llwin(Video::project(bottomLeft->x,bottomLeft->y));
  const Video::Coord2i &urwin(Video::project(topRight->x,topRight->y));

  // 2. set new viewport
  int sx=urwin.x-llwin.x;
  int sy=urwin.y-llwin.y;
  
  // 3. todo check for errors
  Video::setViewport(Video::Rectangle(llwin.x,llwin.y,sx,sy));
}

void
org::jgachine::JGachine::pushViewport()
{
  assert(state);
  state->viewportStack.push(Video::getViewport());
}

void
org::jgachine::JGachine::popViewport()
{
  assert(state);
  state->viewportStack.pop();
}

jint
org::jgachine::JGachine::createPolygon(JArray<JArray<javax::vecmath::Vector2f*>*>* contours)
{
}

void
org::jgachine::JGachine::drawPolygon(jint id)
{
}

void
org::jgachine::JGachine::deletePolygon(jint id)
{
}

