package org.jgachine;

import java.io.*;
import java.net.*;
import javax.vecmath.*;
import org.jgachine.signal.*;
import org.jgachine.event.*;
import java.util.Hashtable;

//! simple game engine
public class JGachine {
    // info
    //! get current width of video screen/window [pixels]
    /*!
      \note this may change while running the game
      since on some platforms the video screen is
      a resizable window
    */
    static public int getWidth() {
	return width;
    }
    //! get current height of video screen/window [pixels]
    /*!
      \note this may change while running the game
      since on some platforms the video screen is
      a resizable window
    */
    static public int getHeight() {
	return height;
    }
    //! get current real width of video screen/window [mm]
    /*!
      \note this may change while running the game
      since on some platforms the video screen is
      a resizable window
      \return 0 if unknown
      \todo try to implement it
    */
    static public int getRealWidth() {
	return 0;
    }
    //! get current real height of video screen/window [mm]
    /*!
      \note this may change while running the game
      since on some platforms the video screen is
      a resizable window
      \return 0 if unknown
      \todo try to implement it
    */
    static public int getRealHeight() {
	return 0;
    }
    

    // coordinates and viewports

    //! set current view port coordinates
    /*!
      \note The OpenGL implementation calls glOrtho with the given parameters
      (man glOrtho). The idea is to let the game programer decide if he wants
      to use device independent coordinates or not and how his coordinate
      system should look like.
      If he does not want device independant coodinates he must update the 
      coordinate system on each resize. This must be done with device
      independant coordinates, too, if the aspect ratio should be respected
      - but then this are not really device independant coordinates anymore
    */
    static public native void setViewportCoordinates(float left, float right,
						     float bottom, float top,
						     float near, float far);
    static public native void pushViewportCoordinates();
    static public native void popViewportCoordinates();

    //! set current view port (position and size)
    /*!
      \param x bottom left x
      \param y bottom left y
      \param sx width
      \param sy height
      \note this are screen (window) coordinates - with OpenGL coordinate
      convention: bottom left is (0/0) top right is (width-1,height-1)
      (man glViewport)
    */
    static public native void setViewport(int x, int y, int sx, int sy);
    //! adjust current viewport
    /*!
      \param bottomLeft the coordinates of the bottom left corner
      \param topRight the coordinates of the top right corner
      \note this are "normal" coordinates (coordinates relative to the 
      current viewport coordinate system) not screen (window) coordinates
    */
    static public native void adjustViewport(Vector2f bottomLeft, Vector2f topRight);
    static public native void pushViewport();
    static public native void popViewport();

    // graphics primitives
    //! draw line with current color
    static public native void drawLine(float x1,float y1, float x2, float y2);
    //! draw lines with current color
    /*!
      \param pts each pair is one independent line
    */
    static public native void drawLines(Vector2f pts[]);
    //! draw connected lines with current color
    static public native void drawLineStrip(Vector2f pts[]);

    //! draws a 1/1 sized quad centered on current position with current color
    static public native void drawQuad();
    //! draw complex polygon (s.a. createPolygon)
    static public native void drawPolygon(int id);
    //! draws a 1/1 sized textured quad centered on current position
    /*!
      \note the current color is used!
      \todo explain how the current color is used
    */
    static public native void drawTexture(int tid);

    //! draws a text string with fixed width font of size 1/1
    /*!
      \param text the text
      \param hcentered center text horizontally
      \param vcentered center text vertically

      \note if neither hcentered nor vcentered is true the bottom left of the text
      is drawn at the current position
    */
    static public native void drawText(String text, boolean hcentered, boolean vcentered);
    //! clear current viewport
    /*!
      \todo in the moment the clear color can't be set - should we use the current color?
    */
    static public native void clear();
    //! swap front and back buffer
    /*!
      \todo in the moment we always try to get a double buffered visual and there is
      no way for the game programer to change this - and to know if we really got one
    */
    static public native void swapBuffers();

    // state manipulation
    static public native void setColor(Color c);
    static public native void adjustColor(Color c);
    static public native Color getColor();
    static public native void pushColor();
    static public native void popColor();

