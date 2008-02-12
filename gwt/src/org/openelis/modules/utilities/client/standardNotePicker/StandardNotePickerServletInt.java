package org.openelis.modules.utilities.client.standardNotePicker;

import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.gwt.widget.pagedtree.TreeModel;

public interface StandardNotePickerServletInt extends AppScreenFormServiceInt {

	public TreeModel getTreeModel();
	
	public String getTreeModelSecondLevel(int id);
}
