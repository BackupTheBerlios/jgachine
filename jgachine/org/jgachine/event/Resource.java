package org.jgachine.event;

import java.io.Serializable;

public class Resource implements Serializable
{
    public Resource(String _name, byte _data[])
    {
	name = _name;
	data = _data;
    }
    public String name;
    public byte data[];
}
