package org.openelis.client.utilities.screen;

import org.openelis.gwt.client.screen.ScreenForm;

public class TestScreen extends ScreenForm {
    
    public TestScreen() {
        super("TestScreen.xml");
        rpc.action = "TestScreen";
    }
    
    public void afterSubmit(String method, boolean Success) {
        if(method.equals("draw"))
            message.setText("Done");
    }

}
