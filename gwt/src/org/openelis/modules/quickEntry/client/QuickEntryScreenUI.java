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
package org.openelis.modules.quickEntry.client;

import static org.openelis.modules.main.client.Logger.logger;
import static org.openelis.ui.screen.Screen.ShortKeys.CTRL;
import static org.openelis.ui.screen.State.DEFAULT;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

import org.openelis.cache.CacheProvider;
import org.openelis.cache.CategoryCache;
import org.openelis.cache.UserCache;
import org.openelis.constants.Messages;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.AuxDataViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.IdVO;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.SampleDO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.domain.SampleTestRequestVO;
import org.openelis.domain.SampleTestReturnVO;
import org.openelis.domain.SystemVariableDO;
import org.openelis.domain.TestMethodSampleTypeVO;
import org.openelis.domain.TestSectionViewDO;
import org.openelis.manager.AuxFieldGroupManager;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.TestManager;
import org.openelis.meta.SampleMeta;
import org.openelis.modules.auxiliary.client.AuxiliaryService;
import org.openelis.modules.panel.client.PanelService;
import org.openelis.modules.sample1.client.SampleService1;
import org.openelis.modules.sample1.client.TestSelectionLookupUI;
import org.openelis.modules.systemvariable.client.SystemVariableService;
import org.openelis.modules.test.client.TestService;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.FieldErrorException;
import org.openelis.ui.common.FormErrorException;
import org.openelis.ui.common.InconsistencyException;
import org.openelis.ui.common.ModulePermission;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.PermissionException;
import org.openelis.ui.common.SystemUserVO;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.event.BeforeCloseEvent;
import org.openelis.ui.event.BeforeCloseHandler;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.GetMatchesEvent;
import org.openelis.ui.event.GetMatchesHandler;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.resources.UIResources;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.services.CalendarService;
import org.openelis.ui.widget.AutoComplete;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.CheckBox;
import org.openelis.ui.widget.Confirm;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.ModalWindow;
import org.openelis.ui.widget.QueryFieldUtil;
import org.openelis.ui.widget.TextBox;
import org.openelis.ui.widget.WindowInt;
import org.openelis.ui.widget.calendar.Calendar;
import org.openelis.ui.widget.table.Row;
import org.openelis.ui.widget.table.Table;
import org.openelis.ui.widget.table.event.BeforeCellEditedEvent;
import org.openelis.ui.widget.table.event.BeforeCellEditedHandler;
import org.openelis.utilcommon.ResultFormatter;
import org.openelis.utilcommon.ResultHelper;

public class QuickEntryScreenUI extends Screen implements CacheProvider {

    @UiTemplate("QuickEntry.ui.xml")
    interface QuickEntryUiBinder extends UiBinder<Widget, QuickEntryScreenUI> {
    };
    
    private static QuickEntryUiBinder uiBinder = GWT.create(QuickEntryUiBinder.class);

    private boolean                                        close;
    private Datetime                                       todaysDate;
    private HashMap<Integer, ArrayList<TestSectionViewDO>> testSectionHash;
    private HashMap<Integer, SampleManagerRowCount>        managers;
    private ModulePermission                               userPermission;
    private ScheduledCommand                               focusEntryCmd;

    @UiField
    protected AutoComplete                                 receivedBy;
    @UiField
    protected Button                                       commit, removeRowButton;
    @UiField
    protected Calendar                                     receivedDate;
    @UiField
    protected CheckBox                                     currentDateTime, printLabels;
    @UiField
    protected Dropdown<Integer>                            printer, sectionId;
    @UiField
    protected Dropdown<String>                             testMethodSampleType;
    @UiField
    protected Table                                        quickEntryTable;
    @UiField
    protected TextBox<Integer>                             accessionNumber;
    @UiField
    protected TextBox<String>                              entry, tubeNumber;

    protected boolean                                      isBusy;
    protected Confirm                                      windowCloseConfirm,
                                                           receivedDateNotTodayConfirm;
    protected HashMap<String, Object>                      cache;
    protected QuickEntryScreenUI                           screen;
    protected TestSelectionLookupUI                        testSelectionLookup;

    public QuickEntryScreenUI(WindowInt window) throws Exception {
        setWindow(window);
        
        userPermission = UserCache.getPermission().getModule("quickentry");
        if (userPermission == null)
            throw new PermissionException(Messages.get().screenPermException("Quick Entry Screen"));

        initWidget(uiBinder.createAndBindUi(this));
        
        close = false;
        managers = new HashMap<Integer, SampleManagerRowCount>();
        testSectionHash = new HashMap<Integer, ArrayList<TestSectionViewDO>>();
        todaysDate = CalendarService.get().getCurrentDatetime(Datetime.YEAR, Datetime.DAY);
    }

