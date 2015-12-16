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
import org.openelis.modules.secondDataEntry.client.VerificationScreen;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.user.client.ui.Widget;

/**
 * This class manages the widgets used for verifying a list of sample data e.g.
 * aux data
 */
public abstract class MultiField<T> implements VerificationField<T> {
    protected VerificationScreen parentScreen;
    protected TableRowElement    tableRowElement;
    protected T                  editableWidget;
    protected Widget             prevTabWidget, nextTabWidget;
    protected ScheduledCommand   focusCommand;
    protected int                rowIndex, numEdit[];
    protected boolean            isVerified[];

    public MultiField(VerificationScreen parentScreen, TableRowElement tableRowElement,
                      T editableWidget, int rowIndex) {
        this.parentScreen = parentScreen;
        this.tableRowElement = tableRowElement;
        this.editableWidget = editableWidget;
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

    /**
     * Returns true if every item in the list has been verified; returns false
     * otherwise
     */
    public boolean getIsVerified() {
        if (isVerified == null)
            return true;
        for (boolean verified : isVerified) {
            if ( !verified)
                return false;
        }
        return true;
    }

    /**
     * Verifies all items in the list
     */
    public void verify() {
        if (isVerified != null) {
            for (int i = 0; i < isVerified.length; i++ )
                verify(i);
        }
    }

    /**
     * Sets the count of items in the lists
     */
    public void setCount(int count) {
        numEdit = new int[count];
        isVerified = new boolean[count];
    }

    /**
     * Verifies the item at the passed index
     */
    protected abstract void verify(int i);

    /**
     * Sets the focus back to the item at the passed index
     */
    protected abstract void refocus(int i);

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

    /**
     * Clears the lists
     */
    protected void clear() {
        numEdit = null;
        isVerified = null;
    }
}