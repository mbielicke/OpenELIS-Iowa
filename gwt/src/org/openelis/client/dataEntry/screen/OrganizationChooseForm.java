package org.openelis.client.dataEntry.screen;

import org.openelis.gwt.client.screen.Screen;
import org.openelis.gwt.client.widget.PopupWindow;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

public class OrganizationChooseForm extends Screen {
	
public PopupWindow window;
	
	public OrganizationChooseForm() {
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
