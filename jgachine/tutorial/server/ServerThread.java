package tutorial.server;

import java.net.*;
import java.io.*;
import org.jgachine.*;
import org.jgachine.signal.*;
import org.jgachine.event.*;

public class ServerThread extends Thread {
    protected Socket socket = null;
    protected String[] args;
    protected ObjectInputStream objIn;
    protected ObjectOutputStream objOut;
    protected Dispatcher dispatcher;
    protected final int id;
    protected ResourceLoader resourceLoader;

    public ServerThread(Socket socket, String[] args, int id) 
	throws java.io.IOException, java.lang.NoSuchMethodException
    {
	super("ServerThread");
        this.socket = socket;
	this.args = args;
	this.id = id;
	resourceLoader = new LocalResourceLoader();

	debug("New connection");
	objIn = new ObjectInputStream(socket.getInputStream());
	objOut = new ObjectOutputStream(socket.getOutputStream());

	dispatcher = new Dispatcher();

	// connect slot to dispatcher
	// unfortunately it seems you can only get Method objects at run-time
	// => if you mistype the function name (or change it only once in the code)
	//    it will only be detected at run-time
	dispatcher.connect(new Slot(this,"handleResourceRequest"));
	debug("dispatcher connected");
    }

    public void run() {
        try {
	    while (true) {
		Object obj = objIn.readObject();
		if (!dispatcher.dispatch(obj)) {
		    // we did not handle this message internally
		    // => dispatch it to the main game thread (upstream)
		    Server.dispatch(id,obj);
		}
	    }
        } catch (Exception e) {
	    System.out.println("Close a connection because of an error");
            e.printStackTrace();
        }
	return;
    }

    public void handleResourceRequest(ResourceRequest resourceRequest) 
	throws java.io.IOException
    {
	debug("got resource request");
	sendResource(resourceLoader.get(resourceRequest.resName));
    }

    protected void sendResource(Resource res)
	throws java.io.IOException
    {
	objOut.writeObject(res);
	objOut.flush();
	debug("Sent resource: \""+res.name+"\" to client");
    }

    static protected void debug(String s){
	System.out.println("ServerThread.java: "+s);
    }
    static Class ResReqClass;
}


