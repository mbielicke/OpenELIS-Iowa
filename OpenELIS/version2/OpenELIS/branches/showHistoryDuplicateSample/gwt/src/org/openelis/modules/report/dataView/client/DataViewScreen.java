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
package org.openelis.modules.report.dataView.client;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;

import org.openelis.cache.CategoryCache;
import org.openelis.constants.Messages;
import org.openelis.domain.DataViewVO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdNameVO;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.CalendarLookUp;
import org.openelis.gwt.widget.CheckBox;
import org.openelis.gwt.widget.DateField;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.Field;
import org.openelis.gwt.widget.FileLoad;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.meta.SampleWebMeta;
import org.openelis.modules.project.client.ProjectService;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.widget.WindowInt;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.TabPanel;

public class DataViewScreen extends Screen {
    private DataViewVO           data;
    private DataViewScreen       screen;
    private TextBox              analysisTestName, analysisMethodName, clientReference,
                                 reportToOrganizationName;
    private TextBox<Integer>     accessionNumberFrom, accessionNumberTo; 
    private CheckBox             excludeResultOverride, excludeResults, excludeAuxData;
    private Dropdown<Integer>    analysisStatusId, projectId;
    private Dropdown<String>     analysisIsReportable;
    private CalendarLookUp       analysisCompletedDateFrom, analysisCompletedDateTo,
                                 analysisReleasedDateFrom, analysisReleasedDateTo, collectionDateFrom,
                                 collectionDateTo, receivedDateFrom, receivedDateTo, enteredDateFrom,
                                 enteredDateTo;
    private CommonTab            commonTab;
    private EnvironmentalTab     environmentalTab;
    private PrivateWellTab       privateWellTab;
    private SDWISTab             sdwisTab;
    private Tabs                 tab;
    private FilterScreen         filter;
    private FileLoad             fileLoad;
    private AppButton            saveQueryButton, executeQueryButton;
    private TabPanel             tabPanel;
    private DataViewReportScreen reportScreen;
    private int                  pairsFilled;

    private enum Tabs {
        QUERY, COMMON, ENVIRONMENTAL, PRIVATE_WELL, SDWIS;
    };

    public DataViewScreen(WindowInt window) throws Exception {
        super((ScreenDefInt)GWT.create(DataViewDef.class));

        setWindow(window);

        tab = Tabs.QUERY;

        data = createData();

        initialize();
        setState(State.DEFAULT);
        initializeDropdowns();
        DataChangeEvent.fire(this);
    }

    private void initialize() {
        //
        // button panel buttons
        //

        screen = this;

        saveQueryButton = (AppButton)def.getWidget("saveQueryButton");
        addScreenHandler(saveQueryButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                saveQuery();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                saveQueryButton.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        fileLoad = (FileLoad)def.getWidget("fileUpload");

//        if (GWT.isScript())
            // used for normal mode
//            fileLoad.setAction("upload");
//        else
            // used for hosted mode
            fileLoad.setAction("openelis/upload");

        fileLoad.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
            public void onSubmitComplete(SubmitCompleteEvent event) {
                openQuery();
            }
        });

