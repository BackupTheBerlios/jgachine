package org.jgachine.scenegraph;
import javax.vecmath.*;
import org.jgachine.*;

public class CoordinateSystem extends Node
{
    public CoordinateSystem(Vector2f _bottomLeft, Vector2f _topRight) {
	bottomLeft=_bottomLeft;
	topRight=_topRight;
    }

    public void apply() {
	JGachine.pushViewportCoordinates();
	JGachine.setViewportCoordinates(bottomLeft.x, topRight.x,
					bottomLeft.y, topRight.y,
					-100,100);
	applyChildren();
	JGachine.popViewportCoordinates();
    }
    public Vector2f bottomLeft, topRight;
}
