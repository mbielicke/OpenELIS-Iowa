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
import java.util.EnumSet;
import java.util.HashMap;

import org.openelis.cache.UserCache;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.SampleEnvironmentalWebVO;
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
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.meta.SampleMeta;
import org.openelis.web.util.ReportScreenUtility;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;

public class FinalReportEnvScreen extends Screen {

    private FinalReportEnvironmentalVO data;
    private ModulePermission           userPermission;
    private CalendarLookUp             releasedFrom, releasedTo, collectedFrom, collectedTo;
    private TextBox                    collectorName, accessionFrom, accessionTo, clientReference, collectionSite,
                                       collectionTown;
    private Dropdown<Integer>          projectCode;   
    private AppButton                  getSamplesButton, resetButton;
    private SampleListScreen           sampleListScreen;
    private ReportScreenUtility        util;
    private String                     clause;
    private HashMap<String, String>    map;
    private String                     valueOrg;

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

        releasedFrom = (CalendarLookUp)def.getWidget("RELEASED_FROM");
        addScreenHandler(releasedFrom, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                releasedFrom.setValue(data.getReleasedFrom());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                releasedFrom.enable(EnumSet.of(State.ADD).contains(event.getState()));
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
        });

        collectedFrom = (CalendarLookUp)def.getWidget("COLLECTED_FROM");
        addScreenHandler(collectedFrom, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                collectedFrom.setValue(data.getCollectedFrom());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                collectedFrom.enable(EnumSet.of(State.ADD).contains(event.getState()));
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
        });

        collectorName = (TextBox)def.getWidget("COLLECTOR_NAME");
        addScreenHandler(collectorName, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                collectorName.setValue(data.getCollectorName());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                collectorName.enable(EnumSet.of(State.ADD).contains(event.getState()));
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
        });

        accessionTo = (TextBox)def.getWidget("ACCESSION_TO");
        addScreenHandler(accessionTo, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                accessionTo.setValue(data.getAccessionTo());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                accessionTo.enable(EnumSet.of(State.ADD).contains(event.getState()));
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
        });

        collectionSite = (TextBox)def.getWidget("COLLECTION_SITE");
        addScreenHandler(collectionSite, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                collectionSite.setValue(data.getCollectionSite());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                collectionSite.enable(EnumSet.of(State.ADD).contains(event.getState()));
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
        });

        projectCode = (Dropdown)def.getWidget("PROJECT_CODE");
        addScreenHandler(projectCode, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                projectCode.setSelection(data.getProjectCode());
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
    }

    protected void getSamples() {
        Query query;
        QueryData qd;
        ArrayList<SampleEnvironmentalWebVO> list;

        if ( !validate()) {
            window.setError(consts.get("correctErrors"));
            return;
        }

        setWidgetValues();

        query = new Query();
        query.setFields(util.getQueryFields());
        valueOrg = map.get(SampleMeta.getSampleOrgOrganizationId());
        qd = new QueryData();
        qd.key = SampleMeta.getSampleOrgOrganizationId();
        qd.query = valueOrg;
        qd.type = QueryData.Type.INTEGER;
        query.setFields(qd);

        try {
            list = service.callList("getSampleList", query);
            if (list != null && list.size() > 0)
                showSampleListScreen(list);
        } catch (Exception e) {
            Window.alert(e.getMessage());
        }
        window.setDone(consts.get("done"));
    }

    private void showSampleListScreen(ArrayList<SampleEnvironmentalWebVO> list) {
        ScreenWindow modal;
        if (sampleListScreen == null) {
            try {
                sampleListScreen = new SampleListScreen();
            } catch (Exception e) {
                e.printStackTrace();
                Window.alert("SampleScreen load Error: " + e.getMessage());
                return;
            }
        }

        modal = new ScreenWindow(ScreenWindow.Mode.LOOK_UP);
        modal.setName(consts.get("chooseSampleCriteria"));
        modal.setContent(sampleListScreen);
        sampleListScreen.setState(State.DEFAULT);
        sampleListScreen.setResults(list);
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

    private void initializeDropdowns() {
        ArrayList<TableDataRow> model;
        ArrayList<IdNameVO> list;
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
    }

    private class FinalReportEnvironmentalVO {

        private Datetime releasedFrom, releasedTo, collectedFrom, collectedTo;
        private String   collectorName, clientReference, collectionSite, collectionTown;
        private Integer  accessionFrom, accessionTo, projectCode;

        public Datetime getReleasedFrom() {
            return releasedFrom;
        }

        public Datetime getReleasedTo() {
            return releasedTo;
        }

        public Datetime getCollectedFrom() {
            return collectedFrom;
        }

        public Datetime getCollectedTo() {
            return collectedTo;
        }

        public String getCollectorName() {
            return collectorName;
        }

        public String getClientReference() {
            return clientReference;
        }

        public String getCollectionSite() {
            return collectionSite;
        }

        public String getCollectionTown() {
            return collectionTown;
        }

        public Integer getAccessionFrom() {
            return accessionFrom;
        }

        public Integer getAccessionTo() {
            return accessionTo;
        }

        public Integer getProjectCode() {
            return projectCode;
        }
    }
}
