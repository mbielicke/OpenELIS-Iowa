package org.openelis.client.dataEntry.screen.organization;

import org.openelis.gwt.client.services.AppScreenFormServiceInt;
import org.openelis.gwt.client.widget.pagedtree.TreeModel;
import org.openelis.gwt.common.AbstractField;
import org.openelis.gwt.common.TableModel;

public interface OrganizationScreenInt extends AppScreenFormServiceInt {

	public TableModel getInitialModel(TableModel model);
	
	public TableModel getOrganizationByLetter(String letter, TableModel tableModel);
	
	public TreeModel getNoteTreeModel(AbstractField key, boolean topLevel);
	
	public String getNoteTreeSecondLevelXml(String key, boolean topLevel);
}
