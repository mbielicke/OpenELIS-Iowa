package org.openelis.portal.modules.finalReport.client;

import static org.openelis.portal.client.Logger.remote;
import static org.openelis.ui.screen.State.QUERY;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.FinalReportWebVO;
import org.openelis.domain.IdNameVO;
import org.openelis.meta.SampleMeta;
import org.openelis.meta.SampleWebMeta;
import org.openelis.portal.cache.CategoryCache;
import org.openelis.portal.messages.Messages;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.ModulePermission;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.widget.CheckBox;
import org.openelis.ui.widget.DateHelper;
import org.openelis.ui.widget.Item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

public class FinalReportScreen extends Screen {

    FinalReportUI                    ui = GWT.create(FinalReportUIImpl.class);

    private ModulePermission         userPermission;

    private FinalReportFormVO        form;

    private HashMap<Integer, String> status;

    public FinalReportScreen() {
        initWidget(ui.asWidget());

        // userPermission =
        // UserCache.getPermission().getModule("w_final_environmental");
        // if (userPermission == null)
        // Window.alert(Messages.get().screenPermException("Final Report Environmental Screen"));

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

        form = new FinalReportFormVO();
        ui.getDeck().showWidget(0);

        ui.getGetSampleListButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                int numDomains;
                String domain;
                Query query;
                QueryData field;
                ArrayList<QueryData> fields;

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
                    field = new QueryData(SampleMeta.getDomain(),
                                          QueryData.Type.STRING,
                                          Constants.domain().SDWIS);
                    domain = Constants.domain().SDWIS;
                    numDomains++ ;
                }
                if ( !DataBaseUtil.isEmpty(ui.getEnvCollector().getText())) {
                    field = new QueryData(SampleMeta.getDomain(),
                                          QueryData.Type.STRING,
                                          Constants.domain().ENVIRONMENTAL);
                    domain = Constants.domain().ENVIRONMENTAL;
                    numDomains++ ;
                }
                if ( !DataBaseUtil.isEmpty(ui.getPatientFirst().getText()) ||
                    !DataBaseUtil.isEmpty(ui.getPatientLast().getText()) ||
                    !DataBaseUtil.isEmpty(ui.getPatientBirthFrom().getText()) ||
                    !DataBaseUtil.isEmpty(ui.getPatientBirthTo().getText())) {
                    field = new QueryData(SampleMeta.getDomain(),
                                          QueryData.Type.STRING,
                                          Constants.domain().CLINICAL);
                    domain = Constants.domain().CLINICAL;
                    numDomains++ ;
                }

                if (numDomains > 1) {
                    Window.alert(Messages.get().finalReport_error_queryDomainException());
                    return;
                }

                /*
                 * check if any of the to fields are filled without the from
                 * fields being filled. Set an error on the widget if they are.
                 */
                if (DataBaseUtil.isEmpty(ui.getCollectedFrom().getText()) &&
                    !DataBaseUtil.isEmpty(ui.getCollectedTo().getText())) {
                    ui.getCollectedTo()
                      .addException(new Exception(Messages.get().finalReport_error_noStartDate()));
                    return;
                }

                if (DataBaseUtil.isEmpty(ui.getReleasedFrom().getText()) &&
                    !DataBaseUtil.isEmpty(ui.getReleasedTo().getText())) {
                    ui.getReleasedTo()
                      .addException(new Exception(Messages.get().finalReport_error_noStartDate()));
                    return;
                }

                if (DataBaseUtil.isEmpty(ui.getAccessionFrom().getText()) &&
                    !DataBaseUtil.isEmpty(ui.getAccessionTo().getText())) {
                    ui.getAccessionTo()
                      .addException(new Exception(Messages.get()
                                                          .finalReport_error_noStartAccession()));
                    return;
                }

                if (DataBaseUtil.isEmpty(ui.getPatientBirthFrom().getText()) &&
                    !DataBaseUtil.isEmpty(ui.getPatientBirthTo().getText())) {
                    ui.getPatientBirthTo()
                      .addException(new Exception(Messages.get().finalReport_error_noStartDate()));
                    return;
                }

                if (domain != null)
                    fields.add(field);
                fields.addAll(createWhereFromParamFields(getQueryFields()));

                query.setFields(fields);

                if (fields.size() < 1) {
                    Window.alert(Messages.get().finalReport_error_emptyQueryException());
                    return;
                }

