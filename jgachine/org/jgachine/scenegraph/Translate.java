package org.jgachine.scenegraph;

import javax.vecmath.*;
import org.jgachine.*;

public class Translate extends Node
{
    public Translate()
    {
	this(new Vector2f(0.0f,0.0f));
    }
    public Translate(Vector2f _t)
    {
	t=_t;
    }

    public void apply() {
	JGachine.pushMatrix();
	JGachine.translate(t);
	applyChildren();
	JGachine.popMatrix();
    }

    public Vector2f t;
}
