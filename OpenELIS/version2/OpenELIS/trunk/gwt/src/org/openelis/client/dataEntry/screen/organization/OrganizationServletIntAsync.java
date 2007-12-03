package org.openelis.client.dataEntry.screen.organization;

import org.openelis.gwt.client.services.AppScreenFormServiceIntAsync;
import org.openelis.gwt.common.AbstractField;
import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.TableModel;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface OrganizationServletIntAsync extends AppScreenFormServiceIntAsync {

	//public void getInitialModel(TableModel model, AsyncCallback callback);
	
	public void getOrganizationByLetter(String letter, TableModel tableModel, FormRPC letterRPC, AsyncCallback callback);
	
	public void getNoteTreeModel(AbstractField key, boolean topLevel, AsyncCallback callback);
	
	public void getNoteTreeSecondLevelXml(String key, boolean topLevel, AsyncCallback callback);
	
}
