package org.jgachine.dscenegraph;

import javax.vecmath.*;
import org.jgachine.*;
import java.io.Serializable;

public class Translate extends Node implements Serializable
{
    public Translate()
    {
	this(new Vector2f(0.0f,0.0f));
    }
    public Translate(Vector2f _t)
    {
	t=_t;
    }
    public Vector2f t;
}
