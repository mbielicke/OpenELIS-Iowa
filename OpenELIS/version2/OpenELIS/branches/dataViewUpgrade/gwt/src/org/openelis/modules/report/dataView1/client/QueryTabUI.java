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
import java.util.logging.Level;

import org.openelis.cache.CategoryCache;
import org.openelis.constants.Messages;
import org.openelis.domain.Constants;
import org.openelis.domain.DataView1VO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdNameVO;
import org.openelis.meta.SampleWebMeta;
import org.openelis.modules.project.client.ProjectService;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.widget.CheckBox;
import org.openelis.ui.widget.Dropdown;
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
import com.google.gwt.user.client.ui.Widget;

public class QueryTabUI extends Screen {
    @UiTemplate("QueryTab.ui.xml")
    interface QueryTabUIBinder extends UiBinder<Widget, QueryTabUI> {
    };

    private static QueryTabUIBinder  uiBinder = GWT.create(QueryTabUIBinder.class);

    @UiField
    protected TextBox<Integer>       accessionNumberFrom, accessionNumberTo;

    @UiField
    protected Calendar               collectionDateFrom, collectionDateTo, receivedDateFrom,
                    receivedDateTo, enteredDateFrom, enteredDateTo, analysisCompletedDateFrom,
                    analysisCompletedDateTo, analysisReleasedDateFrom, analysisReleasedDateTo;

    @UiField
    protected TextBox<String>        clientReference, reportTo, analysisTestName,
                    analysisMethodName;

    @UiField
    protected Dropdown<Integer>      analysisStatusId;

    @UiField
    protected MultiDropdown<Integer> projectId;

    @UiField
    protected Dropdown<String>       domain, analysisIsReportable, result, auxData;

    @UiField
    protected CheckBox               excludeResultOverride;

    protected Screen                 parentScreen;

    protected EventBus               parentBus;

    protected DataView1VO            data;

