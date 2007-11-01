package org.openelis.client.dataEntry.screen;

import org.openelis.gwt.client.screen.ScreenForm;
import org.openelis.gwt.client.widget.ButtonPanel;

import com.google.gwt.user.client.ui.Widget;

public class OrganizeFavoritesForm extends ScreenForm {
	

	public OrganizeFavoritesForm() {
        super("organizeFavorites");
        rpc.action = "organizeFavoritesForm";
    }
	
	public void onClick(Widget sender) {
		if (sender == widgets.get("addButton")) {
			new OrganizeFavoritesAddForm();
		}
    }
	
	public void afterSubmit(String method, boolean success) {
        if (method.equals("draw")) {	        	
        	
        	bpanel = (ButtonPanel)getWidget("buttons");
        	bpanel.enable("cb", true);
        	message.setText("done");
        }
	}
}
