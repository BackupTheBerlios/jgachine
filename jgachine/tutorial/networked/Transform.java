package tutorial.networked;

import java.util.ListIterator;

public class Transform
{
    static public org.jgachine.scenegraph.Node transform(org.jgachine.dscenegraph.Node n) 
	throws java.lang.reflect.InvocationTargetException
    {
	Class[] parms={n.getClass()};
	try{
	    Object[] p={n};
	    return tchildren(n,(org.jgachine.scenegraph.Node)Transform.class.getMethod("t",parms).invoke(null,p));
	}catch(NoSuchMethodException e){
	    debug("got unknown Node: "+n.getClass().getName());
	    return tchildren(n,t(n));
	}catch(java.lang.IllegalAccessException e){
	    throw new RuntimeException("Transform.java: should not happen");
	}
    }

    static protected org.jgachine.scenegraph.Node tchildren(org.jgachine.dscenegraph.Node n,
							    org.jgachine.scenegraph.Node r)
	throws java.lang.reflect.InvocationTargetException
    {
	ListIterator it = n.children.listIterator();
	while (it.hasNext()) {
	    Node c=(Node)it.next();
	    if (c.isEnabled()) r.addNode(transform(c));
	}
	return r;
    }

    static public org.jgachine.scenegraph.Node t(org.jgachine.dscenegraph.Node n)
	throws java.lang.reflect.InvocationTargetException
    {
	debug("Node");
	return new org.jgachine.scenegraph.Node();
    }

    static public org.jgachine.scenegraph.Node t(org.jgachine.dscenegraph.AdjustColor n) throws java.lang.reflect.InvocationTargetException {
	return new org.jgachine.scenegraph.AdjustColor(n.c);
    }

    static public org.jgachine.scenegraph.Node t(org.jgachine.dscenegraph.Translate n) throws java.lang.reflect.InvocationTargetException {
	debug("Translate (x:"+n.t.x+" y:"+n.t.y+")");
	return new org.jgachine.scenegraph.Translate(n.t);
    }

    static public org.jgachine.scenegraph.Node t(org.jgachine.dscenegraph.Rotate n) throws java.lang.reflect.InvocationTargetException {
	debug("Rotate");
	return new org.jgachine.scenegraph.Rotate(n.r);
    }

    static public org.jgachine.scenegraph.Node t(org.jgachine.dscenegraph.Scale n) throws java.lang.reflect.InvocationTargetException {
	debug("Scale");
	return new org.jgachine.scenegraph.Scale(n.t);
    }

    static public org.jgachine.scenegraph.Node t(org.jgachine.dscenegraph.Recolor n) throws java.lang.reflect.InvocationTargetException {
	return new org.jgachine.scenegraph.Recolor(n.c);
    }

    static public org.jgachine.scenegraph.Node t(org.jgachine.dscenegraph.Sprite n)
	throws java.io.IOException
    {
	return new org.jgachine.scenegraph.Sprite(n.resname);
    }

    static public org.jgachine.scenegraph.Node t(org.jgachine.dscenegraph.Camera n) throws java.lang.reflect.InvocationTargetException {
	org.jgachine.scenegraph.Camera r=new org.jgachine.scenegraph.Camera(n.screenMiddle);
	r.scrollTo(n.scroll);
	r.rotateTo(n.r);
	return r;
    }

    static public org.jgachine.scenegraph.Node t(tutorial.networked.RacketSprite n) 
	throws java.lang.reflect.InvocationTargetException, java.io.IOException
    {
	return new org.jgachine.scenegraph.Translate(n.pos)
	    .addNode(new org.jgachine.scenegraph.Scale(n.size)
		     .addNode(new org.jgachine.scenegraph.Recolor(n.color)
			      .addNode(new org.jgachine.scenegraph.Sprite(n.resname))));
    }

    static public org.jgachine.scenegraph.Node t(tutorial.networked.BallSprite n) 
	throws java.lang.reflect.InvocationTargetException, java.io.IOException
    {
	return new org.jgachine.scenegraph.Translate(n.pos)
	    .addNode(new org.jgachine.scenegraph.Rotate(n.rotate)
		     .addNode(new org.jgachine.scenegraph.Scale(n.size)
			      .addNode(new org.jgachine.scenegraph.Sprite(n.resname))));
    }

    static public org.jgachine.scenegraph.Node t(tutorial.networked.RootNode n) 
	throws java.lang.reflect.InvocationTargetException, java.io.IOException
    {
	float bgSize=Math.max(n.assumedScreenSize.x,n.assumedScreenSize.y);
	Vector2f screenMiddle=new Vector2f(n.assumedScreenSize);
	screenMiddle.scale(0.5f);
	return new org.jgachine.scenegraph.Camera(screenMiddle)
	    .addNode(new org.jgachine.scenegraph.Scale(new Vector2f(bgSize,bgSize))
		     .addNode(new org.jgachine.scenegraph.Sprite(n.bgResName)));
    }

    static protected void debug(String s){
	System.out.println("Transform.java: "+s);
    }
}
