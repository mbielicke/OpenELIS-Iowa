package org.openelis.client.supply.screen.storage;

import org.openelis.gwt.client.screen.AppScreenForm;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Widget;

public class StorageScreen extends AppScreenForm{

	private static StorageServletIntAsync screenService = (StorageServletIntAsync) GWT
	.create(StorageServletInt.class);
	
	private static ServiceDefTarget target = (ServiceDefTarget) screenService;
	
	public StorageScreen() {
		super();
		String base = GWT.getModuleBaseURL();
		base += "StorageServlet";
		target.setServiceEntryPoint(base);
		service = screenService;
		formService = screenService;
		getXML();
	}

}