        executeQueryButton = (AppButton)def.getWidget("executeQueryButton");
        addScreenHandler(executeQueryButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                executeQuery();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                executeQueryButton.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        analysisTestName = (TextBox)def.getWidget(SampleWebMeta.getAnalysisTestName());
        addScreenHandler(analysisTestName, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                analysisTestName.setValue(data.getAnalysisTestName());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setAnalysisTestName(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                analysisTestName.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        analysisMethodName = (TextBox)def.getWidget(SampleWebMeta.getAnalysisMethodName());
        addScreenHandler(analysisMethodName, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                analysisMethodName.setValue(data.getAnalysisTestMethodName());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setAnalysisTestMethodName(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                analysisMethodName.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        excludeResultOverride = (CheckBox)def.getWidget("excludeResultOverride");
        addScreenHandler(excludeResultOverride, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                excludeResultOverride.setValue(data.getExcludeResultOverride());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setExcludeResultOverride(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                excludeResultOverride.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        analysisStatusId = (Dropdown<Integer>)def.getWidget(SampleWebMeta.getAnalysisStatusId());
        analysisStatusId.setMultiSelect(true);
        addScreenHandler(analysisStatusId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                analysisStatusId.setSelection(data.getAnalysisStatusId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                data.setAnalysisStatusId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                analysisStatusId.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        analysisIsReportable = (Dropdown<String>)def.getWidget(SampleWebMeta.getAnalysisIsReportable());
        addScreenHandler(analysisIsReportable, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                analysisIsReportable.setSelection(data.getAnalysisIsReportable());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setAnalysisIsReportable(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                analysisIsReportable.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        analysisCompletedDateFrom = (CalendarLookUp)def.getWidget(SampleWebMeta.getAnalysisCompletedDateFrom());
        addScreenHandler(analysisCompletedDateFrom, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                analysisCompletedDateFrom.setValue(DataBaseUtil.toYD(data.getAnalysisCompletedDateFrom()));
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                data.setAnalysisCompletedDateFrom(event.getValue().getDate());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                analysisCompletedDateFrom.enable(EnumSet.of(State.DEFAULT)
                                                        .contains(event.getState()));
            }
        });

        analysisCompletedDateTo = (CalendarLookUp)def.getWidget(SampleWebMeta.getAnalysisCompletedDateTo());
        addScreenHandler(analysisCompletedDateTo, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                analysisCompletedDateTo.setValue(DataBaseUtil.toYD(data.getAnalysisCompletedDateTo()));
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                data.setAnalysisCompletedDateTo(event.getValue().getDate());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                analysisCompletedDateTo.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        analysisReleasedDateFrom = (CalendarLookUp)def.getWidget(SampleWebMeta.getAnalysisReleasedDateFrom());
        addScreenHandler(analysisReleasedDateFrom, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                analysisReleasedDateFrom.setValue(DataBaseUtil.toYM(data.getAnalysisReleasedDateFrom()));
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                data.setAnalysisReleasedDateFrom(event.getValue().getDate());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                analysisReleasedDateFrom.enable(EnumSet.of(State.DEFAULT)
                                                       .contains(event.getState()));
            }
        });

        analysisReleasedDateTo = (CalendarLookUp)def.getWidget(SampleWebMeta.getAnalysisReleasedDateTo());
        addScreenHandler(analysisReleasedDateTo, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                analysisReleasedDateTo.setValue(DataBaseUtil.toYM(data.getAnalysisReleasedDateTo()));
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                data.setAnalysisReleasedDateTo(event.getValue().getDate());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                analysisReleasedDateTo.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        excludeResults = (CheckBox)def.getWidget("excludeResults");
        addScreenHandler(excludeResults, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                excludeResults.setValue(data.getExcludeResults());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setExcludeResults(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                excludeResults.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        excludeAuxData = (CheckBox)def.getWidget("excludeAuxData");
        addScreenHandler(excludeAuxData, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                excludeAuxData.setValue(data.getExcludeAuxData());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setExcludeAuxData(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                excludeAuxData.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        accessionNumberFrom = (TextBox)def.getWidget(SampleWebMeta.getAccessionNumberFrom());
        addScreenHandler(accessionNumberFrom, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                accessionNumberFrom.setFieldValue(data.getAccessionNumberFrom());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                data.setAccessionNumberFrom(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                accessionNumberFrom.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        accessionNumberTo = (TextBox)def.getWidget(SampleWebMeta.getAccessionNumberTo());
        addScreenHandler(accessionNumberTo, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                accessionNumberTo.setFieldValue(data.getAccessionNumberTo());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                data.setAccessionNumberTo(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                accessionNumberTo.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        collectionDateFrom = (CalendarLookUp)def.getWidget(SampleWebMeta.getCollectionDateFrom());
        addScreenHandler(collectionDateFrom, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                collectionDateFrom.setValue(DataBaseUtil.toYD(data.getCollectionDateFrom()));
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                data.setCollectionDateFrom(event.getValue().getDate());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                collectionDateFrom.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        collectionDateTo = (CalendarLookUp)def.getWidget(SampleWebMeta.getCollectionDateTo());
        addScreenHandler(collectionDateTo, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                collectionDateTo.setValue(DataBaseUtil.toYD(data.getCollectionDateTo()));
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                data.setCollectionDateTo(event.getValue().getDate());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                collectionDateTo.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        receivedDateFrom = (CalendarLookUp)def.getWidget(SampleWebMeta.getReceivedDateFrom());
        addScreenHandler(receivedDateFrom, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                receivedDateFrom.setValue(DataBaseUtil.toYM(data.getReceivedDateFrom()));
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                data.setReceivedDateFrom(event.getValue().getDate());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                receivedDateFrom.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        receivedDateTo = (CalendarLookUp)def.getWidget(SampleWebMeta.getReceivedDateTo());
        addScreenHandler(receivedDateTo, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                receivedDateTo.setValue(DataBaseUtil.toYM(data.getReceivedDateTo()));
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                data.setReceivedDateTo(event.getValue().getDate());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                receivedDateTo.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        enteredDateFrom = (CalendarLookUp)def.getWidget(SampleWebMeta.getEnteredDateFrom());
        addScreenHandler(enteredDateFrom, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                enteredDateFrom.setValue(DataBaseUtil.toYM(data.getEnteredDateFrom()));
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                data.setEnteredDateFrom(event.getValue().getDate());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                enteredDateFrom.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        enteredDateTo = (CalendarLookUp)def.getWidget(SampleWebMeta.getEnteredDateTo());
        addScreenHandler(enteredDateTo, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                enteredDateTo.setValue(DataBaseUtil.toYM(data.getEnteredDateTo()));
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                data.setEnteredDateTo(event.getValue().getDate());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                enteredDateTo.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
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
                clientReference.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        projectId = (Dropdown<Integer>)def.getWidget(SampleWebMeta.getProjectId());
        projectId.setMultiSelect(true);
        addScreenHandler(projectId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                projectId.setValue(data.getProjectId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                data.setProjectId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                projectId.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        reportToOrganizationName = (TextBox)def.getWidget("reportToOrganizationName");
        addScreenHandler(reportToOrganizationName, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                reportToOrganizationName.setValue(data.getReportToOrganizationName());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setReportToOrganizationName(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                reportToOrganizationName.enable(EnumSet.of(State.DEFAULT)
                                                       .contains(event.getState()));
            }
        });

        //
        // tabs
        //
        tabPanel = (TabPanel)def.getWidget("tabPanel");
        tabPanel.addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {
            public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {
                int i;

                // tab screen order should be the same as enum or this will
                // not work
                i = event.getItem().intValue();
                tab = Tabs.values()[i];

                window.setBusy();
                drawTabs();
                window.clearStatus();
            }
        });

        commonTab = new CommonTab(def, window);
        addScreenHandler(commonTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                commonTab.setData(data);
                if (tab == Tabs.COMMON)
                    drawTabs();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                commonTab.setState(event.getState());
            }
        });

        environmentalTab = new EnvironmentalTab(def, window);
        addScreenHandler(environmentalTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                environmentalTab.setData(data);
                if (tab == Tabs.ENVIRONMENTAL)
                    drawTabs();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                environmentalTab.setState(event.getState());
            }
        });

        privateWellTab = new PrivateWellTab(def, window);
        addScreenHandler(privateWellTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                privateWellTab.setData(data);
                if (tab == Tabs.PRIVATE_WELL)
                    drawTabs();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                privateWellTab.setState(event.getState());
            }
        });

        sdwisTab = new SDWISTab(def, window);
        addScreenHandler(sdwisTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                sdwisTab.setData(data);
                if (tab == Tabs.SDWIS)
                    drawTabs();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sdwisTab.setState(event.getState());
            }
        });
    }

    private void initializeDropdowns() {
        Integer id;
        ArrayList<TableDataRow> model;
        List<DictionaryDO> list;
        ArrayList<IdNameVO> projects;
        TableDataRow row;
        HashMap<Integer, Integer> map;

        model = new ArrayList<TableDataRow>();
        list = CategoryCache.getBySystemName("analysis_status");
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO d : list)
            model.add(new TableDataRow(d.getId(), d.getEntry()));

        analysisStatusId.setModel(model);
        if (model.size() > 0)
            data.setAnalysisStatusId( ((Integer)model.get(0).key));

        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        try {
            projects = ProjectService.get().fetchList();
            map = new HashMap<Integer, Integer>();
            for (IdNameVO d : projects) {
                id = d.getId();
                if (map.get(id) == null) {
                    row = new TableDataRow(id, d.getName());
                    model.add(row);
                    map.put(id, id);
                }
            }
            projectId.setModel(model);
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
            window.close();
        }

        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        model.add(new TableDataRow("Y", Messages.get().yes()));
        model.add(new TableDataRow("N", Messages.get().no()));
        analysisIsReportable.setModel(model);
    }

    public boolean validate() {
        return super.validate() && validateFromToFields();
    }

    public boolean validateFromToFields() {
        boolean valid, fromEmpty, toEmpty;

        valid = true;
        pairsFilled = 6;

        fromEmpty = analysisCompletedDateFrom.getValue() == null;
        toEmpty = analysisCompletedDateTo.getValue() == null;
        if (!fromEmpty) {
            if (toEmpty) {
                analysisCompletedDateTo.addException(new Exception(Messages.get()
                                                                           .fieldRequiredException()));
                valid = false;
            }
        } else if (!toEmpty) {
            analysisCompletedDateFrom.addException(new Exception(Messages.get()
                                                                         .fieldRequiredException()));
            valid = false;
        } else {
            pairsFilled--;
        }

        fromEmpty = analysisReleasedDateFrom.getValue() == null;
        toEmpty = analysisReleasedDateTo.getValue() == null;
        if (!fromEmpty) {
            if (toEmpty) {
                analysisReleasedDateTo.addException(new Exception(Messages.get()
                                                                          .fieldRequiredException()));
                valid = false;
            }
        } else if (!toEmpty) {
            analysisReleasedDateFrom.addException(new Exception(Messages.get()
                                                                        .fieldRequiredException()));
            valid = false;
        } else {
            pairsFilled--;
        }

        fromEmpty = DataBaseUtil.isEmpty(accessionNumberFrom.getValue());
        toEmpty = DataBaseUtil.isEmpty(accessionNumberTo.getValue());
        if (!fromEmpty) {
            if (toEmpty) {
                accessionNumberTo.addException(new Exception(Messages.get()
                                                                     .fieldRequiredException()));
                valid = false;
            }
        } else if (!toEmpty) {
            accessionNumberFrom.addException(new Exception(Messages.get().fieldRequiredException()));
            valid = false;
        } else {
            pairsFilled--;
        }

        fromEmpty = collectionDateFrom.getValue() == null;
        toEmpty = collectionDateTo.getValue() == null;
        if (!fromEmpty) {
            if (toEmpty) {
                collectionDateTo.addException(new Exception(Messages.get().fieldRequiredException()));
                valid = false;
            }
        } else if (!toEmpty) {
            collectionDateFrom.addException(new Exception(Messages.get().fieldRequiredException()));
            valid = false;
        } else {
            pairsFilled--;
        }

        fromEmpty = receivedDateFrom.getValue() == null;
        toEmpty = receivedDateTo.getValue() == null;
        if (!fromEmpty) {
            if (toEmpty) {
                receivedDateTo.addException(new Exception(Messages.get().fieldRequiredException()));
                valid = false;
            }
        } else if (!toEmpty) {
            receivedDateFrom.addException(new Exception(Messages.get().fieldRequiredException()));
            valid = false;
        } else {
            pairsFilled--;
        }

        fromEmpty = enteredDateFrom.getValue() == null;
        toEmpty = enteredDateTo.getValue() == null;
        if (!fromEmpty) {
            if (toEmpty) {
                enteredDateTo.addException(new Exception(Messages.get().fieldRequiredException()));
                valid = false;
            }
        } else if (!toEmpty) {
            enteredDateFrom.addException(new Exception(Messages.get().fieldRequiredException()));
            valid = false;
        } else {
            pairsFilled--;
        }

        if (valid && pairsFilled == 0)
            valid = false;

        return valid;
    }

    public ArrayList<QueryData> getQueryFields() {
        ArrayList<QueryData> fields;
        QueryData field;

        fields = new ArrayList<QueryData>();
        field = getQuery(analysisTestName,
                         SampleWebMeta.getAnalysisTestName(),
                         QueryData.Type.STRING);
        if (field != null)
            fields.add(field);
        field = getQuery(analysisMethodName,
                         SampleWebMeta.getAnalysisMethodName(),
                         QueryData.Type.STRING);
        if (field != null)
            fields.add(field);

        field = getQuery(analysisStatusId,
                         QueryData.Type.INTEGER,
                         SampleWebMeta.getAnalysisStatusId());
        if (field != null)
            fields.add(field);

        field = getQuery(analysisCompletedDateFrom, SampleWebMeta.getAnalysisCompletedDateFrom());
        if (field != null)
            fields.add(field);
        field = getQuery(analysisCompletedDateTo, SampleWebMeta.getAnalysisCompletedDateTo());
        if (field != null)
            fields.add(field);

        field = getQuery(analysisReleasedDateFrom, SampleWebMeta.getAnalysisReleasedDateFrom());
        if (field != null)
            fields.add(field);
        field = getQuery(analysisReleasedDateTo, SampleWebMeta.getAnalysisReleasedDateTo());
        if (field != null)
            fields.add(field);

        field = getQuery(analysisIsReportable,
                         QueryData.Type.STRING,
                         SampleWebMeta.getAnalysisIsReportable());
        if (field != null)
            fields.add(field);

        field = getQuery(accessionNumberFrom,
                         SampleWebMeta.getAccessionNumberFrom(),
                         QueryData.Type.INTEGER);
        if (field != null)
            fields.add(field);
        field = getQuery(accessionNumberTo,
                         SampleWebMeta.getAccessionNumberTo(),
                         QueryData.Type.INTEGER);
        if (field != null)
            fields.add(field);

        field = getQuery(collectionDateFrom, SampleWebMeta.getCollectionDateFrom());
        if (field != null)
            fields.add(field);
        field = getQuery(collectionDateTo, SampleWebMeta.getCollectionDateTo());
        if (field != null)
            fields.add(field);

        field = getQuery(receivedDateFrom, SampleWebMeta.getReceivedDateFrom());
        if (field != null)
            fields.add(field);
        field = getQuery(receivedDateTo, SampleWebMeta.getReceivedDateTo());
        if (field != null)
            fields.add(field);

        field = getQuery(enteredDateFrom, SampleWebMeta.getEnteredDateFrom());
        if (field != null)
            fields.add(field);
        field = getQuery(enteredDateTo, SampleWebMeta.getEnteredDateTo());
        if (field != null)
            fields.add(field);

        field = getQuery(clientReference, SampleWebMeta.getClientReference(), QueryData.Type.STRING);
        if (field != null)
            fields.add(field);

        field = getQuery(projectId, QueryData.Type.INTEGER, SampleWebMeta.getProjectId());
        if (field != null)
            fields.add(field);

        field = getQuery(reportToOrganizationName,
                         "reportToOrganizationName",
                         QueryData.Type.STRING);
        if (field != null)
            fields.add(field);

        return fields;
    }

    protected void saveQuery() {
        try {
            window.clearStatus();
            if (!validate()) {
                if (pairsFilled == 0)
                    window.setError(Messages.get().atLeastOnePairFilledException());
                else
                    window.setError(Messages.get().correctErrors());
                return;
            }

            if (reportScreen == null) {
                reportScreen = new DataViewReportScreen("saveQuery", window, "DataView.xml");
            } else {
                reportScreen.setWindow(window);
                reportScreen.setRunReportInterface("saveQuery");
                reportScreen.setAttachmentName("DataView.xml");
            }
            //
            // we don't want to serialize the following fields
            //
            data.setQueryFields(null);
            data.setAnalytes(null);
            data.setAuxFields(null);

            reportScreen.runReport(data, new AsyncCallback<ReportStatus>() {

                @Override
                public void onSuccess(ReportStatus result) {
                    String url;

                    if (result.getStatus() == ReportStatus.Status.SAVED) {
                        url = "/openelis/openelis/report?file=" + result.getMessage();
                        if (reportScreen.getAttachmentName() != null)
                            url += "&attachment=" + reportScreen.getAttachmentName();

                        Window.open(URL.encode(url), "saveQuery", null);
                        window.setDone("Generated file " + result.getMessage());
                    } else {
                        window.setDone(result.getMessage());
                    }
                }

                @Override
                public void onFailure(Throwable caught) {
                    window.setError("Failed");
                    Window.alert(caught.getMessage());
                }
            });
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
        }
    }

    protected void openQuery() {
        data = null;
        try {
            data = (DataViewVO)DataViewReportService.get().openQuery();
            if (data == null)
                Window.alert(Messages.get().fileNameNotValidException());
        } catch (Exception e) {
            Window.alert("There was an error with loading the query: " + e.getMessage());
        } finally {
            if (data == null)
                data = createData();
        }

        DataChangeEvent.fire(screen);

        commonTab.setData(data);
        environmentalTab.setData(data);
        privateWellTab.setData(data);
        sdwisTab.setData(data);

        commonTab.draw();
        environmentalTab.draw();
        privateWellTab.draw();
        sdwisTab.draw();
    }

    protected void executeQuery() {
        int cc, ec, wc, sc;
        boolean excludeResults, excludeAuxData;
        String domain;
        Query query;
        ArrayList<QueryData> queryList;
        QueryData field;
        DataViewVO ldata;

        clearErrors();

        cc = commonTab.getCheckIndicator();
        ec = environmentalTab.getCheckIndicator();
        wc = privateWellTab.getCheckIndicator();
        sc = sdwisTab.getCheckIndicator();

        if (ec + wc + sc > 1) {
            window.setError(Messages.get().selFieldsOneDomain());
            return;
        }

        excludeResults = "Y".equals(data.getExcludeResults());
        excludeAuxData = "Y".equals(data.getExcludeAuxData());

        if (excludeResults && excludeAuxData && (cc + ec + wc + sc == 0)) {
            window.setError(Messages.get().selAtleastOneField());
            return;
        }

        window.clearStatus();
        if (!validate()) {
            if (pairsFilled == 0)
                window.setError(Messages.get().atLeastOnePairFilledException());
            else
                window.setError(Messages.get().correctErrors());
            return;
        }

        queryList = createWhere(getQueryFields());
        if (queryList.size() == 0) {
            window.setError(Messages.get().emptyQueryException());
            return;
        }
        query = new Query();
        query.setFields(queryList);

        /*
         * assert the domain if the user has selected one or more checkboxes in
         * one of the tabs showing fields specific to a given domain
         */
        domain = null;
        if (ec == 1)
            domain = "E";
        else if (wc == 1)
            domain = "W";
        else if (sc == 1)
            domain = "S";

        if (domain != null) {
            field = new QueryData();
            field.setQuery(domain);
            field.setKey(SampleWebMeta.getDomain());
            field.setType(QueryData.Type.STRING);
            queryList.add(field);
        }

        data.setQueryFields(query.getFields());
        data.setAnalytes(null);
        data.setAuxFields(null);
        window.setBusy(Messages.get().querying());
        try {
            if (excludeResults && excludeAuxData) {
                if (reportScreen == null) {
                    reportScreen = new DataViewReportScreen("runReport", window, null);
                } else {
                    reportScreen.setWindow(window);
                    reportScreen.setRunReportInterface("runReport");
                    reportScreen.setAttachmentName(null);
                }

                reportScreen.runReport(data, new AsyncCallback<ReportStatus>() {

                    @Override
                    public void onSuccess(ReportStatus result) {
                        String url;

                        if (result.getStatus() == ReportStatus.Status.SAVED) {
                            url = "/openelis/openelis/report?file=" + result.getMessage();
                            if (reportScreen.getAttachmentName() != null)
                                url += "&attachment=" + reportScreen.getAttachmentName();

                            Window.open(URL.encode(url), "runReport", null);
                            window.setDone("Generated file " + result.getMessage());
                        } else {
                            window.setDone(result.getMessage());
                        }
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        window.setError("Failed");
                        Window.alert(caught.getMessage());
                    }
                });
            } else {
                ldata = DataViewReportService.get().fetchAnalyteAndAuxField(data);
                data.setAnalytes(ldata.getTestAnalytes());
                data.setAuxFields(ldata.getAuxFields());
                showFilter(data);
                window.clearStatus();
            }
        } catch (NotFoundException e) {
            window.setDone(Messages.get().noRecordsFound());
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
            window.clearStatus();
        }
    }

    protected void abort() {
        ArrayList<TableDataRow> model;

        data = createData();
        model = analysisStatusId.getData();
        if (model != null && model.size() > 0)
            data.setAnalysisStatusId((Integer)model.get(0).key);
        clearErrors();
        setState(State.DEFAULT);
        DataChangeEvent.fire(this);
    }

    private void drawTabs() {
        switch (tab) {
            case COMMON:
                commonTab.draw();
                break;
            case ENVIRONMENTAL:
                environmentalTab.draw();
                break;
            case PRIVATE_WELL:
                privateWellTab.draw();
                break;
            case SDWIS:
                sdwisTab.draw();
                break;
        }
    }

    private ArrayList<QueryData> createWhere(ArrayList<QueryData> fields) {
        int i;
        QueryData field, fcomp, fRel, fCol, fAcc, fRec, fEnt;
        ArrayList<QueryData> list;

        list = new ArrayList<QueryData>();
        fcomp = fRel = fCol = fAcc = fRec = fEnt = null;

        for (i = 0; i < fields.size(); i++) {
            field = fields.get(i);
            if ( (SampleWebMeta.getAnalysisCompletedDateFrom()).equals(field.getKey())) {
                if (fcomp == null) {
                    fcomp = field;
                    fcomp.setKey(SampleWebMeta.getAnalysisCompletedDate());
                } else {
                    fcomp.setQuery(field.getQuery() + ".." + fcomp.getQuery());
                    list.add(fcomp);
                }
            } else if ( (SampleWebMeta.getAnalysisCompletedDateTo()).equals(field.getKey())) {
                if (fcomp == null) {
                    fcomp = field;
                    fcomp.setKey(SampleWebMeta.getAnalysisCompletedDate());
                } else {
                    fcomp.setQuery(fcomp.getQuery() + ".." + field.getQuery());
                    list.add(fcomp);
                }
            } else if ( (SampleWebMeta.getAnalysisReleasedDateFrom()).equals(field.getKey())) {
                if (fRel == null) {
                    fRel = field;
                    fRel.setKey(SampleWebMeta.getAnalysisReleasedDate());
                } else {
                    fRel.setQuery(field.getQuery() + ".." + fRel.getQuery());
                    list.add(fRel);
                }
            } else if ( (SampleWebMeta.getAnalysisReleasedDateTo()).equals(field.getKey())) {
                if (fRel == null) {
                    fRel = field;
                    fRel.setKey(SampleWebMeta.getAnalysisReleasedDate());
                } else {
                    fRel.setQuery(fRel.getQuery() + ".." + field.getQuery());
                    list.add(fRel);
                }
            } else if ( (SampleWebMeta.getAccessionNumberFrom()).equals(field.getKey())) {
                if (fAcc == null) {
                    fAcc = field;
                    fAcc.setKey(SampleWebMeta.getAccessionNumber());
                } else {
                    fAcc.setQuery(field.getQuery() + ".." + fAcc.getQuery());
                    list.add(fAcc);
                }
            } else if ( (SampleWebMeta.getAccessionNumberTo()).equals(field.getKey())) {
                if (fAcc == null) {
                    fAcc = field;
                    fAcc.setKey(SampleWebMeta.getAccessionNumber());
                } else {
                    fAcc.setQuery(fAcc.getQuery() + ".." + field.getQuery());
                    list.add(fAcc);
                }
            } else if ( (SampleWebMeta.getCollectionDateFrom()).equals(field.getKey())) {
                if (fCol == null) {
                    fCol = field;
                    fCol.setKey(SampleWebMeta.getCollectionDate());
                } else {
                    fCol.setQuery(field.getQuery() + ".." + fCol.getQuery());
                    list.add(fCol);
                }
            } else if ( (SampleWebMeta.getCollectionDateTo()).equals(field.getKey())) {
                if (fCol == null) {
                    fCol = field;
                    fCol.setKey(SampleWebMeta.getCollectionDate());
                } else {
                    fCol.setQuery(fCol.getQuery() + ".." + field.getQuery());
                    list.add(fCol);
                }
            } else if ( (SampleWebMeta.getReceivedDateFrom()).equals(field.getKey())) {
                if (fRec == null) {
                    fRec = field;
                    fRec.setKey(SampleWebMeta.getReceivedDate());
                } else {
                    fRec.setQuery(field.getQuery() + ".." + fRec.getQuery());
                    list.add(fRec);
                }
            } else if ( (SampleWebMeta.getReceivedDateTo()).equals(field.getKey())) {
                if (fRec == null) {
                    fRec = field;
                    fRec.setKey(SampleWebMeta.getReceivedDate());
                } else {
                    fRec.setQuery(fRec.getQuery() + ".." + field.getQuery());
                    list.add(fRec);
                }
            } else if ( (SampleWebMeta.getEnteredDateFrom()).equals(field.getKey())) {
                if (fEnt == null) {
                    fEnt = field;
                    fEnt.setKey(SampleWebMeta.getEnteredDate());
                } else {
                    fEnt.setQuery(field.getQuery() + ".." + fEnt.getQuery());
                    list.add(fEnt);
                }
            } else if ( (SampleWebMeta.getEnteredDateTo()).equals(field.getKey())) {
                if (fEnt == null) {
                    fEnt = field;
                    fEnt.setKey(SampleWebMeta.getEnteredDate());
                } else {
                    fEnt.setQuery(fEnt.getQuery() + ".." + field.getQuery());
                    list.add(fEnt);
                }
            } else {
                list.add(field);
            }
        }
        return list;
    }

    private DataViewVO createData() {
        DataViewVO data;

        data = new DataViewVO();
        data.setExcludeResultOverride("N");
        data.setExcludeResults("N");
        data.setAccessionNumber("N");
        data.setRevision("N");
        data.setCollectionDate("N");
        data.setReceivedDate("N");
        data.setEnteredDate("N");
        data.setReleasedDate("N");
        data.setStatusId("N");
        data.setProjectName("N");
        data.setClientReferenceHeader("N");
        data.setOrganizationId("N");
        data.setOrganizationName("N");
        data.setOrganizationAttention("N");
        data.setOrganizationAddressMultipleUnit("N");
        data.setOrganizationAddressAddress("N");
        data.setOrganizationAddressCity("N");
        data.setOrganizationAddressState("N");
        data.setOrganizationAddressZipCode("N");
        data.setSampleItemTypeofSampleId("N");
        data.setSampleItemSourceOfSampleId("N");
        data.setSampleItemSourceOther("N");
        data.setSampleItemContainerId("N");
        data.setSampleItemContainerReference("N");
        data.setSampleItemItemSequence("N");
        data.setAnalysisTestNameHeader("N");
        data.setAnalysisTestMethodNameHeader("N");
        data.setAnalysisStatusIdHeader("N");
        data.setAnalysisRevision("N");
        data.setAnalysisIsReportableHeader("N");
        data.setAnalysisUnitOfMeasureId("N");
        data.setAnalysisQaName("N");
        data.setAnalysisCompletedDate("N");
        data.setAnalysisCompletedBy("N");
        data.setAnalysisReleasedDate("N");
        data.setAnalysisReleasedBy("N");
        data.setAnalysisStartedDate("N");
        data.setAnalysisPrintedDate("N");
        data.setSampleEnvironmentalIsHazardous("N");
        data.setSampleEnvironmentalPriority("N");
        data.setSampleEnvironmentalCollector("N");
        data.setSampleEnvironmentalCollectorPhone("N");
        data.setSampleEnvironmentalLocation("N");
        data.setSampleEnvironmentalLocationAddressCity("N");
        data.setSampleEnvironmentalDescription("N");
        data.setSamplePrivateWellOwner("N");
        data.setSamplePrivateWellCollector("N");
        data.setSamplePrivateWellWellNumber("N");
        data.setSamplePrivateWellReportToAddressWorkPhone("N");
        data.setSamplePrivateWellReportToAddressFaxPhone("N");
        data.setSamplePrivateWellLocation("N");
        data.setSamplePrivateWellLocationAddressMultipleUnit("N");
        data.setSamplePrivateWellLocationAddressStreetAddress("N");
        data.setSamplePrivateWellLocationAddressCity("N");
        data.setSamplePrivateWellLocationAddressState("N");
        data.setSamplePrivateWellLocationAddressZipCode("N");
        data.setSampleSDWISPwsId("N");
        data.setSampleSDWISPwsName("N");
        data.setSampleSDWISStateLabId("N");
        data.setSampleSDWISFacilityId("N");
        data.setSampleSDWISSampleTypeId("N");
        data.setSampleSDWISSampleCategoryId("N");
        data.setSampleSDWISSamplePointId("N");
        data.setSampleSDWISLocation("N");
        data.setSampleSDWISCollector("N");

        return data;
    }

    protected QueryData getQuery(TextBox tb, String key, QueryData.Type type) {
        QueryData qd;
        Field field;

        field = tb.getField();

        if (DataBaseUtil.isEmpty(field.getValue()))
            return null;

        qd = new QueryData();
        qd.setQuery(field.getValue().toString());
        qd.setKey(key);
        qd.setType(type);
        return qd;
    }

    protected QueryData getQuery(Dropdown dd, QueryData.Type type, String key) {
        StringBuffer buf;
        ArrayList<TableDataRow> sels;
        QueryData qd;

        sels = dd.getSelections();
        buf = new StringBuffer();
        for (TableDataRow r : sels) {
            if (r.key != null) {
                if (buf.length() > 0)
                    buf.append("|");
                buf.append(r.key);
            }
        }

        if (buf.length() > 0) {
            qd = new QueryData();
            qd.setKey(key);
            qd.setType(type);
            qd.setQuery(buf.toString());
            return qd;
        }
        return null;
    }

    protected QueryData getQuery(CalendarLookUp c, String key) {
        QueryData qd;
        DateField field;

        field = (DateField)c.getField();

        if (field.getValue() == null)
            return null;

        qd = new QueryData();
        qd.setQuery(field.formatQuery());
        qd.setKey(key);
        qd.setType(QueryData.Type.DATE);

        return qd;
    }

    private void showFilter(DataViewVO data) {
        ScreenWindow modal;

        if (filter == null) {
            try {
                filter = new FilterScreen();
            } catch (Exception e) {
                e.printStackTrace();
                Window.alert("FilterScreen Error: " + e.getMessage());
                window.clearStatus();
                return;
            }
        }

        modal = new ScreenWindow(ScreenWindow.Mode.DIALOG);
        modal.setName(Messages.get().testAnalyteAuxDataFilter());
        modal.setContent(filter);
        filter.setData(data);
        filter.setState(State.DEFAULT);
        filter.reset();
    }
}