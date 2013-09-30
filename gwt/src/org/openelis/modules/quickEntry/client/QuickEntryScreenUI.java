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
import static org.openelis.ui.screen.State.*;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

import org.openelis.cache.CategoryCache;
import org.openelis.cache.DictionaryCache;
import org.openelis.cache.UserCache;
import org.openelis.cache.UserCacheService;
import org.openelis.constants.Messages;
import org.openelis.domain.Constants;
import org.openelis.domain.DataObject;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.IdVO;
import org.openelis.domain.InstrumentViewDO;
import org.openelis.domain.QcAnalyteViewDO;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.domain.SampleTestRequestVO;
import org.openelis.domain.SampleTestReturnVO;
import org.openelis.domain.TestAnalyteViewDO;
import org.openelis.domain.TestMethodSampleTypeVO;
import org.openelis.domain.TestSectionViewDO;
import org.openelis.domain.WorksheetAnalysisDO;
import org.openelis.domain.WorksheetAnalysisViewDO;
import org.openelis.domain.WorksheetItemDO;
import org.openelis.domain.WorksheetViewDO;
import org.openelis.gwt.widget.DateField;
import org.openelis.manager.AnalysisResultManager;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.WorksheetManager1;
import org.openelis.manager.WorksheetManager1.Load;
import org.openelis.meta.SampleMeta;
import org.openelis.meta.WorksheetBuilderMeta;
import org.openelis.modules.history.client.HistoryScreen;
import org.openelis.modules.instrument.client.InstrumentService;
import org.openelis.modules.main.client.OpenELIS;
import org.openelis.modules.panel.client.PanelService;
import org.openelis.modules.qc.client.QcService;
import org.openelis.modules.result.client.ResultService;
import org.openelis.modules.sample.client.SampleService;
import org.openelis.modules.sample1.client.SampleService1;
import org.openelis.modules.test.client.TestService;
import org.openelis.modules.worksheet1.client.WorksheetLookupScreenUI;
import org.openelis.modules.worksheet1.client.WorksheetNotesTabUI;
import org.openelis.modules.worksheet1.client.WorksheetService1;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.FieldErrorException;
import org.openelis.ui.common.FormErrorException;
import org.openelis.ui.common.LastPageException;
import org.openelis.ui.common.ModulePermission;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.PermissionException;
import org.openelis.ui.common.SystemUserVO;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.event.ActionEvent;
import org.openelis.ui.event.ActionHandler;
import org.openelis.ui.event.BeforeCloseEvent;
import org.openelis.ui.event.BeforeCloseHandler;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.GetMatchesEvent;
import org.openelis.ui.event.GetMatchesHandler;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.ScreenNavigator;
import org.openelis.ui.services.CalendarService;
import org.openelis.ui.widget.AutoComplete;
import org.openelis.ui.widget.AutoCompleteValue;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.CheckBox;
import org.openelis.ui.widget.Confirm;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.Menu;
import org.openelis.ui.widget.MenuItem;
import org.openelis.ui.widget.ModalWindow;
import org.openelis.ui.widget.QueryFieldUtil;
import org.openelis.ui.widget.TabLayoutPanel;
import org.openelis.ui.widget.TextBox;
import org.openelis.ui.widget.WindowInt;
import org.openelis.ui.widget.calendar.Calendar;
import org.openelis.ui.widget.table.Row;
import org.openelis.ui.widget.table.Table;
import org.openelis.ui.widget.table.event.BeforeCellEditedEvent;
import org.openelis.ui.widget.table.event.BeforeCellEditedHandler;
import org.openelis.ui.widget.table.event.CellEditedEvent;
import org.openelis.ui.widget.table.event.CellEditedHandler;
import org.openelis.utils.ReportUtil;

public class QuickEntryScreenUI extends Screen {

    @UiTemplate("QuickEntry.ui.xml")
    interface QuickEntryUiBinder extends UiBinder<Widget, QuickEntryScreenUI> {
    };
    
    private static QuickEntryUiBinder uiBinder = GWT.create(QuickEntryUiBinder.class);

    private boolean                                        close;
    private Datetime                                       todaysDate;
    private HashMap<Integer, ArrayList<TestSectionViewDO>> testSectionHash;
    private HashMap<Integer, SampleManagerRowCount>        managers;
    private ModulePermission                               userPermission;

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

    protected Confirm                                      windowCloseConfirm,
                                                           receivedDateNotTodayConfirm;
    
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

