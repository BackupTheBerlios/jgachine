package org.jgachine.dscenegraph;

import org.jgachine.*;
import java.io.Serializable;

public class Rotate extends Node implements Serializable
{
    public Rotate(){
	this(0.0f);
    }

    public Rotate(float _r)
    {
	r=_r;
    }
    public float r;
}
