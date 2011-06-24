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
import java.util.EnumSet;
import java.util.HashMap;

import org.openelis.cache.CategoryCache;
import org.openelis.cache.UserCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.SampleFinalReportWebVO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.ModulePermission;
import org.openelis.gwt.common.PermissionException;
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
import org.openelis.gwt.widget.CalendarLookUp;
import org.openelis.gwt.widget.DeckPanel;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.Label;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.gwt.widget.web.WebWindow;
import org.openelis.meta.SampleMeta;
import org.openelis.web.util.ReportScreenUtility;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class FinalReportEnvScreen extends Screen {

    private FinalReportEnvironmentalVO data;
    private ModulePermission           userPermission;
    private CalendarLookUp             releasedFrom, releasedTo, collectedFrom, collectedTo;
    private TextBox                    collectorName, accessionFrom, accessionTo, clientReference, collectionSite,
                                       collectionTown;
    private Dropdown<Integer>          projectCode;  
    private ReportScreenUtility        util;
    private String                     clause;
    private HashMap<String, String>    map;
    private String                     valueOrg;
    private DeckPanel                  deckpanel;
    private Decks                      deck;
    private HorizontalPanel            hp;
    private AbsolutePanel              ap;
    private TableWidget                sampleEntTable;
    private Label<String>              queryDeckLabel, numSampleSelected;
    private AppButton                  getSamplesButton, resetButton, runReportButton, resettButton, backButton, selectAllButton;
    private ArrayList<SampleFinalReportWebVO> results;
    
    private enum Decks {
        QUERY, LIST
    };

    /**
     * No-Arg constructor
     */
    public FinalReportEnvScreen() throws Exception {
        super((ScreenDefInt)GWT.create(FinalReportEnvDef.class));
        service = new ScreenService("controller?service=org.openelis.modules.report.server.FinalReportService");

        userPermission = UserCache.getPermission().getModule("w_final_environmental");
        if (userPermission == null)
            throw new PermissionException("screenPermException", "w_final_environmental Screen");

        clause = userPermission.getClause();

        DeferredCommand.addCommand(new Command() {
            public void execute() {
                postConstructor();
            }
        });
    }

    /**
     * This method is called to set the initial state of widgets after the
     * screen is attached to the browser. It is usually called in deferred
     * command.
     */
    private void postConstructor() {
        deck = Decks.QUERY;
        data = new FinalReportEnvironmentalVO();

        initialize();
        setState(State.ADD);
        initializeDropdowns();
        DataChangeEvent.fire(this);
    }

    /**
     * Initialize widgets
     */
    private void initialize() {

        util = new ReportScreenUtility(def);
        map = util.parseClause(clause);
        
        deckpanel = (DeckPanel)def.getWidget("deck");
      
        releasedFrom = (CalendarLookUp)def.getWidget("RELEASED_FROM");
        addScreenHandler(releasedFrom, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                    releasedFrom.setValue(data.getReleasedFrom());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                    releasedFrom.enable(EnumSet.of(State.ADD).contains(event.getState()));
            }
            

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                data.setReleasedFrom(event.getValue());
            }
        });

        releasedTo = (CalendarLookUp)def.getWidget("RELEASED_TO");
        addScreenHandler(releasedTo, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                    releasedTo.setValue(data.getReleasedTo());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                    releasedTo.enable(EnumSet.of(State.ADD).contains(event.getState()));
            }
            
            public void onValueChange(ValueChangeEvent<Datetime> event) {
                data.setReleasedTo(event.getValue());
            }
        });

        collectedFrom = (CalendarLookUp)def.getWidget("COLLECTED_FROM");
        addScreenHandler(collectedFrom, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                    collectedFrom.setValue(data.getCollectedFrom());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                    collectedFrom.enable(EnumSet.of(State.ADD).contains(event.getState()));
            }
            
            public void onValueChange(ValueChangeEvent<Datetime> event) {
                data.setCollectedFrom(event.getValue());
            }
        });

        collectedTo = (CalendarLookUp)def.getWidget("COLLECTED_TO");
        addScreenHandler(collectedTo, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                    collectedTo.setValue(data.getCollectedTo());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                    collectedTo.enable(EnumSet.of(State.ADD).contains(event.getState()));
            }
            
            public void onValueChange(ValueChangeEvent<Datetime> event) {
                data.setCollectedTo(event.getValue());
            }
        });

        collectorName = (TextBox)def.getWidget("COLLECTOR_NAME");
        addScreenHandler(collectorName, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                    collectorName.setValue(data.getCollectorName());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                    collectorName.enable(EnumSet.of(State.ADD).contains(event.getState()));
            }
            
            public void onValueChange(ValueChangeEvent<String> event) {
                data.setCollectorName(event.getValue());
            }
        });

        accessionFrom = (TextBox)def.getWidget("ACCESSION_FROM");
        addScreenHandler(accessionFrom, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                    accessionFrom.setValue(data.getAccessionFrom());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                    accessionFrom.enable(EnumSet.of(State.ADD).contains(event.getState()));
            }
            
            public void onValueChange(ValueChangeEvent<Integer> event) {
                data.setAccessionFrom(event.getValue());
            }
        });

        accessionTo = (TextBox)def.getWidget("ACCESSION_TO");
        addScreenHandler(accessionTo, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                    accessionTo.setValue(data.getAccessionTo());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                    accessionTo.enable(EnumSet.of(State.ADD).contains(event.getState()));
            }
            
            public void onValueChange(ValueChangeEvent<Integer> event) {
                data.setAccessionTo(event.getValue());
            }
        });

        clientReference = (TextBox)def.getWidget("CLIENT_REFERENCE");
        addScreenHandler(clientReference, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                    clientReference.setValue(data.getClientReference());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                    clientReference.enable(EnumSet.of(State.ADD).contains(event.getState()));
            }
            
            public void onValueChange(ValueChangeEvent<String> event) {
                data.setClientReference(event.getValue());
            }
        });

        collectionSite = (TextBox)def.getWidget("COLLECTION_SITE");
        addScreenHandler(collectionSite, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                    collectionSite.setValue(data.getCollectionSite());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                    collectionSite.enable(EnumSet.of(State.ADD).contains(event.getState()));
            }
            
            public void onValueChange(ValueChangeEvent<String> event) {
                data.setCollectionSite(event.getValue());
            }  
        });

        collectionTown = (TextBox)def.getWidget("COLLECTION_TOWN");
        addScreenHandler(collectionTown, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                    collectionTown.setValue(data.getCollectionTown());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                    collectionTown.enable(EnumSet.of(State.ADD).contains(event.getState()));
            }
            
            public void onValueChange(ValueChangeEvent<String> event) {
                data.setCollectionTown(event.getValue());
            }
        });

        projectCode = (Dropdown)def.getWidget("PROJECT_CODE");
        addScreenHandler(projectCode, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                    projectCode.setSelection(data.getProjectCode());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                    projectCode.enable(EnumSet.of(State.ADD).contains(event.getState()));
            }
            
            public void onValueChange(ValueChangeEvent<Integer> event) {
                data.setProjectCode(event.getValue());
            }
        });

        getSamplesButton = (AppButton)def.getWidget("getSampleListButton");
        addScreenHandler(getSamplesButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                getSamples();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                getSamplesButton.enable(true);
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
        
        sampleEntTable = (TableWidget)def.getWidget("sampleEntTable");
        addScreenHandler(sampleEntTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {      
                    sampleEntTable.load(getTableModel());
                    if(sampleEntTable.numRows() > 0)
                        numSampleSelected.setValue(" "+sampleEntTable.numRows()+" samples have been found.");
                    else 
                        numSampleSelected.setValue(" 0 samples have been found. Please change your search criteria to get more samples.");
                        
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
        
        
        selectAllButton = (AppButton)def.getWidget("selectAllButton");
        addScreenHandler(selectAllButton, new ScreenEventHandler<Object>() {            
            
            public void onClick(ClickEvent event) { 
                for(int i = 0;i< sampleEntTable.numRows();i++)
                    sampleEntTable.setCell(i, 0, "Y");                
            }
            
            public void onStateChange(StateChangeEvent<State> event) {
                selectAllButton.enable(true);
            }
        });
        
        numSampleSelected = (Label<String>)def.getWidget("numSampleSelected");
        addScreenHandler(numSampleSelected, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                
             }

            public void onStateChange(StateChangeEvent<State> event) {
                numSampleSelected.enable(EnumSet.of(State.ADD).contains(event.getState()));
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

        resettButton = (AppButton)def.getWidget("resettButton");
        addScreenHandler(resettButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                resetSampleList();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                resettButton.enable(true);
            }
        });
        
        backButton = new AppButton();        
        queryDeckLabel = new Label("Query"); 
        queryDeckLabel.setStyleName("ScreenLabel");
        hp = new HorizontalPanel();
        ap = new AbsolutePanel();
        ap.setStyleName("PreviousButtonImage");
        hp.add(ap);
        hp.add(queryDeckLabel);
        backButton.setWidget(hp);
        addScreenHandler(backButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                loadDeck(null);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                backButton.enable(true);
            }
        });      
    }

    protected void getSamples() {
        Query query;
        QueryData qd;
        ArrayList<SampleFinalReportWebVO> list;
        ArrayList<QueryData> queryList;
        
        if ( !validate()) {
            window.setError(consts.get("correctErrors"));
            return;
        }

        setWidgetValues();
        
        query = new Query();
        queryList = util.getQueryFields();
        /*
         * if user does not enter any search details, throw an error.
         */
        if(queryList.size()<=0){
            window.setError(consts.get("nofieldSelectedError"));
            return;
        }
        query.setFields(queryList);
        
        valueOrg = map.get(SampleMeta.getSampleOrgOrganizationId());
        qd = new QueryData();
        qd.key = SampleMeta.getSampleOrgOrganizationId();
        qd.query = valueOrg;
        qd.type = QueryData.Type.INTEGER;
        query.setFields(qd);
        
       window.setBusy("Retrieving Samples");
        
        try {
            list = service.callList("getSampleList", query);
            if (list != null) {
                loadDeck(list);
                setResults(list);
            }                
        } catch (Exception e) {
            Window.alert(e.getMessage());
        }
       window.clearStatus();
    }

    protected void setWidgetValues() {

        /*
         * completes the fields which don't have values for released and collected dates and accession
         * number
         */
        if (releasedFrom.getValue() != null && releasedTo.getValue() == null)
            releasedTo.setValue(releasedFrom.getValue().add(1));
        if (releasedFrom.getValue() == null && releasedTo.getValue() != null)
            releasedFrom.setValue(releasedTo.getValue().add( -1));
        if (collectedFrom.getValue() != null && collectedTo.getValue() == null)
            collectedTo.setValue(collectedFrom.getValue().add(1));
        if (collectedFrom.getValue() == null && collectedTo.getValue() != null)
            collectedFrom.setValue(collectedTo.getValue().add( -1));
        if ( !DataBaseUtil.isEmpty(accessionFrom.getValue()) &&
            DataBaseUtil.isEmpty(accessionTo.getValue()))
            accessionTo.setValue(accessionFrom.getValue());
        if (DataBaseUtil.isEmpty(accessionFrom.getValue()) &&
            !DataBaseUtil.isEmpty(accessionTo.getValue()))
            accessionFrom.setValue(accessionTo.getValue());
    }

    /**
     * Resets all the fields to their original report specified values
     */
    protected void reset() {
        data = new FinalReportEnvironmentalVO();
        DataChangeEvent.fire(this);
        clearErrors();
    }
    
    /**
     * Resets all the fields to their original report specified values
     */
    protected void resetSampleList() {
        DataChangeEvent.fire(this);
        clearErrors();
    }
    
    protected void loadDeck(ArrayList<SampleFinalReportWebVO> list) {
        switch(deck){
            case QUERY:
                deckpanel.showWidget(1);
                deck = Decks.LIST;
                setState(State.ADD);
                setResults(list);                
                ((WebWindow)window).setCrumbLink(backButton);
                break;
            case LIST :
                deckpanel.showWidget(0);
                deck = Decks.QUERY;                 
                ((WebWindow)window).setCrumbLink(null);
                break;         
        }        
    }

    private void initializeDropdowns() {
        ArrayList<TableDataRow> model;
        ArrayList<IdNameVO> list;
        ArrayList<DictionaryDO> lst;
        TableDataRow row;
        Query query;
        QueryData qd;

        list = null;
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));

        query = new Query();

        valueOrg = map.get(SampleMeta.getSampleOrgOrganizationId());
        qd = new QueryData();
        qd.key = SampleMeta.getSampleOrgOrganizationId();
        qd.query = valueOrg;
        qd.type = QueryData.Type.INTEGER;
        query.setFields(qd);

        try {
            list = service.callList("getProjectList", query);
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int counter = 0; counter < list.size(); counter++ ) {
            row = new TableDataRow(list.get(counter).getId(), list.get(counter).getName());
            model.add(row);
        }
        projectCode.setModel(model);
        
        model.add(new TableDataRow(null, ""));
        lst = CategoryCache.getBySystemName("sample_status");
        for (DictionaryDO d : lst) {         
            row = new TableDataRow(d.getId(), d.getEntry());
            row.enabled = ("Y".equals(d.getIsActive()));
            model.add(row);
        }
        ((Dropdown<Integer>)(sampleEntTable.getColumnWidget("status"))).setModel(model);

    }
    
    public void setResults(ArrayList<SampleFinalReportWebVO> results) {
        this.results = results;
        DataChangeEvent.fire(this);
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
            }
        }
        query.setFields(field); 
        
        field = new QueryData();
        field.key = "DOMAIN";
        field.query = "E";
        query.setFields(field);
        
        if(query.getFields().get(0).query != null) {
            try {
                st = service.call("runReportForWeb", query);
                if (st.getStatus() == ReportStatus.Status.SAVED) {
                    url = "report?file=" + st.getMessage();

                    Window.open(URL.encode(url), "Final Report", null);
                    window.setStatus("Generated file " + st.getMessage(),"");
                } else {
                    window.setStatus(st.getMessage(),"");
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
        SampleFinalReportWebVO data;
        TableDataRow tr;
        Date temp;
        
        model = new ArrayList<TableDataRow>();
        if (results == null) 
            return null;        
        if (results.size() > 0) {
            try {
                for (int i = 0; i < results.size(); i++ ) {
                    data = results.get(i);
                    temp = data.getCollectionDate().getDate();
                    if (data.getCollectionTime() == null) {
                        temp.setHours(0);
                        temp.setMinutes(0);
                    } else {
                        temp.setHours(data.getCollectionTime().getDate().getHours());
                        temp.setMinutes(data.getCollectionTime().getDate().getMinutes());
                    }
                    tr = new TableDataRow(data.getAccessionNumber(),
                                          "N",
                                          data.getAccessionNumber(),
                                          data.getLocation(),
                                          Datetime.getInstance(Datetime.YEAR, Datetime.MINUTE, temp),
                                          data.getCollector(),
                                          data.getStatus(),
                                          data.getPvtWellOwner());
                    tr.data = data;
                    model.add(tr);
                }
            } catch (Exception e) {
                Window.alert(e.getMessage());
                e.printStackTrace();
            }
        } else {
            model = null;
        }

        return model;
    }

    private class FinalReportEnvironmentalVO {        

        private Datetime releasedFrom, releasedTo, collectedFrom, collectedTo;
        private String   collectorName, clientReference, collectionSite, collectionTown, numSampleSelected;
        private Integer  accessionFrom, accessionTo, projectCode;

        public Datetime getReleasedFrom() {
            return releasedFrom;
        }
        
        public void setReleasedFrom(Datetime releasedFrom) {
            this.releasedFrom = releasedFrom;
        }

        public Datetime getReleasedTo() {
            return releasedTo;
        }
        
        public void setReleasedTo(Datetime releasedTo) {
            this.releasedTo = releasedTo;
        }

        public Datetime getCollectedFrom() {
            return collectedFrom;
        }
        
        public void setCollectedFrom(Datetime collectedFrom) {
            this.collectedFrom = collectedFrom;
        }

        public Datetime getCollectedTo() {
            return collectedTo;
        }
        
        public void setCollectedTo(Datetime collectedTo) {
            this.collectedTo = collectedTo;
        }

        public String getCollectorName() {
            return collectorName;
        }
        
        public void setCollectorName(String collectorName) {
            this.collectorName = collectorName;
        }

        public String getClientReference() {
            return clientReference;
        }
        
        public void setClientReference(String clientReference) {
            this.clientReference = clientReference;
        }

        public String getCollectionSite() {
            return collectionSite;
        }
        
        public void setCollectionSite(String collectionSite) {
            this.collectionSite = collectionSite;
        }

        public String getCollectionTown() {
            return collectionTown;
        }
        
        public void setCollectionTown(String collectionTown) {
            this.collectionTown = collectionTown;
        }
        
        public String getNumSampleSelected() {
            return numSampleSelected;
        }
        
        public void setNumSampleSelected(String numSampleSelected) {
            this.numSampleSelected = numSampleSelected;
        }

        public Integer getAccessionFrom() {
            return accessionFrom;
        }
        
        public void setAccessionFrom(Integer accessionFrom) {
            this.accessionFrom = accessionFrom;
        }

        public Integer getAccessionTo() {
            return accessionTo;
        }
        
        public void setAccessionTo(Integer accessionTo) {
            this.accessionTo = accessionTo;
        }

        public Integer getProjectCode() {
            return projectCode;
        }
        
        public void setProjectCode(Integer projectCode) {
            this.projectCode = projectCode;
        }
    }
}
