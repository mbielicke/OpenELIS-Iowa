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
import org.openelis.domain.AnalysisCacheVO;
import org.openelis.domain.DictionaryDO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.SystemUserPermission;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.services.ScreenService;
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

public class OtherTab extends Screen {
            
    private boolean                    loadedFromCache;
    private String                     loadBySection;
    private ArrayList<AnalysisCacheVO> fullList;
    private TableWidget                table;
    
    public OtherTab(ScreenDefInt def, ScreenWindowInt window) {
        setDefinition(def);
        setWindow(window);
        service = new ScreenService("controller?service=org.openelis.modules.todo.server.ToDoService");        
        initialize();        
        initializeDropdowns();        
    }
    
    private void initialize() {                
        table = (TableWidget)def.getWidget("otherTable");
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
        Dropdown<Integer> status;
        
        model = new ArrayList<TableDataRow>();
        try {
            list = CategoryCache.getBySystemName("analysis_status");
            for (DictionaryDO data : list) {
                row = new TableDataRow(data.getId(), data.getEntry());
                model.add(row);
            }            
            status = ((Dropdown<Integer>)table.getColumnWidget("status"));
            status.setModel(model);
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
            window.setBusy(consts.get("fetching"));
            service.callList("getOther", new AsyncCallback<ArrayList<AnalysisCacheVO>>() {
                public void onSuccess(ArrayList<AnalysisCacheVO> result) {
                    ArrayList<TableDataRow> model;         
                    
                    fullList = result;
                    model = getTableModel();
                    Collections.sort(model, new ColumnComparator(0, SortDirection.ASCENDING));
                    table.load(model);
                    window.clearStatus();
                }

                public void onFailure(Throwable error) {
                    if (error instanceof NotFoundException) {
                        window.setDone(consts.get("noRecordsFound"));
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
        Integer priority;
        String domain, sectName, project;
        TableDataRow row;
        ArrayList<TableDataRow> model;
        Datetime scd, sct;
        Date temp;
        SystemUserPermission perm;

        model = new ArrayList<TableDataRow>();
        perm = UserCache.getPermission();
        sectOnly = "Y".equals(loadBySection);

        for (AnalysisCacheVO data : fullList) {
            sectName = data.getSectionName();
            if (sectOnly && perm.getSection(sectName) == null)
                continue;
            row = new TableDataRow(11);
            row.cells.get(0).setValue(data.getSampleAccessionNumber());
            row.cells.get(1).setValue(data.getSampleDomain());
            row.cells.get(2).setValue(sectName);
            row.cells.get(3).setValue(data.getStatusId());
            row.cells.get(4).setValue(data.getTestName());
            row.cells.get(5).setValue(data.getTestMethodName());

            scd = data.getSampleCollectionDate();
            sct = data.getSampleCollectionTime();
            if (scd != null) {
                temp = scd.getDate();
                if (sct == null) {
                    temp.setHours(0);
                    temp.setMinutes(0);
                } else {
                    temp.setHours(sct.getDate().getHours());
                    temp.setMinutes(sct.getDate().getMinutes());
                }

                row.cells.get(6)
                         .setValue(Datetime.getInstance(Datetime.YEAR, Datetime.MINUTE, temp));
            }
            row.cells.get(7).setValue(data.getSampleReceivedDate());
            if ("Y".equals(data.getAnalysisQaeventResultOverride()) ||
                "Y".equals(data.getSampleQaeventResultOverride()))
                row.cells.get(8).setValue("Y");
            else
                row.cells.get(8).setValue("N");

            domain = data.getSampleDomain();
            if ("E".equals(domain)) {
                priority = data.getSampleEnvironmentalPriority();
                project = data.getSampleProjectName();
                if (priority == null) {
                    if (project != null)
                        row.cells.get(9).setValue(project);
                } else {
                    if (project == null)
                        row.cells.get(9).setValue(priority);
                    else
                        row.cells.get(9).setValue(priority + ", " + project);
                }
            } else if ("W".equals(domain)) {
                row.cells.get(9).setValue(data.getSamplePrivateWellOwner());
            } else if ("S".equals(domain)) {
                row.cells.get(9).setValue(data.getSampleSDWISPWSName());
            }

            row.cells.get(10).setValue(data.getSampleReportToName());
            row.data = data;
            model.add(row);
        }

        return model;
    }
    
    public void reloadFromCache() {
        loadedFromCache = false;
    }
    
    public Integer getSelectedId() {
        TableDataRow row;
        AnalysisCacheVO data; 
        
        row = table.getSelection();
        if (row == null)
            return null;
        
        data = (AnalysisCacheVO)row.data;        
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