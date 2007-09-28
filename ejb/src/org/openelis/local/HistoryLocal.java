package org.openelis.local;

import javax.ejb.Local;

import org.openelis.interfaces.Auditable;

@Local
public interface HistoryLocal {

    public void write(Auditable aud, String operation);
}
