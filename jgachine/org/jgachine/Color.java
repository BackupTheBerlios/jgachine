package org.jgachine;
import java.io.Serializable;

public class Color implements Serializable
{
    public Color(float _r, float _g, float _b, float _a){
	r=_r;
	g=_g;
	b=_b;
	a=_a;
    }

    public float r,g,b,a;
}
