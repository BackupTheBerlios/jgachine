package org.jgachine.dscenegraph;

import javax.vecmath.*;
import org.jgachine.*;
import java.io.Serializable;

//! draw a 1x1 border
public class Border extends Node implements Serializable
{
    public Border(float _thickness)
    {
	thickness=_thickness;
    }
    float thickness;
}

