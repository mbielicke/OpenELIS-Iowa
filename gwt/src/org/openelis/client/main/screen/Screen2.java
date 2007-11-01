package org.openelis.client.main.screen;

import org.openelis.gwt.client.screen.Screen;

import com.google.gwt.user.client.ui.Widget;

public class Screen2 extends Screen {
	public Screen2() {
        super("screen2");
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
