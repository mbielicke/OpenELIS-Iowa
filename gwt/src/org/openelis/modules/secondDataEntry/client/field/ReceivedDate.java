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

import static org.openelis.ui.screen.State.*;

import org.openelis.manager.SampleManager1;
import org.openelis.meta.SampleMeta;
import org.openelis.modules.main.client.resources.OpenELISResources;
import org.openelis.modules.secondDataEntry.client.VerificationScreen;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.widget.calendar.Calendar;

import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * This class manages the widgets used for verifying received date
 */
public class ReceivedDate extends SingleField<Calendar> {
    public ReceivedDate(VerificationScreen parentScreen, TableRowElement tableRowElement,
                        Calendar editableWidget, Calendar nonEditableWidget, Image matchImage,
                        Image copyImage, int rowIndex) {
        super(parentScreen, tableRowElement, editableWidget, nonEditableWidget, matchImage,
              copyImage, rowIndex);
        init();
    }

    /**
     * Makes the row in which the widgets are shown, visible and sets its style;
     * adds handlers to the widgets in the row
     */
    protected void init() {
        setRowVisible();

        key = SampleMeta.RECEIVED_DATE;
        parentScreen.addScreenHandler(editableWidget, key, new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent<Integer> event) {
                clear();
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                valueChanged();
                parentScreen.setTabFocusLostWidget(null);
            }

            public void onStateChange(StateChangeEvent event) {
                editableWidget.setEnabled(parentScreen.isState(UPDATE));
                nonEditableWidget.setEnabled(false);
            }

            public Widget onTab(boolean forward) {
                parentScreen.setTabFocusLostWidget(editableWidget);
                return forward ? nextTabWidget : prevTabWidget;
            }
        });
    }

    /**
     * Copies received date from the manager to the editable widget
     */
    public void copyFromSample() {
        SampleManager1 sm;

        sm = parentScreen.getManager();
        if (numEdit > 1 &&
            DataBaseUtil.isDifferentYM(editableWidget.getValue(), sm.getSample().getReceivedDate())) {
            editableWidget.setValue(sm.getSample().getReceivedDate());
            matchImage.setResource(OpenELISResources.INSTANCE.commit());
            copyImage.setResource(OpenELISResources.INSTANCE.arrowLeftImage());
            isVerified = true;
            operation = 1;
            fireScriptletEvent();
        }
    }

    /**
     * Copies received date from the editable widget to the manager
     */
    public void copyToSample() {
        Datetime rdt;
        SampleManager1 sm;

        sm = parentScreen.getManager();
        rdt = editableWidget.getValue();
        if (numEdit > 1 && !editableWidget.hasExceptions() &&
            DataBaseUtil.isDifferentYM(rdt, sm.getSample().getReceivedDate())) {
            sm.getSample().setReceivedDate(rdt);
            nonEditableWidget.setValue(rdt);
            matchImage.setResource(OpenELISResources.INSTANCE.commit());
            copyImage.setResource(OpenELISResources.INSTANCE.arrowRightImage());
            isVerified = true;
            operation = 2;
            fireScriptletEvent();
        }
    }

    /**
     * Verifies whether the value entered by the user is the same as the
     * sample's received date; increments the number of times the value has been
     * changed; if the values are different and the value has been changed more
     * than once, shows the sample's received date to the user
     */
    public void valueChanged() {
        /*
         * increment the number of times the value has been changed
         */
        numEdit++ ;

        /*
         * verify whether the value entered is different from the one in the
         * manager; if it is and this widget has been edited multiple times,
         * show the value in the manager in the widget on the right; blank the
         * icon for the direction of copy because the current value was not
         * copied to or from the manager
         */
        verify();
        if ( !isVerified) {
            copyImage.setResource(OpenELISResources.INSTANCE.blankIcon());
            if (numEdit > 1)
                nonEditableWidget.setValue(parentScreen.getManager().getSample().getReceivedDate());
            /*
             * set the focus back to the editable widget if it lost focus by
             * pressing Tab
             */
            if (parentScreen.getTabFocusLostWidget() == editableWidget)
                refocus();
        }
    }

    /**
     * Verifies whether the value entered by the user is the same as the
     * sample's received date
     */
    public void verify() {
        isVerified = !DataBaseUtil.isDifferentYM(editableWidget.getValue(),
                                                 parentScreen.getManager()
                                                             .getSample()
                                                             .getReceivedDate());
        matchImage.setResource(isVerified ? OpenELISResources.INSTANCE.commit()
                                         : OpenELISResources.INSTANCE.abort());
    }

    /**
     * Sets all the widgets and class fields to their default values
     */
    protected void clear() {
        super.clear();
        editableWidget.setValue(null);
        nonEditableWidget.setValue(null);
        matchImage.setResource(OpenELISResources.INSTANCE.blankIcon());
        copyImage.setResource(OpenELISResources.INSTANCE.blankIcon());
    }
}