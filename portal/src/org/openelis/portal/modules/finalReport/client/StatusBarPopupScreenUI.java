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
package org.openelis.portal.modules.finalReport.client;

import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.widget.Label;
import org.openelis.ui.widget.PercentBar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;

public class StatusBarPopupScreenUI extends Screen {

    @UiTemplate("StatusBarPopupScreen.ui.xml")
    interface StatusBarPopupScreenUIBinder extends UiBinder<Widget, StatusBarPopupScreenUI> {
    };

    private static StatusBarPopupScreenUIBinder uiBinder = GWT.create(StatusBarPopupScreenUIBinder.class);

    @UiField
    protected Label<String>                     message;

    @UiField
    protected PercentBar                        percentBar;

    public StatusBarPopupScreenUI() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    public void setStatus(ReportStatus status) {
        if (status == null) {
            message.setText("no status");
            percentBar.setPercent(new Double(0));
        } else {
            message.setText(status.getMessage());
            percentBar.setPercent(new Double(status.getPercentComplete()));
        }
    }
}
