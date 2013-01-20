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

import org.openelis.web.cache.CategoryCache;
import org.openelis.web.cache.UserCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.FinalReportWebVO;
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
import org.openelis.meta.SampleWebMeta;
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

public class FinalReportPrivateWellScreen extends Screen {

    private FinalReportFormVO                 data;
    private ModulePermission                  userPermission;
    private CalendarLookUp                    releasedFrom, releasedTo, collectedFrom, collectedTo;
    private TextBox                           collectorName, accessionFrom, accessionTo,
                                              clientReference, collectionSite, collectionTown, owner;
    private Dropdown<Integer>                 projectCode;
    private ReportScreenUtility               util;
    private DeckPanel                         deckpanel;
    private Decks                             deck;
    private HorizontalPanel                   hp;
    private AbsolutePanel                     ap;
    private TableWidget                       sampleEntTable;
    private Label<String>                     queryDeckLabel, numSampleSelected;
    private AppButton                         getSamplesButton, resetButton, backButton,
                                              selectAllButton,unselectButton, runReportButton;
    private ArrayList<FinalReportWebVO> results;

    private enum Decks {
        QUERY, LIST
    };

    /**
     * No-Arg constructor
     */
    public FinalReportPrivateWellScreen() throws Exception {
        super((ScreenDefInt)GWT.create(FinalReportPrivateWellDef.class));

        userPermission = UserCache.getPermission().getModule("w_final_privatewell");
        if (userPermission == null)
            throw new PermissionException("screenPermException", "Final Report Private Well Screen");

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
        data = new FinalReportFormVO();

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

        deckpanel = (DeckPanel)def.getWidget("deck");

        releasedFrom = (CalendarLookUp)def.getWidget(SampleWebMeta.getReleasedDateFrom());
        addScreenHandler(releasedFrom, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                releasedFrom.setValue(data.getReleasedFrom());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                data.setReleasedFrom(event.getValue());
                if(releasedFrom.getValue() != null && releasedTo.getValue() == null) {
                    releasedTo.setValue(releasedFrom.getValue().add(1));
                    releasedTo.setFocus(true);
                    releasedTo.selectText();
                    data.setReleasedTo(event.getValue());
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                releasedFrom.enable(EnumSet.of(State.ADD).contains(event.getState()));
            }
        });

        releasedTo = (CalendarLookUp)def.getWidget(SampleWebMeta.getReleasedDateTo());
        addScreenHandler(releasedTo, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                releasedTo.setValue(data.getReleasedTo());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                data.setReleasedTo(event.getValue());
                if (releasedTo.getValue() != null && releasedFrom.getValue() == null) {
                    releasedFrom.setValue(releasedTo.getValue().add( -1));
                    releasedFrom.setFocus(true);
                    releasedFrom.selectText();
                    data.setReleasedFrom(event.getValue());
                }            
            }

