package org.jgachine.dscenegraph;
import javax.vecmath.*;
import org.jgachine.*;
import java.io.Serializable;

public class CoordinateSystem extends Node implements Serializable
{
    public CoordinateSystem(Vector2f _bottomLeft, Vector2f _topRight) {
	bottomLeft=_bottomLeft;
	topRight=_topRight;
    }

    public Vector2f bottomLeft, topRight;
}
