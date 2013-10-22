/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.modules.patient.client;

import static org.openelis.modules.main.client.Logger.logger;
import static org.openelis.ui.screen.State.QUERY;
import static org.openelis.ui.screen.Screen.Validation.Status.VALID;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

import org.openelis.cache.CategoryCache;
import org.openelis.constants.Messages;
import org.openelis.domain.AnalysisViewVO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.PatientDO;
import org.openelis.domain.PatientRelationVO;
import org.openelis.meta.PatientMeta;
import org.openelis.modules.analysis.client.AnalysisService;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.event.BeforeCloseEvent;
import org.openelis.ui.event.BeforeCloseHandler;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
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
import org.openelis.ui.widget.table.event.UnselectionEvent;
import org.openelis.ui.widget.table.event.UnselectionHandler;

public abstract class PatientLookupScreenUI extends Screen {
    
    @UiTemplate("PatientLookup.ui.xml")
    interface PatientLookupUiBinder extends UiBinder<Widget, PatientLookupScreenUI> {
    };
    
    private static PatientLookupUiBinder uiBinder = GWT.create(PatientLookupUiBinder.class);

    @UiField
    protected Button                     search, select, cancel;
    @UiField
    protected Calendar                   birthDate;
    @UiField
    Dropdown<Integer>                    tableGender, tableRelation;
    @UiField
    protected Table                      patientTable, sampleTable, nextOfKinTable;
    @UiField
    protected TextBox<String>            lastName, firstName, nationalId;

    protected PatientDO                  selectedPatient;
    protected PatientRelationVO          selectedNextOfKin;

    public PatientLookupScreenUI() {
        initWidget(uiBinder.createAndBindUi(this));
    }
    
