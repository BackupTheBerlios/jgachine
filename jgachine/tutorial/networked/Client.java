package tutorial.networked;

import org.jgachine.*;
import org.jgachine.event.DevState;

//! code executed on the client side
/*!
  reads the scenegraph from the server and
  transforms it to a client side scenegraph
  forwards input device events to the server

  simple games could always use this client code
*/
public class Client implements Runnable
{
    static DevState[] devState;

    Client()
    {
	// how many input devices are available?
	int devs=JGachine.numDevices();
	// create array holding the current state of each input device
	devState=new DevState[devs];

	// don't forget to initialize our input state
	for (int i=0;i<devs;++i) {
	    devState[i]=new DevState((byte)i);
	}
	// register our input callbacks (slots)
	JGachine.input.connect(new Slot(this,"handleDevState"));
	JGachine.input.connect(new Slot(this,"handleSceneGraph"));
    }

    //! handle input - is called when the state of a device changed
    public void handleDevState(DevState s)
	throws java.io.IOException
    {
	devState[s.devno]=s;
	// send event to server
	JGachine.sendObject(s);
    }

    public void handleSceneGraph(tutorial.networked.RootNode node)
	throws java.lang.reflect.InvocationTargetException
    {
	debug("transform scenegraph");
	long t=JGachine.time();
	Transform.transform(node).apply();
	debug("it took "+(JGachine.time()-t)+"usec to transform scenegraph");
    }

    public void run()
	throws java.io.IOException, java.lang.ClassNotFoundException, java.lang.IllegalAccessException,
	java.lang.reflect.InvocationTargetException
    {
	JGachine.sendObject(new ClientState(true));
	while (!JGachine.quit) {
	    // get input
	    JGachine.poll();
	    long t=JGachine.time();
	    JGachine.readObject();
	    debug("it took "+(JGachine.time()-t)+"usec to consume object");
	    JGachine.swapBuffers();
	}
    }
    static protected void debug(String s){
	System.out.println("Client.java: "+s);
    }
}
