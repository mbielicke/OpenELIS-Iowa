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
package org.openelis.modules.report.secondaryLabel.client;

import static org.openelis.modules.main.client.Logger.logger;
import static org.openelis.ui.screen.Screen.Validation.Status.VALID;
import static org.openelis.ui.screen.State.DEFAULT;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

import org.openelis.cache.UserCache;
import org.openelis.constants.Messages;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.SecondaryLabelVO;
import org.openelis.domain.TestViewDO;
import org.openelis.manager.SampleManager1;
import org.openelis.modules.preferences1.client.PreferencesService1Impl;
import org.openelis.modules.preferences1.client.PrinterService1Impl;
import org.openelis.modules.sample1.client.SampleService1;
import org.openelis.modules.test.client.TestService;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.ModulePermission;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.OptionListItem;
import org.openelis.ui.common.PermissionException;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.Label;
import org.openelis.ui.widget.TextBox;
import org.openelis.ui.widget.WindowInt;
import org.openelis.ui.widget.table.Row;
import org.openelis.ui.widget.table.Table;
import org.openelis.ui.widget.table.event.CellEditedEvent;
import org.openelis.ui.widget.table.event.CellEditedHandler;
import org.openelis.ui.widget.table.event.RowAddedEvent;
import org.openelis.ui.widget.table.event.RowAddedHandler;
import org.openelis.ui.widget.table.event.RowDeletedEvent;
import org.openelis.ui.widget.table.event.RowDeletedHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

public class SecondaryLabelScreenUI extends Screen {

    @UiTemplate("SecondaryLabel.ui.xml")
    interface SecondaryLabelUiBinder extends UiBinder<Widget, SecondaryLabelScreenUI> {
    };

    public static final SecondaryLabelUiBinder uiBinder = GWT.create(SecondaryLabelUiBinder.class);

    @UiField
    protected TextBox<String>                  entry;

    @UiField
    protected Label<Integer>                   accessionNumber;

    @UiField
    protected Dropdown<Integer>                testName;

    @UiField
    protected Dropdown<String>                 printer;

    @UiField
    protected Button                           removeRowButton, printButton, add1Button, set1Button,
                                               remove1Button;

    @UiField
    protected Table                            table;

    protected SecondaryLabelReportScreen       secondaryLabelReportScreen;

    protected String                           defaultPrinter;

    protected HashMap<Integer, SampleManager1> managers;

    public SecondaryLabelScreenUI(WindowInt window) throws Exception {
        ModulePermission userPermission;
        
        setWindow(window);

        userPermission = UserCache.getPermission().getModule("sampletracking");
        if (userPermission == null)
            throw new PermissionException(Messages.get()
                                                  .screenPermException("Secondary Label Screen"));

        initWidget(uiBinder.createAndBindUi(this));

        initialize();
        setState(DEFAULT);
        fireDataChange();
        /*
         * the following is used instead of a ScheduledCommand to make sure
         * that the focus gets set after the widget gets attached to the DOM,
         * which ScheduledCommand doesn't do, as it executes after the creation
         * of the widget, which doesn't mean that the widget is attached
         */
        Scheduler.get().scheduleIncremental(new Scheduler.RepeatingCommand() {
            @Override
            public boolean execute() {
                if (entry.isAttached()) {
                    entry.setFocus(true);
                    return false;
                }
                return true;
            }
        });

        logger.fine("Secondary Label Screen Opened");
    }

