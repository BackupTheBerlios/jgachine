package org.jgachine.scenegraph;

import org.jgachine.*;

//! draw a 1x1 border
public class Border extends Node
{
    public Border(float thickness)
    {
	// quad size /2
	final float qs2=0.5f;
	final float t2=thickness/2.0f;
	Node quad=new Quad();
	Node lrq=new Scale(new Vector2f(thickness,1)).addNode(quad);
	Node left=new Translate(new Vector2f(-qs2+t2,0.0f)).addNode(lrq);
	Node right=new Translate(new Vector2f(qs2-t2,0.0f)).addNode(lrq);
	Node tbq=new Scale(new Vector2f(1.0f-thickness*2.0f,thickness)).addNode(quad);
	Node top=new Translate(new Vector2f(0,qs2-t2)).addNode(tbq);
	Node bottom=new Translate(new Vector2f(0,-qs2+t2)).addNode(tbq);
	this.addNode(left)
	    .addNode(right)
	    .addNode(top)
	    .addNode(bottom);
    }
}

