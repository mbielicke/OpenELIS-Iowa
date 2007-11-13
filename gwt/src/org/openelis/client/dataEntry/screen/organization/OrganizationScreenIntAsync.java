package org.openelis.client.dataEntry.screen.organization;

import org.openelis.gwt.client.services.AppScreenFormServiceIntAsync;
import org.openelis.gwt.common.TableModel;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface OrganizationScreenIntAsync extends AppScreenFormServiceIntAsync {

	public void getInitialModel(TableModel model, AsyncCallback callback);
	
	public void getOrganizationByLetter(String letter, TableModel tableModel, AsyncCallback callback);
	
}
