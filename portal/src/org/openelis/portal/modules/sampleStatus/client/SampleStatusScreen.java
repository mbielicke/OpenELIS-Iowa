package org.openelis.portal.modules.sampleStatus.client;

import static org.openelis.portal.client.Logger.remote;
import static org.openelis.ui.screen.State.QUERY;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;

import org.openelis.domain.Constants;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.SampleStatusWebReportVO;
import org.openelis.domain.SampleViewVO;
import org.openelis.meta.SampleViewMeta;
import org.openelis.portal.cache.UserCache;
import org.openelis.portal.messages.Messages;
import org.openelis.portal.modules.finalReport.client.FinalReportFormVO;
import org.openelis.portal.modules.finalReport.client.FinalReportService;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.ModulePermission;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.resources.UIResources;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.widget.DateHelper;
import org.openelis.ui.widget.Item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

public class SampleStatusScreen extends Screen {

    SampleStatusUI            ui = GWT.create(SampleStatusUIImpl.class);

    private ModulePermission  userPermission;

    private FinalReportFormVO form;

    public SampleStatusScreen() {
        initWidget(ui.asWidget());

        userPermission = UserCache.getPermission().getModule("w_status");
        if (userPermission == null) {
            Window.alert(Messages.get().error_screenPerm("Sample Status Screen"));
            return;
        }

        initialize();
        setState(QUERY);
    }

