/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
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
