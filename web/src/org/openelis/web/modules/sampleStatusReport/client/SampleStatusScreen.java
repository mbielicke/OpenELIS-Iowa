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

import org.openelis.cache.UserCache;
import org.openelis.domain.Constants;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.SampleStatusWebReportVO;
import org.openelis.domain.SampleStatusWebReportVO.QAEventType;
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
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.table.TableDataCell;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.meta.SampleWebMeta;
import org.openelis.web.modules.finalReport.client.FinalReportFormVO;
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
    private FinalReportFormVO                  data;
    private ModulePermission                   userPermission;
    private CalendarLookUp                     collectedFrom, collectedTo;
    private TextBox                            accessionFrom, accessionTo, clientReference;
    private Dropdown<Integer>                  projectCode;
    private ReportScreenUtility                util;
    private DeckPanel                          deckpanel;
    private Decks                              deck;
    private HorizontalPanel                    hp;
    private AbsolutePanel                      ap;
    private TableWidget                        sampleEntTable;
    private Label<String>                      queryDeckLabel;
    private AppButton                          getSamplesButton, resetButton, backButton;
    private ArrayList<SampleStatusWebReportVO> results;
    private SampleStatusQALookupScreen         sampleStatusQALookupscreen;

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
                    data.setCollectedTo(event.getValue());
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
                    data.setCollectedFrom(event.getValue());
                }
            }
        });

        accessionFrom = (TextBox)def.getWidget(SampleWebMeta.getAccessionNumberFrom());
        addScreenHandler(accessionFrom, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                accessionFrom.setValue(data.getAccessionFrom());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                data.setAccessionFrom(event.getValue());
                if ( !DataBaseUtil.isEmpty(accessionFrom.getValue()) &&
                    DataBaseUtil.isEmpty(accessionTo.getValue())) {
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
                if ( !DataBaseUtil.isEmpty(accessionTo.getValue()) &&
                    DataBaseUtil.isEmpty(accessionFrom.getValue())) {
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

            public void onStateChange(StateChangeEvent<State> event) {
                clientReference.enable(EnumSet.of(State.ADD).contains(event.getState()));
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setClientReference(event.getValue());
            }
        });

        projectCode = (Dropdown)def.getWidget(SampleWebMeta.getProjectId());
        projectCode.setMultiSelect(true);
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
        addScreenHandler(sampleEntTable,
                         new ScreenEventHandler<ArrayList<TableDataRow>>() {
                             public void onDataChange(DataChangeEvent event) {
                                 sampleEntTable.load(getTableModel());
                             }

                             public void onStateChange(StateChangeEvent<State> event) {
                                 sampleEntTable.enable(true);
                             }
                         });

        sampleEntTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                String s, str[];
                Integer id;

                ScreenWindow modal;
                TableDataRow row;
                TableDataCell cell;

                cell = sampleEntTable.getCell(event.getRow(), event.getCol());

                if (event.getCol() == 6 && cell.getValue() != null) {
                    if (sampleStatusQALookupscreen == null) {
                        try {
                            sampleStatusQALookupscreen = new SampleStatusQALookupScreen();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Window.alert("SampleStatusQALookup error: " + e.getMessage());
                            return;
                        }
                    }
                    modal = new ScreenWindow(ScreenWindow.Mode.DIALOG, false);
                    modal.setName(consts.get("sampleStatusQALookUp"));
                    modal.setContent(sampleStatusQALookupscreen);

                    row = sampleEntTable.getRow(event.getRow());
                    s = (String)row.data;
                    str = s.split(":");
                    id = Integer.parseInt(str[1]);
                    if ("SAMPLE".equals(str[0]))
                        sampleStatusQALookupscreen.refresh(id,
                                                           SampleStatusQALookupScreen.Type.SAMPLE);
                    else
                        sampleStatusQALookupscreen.refresh(id,
                                                           SampleStatusQALookupScreen.Type.ANALYSIS);
                }
                event.cancel();
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
        ArrayList<IdNameVO> list;
        TableDataRow row;

        list = null;
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        try {
            list = service.callList("getSampleStatusProjectList");
            for (int counter = 0; counter < list.size(); counter++ ) {
                row = new TableDataRow(list.get(counter).getId(), list.get(counter)
                                                                      .getName());
                model.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        projectCode.setModel(model);
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
                loadDeck(list);
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
                backButton.setVisible(true);
                break;
            case LIST:
                deckpanel.showWidget(0);
                deck = Decks.QUERY;
                break;
        }
    }

    public void setResults(ArrayList<SampleStatusWebReportVO> results) {
        this.results = results;
        setState(State.ADD);
        DataChangeEvent.fire(this);
    }

    private ArrayList<TableDataRow> getTableModel() {
        int accRow;
        Integer accNumPrev, accNum;
        String completed, inProgress;
        Date temp;
        Datetime temp1;
        ArrayList<TableDataRow> model;
        TableDataRow row;

        model = new ArrayList<TableDataRow>();
        if (results == null || results.size() == 0)
            return model;

        accNumPrev = null;
        accRow = 0;
        completed = consts.get("completed");
        inProgress = consts.get("inProgress");

        for (SampleStatusWebReportVO data : results) {
            accNum = data.getAccessionNumber();
            /*
             * If analysis status is Released, screen displays
             * "Completed status", for all other statuses screen displays
             * "In Progress".
             */
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

                accRow++ ;
                row = new TableDataRow(7);
                row.cells.get(0).setValue(data.getAccessionNumber());
                row.cells.get(1).setValue(data.getCollector());
                row.cells.get(3).setValue(temp1);
                row.cells.get(4).setValue(data.getReceivedDate());
                row.cells.get(5).setValue(data.getClientReference());
                if ( (QAEventType.OVERRIDE).equals(data.getSampleQA()))
                    row.cells.get(6).setValue("No Result");
                else if ( (QAEventType.WARNING).equals(data.getSampleQA()))
                    row.cells.get(6).setValue("Warning");

                row.style = (accRow % 2 == 0) ? "AltTableRow" : "";
                row.data = "SAMPLE:" + data.getSampleId();
                model.add(row);

                row = new TableDataRow(7);
                row.cells.get(1).setValue(data.getTestReportingDescription() + " : " +
                                          data.getMethodReportingDescription());
                row.cells.get(2)
                         .setValue(Constants.dictionary().ANALYSIS_RELEASED.equals(data.getStatusId()) ? completed : inProgress);
                if ( (QAEventType.OVERRIDE).equals(data.getAnalysisQA()))
                    row.cells.get(6).setValue("No Result");
                else if ( (QAEventType.WARNING).equals(data.getAnalysisQA()))
                    row.cells.get(6).setValue("Warning");
                row.data = "ANALYSIS:" + data.getAnalysisId();
                row.style = (accRow % 2 == 0) ? "AltTableRow" : "";
                model.add(row);
            } else {
                row = new TableDataRow(7);
                row.cells.get(1).setValue(data.getTestReportingDescription() + " : " +
                                          data.getMethodReportingDescription());
                row.cells.get(2)
                         .setValue(Constants.dictionary().ANALYSIS_RELEASED.equals(data.getStatusId()) ? completed : inProgress);
                if ( (QAEventType.OVERRIDE).equals(data.getAnalysisQA()))
                    row.cells.get(6).setValue("No Result");
                else if ( (QAEventType.WARNING).equals(data.getAnalysisQA()))
                    row.cells.get(6).setValue("Warning");
                row.data = "ANALYSIS:" + data.getAnalysisId();
                row.style = (accRow % 2 == 0) ? "AltTableRow" : "";
                model.add(row);
            }
            accNumPrev = accNum;
        }
        return model;
    }

    private ArrayList<QueryData> createWhereFromParamFields(ArrayList<QueryData> fields) {
        int i;
        QueryData field, fCol, fAcc;
        ArrayList<QueryData> list;

        list = new ArrayList<QueryData>();
        fCol = fAcc = null;

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
            } else if ( (SampleWebMeta.getAccessionNumberFrom()).equals(field.key)) {
                if (fAcc == null) {
                    fAcc = field;
                    fAcc.key = SampleWebMeta.getAccessionNumber();
                } else {
                    fAcc.query = field.query + ".." + fAcc.query;
                    list.add(fAcc);
                }
            } else if ( (SampleWebMeta.getAccessionNumberTo()).equals(field.key)) {
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
