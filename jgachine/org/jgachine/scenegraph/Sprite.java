package org.jgachine.scenegraph;

import org.jgachine.*;

public class Sprite extends Node
{
    public Sprite(int _textureID) {
	textureID=_textureID;
    }
    public void apply() {
	JGachine.drawTexture(textureID);
	applyChildren();
    }

    protected int textureID;
}
