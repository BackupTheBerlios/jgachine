package org.jgachine.dscenegraph;
import javax.vecmath.*;
import org.jgachine.*;
import java.io.Serializable;

public class AdjustViewport extends Node implements Serializable
{
    //! adjust current viewport
    /*!
      \param bottomLeft the coordinates of the bottom left corner
      \param topRight the coordinates of the top right corner
    */
    public AdjustViewport(Vector2f _bottomLeft, Vector2f _topRight) {
	bottomLeft=_bottomLeft;
	topRight=_topRight;
    }
    public Vector2f bottomLeft;
    public Vector2f topRight;
}
