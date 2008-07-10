/**
* The contents of this file are subject to the Mozilla Public License
* Version 1.1 (the "License"); you may not use this file except in
* compliance with the License. You may obtain a copy of the License at
* http://www.mozilla.org/MPL/
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations under
* the License.
* 
* The Original Code is OpenELIS code.
* 
* Copyright (C) The University of Iowa.  All Rights Reserved.
*/
package org.openelis.junit.client;

import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Widget;

import org.openelis.modules.main.client.OpenELIS;
import org.openelis.modules.organization.client.OrganizationScreen;


public class TestOrganization extends GWTTestCase {

    static OrganizationScreen orgScreen;
    
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
        orgScreen = new OrganizationScreen();
        Timer timer = new Timer() {
            public void run() {
                assertNotNull(orgScreen.forms.get("display"));
                finishTest();
            }
        }; 
        assertNotNull(orgScreen);
        delayTestFinish(3000);
        timer.schedule(2000);
        
    }
    
    public void testLetterClick() {
        Timer timer = new Timer() {
            public void run() {
                assertNotNull(orgScreen.modelWidget.getModel());
                finishTest();
            }
        };
        assertNotNull(orgScreen);
        orgScreen.onClick((Widget)orgScreen.widgets.get("a"));
        delayTestFinish(5000);
        timer.schedule(3000);
    }

}
