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
    }
}
