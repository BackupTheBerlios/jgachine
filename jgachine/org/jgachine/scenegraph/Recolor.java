package org.jgachine.scenegraph;

import javax.vecmath.*;
import org.jgachine.*;

//! set current color
class Recolor extends Node
{
    public Recolor(Color _c)
    {
	c = _c;
    }

    public void apply() {
	JGachine.pushColor();
	JGachine.setColor(c);
	applyChildren();
	JGachine.popColor();
    }

    public Color c;
}
