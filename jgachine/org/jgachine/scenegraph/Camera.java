package org.jgachine.scenegraph;
import javax.vecmath.*;
import org.jgachine.*;

public class Camera extends Node
{
    public Camera(Vector2f screenMiddle) {
	Translate tmid = new Translate(screenMiddle);
	r = new Rotate();
	scroll = new Translate();
	children.add(tmid);
	tmid.addNode(r.addNode(scroll));
    }
    public void rotate(float dr){
	r.r-=dr;
    }
    public void rotateTo(float _r){
	r.r=-_r;
    }
    public void scrollTo(Vector2f s){
	scroll.t.negate(s);
    }

    public Node addNode(Node n) {
	scroll.addNode(n);
	return this;
    }

    protected Translate scroll;
    protected Rotate r;
}
