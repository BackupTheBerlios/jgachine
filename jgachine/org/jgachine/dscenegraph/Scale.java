package org.jgachine.dscenegraph;

import javax.vecmath.*;
import org.jgachine.*;
import java.io.Serializable;

public class Scale extends Node implements Serializable
{
    public Scale(Vector2f _t)
    {
	t=_t;
    }
    public Vector2f t;
}
