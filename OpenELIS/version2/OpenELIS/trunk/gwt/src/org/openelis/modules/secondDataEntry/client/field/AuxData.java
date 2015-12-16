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
package org.openelis.modules.secondDataEntry.client.field;

import static org.openelis.modules.main.client.Logger.*;
import static org.openelis.ui.screen.State.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

import org.openelis.domain.AuxDataViewDO;
import org.openelis.domain.Constants;
import org.openelis.exception.ParseException;
import org.openelis.manager.AuxFieldGroupManager;
import org.openelis.manager.SampleManager1;
import org.openelis.modules.main.client.resources.OpenELISResources;
import org.openelis.modules.sample1.client.ResultCell;
import org.openelis.modules.sample1.client.ResultCell.Value;
import org.openelis.modules.secondDataEntry.client.VerificationScreen;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.TextBase;
import org.openelis.ui.widget.TextBox;
import org.openelis.ui.widget.table.Row;
import org.openelis.ui.widget.table.Table;
import org.openelis.ui.widget.table.event.BeforeCellEditedEvent;
import org.openelis.ui.widget.table.event.BeforeCellEditedHandler;
import org.openelis.ui.widget.table.event.CellEditedEvent;
import org.openelis.ui.widget.table.event.CellEditedHandler;
import org.openelis.utilcommon.ResultFormatter;
import org.openelis.utilcommon.ResultFormatter.FormattedValue;
import org.openelis.utilcommon.ResultHelper;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

/**
 * This class manages the widgets used for verifying aux data
 */
public class AuxData extends MultiField<Table> {
    protected HashMap<String, ArrayList<Item<Integer>>> dictionaryModel;
    protected int                                       row;

    public AuxData(VerificationScreen parentScreen, TableRowElement tableRowElement,
                   Table editableWidget, int rowIndex) {
        super(parentScreen, tableRowElement, editableWidget, rowIndex);
        init();
    }

