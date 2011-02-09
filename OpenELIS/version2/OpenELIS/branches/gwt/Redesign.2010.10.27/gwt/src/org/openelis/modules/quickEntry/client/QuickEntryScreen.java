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
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.Button;
import org.openelis.gwt.widget.calendar.Calendar;
import org.openelis.gwt.widget.CheckBox;
import org.openelis.gwt.widget.Confirm;
import org.openelis.gwt.widget.DateHelper;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.Item;
import org.openelis.gwt.widget.Window;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.table.Row;
import org.openelis.gwt.widget.table.Table;
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
import com.google.gwt.user.client.rpc.AsyncCallback;

public class QuickEntryScreen extends Screen {

    protected Datetime             recDate;
    protected boolean              useCurrentTime, printLabelsOnTheFly, close;
    protected TestPrepUtility      testLookup;
    protected Confirm              windowCloseConfirm, receivedDateNotTodayConfirm;
    
    private Calendar               receivedDate;
    private TextBox                entry, tubeNumber;
    private TextBox<Integer>       accessionNumber;
    private CheckBox               currentDateTime, printLabels;
    private Button                 commitButton, removeRowButton;
    private Dropdown<String>       testMethodSampleType, testSection,printer;
    private Table                  quickEntryTable;
    private Row                    rowToBeAdded;

    private Integer                sampleLoggedInId, testSectionDefaultId;
    private Datetime               todaysDate;
    private AccessionNumberUtility accNumUtil;
    private ScreenService          calendarService, panelService;
    private ModulePermission       userPermission;
    private HashMap<Integer, QEItem> managers;

