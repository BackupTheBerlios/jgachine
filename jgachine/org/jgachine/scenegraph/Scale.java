package org.jgachine.scenegraph;

import javax.vecmath.*;
import org.jgachine.*;

public class Scale extends Node
{
    public Scale(Vector2f _t)
    {
	t=_t;
    }

    public void apply() {
	JGachine.pushMatrix();
	JGachine.scale(t);
	applyChildren();
	JGachine.popMatrix();
    }

    public Vector2f t;
}
