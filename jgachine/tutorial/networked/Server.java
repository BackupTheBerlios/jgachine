package tutorial.networked;

import java.io.*;
import java.net.*;
import java.util.*;

class Server{
    //    private Socket socket = null;
    static private List clients = new ArrayList();

    public static synchronized void broadcast(Object obj){
	ListIterator it = clients.listIterator();
	while (it.hasNext()) {
	    ServerThread client=(ServerThread)it.next();
	    try {
		client.sendEvent(obj);
	    }catch(java.io.IOException e){
		// todo
	    }
	}
    }

    protected static synchronized void add(ServerThread client) {
            clients.add(client);
    }


    public static void dispatch(int id, Object obj) {
	debug("Got object: \""+obj.getClass().getName()+"\" from connection with id: "+id);
    }

    public static void main(String[] args) 
	throws IOException, NoSuchMethodException
    {
	if ((args==null)||(args.length!=1)){
	    System.out.println("Usage: Server port");
	    return;
	}
	int port=new Integer(args[0]).intValue();
	System.out.println("I am listening on port: "+port);
	ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println("Could not listen on port: "+port);
            System.exit(-1);
        }
	int id=0;
	new Thread(new Pong()).start();
        while (true) {
	    // todo: BUG: id overflow
	    ServerThread newclient=new ServerThread(serverSocket.accept(),args,++id);
            add(newclient);
	    newclient.start();
	}
    }

    static protected void debug(String s){
	System.out.println("Server.java: "+s);
    }
}

