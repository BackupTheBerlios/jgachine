#include <string>
#include <iostream>
#include <stdexcept>

#include <SDL.h>
#include "../video.h"
#include "sdlgl.h"
#include "texture.h"
#include "typedefs.h"
#include "smartptr.h"
#include <vector>
#include "fontdata.h"
#include "glfont.h"
#include <cassert>

#if defined(WIN32)
#define NEED_WIN32_HACK 1
#include <SDL_main.h>
#include <windows.h>

#define USE_MESSAGEBOX 1

/* Show an error message */
static 
void ShowError(const char *title, const char *message)
{
/* If USE_MESSAGEBOX is defined, you need to link with user32.lib */
#ifdef USE_MESSAGEBOX
	MessageBox(NULL, message, title, MB_ICONEXCLAMATION|MB_OK);
#else
	fprintf(stderr, "%s: %s\n", title, message);
#endif
}

#endif



static int m_width;
static int m_height;
static unsigned m_flags;
static bool m_lineSmooth=true;

typedef std::vector<DOPE_SMARTPTR<Texture> > Textures;
// we do not use complex static data structures => pointer
static Textures* textures=NULL;
static GLFont* font=NULL;

#ifdef VIDEO_MOTION_BLUR

static int motionBlurTextureID=0;
static int motionBlurTextureSize=1024;
static int frame=0;


static
void
motionBlur()
{
  // motion blur
  Color tmp(getColor());
  glColor4f(1.0f,1.0f,1.0f,0.3f);
  glPushMatrix();
  glLoadIdentity();
  glTranslatef(m_width/2,m_height/2,0);
  glScalef(m_width,m_height,0);

  glEnable(GL_TEXTURE_2D);

  glBindTexture(GL_TEXTURE_2D,motionBlurTextureID);

  const float w2=0.5;
  const float h2=0.5;

  glBegin(GL_QUADS);
  glTexCoord2f(0,0);
  glVertex2f(-w2,-h2);
  float right=float(m_width)/motionBlurTextureSize;
  float top=float(m_height)/motionBlurTextureSize;
  glTexCoord2f(right,0);
  glVertex2f(w2,-h2);
  glTexCoord2f(right,top);
  glVertex2f(w2,h2);
  glTexCoord2f(0,top);
  glVertex2f(-w2,h2);
  glEnd();

  glDisable(GL_TEXTURE_2D);

  // todo this should be time based
  //  if (!(++frame%3))
    (*textures)[motionBlurTextureID]->copyFromScreen(m_width,m_height);

  setColor(tmp);
  glPopMatrix();
}

#endif

static 
void
resize(int width, int height)		
{
  // Prevent A Divide By Zero By
  if (width==0)
    width=1;
  if (height==0)			
    height=1;

  if ( SDL_SetVideoMode(width, height, 0, m_flags) == NULL ) {
    DOPE_WARN(SDL_GetError());
    throw std::runtime_error(std::string("Couldn't set video mode: ")+SDL_GetError());
  }

  // hack for 8bit displays (especially for my sdl+osmesa+aalib and sdl+osmesa+fbcon hack)
  if (SDL_GetVideoSurface()->format->BitsPerPixel == 8) {
    SDL_Color colors[256];
    int i;
    //    int c=0;
    for(i=0;i<256;i++){
      colors[i].r=i; //(c&0xff0000)>>16;
      colors[i].g=i; //(c&0x00ff00)>>8;
      colors[i].b=i; //c&0x0000ff;
      //      c+=(0xffffff)>>8;
    }
    SDL_SetPalette(SDL_GetVideoSurface(), SDL_LOGPAL|SDL_PHYSPAL, colors, 0, 256);
  }
  char *caption="JGachine";
  SDL_WM_SetCaption(caption,caption);
  if (m_flags&SDL_FULLSCREEN)
    SDL_ShowCursor(SDL_DISABLE);
  else
    SDL_ShowCursor(SDL_ENABLE);
  m_width=width;
  m_height=height;

  int db=0;
  if (SDL_GL_GetAttribute( SDL_GL_DOUBLEBUFFER, &db )) DOPE_WARN("could not get attribute");
  if (!db) DOPE_WARN("did not get double buffer");

  // Reset The Current Viewport
  glViewport(0,0,width,height);
  GL_ERRORS();
  // Select The Projection Matrix
  glMatrixMode(GL_PROJECTION);		
  GL_ERRORS();
  glLoadIdentity();
  GL_ERRORS();
  glOrtho(0.0f,width,0.0f,height,-100.0f,100.0f);
  GL_ERRORS();
  glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
  GL_ERRORS();
  glClear(GL_COLOR_BUFFER_BIT);
  glMatrixMode(GL_MODELVIEW);
  glLoadIdentity();
  glColor3f(1.0,1.0,1.0);

  glEnable(GL_BLEND);
  glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

}

static
void
killWindow()
{
  // set all function pointers zero
#ifdef DLOPEN_OPENGL
  deinitGLSymbols();
#endif
  SDL_QuitSubSystem(SDL_INIT_VIDEO|SDL_INIT_JOYSTICK);
  SDL_Quit();
}