    private static final String      EXCLUDE_ALL = "excludeAll",
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
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 accessionNumberFrom.setValue(null);
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
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 accessionNumberTo.setValue(null);
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
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 collectionDateFrom.setValue(null);
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
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 collectionDateTo.setValue(null);
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
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 receivedDateFrom.setValue(null);
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
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 receivedDateTo.setValue(null);
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
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 enteredDateFrom.setValue(null);
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
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 enteredDateTo.setValue(null);
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 enteredDateTo.setEnabled(isState(DEFAULT));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? clientReference : enteredDateFrom;
                             }
                         });

        addScreenHandler(clientReference,
                         SampleWebMeta.getClientReference(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 clientReference.setValue(null);
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 clientReference.setEnabled(isState(DEFAULT));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? projectId : enteredDateTo;
                             }
                         });

        addScreenHandler(projectId, SampleWebMeta.getProjectId(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                projectId.setValue(null);
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
                             public void onDataChange(DataChangeEvent event) {
                                 reportTo.setValue(null);
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 reportTo.setEnabled(isState(DEFAULT));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? domain : projectId;
                             }
                         });

        addScreenHandler(domain, SampleWebMeta.getDomain(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                domain.setValue(null);
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
                             public void onDataChange(DataChangeEvent event) {
                                 analysisTestName.setValue(null);
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
                             public void onDataChange(DataChangeEvent event) {
                                 analysisMethodName.setValue(null);
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
                             public void onDataChange(DataChangeEvent event) {
                                 analysisIsReportable.setValue(null);
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
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 analysisStatusId.setValue(null);
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
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 analysisCompletedDateFrom.setValue(null);
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
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 analysisCompletedDateTo.setValue(null);
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
                         SampleWebMeta.getReleasedDateFrom(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 analysisReleasedDateFrom.setValue(null);
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
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 analysisReleasedDateTo.setValue(null);
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 analysisReleasedDateTo.setEnabled(isState(DEFAULT));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? excludeResultOverride : analysisReleasedDateFrom;
                             }
                         });

        addScreenHandler(excludeResultOverride,
                         "excludeResultOverride",
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 excludeResultOverride.setValue(getExcludeResultOverride());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setExcludeResultOverride(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 excludeResultOverride.setEnabled(isState(DEFAULT));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? result : analysisReleasedDateTo;
                             }
                         });

        addScreenHandler(result, "results", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                result.setValue(getResults());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                setResults(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                result.setEnabled(isState(DEFAULT));
            }

            public Widget onTab(boolean forward) {
                return forward ? auxData : excludeResultOverride;
            }
        });

        addScreenHandler(auxData, "auxData", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
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

        addQueryData(analysisIsReportable, SampleWebMeta.getAnalysisIsReportable(), fields);

        return fields;
    }

    public Validation validate() {
        int pairsFilled;
        Validation validation;

        pairsFilled = validateFromToFields();
        validation = super.validate();

        /*
         * if none of the "from" and "to" pairs of widgets has values, the query
         * shouldn't be executed; this is because the report will take too long
         * to generate
         */
        if (pairsFilled == 0) {
            validation.addException(new Exception(Messages.get()
                                                          .dataView_atLeastOnePairFilledException()));
            validation.setStatus(Validation.Status.ERRORS);
        }

        return validation;
    }

    private String getExcludeResultOverride() {
        if (data == null || DataBaseUtil.isEmpty(data.getExcludeResultOverride()))
            return "N";
        else
            return data.getExcludeResultOverride();
    }

    private void setExcludeResultOverride(String excludeResultOverride) {
        data.setExcludeResultOverride(excludeResultOverride);
    }

    private String getResults() {
        if (data != null) {
            if ("Y".equals(data.getExcludeResults()))
                return EXCLUDE_ALL;
            else if ("Y".equals(data.getIncludeNotReportableResults()))
                return INCLUDE_NOT_REPORTABLE;
        }

        return null;
    }

    private void setResults(String results) {
        String exc, inc;
        
        exc = "N";
        inc = "N";
        if (EXCLUDE_ALL.equals(results))
            exc = "Y";
        else if (INCLUDE_NOT_REPORTABLE.equals(results))
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
        if (EXCLUDE_ALL.equals(result))
            exc = "Y";
        else if (INCLUDE_NOT_REPORTABLE.equals(result))
            inc = "Y";
        
        data.setExcludeAuxData(exc);
        data.setIncludeNotReportableAuxData(inc);
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
         * the key is set in the hashmap for handlers
         */
        if (field != null)
            field.setKey(key);

        return field;
    }

    /**
     * For each pair of "from" and "to" widgets for a database field like
     * accession number, adds "Field Required" error if one widget has a value
     * and the other doesn't; returns the number of pairs where both widgets
     * have a value
     */
    private int validateFromToFields() {
        int pairsFilled;
        boolean fromEmpty, toEmpty;

        pairsFilled = 6;

        fromEmpty = analysisCompletedDateFrom.getValue() == null;
        toEmpty = analysisCompletedDateTo.getValue() == null;
        if ( !fromEmpty) {
            if (toEmpty) {
                analysisCompletedDateTo.addException(new Exception(Messages.get()
                                                                           .gen_fieldRequiredException()));
            }
        } else if ( !toEmpty) {
            analysisCompletedDateFrom.addException(new Exception(Messages.get()
                                                                         .gen_fieldRequiredException()));
        } else {
            pairsFilled-- ;
        }

        fromEmpty = analysisReleasedDateFrom.getValue() == null;
        toEmpty = analysisReleasedDateTo.getValue() == null;
        if ( !fromEmpty) {
            if (toEmpty) {
                analysisReleasedDateTo.addException(new Exception(Messages.get()
                                                                          .gen_fieldRequiredException()));
            }
        } else if ( !toEmpty) {
            analysisReleasedDateFrom.addException(new Exception(Messages.get()
                                                                        .gen_fieldRequiredException()));
        } else {
            pairsFilled-- ;
        }

        fromEmpty = DataBaseUtil.isEmpty(accessionNumberFrom.getValue());
        toEmpty = DataBaseUtil.isEmpty(accessionNumberTo.getValue());
        if ( !fromEmpty) {
            if (toEmpty) {
                accessionNumberTo.addException(new Exception(Messages.get()
                                                                     .gen_fieldRequiredException()));
            }
        } else if ( !toEmpty) {
            accessionNumberFrom.addException(new Exception(Messages.get()
                                                                   .gen_fieldRequiredException()));
        } else {
            pairsFilled-- ;
        }

        fromEmpty = collectionDateFrom.getValue() == null;
        toEmpty = collectionDateTo.getValue() == null;
        if ( !fromEmpty) {
            if (toEmpty) {
                collectionDateTo.addException(new Exception(Messages.get()
                                                                    .gen_fieldRequiredException()));
            }
        } else if ( !toEmpty) {
            collectionDateFrom.addException(new Exception(Messages.get()
                                                                  .gen_fieldRequiredException()));
        } else {
            pairsFilled-- ;
        }

        fromEmpty = receivedDateFrom.getValue() == null;
        toEmpty = receivedDateTo.getValue() == null;
        if ( !fromEmpty) {
            if (toEmpty) {
                receivedDateTo.addException(new Exception(Messages.get()
                                                                  .gen_fieldRequiredException()));
            }
        } else if ( !toEmpty) {
            receivedDateFrom.addException(new Exception(Messages.get().gen_fieldRequiredException()));
        } else {
            pairsFilled-- ;
        }

        fromEmpty = enteredDateFrom.getValue() == null;
        toEmpty = enteredDateTo.getValue() == null;
        if ( !fromEmpty) {
            if (toEmpty) {
                enteredDateTo.addException(new Exception(Messages.get()
                                                                 .gen_fieldRequiredException()));
            }
        } else if ( !toEmpty) {
            enteredDateFrom.addException(new Exception(Messages.get().gen_fieldRequiredException()));
        } else {
            pairsFilled-- ;
        }

        return pairsFilled;
    }
}