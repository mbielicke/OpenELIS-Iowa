package org.openelis.modules.note.client;

import org.openelis.gwt.common.data.Query;
import org.openelis.manager.NoteManager;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface NoteServiceIntAsync {

    void fetchByRefTableRefIdIsExt(Query query, AsyncCallback<NoteManager> callback);

}
