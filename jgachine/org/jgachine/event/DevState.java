package org.jgachine.event;

import java.io.Serializable;

public class DevState implements Serializable
{
    public DevState() {
	this((byte)0);
    }
    public DevState(byte dev) {
	devno=dev;
    }
    public byte x=0,y=0,buttons=0,devno;
}
