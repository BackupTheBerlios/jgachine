package org.jgachine.scenegraph;

import org.jgachine.*;

public class Rotate extends Node
{
    public Rotate(){
	this(0.0f);
    }

    public Rotate(float _r)
    {
	r=_r;
    }

    public void apply() {
	JGachine.pushMatrix();
	JGachine.rotate(r);
	applyChildren();
	JGachine.popMatrix();
    }

    public float r;
}