    public QuickEntryScreen() throws Exception {
        super((ScreenDefInt)GWT.create(QuickEntryDef.class));
        service = new ScreenService("controller?service=org.openelis.modules.test.server.TestService");
        panelService = new ScreenService("controller?service=org.openelis.modules.panel.server.PanelService");
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
        managers = new HashMap<Integer, QEItem>();
        close = false;
        
        window.addBeforeClosedHandler(new BeforeCloseHandler<Window>() {
            public void onBeforeClosed(BeforeCloseEvent<Window> event) {
                if(close){
                    close = false;
                }else if(quickEntryTable.getRowCount() > 0){
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
                entry.setEnabled(EnumSet.of(State.ADD, State.DEFAULT).contains(event.getState()));
            }
        });

        accessionNumber = (TextBox<Integer>)def.getWidget("accessionNumber");
        addScreenHandler(accessionNumber, new ScreenEventHandler<Integer>() {
            public void onStateChange(StateChangeEvent<State> event) {
                accessionNumber.setEnabled(false);
            }
        });

        tubeNumber = (TextBox)def.getWidget("tubeNumber");
        addScreenHandler(tubeNumber, new ScreenEventHandler<String>() {
            public void onStateChange(StateChangeEvent<State> event) {
                tubeNumber.setEnabled(false);
            }
        });

        receivedDate = (Calendar)def.getWidget("receivedDate");
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
                receivedDate.setEnabled(EnumSet.of(State.ADD, State.DEFAULT).contains(event.getState()));
            }
        });

        testMethodSampleType = (Dropdown)def.getWidget("testMethodSampleType");
        addScreenHandler(testMethodSampleType, new ScreenEventHandler<String>() {
            public void onValueChange(ValueChangeEvent<String> event) {
                int                     i, j;
                Integer                 defaultSectionId;
                ArrayList<IdVO>         testIds;
                ArrayList<Item<String>> model;
                HashMap<Integer,TestSectionViewDO> panelSections;
                IdVO                    testVO;
                TestManager             tm;
                TestMethodSampleTypeVO  typeDO;
                TestSectionViewDO       tsVDO;
                TestSectionManager      tsm;
                
                defaultSectionId = null;
                model = new ArrayList<Item<String>>();
                typeDO = (TestMethodSampleTypeVO)testMethodSampleType.getSelectedItem().getData();
                if (typeDO.getTestId() != null) {
                    try {
                        tm = TestManager.fetchById(typeDO.getTestId());
                        tsm = tm.getTestSections();
                        for (i = 0; i < tsm.count(); i++) {
                            tsVDO = tsm.getSectionAt(i);
                            if (testSectionDefaultId.equals(tsVDO.getFlagId()))
                                defaultSectionId = tsVDO.getSectionId();
                            model.add(new Item<String>(tsVDO.getSectionId().toString(), tsVDO.getSection()));
                            model.get(model.size()-1).setData(tsVDO);
                        }
                        testSection.setModel(model);
                        testSection.setValue(defaultSectionId.toString());
                    } catch (Exception anyE) {
                        com.google.gwt.user.client.Window.alert(consts.get("testSectionLoadError"));
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
                                    model.add(new Item<String>(tsVDO.getSectionId().toString(), tsVDO.getSection()));
                                    model.get(model.size()-1).setData(tsVDO);
                                }
                            }
                        }
                        testSection.setModel(model);
                        testSection.setValue(defaultSectionId.toString());
                    } catch (Exception anyE) {
                        com.google.gwt.user.client.Window.alert(consts.get("panelSectionLoadError"));
                        anyE.printStackTrace();
                    }
                }
            }
            
            public void onStateChange(StateChangeEvent<State> event) {
                testMethodSampleType.setEnabled(EnumSet.of(State.ADD, State.DEFAULT).contains(event.getState()));
            }
        });

        testSection = (Dropdown)def.getWidget("testSection");
        addScreenHandler(testSection, new ScreenEventHandler<String>() {
            public void onStateChange(StateChangeEvent<State> event) {
                testSection.setEnabled(EnumSet.of(State.ADD, State.DEFAULT).contains(event.getState()));
            }
        });

        currentDateTime = (CheckBox)def.getWidget("currentDateTime");
        addScreenHandler(currentDateTime, new ScreenEventHandler<String>() {
            public void onValueChange(ValueChangeEvent<String> event) {
                if ("Y".equals(event.getValue()))
                    updateRecievedDate();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                currentDateTime.setEnabled(EnumSet.of(State.ADD, State.DEFAULT).contains(event.getState()));
            }
        });

        printLabels = (CheckBox)def.getWidget("printLabels");
        addScreenHandler(printLabels, new ScreenEventHandler<String>() {
            public void onValueChange(ValueChangeEvent<String> event) {
                // if ("Y".equals(event.getValue()))
                // updateRecievedDate();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                printLabels.setEnabled(EnumSet.of(State.ADD, State.DEFAULT).contains(event.getState()));
            }
        });

        printer = (Dropdown)def.getWidget("printer");
        addScreenHandler(printer, new ScreenEventHandler<String>() {
            public void onStateChange(StateChangeEvent<State> event) {
                printer.setEnabled(EnumSet.of(State.ADD, State.DEFAULT).contains(event.getState()));
            }
        });

        quickEntryTable = (Table)def.getWidget("quickEntryTable");
        addScreenHandler(quickEntryTable, new ScreenEventHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent event) {
                quickEntryTable.setModel(getTableModel());
            }
            
            public void onStateChange(StateChangeEvent<State> event) {
                quickEntryTable.setEnabled(EnumSet.of(State.ADD).contains(event.getState()));
            }
        });
        
        quickEntryTable.addSelectionHandler(new SelectionHandler<Integer>() {
            public void onSelection(SelectionEvent<Integer> event) {
                removeRowButton.setEnabled(true);
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
        
        removeRowButton = (Button)def.getWidget("removeRowButton");
        addScreenHandler(removeRowButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int selected;

                selected = quickEntryTable.getSelectedRow();

                if (selected != -1)
                    quickEntryTable.removeRowAt(selected);
                
                removeRowButton.setEnabled(false);
                setFocus(entry);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeRowButton.setEnabled(false);
            }
        });

        commitButton = (Button)def.getWidget("commit");
        addScreenHandler(commitButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                commit();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                commitButton.setEnabled(EnumSet.of(State.ADD).contains(event.getState()));
            }
        });
    }

    private void commit() {
        int                  i;
        ArrayList<Integer>   removables;
        QEItem               item;
        Iterator<QEItem>     itr;
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
                manager.getSample().setStatusId(sampleLoggedInId);
                
                if(manager.getSample().getId() == null)
                    manager.add();
                else
                    manager.update();

                removables.add(manager.getSample().getAccessionNumber());
                item.count = 0;
            } catch (ValidationErrorsList e) {
                errorsList.add(new FormErrorException("quickCommitError"));
                for(i = 0; i < e.size(); i++)
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
        DateHelper    helper;
        boolean validDate = true;

        val = (String)entry.getValue();
        window.clearStatus();
        helper = new DateHelper();
        helper.setBegin(Datetime.YEAR);
        helper.setEnd(Datetime.MINUTE);
        // date recieved
        try {
        	recDate = helper.getValue(val);
        }catch(Exception e) {
        	validDate = false;
        }
        if (validDate) {
            if (todaysDate.after(recDate)) {
                LocalizedException ex = new LocalizedException("recievedDateNotTodayExceptionBody", helper.format(recDate));
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
                                receivedDate.setValue(recDate);
                                break;
                        }
                    }
                });
            
                receivedDateNotTodayConfirm.show();
            
            } else {
                receivedDate.setValue(recDate);
            }

            // test/panel
        } else if (val.matches("[TP][0-9]*\\-[0-9]*")) {
            testMethodSampleType.setValue(val);

            // tube #
        } else if (val.matches("[a-zA-Z]{3}[0-9]{3}")) {
            tubeNumber.setValue(val);

            // new accession #
        } else if (val.matches("NEW")) {
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
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }
            }

            // accession #
        } else if (val.matches("[0-9]+") || val.matches("[0-9]+-[0-9]+")) {
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
                                     new QEItem(sampleMan, 0));

                    accessionNumber.setValue(new Integer(val));
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
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }
            }
        } else {
            LocalizedException e = new LocalizedException("invalidEntryException", val);
            window.setError(e.getMessage());
        }
        entry.setValue(null);
        //recDate.exceptions = null;
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
            com.google.gwt.user.client.Window.alert("analysisTestChanged: " + e.getMessage());
        }
    }

    public void testLookupFinished(ArrayList<SampleDataBundle> bundles) {
        int numOfRows;
        QEItem           item;
        Row              newRow;
        SampleDataBundle bundle;
        SampleManager    sManager;

        window.setBusy();

        numOfRows = 0;
        if (bundles.size() == 0)
            rowToBeAdded = null;
        else {
            numOfRows = quickEntryTable.getRowCount();
            newRow = null;
            //quickEntryTable.fireEvents(false);

            for (int i = 0; i < bundles.size(); i++ ) {
                bundle = bundles.get(i);
                if (i == 0) {
                    quickEntryTable.addRow(rowToBeAdded);
                    newRow = rowToBeAdded;
                    rowToBeAdded = null;
                    newRow.setData(bundle);
                } else {
                    newRow = new Row(6);
                    newRow.setData(bundle);
                    quickEntryTable.addRow(newRow);
                }
                
                sManager = bundle.getSampleManager();
                item = managers.get(sManager.getSample().getAccessionNumber());
                if (item == null)
                    managers.put(sManager.getSample().getAccessionNumber(), new QEItem(sManager, 1));
                else
                    item.count++;

                updateQuickEntryRowFromBundle(quickEntryTable.getRowCount() - 1);
            }

            //quickEntryTable.fireEvents(true);
        }
        
        if(numOfRows == 0 && quickEntryTable.getRowCount() > 0)
            setState(State.ADD);

        window.clearStatus();
    }

    private void rowDeleted(Row deletedRow) {
        QEItem            item;
        AnalysisManager   anMan;
        SampleDataBundle  bundle;
        SampleItemManager itemMan;
        SampleManager     man;

        window.setBusy();
        try {
            bundle = (SampleDataBundle)deletedRow.getData();
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
                
            if(quickEntryTable.getRowCount() == 0)
                setState(State.DEFAULT);
            
            window.clearStatus();
        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert("rowDeleted: " + e.getMessage());
        }
    }

    private void addAnalysisRow() {
        int                    sampleItemIndex, analysisAddedIndex;
        Integer                id, accessionNum;
        String                 tubeNum;
        QEItem                 item;
        Row                    row;
        AnalysisManager        anMan;
        SampleDataBundle       anBundle;
        SampleItemManager      itemMan;
        SampleManager          sampleMan;    
        TestMethodSampleTypeVO typeDO;
        TestSectionViewDO      tsVDO;

        typeDO = (TestMethodSampleTypeVO)testMethodSampleType.getSelectedItem().getData();
        accessionNum = Integer.valueOf(accessionNumber.getValue());
        tubeNum = (String)tubeNumber.getValue();
        tsVDO = (TestSectionViewDO)testSection.getSelectedItem().getData();
        row = new Row(6);

        try {
            item = managers.get(accessionNum);

            // if the sample manager is new, create it
            if (item == null) {
                sampleMan = SampleManager.getInstance();
                managers.put(accessionNum, new QEItem(sampleMan, 1));

                sampleMan.getSample().setDomain(SampleManager.QUICK_ENTRY);
                sampleMan.setDefaults();
                sampleMan.getSample().setReceivedById(OpenELIS.getSystemUserPermission().getSystemUserId());
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
            row.setData(anBundle);

            if (typeDO.getTestId() != null)
                id = typeDO.getTestId();
            else
                id = typeDO.getPanelId();

            rowToBeAdded = row;
            analysisTestChanged(id, (typeDO.getTestId() == null), tsVDO, anBundle, sampleMan);

        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert("rowAdded: " + e.getMessage());
        }
    }

    private void updateQuickEntryRowFromBundle(int index) {
        Row row;
        SampleDataBundle bundle;
        SampleManager man;
        SampleItemManager itemMan;
        SampleItemViewDO itemDO;
        AnalysisViewDO anDO;

        try {
            row = quickEntryTable.getRowAt(index);
            bundle = (SampleDataBundle)row.getData();
            man = bundle.getSampleManager();
            itemMan = man.getSampleItems();
            itemDO = itemMan.getSampleItemAt(bundle.getSampleItemIndex());
            anDO = itemMan.getAnalysisAt(bundle.getSampleItemIndex())
                          .getAnalysisAt(bundle.getAnalysisIndex());

            quickEntryTable.setValueAt(index, 0, man.getSample().getAccessionNumber());
            quickEntryTable.setValueAt(index, 1, man.getSample().getReceivedDate());
            quickEntryTable.setValueAt(index, 2, anDO.getTestName());
            quickEntryTable.setValueAt(index, 3, anDO.getMethodName());
            quickEntryTable.setValueAt(index, 4, itemDO.getTypeOfSample());
            // TODO: Need to add value for Tube Number in this cell when we get
            // the specs for where it will be stored.  For now we will store it
            // in sample_item.container_reference.
            quickEntryTable.setValueAt(index, 5, itemDO.getContainerReference());
        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert("updateQuickEntryRowFromBundle: " + e.getMessage());
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
    
    private ArrayList<Row> getTableModel() {
        int                     i, j;
        ArrayList<Row>          model;
        QEItem                  item;
        Iterator<QEItem>        itr;
        Row                     row;
        AnalysisManager         anMan;
        AnalysisViewDO          anDO;
        OrganizationContactDO   data;
        SampleDO                sampleDO;
        SampleItemManager       itemMan;
        SampleItemViewDO        itemDO;
        SampleManager           manager;
        
        model = new ArrayList<Row>();
        if (managers.size() == 0)
            return model;

        try {
            itr = managers.values().iterator();
            while (itr.hasNext()) {       //samples
                item = itr.next();
                manager = item.sampleManager;
                sampleDO = manager.getSample();
                
                for (i = 0; i < manager.getSampleItems().count(); i++) { //items
                    itemMan = manager.getSampleItems();
                    anMan = itemMan.getAnalysisAt(i);
                    itemDO = itemMan.getSampleItemAt(i);
                    
                    for(j = 0; j < anMan.count(); j++) {   //analyses
                        anDO = anMan.getAnalysisAt(j);
                        if (anDO.getId() < 0) {
                            row = new Row(6);
                            row.setCell(0,sampleDO.getAccessionNumber());
                            row.setCell(1,sampleDO.getReceivedDate());
                            row.setCell(2,anDO.getTestName());
                            row.setCell(3,anDO.getMethodName());
                            row.setCell(4,itemDO.getTypeOfSample());
                            // TODO: Need to add value for Tube Number in this cell
                            // when we get the specs for where it will be stored.
                            // For now we will store it in sample_item.container_reference.
                            row.setCell(5,itemDO.getContainerReference());
                            model.add(row);
                        }
                    }
                }
            }
        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert(e.getMessage());
            e.printStackTrace();
        }
        return model;
    }

    private void initializeDropdowns() {
        ArrayList<TestMethodSampleTypeVO> testPanelList;
        ArrayList<Item<String>> model;
        Item<String> row;

        try {
            sampleLoggedInId = DictionaryCache.getIdFromSystemName("sample_logged_in");
            testSectionDefaultId = DictionaryCache.getIdFromSystemName("test_section_default");
            todaysDate = org.openelis.gwt.screen.Calendar.getCurrentDatetime(Datetime.YEAR, Datetime.DAY);
            
            testPanelList = service.callList("fetchTestMethodSampleTypeList");
            model = new ArrayList<Item<String>>();
            model.add(new Item<String>(null, ""));

            for (TestMethodSampleTypeVO typeDO : testPanelList) {
                row = new Item<String>(1);
                if (typeDO.getPanelId() == null) {
                    row.setKey("T" + typeDO.getTestId() + "-" + typeDO.getSampleTypeId());
                    row.setCell(0,typeDO.getTest() + ", " + typeDO.getMethod() + ", " +
                                             typeDO.getSampleType());
                    row.setData(typeDO);
                } else {
                    row.setKey("P" + typeDO.getPanelId() + "-" + typeDO.getSampleTypeId());
                    row.setCell(0,typeDO.getPanel() + ", " + typeDO.getSampleType());
                    row.setData(typeDO);
                }

                model.add(row);
            }

            testMethodSampleType.setModel(model);
        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert("inializeDropdowns: " + e.getMessage());
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
                                             com.google.gwt.user.client.Window.alert(caught.getMessage());
                                         }
                                     });
    }
    
    private class QEItem {
        private SampleManager sampleManager;
        private int           count;

        public QEItem(SampleManager man, int count) {
            this.sampleManager = man;
            this.count = count;
        }
    }
}
