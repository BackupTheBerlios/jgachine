package org.jgachine.dscenegraph;

import javax.vecmath.*;
import org.jgachine.*;
import java.io.Serializable;

//! adjust current color
public class AdjustColor extends Node implements Serializable
{
    public AdjustColor(Color _c)
    {
	c = _c;
    }
    public Color c;
}
