package org.jgachine;

import org.jgachine.ResourceLoader;
import org.jgachine.signal.Slot;
import org.jgachine.event.Resource;
import org.jgachine.event.ResourceRequest;

public class NetResourceLoader implements ResourceLoader
{
    public NetResourceLoader()
    {
    }
    public Resource get(String resname)
	throws java.io.IOException
    {
	debug("Requesting resource from server: \""+resname+"\"");
	ResourceRequest req = new ResourceRequest(resname);
	Resource res = null;
	try {
	    debug("call readObject");
	    res=(Resource)JGachine.sendReceiveObject(req);
	    debug("readObject returned");
	    return res;
	}catch(java.lang.ClassNotFoundException e){
	    debug("exception:"+e.getMessage());
	    e.printStackTrace();
	    throw new java.io.IOException("java.lang.ClassNotFoundException:"+e.getMessage());
	}catch(java.lang.IllegalAccessException e){
	    debug("exception:"+e.getMessage());
	    e.printStackTrace();
	    throw new java.io.IOException("java.lang.IllegalAccessException:"+e.getMessage());
	}catch(java.lang.reflect.InvocationTargetException e){
	    debug("exception:"+e.getMessage());
	    e.printStackTrace();
	    throw new java.io.IOException("java.lang.reflect.InvocationTargetException:"+e.getMessage());
	}catch(Exception e){
	    debug("exception:"+e.getMessage());
	    e.printStackTrace();
	    throw new java.io.IOException("java.io.IOException:"+e.getMessage());
	}
    }

    static protected void debug(String s){
	System.out.println("NetResourceLoader.java: "+s);
    }
}
