package org.jgachine.scenegraph;

import org.jgachine.*;

public class Sprite extends Node
{
    public Sprite(int _textureID) {
	textureID=_textureID;
    }
    public Sprite(String resname)
	throws java.io.IOException
    {
	textureID=JGachine.createTexture(resname);
    }

    public void apply() {
	JGachine.drawTexture(textureID);
	applyChildren();
    }

    protected int textureID;
}
