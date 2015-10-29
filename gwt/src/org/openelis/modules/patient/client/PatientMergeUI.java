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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

import org.openelis.cache.CategoryCache;
import org.openelis.cache.UserCache;
import org.openelis.constants.Messages;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.PatientDO;
import org.openelis.domain.PatientRelationVO;
import org.openelis.domain.SampleAnalysisVO;
import org.openelis.meta.PatientMeta;
import org.openelis.modules.analysis.client.AnalysisService;
import org.openelis.modules.sample1.client.SampleService1;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.ModulePermission;
import org.openelis.ui.common.PermissionException;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.event.BeforeCloseEvent;
import org.openelis.ui.event.BeforeCloseHandler;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.AsyncCallbackUI;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.TextBox;
import org.openelis.ui.widget.WindowInt;
import org.openelis.ui.widget.calendar.Calendar;
import org.openelis.ui.widget.table.Row;
import org.openelis.ui.widget.table.Table;
import org.openelis.ui.widget.table.event.BeforeCellEditedEvent;
import org.openelis.ui.widget.table.event.BeforeCellEditedHandler;
import org.openelis.ui.widget.table.event.CellEditedEvent;
import org.openelis.ui.widget.table.event.CellEditedHandler;

public class PatientMergeUI extends Screen {

    @UiTemplate("PatientMerge.ui.xml")
    interface PatientMergeUiBinder extends UiBinder<Widget, PatientMergeUI> {
    };

    private static PatientMergeUiBinder                     uiBinder = GWT.create(PatientMergeUiBinder.class);

    @UiField
    protected Button                                        search, merge;

    @UiField
    protected Calendar                                      birthDate;

    @UiField
    protected Dropdown<Integer>                             gender, patientRelation,
                                                            patientState;

    @UiField
    protected Table                                         patientTable, sampleTable,
                                                            nextOfKinTable;

    @UiField
    protected TextBox<Integer>                              patientId;

    @UiField
    protected TextBox<String>                               lastName, firstName, nationalId;

    protected ArrayList<PatientDO>                          fromPatients;
    protected AsyncCallbackUI<Void>                         mergeCall;
    protected AsyncCallbackUI<ArrayList<PatientDO>>         queryCall;
    protected AsyncCallbackUI<ArrayList<PatientRelationVO>> fetchByRelatedPatientCall;
    protected AsyncCallbackUI<ArrayList<SampleAnalysisVO>>  fetchByPatientCall;
    protected ModulePermission                              userPermission;
    protected PatientDO                                     selectedPatient, toPatient;

    public PatientMergeUI(WindowInt window) throws Exception {
        setWindow(window);
        
        userPermission = UserCache.getPermission().getModule("patientmerge");
        if (userPermission == null)
            throw new PermissionException(Messages.get().gen_screenPermException("Patient Merge Screen"));
        
        initWidget(uiBinder.createAndBindUi(this));
        initialize();
        setState(QUERY);
        fireDataChange();

        logger.fine("PatientMergeUI Opened");
    }

