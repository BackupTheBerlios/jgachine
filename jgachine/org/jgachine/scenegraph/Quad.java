package org.jgachine.scenegraph;

import org.jgachine.*;

public class Quad extends Node
{
    public void apply() {
	JGachine.drawQuad();
	applyChildren();
    }
}
