package org.openelis.portal.modules.sampleStatus.client;

import static org.openelis.portal.client.Logger.remote;
import static org.openelis.ui.screen.State.QUERY;

import java.util.ArrayList;
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
import org.openelis.ui.widget.Item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.WhiteSpace;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.i18n.shared.DefaultDateTimeFormatInfo;
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
                                 return forward ? ui.getReleasedFrom() : ui.getCollectedFrom();
                             }

                             @Override
                             public Object getQuery() {
                                 return ui.getCollectedTo().getQuery();
                             }
                         });

        addScreenHandler(ui.getReleasedFrom(),
                         SampleViewMeta.getReleasedDateFrom(),
                         new ScreenHandler<Datetime>() {
                             public void onDataChange(DataChangeEvent event) {
                                 ui.getReleasedFrom().setValue(form.getReleasedFrom());
                             }

                             public void onValueChange(ValueChangeEvent<Datetime> event) {
                                 ui.setReleasedError(null);
                                 ui.getReleasedFrom().clearExceptions();
                                 ui.getReleasedTo().clearExceptions();
                                 form.setReleasedFrom(event.getValue());
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? ui.getReleasedTo() : ui.getCollectedTo();
                             }

                             @Override
                             public Object getQuery() {
                                 return ui.getReleasedFrom().getQuery();
                             }
                         });

        addScreenHandler(ui.getReleasedTo(),
                         SampleViewMeta.getReleasedDateTo(),
                         new ScreenHandler<Datetime>() {
                             public void onDataChange(DataChangeEvent event) {
                                 ui.getReleasedTo().setValue(form.getReleasedTo());
                             }

                             public void onValueChange(ValueChangeEvent<Datetime> event) {
                                 ui.setReleasedError(null);
                                 ui.getReleasedTo().clearExceptions();
                                 ui.getReleasedFrom().clearExceptions();
                                 form.setReleasedTo(event.getValue());
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? ui.getAccessionFrom() : ui.getReleasedFrom();
                             }

                             @Override
                             public Object getQuery() {
                                 return ui.getReleasedTo().getQuery();
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
                                 return forward ? ui.getAccessionTo() : ui.getReleasedTo();
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
                                 return forward ? ui.getEnvCollector() : ui.getClientReference();
                             }

                             @Override
                             public Object getQuery() {
                                 return ui.getProjectCode().getQuery();
                             }
                         });

        addScreenHandler(ui.getEnvCollector(),
                         SampleViewMeta.getCollector(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 ui.getEnvCollector().setValue(form.getEnvCollector());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 ui.setEnvCollectorError(null);
                                 ui.getEnvCollector().clearExceptions();
                                 form.setEnvCollector(event.getValue());
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? ui.getSdwisCollector() : ui.getProjectCode();
                             }

                             @Override
                             public Object getQuery() {
                                 return ui.getEnvCollector().getQuery();
                             }
                         });

        addScreenHandler(ui.getSdwisCollector(), "sdwisCollector", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                ui.getSdwisCollector().setValue(form.getSdwisCollector());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                ui.setSdwisCollectorError(null);
                ui.getSdwisCollector().clearExceptions();
                form.setSdwisCollector(event.getValue());
            }

            public Widget onTab(boolean forward) {
                return forward ? ui.getPwsId() : ui.getEnvCollector();
            }

            @Override
            public Object getQuery() {
                QueryData q;

                if (ui.getSdwisCollector().getQuery() == null)
                    return null;
                q = (QueryData)ui.getSdwisCollector().getQuery();
                q.setKey(SampleViewMeta.getCollector());
                return q;
            }
        });

        addScreenHandler(ui.getPwsId(),
                         SampleViewMeta.getPwsNumber0(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 ui.getPwsId().setValue(form.getPwsId());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 ui.setPwsError(null);
                                 ui.getPwsId().clearExceptions();
                                 form.setPwsId(event.getValue());
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? ui.getPatientFirst() : ui.getSdwisCollector();
                             }

                             @Override
                             public Object getQuery() {
                                 return ui.getPwsId().getQuery();
                             }
                         });

        addScreenHandler(ui.getPatientFirst(),
                         SampleViewMeta.getPatientFirstName(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 ui.getPatientFirst().setValue(form.getPatientFirst());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 ui.setPatientFirstError(null);
                                 ui.getPatientFirst().clearExceptions();
                                 form.setPatientFirst(event.getValue());
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? ui.getPatientLast() : ui.getPwsId();
                             }

                             @Override
                             public Object getQuery() {
                                 return ui.getPatientFirst().getQuery();
                             }
                         });

        addScreenHandler(ui.getPatientLast(),
                         SampleViewMeta.getPatientLastName(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 ui.getPatientLast().setValue(form.getPatientLast());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 ui.setPatientLastError(null);
                                 ui.getPatientLast().clearExceptions();
                                 form.setPatientLast(event.getValue());
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? ui.getPatientBirthFrom() : ui.getPatientFirst();
                             }

                             @Override
                             public Object getQuery() {
                                 return ui.getPatientLast().getQuery();
                             }
                         });

        addScreenHandler(ui.getPatientBirthFrom(),
                         SampleViewMeta.getPatientBirthDateFrom(),
                         new ScreenHandler<Datetime>() {
                             public void onDataChange(DataChangeEvent event) {
                                 ui.getPatientBirthFrom().setValue(form.getPatientBirthFrom());
                             }

                             public void onValueChange(ValueChangeEvent<Datetime> event) {
                                 ui.setPatientBirthError(null);
                                 ui.getPatientBirthFrom().clearExceptions();
                                 ui.getPatientBirthTo().clearExceptions();
                                 form.setPatientBirthFrom(event.getValue());
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? ui.getPatientBirthTo() : ui.getPatientLast();
                             }

                             @Override
                             public Object getQuery() {
                                 return ui.getPatientBirthFrom().getQuery();
                             }
                         });

        addScreenHandler(ui.getPatientBirthTo(),
                         SampleViewMeta.getPatientBirthDateTo(),
                         new ScreenHandler<Datetime>() {
                             public void onDataChange(DataChangeEvent event) {
                                 ui.getPatientBirthTo().setValue(form.getPatientBirthTo());
                             }

                             public void onValueChange(ValueChangeEvent<Datetime> event) {
                                 ui.setPatientBirthError(null);
                                 ui.getPatientBirthTo().clearExceptions();
                                 ui.getPatientBirthFrom().clearExceptions();
                                 form.setPatientBirthTo(event.getValue());
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? ui.getGetSampleListButton()
                                               : ui.getPatientBirthFrom();
                             }

                             @Override
                             public Object getQuery() {
                                 return ui.getPatientBirthTo().getQuery();
                             }
                         });

        addScreenHandler(ui.getGetSampleListButton(),
                         "getSampleListButton",
                         new ScreenHandler<Integer>() {
                             public Widget onTab(boolean forward) {
                                 return forward ? ui.getResetButton() : ui.getPatientBirthTo();
                             }
                         });

        addScreenHandler(ui.getResetButton(), "resetButton", new ScreenHandler<Integer>() {
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

    /**
     * create the range queries for variables with from and to fields
     */
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
            getRangeQuery(SampleViewMeta.getReleasedDateFrom(),
                          SampleViewMeta.getReleasedDateTo(),
                          SampleViewMeta.getReleasedDate(),
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

        try {
            getRangeQuery(SampleViewMeta.getPatientBirthDateFrom(),
                          SampleViewMeta.getPatientBirthDateTo(),
                          SampleViewMeta.getPatientBirthDate(),
                          fieldMap);
        } catch (Exception e) {
            ui.setPatientBirthError(Messages.get().finalReport_error_noStartDate());
            error = true;
        }

        /*
         * if there was an error validating the fields, do not query for samples
         */
        if (error)
            throw new Exception();

        return new ArrayList<QueryData>(fieldMap.values());
    }

    /**
     * create a range query string
     */
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

    /**
     * create the table from sample objects
     */
    @SuppressWarnings("deprecation")
    private void setTableData(ArrayList<SampleViewVO> samples) {
        int testRow, sampleRow, qaCount, sampleCount;
        Integer accNumPrev, accNum;
        String completed, inProgress, sampleData, collected;
        DateTimeFormat df, tf, dtf;
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

        df = new DateTimeFormat(Messages.get().gen_datePattern(), new DefaultDateTimeFormatInfo()) {
        };
        tf = new DateTimeFormat(Messages.get().gen_timePattern(), new DefaultDateTimeFormatInfo()) {
        };
        dtf = new DateTimeFormat(Messages.get().gen_dateTimePattern(),
                                 new DefaultDateTimeFormatInfo()) {
        };
        sb = new StringBuffer();
        accNumPrev = null;
        testRow = qaCount = sampleRow = sampleCount = 0;
        completed = Messages.get().sampleStatus_completed();
        inProgress = Messages.get().sampleStatus_inProgress();

        for (int i = 0; i < samples.size(); i++ ) {
            data = samples.get(i);
            accNum = data.getAccessionNumber();

            if ( !accNum.equals(accNumPrev)) {
                sampleCount++ ;
                if (sampleRow > 0) {
                    sb.append("</ol></font>");
                    ui.getTable().setHTML(sampleRow, 6, sb.toString());
                    ui.getTable()
                      .getCellFormatter()
                      .getElement(sampleRow, 6)
                      .getStyle()
                      .setWhiteSpace(WhiteSpace.PRE_WRAP);
                    ui.getTable().getFlexCellFormatter().setRowSpan(sampleRow,
                                                                    0,
                                                                    testRow - sampleRow + 1);
                    ui.getTable().getFlexCellFormatter().setRowSpan(sampleRow,
                                                                    3,
                                                                    testRow - sampleRow + 1);
                    ui.getTable().getFlexCellFormatter().setRowSpan(sampleRow,
                                                                    4,
                                                                    testRow - sampleRow + 1);
                    ui.getTable().getFlexCellFormatter().setRowSpan(sampleRow,
                                                                    5,
                                                                    testRow - sampleRow + 1);
                    ui.getTable().getFlexCellFormatter().setRowSpan(sampleRow,
                                                                    6,
                                                                    testRow - sampleRow + 1);
                    ui.getTable()
                      .getRowFormatter()
                      .setVerticalAlign(sampleRow, HasVerticalAlignment.ALIGN_TOP);
                    sb.setLength(0);
                }
                sampleRow = ++testRow;

                qaCount = 1;

                collected = null;
                if (data.getCollectionDate() != null && data.getCollectionDate().getDate() != null) {
                    collected = df.format(data.getCollectionDate().getDate());
                    if (data.getCollectionTime() != null &&
                        data.getCollectionTime().getDate() != null)
                        collected += " " + tf.format(data.getCollectionTime().getDate());
                }

                ui.getTable().setText(sampleRow,
                                      0,
                                      DataBaseUtil.toString(data.getAccessionNumber()));

                sampleData = null;
                if (Constants.domain().ENVIRONMENTAL.equals(data.getDomain())) {
                    if (data.getCollector() != null) {
                        sampleData = data.getCollector();
                    } else {
                        data.getLocation();
                    }
                } else if (Constants.domain().SDWIS.equals(data.getDomain())) {
                    if (data.getCollector() != null) {
                        sampleData = data.getCollector();
                    } else {
                        data.getPwsNumber0();
                    }
                } else if (Constants.domain().CLINICAL.equals(data.getDomain())) {
                    if (data.getPatientLastName() != null) {
                        sampleData = data.getPatientLastName();
                        if (data.getPatientFirstName() != null)
                            sampleData += ", " + data.getPatientFirstName();
                    }
                }
                if (sampleData == null)
                    sampleData = "---";

                sb.append("<font color=\"red\"><ol>");
                if (sampleQas != null && sampleQas.get(data.getSampleId()) != null) {
                    ui.getTable().setHTML(sampleRow,
                                          1,
                                          sampleData + "<sub><font color=\"red\">" + qaCount++ +
                                                          "</font></sub>");
                    sb.append("<li>");
                    for (String qa : sampleQas.get(data.getSampleId())) {
                        sb.append(qa).append("<br>");
                    }
                    sb.append("</li><br>");
                } else {
                    ui.getTable().setHTML(sampleRow, 1, sampleData);
                }
                ui.getTable().setText(sampleRow, 3, collected);
                if (data.getReceivedDate() != null && data.getReceivedDate().getDate() != null)
                    ui.getTable().setText(sampleRow,
                                          4,
                                          dtf.format(data.getReceivedDate().getDate()));
                ui.getTable().setText(sampleRow, 5, data.getClientReference());
            }
            testRow++ ;
            /*
             * insert QA data if there is any
             */
            if (analysisQas != null && analysisQas.get(data.getAnalysisId()) != null) {
                ui.getTable().setHTML(testRow,
                                      0,
                                      data.getTestReportingDescription() + ", " +
                                                      data.getMethodReportingDescription() +
                                                      "<sub><font color=\"red\">" + qaCount++ +
                                                      "</font></sub>");
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
                ui.getTable()
                  .setHTML(testRow,
                           1,
                           (DataBaseUtil.isSame(Constants.dictionary().ANALYSIS_RELEASED,
                                                data.getAnalysisStatusId()) ? completed
                                                                           : inProgress));
            } else {
                ui.getTable()
                  .setHTML(testRow,
                           0,
                           (data.getTestReportingDescription() != null ? data.getTestReportingDescription()
                                                                      : "") +
                                           ", " +
                                           (data.getMethodReportingDescription() != null ? data.getMethodReportingDescription()
                                                                                        : ""));
                ui.getTable()
                  .setHTML(testRow,
                           1,
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
            ui.getTable().setHTML(sampleRow, 6, sb.toString());
            ui.getTable()
              .getCellFormatter()
              .getElement(sampleRow, 6)
              .getStyle()
              .setWhiteSpace(WhiteSpace.PRE_WRAP);
        }
        ui.getTable().getFlexCellFormatter().setRowSpan(sampleRow, 0, testRow - sampleRow + 1);
        ui.getTable().getFlexCellFormatter().setRowSpan(sampleRow, 3, testRow - sampleRow + 1);
        ui.getTable().getFlexCellFormatter().setRowSpan(sampleRow, 4, testRow - sampleRow + 1);
        ui.getTable().getFlexCellFormatter().setRowSpan(sampleRow, 5, testRow - sampleRow + 1);
        ui.getTable().getFlexCellFormatter().setRowSpan(sampleRow, 6, testRow - sampleRow + 1);
        ui.getTable().getRowFormatter().setVerticalAlign(sampleRow, HasVerticalAlignment.ALIGN_TOP);
        ui.getSampleCountText().setInnerText(sampleCount + " " + Messages.get().gen_samplesFound());
    }

    /**
     * fetch samples that match the search criteria
     */
    private void getSampleList() {
        int numDomains;
        String domain;
        Query query;
        ArrayList<QueryData> fields;

        ui.clearErrors();
        ui.getTable().removeAllRows();

        numDomains = 0;
        domain = null;
        query = new Query();
        fields = new ArrayList<QueryData>();

        /*
         * determine the domain that is being queried.
         */
        if ( !DataBaseUtil.isEmpty(ui.getPwsId().getText()) ||
            !DataBaseUtil.isEmpty(ui.getSdwisCollector().getText())) {
            domain = Constants.domain().SDWIS;
            numDomains++ ;
        }
        if ( !DataBaseUtil.isEmpty(ui.getEnvCollector().getText())) {
            domain = Constants.domain().ENVIRONMENTAL;
            numDomains++ ;
        }
        if ( !DataBaseUtil.isEmpty(ui.getPatientFirst().getText()) ||
            !DataBaseUtil.isEmpty(ui.getPatientLast().getText()) ||
            !DataBaseUtil.isEmpty(ui.getPatientBirthFrom().getText()) ||
            !DataBaseUtil.isEmpty(ui.getPatientBirthTo().getText())) {
            domain = Constants.domain().CLINICAL;
            numDomains++ ;
        }

        if (numDomains > 1) {
            Window.alert(Messages.get().finalReport_error_queryDomainException());
            return;
        }

        try {
            fields = createWhereFromParamFields(getQueryFields());
        } catch (Exception e) {
            return;
        }

        if (domain != null)
            fields.add(new QueryData(SampleViewMeta.getDomain(), QueryData.Type.STRING, domain));

        /*
         * if user does not enter any search details, throw an error.
         */
        if (fields.size() == 0) {
            Window.alert(Messages.get().finalReport_error_emptyQueryException());
            return;
        }

        query.setFields(fields);
        window.setBusy(Messages.get().gen_fetchingSamples());

        SampleStatusService.get()
                           .getSampleListForSampleStatusReport(query,
                                                               new AsyncCallback<ArrayList<SampleViewVO>>() {
                                                                   @Override
                                                                   public void onSuccess(ArrayList<SampleViewVO> list) {
                                                                       window.clearStatus();
                                                                       if (list.size() > 0) {
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

    /**
     * retrieve sample and analysis QA data for all of the samples
     */
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

    /**
     * add HTML to a cell in the table
     */
    private void addHtml(int row, int column, String html) {
        ui.getTable().setHTML(row, column, ui.getTable().getHTML(row, column) + html);
    }
}