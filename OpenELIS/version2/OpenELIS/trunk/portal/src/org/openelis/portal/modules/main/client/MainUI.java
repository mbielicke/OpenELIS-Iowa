package org.openelis.portal.modules.main.client;

import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Panel;

public interface MainUI extends IsWidget {
	
	Panel main();
	Navigation navigation();
	FocusPanel logo();
	
}
