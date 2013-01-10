package org.openelis.remote;

import javax.ejb.Remote;

import org.openelis.manager.NoteManager;

@Remote
public interface NoteManagerRemote {
    public NoteManager fetchByRefTableRefIdIsExt(Integer refTableId, Integer refId, String isExternal) throws Exception;
}
