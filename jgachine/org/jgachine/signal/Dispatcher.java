package org.jgachine.signal;

import java.io.*;
import java.util.*;


//! dispatch signal (object) to connected slots
public class Dispatcher
{
    public Dispatcher(){
	slots = new ArrayList();
    }

    //! register a signal handler/slot
    public void connect(Slot slot){
	slots.add(slot);
    }

    //! register a message handler/signal slot
    public void disconnect(Slot slot){
	// todo
    }

    //! dispatch object/signal
    /*!
      \return true if dispatched, false otherwise
    */
    public boolean dispatch(Object obj) throws 
	java.io.IOException, 
	java.lang.ClassNotFoundException, 
	java.lang.IllegalAccessException,
	java.lang.reflect.InvocationTargetException
    {
	boolean res = false;
	debug("got signal");
	ListIterator it = slots.listIterator();
	while (it.hasNext()) {
	    Slot s=(Slot)it.next();
	    // todo: equals ?
	    if (s.getType()==obj.getClass()) {
		debug("found slot");
		res = true;
		s.dispatch(obj);
	    }
	}
	return res;
    }

    //! registered handlers
    protected List slots;

    static protected void debug(String s){
	//	System.out.println("Dispatcher.java: "+s);
    }
}