            public void onStateChange(StateChangeEvent<State> event) {
                releasedTo.enable(EnumSet.of(State.ADD).contains(event.getState()));
            }
        });

        collectedFrom = (CalendarLookUp)def.getWidget(SampleWebMeta.getCollectionDateFrom());
        addScreenHandler(collectedFrom, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                collectedFrom.setValue(data.getCollectedFrom());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                data.setCollectedFrom(event.getValue());
                if(collectedFrom.getValue() != null && collectedTo.getValue() == null) {
                    collectedTo.setValue(collectedFrom.getValue().add(1));
                    collectedTo.setFocus(true);
                    collectedTo.selectText();
                    data.setCollectedTo(event.getValue());
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                collectedFrom.enable(EnumSet.of(State.ADD).contains(event.getState()));
            }
        });

        collectedTo = (CalendarLookUp)def.getWidget(SampleWebMeta.getCollectionDateTo());
        addScreenHandler(collectedTo, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                collectedTo.setValue(data.getCollectedTo());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                data.setCollectedTo(event.getValue());
                if (collectedTo.getValue() != null && collectedFrom.getValue() == null) {
                    collectedFrom.setValue(collectedTo.getValue().add( -1));
                    collectedFrom.setFocus(true);
                    collectedFrom.selectText();
                    data.setCollectedFrom(event.getValue());
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                collectedTo.enable(EnumSet.of(State.ADD).contains(event.getState()));
            }
        });

        collectorName = (TextBox)def.getWidget(SampleWebMeta.getWellCollector());
        addScreenHandler(collectorName, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                collectorName.setValue(data.getCollectorName());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setCollectorName(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                collectedTo.enable(EnumSet.of(State.ADD).contains(event.getState()));
            }
        });

        accessionFrom = (TextBox)def.getWidget(SampleWebMeta.getAccessionNumberFrom());
        addScreenHandler(accessionFrom, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                accessionFrom.setValue(data.getAccessionFrom());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                data.setAccessionFrom(event.getValue());
                if(!DataBaseUtil.isEmpty(accessionFrom.getValue()) && DataBaseUtil.isEmpty(accessionTo.getValue())) {
                    accessionTo.setFieldValue(accessionFrom.getValue());
                    accessionTo.setFocus(true);
                    accessionTo.selectAll();
                    data.setAccessionTo(event.getValue());
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                accessionFrom.enable(EnumSet.of(State.ADD).contains(event.getState()));
            }
        });

        accessionTo = (TextBox)def.getWidget(SampleWebMeta.getAccessionNumberTo());
        addScreenHandler(accessionTo, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                accessionTo.setValue(data.getAccessionTo());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                data.setAccessionTo(event.getValue());
                if (!DataBaseUtil.isEmpty(accessionTo.getValue()) && DataBaseUtil.isEmpty(accessionFrom.getValue())) {
                    accessionFrom.setFieldValue(accessionTo.getValue());
                    accessionFrom.setFocus(true);
                    accessionFrom.selectAll();
                    data.setAccessionFrom(event.getValue());
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                accessionTo.enable(EnumSet.of(State.ADD).contains(event.getState()));
            }
        });

        clientReference = (TextBox)def.getWidget(SampleWebMeta.getClientReference());
        addScreenHandler(clientReference, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                clientReference.setValue(data.getClientReference());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setClientReference(event.getValue());
            }
            
            public void onStateChange(StateChangeEvent<State> event) {
                clientReference.enable(EnumSet.of(State.ADD).contains(event.getState()));
            }
        });

        collectionSite = (TextBox)def.getWidget(SampleWebMeta.getWellLocation());
        addScreenHandler(collectionSite, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                collectionSite.setValue(data.getCollectionSite());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setCollectionSite(event.getValue());
            }
            
            public void onStateChange(StateChangeEvent<State> event) {
                collectionSite.enable(EnumSet.of(State.ADD).contains(event.getState()));
            }
        });

        collectionTown = (TextBox)def.getWidget(SampleWebMeta.getLocationAddrCity());
        addScreenHandler(collectionTown, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                collectionTown.setValue(data.getCollectionTown());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setCollectionTown(event.getValue());
            }
            
            public void onStateChange(StateChangeEvent<State> event) {
                collectionTown.enable(EnumSet.of(State.ADD).contains(event.getState()));
            }
        });

        owner = (TextBox)def.getWidget(SampleWebMeta.getWellOwner());
        addScreenHandler(owner, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                owner.setValue(data.getOwner());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setOwner(event.getValue());
            }
            
            public void onStateChange(StateChangeEvent<State> event) {
                owner.enable(EnumSet.of(State.ADD).contains(event.getState()));
            }
        });

        projectCode = (Dropdown)def.getWidget(SampleWebMeta.getProjectId());
        projectCode.setMultiSelect(true);
        addScreenHandler(projectCode, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                projectCode.setSelection(data.getProjectCode());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                data.setProjectCode(event.getValue());
            }
            
            public void onStateChange(StateChangeEvent<State> event) {
                projectCode.enable(EnumSet.of(State.ADD).contains(event.getState()));
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
                if (sampleEntTable.numRows() > 0)
                    numSampleSelected.setValue(sampleEntTable.numRows() + " "+ consts.get("numSamplesFound"));
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

        numSampleSelected = (Label<String>)def.getWidget("numSampleSelected");
        addScreenHandler(numSampleSelected, new ScreenEventHandler<String>() {
            public void onStateChange(StateChangeEvent<State> event) {
                numSampleSelected.enable(EnumSet.of(State.ADD).contains(event.getState()));
            }
        });
        
        backButton = (AppButton)def.getWidget("backButton");
        addScreenHandler(backButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
               loadDeck(null);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                backButton.enable(true);
            }
        });

        selectAllButton = (AppButton)def.getWidget("selectAllButton");
        addScreenHandler(selectAllButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                for (int i = 0; i < sampleEntTable.numRows(); i++ )
                    sampleEntTable.setCell(i, 0, "Y");
            }

            public void onStateChange(StateChangeEvent<State> event) {
                selectAllButton.enable(true);
            }
        });
        
        unselectButton = (AppButton)def.getWidget("unselectButton");
        addScreenHandler(unselectButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                resetSampleList();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                unselectButton.enable(true);
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

        queryDeckLabel = new Label(consts.get("backToSearch"));
        queryDeckLabel.setStyleName("ScreenLabel");
        hp = new HorizontalPanel();
        ap = new AbsolutePanel();
        ap.setStyleName("PreviousButtonImage");
        hp.add(ap);
        hp.add(queryDeckLabel);
        backButton.setWidget(hp);
    }
    
    private void initializeDropdowns() {
        ArrayList<TableDataRow> model;
        ArrayList<IdNameVO> projects;
        ArrayList<DictionaryDO> entries;
        TableDataRow row;
        /*
         * Initializing the project code drop down
         */
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));

        try {
            projects = FinalReportService.get().getPrivateWellProjectList();
            for (IdNameVO p : projects) {
                row = new TableDataRow(p.getId(), p.getName());
                model.add(row);
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
        }       
        projectCode.setModel(model);
        /*
         * Initializing the status column of the samples table with the sample
         * status.
         */
        model = new ArrayList<TableDataRow>();
        entries = CategoryCache.getBySystemName("sample_status");
        for (DictionaryDO d : entries) {
            row = new TableDataRow(d.getId(), d.getEntry());
            model.add(row);
        }
        ((Dropdown<Integer>) (sampleEntTable.getColumnWidget("status"))).setModel(model);
    }

    protected void getSamples() {
        Query query;
        ArrayList<FinalReportWebVO> list;
        ArrayList<QueryData> queryList;

        if ( !validate()) {
            window.setError(consts.get("correctErrors"));
            return;
        }

        query = new Query();
        queryList = createWhereFromParamFields(util.getQueryFields());
        /*
         * if user does not enter any search details, throw an error.
         */
        if (queryList.size() == 0) {
            window.setError(consts.get("nofieldSelectedError"));
            return;
        }

        query.setFields(queryList);
        
        window.setBusy(consts.get("retrSamples"));

        try {
            list = FinalReportService.get().getSamplePrivateWellList(query);
            if (list.size() > 0) {
                loadDeck(list);
                setResults(list);
            } else {
                window.setError(consts.get("noSamplesFoundChangeSearch"));
                return;
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
        }
        window.clearStatus();
    }

    /**
     * Resets all the fields to their original report specified values
     */
    protected void reset() {
        data = new FinalReportFormVO();
        setState(State.ADD);
        DataChangeEvent.fire(this);
        clearErrors();
    }

    /**
     * Resets all the fields to their original report specified values
     */
    protected void resetSampleList() {
        setState(State.ADD);
        DataChangeEvent.fire(this);
        clearErrors();
    }

    protected void loadDeck(ArrayList<FinalReportWebVO> list) {
        switch (deck) {
            case QUERY:
                deckpanel.showWidget(1);
                deck = Decks.LIST;
                setState(State.ADD);
                setResults(list);
                backButton.setVisible(true);
                break;
            case LIST:
                deckpanel.showWidget(0);
                deck = Decks.QUERY;
                break;
        }
    }    

    public void setResults(ArrayList<FinalReportWebVO> results) {
        this.results = results;
        setState(State.ADD);
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
        field.key = "SAMPLE_ID";
        sampleEntTable = (TableWidget)def.getWidget("sampleEntTable");
        for (int i = 0; i < sampleEntTable.numRows(); i++ ) {
            row = sampleEntTable.getRow(i);
            value = (String)row.cells.get(0).getValue();
            val = String.valueOf(i);
            if ("Y".equals(value)) {
                /*
                 * If field.query is null, then it is the first index being
                 * selected, else an index is already selected, so field.query
                 * should be appended with comma.
                 */
                if (field.query == null)
                    field.query = val;
                else
                    field.query += "," + val;
            }
        }
        query.setFields(field);

        if (field.query == null) {
            window.setError(consts.get("noSampleSelectedError"));
            return;
        }
        try {
            window.setBusy(consts.get("genReportMessage"));
            st = FinalReportService.get().runReportForWeb(query);
            if (st.getStatus() == ReportStatus.Status.SAVED) {
                url = "report?file=" + st.getMessage();

                Window.open(URL.encode(url), "FinalReport", null);
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
        }
        
        window.clearStatus();
    }

    private ArrayList<TableDataRow> getTableModel() {
        ArrayList<TableDataRow> model;
        FinalReportWebVO data;
        TableDataRow tr;

        model = new ArrayList<TableDataRow>();
        if (results == null || results.size() == 0)
            return model;

        for (int i = 0; i < results.size(); i++ ) {
            data = results.get(i);

            tr = new TableDataRow(data.getAccessionNumber(),
                                  "N",
                                  data.getAccessionNumber(),
                                  data.getLocation(),
                                  data.getCollectionDateTime(),
                                  data.getCollector(),
                                  data.getStatusId(),
                                  data.getLocationAddressCity(),
                                  data.getPrivateWellOwner());
            tr.data = data;
            model.add(tr);
        }
        return model;
    }

    private ArrayList<QueryData> createWhereFromParamFields(ArrayList<QueryData> fields) {
        int i;
        QueryData field, fRel, fCol, fAcc;
        ArrayList<QueryData> list;

        list = new ArrayList<QueryData>();
        fRel = fCol = fAcc = null;

        for (i = 0; i < fields.size(); i++ ) {
            field = fields.get(i);
            if ((SampleWebMeta.getReleasedDateFrom()).equals(field.key)) {
                if (fRel == null) {
                    fRel = field;
                    fRel.key = SampleWebMeta.getReleasedDate();
                } else {
                    fRel.query = field.query + ".." + fRel.query;
                    list.add(fRel);
                }
            } else if ((SampleWebMeta.getReleasedDateTo()).equals(field.key)) {
                if (fRel == null) {
                    fRel = field;
                    fRel.key = SampleWebMeta.getReleasedDate();
                } else {
                    fRel.query = fRel.query + ".." + field.query;
                    list.add(fRel);
                }
            } else if ((SampleWebMeta.getCollectionDateFrom()).equals(field.key)) {
                if (fCol == null) {
                    fCol = field;
                    fCol.key = SampleWebMeta.getCollectionDate();
                } else {
                    fCol.query = field.query + ".." + fCol.query;
                    list.add(fCol);
                }
            } else if ((SampleWebMeta.getCollectionDateTo()).equals(field.key)) {
                if (fCol == null) {
                    fCol = field;
                    fCol.key = SampleWebMeta.getCollectionDate();
                } else {
                    fCol.query = fCol.query + ".." + field.query;
                    list.add(fCol);
                }
            } else if ((SampleWebMeta.getAccessionNumberFrom()).equals(field.key)) {
                if (fAcc == null) {
                    fAcc = field;
                    fAcc.key = SampleWebMeta.getAccessionNumber();
                } else {
                    fAcc.query = field.query + ".." + fAcc.query;
                    list.add(fAcc);
                }
            } else if ((SampleWebMeta.getAccessionNumberTo()).equals(field.key)) {
                if (fAcc == null) {
                    fAcc = field;
                    fAcc.key = SampleWebMeta.getAccessionNumber();
                } else {
                    fAcc.query = fAcc.query + ".." + field.query;
                    list.add(fAcc);
                }
            } else {
                list.add(field);
            } 
        }
        return list;
    }  
}
