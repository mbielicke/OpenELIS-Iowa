package org.openelis.client.main.screen;

import org.openelis.gwt.client.screen.Screen;

import com.google.gwt.user.client.ui.Widget;

public class Screen1 extends Screen {

	public Screen1() {
        super("screen1.xml");
        rpc.action = "Screen1Form";
        getXML();
    }
	
	public void onClick(Widget sender) {
		/*if (sender == inputs.get("okButton")) {
			window.close();
		}else if (sender == inputs.get("cancelButton")) {
			window.close();
		}*/
    }
	
	public void afterSubmit(String method, boolean success) {
        if (method.equals("draw")) {	        	
        	
        }
	}
}
