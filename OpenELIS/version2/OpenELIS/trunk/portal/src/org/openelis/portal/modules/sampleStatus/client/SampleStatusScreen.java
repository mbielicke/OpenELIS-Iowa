package org.openelis.portal.modules.sampleStatus.client;

import static org.openelis.portal.client.Logger.remote;
import static org.openelis.ui.screen.State.QUERY;

import java.util.ArrayList;
import java.util.logging.Level;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.SampleStatusWebReportVO;
import org.openelis.meta.SampleViewMeta;
import org.openelis.portal.cache.UserCache;
import org.openelis.portal.messages.Messages;
import org.openelis.portal.modules.finalReport.client.FinalReportFormVO;
import org.openelis.portal.modules.finalReport.client.FinalReportService;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.ModulePermission;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
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
                queryList = createWhereFromParamFields(getQueryFields());
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
                                                                                   window.clearStatus();
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

    private ArrayList<QueryData> createWhereFromParamFields(ArrayList<QueryData> fields) {
        int i;
        QueryData field, fCol, fAcc;
        ArrayList<QueryData> list;

        list = new ArrayList<QueryData>();
        fCol = fAcc = null;

        for (i = 0; i < fields.size(); i++ ) {
            field = fields.get(i);
            if ( (SampleViewMeta.getCollectionDateFrom()).equals(field.getKey())) {
                if (fCol == null) {
                    fCol = field;
                    fCol.setKey(SampleViewMeta.getCollectionDate());
                } else {
                    fCol.setQuery(field.getQuery() + ".." + fCol.getQuery());
                    list.add(fCol);
                }
            } else if ( (SampleViewMeta.getCollectionDateTo()).equals(field.getKey())) {
                if (fCol == null) {
                    fCol = field;
                    fCol.setKey(SampleViewMeta.getCollectionDate());
                } else {
                    fCol.setQuery(fCol.getQuery() + ".." + field.getQuery());
                    list.add(fCol);
                }
            } else if ( (SampleViewMeta.getAccessionNumberFrom()).equals(field.getKey())) {
                if (fAcc == null) {
                    fAcc = field;
                    fAcc.setKey(SampleViewMeta.getAccessionNumber());
                } else {
                    fAcc.setQuery(field.getQuery() + ".." + fAcc.getQuery());
                    list.add(fAcc);
                }
            } else if ( (SampleViewMeta.getAccessionNumberTo()).equals(field.getKey())) {
                if (fAcc == null) {
                    fAcc = field;
                    fAcc.setKey(SampleViewMeta.getAccessionNumber());
                } else {
                    fAcc.setQuery(fAcc.getQuery() + ".." + field.getQuery());
                    list.add(fAcc);
                }
            } else {
                list.add(field);
            }
        }
        return list;
    }
}