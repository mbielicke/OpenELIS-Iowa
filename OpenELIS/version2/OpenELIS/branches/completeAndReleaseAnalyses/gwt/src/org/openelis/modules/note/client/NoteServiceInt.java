package org.openelis.modules.note.client;

import org.openelis.manager.NoteManager;
import org.openelis.ui.common.data.Query;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

@RemoteServiceRelativePath("note")
public interface NoteServiceInt extends XsrfProtectedService {

    NoteManager fetchByRefTableRefIdIsExt(Query query) throws Exception;

}