                // TODO
                // window.setBusy(Messages.get().gen_genReportMessage());
                FinalReportService.get()
                                  .getSampleList(query,
                                                 new AsyncCallback<ArrayList<FinalReportWebVO>>() {

                                                     @Override
                                                     public void onSuccess(ArrayList<FinalReportWebVO> result) {
                                                         DateHelper dh;

                                                         dh = new DateHelper();
                                                         dh.setEnd(Datetime.MINUTE);
                                                         ui.getDeck().showWidget(1);
                                                         if (result.size() > 0) {

                                                             for (int i = 0; i < result.size(); i++ ) {
                                                                 ui.getTable()
                                                                   .setWidget(i + 1,
                                                                              0,
                                                                              new CheckBox());
                                                                 ui.getTable()
                                                                   .setText(i + 1,
                                                                            1,
                                                                            DataBaseUtil.toString(result.get(i)
                                                                                                        .getAccessionNumber()));
                                                                 ui.getTable()
                                                                   .setText(i + 1,
                                                                            2,
                                                                            dh.format(result.get(i)
                                                                                            .getCollectionDateTime()));
                                                                 ui.getTable()
                                                                   .setText(i + 1,
                                                                            3,
                                                                            result.get(i)
                                                                                  .getCollector());
                                                                 ui.getTable()
                                                                   .setText(i + 1,
                                                                            4,
                                                                            status.get(result.get(i)
                                                                                             .getStatusId()));
                                                                 ui.getTable()
                                                                   .setText(i + 1,
                                                                            5,
                                                                            result.get(i)
                                                                                  .getProjectName());

                                                             }
                                                             // ui.getTable()
                                                             // .setModel(model);
                                                         }
                                                     }

                                                     @Override
                                                     public void onFailure(Throwable caught) {
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

        ui.getBackButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                ui.getTable().getRowCount();
                ui.getDeck().showWidget(0);
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                ui.getCollectedFrom().setQueryMode(isState(QUERY));
                ui.getCollectedTo().setQueryMode(isState(QUERY));
                ui.getReleasedFrom().setQueryMode(isState(QUERY));
                ui.getReleasedTo().setQueryMode(isState(QUERY));
                ui.getAccessionFrom().setQueryMode(isState(QUERY));
                ui.getAccessionTo().setQueryMode(isState(QUERY));
                ui.getClientReference().setQueryMode(isState(QUERY));
                ui.getProjectCode().setQueryMode(isState(QUERY));
                ui.getEnvCollector().setQueryMode(isState(QUERY));
                ui.getSdwisCollector().setQueryMode(isState(QUERY));
                ui.getPwsId().setQueryMode(isState(QUERY));
                ui.getPatientFirst().setQueryMode(isState(QUERY));
                ui.getPatientLast().setQueryMode(isState(QUERY));
                ui.getPatientBirthFrom().setQueryMode(isState(QUERY));
                ui.getPatientBirthTo().setQueryMode(isState(QUERY));
            }
        });

        addScreenHandler(ui.getCollectedFrom(),
                         SampleWebMeta.getCollectionDateFrom(),
                         new ScreenHandler<Datetime>() {
                             public void onDataChange(DataChangeEvent event) {
                                 ui.getCollectedFrom().setValue(form.getCollectedFrom());
                             }

                             public void onValueChange(ValueChangeEvent<Datetime> event) {
                                 ui.getCollectedFrom().clearExceptions();
                                 form.setCollectedFrom(event.getValue());
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? ui.getCollectedTo() : ui.getResetButton();
                             }
                         });

        addScreenHandler(ui.getCollectedTo(),
                         SampleWebMeta.getCollectionDateTo(),
                         new ScreenHandler<Datetime>() {
                             public void onDataChange(DataChangeEvent event) {
                                 ui.getCollectedTo().setValue(form.getCollectedTo());
                             }

                             public void onValueChange(ValueChangeEvent<Datetime> event) {
                                 ui.getCollectedTo().clearExceptions();
                                 form.setCollectedTo(event.getValue());
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? ui.getReleasedFrom() : ui.getCollectedFrom();
                             }
                         });

        addScreenHandler(ui.getReleasedFrom(),
                         SampleWebMeta.getReleasedDateFrom(),
                         new ScreenHandler<Datetime>() {
                             public void onDataChange(DataChangeEvent event) {
                                 ui.getReleasedFrom().setValue(form.getReleasedFrom());
                             }

                             public void onValueChange(ValueChangeEvent<Datetime> event) {
                                 ui.getReleasedFrom().clearExceptions();
                                 form.setReleasedFrom(event.getValue());
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? ui.getReleasedTo() : ui.getCollectedTo();
                             }
                         });

