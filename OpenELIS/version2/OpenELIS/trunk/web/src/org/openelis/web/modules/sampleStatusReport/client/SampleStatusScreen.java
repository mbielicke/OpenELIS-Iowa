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
package org.openelis.web.modules.sampleStatusReport.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;

import org.openelis.cache.DictionaryCache;
import org.openelis.cache.UserCache;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.SampleStatusWebReportVO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.ModulePermission;
import org.openelis.gwt.common.PermissionException;
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
import org.openelis.meta.SampleWebMeta;
import org.openelis.web.util.ReportScreenUtility;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class SampleStatusScreen extends Screen {

    private FinalReportEnvironmentalVO         data;
    private ModulePermission                   userPermission;
    private CalendarLookUp                     collectedFrom, collectedTo;
    private TextBox                            clientReference;
    private Dropdown<Integer>                  projectCode;
    private ReportScreenUtility                util;
    private DeckPanel                          deckpanel;
    private Decks                              deck;
    private HorizontalPanel                    hp;
    private AbsolutePanel                      ap, noSampleSelectedPanel;
    private TableWidget                        sampleEntTable;
    private Label<String>                      queryDeckLabel, noSampleSelected;
    private AppButton                          getSamplesButton, resetButton, backButton;
    private ArrayList<SampleStatusWebReportVO> results;
    private Integer                            statusReleased;

    private enum Decks {
        QUERY, LIST
    };

    /**
     * No-Arg constructor
     */
    public SampleStatusScreen() throws Exception {
        super((ScreenDefInt)GWT.create(SampleStatusDef.class));
        service = new ScreenService("controller?service=org.openelis.modules.report.server.SampleStatusReportService");
        
        userPermission = UserCache.getPermission().getModule("w_status");
        if (userPermission == null)
            throw new PermissionException("screenPermException", "Sample Status Screen");

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

        deckpanel = (DeckPanel)def.getWidget("deck");

        collectedFrom = (CalendarLookUp)def.getWidget(SampleWebMeta.getCollectionDateFrom());
        addScreenHandler(collectedFrom, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                collectedFrom.setValue(data.getCollectedFrom());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                collectedFrom.enable(EnumSet.of(State.ADD).contains(event.getState()));
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                data.setCollectedFrom(event.getValue());
                if (collectedFrom.getValue() != null && collectedTo.getValue() == null) {
                    collectedTo.setValue(collectedFrom.getValue().add(1));
                    collectedTo.setFocus(true);
                    collectedTo.selectText();
                }
            }
        });

        collectedTo = (CalendarLookUp)def.getWidget(SampleWebMeta.getCollectionDateTo());
        addScreenHandler(collectedTo, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                collectedTo.setValue(data.getCollectedTo());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                collectedTo.enable(EnumSet.of(State.ADD).contains(event.getState()));
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                data.setCollectedTo(event.getValue());
                if (collectedTo.getValue() != null && collectedFrom.getValue() == null) {
                    collectedFrom.setValue(collectedTo.getValue().add( -1));
                    collectedFrom.setFocus(true);
                    collectedFrom.selectText();
                }
            }
        });

        clientReference = (TextBox)def.getWidget(SampleWebMeta.getClientReference());
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

        projectCode = (Dropdown)def.getWidget(SampleWebMeta.getProjectId());
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

        noSampleSelectedPanel = (AbsolutePanel)def.getWidget("noSampleSelectedPanel");

        noSampleSelected = (Label<String>)def.getWidget("noSampleSelected");
        addScreenHandler(noSampleSelected, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                noSampleSelected.setText(null);
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
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sampleEntTable.enable(true);
            }
        });

        sampleEntTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                event.cancel();
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

    private void initializeDropdowns() {
        ArrayList<TableDataRow> model;
        ArrayList<IdNameVO> list;
        TableDataRow row;

        list = null;
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        try {
            list = service.callList("getSampleStatusProjectList");
            for (int counter = 0; counter < list.size(); counter++ ) {
                row = new TableDataRow(list.get(counter).getId(), list.get(counter).getName());
                model.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        projectCode.setModel(model);

       try {
            statusReleased = DictionaryCache.getIdBySystemName("analysis_released");           
        } catch (Exception e) {
            Window.alert(e.getMessage());
            window.close();
        }
    }

    protected void getSamples() {
        Query query;
        ArrayList<SampleStatusWebReportVO> list;
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
            list = service.callList("getSampleListForSampleStatusReport", query);
            if (list.size() > 0) {
                noSampleSelectedPanel.setVisible(false);
                loadDeck(list);
                setResults(list);
            } else {
                noSampleSelectedPanel.setVisible(true);
                noSampleSelected.setValue(consts.get("noSamplesFoundChangeSearch"));
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
        data = new FinalReportEnvironmentalVO();
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

    protected void loadDeck(ArrayList<SampleStatusWebReportVO> list) {
        switch (deck) {
            case QUERY:
                deckpanel.showWidget(1);
                deck = Decks.LIST;
                setState(State.ADD);
                setResults(list);
                ((WebWindow)window).setCrumbLink(backButton);
                break;
            case LIST:
                deckpanel.showWidget(0);
                deck = Decks.QUERY;
                ((WebWindow)window).setCrumbLink(null);
                break;
        }
    }

    public void setResults(ArrayList<SampleStatusWebReportVO> results) {
        this.results = results;
        setState(State.ADD);
        DataChangeEvent.fire(this);
    }

    private ArrayList<TableDataRow> getTableModel() {
        Integer accNumPrev, accNum;
        String tempStatus;
        Date temp;
        Datetime temp1;
        ArrayList<TableDataRow> model;
        SampleStatusWebReportVO data;
        TableDataRow row;

        accNumPrev = null;
        tempStatus = "";
        model = new ArrayList<TableDataRow>();
        if (results == null || results.size() == 0)
            return model;
        try {
            for (int i = 0; i < results.size(); i++ ) {
                data = results.get(i);
                accNum = data.getAccessionNumber();
                /*
                 * If analysis status is Released, screen displays "Completed status", for all other statuses
                 * screen displays "In Progress".
                 */
                if(statusReleased.equals(data.getStatusId()))
                    tempStatus = consts.get("completed");
                else
                    tempStatus = consts.get("inProgress");
                if ( !accNum.equals(accNumPrev)) {
                    if (data.getCollectionDate() != null) {
                        temp = data.getCollectionDate().getDate();
                        if (data.getCollectionTime() == null) {
                            temp.setHours(0);
                            temp.setMinutes(0);
                        } else {
                            temp.setHours(data.getCollectionTime().getDate().getHours());
                            temp.setMinutes(data.getCollectionTime().getDate().getMinutes());
                        }
                        temp1 = Datetime.getInstance(Datetime.YEAR, Datetime.MINUTE, temp);
                    } else {
                        temp1 = null;
                    }

                    row = new TableDataRow(6);
                    row.cells.get(0).setValue(data.getAccessionNumber());
                    row.cells.get(1).setValue(data.getCollector());
                    row.cells.get(3).setValue(temp1);
                    row.cells.get(4).setValue(data.getReceivedDate());
                    row.cells.get(5).setValue(data.getClientReference());
                    model.add(row);

                    row = new TableDataRow(6);
                    row.cells.get(1).setValue(data.getTestReportingDescription() + " : " +
                                              data.getMethodReportingDescription());
                    row.cells.get(2).setValue(tempStatus);
                    model.add(row);
                } else {
                    row = new TableDataRow(6);
                    row.cells.get(1).setValue(data.getTestReportingDescription() + " : " +
                                              data.getMethodReportingDescription());
                    row.cells.get(2).setValue(tempStatus);
                    model.add(row);
                }
                accNumPrev = accNum;
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
        }
        return model;
    }

    private ArrayList<QueryData> createWhereFromParamFields(ArrayList<QueryData> fields) {
        int i;
        QueryData field, fCol;
        ArrayList<QueryData> list;

        list = new ArrayList<QueryData>();
        fCol = null;

        for (i = 0; i < fields.size(); i++ ) {
            field = fields.get(i);
            if ( (SampleWebMeta.getCollectionDateFrom()).equals(field.key)) {
                if (fCol == null) {
                    fCol = field;
                    fCol.key = SampleWebMeta.getCollectionDate();
                } else {
                    fCol.query = field.query + ".." + fCol.query;
                    list.add(fCol);
                }
            } else if ( (SampleWebMeta.getCollectionDateTo()).equals(field.key)) {
                if (fCol == null) {
                    fCol = field;
                    fCol.key = SampleWebMeta.getCollectionDate();
                } else {
                    fCol.query = fCol.query + ".." + field.query;
                    list.add(fCol);
                }
            } else if ( (SampleWebMeta.getClientReference()).equals(field.key)) {
                list.add(field);
            } else if ( (SampleWebMeta.getProjectId()).equals(field.key)) {
                list.add(field);
            }
        }
        return list;
    }

    private class FinalReportEnvironmentalVO {

        private Datetime collectedFrom, collectedTo;
        private String   clientReference;
        private Integer  projectCode;

        public Datetime getCollectedFrom() {
            return collectedFrom;
        }

        public void setCollectedFrom(Datetime collectedFrom) {
            this.collectedFrom = DataBaseUtil.toYD(collectedFrom);
        }

        public Datetime getCollectedTo() {
            return collectedTo;
        }

        public void setCollectedTo(Datetime collectedTo) {
            this.collectedTo = DataBaseUtil.toYD(collectedTo);
        }

        public String getClientReference() {
            return clientReference;
        }

        public void setClientReference(String clientReference) {
            this.clientReference = clientReference;
        }

        public Integer getProjectCode() {
            return projectCode;
        }

        public void setProjectCode(Integer projectCode) {
            this.projectCode = projectCode;
        }
    }
}