    // matrix manipulation
    static public native void pushMatrix();
    static public native void popMatrix();
    static public native void translate(Vector2f t);
    static public native void scale(Vector2f t);
    static public native void rotate(float r);

    // timer
    static public native long time();
    static public native void uSleep(long usec);

    // input
    //! get input signals
    /*!
      \note this is non-blocking
    */
    static public native void poll();
    //! get number of available joypad like devices
    static public native int numDevices();
    //! input signal dispatcher (connect to it to get the signals)
    static public final Dispatcher input = new Dispatcher();

    // network
    //! is network available
    static public boolean isNetAvailable() {
	return socket != null;
    }
    //! send object to server
    static public void sendObject(Object obj)
	throws java.io.IOException
    {
	if (!isNetAvailable()) throw new java.io.IOException("not running in networked mode");
	objOut.writeObject(obj);
	objOut.reset();
	objOut.flush();
    }
    //! send object to server and get answer object (RPC)
    static public Object sendReceiveObject(Object obj)
	throws java.io.IOException, java.lang.ClassNotFoundException
    {
	if (!isNetAvailable()) throw new java.io.IOException("not running in networked mode");
	Socket s=new Socket(socket.getInetAddress(),socket.getPort());
	ObjectOutputStream o = new ObjectOutputStream(s.getOutputStream());
	ObjectInputStream i = new ObjectInputStream(s.getInputStream());
	o.writeObject(obj);
	return i.readObject();
    }

    //! read object from server
    /*!
      \note 
      the object read is dispatched via the input signal
      this is blocking !!
      \todo perhaps throws only one special exception
    */
    static public void readObject() throws 
	java.lang.ClassNotFoundException,
	java.io.IOException,
	java.lang.IllegalAccessException,
	java.lang.reflect.InvocationTargetException
    {
	long t1=JGachine.time();
	Object obj = objIn.readObject();
	long t2=JGachine.time();
	if (!input.dispatch(obj)) {
	    debug("Object not handled. Type: "+obj.getClass().getName());
	}
	long t3=JGachine.time();
	debug("deserialize: "+(t2-t1)+"usec, dispatch: "+(t3-t2)+"usec");
    }

    // resources

    //! create texture from resource name
    static public int  createTexture(String resname)
	throws java.io.IOException
    {
	Integer iid=(Integer)textures.get(resname);
	if (iid != null) return iid.intValue();
	int id=createTexture(getResource(resname));
	textures.put(resname,new Integer(id));
	return id;
    }

    //! get resource
    static public Resource getResource(String resname)
	throws java.io.IOException
    {
	return resourceLoader.get(resname);
    }
    //! create polygon resource
    /*!
      create complex polygon (perhaps not convex and perhaps multiple contours)
      see man gluTessBeginPolygon to get the idea

      \return Polygon id
    */
    static public native int createPolygon(Vector2f contours[][]);
    static public native void deletePolygon(int id);

