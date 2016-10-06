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

import org.openelis.modules.main.client.resources.OpenELISResources;
import org.openelis.modules.sample1.client.RunScriptletEvent;
import org.openelis.modules.secondDataEntry.client.VerificationScreen;
import org.openelis.scriptlet.SampleSO.Action_Before;
import org.openelis.ui.common.DataBaseUtil;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * This class manages the widgets used for verifying a single field
 */
public abstract class SingleField<T> implements VerificationField<T> {
    protected VerificationScreen parentScreen;
    protected TableRowElement    tableRowElement;
    protected T                  editableWidget, nonEditableWidget;
    protected Widget             prevTabWidget, nextTabWidget;
    protected Image              matchImage, copyImage;
    protected ScheduledCommand   focusCommand;
    protected int                rowIndex, numEdit;
    protected boolean            isVerified;
    protected Integer            operation;
    protected String             key;

    public SingleField(VerificationScreen parentScreen, TableRowElement tableRowElement,
                       T editableWidget, T nonEditableWidget, Image matchImage, Image copyImage,
                       int rowIndex) {
        this.parentScreen = parentScreen;
        this.tableRowElement = tableRowElement;
        this.editableWidget = editableWidget;
        this.nonEditableWidget = nonEditableWidget;
        this.matchImage = matchImage;
        this.copyImage = copyImage;
        this.rowIndex = rowIndex;
    }

    public T getEditableWidget() {
        return editableWidget;
    }

    public void setPrevTabWidget(Widget prevTabWidget) {
        this.prevTabWidget = prevTabWidget;
    }

    public void setNextTabWidget(Widget nextTabWidget) {
        this.nextTabWidget = nextTabWidget;
    }

    public boolean getIsVerified() {
        return isVerified;
    }

    public String getLogText() {
        return operation != null ? DataBaseUtil.concatWithSeparator(operation, "-", key) : null;
    }

    /**
     * Makes the field's TableRowElement visible and sets its style based on
     * whether it's at an even or odd position among the visible rows
     */
    protected void setRowVisible() {
        tableRowElement.getStyle().setDisplay(Display.TABLE_ROW);
        if (rowIndex % 2 == 0)
            tableRowElement.setClassName(OpenELISResources.INSTANCE.style()
                                                                   .WidgetTableAlternateRow());
    }

    protected void clear() {
        numEdit = 0;
        isVerified = false;
        operation = null;
    }

    /**
     * Makes the focus get set to this widget as soon as it loses focus
     */
    protected void refocus() {
        if (focusCommand == null) {
            focusCommand = new ScheduledCommand() {
                @Override
                public void execute() {
                    ((Focusable)editableWidget).setFocus(true);
                }
            };
        }
        Scheduler.get().scheduleDeferred(focusCommand);
    }

    /**
     * Fires an event to the parent screen to inform it that this field was
     * changed and scriptlets may need to be run
     */
    protected void fireScriptletEvent() {
        fireScriptletEvent(null, null);
    }

    /**
     * Fires an event, for the passed uid and operation, to the parent screen to
     * inform it that this field was changed and scriptlets may need to be run
     */
    protected void fireScriptletEvent(String uid, Action_Before operation) {
        parentScreen.getEventBus().fireEvent(new RunScriptletEvent(uid, key, operation));
    }
}