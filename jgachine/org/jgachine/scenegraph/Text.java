package org.jgachine.scenegraph;

import javax.vecmath.*;
import org.jgachine.*;

public class Text extends Node
{
    public Text(String _text) {
	this(_text,false,false);
    }
    public Text(String _text, boolean hc, boolean vc) {
	text=_text;
	hcentered=hc;
	vcentered=vc;
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
