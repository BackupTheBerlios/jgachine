package org.jgachine;

import java.net.*;
import java.io.*;
import java.util.Hashtable;

public class MyClassLoader extends ClassLoader
{
    MyClassLoader() 
    {
	this(null);
    }
    MyClassLoader(ClassLoader parent) {
	super(parent);
    }

    private byte[] loadClassRemote(String name) throws IOException {
	return JGachine.getResource("class:"+name).data;
    }
    protected Class findClass(String name) throws ClassNotFoundException
    {
	if (name == null)
	    throw new ClassNotFoundException ("null");
	try{
	    byte[] data=loadClassRemote(name);
	    System.out.println("Define class \""+name+"\"from "+ data.length +" bytes bytecode");
	    return defineClass(name, data, 0, data.length);
	}catch(IOException e){
	    throw new ClassNotFoundException(name);
	}
    }
}
