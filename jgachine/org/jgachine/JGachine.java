package org.jgachine;

import java.io.*;
import java.net.*;
import javax.vecmath.*;
import org.jgachine.signal.*;
import org.jgachine.event.*;

//! simple game engine
public class JGachine {
    // info
    //! get current width of video screen
    static public int getWidth() {
	return width;
    }
    //! get current height of video screen
    static public int getHeight() {
	return height;
    }

    // graphics primitives
    static public native void drawLine(int x1,int y1, int x2, int y2);
    static public native void drawQuad();
    static public int  createTexture(String resname)
	throws java.io.IOException
    {
	return createTexture(getResource(resname));
    }
    static public native int  createTexture(Resource imageData);
    static public native void drawTexture(int tid);
    static public native void drawText(String text, boolean hcentered, boolean vcentered);
    static public native void clear();
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
	Object obj = objIn.readObject();
	input.dispatch(obj);
    }

    //! get resource
    static public Resource getResource(String resname)
	throws java.io.IOException
    {
	return resourceLoader.get(resname);
    }



    static public void main(String args[]) {
	if (runCalled) return;
	runCalled=true;

	if ((args==null)||(args.length<1))
	    {
		System.out.println("Usage: Client uri arg1 arg2 ...");
		System.out.println("Where uri is simply a class name for local games (f.e. example.Example)");
		System.out.println("and jgachine://servername:port/example.Example for remote games");
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
	    String scheme = uri.substring(0,p);
	    if (scheme.equals("jgachine")) {
		debug("jgachine uri");
		// this is written that stupid to show whats going on
		String spr = uri.substring(p+"://".length());
		debug("spr: "+spr);
		String sp = spr.substring(0,spr.indexOf("/"));
		debug("sp: "+sp);
		String server = sp.substring(0,sp.indexOf(":"));
		debug("server: "+server);
		int port = new Integer(sp.substring(sp.indexOf(":")+":".length())).intValue();
		debug("port: "+port);
		String gameclass = spr.substring(spr.indexOf("/")+"/".length());
		debug("gameclass: "+gameclass);
		if (port<0) port=34444; // todo default port
		
		resourceLoader = new NetResourceLoader();
		JGachine.init();
		    
		try {
		    socket = new Socket(InetAddress.getByName(server),port);
			
		    // order is important !!
		    objOut = new ObjectOutputStream(socket.getOutputStream());
		    objIn = new ObjectInputStream(socket.getInputStream());
			
		    MyClassLoader loader=new MyClassLoader();
		    Class c=loader.loadClass(gameclass);
		    System.setSecurityManager(new MySecurityManager());
		    debug("installed security manager");
		    Object obj=c.newInstance();
		    debug("calling fetched code");
		    ((Runnable)obj).run();
			
		    socket.close();
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

    static protected Socket socket;
    static protected ObjectInputStream objIn;
    static protected ObjectOutputStream objOut;
    static protected boolean runCalled=false;
    static protected int width=0;
    static protected int height=0;
    static protected ResourceLoader resourceLoader;
}

