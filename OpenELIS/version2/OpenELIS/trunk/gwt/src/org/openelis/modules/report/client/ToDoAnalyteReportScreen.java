/**
 * Exhibit A - UIRF Open-source Based Public Software License.
 * 
 * The contents of this file are subject to the UIRF Open-source Based Public
 * Software License(the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * openelis.uhl.uiowa.edu
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * The Initial Developer of the Original Code is The University of Iowa.
 * Portions created by The University of Iowa are Copyright 2006-2008. All
 * Rights Reserved.
 * 
 * Contributor(s): ______________________________________.
 * 
 * Alternatively, the contents of this file marked "Separately-Licensed" may be
 * used under the terms of a UIRF Software license ("UIRF Software License"), in
 * which case the provisions of a UIRF Software License are applicable instead
 * of those above.
 */
package org.openelis.modules.report.client;

import java.util.ArrayList;

import org.openelis.domain.Constants;
import org.openelis.gwt.screen.ScreenDef;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.Dropdown;

import com.google.gwt.user.client.Window;

public class ToDoAnalyteReportScreen extends ReportScreen {
    private ArrayList<Object> selections;
    
    public ToDoAnalyteReportScreen() throws Exception {
        Integer id;
        
        drawScreen(new ScreenDef());
        setName(consts.get("toDoAnalyteReport"));
        service = new ScreenService("controller?service=org.openelis.modules.report.server.ToDoAnalyteReportService");
        
        selections = new ArrayList<Object>();
        /* 
         * The Analysis Status drop down in the screen needs to have some statuses set by default. 
         * The following list contains those options. After the dropdown is initialized, this list is set as the selection.
         */
        try {
            id = Constants.dictionary().ANALYSIS_LOGGED_IN;
            if (id != null)
                selections.add(id.toString());
            id = Constants.dictionary().ANALYSIS_INITIATED;
            if (id != null)
                selections.add(id.toString());
            id = Constants.dictionary().ANALYSIS_INPREP;
            if (id != null)
                selections.add(id.toString());
            id = Constants.dictionary().ANALYSIS_REQUEUE;
            if (id != null)
                selections.add(id.toString());
        } catch (Exception e) {
            Window.alert(e.getMessage());
            window.close();
        }
        
    }
    
    protected void initialize () {
        Dropdown<Integer> status; 
       
        super.initialize();
        status = (Dropdown)def.getWidget("STATUS");
        status.setSelectionKeys(selections);
    } 
}