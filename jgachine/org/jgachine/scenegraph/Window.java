package org.jgachine.scenegraph;

import org.jgachine.*;
import javax.vecmath.*;

public class Window extends Node
{
    public Window(Color back, Color border, float borderWidth) {
	final float s=1.0f-borderWidth*2.0f;
	Node croot=(Node)new Scale(new Vector2f(s,s));
	if (back != null ) {
	    Node nroot=(Node)new Recolor(back);
	    croot.addNode(nroot);
	    croot=nroot;
	}
	croot.addNode(new Quad());
	addNode(croot);
	croot=(Node)this;
	if (border != null) {
	    croot=(Node)new Recolor(border);
	    addNode(croot);
	}
	croot.addNode(new Border(borderWidth));
    }
}
