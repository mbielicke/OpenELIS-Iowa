package org.openelis.modules.utilities.client.standardNote;

import org.openelis.gwt.services.AppScreenFormServiceIntAsync;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface StandardNoteServletIntAsync extends AppScreenFormServiceIntAsync {

	public void getInitialModel(String cat, AsyncCallback callback);
}
