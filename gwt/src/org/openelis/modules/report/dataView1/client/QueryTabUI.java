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
package org.openelis.modules.report.dataView1.client;

import static org.openelis.modules.main.client.Logger.*;
import static org.openelis.ui.screen.State.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

import org.openelis.cache.CategoryCache;
import org.openelis.cache.SectionCache;
import org.openelis.constants.Messages;
import org.openelis.domain.Constants;
import org.openelis.domain.DataView1VO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.SectionDO;
import org.openelis.meta.SampleWebMeta;
import org.openelis.modules.project.client.ProjectService;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.CheckBox;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.HasExceptions;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.MultiDropdown;
import org.openelis.ui.widget.Queryable;
import org.openelis.ui.widget.TextBox;
import org.openelis.ui.widget.calendar.Calendar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;

public class QueryTabUI extends Screen {
    @UiTemplate("QueryTab.ui.xml")
    interface QueryTabUIBinder extends UiBinder<Widget, QueryTabUI> {
    };

    private static QueryTabUIBinder   uiBinder = GWT.create(QueryTabUIBinder.class);

    @UiField
    protected TextBox<Integer>        accessionNumberFrom, accessionNumberTo;

    @UiField
    protected Calendar                collectionDateFrom, collectionDateTo, receivedDateFrom,
                    receivedDateTo, enteredDateFrom, enteredDateTo, releasedDateFrom,
                    releasedDateTo, analysisCompletedDateFrom, analysisCompletedDateTo,
                    analysisReleasedDateFrom, analysisReleasedDateTo;

    @UiField
    protected TextBox<String>         clientReference, reportTo, analysisTestName,
                    analysisMethodName;

    @UiField
    protected MultiDropdown<Integer>  projectId, analysisStatusId, analysisSectionId;

    @UiField
    protected Dropdown<String>        domain, analysisIsReportable, result, auxData;

    @UiField
    protected CheckBox                excludeResultOverride;

    protected Screen                  parentScreen;

    protected EventBus                parentBus;

    protected DataView1VO             data;

    protected HashMap<String, String> fieldValues;

    private static final String       EXCLUDE_ALL = "excludeAll",
                    INCLUDE_NOT_REPORTABLE = "includeNotReportable";

    public QueryTabUI(Screen parentScreen) {
        this.parentScreen = parentScreen;
        this.parentBus = parentScreen.getEventBus();
        initWidget(uiBinder.createAndBindUi(this));
        initialize();

        data = null;
    }

