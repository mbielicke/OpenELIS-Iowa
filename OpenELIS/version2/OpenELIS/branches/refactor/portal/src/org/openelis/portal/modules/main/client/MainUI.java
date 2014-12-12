package org.openelis.portal.modules.main.client;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;

public interface MainUI extends IsWidget {
	
	Panel main();
	Navigation navigation();
	
}
