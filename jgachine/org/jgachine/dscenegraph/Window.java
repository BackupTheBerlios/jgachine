package org.jgachine.dscenegraph;

import org.jgachine.*;
import javax.vecmath.*;
import java.io.Serializable;

public class Window extends Node implements Serializable
{
    public Window(Color _back, Color _border, float _borderWidth) {
	back=_back;
	border=_border;
	borderWidth=_borderWidth;
    }
    protected Color back;
    protected Color border;
    protected float borderWidth;
}
