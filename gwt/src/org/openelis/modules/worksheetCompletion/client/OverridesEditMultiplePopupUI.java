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
package org.openelis.modules.worksheetCompletion.client;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;

import org.openelis.domain.WorksheetAnalysisViewDO;
import org.openelis.manager.WorksheetManager1;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.TextBox;
import org.openelis.ui.widget.calendar.Calendar;

public abstract class OverridesEditMultiplePopupUI extends Screen {

    @UiTemplate("OverridesEditMultiplePopup.ui.xml")
    interface OverridesEditMultiplePopupUiBinder extends UiBinder<Widget, OverridesEditMultiplePopupUI> {
    };

    private static OverridesEditMultiplePopupUiBinder uiBinder = GWT.create(OverridesEditMultiplePopupUiBinder.class);

    @UiField
    protected Button                                  ok, cancel;
    @UiField
    protected Calendar                                startedDate, completedDate;
    @UiField
    protected TextBox<String>                         systemUsers;

    protected ArrayList<WorksheetAnalysisViewDO>      analyses;
    protected WorksheetManager1                       manager;

    public OverridesEditMultiplePopupUI() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    public void initialize() {
        systemUsers.setEnabled(true);
        startedDate.setEnabled(true);
        completedDate.setEnabled(true);
        ok.setEnabled(true);
        cancel.setEnabled(true);
    }

    public void setData(ArrayList<WorksheetAnalysisViewDO> analyses) {
        this.analyses = analyses;
        /*
         * this is done to get rid of any old error messages
         */
        window.clearStatus();
    }

    /**
     * overridden to respond to the user clicking "ok"
     */
    public abstract void ok();

    @SuppressWarnings("unused")
    @UiHandler("ok")
    protected void ok(ClickEvent event) {
        for (WorksheetAnalysisViewDO waVDO : analyses) {
            if (systemUsers.getValue() != null && systemUsers.getValue().length() > 0)
                waVDO.setSystemUsers(systemUsers.getValue());
            if (startedDate.getValue() != null)
                waVDO.setStartedDate(startedDate.getValue());
            if (completedDate.getValue() != null && waVDO.getAnalysisId() != null)
                waVDO.setCompletedDate(completedDate.getValue());
        }
        window.close();
        ok();
    }

    @SuppressWarnings("unused")
    @UiHandler("cancel")
    protected void cancel(ClickEvent event) {
        window.close();
    }
}