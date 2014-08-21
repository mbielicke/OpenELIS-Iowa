package org.openelis.modules.organization.client;

import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.widget.PopupWindow;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

public class OrganizationChoose extends Screen {
	
public PopupWindow window;
	
	public OrganizationChoose() {
        super("organizationChoose.xml");
        rpc.action = "OrganizationChooseForm";
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
        	window = new PopupWindow("Organization Choose");
            window.content.add(this);
            window.content.setStyleName("Content");
            window.setContentPanel(window.content);
            
            DeferredCommand.addCommand(new Command() {
                public void execute() {
                    window.setVisible(true);
                    window.setPopupPosition((Window.getClientWidth() - window.getOffsetWidth())/2,100);
                    window.size();
                }
            });
        }
	}

}
