package org.jgachine.scenegraph;

import javax.vecmath.*;
import org.jgachine.*;

//! adjust current color
class AdjustColor extends Node
{
    public AdjustColor(Color _c)
    {
	c = _c;
    }

    public void apply() {
	JGachine.pushColor();
	JGachine.adjustColor(c);
	applyChildren();
	JGachine.popColor();
    }

    public Color c;
}
