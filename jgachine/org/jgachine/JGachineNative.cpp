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


typedef std::vector<Video::Color> ColorStack;
static ColorStack* colorStack=NULL;

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
Input::devStateHandler(const Input::DevState& state)
{
  if (org::jgachine::JGachine::quit) return;
  if (!org::jgachine::JGachine::input) return;
  // translate c++ object to java object
  org::jgachine::event::DevState* s=new org::jgachine::event::DevState();
  s->x=state.x;
  s->y=state.y;
  s->buttons=state.buttons;
  s->devno=state.devno;
  org::jgachine::JGachine::input->dispatch(s);
}

void
Input::resizeHandler(int sx, int sy)
{
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
  
  assert(!colorStack);
  colorStack=new ColorStack();

  // at the moment video must always be initialized first
  Video::init();
  Input::init();
}

void org::jgachine::JGachine::deinit()
{
  if (!inited) return;
  
  if (colorStack) {
    delete colorStack;
    colorStack=NULL;
  }
  
  Input::deinit();
  Video::deinit();
}

void
org::jgachine::JGachine::drawLine(jint x1, jint y1, jint x2, jint y2)
{
  Video::drawLine(x1,y1,x2,y2);
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
  assert(colorStack);
  colorStack->push_back(Video::getColor());
}

void 
org::jgachine::JGachine::popColor ()
{
  assert(colorStack);
  if (colorStack->empty()) {
    std::cerr << "TODO: should not happen\n";
    return;
  }
  colorStack->back().set();
  colorStack->pop_back();
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

