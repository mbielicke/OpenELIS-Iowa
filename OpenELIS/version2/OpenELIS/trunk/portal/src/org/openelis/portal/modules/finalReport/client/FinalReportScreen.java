package org.openelis.portal.modules.finalReport.client;

import static org.openelis.portal.client.Logger.remote;
import static org.openelis.ui.screen.State.QUERY;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;

import org.openelis.domain.Constants;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.SampleViewVO;
import org.openelis.meta.SampleViewMeta;
import org.openelis.portal.cache.CategoryCache;
import org.openelis.portal.cache.UserCache;
import org.openelis.portal.messages.Messages;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.ModulePermission;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.resources.UIResources;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.widget.CheckBox;
import org.openelis.ui.widget.DateHelper;
import org.openelis.ui.widget.Item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class FinalReportScreen extends Screen {

    private FinalReportUI          ui = GWT.create(FinalReportUIImpl.class);

    private ModulePermission       userPermission;

    private FinalReportFormVO      form;

    private StatusBarPopupScreenUI statusScreen;

    public FinalReportScreen() {
        initWidget(ui.asWidget());

        userPermission = UserCache.getPermission().getModule("w_final_report");
        if (userPermission == null) {
            Window.alert(Messages.get().error_screenPerm("Final Report Screen"));
            return;
        }

        try {
            CategoryCache.getBySystemNames("sample_status");
        } catch (Exception e) {
            remote.log(Level.SEVERE, e.getMessage(), e);
            Window.alert("error accessing cache");
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

        ui.getSelectAllButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                for (int i = 1; i < ui.getTable().getRowCount(); i++ )
                    ((CheckBox)ui.getTable().getWidget(i, 0)).setValue("Y");
            }
        });

        ui.getUnselectAllButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                for (int i = 1; i < ui.getTable().getRowCount(); i++ )
                    ((CheckBox)ui.getTable().getWidget(i, 0)).setValue("N");
            }
        });

        ui.getRunReportButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                runReport();
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
                                 return forward ? ui.getPatientLast() : ui.getSdwisCollector();
                             }

                             @Override
                             public Object getQuery() {
                                 return ui.getPwsId().getQuery();
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
                                 return forward ? ui.getPatientFirst() : ui.getPwsId();
                             }

                             @Override
                             public Object getQuery() {
                                 return ui.getPatientLast().getQuery();
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
                                 return forward ? ui.getPatientBirthFrom() : ui.getPatientLast();
                             }

                             @Override
                             public Object getQuery() {
                                 return ui.getPatientFirst().getQuery();
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
                                 return forward ? ui.getPatientBirthTo() : ui.getPatientFirst();
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
                         "sampleListButton",
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
            list = FinalReportService.get().getProjectList();
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
            ui.setReleasedError(Messages.get().finalReport_error_noStartDate());
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
        String completed, inProgress;
        StringBuffer additional, referenceInfo;
        SampleViewVO sample;
        DateHelper dh;
        CheckBox check;
        Datetime collectionTime;
        Date collection;

        /*
         * if there are no samples returned, tell the user
         */
        if (samples.size() < 1) {
            Window.alert(Messages.get().finalReport_error_noSamples());
            return;
        }

        /*
         * show the sample list table
         */
        ui.getDeck().showWidget(1);

        /*
         * initialize the column headers
         */
        ui.getTable().setText(0, 1, Messages.get().sample_accessionNumber());
        ui.getTable().setText(0, 2, Messages.get().sample_collectedDate());
        ui.getTable().setText(0, 3, Messages.get().finalReport_referenceInfo());
        ui.getTable().setText(0, 4, Messages.get().finalReport_additionalInfo());
        ui.getTable().setText(0, 5, Messages.get().finalReport_select_status());
        ui.getTable().setText(0, 6, Messages.get().sample_project());
        ui.getTable().getRowFormatter().setStyleName(0, UIResources.INSTANCE.table().Header());
        ui.getTable().getElement().getStyle().setFontSize(12, Unit.PX);

        additional = new StringBuffer();
        referenceInfo = new StringBuffer();
        dh = new DateHelper();
        dh.setEnd(Datetime.MINUTE);
        completed = Messages.get().sampleStatus_completed();
        inProgress = Messages.get().sampleStatus_inProgress();

        for (int i = 0, j = 1; i < samples.size(); i++ , j++ ) {
            sample = samples.get(i);
            check = new CheckBox();
            check.setEnabled(true);
            ui.getTable().setWidget(j, 0, check);
            ui.getTable().setText(j, 1, DataBaseUtil.toString(sample.getAccessionNumber()));
            collectionTime = sample.getCollectionTime();
            collection = null;
            try {
                collection = sample.getCollectionDate().getDate();
                collection.setHours(collectionTime.get(Datetime.HOUR));
                collection.setMinutes(collectionTime.get(Datetime.MINUTE));
            } catch (Exception e) {
                // date or time is null
            }

            if (collection != null)
                ui.getTable().setText(j,
                                      2,
                                      dh.format(new Datetime(Datetime.YEAR,
                                                             Datetime.MINUTE,
                                                             collection)));

            /*
             * show the collector for environmental and SDWIS samples and show
             * the patient's last name for clinical samples
             */
            if ( !DataBaseUtil.isDifferent(sample.getDomain(), "E") ||
                !DataBaseUtil.isDifferent(sample.getDomain(), "S") ||
                !DataBaseUtil.isDifferent(sample.getDomain(), "W")) {
                if (sample.getCollector() != null) {
                    referenceInfo.append("[collector] ").append(sample.getCollector());
                }
            } else if ( !DataBaseUtil.isDifferent(sample.getDomain(), "C") &&
                       sample.getPatientLastName() != null) {
                referenceInfo.append("[patient] ").append(sample.getPatientLastName());
                if (sample.getPatientFirstName() != null)
                    referenceInfo.append(", ").append(sample.getPatientFirstName());
            }
            if (referenceInfo.length() > 0) {
                ui.getTable().setText(j, 3, referenceInfo.toString());
                referenceInfo.setLength(0);
            }

            //@formatter:off
            /*
             * display the following data in the 
             * "Additional Information" column, if available:
             * Environmental: Location, Location Street Address, and Location City
             * Clinical: Provider and Org Name
             * SDWIS: Location and PWS Name, ID
             * Private Well: Location and Location City
             */
            //@formatter:on
            if ( !DataBaseUtil.isDifferent(sample.getDomain(), "E") ||
                !DataBaseUtil.isDifferent(sample.getDomain(), "W")) {
                if (sample.getLocation() != null) {
                    additional.append(sample.getLocation());
                }
                if (sample.getLocationStreetAddress() != null) {
                    if (additional.length() > 0)
                        additional.append("<br/>");
                    additional.append(sample.getLocationStreetAddress());
                }
                if (sample.getLocationCity() != null) {
                    if (additional.length() > 0)
                        additional.append("<br/>");
                    additional.append(sample.getLocationCity());
                }
                ui.getTable().setHTML(j, 4, additional.toString());
                additional.setLength(0);
            } else if ( !DataBaseUtil.isDifferent(sample.getDomain(), "S")) {
                if (sample.getLocation() != null) {
                    additional.append(sample.getLocation());
                }
                if (sample.getPwsNumber0() != null && sample.getPwsName() != null) {
                    if (additional.length() > 0)
                        additional.append("<br/>");
                    additional.append(sample.getPwsNumber0())
                              .append("-")
                              .append(sample.getPwsName());
                }
                ui.getTable().setHTML(j, 4, additional.toString());
                additional.setLength(0);
            } else if ( !DataBaseUtil.isDifferent(sample.getDomain(), "C")) {
                if (sample.getProviderName() != null) {
                    additional.append(sample.getProviderName());
                }
                if (sample.getReportToName() != null) {
                    if (additional.length() > 0)
                        additional.append("<br/>");
                    additional.append(sample.getReportToName());
                }
                ui.getTable().setHTML(j, 4, additional.toString());
                additional.setLength(0);
            }

            /*
             * If analysis status is Released, screen displays
             * "Completed status", for all other statuses screen displays
             * "In Progress".
             */
            ui.getTable().setText(j,
                                  5,
                                  DataBaseUtil.isSame(Constants.dictionary().SAMPLE_RELEASED,
                                                      sample.getSampleStatusId()) ? completed
                                                                                 : inProgress);
            ui.getTable().setText(j, 6, sample.getProjectName());
            /*
             * align all cells to the top
             */
            ui.getTable().getRowFormatter().setVerticalAlign(j, HasVerticalAlignment.ALIGN_TOP);
        }
        /*
         * display how many samples were returned
         */
        if (ui.getTable().getRowCount() > 0) {
            ui.getRowCountText().setInnerText(ui.getTable().getRowCount() - 1 + " " +
                                              Messages.get().gen_samplesFound());
        }
        ui.setCheckBoxCSS();
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
            fields.addAll(createWhereFromParamFields(getQueryFields()));
        } catch (Exception e) {
            return;
        }

        if (domain != null)
            fields.add(new QueryData(SampleViewMeta.getDomain(), QueryData.Type.STRING, domain));

        query.setFields(fields);

        if (fields.size() < 1) {
            Window.alert(Messages.get().finalReport_error_emptyQueryException());
            return;
        }

        window.setBusy(Messages.get().gen_fetchingSamples());
        FinalReportService.get().getSampleList(query, new AsyncCallback<ArrayList<SampleViewVO>>() {

            @Override
            public void onSuccess(ArrayList<SampleViewVO> result) {
                window.clearStatus();
                setTableData(result);
            }

            @Override
            public void onFailure(Throwable caught) {
                window.clearStatus();
                Window.alert(caught.getMessage());
            }
        });
    }

    /**
     * fetch final reports for checked samples
     */
    private void runReport() {
        Query query;
        QueryData field;
        String val;

        query = new Query();
        field = new QueryData();
        field.setKey("SAMPLE_ID");
        for (int i = 1, j = 0; i < ui.getTable().getRowCount(); i++ , j++ ) {
            val = String.valueOf(j);
            if ( !DataBaseUtil.isDifferent( ((CheckBox)ui.getTable().getWidget(i, 0)).getValue(),
                                           "Y")) {
                if (field.getQuery() == null)
                    field.setQuery(val);
                else
                    field.setQuery(field.getQuery() + "," + val);
            }
        }
        query.setFields(field);

        if (field.getQuery() == null) {
            Window.alert(Messages.get().finalReport_error_noSampleSelected());
            return;
        }

        window.setBusy(Messages.get().gen_genReportMessage());
        FinalReportService.get().runReportForWeb(query, new AsyncCallback<ReportStatus>() {

            @Override
            public void onSuccess(ReportStatus result) {
                window.clearStatus();
                if (result.getStatus() == ReportStatus.Status.SAVED) {
                    String url = "/openelisweb/openelisweb/report?file=" + result.getMessage();
                    Window.open(URL.encode(url), "FinalReport", "resizable=yes");
                }
                window.clearStatus();
            }

            @Override
            public void onFailure(Throwable caught) {
                window.clearStatus();
                Window.alert(caught.getMessage());
            }
        });
    }

    /**
     * creates a popup that shows the progress of creating final reports
     */
    private void popup(Query query) {
        final PopupPanel statusPanel;

        if (statusScreen == null)
            statusScreen = new StatusBarPopupScreenUI();

        /*
         * initialize and show the popup screen
         */
        statusPanel = new PopupPanel();
        statusPanel.setSize("450px", "125px");
        statusScreen.setSize("450px", "125px");
        statusPanel.setWidget(statusScreen);
        statusPanel.setPopupPosition(this.getAbsoluteLeft(), this.getAbsoluteTop());
        statusPanel.setModal(true);
        statusPanel.show();
        statusScreen.setStatus(null);

        /*
         * Create final reports. Hide popup when database is updated
         * successfully or error is thrown.
         */
        FinalReportService.get().runReportForWeb(query, new AsyncCallback<ReportStatus>() {

            @Override
            public void onSuccess(ReportStatus result) {
                window.clearStatus();
                statusPanel.hide();
                statusScreen.setStatus(null);
                if (result.getStatus() == ReportStatus.Status.SAVED) {
                    String url = "/openelisweb/openelisweb/report?file=" + result.getMessage();
                    Window.open(URL.encode(url), "FinalReport", "resizable=yes");
                }
                window.clearStatus();
            }

            @Override
            public void onFailure(Throwable caught) {
                window.clearStatus();
                statusPanel.hide();
                statusScreen.setStatus(null);
                Window.alert(caught.getMessage());
            }
        });

        /*
         * refresh the status of creating the reports every second, until the
         * process successfully completes or is aborted because of an error
         */
        Timer timer = new Timer() {
            public void run() {
                ReportStatus status;
                try {
                    status = FinalReportService.get().getStatus();
                    /*
                     * the status only needs to be refreshed while the status
                     * panel is showing because once the job is finished, the
                     * panel is closed
                     */
                    if (statusPanel.isShowing()) {
                        statusScreen.setStatus(status);
                        this.schedule(1000);
                    }
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    remote.log(Level.SEVERE, e.getMessage(), e);
                }
            }
        };
        timer.schedule(1000);
    }
}