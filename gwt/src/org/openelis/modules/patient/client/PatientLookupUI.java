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
package org.openelis.modules.patient.client;

import static org.openelis.modules.main.client.Logger.logger;
import static org.openelis.ui.screen.Screen.Validation.Status.VALID;
import static org.openelis.ui.screen.State.QUERY;

import java.util.ArrayList;
import java.util.logging.Level;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

import org.openelis.cache.CategoryCache;
import org.openelis.constants.Messages;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.PatientDO;
import org.openelis.domain.PatientRelationVO;
import org.openelis.domain.SampleAnalysisVO;
import org.openelis.meta.PatientMeta;
import org.openelis.modules.analysis.client.AnalysisService;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.event.BeforeCloseEvent;
import org.openelis.ui.event.BeforeCloseHandler;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.resources.UIResources;
import org.openelis.ui.screen.AsyncCallbackUI;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.ModalWindow;
import org.openelis.ui.widget.TextBox;
import org.openelis.ui.widget.WindowInt;
import org.openelis.ui.widget.calendar.Calendar;
import org.openelis.ui.widget.table.Row;
import org.openelis.ui.widget.table.Table;
import org.openelis.ui.widget.table.event.BeforeCellEditedEvent;
import org.openelis.ui.widget.table.event.BeforeCellEditedHandler;

public abstract class PatientLookupUI extends Screen {

    @UiTemplate("PatientLookup.ui.xml")
    interface PatientLookupUiBinder extends UiBinder<Widget, PatientLookupUI> {
    };

    private static PatientLookupUiBinder                    uiBinder = GWT.create(PatientLookupUiBinder.class);

    @UiField
    protected Button                                        search, select, cancel;

    @UiField
    protected Calendar                                      birthDate;

    @UiField
    protected Dropdown<Integer>                             gender, patientRelation;

    @UiField
    protected Dropdown<String>                              patientState;

    @UiField
    protected Table                                         patientTable, sampleTable,
                                                            nextOfKinTable;

    @UiField
    protected TextBox<String>                               lastName, firstName, nationalId;

    protected PatientDO                                     queriedPatient, selectedPatient;

    protected PatientRelationVO                             selectedNextOfKin;

    protected boolean                                       selectFirstPatient, dontShowIfNoPatient,
                                                            queryByNId;

    protected AsyncCallbackUI<ArrayList<PatientDO>>         queryCall;

    protected AsyncCallbackUI<ArrayList<PatientRelationVO>> fetchByRelatedPatientCall;

    protected AsyncCallbackUI<ArrayList<SampleAnalysisVO>>  fetchByPatientCall;

    public PatientLookupUI() {
        initWidget(uiBinder.createAndBindUi(this));
        initialize();
        setState(QUERY);
        fireDataChange();

        logger.fine("PatientLookupScreenUI Opened");
    }

