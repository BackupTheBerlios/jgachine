import java.io.*;
import java.net.*;
import java.util.*;

class Server{
    private Socket socket = null;

    public static void dispatch(int id, Object obj) {
	debug("Got object: \""+obj.getClass().getName()+"\" from connection with id: "+id);
    }

    public static void main(String[] args) throws IOException {
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
        while (true) {
	    // todo: BUG: id overflow
            new ServerThread(serverSocket.accept(),args,++id).start();
	}
    }

    static protected void debug(String s){
	System.out.println("Server.java: "+s);
    }
}

