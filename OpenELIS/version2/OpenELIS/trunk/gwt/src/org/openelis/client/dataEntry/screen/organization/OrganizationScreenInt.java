package org.openelis.client.dataEntry.screen.organization;

import org.openelis.gwt.client.services.AppScreenFormServiceInt;
import org.openelis.gwt.common.TableModel;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface OrganizationScreenInt extends AppScreenFormServiceInt {

	public TableModel getInitialModel(TableModel model);
	
	public TableModel getOrganizationByLetter(String letter, TableModel tableModel);
}
