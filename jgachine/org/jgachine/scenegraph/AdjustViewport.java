package org.jgachine.scenegraph;
import javax.vecmath.*;
import org.jgachine.*;

public class AdjustViewport extends Node
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
    public void apply() {
	JGachine.pushViewport();
	JGachine.adjustViewport(c);
	applyChildren();
	JGachine.popViewport();
    }
    public Vector2f bottomLeft;
    public Vector2f topRight;
}
