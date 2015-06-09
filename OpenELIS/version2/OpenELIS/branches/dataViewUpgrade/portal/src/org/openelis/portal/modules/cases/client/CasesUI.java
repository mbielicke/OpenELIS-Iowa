package org.openelis.portal.modules.cases.client;

import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.table.Table;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.IsWidget;

public interface CasesUI extends IsWidget {
	
	Table getCases();
	Dropdown<Integer> tags();
	Table tagTable();

}
