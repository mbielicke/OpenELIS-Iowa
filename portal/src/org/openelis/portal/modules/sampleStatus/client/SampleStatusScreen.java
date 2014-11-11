package org.openelis.portal.modules.sampleStatus.client;

import static org.openelis.portal.client.Logger.remote;
import static org.openelis.ui.screen.State.QUERY;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;

import org.openelis.domain.Constants;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.SampleViewVO;
import org.openelis.meta.SampleViewMeta;
import org.openelis.portal.cache.UserCache;
import org.openelis.portal.messages.Messages;
import org.openelis.portal.modules.finalReport.client.FinalReportFormVO;
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
import com.google.gwt.dom.client.Style.WhiteSpace;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Widget;

public class SampleStatusScreen extends Screen {

    SampleStatusUI            ui = GWT.create(SampleStatusUIImpl.class);

    private ModulePermission  userPermission;

    private FinalReportFormVO form;

    private HashMap<Integer, ArrayList<String>> sampleQas, analysisQas;

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
        IdNameVO project;
        ArrayList<Item<Integer>> model;
        ArrayList<IdNameVO> list;
        Item<Integer> row;

        ui.initialize();
        form = new FinalReportFormVO();
        ui.getDeck().showWidget(0);

        ui.getGetSampleListButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                getSampleList();
            }
        });

        ui.getResetButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                form = new FinalReportFormVO();
                ui.clearErrors();
                fireDataChange();
            }
        });

        ui.getBackButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                ui.getDeck().showWidget(0);
            }
        });

        addScreenHandler(ui.getCollectedFrom(),
                         SampleViewMeta.getCollectionDateFrom(),
                         new ScreenHandler<Datetime>() {
                             public void onDataChange(DataChangeEvent event) {
                                 ui.getCollectedFrom().setValue(form.getCollectedFrom());
                             }

                             public void onValueChange(ValueChangeEvent<Datetime> event) {
                                 ui.setCollectedError(null);
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
                                 ui.setCollectedError(null);
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
                                 ui.setAccessionError(null);
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
                                 ui.getAccessionTo().setValue(form.getAccessionTo());
                             }

                             public void onValueChange(ValueChangeEvent<Integer> event) {
                                 ui.setAccessionError(null);
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
                                 ui.setClientReferenceError(null);
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
                                 ui.setProjectError(null);
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
            list = SampleStatusService.get().getProjectList();
            for (int i = 0; i < list.size(); i++ ) {
                project = list.get(i);
                row = new Item<Integer>(2);
                row.setKey(project.getId());
                row.setCell(0, project.getName());
                row.setCell(1, project.getDescription());
                model.add(row);
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
            remote.log(Level.SEVERE, e.getMessage(), e);
        }
        ui.getProjectCode().setModel(model);
    }

    private ArrayList<QueryData> createWhereFromParamFields(ArrayList<QueryData> fields) throws Exception {
        boolean error;
        HashMap<String, QueryData> fieldMap;

        fieldMap = new HashMap<String, QueryData>();
        for (QueryData data : fields) {
            fieldMap.put(data.getKey(), data);
        }

        error = false;
        try {
            getRangeQuery(SampleViewMeta.getCollectionDateFrom(),
                          SampleViewMeta.getCollectionDateTo(),
                          SampleViewMeta.getCollectionDate(),
                          fieldMap);
        } catch (Exception e) {
            ui.setCollectedError(Messages.get().finalReport_error_noStartDate());
            error = true;
        }

        try {
            getRangeQuery(SampleViewMeta.getAccessionNumberFrom(),
                          SampleViewMeta.getAccessionNumberTo(),
                          SampleViewMeta.getAccessionNumber(),
                          fieldMap);
        } catch (Exception e) {
            ui.setAccessionError(Messages.get().finalReport_error_noStartAccession());
            error = true;
        }

        /*
         * if there was an error validating the fields, do not query for samples
         */
        if (error)
            throw new Exception();

        return new ArrayList<QueryData>(fieldMap.values());
    }

    private HashMap<String, QueryData> getRangeQuery(String fromKey, String toKey, String key,
                                                     HashMap<String, QueryData> fieldMap) throws Exception {
        QueryData from, to, range;

        from = fieldMap.get(fromKey);
        to = fieldMap.get(toKey);

        if (to == null && from == null) {
            return fieldMap;
        } else if (to != null && from == null) {
            throw new Exception();
        } else if (to == null && from != null) {
            range = fieldMap.get(fromKey);
            range.setKey(key);
            range.setQuery(from.getQuery() + ".." + from.getQuery());
            fieldMap.put(key, range);
            fieldMap.remove(fromKey);
            fieldMap.remove(toKey);
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

    @SuppressWarnings("deprecation")
    private void setTableData(ArrayList<SampleViewVO> samples) {
        int currRow, qaCount;
        Integer accNumPrev, accNum;
        String completed, inProgress, collector;
        Date temp;
        Datetime temp1;
        DateHelper dh;
        SampleViewVO data;
        StringBuffer sb;

        /*
         * if there are no samples returned, tell the user
         */
        if (samples == null || samples.size() < 1) {
            ui.getTable().setText(0, 1, Messages.get().finalReport_error_noSamples());
            return;
        }

        /*
         * show the sample list table
         */
        ui.getDeck().showWidget(1);

        /*
         * initialize the column headers
         */
        ui.getTable().setText(0, 0, Messages.get().gen_accessionNumber());
        ui.getTable().setText(0, 1, Messages.get().sampleStatus_description());
        ui.getTable().setText(0, 2, Messages.get().sampleStatus_testStatus());
        ui.getTable().setText(0, 3, Messages.get().sample_collectedDate());
        ui.getTable().setText(0, 4, Messages.get().sampleStatus_dateReceived());
        ui.getTable().setText(0, 5, Messages.get().sample_clientReference());
        ui.getTable().setText(0, 6, Messages.get().sampleStatus_qaEvent());
        ui.getTable().getRowFormatter().setStyleName(0, UIResources.INSTANCE.table().Header());

        dh = new DateHelper();
        sb = new StringBuffer();
        dh.setEnd(Datetime.MINUTE);
        accNumPrev = null;
        currRow = qaCount = 0;
        completed = Messages.get().sampleStatus_completed();
        inProgress = Messages.get().sampleStatus_inProgress();

        for (int i = 0; i < samples.size(); i++ ) {
            data = samples.get(i);
            accNum = data.getAccessionNumber();

            if ( !accNum.equals(accNumPrev)) {
                qaCount = 1;
                if (currRow > 0) {
                    sb.append("</ol></font>");
                    ui.getTable().setHTML(currRow, 6, sb.toString());
                    ui.getTable()
                      .getCellFormatter()
                      .getElement(currRow, 6)
                      .getStyle()
                      .setWhiteSpace(WhiteSpace.PRE_WRAP);
                    sb.setLength(0);
                }
                currRow++ ;
                ui.getTable().getRowFormatter().setVerticalAlign(currRow,
                                                                 HasVerticalAlignment.ALIGN_TOP);
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

                // TODO
                if (data.getCollector() == null)
                    collector = "Collector Not Available";
                else
                    collector = data.getCollector();
                sb.append("<font color=\"red\"><ol>");
                if (sampleQas != null && sampleQas.get(data.getSampleId()) != null) {
                    ui.getTable().setHTML(currRow,
                                          1,
                                          collector + "<sub><font color=\"red\">" + qaCount++ +
                                                          "</font></sub>");
                    sb.append("<li>");
                    ui.getTable().setHTML(currRow, 2, "<sub/>");
                    for (String qa : sampleQas.get(data.getSampleId())) {
                        sb.append(qa).append("<br>");
                    }
                    sb.append("</li><br>");
                } else {
                    ui.getTable().setHTML(currRow, 1, collector);
                }
                ui.getTable().setText(currRow, 3, dh.format(temp1));
                ui.getTable().setText(currRow, 4, dh.format(data.getReceivedDate()));
                ui.getTable().setText(currRow, 5, data.getClientReference());
            }
            if (analysisQas != null && analysisQas.get(data.getAnalysisId()) != null) {
                addHtml(currRow, 1, "<br><hr>" + data.getTestReportingDescription() + " : " +
                                    data.getMethodReportingDescription() +
                                    "<sub><font color=\"red\">" + qaCount++ + "</font></sub>");
                sb.append("<li>");
                for (String qa : analysisQas.get(data.getAnalysisId())) {
                    sb.append(qa).append("<br>");
                }
                sb.append("</li><br>");

                /*
                 * If analysis status is Released, screen displays
                 * "Completed status", for all other statuses screen displays
                 * "In Progress".
                 */
                addHtml(currRow, 2, "<br><hr>" +
                                    (DataBaseUtil.isSame(Constants.dictionary().ANALYSIS_RELEASED,
                                                         data.getAnalysisStatusId()) ? completed
                                                                                    : inProgress) +
                                    "<sub/>");
            } else {
                addHtml(currRow, 1, "<br><hr>" + data.getTestReportingDescription() + " : " +
                                    data.getMethodReportingDescription());
                addHtml(currRow, 2, "<br><hr>" +
                                    (DataBaseUtil.isSame(Constants.dictionary().ANALYSIS_RELEASED,
                                                         data.getAnalysisStatusId()) ? completed
                                                                                    : inProgress));
            }

            accNumPrev = accNum;
        }

        /*
         * complete the html for the last row
         */
        if (sb.length() > 0) {
            sb.append("</ol></font>");
            ui.getTable().setHTML(currRow, 6, sb.toString());
            ui.getTable()
              .getCellFormatter()
              .getElement(currRow, 6)
              .getStyle()
              .setWhiteSpace(WhiteSpace.PRE_WRAP);
        }
    }

    private void getSampleList() {
        Query query;
        ArrayList<QueryData> queryList;

        ui.clearErrors();
        ui.getTable().removeAllRows();

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
        window.setBusy(Messages.get().gen_fetchingSamples());

        SampleStatusService.get()
                           .getSampleListForSampleStatusReport(query,
                                                               new AsyncCallback<ArrayList<SampleViewVO>>() {
                                                                   @Override
                                                                   public void onSuccess(ArrayList<SampleViewVO> list) {
                                                                       if (list.size() > 0) {
                                                                           window.clearStatus();
                                                                           try {
                                                                               fetchQaData(list);
                                                                           } catch (Exception e) {
                                                                               Window.alert(e.getMessage());
                                                                               return;
                                                                           }
                                                                           setTableData(list);
                                                                       } else {
                                                                           Window.alert(Messages.get()
                                                                                                .finalReport_error_noSamples());
                                                                       }
                                                                   }

                                                                   @Override
                                                                   public void onFailure(Throwable caught) {
                                                                       window.clearStatus();
                                                                       Window.alert(caught.getMessage());
                                                                   }
                                                               });
    }

    // TODO
    private void fetchQaData(ArrayList<SampleViewVO> samples) throws Exception {
        HashSet<Integer> sids, aids;

        sids = new HashSet<Integer>();
        aids = new HashSet<Integer>();
        for (SampleViewVO sample : samples) {
            sids.add(sample.getSampleId());
            aids.add(sample.getAnalysisId());
        }

        sampleQas = SampleStatusService.get().getSampleQaEvents(new ArrayList<Integer>(sids));

        analysisQas = SampleStatusService.get().getAnalysisQaEvents(new ArrayList<Integer>(aids));
    }

    private void addHtml(int row, int column, String html) {
        ui.getTable().setHTML(row, column, ui.getTable().getHTML(row, column) + html);
    }
}