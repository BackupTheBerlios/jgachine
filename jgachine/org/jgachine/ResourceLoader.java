package org.jgachine;

import org.jgachine.event.Resource;

public interface ResourceLoader {
    Resource get(String resname) throws java.io.IOException;
}

