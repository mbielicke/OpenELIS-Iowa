package org.openelis.junit.client;

import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.Timer;

import org.openelis.modules.main.client.OpenELIS;
import org.openelis.modules.provider.client.ProviderScreen;


public class TestProvider extends GWTTestCase {

    public String getModuleName() {
        // TODO Auto-generated method stub
        return "org.openelis.OpenELIS";
    }
    
    public void testInit() {
        try {
            new OpenELIS().setWidgetMap();
            assertTrue(true);
        }catch(Exception e) {
            fail(e.getMessage());
        }
    }
    
    public void testProviderScreen() {
        final ProviderScreen orgScreen = new ProviderScreen();
        Timer timer = new Timer() {
            public void run() {
                assertNotNull(orgScreen.forms.get("display"));
                finishTest();
            }
        }; 
        assertNotNull(orgScreen);
        delayTestFinish(2000);
        timer.schedule(1500);
    }

}
