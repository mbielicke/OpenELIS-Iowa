package org.openelis.portal.modules.cases.client;

import org.openelis.portal.client.resources.Resources;
import org.openelis.ui.resources.TableCSS;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;

public class CaseHistoryTable extends Composite {
	
	FlexTable table;
	TableCSS css;
	
	public CaseHistoryTable() {
		table = new FlexTable();
		initWidget(table);
		
		css = Resources.INSTANCE.table();
		css.ensureInjected();
	}
	
	

}
