package org.openelis.remote;

import javax.ejb.Remote;

import org.openelis.manager.NoteManager;

@Remote
public interface NoteManagerRemote {
    public NoteManager fetchByRefTableRefId(Integer refTableId, Integer refId) throws Exception;
}
