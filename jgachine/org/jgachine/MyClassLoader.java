package org.jgachine;

import java.net.*;
import java.io.*;
import java.util.Hashtable;

public class MyClassLoader extends ClassLoader
{
    public MyClassLoader() 
    {
	this(null);
    }
    public MyClassLoader(ClassLoader parent) {
	super(parent);
	debug("constructed");
    }

    private byte[] loadClassRemote(String name) throws IOException {
	return JGachine.getResource("class:"+name).data;
    }

    public Class loadClass(String name) throws ClassNotFoundException
    {
	debug("loadClass("+name+")");
	return super.loadClass(name);
    }

    protected Class findClass(String name) throws ClassNotFoundException
    {
	debug("findClass("+name+")");
	if (name == null)
	    throw new ClassNotFoundException ("null");
	try{
	    byte[] data=loadClassRemote(name);
	    System.out.println("Define class \""+name+"\"from "+ data.length +" bytes bytecode");
	    return defineClass(name, data, 0, data.length);
	}catch(IOException e){
	    throw new ClassNotFoundException(name+" caused by io exception:"+e.getMessage());
	}
    }

    static protected void debug(String s){
	System.out.println("MyClassLoader.java: "+s);
    }
}
