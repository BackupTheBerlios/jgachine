package org.jgachine.dscenegraph;

import java.util.*;
import java.io.Serializable;

public class Node implements Serializable
{
    public Node() {
	children = new ArrayList();
	enabled=true;
    }
    public boolean isEnabled(){
	return enabled;
    }

    public Node addNode(Node n) {
	children.add(n);
	return this;
    }
    
    public List children;
    public boolean enabled;
}
