package org.jgachine.dscenegraph;
import javax.vecmath.*;
import org.jgachine.*;
import java.io.Serializable;

public class Camera extends Node implements Serializable 
{
    public Camera(Vector2f _screenMiddle) {
	screenMiddle=_screenMiddle;
    }
    public void rotate(float dr){
	r+=dr;
    }
    public void rotateTo(float _r){
	r=_r;
    }
    public void scrollTo(Vector2f s){
	scroll.set(s);
    }

    public Vector2f screenMiddle=new Vector2f();
    public Vector2f scroll=new Vector2f();
    public float r;
}
