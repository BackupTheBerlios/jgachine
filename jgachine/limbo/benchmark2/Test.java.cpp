import java.io.*;
import java.lang.System.currentTimeMillis;


class A 
#ifdef EXT
    implements Externalizable
#else
    implements Serializable
#endif
{
    public float f=2;
    public float x=3;
    public int i=7;
#ifdef EXT
    public void writeExternal(ObjectOutput out)
	throws IOException
    {
	out.writeFloat(f);
	out.writeFloat(x);
	out.writeInt(i);
    }
    public void readExternal(ObjectInput in)
	throws IOException, ClassNotFoundException
    {
	f=in.readFloat();
	x=in.readFloat();
	i=in.readInt();
    }
#endif
}


class B extends A
#ifdef EXT
    implements Externalizable
#else
    implements Serializable
#endif
{
    public B() 
    {
	super();
    }
  
    public boolean b=true;
#ifdef EXT
    public void writeExternal(ObjectOutput out)
	throws IOException
    {
	super.writeExternal(out);
	out.writeBoolean(b);
    }
    public void readExternal(ObjectInput in)
	throws IOException, ClassNotFoundException
    {
	super.readExternal(in);
	b=in.readBoolean();
    }
#endif
}

public class Test
{
    static public void main(String args[]) 
	throws IOException, ClassNotFoundException
    {
	final int tests=100;

	ByteArrayOutputStream o=new ByteArrayOutputStream();

	ObjectOutputStream objOut=new ObjectOutputStream(o);
	Object obj=new B();
	long start = System.currentTimeMillis();
	for (int i=0;i<tests;++i) {
	    objOut.writeObject(obj);
	    objOut.reset();
	    objOut.flush();
	}
	long cur = System.currentTimeMillis();
	System.out.println("Serialization: "+(cur-start)+"("+((cur-start)/tests)+")");

	ObjectInputStream objIn=new ObjectInputStream(new ByteArrayInputStream(o.toByteArray()));
	start = System.currentTimeMillis();
	for (int i=0;i<tests;++i) {
	    B b=(B)objIn.readObject();
	}
	cur = System.currentTimeMillis();
	System.out.println("Deserialization: "+(cur-start)+"("+((cur-start)/tests)+")");
    }
}
