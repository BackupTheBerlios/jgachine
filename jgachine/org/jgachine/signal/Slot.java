package org.jgachine.signal;

import java.lang.*;
import java.lang.reflect.*;

public class Slot
{
    //! create slot using object and the first method with this name
    /*
      \note be careful - this is just to help you type less and only works as expected if
      you only have one method with this name !
    */
    public Slot(Object obj, String methodName) {
	Method methods[]=obj.getClass().getMethods();
	Method match = null;
	if ((methods == null)||(methods.length==0))
	    throw new FatalException("Method: \""+methodName+"\" not found");
	for (int i=0;i<methods.length;++i) {
	    if (methods[i].getName().equals(methodName)) {
		match=methods[i];
		break;
	    }
	}
	if (match == null)
	    throw new FatalException("Method: \""+methodName+"\" not found");
	init(obj,match);
    }

    //! create slot using object and method
    public Slot(Object _obj, Method _method) {
	init(_obj,_method);
    }

    public void dispatch(Object signal)
	throws java.lang.IllegalAccessException, java.lang.reflect.InvocationTargetException
    {
	Object p[]=new Object[1];
	p[0]=signal;
	method.invoke(obj,p);
    }

    public Class getType(){
	return signalClass;
    }

    void init(Object _obj, Method _method) {
	obj=_obj;
	method=_method;

	Class params[]=method.getParameterTypes();
	if (params.length!=1)
	    throw new FatalException("in the moment we only support signals consisting of one object");
	signalClass=params[0];
    }

    protected Object obj;
    protected Method method;
    protected Class signalClass;
}
