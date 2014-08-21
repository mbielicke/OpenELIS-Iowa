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
package org.openelis.modules.sample.client;

import java.util.ArrayList;

import org.openelis.domain.DictionaryDO;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.widget.table.Row;
import org.openelis.gwt.widget.table.Table;
import org.openelis.utilcommon.ResultValidator;
import org.openelis.utilcommon.ResultValidator.OptionItem;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.user.client.Window;

public class ResultSuggestionsScreen extends Screen {
    protected Table suggestionsTable;
    
    protected ResultValidator resultValidator;
    protected Integer unitId;
     
     public ResultSuggestionsScreen() throws Exception {
         super((ScreenDefInt)GWT.create(ResultSuggestionsDef.class));

         // Setup link between Screen and widget Handlers
         initialize();

         // Initialize Screen
         setState(State.DEFAULT);
     }

     private void initialize() {
         suggestionsTable = (Table)def.getWidget("suggestionsTable");
         addScreenHandler(suggestionsTable, new ScreenEventHandler<ArrayList<Row>>() {
             public void onDataChange(DataChangeEvent event) {
                 suggestionsTable.setModel(getTableModel());
             }
             
             public void onStateChange(StateChangeEvent<State> event) {
                 suggestionsTable.setEnabled(true);
             }
         });
         
         suggestionsTable.addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>(){
            public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {
                //do nothing
            } 
         });
     }
     
     private ArrayList<Row> getTableModel(){
        Row row;
        String message;
        ArrayList<Row> model;
        ArrayList<OptionItem> suggestions;
        DictionaryDO data;

        model = new ArrayList<Row>();
        row = null;
        suggestions = resultValidator.getRanges(unitId);
        try {
            for (OptionItem o : suggestions) {
                message = consts.get(o.getProperty());
                message = message.replace("{0}", o.getValue());                   
                row = new Row(message, message);
                model.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert(e.getMessage());            
        }

        return model;
     }
     
     public void setValidator(ResultValidator resultValidator, Integer unitId) {
         this.resultValidator = resultValidator;
         this.unitId = unitId;
         
         DataChangeEvent.fire(this);
     }
 }
