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
package org.openelis.modules.quickEntry.client;

import org.openelis.gwt.common.SecurityModule;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.manager.SampleManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;

public class QuickEntryScreen extends Screen {
    
    private SecurityModule                 security;
    private SampleManager                  manager;

    public QuickEntryScreen() throws Exception {
        // Call base to get ScreenDef and draw screen
        super((ScreenDefInt)GWT.create(QuickEntryDef.class));
        //TODO not sure yet service = new ScreenService("controller?service=org.openelis.modules.sample.server.SampleService");

        //security = OpenELIS.security.getModule("quickEntry");
        
        //if (security == null)
        //    throw new SecurityException("screenPermException", "Quick Entry Screen");

        DeferredCommand.addCommand(new Command() {
            public void execute() {
                postConstructor();
            }
        });
    }
    
    private void postConstructor() {
        /*tab = Tabs.SAMPLE_ITEM;
        manager = SampleManager.getInstance();
        manager.getSample().setDomain(SampleManager.ENVIRONMENTAL_DOMAIN_FLAG);

        try {
            DictionaryCache.preloadByCategorySystemNames("sample_status", "analysis_status",
                                                         "type_of_sample", "source_of_sample",
                                                         "sample_container", "unit_of_measure",
                                                         "qaevent_type", "aux_field_value_type",
                                                         "organization_type");
        } catch (Exception e) {
            Window.alert(e.getMessage());
            window.close();
        }
*/
        initialize();
        initializeDropdowns();
        setState(State.DEFAULT);
        DataChangeEvent.fire(this);
    }

    private void initialize() {
        
    }
    
    private void initializeDropdowns() {
        
    }
}
