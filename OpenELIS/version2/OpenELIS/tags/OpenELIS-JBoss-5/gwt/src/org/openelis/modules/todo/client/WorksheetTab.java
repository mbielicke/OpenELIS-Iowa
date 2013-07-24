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
package org.openelis.modules.todo.client;

import java.util.ArrayList;
import java.util.Collections;

import org.openelis.cache.UserCache;
import org.openelis.domain.WorksheetCacheVO;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.SystemUserPermission;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.ScreenWindowInt;
import org.openelis.gwt.widget.table.ColumnComparator;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.gwt.widget.table.event.SortEvent.SortDirection;

import com.google.gwt.user.client.Window;

public class WorksheetTab extends Screen {
            
    private boolean                     loadedFromCache;
    private String                      loadBySection;  
    private ArrayList<WorksheetCacheVO> fullList;
    private TableWidget                 table;
    
    public WorksheetTab(ScreenDefInt def, ScreenWindowInt window) {
        setDefinition(def);
        setWindow(window);
        service = new ScreenService("controller?service=org.openelis.modules.todo.server.ToDoService");        
        initialize();        
    }
    
    private void initialize() {                
        table = (TableWidget)def.getWidget("worksheetTable");
        addScreenHandler(table, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                table.load(getTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                table.enable(true);
            }
        });
        
        table.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {                                 
                event.cancel();
            }            
        });
           
        loadBySection = "N";
    }
    
    private ArrayList<TableDataRow> getTableModel() {
        boolean sectOnly;
        String sectName;
        TableDataRow row;
        ArrayList<TableDataRow> model;
        SystemUserPermission perm;
        
        model = new ArrayList<TableDataRow>();

        try {            
            perm = UserCache.getPermission(); 
            if (!loadedFromCache) {
                window.setBusy(consts.get("fetching"));
                fullList = service.callList("getWorksheet");
                window.clearStatus();
            }
            sectOnly = "Y".equals(loadBySection);
            for (WorksheetCacheVO data: fullList) {
                sectName = data.getSectionName();                
                if (sectOnly && perm.getSection(sectName) == null)
                    continue;                
                row = new TableDataRow(6);                
                row.cells.get(0).setValue(data.getId());
                row.cells.get(1).setValue(data.getSystemUserName());
                row.cells.get(2).setValue(sectName);
                row.cells.get(3).setValue(data.getTestName());
                row.cells.get(4).setValue(data.getTestMethodName());               
                row.cells.get(5).setValue(data.getCreatedDate());                
                row.data = data;
                model.add(row);
            }
            Collections.sort(model,new ColumnComparator(0, SortDirection.ASCENDING));
        } catch (NotFoundException e) {
            window.clearStatus();
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
            window.clearStatus();
        }        
        return model;
    }
    
    public void reloadFromCache() {
        loadedFromCache = false;
    }
    
    public Integer getSelectedId() {
        TableDataRow row;
        WorksheetCacheVO data; 
        
        row = table.getSelection();
        if (row == null)
            return null;
        
        data = (WorksheetCacheVO)row.data;        
        return data.getId();
    }
    
    public void draw(String loadBySection) {        
        if (!loadedFromCache || !loadBySection.equals(this.loadBySection)) {             
            this.loadBySection = loadBySection;
            DataChangeEvent.fire(this);                
        }
        loadedFromCache = true;
    }
}