    static public void main(String args[]) {
	if (runCalled) return;
	runCalled=true;

	if (System.getProperty("java.system.class.loader").equals("org.jgachine.MyClassLoader")) {
	    // detect bug where our classloader is not the system classloader
	    // for e.g. gcc bug #14572
	    // TODO: find a workaround
	    classloaderbug=!ClassLoader.getSystemClassLoader().getClass().getName().equals("org.jgachine.MyClassLoader");
	    if (classloaderbug)
		debug("BUG!: we can't use our ClassLoader");
	    else
		debug("fine - our ClassLoader is installed");
	}

	try {
	    Class dummy = Class.forName("java.util.ArrayList");
	}catch(Exception e){
	    e.printStackTrace();
	}

	if ((args==null)||(args.length<1))
	    {
		System.out.println("Usage: Client uri arg1 arg2 ...");
		System.out.println("Where uri is simply a class name for local games (f.e. tutorial.pong1.Pong)");
		System.out.println("and jgachine://servername:port/tutorial.pong1.Pong for remote games");
		return;
	    }
	// libgcj does not implement the uri yet (URL seems a bit strange...)
	// URI uri = new URIargs[0]);
	String uri = args[0];
	int p=uri.indexOf("://");
	if (p<=0) {
	    // run local game - without network
	    resourceLoader = new LocalResourceLoader();
	    JGachine.init();
	    try {
		Class c=Class.forName(uri);
		System.setSecurityManager(new MySecurityManager());
		debug("installed security manager");
		Object obj=c.newInstance();
		debug("calling fetched code");
		((Runnable)obj).run();
	    }catch(Exception e){
		e.printStackTrace();
	    }
	    JGachine.deinit();
	    return;
	}else{
	    System.setSecurityManager(new MySecurityManager());
	    debug("installed security manager");

	    String scheme = uri.substring(0,p);
	    if (scheme.equals("jgachine")) {
		debug("jgachine uri => connect to server");
		// this is written that stupid to show whats going on
		String spr = uri.substring(p+"://".length());
		//		debug("spr: "+spr);
		String sp = spr.substring(0,spr.indexOf("/"));
		//		debug("sp: "+sp);
		String server = sp.substring(0,sp.indexOf(":"));
		//		debug("server: "+server);
		int port = new Integer(sp.substring(sp.indexOf(":")+":".length())).intValue();
		//		debug("port: "+port);
		String gameclass = spr.substring(spr.indexOf("/")+"/".length());
		//		debug("gameclass: "+gameclass);
		if (port<0) port=34444; // todo default port - perhaps via property
		
		resourceLoader = new NetResourceLoader();
		JGachine.init();
		    
		try {
		    socket = new Socket(InetAddress.getByName(server),port);

		    MyClassLoader loader=new MyClassLoader();
			
		    // order is important !! ?? which order and why?
		    objOut = new ObjectOutputStream(socket.getOutputStream());
		    objIn = new ObjectInputStream(socket.getInputStream());

		    /* does not work
		       debug("use bug workaround");

		       // workaround bug
		       // TODO: this is not a complete workaround
		       // we only replace the classloader of our objectinputstream and not the systemclassloader
		       Class consargs[]={InputStream.class};
		       Class objInClass=loader.loadClass("java.io.ObjectInputStream");
		       debug("objInClass ClassLoader: "+objInClass.getClass().getClassLoader().getClass().getName());
		       Object parms[]={socket.getInputStream()};
		       objIn = (java.io.ObjectInputStream)objInClass.getConstructor(consargs).newInstance(parms);
		       debug("objIn ClassLoader: "+objIn.getClass().getClassLoader().getClass().getName());
		    */
			
		    Class c=loader.loadClass(gameclass);
		    Object obj=c.newInstance();
		    debug("calling fetched code (ClassLoader: "+c.getClassLoader().getClass().getName()+")");
		    ((Runnable)obj).run();
			
		    socket.close();
		}catch(java.lang.reflect.InvocationTargetException e){
		    e.printStackTrace();
		    Throwable wrapped=e.getCause();
		    int depth=1;
		    while (wrapped!=null) {
			debug("unwrap InvocationTargetException ("+depth+")");
			debug(wrapped.getMessage());
			wrapped.printStackTrace();
			wrapped=wrapped.getCause();
			++depth;
		    }
		}catch(Exception e){
		    e.printStackTrace();
		}
		    
		JGachine.deinit();
	    }else{
		System.out.println("Unsupported URI scheme");
		return;
	    }
	}
    }

    // our data
    static public boolean quit=false;

    // private stuff --------------------------------------------------
    static protected native void init();
    static protected native void deinit();
    static protected void debug(String s){
	System.out.println("JGachine.java: "+s);
    }

    //! create texture from resource
    static protected native int  createTexture(Resource imageData);

    static protected Socket socket;
    static protected ObjectInputStream objIn;
    static protected ObjectOutputStream objOut;
    static protected boolean runCalled=false;
    static protected int width=0;
    static protected int height=0;
    static protected ResourceLoader resourceLoader;
    static private boolean classloaderbug;
    static private Hashtable textures = new Hashtable();
}

