package org.jgachine;

import java.io.*;
import org.jgachine.ResourceLoader;
import org.jgachine.event.Resource;

public class LocalResourceLoader implements ResourceLoader
{
    public Resource get(String resname)       
	throws java.io.IOException
    {
	// get from local host - todo: this is the same code the server uses
	int p=resname.indexOf(":");
	String restype=resname.substring(0,p);
	// todo are errors checked?
	String resloc=resname.substring(p+1);
	if (restype.equals("class")) {
	    return new Resource(resname,getLocal(resloc.replace('.','/')+".class"));
	}else if (restype.equals("data")){
	    return new Resource(resname,getLocal("data/"+resloc));
	}else{
	    throw new java.io.IOException("unknown requested resoure type: \""+restype+"\"");
	}
    }

    static protected byte[] getLocal(String resname)
	throws java.io.IOException
    {
	debug("get resource: \""+resname+"\"");
	InputStream resstream = ClassLoader.getSystemResourceAsStream(resname);
	if (resstream == null){
	    throw new java.io.IOException("Resource: \""+resname+"\" not found");
	}
	BufferedInputStream bin = new BufferedInputStream(resstream);
	// write it into a byte array 
	ByteArrayOutputStream bstrout = new ByteArrayOutputStream();
	int byteread=0;
	while (( byteread = bin.read()) >= 0)
	    bstrout.write(byteread);
	return bstrout.toByteArray();
    }
    static protected void debug(String s){
	System.out.println("LocalResourceLoader.java: "+s);
    }
}
