package org.jgachine.dscenegraph;

import org.jgachine.*;
import java.io.Serializable;

public class Sprite extends Node implements Serializable
{
    public Sprite(String _resname) {
	resname=_resname;
    }

    public String resname;
}