    /**
     * Setup state and data change handles for every widget on the screen
     */
    public void initialize() {
        ArrayList<DictionaryDO> list;
        ArrayList<Item<Integer>> model;

        //
        // screen fields and buttons
        //
        addScreenHandler(patientId, PatientMeta.getId(), new ScreenHandler<Integer>() {
            public void onStateChange(StateChangeEvent event) {
                patientId.setEnabled(isState(QUERY));
                patientId.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? lastName : merge;
            }
        });

        addScreenHandler(lastName, PatientMeta.getLastName(), new ScreenHandler<String>() {
            public void onStateChange(StateChangeEvent event) {
                lastName.setEnabled(isState(QUERY));
                lastName.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? firstName : patientId;
            }
        });

        addScreenHandler(firstName, PatientMeta.getFirstName(), new ScreenHandler<String>() {
            public void onStateChange(StateChangeEvent event) {
                firstName.setEnabled(isState(QUERY));
                firstName.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? birthDate : lastName;
            }
        });

        addScreenHandler(birthDate, PatientMeta.getBirthDate(), new ScreenHandler<Datetime>() {
            public void onStateChange(StateChangeEvent event) {
                birthDate.setEnabled(isState(QUERY));
                birthDate.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? nationalId : firstName;
            }
        });

        addScreenHandler(nationalId, PatientMeta.getNationalId(), new ScreenHandler<String>() {
            public void onStateChange(StateChangeEvent event) {
                nationalId.setEnabled(isState(QUERY));
                nationalId.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? search : birthDate;
            }
        });

        addScreenHandler(search, "search", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                search.setEnabled(true);
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
                return forward ? merge : search;
            }
        });

        patientTable.addSelectionHandler(new SelectionHandler<Integer>() {
            public void onSelection(SelectionEvent<Integer> event) {
                patientSelected();
            }
        });

        patientTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                PatientDO pDO;
                
                if (event.getCol() != 0 && event.getCol() != 1) {
                    event.cancel();
                } else {
                    pDO = patientTable.getRowAt(event.getRow()).getData();
                    switch (event.getCol()) {
                        case 0:
                            if (toPatient != null && toPatient.getId().equals(pDO.getId())) {
                                Window.alert(Messages.get().patientMerge_duplicateFromTo());
                                event.cancel();
                            }
                            break;
                            
                        case 1:
                            if (fromPatients.size() > 0) {
                                for (PatientDO data : fromPatients) {
                                    if (pDO.getId().equals(data.getId())) {
                                        Window.alert(Messages.get().patientMerge_duplicateFromTo());
                                        event.cancel();
                                        break;
                                    }
                                }
                            }
                            break;
                    }
                }
            }
        });

        patientTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int i, r, c;
                Object val;
                PatientDO pDO;
                Row row;

                r = event.getRow();
                c = event.getCol();
                row = patientTable.getRowAt(r);
                val = patientTable.getValueAt(r,c);
                pDO = row.getData();
                
                switch (c) {
                    case 0:
                        if (fromPatients == null)
                            fromPatients = new ArrayList<PatientDO>();
                        if ("Y".equals(val))
                            fromPatients.add(pDO);
                        else if ("N".equals(val))
                            fromPatients.remove(pDO);
                        break;
                    
                    case 1:
                        if ("Y".equals(val)) {
                            if (toPatient != null) {
                                for (i = 0; i < patientTable.getRowCount(); i++) {
                                    if (i != event.getRow())
                                        patientTable.setValueAt(i, 1, "N");
                                }
                            }
                            toPatient = pDO;
                        } else {
                            toPatient = null;
                        }
                        break;
                }
                
                if (fromPatients != null && fromPatients.size() > 0 && toPatient != null)
                    merge.setEnabled(true);
                else
                    merge.setEnabled(false);
            }
        });

        //
        // next of kin table
        //
        addScreenHandler(nextOfKinTable, "nextOfKinTable", new ScreenHandler<ArrayList<Row>>() {
            public void onStateChange(StateChangeEvent event) {
                nextOfKinTable.setEnabled(true);
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

        addScreenHandler(merge, "merge", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                merge.setEnabled(false);
            }

            public Widget onTab(boolean forward) {
                return forward ? patientId : patientTable;
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
        model = new ArrayList<Item<Integer>>();
        for (DictionaryDO resultDO : list)
            model.add(new Item<Integer>(resultDO.getId(), resultDO.getEntry()));
        patientState.setModel(model);
    }

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

    @SuppressWarnings("unused")
    @UiHandler("search")
    protected void search(ClickEvent event) {
        executeQuery();
    }

    @SuppressWarnings("unused")
    @UiHandler("merge")
    protected void merge(ClickEvent event) {
        setBusy(Messages.get().gen_merging());
        
        if (mergeCall == null) {
            mergeCall = new AsyncCallbackUI<Void>() {
                public void success(Void result) {
                    executeQuery();
                    setDone(Messages.get().gen_mergingComplete());
                }
                
                public void failure(Throwable e) {
                    Window.alert("Error: PatientMerge call merge failed; " + e.getMessage());
                    setError(Messages.get().gen_mergingFailed());
                }
            };
        }
        
        SampleService1.get().mergePatients(fromPatients, toPatient, mergeCall);
    }

    /**
     * executes the query for finding patients based on the fields specified by
     * the user
     */
    private void executeQuery() {
        Query query;

        if (validate().getStatus() != VALID) {
            setError(Messages.get().gen_correctErrors());
            return;
        }

        query = new Query();
        query.setFields(getQueryFields());
        
        if (query.getFields().size() > 0) {
            setBusy(Messages.get().gen_querying());
            
            if (queryCall == null) {
                queryCall = new AsyncCallbackUI<ArrayList<PatientDO>>() {
                    public void success(ArrayList<PatientDO> result) {
                        setQueryResult(result);
                        setDone(Messages.get().gen_queryingComplete());
                    }
    
                    public void notFound() {
                        setQueryResult(null);
                        setDone(Messages.get().gen_noRecordsFound());
                    }
    
                    public void failure(Throwable e) {
                        setQueryResult(null);
                        Window.alert("Error: PatientLookup call query failed; " + e.getMessage());
                        setError(Messages.get().gen_queryFailed());
                    }
                };
            }

            query.setRowsPerPage(50);
            PatientService.get().query(query, queryCall);
        } else {
            setDone(Messages.get().gen_emptyQueryException());
        }
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
                row = new Row(14);
                row.setCell(0, "N");
                row.setCell(1, "N");
                row.setCell(2, data.getId());
                row.setCell(3, data.getLastName());
                row.setCell(4, data.getFirstName());
                row.setCell(5, data.getBirthDate());
                row.setCell(6, data.getGenderId());
                row.setCell(7, data.getNationalId());
                row.setCell(8, data.getAddress().getMultipleUnit());
                row.setCell(9, data.getAddress().getStreetAddress());
                row.setCell(10, data.getAddress().getCity());
                row.setCell(11, data.getAddress().getState());
                row.setCell(12, data.getAddress().getZipCode());
                row.setCell(13, data.getAddress().getHomePhone());
                row.setData(data);
                model.add(row);
            }
        } else {
            model = null;
        }
        patientTable.setModel(model);
        merge.setEnabled(false);
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
}