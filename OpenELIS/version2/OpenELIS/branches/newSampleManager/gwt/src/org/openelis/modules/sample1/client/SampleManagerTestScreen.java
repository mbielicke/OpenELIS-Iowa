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
package org.openelis.modules.sample1.client;


import java.util.ArrayList;

import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.TextBox;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.SampleManager1.Load;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.Window;

public class SampleManagerTestScreen extends Screen {

    
    protected AppButton        testButton;
    protected TextBox<Integer> numSamples;
    protected ArrayList<SampleManager1.Load> elements;
    
    public SampleManagerTestScreen() throws Exception {
       //super((ScreenDefInt)GWT.create(SampleManagerTestDef.class));

       initialize();
    }

    private void initialize() {
        numSamples = (TextBox<Integer>)def.getWidget("numSamples");
        numSamples.enable(true);
        
        testButton = (AppButton)def.getWidget("testButton");
        testButton.enable(true);
        addScreenHandler(testButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                test();
            }
        });
    }
    
    private void test() {   
        int i, num;
        SampleManager1 s;
        ArrayList<SampleManager1> samples;
        ArrayList<Integer> ids;
        
        if (DataBaseUtil.isEmpty(numSamples.getText()) || numSamples.getExceptions()!= null)            
            return;
        
        try {
            num = Integer.parseInt(numSamples.getText());
            ids = new ArrayList<Integer>();
            for (i = 1; i <= num; i++) 
                ids.add(i);
            /*ids.add(100);
            ids.add(101);
            ids.add(11876);
            ids.add(11883);*/
                      
            //window.setBusy(consts.get("fetching"));
            
            SampleManager1.Load elements[] = {Load.ORGANIZATION, Load.PROJECT, Load.QA,
                                              Load.AUXDATA, Load.STORAGE, Load.NOTE,
                                              Load.ANALYSISUSER, Load.RESULT};
            
            samples = SampleService1.get().fetchForUpdate(ids, elements);
            System.out.println("samples fetched ");
            for (i = 0; i < samples.size(); i++) {
                s = samples.get(i);
                if (s.organization == null)
                    System.out.println("organization is null for: "+ s.getSample().getId());     
                if (s.project == null)
                    System.out.println("project is null for: "+ s.getSample().getId());
                if (s.qaEvent == null)
                    System.out.println("qaEvent is null for: "+ s.getSample().getId());
                if (s.auxData == null)
                    System.out.println("aux data is null for: "+ s.getSample().getId());
            }
            window.setDone("fetched " + samples.size()+ " samples");
        } catch(Exception e) {            
            Window.alert(e.getMessage());
            window.clearStatus();
        }
    }
}
