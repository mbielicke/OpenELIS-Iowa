package org.openelis.modules.utilities.client.standardNotePicker;

import org.openelis.gwt.services.AppScreenFormServiceIntAsync;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface StandardNotePickerServletIntAsync extends AppScreenFormServiceIntAsync{

	public void getTreeModel(Integer key, boolean topLevel, AsyncCallback callback);
}