    public void initialize() {
        String dom;
        Item<Integer> row;
        Item<String> strow;
        ArrayList<Item<Integer>> model;
        ArrayList<Item<String>> stmodel;
        ArrayList<IdNameVO> projects;
        ArrayList<DictionaryDO> entries;

        addScreenHandler(accessionNumberFrom,
                         SampleWebMeta.getAccessionNumberFrom(),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent<Integer> event) {
                                 accessionNumberFrom.setValue(getAccessionNumberFrom());
                             }
                             
                             public void onValueChange(ValueChangeEvent<Integer> event) {
                                 validateFromTo(accessionNumberFrom, accessionNumberTo);
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 accessionNumberFrom.setEnabled(isState(DEFAULT));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? accessionNumberTo : auxData;
                             }
                         });

        addScreenHandler(accessionNumberTo,
                         SampleWebMeta.getAccessionNumberTo(),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent<Integer> event) {
                                 accessionNumberTo.setValue(getAccessionNumberTo());
                             }
                             
                             public void onValueChange(ValueChangeEvent<Integer> event) {
                                 validateFromTo(accessionNumberFrom, accessionNumberTo);
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 accessionNumberTo.setEnabled(isState(DEFAULT));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? collectionDateFrom : accessionNumberFrom;
                             }
                         });

        addScreenHandler(collectionDateFrom,
                         SampleWebMeta.getCollectionDateFrom(),
                         new ScreenHandler<Datetime>() {
                             public void onDataChange(DataChangeEvent<Datetime> event) {
                                 collectionDateFrom.setValue(getCollectionDateFrom());
                             }
                             
                             public void onValueChange(ValueChangeEvent<Datetime> event) {
                                 validateFromTo(collectionDateFrom, collectionDateTo);
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 collectionDateFrom.setEnabled(isState(DEFAULT));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? collectionDateTo : accessionNumberTo;
                             }
                         });

        addScreenHandler(collectionDateTo,
                         SampleWebMeta.getCollectionDateTo(),
                         new ScreenHandler<Datetime>() {
                             public void onDataChange(DataChangeEvent<Datetime> event) {
                                 collectionDateTo.setValue(getCollectionDateTo());
                             }
                             
                             public void onValueChange(ValueChangeEvent<Datetime> event) {
                                 validateFromTo(collectionDateFrom, collectionDateTo);
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 collectionDateTo.setEnabled(isState(DEFAULT));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? receivedDateFrom : collectionDateFrom;
                             }
                         });

        addScreenHandler(receivedDateFrom,
                         SampleWebMeta.getReceivedDateFrom(),
                         new ScreenHandler<Datetime>() {
                             public void onDataChange(DataChangeEvent<Datetime> event) {
                                 receivedDateFrom.setValue(getReceivedDateFrom());
                             }
                             
                             public void onValueChange(ValueChangeEvent<Datetime> event) {
                                 validateFromTo(receivedDateFrom, receivedDateTo);
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 receivedDateFrom.setEnabled(isState(DEFAULT));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? receivedDateTo : collectionDateTo;
                             }
                         });

        addScreenHandler(receivedDateTo,
                         SampleWebMeta.getReceivedDateTo(),
                         new ScreenHandler<Datetime>() {
                             public void onDataChange(DataChangeEvent<Datetime> event) {
                                 receivedDateTo.setValue(getReceivedDateTo());
                             }
                             
                             public void onValueChange(ValueChangeEvent<Datetime> event) {
                                 validateFromTo(receivedDateFrom, receivedDateTo);
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 receivedDateTo.setEnabled(isState(DEFAULT));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? enteredDateFrom : receivedDateFrom;
                             }
                         });

        addScreenHandler(enteredDateFrom,
                         SampleWebMeta.getEnteredDateFrom(),
                         new ScreenHandler<Datetime>() {
                             public void onDataChange(DataChangeEvent<Datetime> event) {
                                 enteredDateFrom.setValue(getEnteredDateFrom());
                             }
                             
                             public void onValueChange(ValueChangeEvent<Datetime> event) {
                                 validateFromTo(enteredDateFrom, enteredDateTo);
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 enteredDateFrom.setEnabled(isState(DEFAULT));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? enteredDateTo : receivedDateTo;
                             }
                         });

        addScreenHandler(enteredDateTo,
                         SampleWebMeta.getEnteredDateTo(),
                         new ScreenHandler<Datetime>() {
                             public void onDataChange(DataChangeEvent<Datetime> event) {
                                 enteredDateTo.setValue(getEnteredDateTo());
                             }
                             
                             public void onValueChange(ValueChangeEvent<Datetime> event) {
                                 validateFromTo(enteredDateFrom, enteredDateTo);
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 enteredDateTo.setEnabled(isState(DEFAULT));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? releasedDateFrom : enteredDateFrom;
                             }
                         });

        addScreenHandler(releasedDateFrom,
                         SampleWebMeta.getReleasedDateFrom(),
                         new ScreenHandler<Datetime>() {
                             public void onDataChange(DataChangeEvent<Datetime> event) {
                                 releasedDateFrom.setValue(getReleasedDateFrom());
                             }
                             
                             public void onValueChange(ValueChangeEvent<Datetime> event) {
                                 validateFromTo(releasedDateFrom, releasedDateTo);
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 releasedDateFrom.setEnabled(isState(DEFAULT));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? releasedDateTo : enteredDateTo;
                             }
                         });

        addScreenHandler(releasedDateTo,
                         SampleWebMeta.getReleasedDateTo(),
                         new ScreenHandler<Datetime>() {
                             public void onDataChange(DataChangeEvent<Datetime> event) {
                                 releasedDateTo.setValue(getReleasedDateTo());
                             }
                             
                             public void onValueChange(ValueChangeEvent<Datetime> event) {
                                 validateFromTo(releasedDateFrom, releasedDateTo);
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 releasedDateTo.setEnabled(isState(DEFAULT));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? clientReference : releasedDateFrom;
                             }
                         });

        addScreenHandler(clientReference,
                         SampleWebMeta.getClientReference(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 clientReference.setValue(getClientReference());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 clientReference.setEnabled(isState(DEFAULT));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? projectId : releasedDateTo;
                             }
                         });

        addScreenHandler(projectId,
                         SampleWebMeta.getProjectId(),
                         new ScreenHandler<ArrayList<Integer>>() {
                             public void onDataChange(DataChangeEvent<ArrayList<Integer>> event) {
                                 projectId.setValue(getProjectId());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 projectId.setEnabled(isState(DEFAULT));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? reportTo : clientReference;
                             }
                         });

        addScreenHandler(reportTo,
                         SampleWebMeta.getSampleOrgOrganizationName(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 reportTo.setValue(getSampleOrgOrganizationName());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 reportTo.setEnabled(isState(DEFAULT));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? domain : projectId;
                             }
                         });

        addScreenHandler(domain, SampleWebMeta.getDomain(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent<String> event) {
                domain.setValue(getDomain());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                /*
                 * the widgets in the tabs for domains may need to be enabled or
                 * disabled based on the current domain
                 */
                parentBus.fireEvent(new DomainChangeEvent(event.getValue()));
            }

            public void onStateChange(StateChangeEvent event) {
                domain.setEnabled(isState(DEFAULT));
            }

            public Widget onTab(boolean forward) {
                return forward ? analysisTestName : reportTo;
            }
        });

        addScreenHandler(analysisTestName,
                         SampleWebMeta.getAnalysisTestName(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 analysisTestName.setValue(getAnalysisTestName());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 analysisTestName.setEnabled(isState(DEFAULT));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? analysisMethodName : domain;
                             }
                         });

        addScreenHandler(analysisMethodName,
                         SampleWebMeta.getAnalysisMethodName(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 analysisMethodName.setValue(getAnalysisMethodName());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 analysisMethodName.setEnabled(isState(DEFAULT));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? analysisIsReportable : analysisTestName;
                             }
                         });

        addScreenHandler(analysisIsReportable,
                         SampleWebMeta.getAnalysisIsReportable(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 analysisIsReportable.setValue(getAnalysisIsReportable());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 analysisIsReportable.setEnabled(isState(DEFAULT));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? analysisStatusId : analysisMethodName;
                             }
                         });

        addScreenHandler(analysisStatusId,
                         SampleWebMeta.getAnalysisStatusId(),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent<Integer> event) {
                                 analysisStatusId.setValue(getAnalysisStatusId());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 analysisStatusId.setEnabled(isState(DEFAULT));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? analysisCompletedDateFrom : analysisIsReportable;
                             }
                         });

        addScreenHandler(analysisCompletedDateFrom,
                         SampleWebMeta.getAnalysisCompletedDateFrom(),
                         new ScreenHandler<Datetime>() {
                             public void onDataChange(DataChangeEvent<Datetime> event) {
                                 analysisCompletedDateFrom.setValue(getAnalysisCompletedDateFrom());
                             }
                             
                             public void onValueChange(ValueChangeEvent<Datetime> event) {
                                 validateFromTo(analysisCompletedDateFrom, analysisCompletedDateTo);
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 analysisCompletedDateFrom.setEnabled(isState(DEFAULT));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? analysisCompletedDateTo : analysisStatusId;
                             }
                         });

        addScreenHandler(analysisCompletedDateTo,
                         SampleWebMeta.getAnalysisCompletedDateTo(),
                         new ScreenHandler<Datetime>() {
                             public void onDataChange(DataChangeEvent<Datetime> event) {
                                 analysisCompletedDateTo.setValue(getAnalysisCompletedDateTo());
                             }
                             
                             public void onValueChange(ValueChangeEvent<Datetime> event) {
                                 validateFromTo(analysisCompletedDateFrom, analysisCompletedDateTo);
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 analysisCompletedDateTo.setEnabled(isState(DEFAULT));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? analysisReleasedDateFrom
                                               : analysisCompletedDateFrom;
                             }
                         });

        addScreenHandler(analysisReleasedDateFrom,
                         SampleWebMeta.getAnalysisReleasedDateFrom(),
                         new ScreenHandler<Datetime>() {
                             public void onDataChange(DataChangeEvent<Datetime> event) {
                                 analysisReleasedDateFrom.setValue(getAnalysisReleasedDateFrom());
                             }
                             
                             public void onValueChange(ValueChangeEvent<Datetime> event) {
                                 validateFromTo(analysisReleasedDateFrom, analysisReleasedDateTo);
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 analysisReleasedDateFrom.setEnabled(isState(DEFAULT));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? analysisReleasedDateTo : analysisCompletedDateTo;
                             }
                         });

        addScreenHandler(analysisReleasedDateTo,
                         SampleWebMeta.getAnalysisReleasedDateTo(),
                         new ScreenHandler<Datetime>() {
                             public void onDataChange(DataChangeEvent<Datetime> event) {
                                 analysisReleasedDateTo.setValue(getAnalysisReleasedDateTo());
                             }
                             
                             public void onValueChange(ValueChangeEvent<Datetime> event) {
                                 validateFromTo(analysisReleasedDateFrom, analysisReleasedDateTo);
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 analysisReleasedDateTo.setEnabled(isState(DEFAULT));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? analysisSectionId : analysisReleasedDateFrom;
                             }
                         });

        addScreenHandler(analysisSectionId,
                         SampleWebMeta.getAnalysisSectionId(),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent<Integer> event) {
                                 analysisSectionId.setValue(getAnalysisSectionId());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 analysisSectionId.setEnabled(isState(DEFAULT));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? excludeResultOverride : analysisReleasedDateTo;
                             }
                         });

        addScreenHandler(excludeResultOverride,
                         "excludeResultOverride",
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 excludeResultOverride.setValue(getExcludeResultOverride());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setExcludeResultOverride(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 excludeResultOverride.setEnabled(isState(DEFAULT));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? result : analysisSectionId;
                             }
                         });

        addScreenHandler(result, "results", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent<String> event) {
                result.setValue(getResult());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                setResult(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                result.setEnabled(isState(DEFAULT));
            }

            public Widget onTab(boolean forward) {
                return forward ? auxData : excludeResultOverride;
            }
        });

        addScreenHandler(auxData, "auxData", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent<String> event) {
                auxData.setValue(getAuxData());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                setAuxData(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                auxData.setEnabled(isState(DEFAULT));
            }

            public Widget onTab(boolean forward) {
                return forward ? accessionNumberFrom : result;
            }
        });

        model = new ArrayList<Item<Integer>>();
        try {
            projects = ProjectService.get().fetchList();
            for (IdNameVO d : projects) {
                row = new Item<Integer>(d.getId(), d.getName());
                model.add(row);
            }
            projectId.setModel(model);
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
            parentScreen.getWindow().close();
        }

        stmodel = new ArrayList<Item<String>>();
        entries = CategoryCache.getBySystemName("sample_domain");
        for (DictionaryDO d : entries) {
            dom = null;
            if (Constants.dictionary().ENVIRONMENTAL.equals(d.getId()))
                dom = Constants.domain().ENVIRONMENTAL;
            else if (Constants.dictionary().PRIVATE_WELL.equals(d.getId()))
                dom = Constants.domain().PRIVATEWELL;
            else if (Constants.dictionary().SDWIS.equals(d.getId()))
                dom = Constants.domain().SDWIS;
            else if (Constants.dictionary().CLINICAL.equals(d.getId()))
                dom = Constants.domain().CLINICAL;
            else if (Constants.dictionary().NEWBORN.equals(d.getId()))
                dom = Constants.domain().NEONATAL;
            else if (Constants.dictionary().PT.equals(d.getId()))
                dom = Constants.domain().PT;

            if (dom != null) {
                strow = new Item<String>(dom, d.getEntry());
                stmodel.add(strow);
            }
        }

        domain.setModel(stmodel);

        model = new ArrayList<Item<Integer>>();
        for (DictionaryDO d : CategoryCache.getBySystemName("analysis_status")) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            model.add(row);
        }

        analysisStatusId.setModel(model);

        model = new ArrayList<Item<Integer>>();
        for (SectionDO s : SectionCache.getList()) {
            row = new Item<Integer>(s.getId(), s.getName());
            model.add(row);
        }

        analysisSectionId.setModel(model);

        stmodel = new ArrayList<Item<String>>();
        stmodel.add(new Item<String>("Y", Messages.get().gen_yes()));
        stmodel.add(new Item<String>("N", Messages.get().gen_no()));

        analysisIsReportable.setModel(stmodel);

        stmodel = new ArrayList<Item<String>>();
        stmodel.add(new Item<String>(EXCLUDE_ALL, Messages.get().dataView_excludeAll()));
        stmodel.add(new Item<String>(INCLUDE_NOT_REPORTABLE,
                                     Messages.get().dataView_includeNotReportable()));

        result.setModel(stmodel);
        auxData.setModel(stmodel);
    }

    public void setData(DataView1VO data) {
        this.data = data;
        fieldValues = new HashMap<String, String>();
        if (data == null || data.getQueryFields() == null)
            return;
        /*
         * the query string for each query field is put in a map, where the key
         * is the field's key and the value is the query string; the map is used
         * to fill the widgets, without having to iterate over the query fields
         * repeatedly; the query string of some fields like accession number are
         * split into "from" and "to" values before being set in the map
         */
        for (QueryData f : data.getQueryFields()) {
            if (SampleWebMeta.getAccessionNumber().equals(f.getKey()))
                setFromToValues(SampleWebMeta.getAccessionNumberFrom(),
                                SampleWebMeta.getAccessionNumberTo(),
                                f.getQuery());
            else if (SampleWebMeta.getCollectionDate().equals(f.getKey()))
                setFromToValues(SampleWebMeta.getCollectionDateFrom(),
                                SampleWebMeta.getCollectionDateTo(),
                                f.getQuery());
            else if (SampleWebMeta.getReceivedDate().equals(f.getKey()))
                setFromToValues(SampleWebMeta.getReceivedDateFrom(),
                                SampleWebMeta.getReceivedDateTo(),
                                f.getQuery());
            else if (SampleWebMeta.getEnteredDate().equals(f.getKey()))
                setFromToValues(SampleWebMeta.getEnteredDateFrom(),
                                SampleWebMeta.getEnteredDateTo(),
                                f.getQuery());
            else if (SampleWebMeta.getReleasedDate().equals(f.getKey()))
                setFromToValues(SampleWebMeta.getReleasedDateFrom(),
                                SampleWebMeta.getReleasedDateTo(),
                                f.getQuery());
            else if (SampleWebMeta.getAnalysisCompletedDate().equals(f.getKey()))
                setFromToValues(SampleWebMeta.getAnalysisCompletedDateFrom(),
                                SampleWebMeta.getAnalysisCompletedDateTo(),
                                f.getQuery());
            else if (SampleWebMeta.getAnalysisReleasedDate().equals(f.getKey()))
                setFromToValues(SampleWebMeta.getAnalysisReleasedDateFrom(),
                                SampleWebMeta.getAnalysisReleasedDateTo(),
                                f.getQuery());
            else
                fieldValues.put(f.getKey(), f.getQuery());
        }
    }

    public void setState(State state) {
        super.setState(state);
        /*
         * the widgets in the tabs for domains may need to be enabled or
         * disabled based on the current domain
         */
        parentBus.fireEvent(new DomainChangeEvent(getDomain()));
    }

    public void onDataChange() {
        fireDataChange();
    }

    /**
     * Returns a list of QueryData for the widgets in the tab; only one
     * QueryData is created for a pair of "from" and "to" widgets
     */
    public ArrayList<QueryData> getQueryFields() {
        QueryData field;
        ArrayList<QueryData> fields;

        fields = new ArrayList<QueryData>();

        addQueryData(accessionNumberFrom,
                     accessionNumberTo,
                     SampleWebMeta.getAccessionNumber(),
                     QueryData.Type.INTEGER,
                     fields);

        addQueryData(collectionDateFrom,
                     collectionDateTo,
                     SampleWebMeta.getCollectionDate(),
                     QueryData.Type.DATE,
                     fields);

        addQueryData(receivedDateFrom,
                     receivedDateTo,
                     SampleWebMeta.getReceivedDate(),
                     QueryData.Type.DATE,
                     fields);

        addQueryData(enteredDateFrom,
                     enteredDateTo,
                     SampleWebMeta.getEnteredDate(),
                     QueryData.Type.DATE,
                     fields);

        addQueryData(releasedDateFrom,
                     releasedDateTo,
                     SampleWebMeta.getReleasedDate(),
                     QueryData.Type.DATE,
                     fields);

        addQueryData(clientReference, SampleWebMeta.getClientReference(), fields);
        addQueryData(projectId, SampleWebMeta.getProjectId(), fields);

        /*
         * if the user is querying by report-to, add a query field for it; also
         * add a field for organization type to restrict the query to report-to
         * organizations only
         */
        field = getQueryData(reportTo, SampleWebMeta.getSampleOrgOrganizationName());
        if (field != null) {
            fields.add(field);

            field = new QueryData();
            field.setQuery(Constants.dictionary().ORG_REPORT_TO.toString());
            field.setKey(SampleWebMeta.getSampleOrgTypeId());
            field.setType(QueryData.Type.INTEGER);
            fields.add(field);
        }

        addQueryData(domain, SampleWebMeta.getDomain(), fields);
        addQueryData(analysisTestName, SampleWebMeta.getAnalysisTestName(), fields);
        addQueryData(analysisMethodName, SampleWebMeta.getAnalysisMethodName(), fields);
        addQueryData(analysisIsReportable, SampleWebMeta.getAnalysisIsReportable(), fields);
        addQueryData(analysisStatusId, SampleWebMeta.getAnalysisStatusId(), fields);

        addQueryData(analysisCompletedDateFrom,
                     analysisCompletedDateTo,
                     SampleWebMeta.getAnalysisCompletedDate(),
                     QueryData.Type.DATE,
                     fields);

        addQueryData(analysisReleasedDateFrom,
                     analysisReleasedDateTo,
                     SampleWebMeta.getAnalysisReleasedDate(),
                     QueryData.Type.DATE,
                     fields);

        addQueryData(analysisSectionId, SampleWebMeta.getAnalysisSectionId(), fields);

        return fields;
    }

    public Validation validate() {
        int pairsFilled;
        Validation validation;

        validation = super.validate();

        pairsFilled = 7;

        /*
         * keep track of the number of "from" and "to" pairs where both widgets
         * have a value
         */
        if (isFromToEmpty(accessionNumberFrom, accessionNumberTo))
            pairsFilled-- ;
        if (isFromToEmpty(collectionDateFrom, collectionDateTo))
            pairsFilled-- ;
        if (isFromToEmpty(receivedDateFrom, receivedDateTo))
            pairsFilled-- ;
        if (isFromToEmpty(enteredDateFrom, enteredDateTo))
            pairsFilled-- ;
        if (isFromToEmpty(releasedDateFrom, releasedDateTo))
            pairsFilled-- ;
        if (isFromToEmpty(analysisCompletedDateFrom, analysisCompletedDateTo))
            pairsFilled-- ;
        if (isFromToEmpty(analysisReleasedDateFrom, analysisReleasedDateTo))
            pairsFilled-- ;

        /*
         * if none of the "from" and "to" pairs of widgets has values, the query
         * shouldn't be executed; otherwise the report will take too long to
         * generate
         */
        if (pairsFilled == 0) {
            validation.addException(new Exception(Messages.get()
                                                          .dataView_atLeastOnePairFilledException()));
            validation.setStatus(Validation.Status.ERRORS);
        }

        return validation;
    }

    private Integer getAccessionNumberFrom() {
        return getInteger(SampleWebMeta.getAccessionNumberFrom());
    }

    private Integer getAccessionNumberTo() {
        return getInteger(SampleWebMeta.getAccessionNumberTo());
    }

    private Datetime getCollectionDateFrom() {
        return getDatetime(SampleWebMeta.getCollectionDateFrom(), collectionDateFrom);
    }

    private Datetime getCollectionDateTo() {
        return getDatetime(SampleWebMeta.getCollectionDateTo(), collectionDateTo);
    }

    private Datetime getReceivedDateFrom() {
        return getDatetime(SampleWebMeta.getReceivedDateFrom(), receivedDateFrom);
    }

    private Datetime getReceivedDateTo() {
        return getDatetime(SampleWebMeta.getReceivedDateTo(), receivedDateTo);
    }

    private Datetime getEnteredDateFrom() {
        return getDatetime(SampleWebMeta.getEnteredDateFrom(), enteredDateFrom);
    }

    private Datetime getEnteredDateTo() {
        return getDatetime(SampleWebMeta.getEnteredDateTo(), enteredDateTo);
    }

    private Datetime getReleasedDateFrom() {
        return getDatetime(SampleWebMeta.getReleasedDateFrom(), releasedDateFrom);
    }

    private Datetime getReleasedDateTo() {
        return getDatetime(SampleWebMeta.getReleasedDateTo(), releasedDateTo);
    }

    private String getClientReference() {
        return fieldValues.get(SampleWebMeta.getClientReference());
    }

    private ArrayList<Integer> getProjectId() {
        return getSelectedIds(SampleWebMeta.getProjectId());
    }

    private String getSampleOrgOrganizationName() {
        return fieldValues.get(SampleWebMeta.getSampleOrgOrganizationName());
    }

    private String getDomain() {
        return fieldValues.get(SampleWebMeta.getDomain());
    }

    private String getAnalysisTestName() {
        return fieldValues.get(SampleWebMeta.getAnalysisTestName());
    }

    private String getAnalysisMethodName() {
        return fieldValues.get(SampleWebMeta.getAnalysisMethodName());
    }

    private String getAnalysisIsReportable() {
        return fieldValues.get(SampleWebMeta.getAnalysisIsReportable());
    }

    private ArrayList<Integer> getAnalysisStatusId() {
        return getSelectedIds(SampleWebMeta.getAnalysisStatusId());
    }

    private Datetime getAnalysisCompletedDateFrom() {
        return getDatetime(SampleWebMeta.getAnalysisCompletedDateFrom(), analysisCompletedDateFrom);
    }

    private Datetime getAnalysisCompletedDateTo() {
        return getDatetime(SampleWebMeta.getAnalysisCompletedDateTo(), analysisCompletedDateTo);
    }

    private Datetime getAnalysisReleasedDateFrom() {
        return getDatetime(SampleWebMeta.getAnalysisReleasedDateFrom(), analysisReleasedDateFrom);
    }

    private Datetime getAnalysisReleasedDateTo() {
        return getDatetime(SampleWebMeta.getAnalysisReleasedDateTo(), analysisReleasedDateTo);
    }

    private ArrayList<Integer> getAnalysisSectionId() {
        return getSelectedIds(SampleWebMeta.getAnalysisSectionId());
    }

    private String getExcludeResultOverride() {
        if (data == null)
            return "N";
        else
            return data.getExcludeResultOverride();
    }

    private void setExcludeResultOverride(String excludeResultOverride) {
        data.setExcludeResultOverride(excludeResultOverride);
    }

    private String getResult() {
        if (data != null) {
            if ("Y".equals(data.getExcludeResults()))
                return EXCLUDE_ALL;
            else if ("Y".equals(data.getIncludeNotReportableResults()))
                return INCLUDE_NOT_REPORTABLE;
        }

        return null;
    }

    private void setResult(String result) {
        String exc, inc;

        exc = "N";
        inc = "N";
        if (EXCLUDE_ALL.equals(result))
            exc = "Y";
        else if (INCLUDE_NOT_REPORTABLE.equals(result))
            inc = "Y";

        data.setExcludeResults(exc);
        data.setIncludeNotReportableResults(inc);
    }

    private String getAuxData() {
        if (data != null) {
            if ("Y".equals(data.getExcludeAuxData()))
                return EXCLUDE_ALL;
            else if ("Y".equals(data.getIncludeNotReportableAuxData()))
                return INCLUDE_NOT_REPORTABLE;
        }

        return null;
    }

    private void setAuxData(String auxData) {
        String exc, inc;

        exc = "N";
        inc = "N";
        if (EXCLUDE_ALL.equals(auxData))
            exc = "Y";
        else if (INCLUDE_NOT_REPORTABLE.equals(auxData))
            inc = "Y";

        data.setExcludeAuxData(exc);
        data.setIncludeNotReportableAuxData(inc);
    }

    private Integer getInteger(String key) {
        String value;

        value = fieldValues.get(key);
        return !DataBaseUtil.isEmpty(value) ? Integer.valueOf(value) : null;
    }

    private Datetime getDatetime(String key, Calendar calendar) {
        String value;

        value = fieldValues.get(key);
        try {
            return !DataBaseUtil.isEmpty(value) ? calendar.getHelper().getValue(value) : null;
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
        }

        return null;
    }

    /**
     * Returns the list of selected ids for a multi-dropdown like project
     */
    private ArrayList<Integer> getSelectedIds(String key) {
        String value, sels[];
        ArrayList<Integer> ids;

        value = fieldValues.get(key);
        ids = null;
        if (DataBaseUtil.isEmpty(value))
            return ids;

        sels = value.split("\\|");
        if (sels.length > 0) {
            ids = new ArrayList<Integer>();
            for (String s : sels)
                ids.add(Integer.valueOf(s.trim()));
        }

        return ids;
    }

    /**
     * Creates a query data where the query string contains the values of the
     * passed widgets separated by a delimiter; the key and type are set to the
     * passed values; doesn't create the query data if the value of either
     * widget is null or empty
     */
    private void addQueryData(Queryable fromWidget, Queryable toWidget, String key,
                              QueryData.Type type, ArrayList<QueryData> fields) {

        QueryData fromField, toField, field;
        Object fromQuery, toQuery;

        fromField = (QueryData)fromWidget.getQuery();
        toField = (QueryData)toWidget.getQuery();
        fromQuery = fromField != null ? fromField.getQuery() : null;
        toQuery = toField != null ? toField.getQuery() : null;

        if ( !DataBaseUtil.isEmpty(fromQuery) && !DataBaseUtil.isEmpty(toQuery)) {
            field = new QueryData();
            field.setKey(key);
            field.setQuery(DataBaseUtil.concatWithSeparator(fromQuery, "..", toQuery));
            field.setType(type);
            fields.add(field);
        }
    }

    /**
     * Adds the query data for the passed widget to the passed list; sets the
     * passed key in the query data
     */
    private void addQueryData(Queryable widget, String key, ArrayList<QueryData> fields) {
        QueryData field;

        field = getQueryData(widget, key);
        if (field != null)
            fields.add(field);
    }

    /**
     * Returns a query data created from the passed widget; a query data is
     * created only if there's a query string specified in the widget; sets the
     * passed key in the query data
     */
    private QueryData getQueryData(Queryable widget, String key) {
        QueryData field;

        field = (QueryData)widget.getQuery();
        /*
         * the key is set here because it's not set when the query data is
         * created; this is because, the widget doesn't know what its key is;
         * the key is in the hashmap for screen handlers
         */
        if (field != null)
            field.setKey(key);

        return field;
    }

    /**
     * splits the query into "from" and "to" values; sets "from" as the value
     * for "fromKey" and "to" as the value for "toKey" in the map used to
     * populate the widgets on the tab
     */
    private void setFromToValues(String fromKey, String toKey, String query) {
        String value[];

        if (DataBaseUtil.isEmpty(query))
            return;

        value = query.split("\\..");
        if (value.length == 2) {
            fieldValues.put(fromKey, value[0]);
            fieldValues.put(toKey, value[1]);
        }
    }

    /**
     * If one of the widgets in the passed pair of "from" and "to" widgets has a
     * value and the other doesn't, adds "Field Required" error to the widget
     * that doesn't have a value; clears any previous programmatically added
     * errors
     */
    private void validateFromTo(Widget fromWidget, Widget toWidget) {
        HasExceptions fromHasEx, toHasEx;
        boolean fromEmpty, toEmpty;

        fromEmpty = DataBaseUtil.isEmpty( ((HasValue)fromWidget).getValue());
        toEmpty = DataBaseUtil.isEmpty( ((HasValue)toWidget).getValue());
        fromHasEx = (HasExceptions)fromWidget;
        toHasEx = (HasExceptions)toWidget;

        fromHasEx.clearEndUserExceptions();
        toHasEx.clearEndUserExceptions();
        if ( !fromEmpty) {
            if (toEmpty)
                toHasEx.addException(new Exception(Messages.get().gen_fieldRequiredException()));
        } else if ( !toEmpty) {
            fromHasEx.addException(new Exception(Messages.get().gen_fieldRequiredException()));
        }
    }

    /**
     * Returns true if none of the passed widgets has a value; returns false
     * otherwise
     */
    private boolean isFromToEmpty(HasValue fromWidget, HasValue toWidget) {
        return DataBaseUtil.isEmpty(fromWidget.getValue()) &&
               DataBaseUtil.isEmpty(toWidget.getValue());
    }
}