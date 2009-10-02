package org.openelis.web.modules.main.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

public class OpenELISWeb implements EntryPoint {

	public void onModuleLoad() {
		 try {
			  RootPanel.get("main").add(new OpenELISWebScreen());
		      }catch(Throwable e){
		    	  e.printStackTrace();
		    	  Window.alert("Unable to start app : "+e.getMessage());
		      }
		
	}

}
