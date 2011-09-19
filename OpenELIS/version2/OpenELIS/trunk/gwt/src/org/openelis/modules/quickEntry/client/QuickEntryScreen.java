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
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;

import org.openelis.cache.DictionaryCache;
import org.openelis.cache.UserCache;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.IdVO;
import org.openelis.domain.OrganizationContactDO;
import org.openelis.domain.SampleDO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.domain.TestMethodSampleTypeVO;
import org.openelis.domain.TestSectionViewDO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.LocalizedException;
import org.openelis.gwt.common.PermissionException;
import org.openelis.gwt.common.ModulePermission;
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
import org.openelis.manager.TestManager;
import org.openelis.manager.TestSectionManager;
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

    protected DateField            recDate;
    protected boolean              useCurrentTime, printLabelsOnTheFly, close;
    protected TestPrepUtility      testLookup;
    protected Confirm              windowCloseConfirm, receivedDateNotTodayConfirm;

    private CalendarLookUp         receivedDate;
    private TextBox                entry, tubeNumber;
    private TextBox<Integer>       accessionNumber;
    private CheckBox               currentDateTime, printLabels;
    private AppButton              commitButton, removeRowButton;
    private Dropdown<String>       testMethodSampleType, testSection, printer;
    private TableWidget            quickEntryTable;
    private TableDataRow           rowToBeAdded;

    private Integer                sampleNotVerifiedId, testSectionDefaultId;
    private Datetime               todaysDate;
    private AccessionNumberUtility accNumUtil;
    private ScreenService          calendarService, panelService;
    private ModulePermission       userPermission;
    private HashMap<Integer, Item> managers;

    public QuickEntryScreen() throws Exception {
        super((ScreenDefInt)GWT.create(QuickEntryDef.class));
        service = new ScreenService("controller?service=org.openelis.modules.test.server.TestService");
        panelService = new ScreenService("controller?service=org.openelis.modules.panel.server.PanelService");
        calendarService = new ScreenService("controller?service=org.openelis.gwt.server.CalendarService");

        userPermission = UserCache.getPermission().getModule("quickentry");
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
        managers = new HashMap<Integer, Item>();
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
                if (todaysDate.after(event.getValue())) {
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
            public void onValueChange(ValueChangeEvent<String> event) {
                int                     i, j;
                Integer                 defaultSectionId;
                ArrayList<IdVO>         testIds;
                ArrayList<TableDataRow> model;
                HashMap<Integer,TestSectionViewDO> panelSections;
                IdVO                    testVO;
                TestManager             tm;
                TestMethodSampleTypeVO  typeDO;
                TestSectionViewDO       tsVDO;
                TestSectionManager      tsm;
                
                defaultSectionId = null;
                model = new ArrayList<TableDataRow>();
                typeDO = (TestMethodSampleTypeVO)testMethodSampleType.getSelection().data;
                if (typeDO.getTestId() != null) {
                    try {
                        tm = TestManager.fetchById(typeDO.getTestId());
                        tsm = tm.getTestSections();
                        for (i = 0; i < tsm.count(); i++) {
                            tsVDO = tsm.getSectionAt(i);
                            if (testSectionDefaultId.equals(tsVDO.getFlagId()))
                                defaultSectionId = tsVDO.getSectionId();
                            model.add(new TableDataRow(tsVDO.getSectionId(), tsVDO.getSection()));
                            model.get(model.size()-1).data = tsVDO;
                        }
                        testSection.setModel(model);
                        testSection.setSelection(defaultSectionId);
                    } catch (Exception anyE) {
                        Window.alert(consts.get("testSectionLoadError"));
                        anyE.printStackTrace();
                    }
                } else {
                    panelSections = new HashMap<Integer,TestSectionViewDO>();
                    try {
                        testIds = panelService.callList("fetchTestIdsByPanelId", typeDO.getPanelId());
                        for (i = 0; i < testIds.size(); i++) {
                            testVO = testIds.get(i);
                            tm = TestManager.fetchById(testVO.getId());
                            tsm = tm.getTestSections();
                            for (j = 0; j < tsm.count(); j++) {
                                tsVDO = tsm.getSectionAt(j);
                                if (!panelSections.containsKey(tsVDO.getSectionId())) {
                                    panelSections.put(tsVDO.getSectionId(), tsVDO);
                                    if (testSectionDefaultId.equals(tsVDO.getFlagId()))
                                        defaultSectionId = tsVDO.getSectionId();
                                    model.add(new TableDataRow(tsVDO.getSectionId(), tsVDO.getSection()));
                                    model.get(model.size()-1).data = tsVDO;
                                }
                            }
                        }
                        testSection.setModel(model);
                        testSection.setSelection(defaultSectionId);
                    } catch (Exception anyE) {
                        Window.alert(consts.get("panelSectionLoadError"));
                        anyE.printStackTrace();
                    }
                }
            }
            
            public void onStateChange(StateChangeEvent<State> event) {
                testMethodSampleType.enable(EnumSet.of(State.ADD, State.DEFAULT).contains(event.getState()));
            }
        });

        testSection = (Dropdown)def.getWidget("testSection");
        addScreenHandler(testSection, new ScreenEventHandler<String>() {
            public void onStateChange(StateChangeEvent<State> event) {
                testSection.enable(EnumSet.of(State.ADD, State.DEFAULT).contains(event.getState()));
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
                rowDeleted(event.getRow());
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
        int                  i;
        ArrayList<Integer>   removables;
        Item                 item;
        Iterator<Item>       itr;
        SampleManager        manager;
        ValidationErrorsList errorsList;
        
        setFocus(null);
        window.setBusy(consts.get("adding"));
        
        itr = managers.values().iterator();
        errorsList = new ValidationErrorsList();
        manager = null;
        removables = new ArrayList<Integer>();
        while (itr.hasNext()) {
            try {
                item = itr.next();
                manager = item.sampleManager;
                manager.getSample().setReceivedById(UserCache.getPermission().getSystemUserId());
                manager.getSample().setStatusId(sampleNotVerifiedId);
                
                if (manager.getSample().getId() == null)
                    manager.add();
                else
                    manager.update();

                removables.add(manager.getSample().getAccessionNumber());
                item.count = 0;
            } catch (ValidationErrorsList e) {
                errorsList.add(new FormErrorException("quickCommitError"));
                for (i = 0; i < e.size(); i++)
                    errorsList.add(new FormErrorException("rowError", 
                                   manager.getSample().getAccessionNumber().toString(), e.getErrorList().get(i).getLocalizedMessage()));
            } catch (Exception e) {
                errorsList.add(new FormErrorException("quickCommitError"));
                errorsList.add(new FormErrorException("rowError", 
                               manager.getSample().getAccessionNumber().toString(), e.getMessage()));
            }
        }
        
        for (i = 0; i < removables.size(); i++)
            managers.remove(removables.get(i));
        
        if (errorsList.size() > 0) {
            showErrors(errorsList);
        } else {
            setState(Screen.State.DEFAULT);
            window.setDone(consts.get("addingComplete"));
        }
        
        DataChangeEvent.fire(this);
    }

    private void entryChanged() {
        int           index;
        String        val;
        Integer       accessionNum;
        SampleDO      sampleDO;
        SampleManager sampleMan;
        LocalizedException ex;

        val = entry.getValue();
        window.clearStatus();

        // date recieved
        recDate.setStringValue(val);
        recDate.validate();
        if (recDate.exceptions == null) {
            if (todaysDate.after(recDate.getValue())) {
                ex = new LocalizedException("recievedDateNotTodayExceptionBody", recDate.getValue().toString());
                receivedDateNotTodayConfirm = new Confirm(Confirm.Type.QUESTION,
                                                    consts.get("recievedDateNotTodayExceptionTitle"),
                                                    ex.getMessage(),
                                                    "No", "Yes");
                receivedDateNotTodayConfirm.addSelectionHandler(new SelectionHandler<Integer>() {
                    public void onSelection(SelectionEvent<Integer> event) {
                        switch (event.getSelectedItem().intValue()) {
                            case 0:
                                // do nothing
                                break;
                            case 1:
                                receivedDate.setValue(recDate.getValue());
                                break;
                        }
                    }
                });
                receivedDateNotTodayConfirm.show();
            } else {
                receivedDate.setValue(recDate.getValue());
            }
        } else if (val.matches("[TP][0-9]*\\-[0-9]*")) {    // test & panel
            try {
                testMethodSampleType.setValue(val, true);
            } catch (Exception e) {
                ex = new LocalizedException("invalidEntryException", val);
                window.setError(ex.getMessage());
            }
        } else if (val.matches("[a-zA-Z]{3}[0-9]{3}")) {    // tube #
            tubeNumber.setValue(val);
        } else if (val.matches("NEW")) {                    // new accession #
            if (validateFields()) {
                if (accNumUtil == null)
                    accNumUtil = new AccessionNumberUtility();

                try {
                    accessionNum = accNumUtil.getNewAccessionNumber();
                    accessionNumber.setValue(accessionNum);
                    addAnalysisRow();
                } catch (ValidationErrorsList e) {
                    showErrors(e);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        } else if (val.matches("[0-9]+") || val.matches("[0-9]+-[0-9]+")) { // accession # 
            if (validateFields()) {
                if (accNumUtil == null)
                    accNumUtil = new AccessionNumberUtility();
                //
                // Trim the Sample Item ID from the end of the bar coded
                // accession number
                //
                index = val.indexOf("-");
                if (index != -1)
                    val = val.substring(0, index);

                try {
                    sampleDO = new SampleDO();
                    sampleDO.setAccessionNumber(Integer.valueOf(val));
                    sampleMan = accNumUtil.accessionNumberEntered(sampleDO);

                    // if this sample has been entered as quick entry before
                    // then add it to the manager hash for reuse, if it hasnt
                    // been already
                    if (sampleMan != null &&
                        managers.get(sampleMan.getSample().getAccessionNumber()) == null)
                        managers.put(sampleMan.getSample().getAccessionNumber(),
                                     new Item(sampleMan, 0));

                    accessionNumber.setValue(val);
                    addAnalysisRow();
                } catch (NumberFormatException e) {
                    ex = new LocalizedException("invalidEntryException", val);
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
            ex = new LocalizedException("invalidEntryException", val);
            window.setError(ex.getMessage());
        }
        entry.setValue(null);
        recDate.exceptions = null;
    }

    public void analysisTestChanged(Integer id,
                                    boolean panel,
                                    TestSectionViewDO tsVDO,
                                    SampleDataBundle analysisBundle,
                                    SampleManager manager) {
        TestPrepUtility.Type type;

        if (testLookup == null) {
            testLookup = new TestPrepUtility();
            testLookup.setScreen(this);

            testLookup.addActionHandler(new ActionHandler<TestPrepUtility.Action>() {
                @SuppressWarnings("unchecked")
                public void onAction(ActionEvent<org.openelis.modules.sample.client.TestPrepUtility.Action> event) {
                    testLookupFinished((ArrayList<SampleDataBundle>)event.getData());
                }
            });
        }

        if (panel)
            type = TestPrepUtility.Type.PANEL;
        else
            type = TestPrepUtility.Type.TEST;

        try {
            testLookup.lookup(analysisBundle, type, id, tsVDO);
        } catch (Exception e) {
            Window.alert("analysisTestChanged: " + e.getMessage());
        }
    }

    public void testLookupFinished(ArrayList<SampleDataBundle> bundles) {
        int numOfRows;
        Item item;
        TableDataRow newRow;
        SampleDataBundle bundle;
        SampleManager sManager;

        window.setBusy();

        numOfRows = 0;
        if (bundles.size() == 0)
            rowToBeAdded = null;
        else {
            numOfRows = quickEntryTable.numRows();
            newRow = null;
            quickEntryTable.fireEvents(false);

            for (int i = 0; i < bundles.size(); i++ ) {
                bundle = bundles.get(i);
                if (i == 0) {
                    quickEntryTable.addRow(rowToBeAdded);
                    newRow = rowToBeAdded;
                    rowToBeAdded = null;
                    newRow.data = bundle;
                } else {
                    newRow = new TableDataRow(6);
                    newRow.data = bundle;
                    quickEntryTable.addRow(newRow);
                }

                sManager = bundle.getSampleManager();
                item = managers.get(sManager.getSample().getAccessionNumber());
                if (item == null)
                    managers.put(sManager.getSample().getAccessionNumber(), new Item(sManager, 1));
                else
                    item.count++;

                updateQuickEntryRowFromBundle(quickEntryTable.numRows() - 1);
            }

            quickEntryTable.fireEvents(true);
        }

        if (quickEntryTable.numRows() > 0) {
            if (numOfRows == 0)
                setState(State.ADD);
            quickEntryTable.selectRow(quickEntryTable.numRows() - 1);
            quickEntryTable.scrollToVisisble();
        }

        window.clearStatus();
   }

    private void rowDeleted(TableDataRow deletedRow) {
        Item              item;
        AnalysisManager   anMan;
        SampleDataBundle  bundle;
        SampleItemManager itemMan;
        SampleManager     man;

        window.setBusy();
        try {
            bundle = (SampleDataBundle)deletedRow.data;
            man = bundle.getSampleManager();
            itemMan = man.getSampleItems();
            anMan = itemMan.getAnalysisAt(bundle.getSampleItemIndex());
            item = managers.get(man.getSample().getAccessionNumber());
            
            item.count--;

            if (item.count < 1 && man.getSample().getId() != null) {
                man.abortUpdate();
            } else {
                anMan.removeAnalysisAt(bundle.getAnalysisIndex());
                // if it's the last analysis remove the manager
                if (anMan.count() == 0)
                    itemMan.removeSampleItemAt(bundle.getSampleItemIndex());
            }
            
            if (item.count < 1)
                managers.remove(man.getSample().getAccessionNumber());

            if (quickEntryTable.numRows() == 0)
                setState(State.DEFAULT);
            
            window.clearStatus();
        } catch (Exception e) {
            Window.alert("rowDeleted: " + e.getMessage());
        }
    }

    private void addAnalysisRow() {
        int                    sampleItemIndex, analysisAddedIndex;
        Integer                id, accessionNum;
        String                 tubeNum;
        Item                   item;
        TableDataRow           row;
        AnalysisManager        anMan;
        SampleDataBundle       anBundle;
        SampleItemManager      itemMan;
        SampleManager          sampleMan;
        TestMethodSampleTypeVO typeDO;
        TestSectionViewDO      tsVDO;

        typeDO = (TestMethodSampleTypeVO)testMethodSampleType.getSelection().data;
        accessionNum = Integer.valueOf(accessionNumber.getValue());
        tubeNum = tubeNumber.getValue();
        tsVDO = (TestSectionViewDO)testSection.getSelection().data;
        row = new TableDataRow(6);

        try {
            item = managers.get(accessionNum);

            // if the sample manager is new, create it
            if (item == null) {
                sampleMan = SampleManager.getInstance();
                managers.put(accessionNum, new Item(sampleMan, 1));

                sampleMan.getSample().setDomain(SampleManager.QUICK_ENTRY);
                sampleMan.setDefaults();
                sampleMan.getSample().setReceivedById(UserCache.getPermission().getSystemUserId());
                sampleMan.getSample().setAccessionNumber(accessionNum);
                sampleMan.getSample().setReceivedDate(receivedDate.getValue());
            } else {
                sampleMan = item.sampleManager;
                item.count++;
            }

            itemMan = sampleMan.getSampleItems();
            sampleItemIndex = getSampleItemIndex(typeDO.getSampleTypeId(), itemMan);
            itemMan.getSampleItemAt(sampleItemIndex).setContainerReference(tubeNum);
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
            analysisTestChanged(id, (typeDO.getTestId() == null), tsVDO, anBundle, sampleMan);
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
            // TODO: Need to add value for Tube Number in this cell when we get
            // the specs for where it will be stored.  For now we will store it
            // in sample_item.container_reference.
            quickEntryTable.setCell(index, 5, itemDO.getContainerReference());
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

        // test needs filled out
        if (testSection.getValue() == null) {
            window.setError(consts.get("testSectionNoValueException"));
            return false;
        }

        return true;
    }
    
    private ArrayList<TableDataRow> getTableModel() {
        int                     i, j;
        ArrayList<TableDataRow> model;
        Item                    item;
        Iterator<Item>          itr;
        TableDataRow            row;
        AnalysisManager         anMan;
        AnalysisViewDO          anDO;
        OrganizationContactDO   data;
        SampleDO                sampleDO;
        SampleItemManager       itemMan;
        SampleItemViewDO        itemDO;
        SampleManager           manager;
        
        model = new ArrayList<TableDataRow>();
        if (managers.size() == 0)
            return model;

        try {
            itr = managers.values().iterator();
            while (itr.hasNext()) { // samples
                item = itr.next();
                manager = item.sampleManager;
                sampleDO = manager.getSample();

                for (i = 0; i < manager.getSampleItems().count(); i++) { // items
                    itemMan = manager.getSampleItems();
                    anMan = itemMan.getAnalysisAt(i);
                    itemDO = itemMan.getSampleItemAt(i);

                    for (j = 0; j < anMan.count(); j++) { // analyses
                        anDO = anMan.getAnalysisAt(j);
                        if (anDO.getId() < 0) {
                            row = new TableDataRow(6);
                            row.cells.get(0).value = sampleDO.getAccessionNumber();
                            row.cells.get(1).value = sampleDO.getReceivedDate();
                            row.cells.get(2).value = anDO.getTestName();
                            row.cells.get(3).value = anDO.getMethodName();
                            row.cells.get(4).value = itemDO.getTypeOfSample();
                            // TODO: Need to add value for Tube Number in this
                            // cell
                            // when we get the specs for where it will be
                            // stored.
                            // For now we will store it in
                            // sample_item.container_reference.
                            row.cells.get(5).value = itemDO.getContainerReference();
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
            sampleNotVerifiedId = DictionaryCache.getIdBySystemName("sample_not_verified");
            testSectionDefaultId = DictionaryCache.getIdBySystemName("test_section_default");
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
                         if (currentDateTime.getValue() != null && "Y".equals(currentDateTime.getValue())) {
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
    
    private class Item {
        private SampleManager sampleManager;
        private int           count;

        public Item(SampleManager man, int count) {
            this.sampleManager = man;
            this.count = count;
        }
    }
}
