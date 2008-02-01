package org.openelis.client.dataEntry.screen.organization;

import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.Timer;

import org.openelis.client.main.OpenELIS;


public class TestOrganization extends GWTTestCase {

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
    
    public void testOrganizationScreen() {
        System.out.println("Creating org screen");
        final OrganizationScreen orgScreen = new OrganizationScreen();
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