    /**
     * Setup state and data change handles for every widget on the screen
     */
    public void initialize() throws Exception {
        ArrayList<TestMethodSampleTypeVO> testPanelList;
        ArrayList<Item<String>> model;
        Item<String> item;

        screen = this;
        cache = new HashMap<String, Object>();

        addShortcut(commit, 'm', CTRL);
        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                commit.setEnabled(false);
            }
        });

        addScreenHandler(entry, "entry", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                entry.setValue(null);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                entryChanged();
            }
            
            public void onStateChange(StateChangeEvent event) {
                entry.setEnabled(true);
            }            

            public Widget onTab(boolean forward) {
                return forward ? receivedDate : receivedDate;
            }
        });

        addScreenHandler(accessionNumber, SampleMeta.getAccessionNumber(), new ScreenHandler<Integer>() {
            public void onStateChange(StateChangeEvent event) {
                accessionNumber.setEnabled(false);
            }            

            public Widget onTab(boolean forward) {
                return forward ? entry : entry;
            }
        });
            
        addScreenHandler(tubeNumber, "tubeNumber", new ScreenHandler<String>() {
            public void onStateChange(StateChangeEvent event) {
                tubeNumber.setEnabled(false);
            }            

            public Widget onTab(boolean forward) {
                return forward ? entry : entry;
            }
        });
            
        addScreenHandler(receivedDate, SampleMeta.getReceivedDate(), new ScreenHandler<Datetime>() {
            public void onValueChange(ValueChangeEvent<Datetime> event) {
                if (todaysDate.after(event.getValue())) {
                    Exception ex = new Exception(Messages.get().receivedDateNotTodayExceptionBody(
                                                                   event.getValue()
                                                                        .toString()));
                    receivedDateNotTodayConfirm = new Confirm(Confirm.Type.QUESTION,
                                                              Messages.get().receivedDateNotTodayExceptionTitle(),
                                                              ex.getMessage(),
                                                              "No",
                                                              "Yes");
                    receivedDateNotTodayConfirm.addSelectionHandler(new SelectionHandler<Integer>() {
                        public void onSelection(SelectionEvent<Integer> event) {
                            switch (event.getSelectedItem().intValue()) {
                                case 0:
                                    receivedDate.setValue(null);
                                    entry.setFocus(true);
                                    break;
                                case 1:
                                    entry.setFocus(true);
                                    break;
                            }
                        }
                    });

                    receivedDateNotTodayConfirm.show();
                }
            }

            public void onStateChange(StateChangeEvent event) {
                receivedDate.setEnabled(true);
            }            

            public Widget onTab(boolean forward) {
                return forward ? entry : entry;
            }
        });

        addScreenHandler(receivedBy, SampleMeta.getReceivedById(), new ScreenHandler<Integer>() {
            public void onStateChange(StateChangeEvent event) {
                receivedBy.setEnabled(true);
            }            

            public Widget onTab(boolean forward) {
                return forward ? entry : entry;
            }
        });

        receivedBy.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                ArrayList<SystemUserVO> users;
                ArrayList<Item<Integer>> model;
                
                window.setBusy();
                try {
                    model = new ArrayList<Item<Integer>>();
                    users = UserCache.getEmployees(QueryFieldUtil.parseAutocomplete(event.getMatch()));
                    for (SystemUserVO user : users)
                        model.add(new Item<Integer>(user.getId(), user.getLoginName()));
                    receivedBy.showAutoMatches(model);
                } catch (Exception e) {
                    e.printStackTrace();
                    Window.alert(e.toString());
                }
                window.clearStatus();
            }
        });

        addScreenHandler(testMethodSampleType, "testMethodSampleType", new ScreenHandler<String>() {
            public void onValueChange(ValueChangeEvent<String> event) {
                Integer defaultSectionId;
                ArrayList<IdVO> testIds;
                ArrayList<Item<Integer>> model;
                ArrayList<TestSectionViewDO> sections;
                HashMap<Integer, TestSectionViewDO> panelSections;
                Item<Integer> item;
                TestMethodSampleTypeVO typeDO;

                defaultSectionId = null;
                model = new ArrayList<Item<Integer>>();
                if (testMethodSampleType.getSelectedItem() != null) {
                    typeDO = (TestMethodSampleTypeVO)testMethodSampleType.getSelectedItem().getData();
                    if (typeDO.getTestId() != null) {
                        try {
                            sections = testSectionHash.get(typeDO.getTestId());
                            if (sections == null) {
                                sections = TestService.get().fetchTestSectionsByTestId(typeDO.getTestId());
                                testSectionHash.put(typeDO.getTestId(), sections);
                            }
                            for (TestSectionViewDO tsVDO : sections) {
                                if (Constants.dictionary().TEST_SECTION_DEFAULT.equals(tsVDO.getFlagId()))
                                    defaultSectionId = tsVDO.getSectionId();
                                item = (new Item<Integer>(tsVDO.getSectionId(),
                                                         tsVDO.getSection()));
                                item.setData(tsVDO);
                                model.add(item);
                            }
                            sectionId.setModel(model);
                            sectionId.setValue(defaultSectionId);
                        } catch (Exception anyE) {
                            Window.alert(Messages.get().testSectionLoadError());
                            anyE.printStackTrace();
                        }
                    } else {
                        panelSections = new HashMap<Integer, TestSectionViewDO>();
                        try {
                            testIds = PanelService.get().fetchTestIdsByPanelId(typeDO.getPanelId());
                            for (IdVO testVO : testIds) {
                                sections = testSectionHash.get(typeDO.getTestId());
                                if (sections == null) {
                                    sections = TestService.get().fetchTestSectionsByTestId(testVO.getId());
                                    testSectionHash.put(typeDO.getTestId(), sections);
                                }
                                for (TestSectionViewDO tsVDO : sections) {
                                    if (!panelSections.containsKey(tsVDO.getSectionId())) {
                                        panelSections.put(tsVDO.getSectionId(), tsVDO);
                                        if (Constants.dictionary().TEST_SECTION_DEFAULT.equals(tsVDO.getFlagId()))
                                            defaultSectionId = tsVDO.getSectionId();
                                        item = (new Item<Integer>(tsVDO.getSectionId(),
                                                        tsVDO.getSection()));
                                        item.setData(tsVDO);
                                        model.add(item);
                                    }
                                }
                            }
                            sectionId.setModel(model);
                            sectionId.setValue(defaultSectionId);
                        } catch (Exception anyE) {
                            Window.alert(Messages.get().panelSectionLoadError());
                            anyE.printStackTrace();
                        }
                    }
                } else {
                    sectionId.setModel(model);
                }
            }

            public void onStateChange(StateChangeEvent event) {
                testMethodSampleType.setEnabled(true);
            }            

            public Widget onTab(boolean forward) {
                return forward ? entry : entry;
            }
        });

        addScreenHandler(sectionId, SampleMeta.getAnalysisSectionId(), new ScreenHandler<Integer>() {
            public void onStateChange(StateChangeEvent event) {
                sectionId.setEnabled(true);
            }            

            public Widget onTab(boolean forward) {
                return forward ? entry : entry;
            }
        });
            
        addScreenHandler(currentDateTime, "currentDateTime", new ScreenHandler<String>() {
            public void onValueChange(ValueChangeEvent<String> event) {
                if ("Y".equals(event.getValue()))
                    updateRecievedDate();
            }

            public void onStateChange(StateChangeEvent event) {
                currentDateTime.setEnabled(true);
            }            

            public Widget onTab(boolean forward) {
                return forward ? entry : entry;
            }
        });

        addScreenHandler(printLabels, "printLabels", new ScreenHandler<String>() {
            public void onStateChange(StateChangeEvent event) {
                printLabels.setEnabled(true);
            }            

            public Widget onTab(boolean forward) {
                return forward ? entry : entry;
            }
        });

        addScreenHandler(printer, "printer", new ScreenHandler<Integer>() {
            public void onStateChange(StateChangeEvent event) {
                printer.setEnabled(false);
            }            

            public Widget onTab(boolean forward) {
                return forward ? entry : entry;
            }
        });
            
        addScreenHandler(quickEntryTable, "quickEntryTable", new ScreenHandler<ArrayList<Item<String>>>() {
            public void onDataChange(DataChangeEvent event) {
                quickEntryTable.setModel(getTableModel());
            }

            public void onStateChange(StateChangeEvent event) {
                quickEntryTable.setEnabled(true);
                quickEntryTable.setAllowMultipleSelection(false);
            }            
        });

        quickEntryTable.addSelectionHandler(new SelectionHandler<Integer>() {
            public void onSelection(SelectionEvent<Integer> event) {
                if (quickEntryTable.getSelectedRow() != -1)
                    removeRowButton.setEnabled(true);
                else
                    removeRowButton.setEnabled(false);
            }
        });
        
        quickEntryTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                //
                // This table cannot be edited
                //
                event.cancel();
            }
        });
        
        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                removeRowButton.setEnabled(false);
            }
        });

        window.addBeforeClosedHandler(new BeforeCloseHandler<WindowInt>() {
            public void onBeforeClosed(BeforeCloseEvent<WindowInt> event) {
                if (close) {
                    close = false;
                } else if (quickEntryTable.getRowCount() > 0) {
                    event.cancel();
                    if (windowCloseConfirm == null) {
                        windowCloseConfirm = new Confirm(Confirm.Type.QUESTION,
                                                         Messages.get().onCloseConfirmTitle(),
                                                         Messages.get().onCloseConfirmBody(),
                                                         "No",
                                                         "Yes",
                                                         "Cancel");
                        windowCloseConfirm.addSelectionHandler(new SelectionHandler<Integer>() {
                            public void onSelection(SelectionEvent<Integer> event) {
                                switch (event.getSelectedItem().intValue()) {
                                    case 0:
                                        abort();
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
        
        try {
            CategoryCache.getBySystemNames("type_of_sample");
        } catch (Exception e) {
            throw new Exception("QuickEntryScreenUI: missing dictionary entry; " + e.getMessage());
        }
        
        //
        // load test/method/sample type dropdown model
        //
        testPanelList = TestService.get().fetchTestMethodSampleTypeList();
        model = new ArrayList<Item<String>>();
        for (TestMethodSampleTypeVO typeDO : testPanelList) {
            if (typeDO.getPanelId() == null) {
                item = new Item<String>("T" + typeDO.getTestId() + "-" + typeDO.getSampleTypeId(),
                                        typeDO.getTest() + ", " + typeDO.getMethod() +
                                        ", " + typeDO.getSampleType());
                item.setData(typeDO);
            } else {
                item = new Item<String>("P" + typeDO.getPanelId() + "-" + typeDO.getSampleTypeId(),
                                        typeDO.getPanel() + ", " + typeDO.getSampleType());
                item.setData(typeDO);
            }
            model.add(item);
        }
        testMethodSampleType.setModel(model);

        /*
         * to set the focus back on the entry widget, a command must be scheduled
         */
        focusEntryCmd = new ScheduledCommand() {
            @Override
            public void execute() {
                entry.setFocus(true);
            }
        };

        setState(DEFAULT);
        fireDataChange();
        
        receivedBy.setValue(UserCache.getPermission().getSystemUserId(), UserCache.getPermission().getLoginName());

        logger.fine("Quick Entry Screen Opened");
    }
    
    /**
     * returns from the cache, the object that has the specified key and is of
     * the specified class
     */
    @Override
    public <T> T get(Object key, Class<?> c) {
        String cacheKey;
        Object obj;

        if (cache == null)
            return null;

        cacheKey = null;
        if (c == TestManager.class)
            cacheKey = "tm:" + key;
        else if (c == AuxFieldGroupManager.class)
            cacheKey = "am:" + key;

        obj = cache.get(cacheKey);
        if (obj != null)
            return (T)obj;

        /*
         * if the requested object is not in the cache then obtain it and put it
         * in the cache
         */
        try {
            if (c == TestManager.class)
                obj = TestService.get().fetchById((Integer)key);
            else if (c == AuxFieldGroupManager.class)
                obj = AuxiliaryService.get().fetchById((Integer)key);

            cache.put(cacheKey, obj);
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return (T)obj;
    }

    /*
     * basic button methods
     */
    @SuppressWarnings("unused")
    @UiHandler("commit")
    protected void commit(ClickEvent event) {
        commit();
    }
    
    @SuppressWarnings("unused")
    @UiHandler("removeRowButton")
    protected void removeRow(ClickEvent event) {
        int rowIndex;
        AnalysisViewDO aVDO;
        Row row;
        SampleItemViewDO siVDO;
        SampleManager1 man;
        SampleManagerRowCount smRC;
        
        rowIndex = quickEntryTable.getSelectedRow();
        row = quickEntryTable.getRowAt(rowIndex);
        aVDO = row.getData();
        smRC = managers.get(row.getCell(0));
        man = smRC.sampleManager;
        man.analysis.removeAnalysis(aVDO.getId());
        smRC.count--;
        if (smRC.count <= 0) {
            if (man.getSample().getId() != null) {
                try {
                    SampleService1.get().unlock(man.getSample().getId(),
                                                (SampleManager1.Load[])null);
                } catch (Exception anyE) {
                    Window.alert(anyE.getMessage());
                }
            }
            managers.remove(man.getSample().getAccessionNumber());
        }
        quickEntryTable.removeRowAt(rowIndex);
        if (quickEntryTable.getRowCount() == 0)
            commit.setEnabled(false);
    }
    
    private void commit() {
        int i;
        ArrayList<Integer> removables;
        SampleManager1 man;
        ValidationErrorsList errorsList;

        finishEditing();
        clearErrors();
        
        window.setBusy(Messages.get().adding());

        errorsList = new ValidationErrorsList();
        removables = new ArrayList<Integer>();
        for (SampleManagerRowCount smRC : managers.values()) {
            man = smRC.sampleManager;
            try {
                SampleService1.get().update(man, true);
                removables.add(man.getSample().getAccessionNumber());
            } catch (ValidationErrorsList e) {
                errorsList.add(new FormErrorException(Messages.get().quickCommitError()));
                for (i = 0; i < e.size(); i++)
                    errorsList.add(new FormErrorException(Messages.get()
                                                                  .rowError(man.getSample()
                                                                               .getAccessionNumber()
                                                                               .toString(),
                                                                            e.getErrorList()
                                                                             .get(i)
                                                                             .getLocalizedMessage())));
            } catch (Exception e) {
                errorsList.add(new FormErrorException(Messages.get().quickCommitError()));
                errorsList.add(new FormErrorException(Messages.get()
                                                              .rowError(man.getSample()
                                                                           .getAccessionNumber()
                                                                           .toString(),
                                                                        e.getMessage())));
            }
        }
        
        for (i = 0; i < removables.size(); i++)
            managers.remove(removables.get(i));

        if (errorsList.size() > 0) {
            showErrors(errorsList);
        } else {
            setState(DEFAULT);
            window.setDone(Messages.get().addingComplete());
        }

        fireDataChange();
    }
    
    private void abort() {
        int i;
        ArrayList<Integer> removables;
        SampleManager1 man;
        ValidationErrorsList errorsList;

        finishEditing();
        clearErrors();
        
        window.setBusy(Messages.get().gen_cancelChanges());

        errorsList = new ValidationErrorsList();
        removables = new ArrayList<Integer>();
        for (SampleManagerRowCount smRC : managers.values()) {
            man = smRC.sampleManager;
            if (man.getSample().getId() != null) {
                try {
                    SampleService1.get().unlock(man.getSample().getId(), (SampleManager1.Load[])null);
                    removables.add(man.getSample().getAccessionNumber());
                } catch (Exception e) {
                    errorsList.add(new FormErrorException(Messages.get()
                                                                  .rowError(man.getSample()
                                                                               .getAccessionNumber()
                                                                               .toString(),
                                                                            e.getMessage())));
                }
            } else {
                removables.add(man.getSample().getAccessionNumber());
            }
        }
        
        for (i = 0; i < removables.size(); i++)
            managers.remove(removables.get(i));

        if (errorsList.size() > 0) {
            showErrors(errorsList);
        } else {
            setState(DEFAULT);
            window.setDone(Messages.get().addAborted());
        }
    }
        
    private void entryChanged() {
        int index;
        final Date recDate;
        Exception ex;
        Integer accessionNum;
        SampleManager1 sampleMan;
        SampleManagerRowCount smRowCount;
        String val;
        SystemVariableDO sysVarDO;

        val = entry.getValue();
        window.clearStatus();

        try {
            recDate = DateTimeFormat.getFormat(Messages.get().dateTimePattern()).parseStrict(val);
            if (todaysDate.after(recDate)) {
                ex = new Exception(Messages.get()
                                           .receivedDateNotTodayExceptionBody(DateTimeFormat.getFormat(Messages.get()
                                                                                                               .dateTimePattern())
                                                                                            .format(recDate)));
                receivedDateNotTodayConfirm = new Confirm(Confirm.Type.QUESTION,
                                                          Messages.get()
                                                                  .receivedDateNotTodayExceptionTitle(),
                                                          ex.getMessage(),
                                                          "No",
                                                          "Yes");
                receivedDateNotTodayConfirm.addSelectionHandler(new SelectionHandler<Integer>() {
                    public void onSelection(SelectionEvent<Integer> event) {
                        switch (event.getSelectedItem().intValue()) {
                            case 0:
                                // do nothing
                                break;
                            case 1:
                                receivedDate.setValue(new Datetime(Datetime.YEAR, Datetime.MINUTE, recDate));
                                break;
                        }
                    }
                });
                receivedDateNotTodayConfirm.show();
            } else {
                receivedDate.setValue(new Datetime(Datetime.YEAR, Datetime.MINUTE, recDate));
            }
        } catch (IllegalArgumentException iaE) {
            // ignore and fall through
            if (val.matches("[TP][0-9]*\\-[0-9]*")) {    // test & panel
                try {
                    testMethodSampleType.setValue(val, true);
                } catch (Exception e) {
                    ex = new Exception(Messages.get().invalidEntryException(val));
                    window.setError(ex.getMessage());
                }
            } else if (val.matches("[a-zA-Z]{3}[0-9]{3}")) {    // tube #
                tubeNumber.setValue(val);
            } else if (val.matches("NEW")) {                    // new accession #
                // TODO: Implement using the "NEW" keyword to get the next accession
                //       number for use when printing the labels on the fly
                Window.alert("Not yet implemented!");
//                if (validateFields()) {
//                    if (accNumUtil == null)
//                        accNumUtil = new AccessionNumberUtility();
//    
//                    try {
//                        accessionNum = accNumUtil.getNewAccessionNumber();
//                        accessionNumber.setFieldValue(accessionNum);
//                        addAnalysisRow();
//                    } catch (ValidationErrorsList e) {
//                        showErrors(e);
//                    } catch (Exception e) {
//                        Window.alert(e.getMessage());
//                    }
//                }
            } else if (val.matches("[0-9]+") || val.matches("[0-9]+-[0-9]+")) { // accession #
                if (validateFields()) {
                    //
                    // Trim the Sample Item ID from the end of the bar coded
                    // accession number
                    //
                    index = val.indexOf("-");
                    if (index != -1)
                        val = val.substring(0, index);
    
                    try {
                        accessionNum = Integer.valueOf(val);
                        smRowCount = managers.get(accessionNum);
                        if (smRowCount == null) {
                            try {
                                sampleMan = SampleService1.get().fetchByAccession(accessionNum, (SampleManager1.Load[])null);
                                if (Constants.dictionary().SAMPLE_RELEASED.equals(sampleMan.getSample().getStatusId()))
                                    throw new InconsistencyException(Messages.get().sample_cantAddAnalysis());
                                sampleMan = SampleService1.get().fetchForUpdate(sampleMan.getSample().getId(), (SampleManager1.Load[])null);
                            } catch (NotFoundException nfE) {
                                sysVarDO = SystemVariableService.get().fetchByExactName("last_accession_number");
                                if (accessionNum.compareTo(Integer.valueOf(sysVarDO.getValue())) > 0)
                                    throw new InconsistencyException(Messages.get()
                                                                             .sample_accessionNumberNotInUse(accessionNum));
                                sampleMan = SampleService1.get().getInstance(Constants.domain().QUICKENTRY);
                                sampleMan.getSample().setAccessionNumber(accessionNum);
                                sampleMan.getSample().setReceivedDate(receivedDate.getValue());
                                sampleMan.getSample().setReceivedById(receivedBy.getValue().getId());
                            }
                            managers.put(sampleMan.getSample().getAccessionNumber(),
                                         new SampleManagerRowCount(sampleMan, 0));
                        } else {
                            sampleMan = smRowCount.sampleManager;
                        }
                        accessionNumber.setValue(accessionNum);
                        addAnalysis();
                    } catch (NumberFormatException e) {
                        ex = new Exception(Messages.get().invalidEntryException(val));
                        window.setError(ex.getMessage());
                    } catch (InconsistencyException e) {
                        window.setError(e.getMessage());
                    } catch (ValidationErrorsList e) {
                        int i;
                        FieldErrorException fe;
                        ValidationErrorsList newE;
                        
                        newE = new ValidationErrorsList();
    
                        // convert all the field errors to form errors
                        for (i = 0; i < e.size(); i++ ) {
                            if (e.getErrorList().get(i) instanceof FieldErrorException) {
                                fe = (FieldErrorException)e.getErrorList().get(i);
                                newE.add(new FormErrorException(fe.getMessage()));
                            } else {
                                newE.add(e.getErrorList().get(i));
                            }
                        }
                        showErrors(newE);
                    } catch (Exception e) {
                        Window.alert(e.getMessage());
                    }
                }
            } else {
                ex = new Exception(Messages.get().invalidEntryException(val));
                window.setError(ex.getMessage());
            }
        }
        entry.setValue(null);
        /*
         * to set the focus back on the entry widget, a command must be scheduled
         */
        Scheduler.get().scheduleDeferred(focusEntryCmd);
    }

    private void addAnalysis() {
        Integer accessionNum;
        SampleItemViewDO siVDO;
        SampleManagerRowCount smRowCount;
        SampleManager1 sampleMan;
        SampleTestRequestVO requestVO;
        SampleTestReturnVO returnVO;
        String tubeNum;
        TestMethodSampleTypeVO typeDO;
        TestSectionViewDO tsVDO;
        ValidationErrorsList errors;

        typeDO = (TestMethodSampleTypeVO)testMethodSampleType.getSelectedItem().getData();
        accessionNum = accessionNumber.getValue();
        tubeNum = tubeNumber.getValue();
        tsVDO = (TestSectionViewDO)sectionId.getSelectedItem().getData();

        try {
            smRowCount = managers.get(accessionNum);
            sampleMan = smRowCount.sampleManager;

            siVDO = getSampleItemForType(typeDO, sampleMan);
            siVDO.setContainerReference(tubeNum);
            
            if (typeDO.getTestId() != null)
                requestVO = new SampleTestRequestVO(sampleMan.getSample().getId(),
                                                    siVDO.getId(),
                                                    typeDO.getTestId(),
                                                    null,
                                                    tsVDO.getSectionId(),
                                                    null,
                                                    null,
                                                    false,
                                                    null);
            else
                requestVO = new SampleTestRequestVO(sampleMan.getSample().getId(),
                                                    siVDO.getId(),
                                                    null,
                                                    null,
                                                    tsVDO.getSectionId(),
                                                    null,
                                                    typeDO.getPanelId(),
                                                    false,
                                                    null);

            returnVO = SampleService1.get().addAnalysis(sampleMan, requestVO);
            smRowCount.sampleManager = returnVO.getManager();
            
            errors = new ValidationErrorsList();
            validateAuxDataAndResults(smRowCount.sampleManager, errors);
            if (errors.size() > 0) {
                if (returnVO.getErrors() != null) {
                    for (Exception e : errors.getErrorList())
                        returnVO.getErrors().add(e);
                } else {
                    returnVO.setErrors(errors);
                }
            }
            showErrorsOrTests(returnVO);
            if (!isBusy)
                fireDataChange();
        } catch (Exception e) {
            Window.alert("rowAdded: " + e.getMessage());
        }
    }
    
    /**
     * Shows the errors in the VO or the popup for selecting the prep/reflex
     * tests for the analyses in the VO because they were added/changed in the
     * back-end.
     */
    private void showErrorsOrTests(final SampleTestReturnVO ret) {
        ModalWindow modal;

        if (ret == null)
            return;

        if (ret.getErrors() != null && ret.getErrors().size() > 0) {
            showErrors(ret.getErrors());
        } else if (ret.getTests() != null && ret.getTests().size() > 0) {
            /*
             * show the pop for selecting prep/reflex tests
             */
            if (testSelectionLookup == null) {
                testSelectionLookup = new TestSelectionLookupUI() {
                    @Override
                    public TestManager getTestManager(Integer testId) {
                        return screen.get(testId, TestManager.class);
                    }

                    @Override
                    public void ok() {
                        ArrayList<SampleTestRequestVO> tests;

                        isBusy = false;
                        tests = testSelectionLookup.getSelectedTests();
                        /*
                         * keep isBusy to be true if some tests were selected on
                         * the popup because they need to be added to the
                         * manager
                         */
                        if (tests != null && tests.size() > 0)
                            addAdditionalAnalyses(ret.getManager(), tests);
                        /*
                         * to set the focus back on the entry widget, a command must be scheduled
                         */
                        Scheduler.get().scheduleDeferred(focusEntryCmd);
                    }
                };
            }

            /*
             * delay refresh of the table until we have added all tests
             */
            isBusy = true;

            modal = new ModalWindow();
            modal.setSize("520px", "350px");
            modal.setName(Messages.get().testSelection_prepTestSelection());
            modal.setCSS(UIResources.INSTANCE.popupWindow());
            modal.setContent(testSelectionLookup);

            testSelectionLookup.setData(ret.getManager(), ret.getTests());
            testSelectionLookup.setWindow(modal);
        }
    }

    private void addAdditionalAnalyses(SampleManager1 man, ArrayList<SampleTestRequestVO> tests) {
        SampleManagerRowCount smRowCount;
        SampleTestReturnVO returnVO;
        ValidationErrorsList errors;

        try {
            returnVO = SampleService1.get().addAnalyses(man, tests);
            smRowCount = managers.get(returnVO.getManager().getSample().getAccessionNumber());
            smRowCount.sampleManager = returnVO.getManager();

            errors = new ValidationErrorsList();
            validateAuxDataAndResults(smRowCount.sampleManager, errors);
            if (errors.size() > 0) {
                if (returnVO.getErrors() != null) {
                    for (Exception e : errors.getErrorList())
                        returnVO.getErrors().add(e);
                } else {
                    returnVO.setErrors(errors);
                }
            }
            showErrorsOrTests(returnVO);
            if (!isBusy)
                fireDataChange();
        } catch (Exception e) {
            Window.alert("rowAdded: " + e.getMessage());
        }
    }
    
    private SampleItemViewDO getSampleItemForType(TestMethodSampleTypeVO typeDO, SampleManager1 man) {
        int i;
        SampleItemViewDO itemDO;

        for (i = 0; i < man.item.count(); i++) {
            itemDO = man.item.get(i);
            if (typeDO.getSampleTypeId().equals(itemDO.getTypeOfSampleId()))
                return itemDO;
        }
        
        itemDO = man.item.add();
        itemDO.setTypeOfSampleId(typeDO.getSampleTypeId());
        itemDO.setTypeOfSample(typeDO.getSampleType());
        return itemDO;
    }
    
    private ArrayList<Row> getTableModel() {
        int i, j, count;
        AnalysisViewDO aVDO;
        ArrayList<Row> model;
        Row row;
        SampleDO sDO;
        SampleItemViewDO siVDO;
        SampleManager1 man;
        
        model = new ArrayList<Row>();
        for (SampleManagerRowCount smRC : managers.values()) {
            count = 0;
            man = smRC.sampleManager;
            sDO = man.getSample();
            for (i = 0; i < man.item.count(); i++) {
                siVDO = man.item.get(i);
                for (j = 0; j < man.analysis.count(siVDO); j ++) {
                    aVDO = man.analysis.get(siVDO, j);
                    if (aVDO.getId() < 0) {
                        row = new Row(6);
                        row.setCell(0, sDO.getAccessionNumber());
                        row.setCell(1, sDO.getReceivedDate());
                        row.setCell(2, aVDO.getTestName());
                        row.setCell(3, aVDO.getMethodName());
                        row.setCell(4, siVDO.getTypeOfSample());
                        if (Constants.domain().HUMAN.equals(sDO.getDomain()))
                            row.setCell(5, sDO.getClientReference());
                        row.setData(aVDO);
                        model.add(row);
                        count++;
                    }
                }
            }
            smRC.count = count;
        }
        
        commit.setEnabled(model.size() > 0);

        return model;
    }

    private boolean validateFields() {
        // received date needs filled out
        if (receivedDate.getValue() == null) {
            window.setError(Messages.get().receivedDateNoValueException());
            return false;
        }

        // received by needs filled out
        if (receivedBy.getValue() == null) {
            window.setError(Messages.get().receivedByNoValueException());
            return false;
        }

        // test needs filled out
        if (testMethodSampleType.getValue() == null) {
            window.setError(Messages.get().testMethodNoValueException());
            return false;
        }

        // test needs filled out
        if (sectionId.getValue() == null) {
            window.setError(Messages.get().testSectionNoValueException());
            return false;
        }

        return true;
    }
    
    private void validateAuxDataAndResults(SampleManager1 man, ValidationErrorsList errors) {
        int i, j, k, l;
        AnalysisViewDO aVDO;
        AuxDataViewDO adVDO;
        AuxFieldGroupManager afgMan;
        Integer groupId;
        ResultFormatter rf;
        ResultViewDO rVDO;
        SampleItemViewDO siVDO;
        TestManager tMan;

        groupId = null;
        rf = null;
        for (i = 0; i < man.auxData.count(); i++) {
            adVDO = man.auxData.get(i);
            if (adVDO.getValue() != null && adVDO.getTypeId() == null) {
                try {
                    if (!adVDO.getGroupId().equals(groupId)) {
                        afgMan = get(adVDO.getGroupId(), AuxFieldGroupManager.class);
                        rf = afgMan.getFormatter();
                        groupId = adVDO.getGroupId();
                    }
                    ResultHelper.formatValue(adVDO, adVDO.getValue(), rf);
                } catch (Exception anyE) {
                    errors.add(new Exception(Messages.get().aux_defaultValueInvalidException(man.getSample().getAccessionNumber(),
                                                                                             adVDO.getAnalyteName(),
                                                                                             adVDO.getValue())));
                    logger.log(Level.SEVERE, anyE.getMessage(), anyE);
                }
            }
        }

        for (i = 0; i < man.item.count(); i++) {
            siVDO = man.item.get(i);
            for (j = 0; j < man.analysis.count(siVDO); j++) {
                aVDO = man.analysis.get(siVDO, j);
                tMan = screen.get(aVDO.getTestId(), TestManager.class);
                try {
                    rf = tMan.getFormatter();
                    for (k = 0; k < man.result.count(aVDO); k++) {
                        for (l = 0; l < man.result.count(aVDO, k); l++) {
                            rVDO = man.result.get(aVDO, k, l);
                            if (rVDO.getValue() != null && rVDO.getTypeId() == null) {
                                try {
                                    ResultHelper.formatValue(rVDO,
                                                             rVDO.getValue(),
                                                             aVDO.getUnitOfMeasureId(),
                                                             rf);
                                } catch (Exception anyE1) {
                                    errors.add(new Exception(Messages.get().result_defaultValueInvalidException(man.getSample().getAccessionNumber(),
                                                                                                                aVDO.getTestName(),
                                                                                                                aVDO.getMethodName(),
                                                                                                                rVDO.getAnalyte(),
                                                                                                                rVDO.getValue())));
                                    logger.log(Level.SEVERE, anyE1.getMessage(), anyE1);
                                }
                            }
                        }
                    }
                } catch (Exception anyE) {
                    errors.add(anyE);
                    logger.log(Level.SEVERE, anyE.getMessage(), anyE);
                }
            }
        }
    }

    private void updateRecievedDate() {
        CalendarService.get().getCurrentDatetime(Datetime.YEAR, Datetime.MINUTE, new AsyncCallback<Datetime>() {
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

    private class SampleManagerRowCount {
        private int count;
        private SampleManager1 sampleManager;

        public SampleManagerRowCount(SampleManager1 man, int count) {
            this.sampleManager = man;
            this.count = count;
        }
    }
}