    /**
     * Setup state and data change handles for every widget on the screen
     */
    private void initialize() {
        ArrayList<Item<Integer>> model;
        ArrayList<IdNameVO> list;
        Item<Integer> row;

        ui.initialize();
        form = new FinalReportFormVO();
        ui.getDeck().showWidget(0);

        ui.getGetSampleListButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                Query query;
                ArrayList<QueryData> queryList;

                // if ( !validate()) {
                // Window.alert(Messages.get().correctErrors());
                // return;
                // }
                query = new Query();
                try {
                    queryList = createWhereFromParamFields(getQueryFields());
                } catch (Exception e) {
                    return;
                }
                /*
                 * if user does not enter any search details, throw an error.
                 */
                if (queryList.size() == 0) {
                    Window.alert(Messages.get().finalReport_error_emptyQueryException());
                    return;
                }

                query.setFields(queryList);

                SampleStatusService.get()
                                   .getSampleListForSampleStatusReport(query,
                                                                       new AsyncCallback<ArrayList<SampleStatusWebReportVO>>() {
                                                                           @Override
                                                                           public void onSuccess(ArrayList<SampleStatusWebReportVO> list) {
                                                                               if (list.size() > 0) {
                                                                                   // loadDeck(list);
                                                                                   // window.clearStatus();
                                                                               } else {
                                                                                   Window.alert(Messages.get()
                                                                                                        .finalReport_error_noSamples());
                                                                               }
                                                                           }

                                                                           @Override
                                                                           public void onFailure(Throwable caught) {
                                                                               // window.clearStatus();
                                                                               Window.alert(caught.getMessage());
                                                                           }
                                                                       });

            }
        });

        ui.getResetButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                form = new FinalReportFormVO();
                clearErrors();
                fireDataChange();
            }
        });

        addScreenHandler(ui.getCollectedFrom(),
                         SampleViewMeta.getCollectionDateFrom(),
                         new ScreenHandler<Datetime>() {
                             public void onDataChange(DataChangeEvent event) {
                                 ui.getCollectedFrom().setValue(form.getCollectedFrom());
                             }

                             public void onValueChange(ValueChangeEvent<Datetime> event) {
                                 ui.getCollectedFrom().clearExceptions();
                                 ui.getCollectedTo().clearExceptions();
                                 form.setCollectedFrom(event.getValue());
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? ui.getCollectedTo() : ui.getResetButton();
                             }

                             @Override
                             public Object getQuery() {
                                 return ui.getCollectedFrom().getQuery();
                             }
                         });

        addScreenHandler(ui.getCollectedTo(),
                         SampleViewMeta.getCollectionDateTo(),
                         new ScreenHandler<Datetime>() {
                             public void onDataChange(DataChangeEvent event) {
                                 ui.getCollectedTo().setValue(form.getCollectedTo());
                             }

                             public void onValueChange(ValueChangeEvent<Datetime> event) {
                                 ui.getCollectedTo().clearExceptions();
                                 ui.getCollectedFrom().clearExceptions();
                                 form.setCollectedTo(event.getValue());
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? ui.getAccessionFrom() : ui.getCollectedFrom();
                             }

                             @Override
                             public Object getQuery() {
                                 return ui.getCollectedTo().getQuery();
                             }
                         });

        addScreenHandler(ui.getAccessionFrom(),
                         SampleViewMeta.getAccessionNumberFrom(),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent event) {
                                 ui.getAccessionFrom().setValue(form.getAccessionFrom());
                             }

                             public void onValueChange(ValueChangeEvent<Integer> event) {
                                 ui.getAccessionFrom().clearExceptions();
                                 ui.getAccessionTo().clearExceptions();
                                 form.setAccessionFrom(event.getValue());
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? ui.getAccessionTo() : ui.getCollectedTo();
                             }

                             @Override
                             public Object getQuery() {
                                 return ui.getAccessionFrom().getQuery();
                             }
                         });

        addScreenHandler(ui.getAccessionTo(),
                         SampleViewMeta.getAccessionNumberTo(),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent event) {
                                 ui.getAccessionTo().setValue(form.getAccessionFrom());
                             }

                             public void onValueChange(ValueChangeEvent<Integer> event) {
                                 ui.getAccessionTo().clearExceptions();
                                 ui.getAccessionFrom().clearExceptions();
                                 form.setAccessionTo(event.getValue());
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? ui.getClientReference() : ui.getAccessionFrom();
                             }

                             @Override
                             public Object getQuery() {
                                 return ui.getAccessionTo().getQuery();
                             }
                         });

        addScreenHandler(ui.getClientReference(),
                         SampleViewMeta.getClientReference(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 ui.getClientReference().setValue(form.getClientReference());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 ui.getClientReference().clearExceptions();
                                 form.setClientReference(event.getValue());
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? ui.getProjectCode() : ui.getAccessionTo();
                             }

                             @Override
                             public Object getQuery() {
                                 return ui.getClientReference().getQuery();
                             }
                         });

        addScreenHandler(ui.getProjectCode(),
                         SampleViewMeta.getProjectId(),
                         new ScreenHandler<ArrayList<Integer>>() {
                             public void onDataChange(DataChangeEvent event) {
                                 ui.getProjectCode().setValue(form.getProjectCodes());
                             }

                             public void onValueChange(ValueChangeEvent<ArrayList<Integer>> event) {
                                 ui.getProjectCode().clearExceptions();
                                 form.setProjectCodes(event.getValue());
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? ui.getGetSampleListButton()
                                               : ui.getClientReference();
                             }

                             @Override
                             public Object getQuery() {
                                 return ui.getProjectCode().getQuery();
                             }
                         });

        addScreenHandler(ui.getGetSampleListButton(), "", new ScreenHandler<Integer>() {
            public Widget onTab(boolean forward) {
                return forward ? ui.getResetButton() : ui.getProjectCode();
            }
        });

        addScreenHandler(ui.getResetButton(), "", new ScreenHandler<Integer>() {
            public Widget onTab(boolean forward) {
                return forward ? ui.getCollectedFrom() : ui.getGetSampleListButton();
            }
        });

        /*
         * Initializing the project code drop down
         */
        model = new ArrayList<Item<Integer>>();
        try {
            // TODO
            list = FinalReportService.get().getProjectList();
            for (int i = 0; i < list.size(); i++ ) {
                row = new Item<Integer>(list.get(i).getId(), list.get(i).getName());
                model.add(row);
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
            remote.log(Level.SEVERE, e.getMessage(), e);
        }
        ui.getProjectCode().setModel(model);
    }

    private ArrayList<QueryData> createWhereFromParamFields(ArrayList<QueryData> fields) throws Exception {
        HashMap<String, QueryData> fieldMap;

        fieldMap = new HashMap<String, QueryData>();
        for (QueryData data : fields) {
            fieldMap.put(data.getKey(), data);
        }

        try {
            getRangeQuery(SampleViewMeta.getCollectionDateFrom(),
                          SampleViewMeta.getCollectionDateTo(),
                          SampleViewMeta.getCollectionDate(),
                          fieldMap);
        } catch (Exception e) {
            ui.getCollectedTo()
              .addException(new Exception(Messages.get().finalReport_error_noStartDate()));
            throw e;
        }

        try {
            getRangeQuery(SampleViewMeta.getAccessionNumberFrom(),
                          SampleViewMeta.getAccessionNumberTo(),
                          SampleViewMeta.getAccessionNumber(),
                          fieldMap);
        } catch (Exception e) {
            ui.getAccessionTo()
              .addException(new Exception(Messages.get().finalReport_error_noStartAccession()));
            throw e;
        }

        return new ArrayList<QueryData>(fieldMap.values());
    }

    private HashMap<String, QueryData> getRangeQuery(String fromKey, String toKey, String key,
                                                     HashMap<String, QueryData> fieldMap) throws Exception {
        QueryData from, to, range;

        from = fieldMap.get(fromKey);
        to = fieldMap.get(toKey);

        if (to != null && from == null) {
            throw new Exception();
        } else if (to == null && from == null) {
            return fieldMap;
        } else {
            range = fieldMap.get(fromKey);
            range.setKey(key);
            range.setQuery(from.getQuery() + ".." + to.getQuery());
            fieldMap.put(key, range);
            fieldMap.remove(fromKey);
            fieldMap.remove(toKey);
        }

        return fieldMap;
    }

    private void setTableData(ArrayList<SampleViewVO> samples) {
        int currRow;
        Integer accNumPrev, accNum;
        String completed, inProgress;
        Date temp;
        Datetime temp1;
        DateHelper dh;
        SampleViewVO data;

        /*
         * show the sample list table
         */
        ui.getDeck().showWidget(1);

        /*
         * if there are no samples returned, tell the user
         */
        if (samples == null || samples.size() < 1) {
            ui.getTable().setText(0, 1, Messages.get().finalReport_error_noSamples());
            return;
        }
        /*
         * initialize the column headers
         */
        ui.getTable().setText(0, 0, Messages.get().gen_accessionNumber());
        ui.getTable().setText(0, 1, Messages.get().sampleStatus_description());
        ui.getTable().setText(0, 2, Messages.get().sampleStatus_testStatus());
        ui.getTable().setText(0, 3, Messages.get().gen_collectedDate());
        ui.getTable().setText(0, 4, Messages.get().sampleStatus_dateReceived());
        ui.getTable().setText(0, 5, Messages.get().sample_clientReference());
        ui.getTable().setText(0, 6, Messages.get().sampleStatus_qaEvent());
        ui.getTable().getRowFormatter().setStyleName(0, UIResources.INSTANCE.table().Header());

        dh = new DateHelper();
        dh.setEnd(Datetime.MINUTE);
        accNumPrev = null;
        currRow = 1;
        completed = Messages.get().sampleStatus_completed();
        inProgress = Messages.get().sampleStatus_inProgress();

        for (int i = 0; i < samples.size(); i++ ) {
            data = samples.get(i);
            accNum = data.getAccessionNumber();

            /*
             * If analysis status is Released, screen displays
             * "Completed status", for all other statuses screen displays
             * "In Progress".
             */
            if ( !accNum.equals(accNumPrev)) {
                currRow++ ;
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
                ui.getTable().setText(currRow, 0, DataBaseUtil.toString(data.getAccessionNumber()));
                ui.getTable().setText(currRow, 1, data.getCollector());
                ui.getTable().setText(currRow, 3, dh.format(temp1));
                ui.getTable().setText(currRow, 4, dh.format(data.getReceivedDate()));
                ui.getTable().setText(currRow, 5, data.getClientReference());
                // ui.getTable().setText(currRow, 6, data.getAnalysisQa());
            } else {
                ui.getTable().setText(currRow,
                                      1,
                                      ui.getTable().getText(currRow, 1) + "\n" +
                                                      data.getTestReportingDescription() + " : " +
                                                      data.getMethodReportingDescription());
                ui.getTable()
                  .setText(currRow,
                           2,
                           ui.getTable().getText(currRow, 2) +
                                           "\n" +
                                           (Constants.dictionary().ANALYSIS_RELEASED.equals(data.getSampleStatusId()) ? completed
                                                                                                                     : inProgress));
            }
            accNumPrev = accNum;
        }
    }
}