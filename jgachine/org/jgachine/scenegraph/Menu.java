package org.jgachine.scenegraph;

import org.jgachine.*;
import org.jgachine.signal.*;
import org.jgachine.event.*;
import java.util.*;

//! simple menu
public class Menu extends Node
{
    public Menu(List _entries, Color bgColor) {
	dispatcher = new Dispatcher();
	entries = _entries;
	// create border and background
	final float borderSize=0.05f;
	addNode(new Window(bgColor, null, borderSize));
	// layout menu text
	String text="";
	ListIterator it = entries.listIterator();
	int maxlen=0;
	while (it.hasNext()) {
	    String row=(String)it.next();
	    maxlen=Math.max(maxlen,row.length());
	    text+=row;
	    if (it.hasNext()) text+="\n";
	}
	addNode(new Scale(new Vector2f((1.0f-4.0f*borderSize)/maxlen,(1.0f-4.0f*borderSize)/entries.size())).addNode(new Text(text,true,true)));
    }

    //! handle input - is called when the state of a device changed
    public void handleDevState(DevState s)
    {
	if (entries.size()==0) return;
	// we are only interested in the first device
	if (s.devno>0) return;
	if (s.buttons!=0) {
	    try{
		dispatcher.dispatch(new Integer(selection));
	    }catch(Exception e){
		// todo
	    }
	    return;
	}
	if (s.y<0) --selection;
	else if (s.y>0) ++selection;
	if (selection<0) selection=entries.size();
	if (selection>=entries.size()) selection=0;
    }

    //! output signals are dispatched via this one
    public Dispatcher dispatcher;

    protected List entries;
    protected int selection=0;
}
