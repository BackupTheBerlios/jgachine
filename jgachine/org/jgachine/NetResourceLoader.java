package org.jgachine;

import org.jgachine.ResourceLoader;
import org.jgachine.signal.Slot;
import org.jgachine.event.Resource;
import org.jgachine.event.ResourceRequest;

public class NetResourceLoader implements ResourceLoader
{
    public NetResourceLoader()
    {
	queue = null;
	JGachine.input.connect(new Slot(this,"handleResource"));
    }
    public Resource get(String resname)
	throws java.io.IOException
    {
	if (queue != null) {
	    throw new java.io.IOException("sorry in the moment only one outstanding resource request permitted (TODO)");
	}
	debug("Requesting resource from server: \""+resname+"\"");
	ResourceRequest req = new ResourceRequest(resname);
	JGachine.sendObject(req);

	// todo handle the case if we don't get a resource
	// then we have to put back the received object (in a stream or queue)
	while (queue == null) {
	    try {
		JGachine.readObject();
	    }catch(java.lang.ClassNotFoundException e){
		throw new java.io.IOException("java.lang.ClassNotFoundException:"+e.getMessage());
	    }catch(java.lang.IllegalAccessException e){
		throw new java.io.IOException("java.lang.IllegalAccessException:"+e.getMessage());
	    }catch(java.lang.reflect.InvocationTargetException e){
		throw new java.io.IOException("java.lang.reflect.InvocationTargetException:"+e.getMessage());
	    }
	}
	Resource res = queue;
	queue = null;
	return res;
    }
    public void handleResource(Resource res){
	queue = res;
    }

    protected Resource queue;

    static protected void debug(String s){
	System.out.println("NetResourceLoader.java: "+s);
    }
}