        addShortcut(commit, 'm', CTRL);
        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                commit.setEnabled(false);
                entry.setEnabled(true);
                accessionNumber.setEnabled(false);
                tubeNumber.setEnabled(false);
                receivedDate.setEnabled(true);
                testMethodSampleType.setEnabled(true);
                sectionId.setEnabled(true);
                currentDateTime.setEnabled(true);
                printLabels.setEnabled(true);
                printer.setEnabled(true);
                quickEntryTable.setEnabled(true);
                quickEntryTable.setAllowMultipleSelection(true);
                removeRowButton.setEnabled(false);
            }
        });

        addScreenHandler(entry, "entry", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                entry.setValue(null);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                entryChanged();
            }

            public Widget onTab(boolean forward) {
                return forward ? entry : entry;
            }
        });

        addScreenHandler(accessionNumber, SampleMeta.getAccessionNumber(), new ScreenHandler<Integer>() {
            public Widget onTab(boolean forward) {
                return forward ? entry : entry;
            }
        });
            
        addScreenHandler(tubeNumber, "tubeNumber", new ScreenHandler<String>() {
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

            public Widget onTab(boolean forward) {
                return forward ? entry : entry;
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
            }

            public Widget onTab(boolean forward) {
                return forward ? entry : entry;
            }
        });

        addScreenHandler(sectionId, SampleMeta.getAnalysisSectionId(), new ScreenHandler<Integer>() {
            public Widget onTab(boolean forward) {
                return forward ? entry : entry;
            }
        });
            
        addScreenHandler(currentDateTime, "currentDateTime", new ScreenHandler<String>() {
            public void onValueChange(ValueChangeEvent<String> event) {
                if ("Y".equals(event.getValue()))
                    updateRecievedDate();
            }

            public Widget onTab(boolean forward) {
                return forward ? entry : entry;
            }
        });

        addScreenHandler(printLabels, "printLabels", new ScreenHandler<String>() {
            public Widget onTab(boolean forward) {
                return forward ? entry : entry;
            }
        });

        addScreenHandler(printer, "printer", new ScreenHandler<Integer>() {
            public Widget onTab(boolean forward) {
                return forward ? entry : entry;
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
                                        typeDO.getTest(), typeDO.getMethod(), typeDO.getSampleType());
                item.setData(typeDO);
            } else {
                item = new Item<String>("P" + typeDO.getPanelId() + "-" + typeDO.getSampleTypeId(),
                                        typeDO.getPanel(), null, typeDO.getSampleType());
                item.setData(typeDO);
            }
            model.add(item);
        }
        testMethodSampleType.setModel(model);

        setState(DEFAULT);
        fireDataChange();

        logger.fine("Quick Entry Screen Opened");
    }
    
    /*
     * basic button methods
     */
    @SuppressWarnings("unused")
    @UiHandler("commit")
    protected void commit(ClickEvent event) {
        commit();
    }
    
    private void commit() {
//        int i;
//        ArrayList<Integer> removables;
//        Item item;
//        Iterator<Item> itr;
//        SampleManager manager;
//        ValidationErrorsList errorsList;
//
//        finishEditing();
//        clearErrors();
//        
//        window.setBusy(Messages.get().adding());
//
//        itr = managers.values().iterator();
//        errorsList = new ValidationErrorsList();
//        manager = null;
//        removables = new ArrayList<Integer>();
//        while (itr.hasNext()) {
//            try {
//                item = itr.next();
//                manager = item.sampleManager;
//                manager.getSample().setReceivedById(UserCache.getPermission()
//                                                             .getSystemUserId());
//                manager.getSample()
//                       .setStatusId(Constants.dictionary().SAMPLE_NOT_VERIFIED);
//
//                if (manager.getSample().getId() == null)
//                    manager.add();
//                else
//                    manager.update();
//
//                removables.add(manager.getSample().getAccessionNumber());
//                item.count = 0;
//            } catch (ValidationErrorsList e) {
//                errorsList.add(new FormErrorException(Messages.get().quickCommitError()));
//                for (i = 0; i < e.size(); i++)
//                    errorsList.add(new FormErrorException(Messages.get().rowError(
//                                                          manager.getSample()
//                                                                 .getAccessionNumber()
//                                                                 .toString(),
//                                                          e.getErrorList()
//                                                           .get(i)
//                                                           .getLocalizedMessage())));
//            } catch (Exception e) {
//                errorsList.add(new FormErrorException(Messages.get().quickCommitError()));
//                errorsList.add(new FormErrorException(Messages.get().rowError(
//                                                      manager.getSample()
//                                                             .getAccessionNumber()
//                                                             .toString(),
//                                                      e.getMessage())));
//            }
//        }
//        
//        for (i = 0; i < removables.size(); i++)
//            managers.remove(removables.get(i));
//
//        if (errorsList.size() > 0) {
//            showErrors(errorsList);
//        } else {
//            setState(Screen.State.DEFAULT);
//            window.setDone(Messages.get().addingComplete());
//        }
//
//        DataChangeEvent.fire(this);
    }
        
    private void entryChanged() {
        int index;
        final Datetime recDate;
        Exception ex;
        Integer accessionNum;
        SampleManager1 sampleMan;
        SampleManagerRowCount smRowCount;
        String val;

        val = entry.getValue();
        window.clearStatus();

        // date received
        recDate = ReportUtil.getDatetime(val);
        if (recDate != null) {
            if (todaysDate.after(recDate)) {
                ex = new Exception(Messages.get().receivedDateNotTodayExceptionBody(
                                            ReportUtil.toString(recDate, Messages.get().dateTimePattern())));
                receivedDateNotTodayConfirm = new Confirm(Confirm.Type.QUESTION,
                                                          Messages.get().receivedDateNotTodayExceptionTitle(),
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
                                receivedDate.setValue(recDate);
                                break;
                        }
                    }
                });
                receivedDateNotTodayConfirm.show();
            } else {
                receivedDate.setValue(recDate);
            }
        } else if (val.matches("[TP][0-9]*\\-[0-9]*")) {    // test & panel
            try {
                testMethodSampleType.setValue(val, true);
            } catch (Exception e) {
                ex = new Exception(Messages.get().invalidEntryException(val));
                window.setError(ex.getMessage());
            }
        } else if (val.matches("[a-zA-Z]{3}[0-9]{3}")) {    // tube #
            tubeNumber.setValue(val);
        } else if (val.matches("NEW")) {                    // new accession #
//            if (validateFields()) {
//                if (accNumUtil == null)
//                    accNumUtil = new AccessionNumberUtility();
//
//                try {
//                    accessionNum = accNumUtil.getNewAccessionNumber();
//                    accessionNumber.setFieldValue(accessionNum);
//                    addAnalysisRow();
//                } catch (ValidationErrorsList e) {
//                    showErrors(e);
//                } catch (Exception e) {
//                    Window.alert(e.getMessage());
//                }
//            }
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
                    smRowCount = managers.get(val);
                    if (smRowCount == null) {
                        sampleMan = SampleService1.get().getInstance(Constants.domain().QUICKENTRY);
                        SampleService1.get().setAccessionNumber(sampleMan, accessionNum);
                        managers.put(sampleMan.getSample().getAccessionNumber(),
                                     new SampleManagerRowCount(sampleMan, 0));
                    } else {
                        sampleMan = smRowCount.sampleManager;
                    }
                    accessionNumber.setValue(accessionNum);
                    addAnalysisRow();
                } catch (NumberFormatException e) {
                    ex = new Exception(Messages.get().invalidEntryException(val));
                    window.setError(ex.getMessage());
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
        entry.setValue(null);
        entry.setFocus(true);
    }

    private void addAnalysisRow() {
        Integer accessionNum;
        SampleItemViewDO siVDO;
        SampleManagerRowCount smRowCount;
        SampleManager1 sampleMan;
        SampleTestRequestVO requestVO;
        SampleTestReturnVO returnVO;
        String tubeNum;
        TestMethodSampleTypeVO typeDO;
        TestSectionViewDO tsVDO;

        typeDO = (TestMethodSampleTypeVO)testMethodSampleType.getSelectedItem().getData();
        accessionNum = Integer.valueOf(accessionNumber.getValue());
        tubeNum = tubeNumber.getValue();
        tsVDO = (TestSectionViewDO)sectionId.getSelectedItem().getData();

        try {
            smRowCount = managers.get(accessionNum);
            sampleMan = smRowCount.sampleManager;
            sampleMan.getSample().setReceivedDate(receivedDate.getValue());
            smRowCount.count++;

            siVDO = sampleMan.item.add();
            siVDO.setContainerReference(tubeNum);
            siVDO.setTypeOfSampleId(typeDO.getSampleTypeId());
            siVDO.setTypeOfSample(typeDO.getSampleType());
            
            if (typeDO.getTestId() != null)
                requestVO = new SampleTestRequestVO(siVDO.getId(),
                                                typeDO.getTestId(),
                                                null,
                                                null,
                                                null,
                                                null,
                                                false,
                                                null);
            else
                requestVO = new SampleTestRequestVO(siVDO.getId(),
                                                null,
                                                null,
                                                null,
                                                null,
                                                typeDO.getPanelId(),
                                                false,
                                                null);

            returnVO = SampleService1.get().addTest(sampleMan, requestVO);
            smRowCount.sampleManager = returnVO.getManager();
        } catch (Exception e) {
            Window.alert("rowAdded: " + e.getMessage());
        }
    }

    private boolean validateFields() {
        // received date needs filled out
        if (receivedDate.getValue() == null) {
            window.setError(Messages.get().receivedDateNoValueException());
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
        private SampleManager1 sampleManager;
        private int            count;

        public SampleManagerRowCount(SampleManager1 man, int count) {
            this.sampleManager = man;
            this.count = count;
        }
    }
}