    /**
     * Makes the row in which the widgets are shown, visible and sets its style;
     * adds handlers to the widgets in the row
     */
    protected void init() {
        ScheduledCommand cmd;

        setRowVisible();

        /*
         * this is done so that the table gets resized to show the headers and
         * rows after it's made visible; it won't get resized otherwise because
         * it's in a LayoutPanel, and that panel is inside a <td> and not
         * another panel; so the browser's chain for resizing panels gets broken
         * and doesn't reach the table
         */
        cmd = new ScheduledCommand() {
            @Override
            public void execute() {
                editableWidget.onResize();
            }
        };
        Scheduler.get().scheduleDeferred(cmd);

        parentScreen.addScreenHandler(editableWidget,
                                      "auxDataTable",
                                      new ScreenHandler<ArrayList<Row>>() {
                                          public void onDataChange(DataChangeEvent<ArrayList<Row>> event) {
                                              clear();
                                              editableWidget.setModel(getTableModel());
                                              setCount(parentScreen.getManager() != null ? parentScreen.getManager().auxData.count()
                                                                                        : 0);
                                          }

                                          public void onStateChange(StateChangeEvent event) {
                                              editableWidget.setEnabled(true);
                                          }

                                          public Widget onTab(boolean forward) {
                                              return forward ? nextTabWidget : prevTabWidget;
                                          }
                                      });
        /*
         * overridden because the table by default doesn't show any special
         * style on getting the focus
         */
        editableWidget.addDomHandler(new FocusHandler() {
            @Override
            public void onFocus(FocusEvent event) {
                if (editableWidget.getRowCount() > 0 && editableWidget.getSelectedRow() == -1)
                    editableWidget.selectRowAt(0);
            }
        }, FocusEvent.getType());

        editableWidget.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            @Override
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                AuxDataViewDO data;

                if ( !parentScreen.isState(UPDATE) || event.getCol() != 1) {
                    event.cancel();
                    return;
                }

                /*
                 * set the editor, e.g. dropdown or text box, in the editable
                 * cell and in the cell for showing the value in the manager for
                 * this row's analyte
                 */
                data = parentScreen.getManager().auxData.get(event.getRow());
                try {
                    setCellEditor(1, data.getAuxFieldGroupId(), data.getAuxFieldId());
                    setCellEditor(4, data.getAuxFieldGroupId(), data.getAuxFieldId());
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                    event.cancel();
                }
            }
        });

        editableWidget.addCellEditedHandler(new CellEditedHandler() {
            @Override
            public void onCellUpdated(CellEditedEvent event) {
                switch (event.getCol()) {
                    case 1:
                        valueChanged(event.getRow(), false);
                        break;
                }
            }
        });
    }

    /**
     * Copies the row's aux data DO's value to the editable cell
     */
    public void copyFromSample() {
        int r;
        Value userVal, manVal;

        r = editableWidget.getEditingRow();
        if (r < 0)
            return;
        /*
         * here the non-editable cell's value is used in the comparison instead
         * of getting the value from the DO because if the value has been
         * changed twice by the user, the DO's value would already be showing in
         * the non-editable cell
         */
        userVal = (Value)editableWidget.getValueAt(r, 1);
        manVal = (Value)editableWidget.getValueAt(r, 4);
        if (numEdit[r] > 1 && DataBaseUtil.isDifferent(userVal, manVal)) {
            editableWidget.setValueAt(r, 1, manVal);
            editableWidget.setValueAt(r, 2, OpenELISResources.INSTANCE.icon().CommitButtonImage());
            editableWidget.setValueAt(r, 3, OpenELISResources.INSTANCE.icon().arrowLeftImage());
            /*
             * set the focus back to the cell being edited because it loses
             * focus when setValueAt is called
             */
            editableWidget.startEditing(r, 1);
            isVerified[r] = true;
        }
    }

    /**
     * Copies the value in the row's editable cell to its aux data DO
     */
    public void copyToSample() {
        int r;
        AuxDataViewDO data;
        AuxFieldGroupManager agm;
        ResultFormatter rf;
        Value userVal, manVal;

        r = editableWidget.getEditingRow();
        if (r < 0)
            return;

        /*
         * validate the value entered by the user and copy it to the row's DO
         * and non-editable cell; set the type to null if the value is not
         * valid; here the non-editable cell's value is used in the comparison
         * instead of getting the value from the DO because if the value has
         * been changed twice by the user, the DO's value would already be
         * showing in the non-editable cell
         */
        userVal = (Value)editableWidget.getValueAt(r, 1);
        manVal = (Value)editableWidget.getValueAt(r, 4);
        data = parentScreen.getManager().auxData.get(r);
        if (numEdit[r] > 1 && DataBaseUtil.isDifferent(userVal, manVal)) {
            try {
                agm = parentScreen.getCacheProvider().get(data.getAuxFieldGroupId(),
                                                          AuxFieldGroupManager.class);
                rf = agm.getFormatter();
                ResultHelper.formatValue(data, userVal.getDisplay(), rf);
            } catch (ParseException e) {
                data.setValue(userVal.getDisplay());
                data.setTypeId(null);
            } catch (Exception e) {
                Window.alert(e.getMessage());
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
            editableWidget.setValueAt(r, 4, userVal);
            editableWidget.setValueAt(r, 2, OpenELISResources.INSTANCE.icon().CommitButtonImage());
            editableWidget.setValueAt(r, 3, OpenELISResources.INSTANCE.icon().arrowRightImage());
            /*
             * set the focus back to the editable cell because it loses focus
             * when setValueAt is called
             */
            editableWidget.startEditing(r, 1);
            isVerified[r] = true;
        }
    }

    /**
     * Verifies whether the value entered by the user in the row at the passed
     * index is the same as the value in the aux data DO at the same index;
     * increments the number of times the value has been changed; if the values
     * are different and the value has been changed more than once, shows the
     * DO's value to the user
     */
    public void valueChanged() {
        valueChanged(editableWidget.getEditingRow(), true);
    }

    /**
     * Marks the aux data in the sample at the passed index as verified if its
     * value is the same as the value entered by the user; shows the image for
     * "match" or "no match" based on whether the two values are same or
     * different respectively
     */
    protected void verify(int i) {
        boolean match;
        Value value;
        String userVal;
        AuxDataViewDO data;

        userVal = null;
        value = (Value)editableWidget.getValueAt(i, 1);
        if (value != null)
            userVal = value.getDictId() != null ? value.getDictId() : value.getDisplay();

        data = parentScreen.getManager().auxData.get(i);
        match = !DataBaseUtil.isDifferent(userVal, data.getValue());
        /*
         * set the icon for match/no match
         */
        editableWidget.setValueAt(i, 2, match ? OpenELISResources.INSTANCE.icon()
                                                                          .CommitButtonImage()
                                             : OpenELISResources.INSTANCE.icon().AbortButtonImage());
        isVerified[i] = match;
    }

    /**
     * Sets the focus to the editable cell in the row at the passed index
     */
    protected void refocus(int i) {
        /*
         * "row" is a class-level variable because if it's a simple local
         * variable it won't be in the scope of the ScheduledCommand below; it
         * can't be final either because then its value in the command won't
         * change after the command has been created
         */
        row = i;
        if (focusCommand == null) {
            focusCommand = new ScheduledCommand() {
                @Override
                public void execute() {
                    editableWidget.startEditing(row, 1);
                }
            };
        }
        Scheduler.get().scheduleDeferred(focusCommand);
    }

    /**
     * Creates and returns the model for the table
     */
    private ArrayList<Row> getTableModel() {
        Row row;
        AuxDataViewDO data;
        ArrayList<Row> model;
        SampleManager1 sm;

        model = new ArrayList<Row>();
        sm = parentScreen.getManager();
        if (sm == null)
            return model;

        for (int i = 0; i < sm.auxData.count(); i++ ) {
            data = sm.auxData.get(i);
            row = new Row(data.getAnalyteName(),
                          new ResultCell.Value(null, null),
                          null,
                          null,
                          new ResultCell.Value(null, null));
            model.add(row);
        }

        return model;
    }

    /**
     * Sets the cell editor for the column at position "col" in aux data table;
     * the editor is a dropdown if all potential values for an aux field are
     * dictionary, otherwise the editor is a textbox; if the editor is a
     * dropdown, creates and sets its model from the values; if it's a textbox,
     * sets its case to upper or lower if all values for the aux field are of
     * type alpha lower or alpha upper; the aux field group and aux field are
     * specified by the two passed ids respectively
     */
    private void setCellEditor(int col, Integer groupId, Integer fieldId) throws Exception {
        Integer caseFlag;
        String key;
        ResultCell rc;
        ResultFormatter rf;
        TextBox tb;
        ArrayList<Item<Integer>> model;
        ArrayList<FormattedValue> values;
        AuxFieldGroupManager agm;

        if (dictionaryModel == null)
            dictionaryModel = new HashMap<String, ArrayList<Item<Integer>>>();

        caseFlag = null;
        key = groupId + ":" + fieldId;
        model = dictionaryModel.get(key);
        if (model == null) {
            agm = parentScreen.getCacheProvider().get(groupId, AuxFieldGroupManager.class);
            rf = agm.getFormatter();
            /*
             * if all the values for this aux field are dictionary values, then
             * create a dropdown model from them
             */
            if (rf.hasAllDictionary(fieldId, null)) {
                values = rf.getDictionaryValues(fieldId, null);
                if (values != null) {
                    model = new ArrayList<Item<Integer>>();
                    for (FormattedValue v : values)
                        model.add(new Item<Integer>(v.getId(), v.getDisplay()));
                }
            } else if (rf.hasOnlyAlphaLower(fieldId, null)) {
                caseFlag = Constants.dictionary().TEST_RES_TYPE_ALPHA_LOWER;
            } else if (rf.hasOnlyAlphaUpper(fieldId, null)) {
                caseFlag = Constants.dictionary().TEST_RES_TYPE_ALPHA_UPPER;
            }
        }
        /*
         * this ensures that even if a model was not found above, it's not
         * looked up again
         */
        dictionaryModel.put(key, model);
        rc = (ResultCell)editableWidget.getColumnAt(col).getCellEditor();
        rc.setModel(model);

        /*
         * set the case if the editor is a textbox
         */
        if (rc.getWidget() instanceof TextBox) {
            tb = (TextBox)rc.getWidget();
            if (Constants.dictionary().TEST_RES_TYPE_ALPHA_LOWER.equals(caseFlag))
                tb.setCase(TextBase.Case.LOWER);
            else if (Constants.dictionary().TEST_RES_TYPE_ALPHA_UPPER.equals(caseFlag))
                tb.setCase(TextBase.Case.UPPER);
            else
                tb.setCase(TextBase.Case.MIXED);
        }
    }

    /**
     * Verifies whether the value entered by the user in the row at the passed
     * index is the same as the value in the aux data DO at the same index; if
     * the values are different and the value has been changed by the user more
     * than once, shows the DO's value to the user; increments the number of
     * times the value has been changed; if the boolean flag is true, the user
     * wants the entered value to be treated as changed even though it wasn't
     * actually changed; this could be to verify the entered value again and see
     * the DO's value without needing to the enter some other value
     */
    private void valueChanged(int r, boolean isForced) {
        Value value;
        AuxDataViewDO data;

        if (r < 0)
            return;
        data = parentScreen.getManager().auxData.get(r);

        /*
         * increment the number of times the value for this aux data has been
         * changed
         */
        numEdit[r]++ ;

        /*
         * if there's no match and the value has been changed by the user more
         * than once, show the value in the manager to the user
         */
        verify(r);
        if ( !isVerified[r]) {
            /*
             * blank the icon for the direction of copy because the current
             * value was not copied to or from the manager
             */
            editableWidget.setValueAt(r, 3, OpenELISResources.INSTANCE.icon().blankIcon());
            if (numEdit[r] > 1) {
                if (Constants.dictionary().AUX_DICTIONARY.equals(data.getTypeId()))
                    value = new ResultCell.Value(null, data.getValue());
                else
                    value = new ResultCell.Value(data.getValue(), null);

                editableWidget.setValueAt(r, 4, value);
            }
        }

        /*
         * reset the focus to the cell most recently edited, to allow the user
         * to change the value; this needs to be done because either that cell
         * didn't have focus when this method was called or it lost focus when
         * the icon for match/no match was set in verify()
         */
        if ( !isVerified[r] || isForced)
            refocus(r);
    }
}