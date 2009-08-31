package org.openelis.remote;

import javax.ejb.Remote;

import org.openelis.manager.NoteManager;

@Remote
public interface NoteManagerRemote {
    public NoteManager fetch(Integer referenceTableId, Integer referenceId) throws Exception;
}
