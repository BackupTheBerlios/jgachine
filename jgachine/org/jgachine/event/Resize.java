package org.jgachine.event;

import java.io.Serializable;

public class Resize implements Serializable
{
    public Resize(int _width, int _height) {
	width=_width;
	height=_height;
    }
    public int width,height;
}
