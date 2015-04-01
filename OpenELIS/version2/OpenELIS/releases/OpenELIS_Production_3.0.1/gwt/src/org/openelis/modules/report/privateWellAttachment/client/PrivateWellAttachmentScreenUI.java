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
package org.openelis.modules.report.privateWellAttachment.client;

import static org.openelis.modules.main.client.Logger.*;
import static org.openelis.ui.screen.Screen.Validation.Status.*;
import static org.openelis.ui.screen.State.*;

import java.util.logging.Level;

import org.openelis.cache.UserCache;
import org.openelis.constants.Messages;
import org.openelis.ui.common.ModulePermission;
import org.openelis.ui.common.PermissionException;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.TextBox;
import org.openelis.ui.widget.WindowInt;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

public class PrivateWellAttachmentScreenUI extends Screen {

    @UiTemplate("PrivateWellAttachment.ui.xml")
    interface PrivateWellAttachmentUiBinder extends UiBinder<Widget, PrivateWellAttachmentScreenUI> {
    };

    public static final PrivateWellAttachmentUiBinder uiBinder = GWT.create(PrivateWellAttachmentUiBinder.class);

    @UiField
    protected TextBox<String>                  accessionNumber;

    @UiField
    protected Button                           runReportButton;

    protected PrivateWellAttachmentReportScreen       privateWellAttachmentReportScreen;

    public PrivateWellAttachmentScreenUI(WindowInt window) throws Exception {
        ModulePermission userPermission;
        
        setWindow(window);

        userPermission = UserCache.getPermission().getModule("sampletracking");
        if (userPermission == null)
            throw new PermissionException(Messages.get()
                                                  .screenPermException("Private Well Attachment Screen"));

        initWidget(uiBinder.createAndBindUi(this));

        initialize();
        setState(DEFAULT);
        fireDataChange();

        logger.fine("Private Well Attachment Screen");
    }

    protected void initialize() {
        /*
         * screen fields and widgets
         */
        addScreenHandler(accessionNumber, "accessionNumber", new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                accessionNumber.setValue(null);
            }
            
            public void onStateChange(StateChangeEvent event) {
                accessionNumber.setEnabled(true);
            }

            public Widget onTab(boolean forward) {
                return forward ? runReportButton : runReportButton;
            }
        });

        addScreenHandler(runReportButton, "runReportButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                runReportButton.setEnabled(true);
            }

            public Widget onTab(boolean forward) {
                return forward ? accessionNumber : accessionNumber;
            }
        });
    }

    @UiHandler("runReportButton")
    protected void print(ClickEvent event) {
        Validation validation;

        finishEditing();
        clearErrors();

        validation = validate();

        if (validation.getStatus() != VALID) {
            setError(Messages.get().gen_correctErrors());
            return;
        }

        /*
         * print the labels
         */
        try {
            if (privateWellAttachmentReportScreen == null)
                privateWellAttachmentReportScreen = new PrivateWellAttachmentReportScreen(window);

            privateWellAttachmentReportScreen.runReport(accessionNumber.getValue());
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}