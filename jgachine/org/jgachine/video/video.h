//! Video subsystem Interface
/*!
  The Video subsystem is a state machine and since the reference
  implementation uses OpenGL - it is heavily inspired by OpenGL.
*/
namespace Video
{
  struct Color;
  
  void init();

  void toggleFullscreen();
  void iconify();
  void resize(int sx, int sy);
  
  void drawLine(int x1,int y1,int x2,int y2);
  void swapBuffers();
  int createTexture(unsigned dsize, const char* data, const char *extension=NULL, const char *mimeType=NULL);
  void drawTexture(int tid);
  void drawText(const std::string &text, bool hcentered, bool vcentered);
  void drawQuad();
  
  void setColor(const Color &c);
  Color getColor();
  
  void pushMatrix();
  void popMatrix();
  void translate(float x,float y);
  void scale(float x,float y);
  void rotate(float r);
  void clear();
  
  void deinit();

  struct Color {
    Color(float _r, float _g, float _b, float _a)
      : r(_r), g(_g), b(_b), a(_a)
    {}
    
    float r,g,b,a;
    
    void set()
    {
      setColor(*this);
    }

    Color &adjust(const Color &o)
    {
      r*=o.r;
      g*=o.g;
      b*=o.b;
      a*=o.a;
      return *this;
    }
  };
};