    /**
     * Setup state and data change handles for every widget on the screen
     */
    public void initialize() {
        ArrayList<DictionaryDO> list;
        ArrayList<Item<Integer>> model;
        ArrayList<Item<String>> smodel;

        //
        // screen fields and buttons
        //
        addScreenHandler(lastName, PatientMeta.getLastName(), new ScreenHandler<String>() {
            public void onStateChange(StateChangeEvent event) {
                lastName.setEnabled(!queryByNId && isState(QUERY));
                lastName.setQueryMode(!queryByNId && isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? firstName : cancel;
            }
        });

        addScreenHandler(firstName, PatientMeta.getFirstName(), new ScreenHandler<String>() {
            public void onStateChange(StateChangeEvent event) {
                firstName.setEnabled(!queryByNId && isState(QUERY));
                firstName.setQueryMode(!queryByNId && isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? birthDate : lastName;
            }
        });

        addScreenHandler(birthDate, PatientMeta.getBirthDate(), new ScreenHandler<Datetime>() {
            public void onStateChange(StateChangeEvent event) {
                birthDate.setEnabled(!queryByNId && isState(QUERY));
                birthDate.setQueryMode(!queryByNId && isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? nationalId : firstName;
            }
        });

        addScreenHandler(nationalId, PatientMeta.getNationalId(), new ScreenHandler<String>() {
            public void onStateChange(StateChangeEvent event) {
                nationalId.setEnabled(!queryByNId && isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? search : birthDate;
            }
        });

        addScreenHandler(search, "search", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                search.setEnabled(!queryByNId);
            }

            public Widget onTab(boolean forward) {
                return forward ? patientTable : nationalId;
            }
        });

        //
        // patient table
        //
        addScreenHandler(patientTable, "patientTable", new ScreenHandler<ArrayList<Row>>() {
            public void onStateChange(StateChangeEvent event) {
                patientTable.setEnabled(true);
            }

            public Widget onTab(boolean forward) {
                return forward ? select : search;
            }
        });

        patientTable.addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {
            @Override
            public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {
                PatientDO data;

                data = patientTable.getRowAt(0).getData();
                if (queryByNId &&
                    (queriedPatient.getId() != null && !queriedPatient.getId().equals(data.getId())))
                    event.cancel();
            }
        });

        patientTable.addSelectionHandler(new SelectionHandler<Integer>() {
            public void onSelection(SelectionEvent<Integer> event) {
                patientSelected();
            }
        });

        patientTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                // this table cannot be edited
                event.cancel();
            }
        });

        //
        // next of kin table
        //
        addScreenHandler(nextOfKinTable, "nextOfKinTable", new ScreenHandler<ArrayList<Row>>() {
            public void onStateChange(StateChangeEvent event) {
                nextOfKinTable.setEnabled(!queryByNId);
            }
        });

        nextOfKinTable.addSelectionHandler(new SelectionHandler<Integer>() {
            public void onSelection(SelectionEvent<Integer> event) {
                selectedNextOfKin = nextOfKinTable.getRowAt(nextOfKinTable.getSelectedRow())
                                                  .getData();
            }
        });

        nextOfKinTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                // this table cannot be edited
                event.cancel();
            }
        });

        //
        // sample table
        //
        addScreenHandler(sampleTable, "sampleTable", new ScreenHandler<ArrayList<Row>>() {
            public void onStateChange(StateChangeEvent event) {
                sampleTable.setEnabled(true);
            }
        });

        sampleTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                // this table cannot be edited
                event.cancel();
            }
        });

        addScreenHandler(select, "select", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                select.setEnabled(!queryByNId);
            }

            public Widget onTab(boolean forward) {
                return forward ? cancel : patientTable;
            }
        });

        addScreenHandler(cancel, "cancel", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                cancel.setEnabled(true);
            }

            public Widget onTab(boolean forward) {
                return forward ? lastName : select;
            }
        });

        //
        // load gender dropdown model
        //
        list = CategoryCache.getBySystemName("gender");
        model = new ArrayList<Item<Integer>>();
        for (DictionaryDO resultDO : list)
            model.add(new Item<Integer>(resultDO.getId(), resultDO.getEntry()));
        gender.setModel(model);

        //
        // load patient relation dropdown model
        //
        list = CategoryCache.getBySystemName("patient_relation");
        model = new ArrayList<Item<Integer>>();
        for (DictionaryDO resultDO : list)
            model.add(new Item<Integer>(resultDO.getId(), resultDO.getEntry()));
        patientRelation.setModel(model);

        //
        // load state dropdown model
        //
        list = CategoryCache.getBySystemName("state");
        smodel = new ArrayList<Item<String>>();
        for (DictionaryDO resultDO : list)
            model.add(new Item<Integer>(resultDO.getId(), resultDO.getEntry()));
        patientState.setModel(smodel);
    }

    /**
     * overridden to respond to the user clicking "select"
     */
    public abstract void select();

    /**
     * overridden to respond to the user clicking "cancel"
     */
    public abstract void cancel();

    /**
     * overriden to respond to a query returning some results
     */
    public abstract void patientsFound();

    public void setWindow(WindowInt window) {
        super.setWindow(window);
        window.addBeforeClosedHandler(new BeforeCloseHandler<WindowInt>() {
            public void onBeforeClosed(BeforeCloseEvent<WindowInt> event) {
                patientTable.setModel(null);
                sampleTable.setModel(null);
                nextOfKinTable.setModel(null);
            }
        });
    }

    public void setState(State state) {
        this.state = state;
        bus.fireEventFromSource(new StateChangeEvent(state), this);
    }

    /**
     * Finds patients based on the data in the DO. If the flag is true then the
     * lookup screen is not shown if no patient was found; otherwise the screen
     * is shown regardless.
     */
    public void query(PatientDO data, boolean dontShowIfNoPatient) {
        window = null;
        selectedPatient = null;
        selectedNextOfKin = null;
        this.dontShowIfNoPatient = dontShowIfNoPatient;

        lastName.setValue(null);
        firstName.setValue(null);
        birthDate.setValue(null);
        nationalId.setValue(null);
        queryByNId = false;
        queriedPatient = data;
        if (data != null) {
            /*
             * allow querying by either the NID or the other fields
             */
            if (data.getNationalId() == null) {
                lastName.setValue(data.getLastName());
                firstName.setValue(data.getFirstName());
                birthDate.setValue(data.getBirthDate());
            } else {
                queryByNId = true;
                nationalId.setValue(data.getNationalId());
            }
            /*
             * if there is a query specified then select the first patient
             * returned by the query by default
             */
            selectFirstPatient = true;
            executeQuery();
        } else {
            showScreen(null);
            /*
             * if there is no query specified then make the the user select the
             * patient
             */
            selectFirstPatient = false;
        }

        setState(QUERY);
    }

    /**
     * executes the query for finding patients based on the fields specified by
     * the user
     */
    @UiHandler("search")
    protected void search(ClickEvent event) {
        executeQuery();
    }

    /**
     * closes the window and notifies the screen that brought up this screen
     * that a patient was selected
     */
    @UiHandler("select")
    protected void select(ClickEvent event) {
        window.close();
        select();
    }

    /**
     * closes the window and notifies the screen that brought up this screen
     * that no patient was selected
     */
    @UiHandler("cancel")
    protected void cancel(ClickEvent event) {
        window.close();
        cancel();
    }

    /**
     * executes the query for finding patients based on the fields specified by
     * the user
     */
    private void executeQuery() {
        Query query;
        ArrayList<QueryData> fields;

        if (validate().getStatus() != VALID) {
            setError(Messages.get().gen_correctErrors());
            return;
        }

        fields = getQueryFields();

        if (fields.size() == 0) {
            /*
             * window will be null if the popup was never shown
             */
            if (window != null)
                setError(Messages.get().gen_emptyQueryException());
            return;
        }

        query = new Query();
        query.setFields(fields);
        query.setRowsPerPage(50);

        if (window != null)
            setBusy(Messages.get().gen_querying());

        if (queryCall == null) {
            queryCall = new AsyncCallbackUI<ArrayList<PatientDO>>() {
                public void success(ArrayList<PatientDO> result) {
                    String error;
                    PatientDO data;

                    /*
                     * load the widgets with the query's results and show the
                     * screen if it isn't showing currently
                     */
                    setQueryResult(result);
                    patientsFound();
                    error = null;
                    if (selectFirstPatient) {
                        /*
                         * if the query was executed only for NID then at most
                         * one patient will be returned; otherwise multiple
                         * patients could be returned; show an error if the NID
                         * has been used for a patient different from the one
                         * used for the query; otherwise select the first row
                         */
                        data = patientTable.getRowAt(0).getData();
                        if (queryByNId) {
                            if (queriedPatient.getId() == null ||
                                queriedPatient.getId().equals(data.getId()))
                                patientTable.selectRowAt(0);
                            else
                                error = Messages.get().patientLookup_nidUsedForOtherPatient();
                        } else {
                            patientTable.selectRowAt(0);
                        }
                        patientSelected();
                    }

                    if (window == null)
                        showScreen(error);
                    else if (error != null)
                        setError(error);
                    else
                        setDone(Messages.get().gen_queryingComplete());
                }

                public void notFound() {
                    if (dontShowIfNoPatient) {
                        /*
                         * don't show the popup if no patient was found
                         */
                        cancel();
                        return;
                    }

                    /*
                     * clear the widgets; show the screen if it isn't showing
                     * currently
                     */
                    setQueryResult(null);
                    if (window == null)
                        showScreen(null);
                    else
                        setDone(Messages.get().gen_noRecordsFound());

                    /*
                     * this allows the user to change the query, because the
                     * previous one didn't return any results
                     */
                    lastName.setFocus(true);
                }

                public void failure(Throwable e) {
                    if (window != null) {
                        setQueryResult(null);
                        setError(Messages.get().gen_queryFailed());
                    }
                    Window.alert("Error: PatientLookup call query failed; " + e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                }
            };
        }

        PatientService.get().query(query, queryCall);
    }

    /**
     * loads the table for patients with the passed list and clears the other
     * tables
     */
    private void setQueryResult(ArrayList<PatientDO> list) {
        ArrayList<Row> model;
        Row row;

        if (list != null) {
            model = new ArrayList<Row>();
            for (PatientDO data : list) {
                row = new Row(12);
                row.setCell(0, data.getId());
                row.setCell(1, data.getLastName());
                row.setCell(2, data.getFirstName());
                row.setCell(3, data.getBirthDate());
                row.setCell(4, data.getGenderId());
                row.setCell(5, data.getNationalId());
                row.setCell(6, data.getAddress().getMultipleUnit());
                row.setCell(7, data.getAddress().getStreetAddress());
                row.setCell(8, data.getAddress().getCity());
                row.setCell(9, data.getAddress().getState());
                row.setCell(10, data.getAddress().getZipCode());
                row.setCell(11, data.getAddress().getHomePhone());
                row.setData(data);
                model.add(row);
            }
        } else {
            model = null;
        }
        patientTable.setModel(model);
        patientSelected();
    }

    /**
     * resets the selected patient based on the newly selected row and loads the
     * other tables with the samples and next of kin linked to the patient
     */
    private void patientSelected() {
        if (patientTable.getSelectedRow() != -1)
            selectedPatient = patientTable.getRowAt(patientTable.getSelectedRow()).getData();
        else
            selectedPatient = null;

        select.setEnabled(selectedPatient != null);
        showSamples();
        showNextOfKin();
    }

    /**
     * loads the samples linked to the selected patient, in the corresponding
     * table
     */
    private void showSamples() {
        if (selectedPatient == null) {
            sampleTable.setModel(null);
            return;
        }

        if (fetchByPatientCall == null) {
            fetchByPatientCall = new AsyncCallbackUI<ArrayList<SampleAnalysisVO>>() {
                public void success(ArrayList<SampleAnalysisVO> result) {
                    Row row;
                    ArrayList<Row> model;

                    model = new ArrayList<Row>();
                    for (SampleAnalysisVO data : result) {
                        row = new Row(4);
                        row.setCell(0, data.getAccessionNumber());
                        row.setCell(1, data.getReceivedDate());
                        row.setCell(2, data.getTestName());
                        row.setCell(3, data.getMethodName());
                        row.setData(data);
                        model.add(row);
                    }
                    sampleTable.setModel(model);
                }

                public void notFound() {
                    sampleTable.setModel(null);
                }

                public void failure(Throwable e) {
                    sampleTable.setModel(null);
                    Window.alert("Error: PatientSampleLookup call query failed; " + e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                }
            };
        }

        AnalysisService.get().fetchByPatientId(selectedPatient.getId(), fetchByPatientCall);
    }

    /**
     * loads the next of kin linked to the selected patient, in the
     * corresponding table
     */
    private void showNextOfKin() {
        if (selectedPatient == null) {
            nextOfKinTable.setModel(null);
            return;
        }

        if (fetchByRelatedPatientCall == null) {
            fetchByRelatedPatientCall = new AsyncCallbackUI<ArrayList<PatientRelationVO>>() {
                public void success(ArrayList<PatientRelationVO> result) {
                    ArrayList<Row> model;
                    Row row;

                    model = new ArrayList<Row>();
                    for (PatientRelationVO data : result) {
                        row = new Row(4);
                        row.setCell(0, data.getLastName());
                        row.setCell(1, data.getFirstName());
                        row.setCell(2, data.getBirthDate());
                        row.setCell(3, data.getRelationId());
                        row.setData(data);
                        model.add(row);
                    }
                    nextOfKinTable.setModel(model);
                }

                public void notFound() {
                    nextOfKinTable.setModel(null);
                }

                public void failure(Throwable e) {
                    nextOfKinTable.setModel(null);
                    Window.alert("Error: PatientRelationLookup call query failed; " +
                                 e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                }
            };
        }

        PatientService.get().fetchByRelatedPatientId(selectedPatient.getId(),
                                                     fetchByRelatedPatientCall);
    }

    /**
     * Shows the screen in a newly created window; sets the focus to a specific
     * widget based on whether or not there are any patients showing in their
     * table
     */
    private void showScreen(final String error) {
        ModalWindow modal;
        ScheduledCommand cmd;

        modal = new ModalWindow();
        modal.setSize("880px", "355px");
        modal.setName(Messages.get().patientLookup_patientLookup());
        modal.setCSS(UIResources.INSTANCE.popupWindow());
        modal.setContent(this);

        /*
         * a ScheduledCommand is used to make sure that the focus to the right
         * widget is set after the screen starts showing
         */
        cmd = new ScheduledCommand() {
            @Override
            public void execute() {
                if (patientTable.getRowCount() == 0)
                    lastName.setFocus(true);
                else if (selectFirstPatient)
                    /*
                     * this is done so that if a patient is selected in the
                     * table then on pressing the tab key, the focus can be set
                     * to the button "Select"
                     */
                    patientTable.setFocus(true);

                if (error != null)
                    setError(error);
            }
        };
        Scheduler.get().scheduleDeferred(cmd);

        this.setWindow(modal);
    }
}