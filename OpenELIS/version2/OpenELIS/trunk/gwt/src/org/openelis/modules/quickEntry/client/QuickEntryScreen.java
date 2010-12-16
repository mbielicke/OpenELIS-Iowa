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
package org.openelis.modules.quickEntry.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.OrganizationContactDO;
import org.openelis.domain.SampleDO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.domain.TestMethodSampleTypeVO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.LocalizedException;
import org.openelis.gwt.common.PermissionException;
import org.openelis.gwt.common.ModulePermission;
import org.openelis.gwt.common.Util;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.BeforeCloseEvent;
import org.openelis.gwt.event.BeforeCloseHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Calendar;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.CalendarLookUp;
import org.openelis.gwt.widget.CheckBox;
import org.openelis.gwt.widget.Confirm;
import org.openelis.gwt.widget.DateField;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.gwt.widget.table.event.RowDeletedEvent;
import org.openelis.gwt.widget.table.event.RowDeletedHandler;
import org.openelis.manager.AnalysisManager;
import org.openelis.manager.SampleDataBundle;
import org.openelis.manager.SampleItemManager;
import org.openelis.manager.SampleManager;
import org.openelis.modules.main.client.openelis.OpenELIS;
import org.openelis.modules.sample.client.AccessionNumberUtility;
import org.openelis.modules.sample.client.TestPrepUtility;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class QuickEntryScreen extends Screen {

    protected DateField                     recDate;
    protected boolean                       useCurrentTime, printLabelsOnTheFly, close;
    protected TestPrepUtility               testLookup;
    protected Confirm                       windowCloseConfirm, receivedDateNotTodayConfirm;
    
    private CalendarLookUp                  receivedDate;
    private TextBox                         entry, tubeNumber;
    private TextBox<Integer>                accessionNumber;
    private CheckBox                        currentDateTime, printLabels;
    private AppButton                       commitButton, removeRowButton;
    private Dropdown<String>                testMethodSampleType, printer;
    private TableWidget                     quickEntryTable;
    private TableDataRow                    rowToBeAdded;

    private Integer                         sampleLoggedInId;
    private Datetime                        todaysDate;
    private AccessionNumberUtility          accNumUtil;
    private ScreenService                   calendarService;
    private ModulePermission                userPermission;
    private HashMap<Integer, SampleManager> managers;

    public QuickEntryScreen() throws Exception {
        super((ScreenDefInt)GWT.create(QuickEntryDef.class));
        service = new ScreenService("controller?service=org.openelis.modules.test.server.TestService");
        calendarService = new ScreenService("controller?service=org.openelis.gwt.server.CalendarService");

        userPermission = OpenELIS.getSystemUserPermission().getModule("quickentry");
        if (userPermission == null)
            throw new PermissionException("screenPermException", "Quick Entry Screen");

        DeferredCommand.addCommand(new Command() {
            public void execute() {
                postConstructor();
            }
        });
    }

    private void postConstructor() {
        initialize();
        initializeDropdowns();
        setState(State.DEFAULT);
        DataChangeEvent.fire(this);

        DeferredCommand.addCommand(new Command() {
            public void execute() {
                setFocus(entry);
            }
        });
    }

    private void initialize() {
        managers = new HashMap<Integer, SampleManager>();
        close = false;
        
        window.addBeforeClosedHandler(new BeforeCloseHandler<ScreenWindow>() {
            public void onBeforeClosed(BeforeCloseEvent<ScreenWindow> event) {
                if(close){
                    close = false;
                }else if(quickEntryTable.numRows() > 0){
                    event.cancel();
                    if (windowCloseConfirm == null) {
                        windowCloseConfirm = new Confirm(Confirm.Type.QUESTION,
                                                            consts.get("onCloseConfirmTitle"),
                                                            consts.get("onCloseConfirmBody"),
                                                            "No", "Yes", "Cancel");
                            windowCloseConfirm.addSelectionHandler(new SelectionHandler<Integer>() {
                            public void onSelection(SelectionEvent<Integer> event) {
                                switch (event.getSelectedItem().intValue()) {
                                    case 0:
                                        close = true;
                                        window.close();
                                        break;
                                    case 1:
                                        commit();
                                        break;
                                }
                            }
                        });
                    }

                    windowCloseConfirm.show();
                }
            }
        });
        
        entry = (TextBox)def.getWidget("entry");
        addScreenHandler(entry, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                entry.setValue(null);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                entryChanged();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                entry.enable(EnumSet.of(State.ADD, State.DEFAULT).contains(event.getState()));
            }
        });

        accessionNumber = (TextBox<Integer>)def.getWidget("accessionNumber");
        addScreenHandler(accessionNumber, new ScreenEventHandler<Integer>() {
            public void onStateChange(StateChangeEvent<State> event) {
                accessionNumber.enable(false);
            }
        });

        tubeNumber = (TextBox)def.getWidget("tubeNumber");
        addScreenHandler(tubeNumber, new ScreenEventHandler<String>() {
            public void onStateChange(StateChangeEvent<State> event) {
                tubeNumber.enable(false);
            }
        });

        receivedDate = (CalendarLookUp)def.getWidget("receivedDate");
        addScreenHandler(receivedDate, new ScreenEventHandler<Datetime>() {
            public void onValueChange(ValueChangeEvent<Datetime> event) {
                if(todaysDate.after(event.getValue())){
                        LocalizedException ex = new LocalizedException("recievedDateNotTodayExceptionBody", event.getValue().toString());
                        receivedDateNotTodayConfirm = new Confirm(Confirm.Type.QUESTION,
                                                            consts.get("recievedDateNotTodayExceptionTitle"),
                                                            ex.getMessage(),
                                                            "No", "Yes");
                        receivedDateNotTodayConfirm.addSelectionHandler(new SelectionHandler<Integer>() {
                            public void onSelection(SelectionEvent<Integer> event) {
                                switch (event.getSelectedItem().intValue()) {
                                    case 0:
                                        receivedDate.setValue(null);
                                        setFocus(entry);
                                        break;
                                    case 1:
                                        setFocus(entry);
                                        break;
                                }
                            }
                        });

                    receivedDateNotTodayConfirm.show();
                    
                    }
            }
            
            public void onStateChange(StateChangeEvent<State> event) {
                receivedDate.enable(EnumSet.of(State.ADD, State.DEFAULT).contains(event.getState()));
            }
        });

        testMethodSampleType = (Dropdown)def.getWidget("testMethodSampleType");
        addScreenHandler(testMethodSampleType, new ScreenEventHandler<String>() {
            public void onStateChange(StateChangeEvent<State> event) {
                testMethodSampleType.enable(EnumSet.of(State.ADD, State.DEFAULT).contains(event.getState()));
            }
        });

        currentDateTime = (CheckBox)def.getWidget("currentDateTime");
        addScreenHandler(currentDateTime, new ScreenEventHandler<String>() {
            public void onValueChange(ValueChangeEvent<String> event) {
                if ("Y".equals(event.getValue()))
                    updateRecievedDate();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                currentDateTime.enable(EnumSet.of(State.ADD, State.DEFAULT).contains(event.getState()));
            }
        });

        printLabels = (CheckBox)def.getWidget("printLabels");
        addScreenHandler(printLabels, new ScreenEventHandler<String>() {
            public void onValueChange(ValueChangeEvent<String> event) {
                // if ("Y".equals(event.getValue()))
                // updateRecievedDate();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                printLabels.enable(EnumSet.of(State.ADD, State.DEFAULT).contains(event.getState()));
            }
        });

        printer = (Dropdown)def.getWidget("printer");
        addScreenHandler(printer, new ScreenEventHandler<String>() {
            public void onStateChange(StateChangeEvent<State> event) {
                printer.enable(EnumSet.of(State.ADD, State.DEFAULT).contains(event.getState()));
            }
        });

        quickEntryTable = (TableWidget)def.getWidget("quickEntryTable");
        addScreenHandler(quickEntryTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                quickEntryTable.load(getTableModel());
            }
            
            public void onStateChange(StateChangeEvent<State> event) {
                quickEntryTable.enable(EnumSet.of(State.ADD).contains(event.getState()));
            }
        });
        
        quickEntryTable.addSelectionHandler(new SelectionHandler<TableRow>() {
            public void onSelection(SelectionEvent<TableRow> event) {
                removeRowButton.enable(true);
            }
        });

        quickEntryTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                event.cancel();
            }
        });

        quickEntryTable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                rowDeleted(event.getIndex(), event.getRow());
            }
        });
        
        removeRowButton = (AppButton)def.getWidget("removeRowButton");
        addScreenHandler(removeRowButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int selected;

                selected = quickEntryTable.getSelectedRow();

                if (selected != -1)
                    quickEntryTable.deleteRow(selected);
                
                removeRowButton.enable(false);
                setFocus(entry);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeRowButton.enable(false);
            }
        });

        commitButton = (AppButton)def.getWidget("commit");
        addScreenHandler(commitButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                commit();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                commitButton.enable(EnumSet.of(State.ADD).contains(event.getState()));
            }
        });

        recDate = new DateField();
        recDate.setBegin(Datetime.YEAR);
        recDate.setEnd(Datetime.MINUTE);
        recDate.setMin(364);
        recDate.setMax(0);
    }

    private void commit() {
        Collection<SampleManager> manList;
        Iterator<SampleManager> itr;
        SampleManager manager;
        ValidationErrorsList errorsList;
        
        setFocus(null);
        window.setBusy(consts.get("adding"));
        
        manList = managers.values();
        itr = manList.iterator();
        errorsList = new ValidationErrorsList();
        manager = null;
        while(itr.hasNext()){
            try{
            manager = itr.next();
            manager.getSample().setStatusId(sampleLoggedInId);
            
            if(manager.getSample().getId() == null)
                manager.add();
            else{
                manager.update();
            }
            
            managers.remove(manager.getSample().getAccessionNumber());
            }catch(ValidationErrorsList e){
                errorsList.add(new FormErrorException("quickCommitError"));
                for(int i=0; i<e.size(); i++)
                    errorsList.add(new FormErrorException("rowError", 
                               manager.getSample().getAccessionNumber().toString(), e.getErrorList().get(i).getLocalizedMessage()));
            }catch(Exception e){
                errorsList.add(new FormErrorException("quickCommitError"));
                errorsList.add(new FormErrorException("rowError", 
                               manager.getSample().getAccessionNumber().toString(), e.getMessage()));
            }
        }
           
        if(errorsList.size() > 0)
            showErrors(errorsList);
        else{
            setState(Screen.State.DEFAULT);
            window.clearStatus();
        }
        
        DataChangeEvent.fire(this);
    }

    private void entryChanged() {
        String val;
        Integer accessionNum;
        SampleDO sampleDO;
        SampleManager sampleMan;

        val = entry.getValue();
        window.clearStatus();

        // date recieved
        recDate.setStringValue(val);
        if (recDate.exceptions == null) {
            recDate.validate();
            if (recDate.exceptions == null){
                if(todaysDate.after(recDate.getValue())){
                    LocalizedException ex = new LocalizedException("recievedDateNotTodayExceptionBody", recDate.getValue().toString());
                    receivedDateNotTodayConfirm = new Confirm(Confirm.Type.QUESTION,
                                                        consts.get("recievedDateNotTodayExceptionTitle"),
                                                        ex.getMessage(),
                                                        "No", "Yes");
                    receivedDateNotTodayConfirm.addSelectionHandler(new SelectionHandler<Integer>() {
                        public void onSelection(SelectionEvent<Integer> event) {
                            switch (event.getSelectedItem().intValue()) {
                                case 0:
                                    //do nothing
                                    break;
                                case 1:
                                    receivedDate.setValue(recDate.getValue());
                                    break;
                            }
                        }
                    });
                
                    receivedDateNotTodayConfirm.show();
                
                }else
                    receivedDate.setValue(recDate.getValue());
                
            } else {
                LocalizedException e = new LocalizedException("invalidEntryException", val);
                window.setError(e.getMessage());
            }

            // test/panel
        } else if (val.matches("[TP][0-9]*\\-[0-9]*")) {
            testMethodSampleType.setValue(val);

            // tube #
        } else if (val.matches("[a-zA-Z]{3}[0-9]{3}")) {
            tubeNumber.setValue(val);

            // new accession #
        } else if (val.matches("NEW")) { // FIXME for now this is my new
                                         // accession string. change this to
                                         // what they want
            if (validateFields()) {
                if (accNumUtil == null)
                    accNumUtil = new AccessionNumberUtility();

                try {
                    accessionNum = accNumUtil.getNewAccessionNumber();
                    accessionNumber.setValue(accessionNum);
                    addAnalysisRow();
                    
                }catch(ValidationErrorsList e){
                    showErrors(e);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }

            // accession #
        } else if (val.matches("[0-9]*")) {
            if (validateFields()) {
                if (accNumUtil == null)
                    accNumUtil = new AccessionNumberUtility();

                try {
                    sampleDO = new SampleDO();
                    sampleDO.setAccessionNumber(Integer.valueOf(val));
                    sampleMan = accNumUtil.accessionNumberEntered(sampleDO);

                    // if this sample has been entered as quick entry before
                    // then add it to the manager hash for reuse, if it hasnt
                    // been already
                    if (sampleMan != null &&
                        managers.get(sampleMan.getSample().getAccessionNumber()) == null)
                        managers.put(sampleMan.getSample().getAccessionNumber(), sampleMan);

                    accessionNumber.setValue(val);
                    addAnalysisRow();
                } catch (NumberFormatException e) {
                    LocalizedException ex = new LocalizedException("invalidEntryException", val);
                    window.setError(ex.getMessage());
                } catch (ValidationErrorsList e) {
                    FieldErrorException fe;
                    ValidationErrorsList newE = new ValidationErrorsList();

                    // convert all the field errors to form errors
                    for (int i = 0; i < e.size(); i++ ) {
                        fe = (FieldErrorException)e.getErrorList().get(i);
                        newE.add(new FormErrorException(fe.getKey()));
                    }

                    showErrors(newE);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }

        } else {
            LocalizedException e = new LocalizedException("invalidEntryException", val);
            window.setError(e.getMessage());
        }
        entry.setValue(null);
        recDate.exceptions = null;
    }

    public void analysisTestChanged(Integer id,
                                    boolean panel,
                                    SampleDataBundle analysisBundle,
                                    SampleManager manager) {
        TestPrepUtility.Type type;

        if (testLookup == null) {
            testLookup = new TestPrepUtility();
            testLookup.setScreen(this);

            testLookup.addActionHandler(new ActionHandler<TestPrepUtility.Action>() {
                public void onAction(ActionEvent<org.openelis.modules.sample.client.TestPrepUtility.Action> event) {
                    testLookupFinished((ArrayList<SampleDataBundle>)event.getData());
                }
            });
        }

        if (panel)
            type = TestPrepUtility.Type.PANEL;
        else
            type = TestPrepUtility.Type.TEST;

        testLookup.setManager(manager);

        try {
            testLookup.lookup(analysisBundle, type, id);

        } catch (Exception e) {
            Window.alert("analysisTestChanged: " + e.getMessage());
        }
    }

    public void testLookupFinished(ArrayList<SampleDataBundle> bundles) {
        int numOfRows;
        window.setBusy();
        TableDataRow newRow;

        numOfRows = 0;
        if (bundles.size() == 0)
            rowToBeAdded = null;
        else {
            numOfRows = quickEntryTable.numRows();
            newRow = null;
            quickEntryTable.fireEvents(false);

            for (int i = 0; i < bundles.size(); i++ ) {
                if (i == 0) {
                    quickEntryTable.addRow(rowToBeAdded);
                    newRow = rowToBeAdded;
                    rowToBeAdded = null;
                    newRow.data = bundles.get(i);
                } else {
                    newRow = new TableDataRow(6);
                    newRow.data = bundles.get(i);
                    quickEntryTable.addRow(newRow);
                }

                updateQuickEntryRowFromBundle(quickEntryTable.numRows() - 1);
            }
            

            quickEntryTable.fireEvents(true);
        }
        
        if(numOfRows == 0 && quickEntryTable.numRows() > 0)
            setState(State.ADD);

        window.clearStatus();
    }

    private void rowDeleted(int index, TableDataRow deletedRow) {
        window.setBusy();
        SampleDataBundle bundle;
        SampleManager man;
        SampleItemManager itemMan;
        AnalysisManager anMan;

        try {
            bundle = (SampleDataBundle)deletedRow.data;
            man = bundle.getSampleManager();
            itemMan = man.getSampleItems();
            anMan = itemMan.getAnalysisAt(bundle.getSampleItemIndex());

            if(man.getSample().getId() != null && !accessionNumExistsInTable(man.getSample().getAccessionNumber())){
                man.abortUpdate();
                managers.remove(man.getSample().getAccessionNumber());
            }else{
                anMan.removeAnalysisAt(bundle.getAnalysisIndex());
                // if its the last analysis remove the manager
                if (anMan.count() == 0)
                    itemMan.removeSampleItemAt(bundle.getSampleItemIndex());
    
                // if its the last item remove the manager from the hash
                if (itemMan.count() == 0)
                    managers.remove(man.getSample().getAccessionNumber());
            }
            
            if(quickEntryTable.numRows() == 0)
                setState(State.DEFAULT);
            
            window.clearStatus();
        } catch (Exception e) {
            Window.alert("rowDeleted: " + e.getMessage());
        }
    }

    private void addAnalysisRow() {
        TableDataRow row;
        TestMethodSampleTypeVO typeDO;
        Integer id;
        SampleManager sampleMan;
        SampleItemManager itemMan;
        AnalysisManager anMan;
        int sampleItemIndex, analysisAddedIndex;
        SampleDataBundle anBundle;
        Integer accessionNum;

        typeDO = (TestMethodSampleTypeVO)testMethodSampleType.getSelection().data;
        accessionNum = Integer.valueOf(accessionNumber.getValue());
        row = new TableDataRow(6);

        try {
            sampleMan = managers.get(accessionNum);

            // if the sample manager is new, create it
            if (sampleMan == null) {
                sampleMan = SampleManager.getInstance();
                managers.put(accessionNum, sampleMan);

                sampleMan.getSample().setDomain(SampleManager.QUICK_ENTRY);
                sampleMan.setDefaults();
                sampleMan.getSample().setReceivedById(OpenELIS.getSystemUserPermission().getSystemUserId());
                sampleMan.getSample().setAccessionNumber(accessionNum);
                sampleMan.getSample().setReceivedDate(receivedDate.getValue());
            }

            itemMan = sampleMan.getSampleItems();
            sampleItemIndex = getSampleItemIndex(typeDO.getSampleTypeId(), itemMan);
            itemMan.getSampleItemAt(sampleItemIndex).setTypeOfSampleId(typeDO.getSampleTypeId());
            itemMan.getSampleItemAt(sampleItemIndex).setTypeOfSample(typeDO.getSampleType());
            anMan = itemMan.getAnalysisAt(sampleItemIndex);

            analysisAddedIndex = anMan.addAnalysis();
            anBundle = anMan.getBundleAt(analysisAddedIndex);
            row.data = anBundle;

            if (typeDO.getTestId() != null)
                id = typeDO.getTestId();
            else
                id = typeDO.getPanelId();

            rowToBeAdded = row;
            analysisTestChanged(id, (typeDO.getTestId() == null), anBundle, sampleMan);

        } catch (Exception e) {
            Window.alert("rowAdded: " + e.getMessage());
        }
    }

    private void updateQuickEntryRowFromBundle(int index) {
        TableDataRow row;
        SampleDataBundle bundle;
        SampleManager man;
        SampleItemManager itemMan;
        SampleItemViewDO itemDO;
        AnalysisViewDO anDO;

        try {
            row = quickEntryTable.getRow(index);
            bundle = (SampleDataBundle)row.data;
            man = bundle.getSampleManager();
            itemMan = man.getSampleItems();
            itemDO = itemMan.getSampleItemAt(bundle.getSampleItemIndex());
            anDO = itemMan.getAnalysisAt(bundle.getSampleItemIndex())
                          .getAnalysisAt(bundle.getAnalysisIndex());

            quickEntryTable.setCell(index, 0, man.getSample().getAccessionNumber());
            quickEntryTable.setCell(index, 1, man.getSample().getReceivedDate());
            quickEntryTable.setCell(index, 2, anDO.getTestName());
            quickEntryTable.setCell(index, 3, anDO.getMethodName());
            quickEntryTable.setCell(index, 4, itemDO.getTypeOfSample());
            // FIXME this code is not there.
            // have no specs to put it in either quickEntryTable.setCell(index,
            // 5, value);

        } catch (Exception e) {
            Window.alert("updateQuickEntryRowFromBundle: " + e.getMessage());
        }
    }

    private int getSampleItemIndex(Integer typeOfSampleId, SampleItemManager man) {
        int index = -1;
        SampleItemViewDO itemDO;

        for (int i = 0; i < man.count(); i++ ) {
            itemDO = man.getSampleItemAt(i);

            if (typeOfSampleId.equals(itemDO.getTypeOfSampleId())) {
                index = i;
                break;
            }
        }

        if (index != -1)
            return index;
        else
            return man.addSampleItem();
    }

    private boolean validateFields() {
        // received date needs filled out
        if (receivedDate.getValue() == null) {
            window.setError(consts.get("receivedDateNoValueException"));
            return false;
        }

        // test needs filled out
        if (testMethodSampleType.getValue() == null) {
            window.setError(consts.get("testMethodNoValueException"));
            return false;
        }

        return true;
    }
    
    private boolean accessionNumExistsInTable(Integer accessionNumber){
        TableDataRow row;
        
        for(int i=0; i<quickEntryTable.numRows(); i++){
            row = quickEntryTable.getRow(i);
            
            if(accessionNumber.equals(row.cells.get(0).value))
                return true;
        }
        
        return false;
    }
    
    private ArrayList<TableDataRow> getTableModel() {
        Collection<SampleManager> manList;
        Iterator<SampleManager> itr;
        SampleManager manager;
        SampleItemManager itemMan;
        AnalysisManager anMan;
        SampleDO sampleDO;
        SampleItemViewDO itemDO;
        AnalysisViewDO anDO;
        TableDataRow row;
        OrganizationContactDO data;
        ArrayList<TableDataRow> model;
        
        model = new ArrayList<TableDataRow>();
        if (managers.size() == 0)
            return model;

        try {
            manList = managers.values();
            itr = manList.iterator();
            while(itr.hasNext()){       //samples
                manager = itr.next();
                sampleDO = manager.getSample();
                
                for(int i=0; i<manager.getSampleItems().count(); i++){ //items
                    itemMan = manager.getSampleItems();
                    anMan = itemMan.getAnalysisAt(i);
                    itemDO = itemMan.getSampleItemAt(i);
                    
                    for(int j=0; j<anMan.count(); j++){   //analyses
                        anDO = anMan.getAnalysisAt(j);
                        if(anDO.getId() < 0){
                            row = new TableDataRow(6);
                            row.cells.get(0).value = sampleDO.getAccessionNumber();
                            row.cells.get(1).value = sampleDO.getReceivedDate();
                            row.cells.get(2).value = anDO.getTestName();
                            row.cells.get(3).value = anDO.getMethodName();
                            row.cells.get(4).value = itemDO.getTypeOfSample();
                            //TODO not here yet row.cells.get(5).value = "";
                            model.add(row);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
        }
        return model;
    }

    private void initializeDropdowns() {
        ArrayList<TestMethodSampleTypeVO> testPanelList;
        ArrayList<TableDataRow> model;
        TableDataRow row;

        try {
            sampleLoggedInId = DictionaryCache.getIdFromSystemName("sample_logged_in");
            todaysDate = Calendar.getCurrentDatetime(Datetime.YEAR, Datetime.DAY);
            
            testPanelList = service.callList("fetchTestMethodSampleTypeList");
            model = new ArrayList<TableDataRow>();
            model.add(new TableDataRow(null, ""));

            for (TestMethodSampleTypeVO typeDO : testPanelList) {
                row = new TableDataRow(1);
                if (typeDO.getPanelId() == null) {
                    row.key = "T" + typeDO.getTestId() + "-" + typeDO.getSampleTypeId();
                    row.cells.get(0).value = typeDO.getTest() + ", " + typeDO.getMethod() + ", " +
                                             typeDO.getSampleType();
                    row.data = typeDO;
                } else {
                    row.key = "P" + typeDO.getPanelId() + "-" + typeDO.getSampleTypeId();
                    row.cells.get(0).value = typeDO.getPanel() + ", " + typeDO.getSampleType();
                    row.data = typeDO;
                }

                model.add(row);
            }

            testMethodSampleType.setModel(model);
        } catch (Exception e) {
            Window.alert("inializeDropdowns: " + e.getMessage());
            window.close();
        }
    }

    private void updateRecievedDate() {
        calendarService.callDatetime("getCurrentDatetime", Datetime.YEAR, Datetime.MINUTE,
                                     new AsyncCallback<Datetime>() {
                                         public void onSuccess(Datetime currentDate) {
                                             if (currentDateTime.getValue() != null &&
                                                 "Y".equals(currentDateTime.getValue())) {
                                                 receivedDate.setValue(currentDate);
                                                 Timer timer = new Timer() {
                                                     public void run() {
                                                         updateRecievedDate();
                                                     }
                                                 };
                                                 timer.schedule(55000);
                                             }
                                         }

                                         public void onFailure(Throwable caught) {
                                             Window.alert(caught.getMessage());
                                         }
                                     });
    }
}