static
void
createWindow() 
{
  //  m_flags = SDL_OPENGL|SDL_RESIZABLE|SDL_FULLSCREEN;
  m_flags = SDL_OPENGL|SDL_RESIZABLE;
  SDL_GL_SetAttribute( SDL_GL_DOUBLEBUFFER, 1 );

  resize(1024,768);

  glDisable(GL_NORMALIZE);
  GL_ERRORS();
  glDisable(GL_LIGHTING);
  GL_ERRORS();
  glDisable(GL_CULL_FACE);
  GL_ERRORS();
  float range[2];
  glGetFloatv(GL_LINE_WIDTH_RANGE,range);
  GL_ERRORS();
  //  std::cerr << range[0] << "<LINE_WIDTH<"<<range[1]<<"\n";
  if (!m_lineSmooth) {
    glDisable(GL_LINE_SMOOTH);
    GL_ERRORS();
  }else{
    glEnable(GL_LINE_SMOOTH);
    GL_ERRORS();
  }
  glShadeModel(GL_FLAT);
  GL_ERRORS();

  GL_ERRORS();
  
  //  if (getGUIConfig().quality<=1) {glDisable(GL_DITHER);GL_ERRORS();}
}

void Video::init()
{
#if defined(NEED_WIN32_HACK)
  // hack for mingw to replace/reproduce SDL_main stuff
  ShowError("WinMain() error", "will do preininit now");
  if ( SDL_Init(SDL_INIT_NOPARACHUTE) < 0 ) {
    std::string error(SDL_GetError());
    ShowError("WinMain() error", error.c_str());
    throw std::runtime_error(std::string("Could not preinit SDL: ")+error);
  }
  SDL_SetModuleHandle(GetModuleHandle(NULL));
#endif



  textures=new Textures();
  std::cout << __PRETTY_FUNCTION__ << std::endl;
  if ( SDL_Init(SDL_INIT_VIDEO|SDL_INIT_JOYSTICK|SDL_INIT_NOPARACHUTE) < 0 ) {
    throw std::runtime_error(std::string("Couldn't init SDL: ")+SDL_GetError());
  }
  
#ifdef DLOPEN_OPENGL
  loadGL(getGUIConfig().libGL);
  loadGLU(getGUIConfig().libGLU);
  lookupGLSymbols();
#endif  
  createWindow();

  // load font texture
  int fontTextureID=createTexture(fontDataSize,(const char *)fontData);
  // create font
  assert(fontTextureID>=0);
  assert(fontTextureID<(int)textures->size());
  font=new GLFont((*textures)[fontTextureID]);

#ifdef VIDEO_MOTION_BLUR
  // create texure for motion blur
  DOPE_SMARTPTR<Texture> mbt(new Texture(motionBlurTextureSize));
  textures->push_back(mbt);
  motionBlurTextureID=mbt->getTextureID();
#endif
}

void Video::deinit()
{
  std::cout << __PRETTY_FUNCTION__ << std::endl;
  if (font) {
    delete font;
    font=NULL;
  }
  if (textures) {
    delete textures;
    textures=NULL;
  }
  killWindow();
}



void
Video::drawLine(int x1,int y1,int x2,int y2)
{
    glBegin(GL_LINES);
    glVertex2f(x1,y1);
    glVertex2f(x2,y2);
    glEnd();
}



void 
Video::swapBuffers()
{
#ifdef VIDEO_MOTION_BLUR
  motionBlur();
#endif
  SDL_GL_SwapBuffers();
}

int
Video::createTexture(unsigned dsize, const char* data, const char *extension, const char *mimeType)
{
  DOPE_SMARTPTR<Texture> t(new Texture(dsize,data,extension,mimeType));
  int id=t->getTextureID();
  DOPE_MSG("Info:", "Texture ID: "<<id);
  DOPE_CHECK((id>=0)&&(id<30000));
  if (id>=(int)textures->size())
    textures->resize(id+1);
  (*textures)[id]=t;
  return id;
}

void
Video::drawQuad()
{
  const float w2=0.5;
  const float h2=0.5;

  glBegin(GL_QUADS);
  glTexCoord2f(0,1);
  glVertex2f(-w2,-h2);
  glTexCoord2f(1,1);
  glVertex2f(w2,-h2);
  glTexCoord2f(1,0);
  glVertex2f(w2,h2);
  glTexCoord2f(0,0);
  glVertex2f(-w2,h2);
  glEnd();
}

void
Video::drawTexture(int tid)
{
  DOPE_CHECK(textures);
  if ((tid<0)||(tid>=(int)textures->size())) {
    DOPE_WARN("invalid texure id:"<<tid);
    return;
  }
    
  glEnable(GL_TEXTURE_2D);

  //  DOPE_SMARTPTR<Texture> &t((*textures)[tid]);
  //  DOPE_CHECK(t.get());
  //  bool blend=(t->isTransparent());
  //  if (blend) glEnable(GL_BLEND);
  //  glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
  glBindTexture(GL_TEXTURE_2D,tid);

  drawQuad();

  glDisable(GL_TEXTURE_2D);
  //if (blend) glDisable(GL_BLEND);
}

void
Video::drawText(const std::string &text, bool hcentered, bool vcentered)
{
  assert(font);
  font->drawText(text,hcentered);
}

void
Video::setColor(const Color &c)
{
  glColor4f(c.r,c.g,c.b,c.a);
}

Video::Color
Video::getColor()
{
  float c[4];
  glGetFloatv(GL_CURRENT_COLOR, c);
  return Color(c[0],c[1],c[2],c[3]);
}



void
Video::pushMatrix()
{
  glPushMatrix();
}

void
Video::popMatrix()
{
  glPopMatrix();
}

void
Video::translate(float x,float y)
{
  glTranslatef(x,y,0);
}

void
Video::scale(float x,float y)
{
  glScalef(x,y,0);
}

void
Video::rotate(float r)
{
  glRotatef(r,0,0,1);
}

void
Video::clear()
{
  glClear(GL_COLOR_BUFFER_BIT);
}
