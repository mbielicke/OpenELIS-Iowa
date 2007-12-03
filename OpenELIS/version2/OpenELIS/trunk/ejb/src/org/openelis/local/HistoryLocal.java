package org.openelis.local;

import javax.ejb.Local;

import org.openelis.utils.Auditable;

@Local
public interface HistoryLocal {

    public void write(Auditable aud, Integer operation, String changes);
}
