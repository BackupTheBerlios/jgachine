package tutorial.networked;

import java.io.Serializable;

public class ClientState implements Serializable
{
    //! this signals the server wether he may send unrequested messages or not
    public boolean eventsAllowed;

    public ClientState(boolean _eventsAllowed) {
	eventsAllowed=_eventsAllowed;
    }
}
