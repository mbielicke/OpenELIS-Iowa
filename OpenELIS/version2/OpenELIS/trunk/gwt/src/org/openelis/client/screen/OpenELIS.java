package org.openelis.client.screen;

import org.openelis.client.service.OpenELISService;
import org.openelis.gwt.client.screen.Screen;
import org.openelis.gwt.client.screen.ScreenMenuPanel;
import org.openelis.gwt.client.widget.WindowBrowser;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

public class OpenELIS extends Screen {

    
	public OpenELIS() {
        super("OpenELIS.xml");
        rpc.action = "OpenELIS";
        getXML();
	}
	
	public OpenELIS(String xml) {
        super(xml+".xml");
        rpc.action = xml;
        getXML();
	}
	
    public void afterSubmit(String method, boolean Success) {

        if (method.equals("draw")) {
            OpenELISService.getInstance().getMenuList(new AsyncCallback() {
                public void onSuccess(Object result) {
                    try {
                        ((ScreenMenuPanel)widgets.get("menu")).load((String)result);
                    } catch (Exception e) {
                        Window.alert(e.getMessage());
                    }
                }

                public void onFailure(Throwable caught) {
                    Window.alert(caught.getMessage());
                }
            });
          
        }
    }

    public void onClick(Widget item) {
        WindowBrowser browser = (WindowBrowser)getWidget("browser");
        if(item == widgets.get("testScreen")){
            if(!browser.selectScreen("Test Screen"))
                browser.addScreen(new TestScreen(), "Test Screen", "testScreen");
        }
	
    }
}