    /**
     * Setup state and data change handles for every widget on the screen
     */
    public void initialize() throws Exception {
        ArrayList<DictionaryDO> dictList;
        ArrayList<Item<Integer>> model;

        //
        // screen fields and buttons
        //
        addScreenHandler(lastName, PatientMeta.getLastName(), new ScreenHandler<String>() {
            public void onStateChange(StateChangeEvent event) {
                lastName.setEnabled(true);
                lastName.setQueryMode(true);
            }

            public Widget onTab(boolean forward) {
                return forward ? firstName : cancel;
            }
        });

        addScreenHandler(firstName, PatientMeta.getFirstName(), new ScreenHandler<String>() {
            public void onStateChange(StateChangeEvent event) {
                firstName.setEnabled(true);
                firstName.setQueryMode(true);
            }

            public Widget onTab(boolean forward) {
                return forward ? birthDate : lastName;
            }
        });

        addScreenHandler(birthDate, PatientMeta.getBirthDate(), new ScreenHandler<Datetime>() {
            public void onStateChange(StateChangeEvent event) {
                birthDate.setEnabled(true);
                birthDate.setQueryMode(true);
            }

            public Widget onTab(boolean forward) {
                return forward ? nationalId : firstName;
            }
        });

        addScreenHandler(nationalId, PatientMeta.getNationalId(), new ScreenHandler<String>() {
            public void onStateChange(StateChangeEvent event) {
                nationalId.setEnabled(true);
                nationalId.setQueryMode(true);
            }

            public Widget onTab(boolean forward) {
                return forward ? search : birthDate;
            }
        });

        addScreenHandler(search, "search", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                search.setEnabled(isState(QUERY));
            }
            
            public Widget onTab(boolean forward) {
                return forward ? select : nationalId;
            }
        });

        //
        // patient table
        //
        addScreenHandler(patientTable, "patientTable", new ScreenHandler<ArrayList<Row>>() {
            public void onStateChange(StateChangeEvent event) {
                patientTable.setEnabled(true);
                patientTable.setAllowMultipleSelection(false);
            }
            
            public Widget onTab(boolean forward) {
                return forward ? select : search;
            }
        });

        patientTable.addSelectionHandler(new SelectionHandler<Integer>() {
            public void onSelection(SelectionEvent<Integer> event) {
                if (patientTable.getSelectedRow() != -1) {
                    select.setEnabled(true);
                    showSamples();
                    showNextOfKin();
                }
            }
        });
        
        patientTable.addUnselectionHandler(new UnselectionHandler<Integer>() {
            public void onUnselection(UnselectionEvent<Integer> event) {
                if (patientTable.getSelectedRow() == -1) {
                    select.setEnabled(false);
                    showSamples();
                    showNextOfKin();
                }
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
                nextOfKinTable.setEnabled(true);
                nextOfKinTable.setAllowMultipleSelection(false);
            }
        });

        nextOfKinTable.addSelectionHandler(new SelectionHandler<Integer>() {
            public void onSelection(SelectionEvent<Integer> event) {
                if (nextOfKinTable.getSelectedRow() != -1) {
//                    select.setEnabled(true);
                }
            }
        });
        
        nextOfKinTable.addUnselectionHandler(new UnselectionHandler<Integer>() {
            public void onUnselection(UnselectionEvent<Integer> event) {
                if (nextOfKinTable.getSelectedRow() == -1) {
//                    select.setEnabled(false);
                }
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
                sampleTable.setAllowMultipleSelection(false);
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
                select.setEnabled(false);
            }
            
            public Widget onTab(boolean forward) {
                return forward ? cancel : search;
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

        try {
            CategoryCache.getBySystemNames("gender", "patient_relation");
        } catch (Exception e) {
            throw new Exception("PatientLookupScreenUI: missing dictionary entry; " + e.getMessage());
        }

        //
        // load gender dropdown model
        //
        dictList  = CategoryCache.getBySystemName("gender");
        model = new ArrayList<Item<Integer>>();
        for (DictionaryDO resultDO : dictList)
            model.add(new Item<Integer>(resultDO.getId(),resultDO.getEntry()));
        tableGender.setModel(model);

        //
        // load patient relation dropdown model
        //
        dictList  = CategoryCache.getBySystemName("patient_relation");
        model = new ArrayList<Item<Integer>>();
        for (DictionaryDO resultDO : dictList)
            model.add(new Item<Integer>(resultDO.getId(),resultDO.getEntry()));
        tableRelation.setModel(model);

        setState(QUERY);
        fireDataChange();
        lastName.setFocus(true);

        logger.fine("PatientLookupScreenUI Opened");
    }
    
    /**
     * overridden to respond to the user clicking "ok"
     */
    public abstract void select();

    /**
     * overridden to respond to the user clicking "cancel"
     */
    public abstract void cancel();

    @SuppressWarnings("unused")
    @UiHandler("search")
    protected void executeQuery(ClickEvent event) {
        executeQuery();
    }
    
    private void executeQuery() {
        Query query;

        if (validate().getStatus() != VALID) {
            window.setError(Messages.get().correctErrors());
            return;
        }

        query = new Query();
        query.setFields(getQueryFields());

        if (query.getFields().size() > 0) {
            window.setBusy(Messages.get().querying());
    
            query.setRowsPerPage(50);
            PatientService.get().query(query, new AsyncCallback<ArrayList<PatientDO>>() {
                public void onSuccess(ArrayList<PatientDO> list) {
                    setQueryResult(list);
                    patientTable.selectRowAt(0);
                }
    
                public void onFailure(Throwable error) {
                    setQueryResult(null);
                    if (error instanceof NotFoundException) {
                        window.setDone(Messages.get().noRecordsFound());
                    } else {
                        Window.alert("Error: PatientLookup call query failed; "+error.getMessage());
                        window.setError(Messages.get().queryFailed());
                    }
                }
            });
        } else {
            window.setDone(Messages.get().emptyQueryException());
        }
    }

    private void setQueryResult(ArrayList<PatientDO> list) {
        ArrayList<Row> model;
        Row row;
        
        model = new ArrayList<Row>();
        if (list == null || list.size() == 0) {
            window.setDone(Messages.get().noRecordsFound());
        } else {
            for (PatientDO patientRow : list) {
                row = new Row(6);
                row.setCell(0, patientRow.getId());
                row.setCell(1, patientRow.getLastName());
                row.setCell(2, patientRow.getFirstName());
                row.setCell(3, patientRow.getBirthDate());
                row.setCell(4, patientRow.getGenderId());
                row.setCell(5, patientRow.getNationalId());
                row.setData(patientRow);
                model.add(row);
            }

            window.setDone(Messages.get().queryingComplete());
        }

        patientTable.setModel(model);
        showNextOfKin();
        showSamples();
    }
    
    private void showNextOfKin() {
        PatientDO patDO;
        
        if (patientTable.getSelectedRow() != -1) {
            patDO = patientTable.getRowAt(patientTable.getSelectedRow()).getData();
            PatientService.get().fetchByRelatedPatientId(patDO.getId(), new AsyncCallback<ArrayList<PatientRelationVO>>() {
                ArrayList<Row> model;
                Row row;

                public void onSuccess(ArrayList<PatientRelationVO> list) {
                    model = new ArrayList<Row>();
                    for (PatientRelationVO data : list) {
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
    
                public void onFailure(Throwable error) {
                    nextOfKinTable.setModel(null);
                    if (error instanceof NotFoundException) {
                        window.setDone(Messages.get().noRecordsFound());
                    } else {
                        Window.alert("Error: PatientRelationLookup call query failed; "+error.getMessage());
                        window.setError(Messages.get().queryFailed());
                    }
                }
            });
        } else {
            nextOfKinTable.setModel(null);
        }
    }
    
    private void showSamples() {
        PatientDO patDO;
        
        if (patientTable.getSelectedRow() != -1) {
            patDO = patientTable.getRowAt(patientTable.getSelectedRow()).getData();
            AnalysisService.get().fetchByPatientId(patDO.getId(), new AsyncCallback<ArrayList<AnalysisViewVO>>() {
                ArrayList<Row> model;
                Row row;

                public void onSuccess(ArrayList<AnalysisViewVO> list) {
                    model = new ArrayList<Row>();
                    for (AnalysisViewVO data : list) {
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
    
                public void onFailure(Throwable error) {
                    sampleTable.setModel(null);
                    if (error instanceof NotFoundException) {
                        window.setDone(Messages.get().noRecordsFound());
                    } else {
                        Window.alert("Error: PatientSampleLookup call query failed; "+error.getMessage());
                        window.setError(Messages.get().queryFailed());
                    }
                }
            });
        } else {
            sampleTable.setModel(null);
        }
    }
    
    @SuppressWarnings("unused")
    @UiHandler("select")
    protected void select(ClickEvent event) {
        Row selectedRow;
        
        selectedRow = null;
        selectedPatient = null;
        selectedNextOfKin = null;
        if (patientTable.getSelectedRow() != -1) {
            selectedRow = patientTable.getRowAt(patientTable.getSelectedRow());
            selectedPatient = selectedRow.getData();
        }
        if (nextOfKinTable.getSelectedRow() != -1) {
            selectedRow = nextOfKinTable.getRowAt(nextOfKinTable.getSelectedRow());
            selectedNextOfKin = selectedRow.getData();
        }
        window.close();
        select();
    }
    
    @SuppressWarnings("unused")
    @UiHandler("cancel")
    protected void cancel(ClickEvent event) {
        window.close();
        selectedPatient = null;
        selectedNextOfKin = null;
        cancel();
    }
    
    public void search(PatientDO patient) {
        lastName.setValue(patient.getLastName());
        firstName.setValue(patient.getFirstName());
        birthDate.setValue(patient.getBirthDate());
        nationalId.setValue(patient.getNationalId());
        
        executeQuery();
        if (patientTable.getRowCount() > 0) {
            patientTable.setFocus(true);
        } else {
            lastName.setFocus(true);
        }
    }
    
    public PatientDO getSelectedPatient() {
        return selectedPatient;
    }
    
    public PatientRelationVO getSelectedNextOfKin() {
        return selectedNextOfKin;
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
}