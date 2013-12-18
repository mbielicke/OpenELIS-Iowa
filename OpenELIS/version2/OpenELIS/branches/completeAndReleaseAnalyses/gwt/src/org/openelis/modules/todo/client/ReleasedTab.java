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
import java.util.Date;
import java.util.List;

import org.openelis.cache.CategoryCache;
import org.openelis.cache.UserCache;
import org.openelis.constants.Messages;
import org.openelis.domain.AnalysisViewVO;
import org.openelis.domain.DictionaryDO;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.SystemUserPermission;
import org.openelis.ui.widget.WindowInt;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.ScreenWindowInt;
import org.openelis.gwt.widget.table.ColumnComparator;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.gwt.widget.table.event.SortEvent.SortDirection;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ReleasedTab extends Screen {
            
    private boolean                    loadedFromCache;
    private String                     loadBySection;  
    private ArrayList<AnalysisViewVO> fullList;
    private TableWidget                table;
    
    public ReleasedTab(ScreenDefInt def, WindowInt window) {
        setDefinition(def);
        setWindow(window);
        initialize();        
        initializeDropdowns();
    }
    
    private void initialize() {                
        table = (TableWidget)def.getWidget("releasedTable");
        addScreenHandler(table, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                loadTableModel();
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
    
    private void initializeDropdowns() {
        ArrayList<TableDataRow> model;
        TableDataRow row;
        List<DictionaryDO> list;
        Dropdown<Integer> domain;
        
        model = new ArrayList<TableDataRow>();
        try {
            list = CategoryCache.getBySystemName("sample_domain");
            for (DictionaryDO data : list) {
                row = new TableDataRow(data.getCode(), data.getEntry());
                model.add(row);
            }            
            domain = ((Dropdown<Integer>)table.getColumnWidget("domain"));
            domain.setModel(model);
        } catch (Exception e) {
            Window.alert(e.getMessage());
            window.close();
        }
    }
    
    private void loadTableModel() {
        ArrayList<TableDataRow> model;
        
        if (loadedFromCache) {
            model = getTableModel();
            Collections.sort(model, new ColumnComparator(0, SortDirection.ASCENDING));
            table.load(model);
        } else {
            window.setBusy(Messages.get().fetching());
            ToDoService.get().getReleased(new AsyncCallback<ArrayList<AnalysisViewVO>>() {
                public void onSuccess(ArrayList<AnalysisViewVO> result) {
                    ArrayList<TableDataRow> model;         
                    
                    fullList = result;
                    model = getTableModel();
                    Collections.sort(model, new ColumnComparator(0, SortDirection.ASCENDING));
                    table.load(model);
                    window.clearStatus();
                }

                public void onFailure(Throwable error) {
                    if (error instanceof NotFoundException) {
                        window.setDone(Messages.get().noRecordsFound());
                    } else {
                        Window.alert(error.getMessage());
                        error.printStackTrace();
                        window.clearStatus();
                    }

                }
            });
        }
    }
    
    private ArrayList<TableDataRow> getTableModel() {
        boolean sectOnly;
        String sectName;
        TableDataRow row;
        ArrayList<TableDataRow> model;
        Datetime scd, sct;
        Date temp;
        SystemUserPermission perm;

        model = new ArrayList<TableDataRow>();
        perm = UserCache.getPermission();
        sectOnly = "Y".equals(loadBySection);

        try {
            for (AnalysisViewVO data : fullList) {
                sectName = data.getSectionName();
                if (sectOnly && perm.getSection(sectName) == null)
                    continue;
                row = new TableDataRow(10);
                row.cells.get(0).setValue(data.getAccessionNumber());
                row.cells.get(1).setValue(data.getDomain());
                row.cells.get(2).setValue(sectName);
                row.cells.get(3).setValue(data.getTestName());
                row.cells.get(4).setValue(data.getMethodName());

                scd = data.getCollectionDate();
                sct = data.getCollectionTime();
                if (scd != null) {
                    temp = scd.getDate();
                    if (sct == null) {
                        temp.setHours(0);
                        temp.setMinutes(0);
                    } else {
                        temp.setHours(sct.getDate().getHours());
                        temp.setMinutes(sct.getDate().getMinutes());
                    }

                    row.cells.get(5).setValue(Datetime.getInstance(Datetime.YEAR, Datetime.MINUTE,
                                                                   temp));
                }
                row.cells.get(6).setValue(data.getReleasedDate());
                row.cells.get(7).setValue(data.getAnalysisResultOverride());
                row.cells.get(8).setValue(data.getToDoDescription());
                row.cells.get(9).setValue(data.getPrimaryOrganizationName());
                row.data = data;
                model.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
        }

        return model;
    }
    
    public void reloadFromCache() {
        loadedFromCache = false;
    }
    
    public Integer getSelectedId() {
        TableDataRow row;
        AnalysisViewVO data; 
        
        row = table.getSelection();
        if (row == null)
            return null;
        
        data = (AnalysisViewVO)row.data;        
        return data.getSampleId();
    }
    
    public void draw(String loadBySection) {        
        if (!loadedFromCache || !loadBySection.equals(this.loadBySection)) {             
            this.loadBySection = loadBySection;
            DataChangeEvent.fire(this);                
        }
        loadedFromCache = true;
    }
}