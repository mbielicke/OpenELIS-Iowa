package org.openelis.modules.dataEntry.client.organization;

import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.TableModel;
import org.openelis.gwt.services.AppScreenFormServiceInt;

public interface OrganizationServletInt extends AppScreenFormServiceInt {

	//public TableModel getInitialModel(TableModel model);
	
	public TableModel getOrganizationByLetter(String letter, TableModel tableModel, FormRPC letterRPC);
	
	public DataModel getNotesModel(Integer key);
	
	public DataModel getInitialModel(String cat);
}
