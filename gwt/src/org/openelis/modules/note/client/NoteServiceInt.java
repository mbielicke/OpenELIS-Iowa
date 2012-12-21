package org.openelis.modules.note.client;

import org.openelis.gwt.common.data.Query;
import org.openelis.manager.NoteManager;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("note")
public interface NoteServiceInt extends RemoteService {

    NoteManager fetchByRefTableRefIdIsExt(Query query) throws Exception;

}