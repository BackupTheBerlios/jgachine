package org.jgachine;

import java.util.*;

public class Node
{
    public Node() {
	children = new ArrayList();
	enabled=true;
    }
    public void apply() {
	applyChildren();
    }
    public boolean isEnabled(){
	return enabled;
    }

    public Node addNode(Node n) {
	children.add(n);
	return this;
    }
    
    protected void applyChildren() {
	ListIterator it = children.listIterator();
	while (it.hasNext()) {
	    Node c=(Node)it.next();
	    if (c.isEnabled()) c.apply();
	}
    }

    protected List children;
    protected boolean enabled;
}
