package org.jgachine.dscenegraph;

import javax.vecmath.*;
import org.jgachine.*;
import java.io.Serializable;

//! set current color
public class Recolor extends Node implements Serializable
{
    public Recolor(Color _c)
    {
	c = _c;
    }
    public Color c;
}
