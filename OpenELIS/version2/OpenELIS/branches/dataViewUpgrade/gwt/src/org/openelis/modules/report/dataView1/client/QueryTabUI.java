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
import org.openelis.domain.DataViewVO1;
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
import org.openelis.ui.screen.Screen.Validation;
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
import com.google.gwt.user.client.ui.HasValue;
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
    protected Dropdown<String>       domain, analysisIsReportable;

    @UiField
    protected CheckBox               excludeResultOverride, excludeResults, excludeAuxData;

    protected Screen                 parentScreen;

    protected EventBus               parentBus;

    protected DataViewVO1            data;

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
                                 return forward ? accessionNumberTo : excludeAuxData;
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

        addScreenHandler(reportTo, "reportTo", new ScreenHandler<String>() {
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
                                 return forward ? excludeResultOverride : analysisTestName;
                             }
                         });

        addScreenHandler(excludeResultOverride,
                         "excludeResultOverride",
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 excludeResultOverride.setValue("N");
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setExcludeResultOverride(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 excludeResultOverride.setEnabled(isState(DEFAULT));
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
                                 return forward ? analysisCompletedDateFrom : excludeResultOverride;
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
                                 return forward ? analysisIsReportable : analysisReleasedDateFrom;
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
                                 return forward ? excludeResults : analysisReleasedDateTo;
                             }
                         });

        addScreenHandler(excludeResults, "excludeResults", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                excludeResults.setValue("N");
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                setExcludeResults(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                excludeResults.setEnabled(isState(DEFAULT));
            }

            public Widget onTab(boolean forward) {
                return forward ? excludeAuxData : analysisIsReportable;
            }
        });

        addScreenHandler(excludeAuxData, "excludeAuxData", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                excludeAuxData.setValue("N");
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                setExcludeAuxData(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                excludeAuxData.setEnabled(isState(DEFAULT));
            }

            public Widget onTab(boolean forward) {
                return forward ? accessionNumberFrom : excludeResults;
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
    }

    public void setData(DataViewVO1 data) {
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

        addQueryData(clientReference, fields);
        addQueryData(projectId, fields);
        addQueryData(domain, fields);
        addQueryData(analysisTestName, fields);
        addQueryData(analysisMethodName, fields);
        addQueryData(analysisStatusId, fields);

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

        addQueryData(analysisIsReportable, fields);

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

    private void setExcludeResultOverride(String excludeResultOverride) {
        data.setExcludeResultOverride(excludeResultOverride);
    }

    private void setExcludeResults(String excludeResults) {
        data.setExcludeResults(excludeResults);
    }

    private void setExcludeAuxData(String excludeAuxData) {
        data.setExcludeAuxData(excludeAuxData);
    }

    /**
     * Creates a query data where the query string contains the values of the
     * passed widgets separated by a delimiter; the key and type are set to the
     * passed values; doesn't create the query data if the value of either
     * widget is null or empty
     */
    private void addQueryData(HasValue<?> fromWidget, HasValue<?> toWidget, String key,
                              QueryData.Type type, ArrayList<QueryData> fields) {
        QueryData field;
        Object fromValue, toValue;

        field = null;
        fromValue = fromWidget.getValue();
        toValue = toWidget.getValue();

        if ( !DataBaseUtil.isEmpty(fromValue) && !DataBaseUtil.isEmpty(toValue)) {
            field = new QueryData();
            field.setKey(key);
            field.setQuery(DataBaseUtil.concatWithSeparator(fromValue, "..", toValue));
            field.setType(type);
        }

        if (field != null)
            fields.add(field);
    }

    /**
     * Adds the query data for the passed widget to the passed list
     */
    private void addQueryData(Queryable widget, ArrayList<QueryData> fields) {
        if (widget.getQuery() != null)
            fields.add((QueryData)widget.getQuery());
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