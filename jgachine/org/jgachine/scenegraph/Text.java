package org.jgachine.scenegraph;

import javax.vecmath.*;
import org.jgachine.*;

public class Text extends Node
{
    public Text(String _text) {
	text=_text;
	hcentered=false;
	vcentered=false;
    }
    public void apply() {
	JGachine.drawText(text,hcentered,vcentered);
	applyChildren();
    }
    public void set(String _text) {
	text=_text;
    }

    protected String text;
    protected boolean hcentered, vcentered;
}