        addScreenHandler(ui.getReleasedTo(),
                         SampleWebMeta.getReleasedDateTo(),
                         new ScreenHandler<Datetime>() {
                             public void onDataChange(DataChangeEvent event) {
                                 ui.getReleasedTo().setValue(form.getReleasedTo());
                             }

                             public void onValueChange(ValueChangeEvent<Datetime> event) {
                                 ui.getReleasedTo().clearExceptions();
                                 form.setReleasedTo(event.getValue());
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? ui.getAccessionFrom() : ui.getReleasedFrom();
                             }
                         });

        addScreenHandler(ui.getAccessionFrom(),
                         SampleWebMeta.getAccessionNumberFrom(),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent event) {
                                 ui.getAccessionFrom().setValue(form.getAccessionFrom());
                             }

                             public void onValueChange(ValueChangeEvent<Integer> event) {
                                 ui.getAccessionFrom().clearExceptions();
                                 form.setAccessionFrom(event.getValue());
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? ui.getAccessionTo() : ui.getReleasedTo();
                             }
                         });

        addScreenHandler(ui.getAccessionTo(),
                         SampleWebMeta.getAccessionNumberTo(),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent event) {
                                 ui.getAccessionTo().setValue(form.getAccessionFrom());
                             }

                             public void onValueChange(ValueChangeEvent<Integer> event) {
                                 ui.getAccessionTo().clearExceptions();
                                 form.setAccessionTo(event.getValue());
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? ui.getClientReference() : ui.getAccessionFrom();
                             }
                         });

        addScreenHandler(ui.getClientReference(),
                         SampleWebMeta.getClientReference(),
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
                         });

        addScreenHandler(ui.getProjectCode(),
                         SampleWebMeta.getProjectId(),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent event) {
                                 ui.getProjectCode().setValue(form.getProjectCode());
                             }

                             public void onValueChange(ValueChangeEvent<Integer> event) {
                                 ui.getProjectCode().clearExceptions();
                                 form.setProjectCode(event.getValue());
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? ui.getEnvCollector() : ui.getClientReference();
                             }
                         });

        addScreenHandler(ui.getEnvCollector(),
                         SampleWebMeta.getEnvCollector(),
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
                         });

        addScreenHandler(ui.getSdwisCollector(),
                         SampleWebMeta.getSDWISCollector(),
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
                         });

        addScreenHandler(ui.getPwsId(), SampleWebMeta.getPwsNumber0(), new ScreenHandler<String>() {
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
        });

        addScreenHandler(ui.getPatientFirst(),
                         SampleWebMeta.getClinPatientFirstName(),
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
                         });

        addScreenHandler(ui.getPatientLast(),
                         SampleWebMeta.getClinPatientLastName(),
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
                         });

        addScreenHandler(ui.getPatientBirthFrom(),
                         SampleWebMeta.getClinPatientBirthDateFrom(),
                         new ScreenHandler<Datetime>() {
                             public void onDataChange(DataChangeEvent event) {
                                 ui.getPatientBirthFrom().setValue(form.getPatientBirthFrom());
                             }

                             public void onValueChange(ValueChangeEvent<Datetime> event) {
                                 ui.getPatientBirthFrom().clearExceptions();
                                 form.setPatientBirthFrom(event.getValue());
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? ui.getPatientBirthTo() : ui.getPatientLast();
                             }
                         });

        addScreenHandler(ui.getPatientBirthTo(),
                         SampleWebMeta.getClinPatientBirthDateTo(),
                         new ScreenHandler<Datetime>() {
                             public void onDataChange(DataChangeEvent event) {
                                 ui.getPatientBirthTo().setValue(form.getPatientBirthTo());
                             }

                             public void onValueChange(ValueChangeEvent<Datetime> event) {
                                 ui.getPatientBirthTo().clearExceptions();
                                 form.setPatientBirthTo(event.getValue());
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? ui.getGetSampleListButton()
                                               : ui.getPatientBirthFrom();
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
        model.add(new Item<Integer>(null, ""));
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

        /*
         * initialize the column headers
         */
        ui.getTable().setText(0, 0, Messages.get().finalReport_select());
        ui.getTable().setText(0, 1, Messages.get().finalReport_accessionNumber());
        ui.getTable().setText(0, 2, Messages.get().finalReport_collectedDate());
        ui.getTable().setText(0,
                              3,
                              Messages.get().finalReport_collector() + "/" +
                                              Messages.get().finalReport_patientLast());
        ui.getTable().setText(0, 4, Messages.get().finalReport_select_status());
        ui.getTable().setText(0, 5, Messages.get().finalReport_project());

    }

    private ArrayList<QueryData> createWhereFromParamFields(ArrayList<QueryData> fields) {
        int i;
        QueryData field, fCol, fRel, fAcc, fBir;
        String fColFrom, fColTo, fRelFrom, fRelTo, fAccFrom, fAccTo, fBirFrom, fBirTo;
        ArrayList<QueryData> list;

        list = new ArrayList<QueryData>();
        fRel = fCol = fAcc = fBir = null;
        fColFrom = fColTo = fRelFrom = fRelTo = fAccFrom = fAccTo = fBirFrom = fBirTo = null;

        for (i = 0; i < fields.size(); i++ ) {
            field = fields.get(i);
            if ( (SampleWebMeta.getCollectionDateFrom()).equals(field.getKey())) {
                if (fCol == null) {
                    fCol = field;
                    fCol.setKey(SampleWebMeta.getCollectionDate());
                } else {
                    fCol.setQuery(field.getQuery() + ".." + fCol.getQuery());
                    list.add(fCol);
                }
                fColFrom = field.getQuery();
            } else if ( (SampleWebMeta.getCollectionDateTo()).equals(field.getKey())) {
                if (fCol == null) {
                    fCol = field;
                    fCol.setKey(SampleWebMeta.getCollectionDate());
                } else {
                    fCol.setQuery(fCol.getQuery() + ".." + field.getQuery());
                    list.add(fCol);
                }
                fColTo = field.getQuery();
            } else if ( (SampleWebMeta.getReleasedDateFrom()).equals(field.getKey())) {
                if (fRel == null) {
                    fRel = field;
                    fRel.setKey(SampleWebMeta.getReleasedDate());
                } else {
                    fRel.setQuery(field.getQuery() + ".." + fRel.getQuery());
                    list.add(fRel);
                }
                fRelFrom = field.getQuery();
            } else if ( (SampleWebMeta.getReleasedDateTo()).equals(field.getKey())) {
                if (fRel == null) {
                    fRel = field;
                    fRel.setKey(SampleWebMeta.getReleasedDate());
                } else {
                    fRel.setQuery(fRel.getQuery() + ".." + field.getQuery());
                    list.add(fRel);
                }
                fRelTo = field.getQuery();
            } else if ( (SampleWebMeta.getAccessionNumberFrom()).equals(field.getKey())) {
                if (fAcc == null) {
                    fAcc = field;
                    fAcc.setKey(SampleWebMeta.getAccessionNumber());
                } else {
                    fAcc.setQuery(field.getQuery() + ".." + fAcc.getQuery());
                    list.add(fAcc);
                }
                fAccFrom = field.getQuery();
            } else if ( (SampleWebMeta.getAccessionNumberTo()).equals(field.getKey())) {
                if (fAcc == null) {
                    fAcc = field;
                    fAcc.setKey(SampleWebMeta.getAccessionNumber());
                } else {
                    fAcc.setQuery(fAcc.getQuery() + ".." + field.getQuery());
                    list.add(fAcc);
                }
                fAccTo = field.getQuery();
            } else if ( (SampleWebMeta.getClinPatientBirthDateFrom()).equals(field.getKey())) {
                if (fBir == null) {
                    fBir = field;
                    fBir.setKey(SampleWebMeta.getClinPatientBirthDate());
                } else {
                    fBir.setQuery(field.getQuery() + ".." + fBir.getQuery());
                    list.add(fBir);
                }
                fBirFrom = field.getQuery();
            } else if ( (SampleWebMeta.getClinPatientBirthDateTo()).equals(field.getKey())) {
                if (fBir == null) {
                    fBir = field;
                    fBir.setKey(SampleWebMeta.getClinPatientBirthDate());
                } else {
                    fBir.setQuery(fBir.getQuery() + ".." + field.getQuery());
                    list.add(fBir);
                }
                fBirTo = field.getQuery();
            } else {
                list.add(field);
            }
        }
        if (fColFrom != null && fColTo == null)
            list.add(fCol);
        if (fRelFrom != null && fRelTo == null)
            list.add(fRel);
        if (fAccFrom != null && fAccTo == null)
            list.add(fAcc);
        if (fBirFrom != null && fBirTo == null)
            list.add(fBir);
        return list;
    }
}
