package org.openelis.remote;

import java.util.HashMap;
import javax.ejb.Remote;

@Remote
public interface SecurityRemote {

    public HashMap getModules(String application);
}
