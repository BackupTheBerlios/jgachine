//! Video subsystem Interface
/*!
  The Video subsystem is a state machine and since the reference
  implementation uses OpenGL - it is heavily inspired by OpenGL.
*/
namespace Video
{
  struct Color;
  struct ViewportCoordinates;
  struct Coord2i;
  struct Rectangle;
  
  void init();

  void toggleFullscreen();
  void iconify();
  void resize(int sx, int sy);
  
  void drawLine(float x1,float y1,float x2,float y2);
  void swapBuffers();
  int createTexture(unsigned dsize, const char* data, const char *extension=NULL, const char *mimeType=NULL);
  void drawTexture(int tid);
  void drawText(const std::string &text, bool hcentered, bool vcentered);
  void drawQuad();
  
  void setColor(const Color &c);
  Color getColor();

  void setViewportCoordinates(const ViewportCoordinates &coords);
  ViewportCoordinates getViewportCoordinates();
  
  void setViewport(const Rectangle &r);
  Rectangle getViewport();
  
  void pushMatrix();
  void popMatrix();
  void translate(float x,float y);
  void scale(float x,float y);
  void rotate(float r);
  void clear();
  //! get screen (window) coordinates for this point
  Coord2i project(float x, float y);
  
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

  struct ViewportCoordinates 
  {
    ViewportCoordinates(){}
    ViewportCoordinates(float _left, float _right, float _bottom, float _top, float _near, float _far)
      : left(_left), right(_right), bottom(_bottom), top(_top), near(_near), far(_far)
    {}
    
    float left,right,bottom,top,near,far;
    void set() const
    {
      setViewportCoordinates(*this);
    }
  };

  struct Coord2i
  {
    Coord2i()
    {}
    Coord2i(int _x,int _y)
      : x(_x), y(_y)
    {}
    int x,y;
  };

  struct Rectangle
  {
    Rectangle()
    {}
    Rectangle(int _x, int _y, int _sx, int _sy)
      : x(_x), y(_y), sx(_sx), sy(_sy)
    {}
    int x,y,sx,sy;
    void set() const
    {
      setViewport(*this);
    }
  };
};
