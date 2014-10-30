package org.openelis.portal.modules.finalReport.client;

import static org.openelis.portal.client.Logger.remote;
import static org.openelis.ui.screen.State.QUERY;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;

import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
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
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

public class FinalReportScreen extends Screen {

    private FinalReportUI            ui = GWT.create(FinalReportUIImpl.class);

    private ModulePermission         userPermission;

    private FinalReportFormVO        form;

    private HashMap<Integer, String> status;

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

        // ui.getTable().addClickHandler(new ClickHandler() {
        //
        // @Override
        // public void onClick(ClickEvent event) {
        // ui.getTable().getWidget(event.get, 0);
        // }
        // })

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

        addScreenHandler(ui.getSdwisCollector(),
                         SampleViewMeta.getCollector(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 ui.getSdwisCollector().setValue(form.getSdwisCollector());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 ui.getSdwisCollector().clearExceptions();
                                 form.setSdwisCollector(event.getValue());
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? ui.getPwsId() : ui.getEnvCollector();
                             }

                             @Override
                             public Object getQuery() {
                                 return ui.getSdwisCollector().getQuery();
                             }
                         });

        addScreenHandler(ui.getPwsId(),
                         SampleViewMeta.getPwsNumber0(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 ui.getPwsId().setValue(form.getPwsId());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
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

        addScreenHandler(ui.getGetSampleListButton(), "", new ScreenHandler<Integer>() {
            public Widget onTab(boolean forward) {
                return forward ? ui.getResetButton() : ui.getPatientBirthTo();
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

        status = new HashMap<Integer, String>();
        for (DictionaryDO d : CategoryCache.getBySystemName("sample_status")) {
            status.put(d.getId(), d.getEntry());
        }
    }

    /**
     * create the range queries for variables with from and to fields
     */
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
            ui.setCollectedError(Messages.get().finalReport_error_noStartDate());
            throw e;
        }

        try {
            getRangeQuery(SampleViewMeta.getReleasedDateFrom(),
                          SampleViewMeta.getReleasedDateTo(),
                          SampleViewMeta.getReleasedDate(),
                          fieldMap);
        } catch (Exception e) {
            ui.setReleasedError(Messages.get().finalReport_error_noStartDate());
            throw e;
        }

        try {
            getRangeQuery(SampleViewMeta.getAccessionNumberFrom(),
                          SampleViewMeta.getAccessionNumberTo(),
                          SampleViewMeta.getAccessionNumber(),
                          fieldMap);
        } catch (Exception e) {
            ui.setAccessionError(Messages.get().finalReport_error_noStartAccession());
            throw e;
        }

        try {
            getRangeQuery(SampleViewMeta.getPatientBirthDateFrom(),
                          SampleViewMeta.getPatientBirthDateTo(),
                          SampleViewMeta.getPatientBirthDate(),
                          fieldMap);
        } catch (Exception e) {
            ui.setPatientBirthError(Messages.get().finalReport_error_noStartDate());
            throw e;
        }

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

    @SuppressWarnings("deprecation")
    private void setTableData(ArrayList<SampleViewVO> samples) {
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
        ui.getTable().setText(0, 4, Messages.get().finalReport_select_status());
        ui.getTable().setText(0, 5, Messages.get().finalReport_project());
        ui.getTable().getRowFormatter().setStyleName(0, UIResources.INSTANCE.table().Header());
        ui.getTable().getElement().getStyle().setTextAlign(TextAlign.CENTER);
        ui.getTable().getElement().getStyle().setPadding(12, Unit.PX);
        ui.getTable().getElement().getStyle().setFontSize(18, Unit.PX);

        dh = new DateHelper();
        dh.setEnd(Datetime.MINUTE);

        for (int i = 0, j = 1; i < samples.size(); i++ , j++ ) {
            sample = samples.get(i);
            check = new CheckBox();
            check.setEnabled(true);
            ui.getTable().setWidget(j, 0, check);
            ui.getTable().setText(j, 1, DataBaseUtil.toString(sample.getAccessionNumber()));
            collectionTime = sample.getCollectionTime();
            collection = sample.getCollectionDate().getDate();
            try {
                collection.setHours(collectionTime.get(Datetime.HOUR));
                collection.setMinutes(collectionTime.get(Datetime.MINUTE));
            } catch (Exception e) {
                // time is null
            }

            ui.getTable()
              .setText(j, 2, dh.format(new Datetime(Datetime.YEAR, Datetime.MINUTE, collection)));

            /*
             * show the collector for environmental and SDWIS samples and show
             * the patient's last name for clinical samples
             */
            if ( !DataBaseUtil.isDifferent(sample.getDomain(), "E") ||
                !DataBaseUtil.isDifferent(sample.getDomain(), "S")) {
                if (sample.getCollector() != null)
                    ui.getTable().setText(j, 3, "[collector] " + sample.getCollector());
            } else if ( !DataBaseUtil.isDifferent(sample.getDomain(), "C") &&
                       sample.getPatientLastName() != null) {
                ui.getTable().setText(j, 3, "[patient] " + sample.getPatientLastName());
            }
            ui.getTable().setText(j, 4, status.get(sample.getSampleStatusId()));
            if (DataBaseUtil.isSame(Constants.dictionary().SAMPLE_RELEASED,
                                    sample.getSampleStatusId()) ||
                DataBaseUtil.isSame(Constants.dictionary().SAMPLE_COMPLETED,
                                    sample.getSampleStatusId())) {
                ui.getTable().getCellFormatter().setStyleName(j,
                                                              4,
                                                              UIResources.INSTANCE.table()
                                                                                  .GreenStatus());
            } else if (DataBaseUtil.isSame(Constants.dictionary().SAMPLE_LOGGED_IN,
                                           sample.getSampleStatusId()) ||
                       DataBaseUtil.isSame(Constants.dictionary().SAMPLE_NOT_VERIFIED,
                                           sample.getSampleStatusId())) {
                ui.getTable().getCellFormatter().setStyleName(j,
                                                              4,
                                                              UIResources.INSTANCE.table()
                                                                                  .YellowStatus());
            } else if (DataBaseUtil.isSame(Constants.dictionary().SAMPLE_ERROR,
                                           sample.getSampleStatusId())) {
                ui.getTable().getCellFormatter().setStyleName(j,
                                                              4,
                                                              UIResources.INSTANCE.table()
                                                                                  .RedStatus());
            }
            ui.getTable().setText(j, 5, sample.getProjectName());

            /*
             * set row height higher for mobile and tablet versions
             */
            // ui.setWidgetWidth(j);
            ui.getTable().getCellFormatter().getElement(j, 0).getStyle().setPadding(10, Unit.PX);
            ui.getTable().getCellFormatter().getElement(j, 1).getStyle().setPadding(10, Unit.PX);
            ui.getTable().getCellFormatter().getElement(j, 2).getStyle().setPadding(10, Unit.PX);
            ui.getTable().getCellFormatter().getElement(j, 3).getStyle().setPadding(10, Unit.PX);
            ui.getTable().getCellFormatter().getElement(j, 4).getStyle().setPadding(10, Unit.PX);
            ui.getTable().getCellFormatter().getElement(j, 5).getStyle().setPadding(10, Unit.PX);
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
        QueryData field;
        ArrayList<QueryData> fields;

        ui.clearErrors();
        ui.getTable().removeAllRows();
        numDomains = 0;
        domain = null;
        query = new Query();
        field = new QueryData();
        fields = new ArrayList<QueryData>();

        /*
         * determine the domain that is being queried.
         */
        if ( !DataBaseUtil.isEmpty(ui.getPwsId().getText()) ||
            !DataBaseUtil.isEmpty(ui.getSdwisCollector().getText())) {
            field = new QueryData(SampleViewMeta.getDomain(),
                                  QueryData.Type.STRING,
                                  Constants.domain().SDWIS);
            domain = Constants.domain().SDWIS;
            numDomains++ ;
        }
        if ( !DataBaseUtil.isEmpty(ui.getEnvCollector().getText())) {
            field = new QueryData(SampleViewMeta.getDomain(),
                                  QueryData.Type.STRING,
                                  Constants.domain().ENVIRONMENTAL);
            domain = Constants.domain().ENVIRONMENTAL;
            numDomains++ ;
        }
        if ( !DataBaseUtil.isEmpty(ui.getPatientFirst().getText()) ||
            !DataBaseUtil.isEmpty(ui.getPatientLast().getText()) ||
            !DataBaseUtil.isEmpty(ui.getPatientBirthFrom().getText()) ||
            !DataBaseUtil.isEmpty(ui.getPatientBirthTo().getText())) {
            field = new QueryData(SampleViewMeta.getDomain(),
                                  QueryData.Type.STRING,
                                  Constants.domain().CLINICAL);
            domain = Constants.domain().CLINICAL;
            numDomains++ ;
        }

        if (numDomains > 1) {
            Window.alert(Messages.get().finalReport_error_queryDomainException());
            return;
        }

        if (domain != null)
            fields.add(field);

        try {
            fields.addAll(createWhereFromParamFields(getQueryFields()));
        } catch (Exception e) {
            return;
        }

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
                    String url = "/portal/portal/report?file=" + result.getMessage();
                    Window.open(URL.encode(url), "FinalReport", null);
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
}