package org.openelis.remote;

import javax.ejb.Remote;

import org.openelis.manager.NotesManager;

@Remote
public interface NoteManagerRemote {
    public NotesManager update(NotesManager man) throws Exception;
    public NotesManager add(NotesManager man) throws Exception;
    public NotesManager fetch(Integer referenceTableId, Integer referenceId) throws Exception;
}
