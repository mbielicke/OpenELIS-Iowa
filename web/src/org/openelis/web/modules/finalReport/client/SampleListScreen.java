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
package org.openelis.web.modules.finalReport.client;

import java.util.ArrayList;
import java.util.Date;

import org.openelis.cache.CategoryCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.SampleEnvironmentalWebVO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.ReportStatus;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.CheckBox;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;

public class SampleListScreen extends Screen {

    private CheckBox                 selectAll;
    private TableWidget              sampleEntTable;
    private AppButton                runReportButton, resetButton;
    private ArrayList<SampleEnvironmentalWebVO> results;
    private String                   organizationIds;

    /**
     * No-Arg constructor
     */
    public SampleListScreen() {
        super((ScreenDefInt)GWT.create(SampleListDef.class));
        service = new ScreenService("controller?service=org.openelis.modules.report.server.FinalReportService");
        initialize();
        setState(State.DEFAULT);
        initializeDropdowns();
    }

    /**
     * Initialize widgets
     */
    private void initialize() {  
        
        sampleEntTable = (TableWidget)def.getWidget("sampleEntTable");
        
        addScreenHandler(sampleEntTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                sampleEntTable.load(getTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sampleEntTable.enable(true);
            }
        });
        
        sampleEntTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
               if (event.getCol() > 0)
                    event.cancel();
            }
        });
        
        selectAll = (CheckBox)def.getWidget("selectAll");
        addScreenHandler(selectAll, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                selectAll.setValue("N");
            }
            
            public void onValueChange(ValueChangeEvent<String> event) {
                String val;
                
                val = selectAll.getValue();
                for(int i = 0;i< sampleEntTable.numRows();i++)
                    sampleEntTable.setCell(i, 0, val);                
            }
        });

        runReportButton = (AppButton)def.getWidget("runReportButton");
        addScreenHandler(runReportButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                runReport();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                runReportButton.enable(true);
            }
        });

        resetButton = (AppButton)def.getWidget("resetButton");
        addScreenHandler(resetButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                reset();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                resetButton.enable(true);
            }
        });
    }
    
    private void initializeDropdowns() {
        ArrayList<TableDataRow> model;
        ArrayList<DictionaryDO> list;
        TableDataRow row;

        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        list = CategoryCache.getBySystemName("sample_status");
        for (DictionaryDO d : list) {         
            row = new TableDataRow(d.getId(), d.getEntry());
            row.enabled = ("Y".equals(d.getIsActive()));
            model.add(row);
        }
        ((Dropdown<Integer>)(sampleEntTable.getColumnWidget("status"))).setModel(model);
    }
    
    public void setResults(ArrayList<SampleEnvironmentalWebVO> results) {
        this.results = results;
        DataChangeEvent.fire(this);
    }
    
    protected void setOrganization(String s){
        organizationIds = s;
    }
    
    protected String getOrganization(){
        return organizationIds;
    }

    protected void runReport() {
        Query query;
        String value;
        QueryData field;
        TableDataRow row;
        ReportStatus st;
        String url, val;

        query = new Query();
        field = new QueryData();
        sampleEntTable = (TableWidget)def.getWidget("sampleEntTable");
        for (int i = 0; i < sampleEntTable.numRows(); i++ ) {
            row = sampleEntTable.getRow(i);
            value = (String)row.cells.get(0).getValue();
            val = String.valueOf(i);
            if ("Y".equals(value)) {
                if(field.query==null)               
                    field.query = val;
                else
                    field.query +=","+ val;
                query.setFields(field);               
            }
        }
        
        if ( field.query != null) {
            try {
                st = service.call("runReportForWeb", query);
                if (st.getStatus() == ReportStatus.Status.SAVED) {
                    url = "report?file=" + st.getMessage();

                    Window.open(URL.encode(url), "Final Report", null);
                    window.setDone("Generated file " + st.getMessage());
                } else {
                    window.setDone(st.getMessage());
                }
            } catch (Exception e) {
                Window.alert(e.getMessage());
            }
        }
        else {
            window.setError(consts.get("noSampleSelectedError"));
            return;
        }
    }

    private ArrayList<TableDataRow> getTableModel() {
        ArrayList<TableDataRow> model;
        SampleEnvironmentalWebVO data;
        TableDataRow tr;
        Date temp;
        model = new ArrayList<TableDataRow>();
        try {
            for (int i = 0; i < results.size(); i++ ) {
                data = results.get(i);
                temp = data.getCollectionDate().getDate();
                if(data.getCollectionTime()== null){
                    temp.setHours(0);
                    temp.setMinutes(0);
                } else {
                    temp.setHours(data.getCollectionTime().getDate().getHours());
                    temp.setMinutes(data.getCollectionTime().getDate().getMinutes());                  
                }                    
                 tr = new TableDataRow(data.getAccessionNumber(),
                                      "N", data.getAccessionNumber(),data.getCollectionSite(), Datetime.getInstance(Datetime.YEAR,Datetime.MINUTE,temp),
                                      data.getCollector(),data.getStatus(),data.getTown());
                tr.data = data;
                model.add(tr);
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
        }
        return model;
    }

    /**
     * Resets all the fields to their original report specified values
     */
    protected void reset() {
        DataChangeEvent.fire(this);
        clearErrors();
    }

}
