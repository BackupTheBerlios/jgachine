package org.jgachine.event;

//! resource request message
public class ResourceRequest implements java.io.Serializable {
    public ResourceRequest()
    {}

    public ResourceRequest(String resName)
    {
	this.resName = resName;
    }
    public String resName;
}

