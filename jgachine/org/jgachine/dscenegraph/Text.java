package org.jgachine.dscenegraph;

import javax.vecmath.*;
import org.jgachine.*;
import java.io.Serializable;

public class Text extends Node implements Serializable
{
    public Text(String _text) {
	this(_text,false,false);
    }
    public Text(String _text, boolean hc, boolean vc) {
	text=_text;
	hcentered=hc;
	vcentered=vc;
    }
    public void set(String _text) {
	text=_text;
    }

    protected String text;
    protected boolean hcentered, vcentered;
}
