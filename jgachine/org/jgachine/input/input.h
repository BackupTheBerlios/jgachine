namespace Input
{
  struct DevState
  {
    DevState() : x(0),y(0),buttons(0)
    {}
      
    char x,y,devno,buttons;

    bool operator==(const DevState &o) const
    {
      return (x==o.x)&&(y==o.y)&&(devno==o.devno)&&(buttons==o.buttons);
    }
    bool operator!=(const DevState &o) const
    {
      return !operator==(o);
    }
  };
  
  typedef short int Unicode;

  // callbacks
  void quitHandler();
  void toggleFullscreenHandler();
  void iconifyHandler();
  
  void devStateHandler(const DevState&);
  void charHandler(Unicode);
  void resizeHandler(int sx, int sy);

  // to implement
  void init();
  void charInput(bool active);
  unsigned attachedDevices();
  void poll();
  void deinit();
}