    protected void initialize() {
        Item<Integer> row;
        Item<String> srow;
        ArrayList<Item<Integer>> model;
        ArrayList<Item<String>> smodel;

        /*
         * screen fields and widgets
         */
        addScreenHandler(entry, "entry", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                entry.setValue(null);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                ScheduledCommand cmd;

                entryChanged(event.getValue());

                entry.setValue(null);
                cmd = new ScheduledCommand() {
                    @Override
                    public void execute() {
                        entry.setFocus(true);
                    }
                };
                Scheduler.get().scheduleDeferred(cmd);
            }

            public void onStateChange(StateChangeEvent event) {
                entry.setEnabled(true);
            }

            public Widget onTab(boolean forward) {
                setFocus(false);
                return forward ? entry : entry;
            }
        });

        addScreenHandler(accessionNumber, "accessionNumber", new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                accessionNumber.setValue(null);
            }
        });

        addScreenHandler(testName, "testName", new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                testName.setValue(null);
            }

            public void onStateChange(StateChangeEvent event) {
                testName.setEnabled(true);
            }

            public Widget onTab(boolean forward) {
                return forward ? printer : entry;
            }
        });

        addScreenHandler(printer, "printer", new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                printer.setValue(defaultPrinter);
            }

            public void onStateChange(StateChangeEvent event) {
                printer.setEnabled(true);
            }

            public Widget onTab(boolean forward) {
                return forward ? table : testName;
            }
        });

        addScreenHandler(table, "table", new ScreenHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent event) {
                table.setModel(null);
            }

            public void onStateChange(StateChangeEvent event) {
                table.setEnabled(true);
            }

            public Widget onTab(boolean forward) {
                return forward ? removeRowButton : printer;
            }
        });
        
        table.addCellEditedHandler(new CellEditedHandler() {
            @Override
            public void onCellUpdated(CellEditedEvent event) {
                int r;
                Object val;
                Integer n;
                SecondaryLabelVO data;
                                
                if (event.getCol() == 3) {
                    r = event.getRow();
                    data = table.getRowAt(r).getData();
                    val = table.getValueAt(r,  3);
                    /*
                     * if the user entered an invalid number, the value in
                     * the cell is a String and not an Integer
                     */
                    n = null;  
                    if (val instanceof Integer)
                        n = (Integer)val;
                    data.setLabelQty(n);
                    table.clearExceptions(r,  3);
                    if (n == null || n < 1)
                        table.addException(r, 3, new Exception(Messages.get().secondaryLabel_invalidQtyException()));
                }
            }
        });

        table.addSelectionHandler(new SelectionHandler<Integer>() {
            @Override
            public void onSelection(SelectionEvent<Integer> event) {
                removeRowButton.setEnabled(true);
            }
        });

        table.addRowAddedHandler(new RowAddedHandler() {
            @Override
            public void onRowAdded(RowAddedEvent event) {
                table.selectRowAt(event.getIndex());
                removeRowButton.setEnabled(true);
            }
        });

        table.addRowDeletedHandler(new RowDeletedHandler() {
            @Override
            public void onRowDeleted(RowDeletedEvent event) {
                removeRowButton.setEnabled(false);
            }
        });

        addScreenHandler(removeRowButton, "removeRowButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                removeRowButton.setEnabled(false);
            }

            public Widget onTab(boolean forward) {
                return forward ? printButton : table;
            }
        });

        addScreenHandler(printButton, "printButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                printButton.setEnabled(true);
            }

            public Widget onTab(boolean forward) {
                return forward ? add1Button : removeRowButton;
            }
        });

        addScreenHandler(add1Button, "add1Button", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                add1Button.setEnabled(true);
            }

            public Widget onTab(boolean forward) {
                return forward ? set1Button : printButton;
            }
        });

        addScreenHandler(set1Button, "set1Button", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                set1Button.setEnabled(true);
            }

            public Widget onTab(boolean forward) {
                return forward ? remove1Button : add1Button;
            }
        });

        addScreenHandler(remove1Button, "remove1Button", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                remove1Button.setEnabled(true);
            }

            public Widget onTab(boolean forward) {
                return forward ? entry : set1Button;
            }
        });

        /*
         * load models in the dropdowns
         */
        model = new ArrayList<Item<Integer>>();
        try {
            for (TestViewDO t : TestService.get().fetchList()) {
                /*
                 * show active tests that have a label format defined and label
                 * quantity of at least one
                 */
                if ("Y".equals(t.getIsActive()) && t.getLabelId() != null &&
                    t.getLabelQty() != null && t.getLabelQty() > 0) {
                    row = new Item<Integer>(t.getId(),
                                            DataBaseUtil.concatWithSeparator(t.getName(),
                                                                             ", ",
                                                                             t.getMethodName()));
                    row.setData(t);
                    model.add(row);
                }
            }
            testName.setModel(model);

            defaultPrinter = PreferencesService1Impl.INSTANCE.userRoot().get("default_bar_code_printer", null);

            /*
             * show barcode printers
             */
            smodel = new ArrayList<Item<String>>();
            for (OptionListItem i : PrinterService1Impl.INSTANCE.getPrinters("zpl")) {
                srow = new Item<String>(i.getKey(), i.getLabel());
                smodel.add(srow);
            }
            printer.setModel(smodel);
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            Window.alert(e.getMessage());
        }
    }

    @UiHandler("removeRowButton")
    protected void removeRow(ClickEvent event) {
        int r;

        r = table.getSelectedRow();
        if (r > -1 && table.getRowCount() > 0)
            table.removeRowAt(r);
    }

    @UiHandler("printButton")
    protected void print(ClickEvent event) {
        Validation validation;
        SecondaryLabelVO data;
        ArrayList<SecondaryLabelVO> labels;

        finishEditing();

        validation = validate();
        if (validation.getStatus() != VALID) {
            setError(Messages.get().secondaryLabel_correctErrorsPrint());
            return;
        }

        if (table.getRowCount() == 0) {
            setError(Messages.get().secondaryLabel_noLabelsToPrintException());
            return;
        }

        /*
         * make a list of all labels to be printed; also, set the printer
         */
        labels = new ArrayList<SecondaryLabelVO>();
        for (Row row : table.getModel()) {
            data = row.getData();
            data.setPrinter(printer.getValue());
            labels.add(data);
        }

        /*
         * print the labels
         */
        try {
            if (secondaryLabelReportScreen == null)
                secondaryLabelReportScreen = new SecondaryLabelReportScreen(window);

            /*
             * clear the table to make sure that the user doesn't accidentally
             * print the same labels again
             */
            table.setModel(null);
            secondaryLabelReportScreen.runReport(labels);
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @UiHandler("add1Button")
    protected void add1(ClickEvent event) {
        setLabelQty(1);
    }

    @UiHandler("set1Button")
    protected void set1(ClickEvent event) {
        setLabelQty(0);
    }

    @UiHandler("remove1Button")
    protected void remove1(ClickEvent event) {
        setLabelQty(-1);
    }

    private void setLabelQty(int numberFlag) {
        Integer n;
        SecondaryLabelVO data;
        
        finishEditing();
        table.clearExceptions();
        for (int i = 0; i < table.getRowCount(); i++) {
            data = table.getRowAt(i).getData();
            n = data.getLabelQty();
            if (numberFlag == 0)
                n = 1;
            else if (n != null) 
                n += numberFlag;
            data.setLabelQty(n);
            table.setValueAt(i, 3, n);
            if (n != null && n < 1)
                table.addException(i, 3, new Exception(Messages.get().secondaryLabel_invalidQtyException()));
        }
    }

    private void entryChanged(String value) {
        int i, index;
        boolean found;
        Integer accNum;
        TestViewDO test;
        SampleManager1 sm;
        AnalysisViewDO ana;
        SecondaryLabelVO data;
        Item<Integer> item;
        Row row;

        if (value == null)
            return;

        try {
            if (value.matches("[0-9]+") || value.matches("[0-9]+-[0-9]+")) {
                /*
                 * this is a sample or sample item label; a test must be
                 * selected before any further processing
                 */
                item = testName.getSelectedItem();
                if (item == null) {
                    setError(Messages.get().secondaryLabel_pleaseSelectTest());
                    entry.setValue(null);
                    return;
                }

                /*
                 * trim the sample item id from the end of the bar coded
                 * accession number
                 */
                index = value.indexOf("-");
                if (index != -1)
                    value = value.substring(0, index);

                accNum = Integer.valueOf(value);
                if (managers == null)
                    managers = new HashMap<Integer, SampleManager1>();

                sm = managers.get(accNum);
                if (sm == null) {
                    setBusy(Messages.get().gen_fetching());
                    sm = SampleService1.get().fetchByAccession(accNum);
                    managers.put(accNum, sm);
                    clearStatus();
                }
                /*
                 * find out if the scanned-in sample has the selected test and
                 * also if the analysis for the test is non-cancelled; show an
                 * error if such an analysis is not found
                 */
                found = false;
                test = item.getData();
                ana = null;
                for (i = 0; i < sm.analysis.count(); i++ ) {
                    ana = sm.analysis.get(i);
                    if (ana.getTestId().equals(test.getId()) &&
                        !Constants.dictionary().ANALYSIS_CANCELLED.equals(ana.getStatusId())) {
                        found = true;
                        break;
                    }
                }

                if ( !found) {
                    setError(Messages.get().secondaryLabel_testNotOnSampleException());
                    entry.setValue(null);
                    return;
                }

                data = new SecondaryLabelVO();
                data.setSampleId(sm.getSample().getId());
                data.setAnalysisId(ana.getId());
                data.setLabelQty(test.getLabelQty());

                accessionNumber.setValue(accNum);

                /*
                 * add the row for this accession number to the table
                 */
                row = new Row(4);
                row.setCell(0, accNum);
                row.setCell(1, test.getName());
                row.setCell(2, test.getMethodName());
                row.setCell(3, test.getLabelQty());
                row.setData(data);
                table.addRow(row);
            } else if (value.matches("[T][0-9]*\\-[0-9]*")) {
                /*
                 * this is a test label; get the test id and set it in the
                 * dropdown
                 */
                index = value.indexOf("-");
                value = value.substring(1, index);
                testName.setValue(Integer.valueOf(value));
            } else {
                window.setError(Messages.get().sample_invalidEntryException(value));
            }
        } catch (NumberFormatException e) {
            setError(Messages.get().sample_invalidEntryException(value));
        } catch (NotFoundException e) {
            setError(Messages.get().gen_noRecordsFound());
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            Window.alert(e.getMessage());
            clearStatus();
        }
    }
}