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
package org.openelis.modules.completeRelease1.client;

import org.openelis.constants.Messages;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.Screen.Validation.Status;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.TextBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;

/**
 * This class is used to allow specifying a worksheet's id to fetch the analyses
 * linked to it
 */
public abstract class QueryByWorksheetLookupUI extends Screen {

    @UiTemplate("QueryByWorksheetLookup.ui.xml")
    interface QueryByWorksheetLookupUIBinder extends UiBinder<Widget, QueryByWorksheetLookupUI> {
    };

    private static QueryByWorksheetLookupUIBinder uiBinder = GWT.create(QueryByWorksheetLookupUIBinder.class);

    @UiField
    protected TextBox<Integer>                    worksheetNum;

    @UiField
    protected Button                              okButton, cancelButton;

    protected Integer                             worksheetId;

    protected ScheduledCommand                    cmd;

    public QueryByWorksheetLookupUI() {
        initWidget(uiBinder.createAndBindUi(this));
        initialize();
    }

    public void initialize() {
        addScreenHandler(worksheetNum, "worksheetNum", new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent<Integer> event) {
                worksheetNum.setValue(null);
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                worksheetId = event.getValue();
            }

            public void onStateChange(StateChangeEvent event) {
                worksheetNum.setEnabled(true);
            }

            public Widget onTab(boolean forward) {
                return forward ? okButton : cancelButton;
            }
        });

        addScreenHandler(okButton, "okButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                okButton.setEnabled(true);
            }

            public Widget onTab(boolean forward) {
                return forward ? cancelButton : worksheetNum;
            }
        });

        addScreenHandler(cancelButton, "cancelButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                cancelButton.setEnabled(true);
            }

            public Widget onTab(boolean forward) {
                return forward ? worksheetNum : okButton;
            }
        });
    }

    public void setData() {
        clearErrors();
        setState(state);
        fireDataChange();

        /*
         * without this scheduled command, setting focus on the textbox doesn't
         * work
         */
        if (cmd == null) {
            cmd = new ScheduledCommand() {
                @Override
                public void execute() {
                    worksheetNum.setFocus(true);
                }
            };
        }
        Scheduler.get().scheduleDeferred(cmd);
    }

    public void setState(State state) {
        this.state = state;
        bus.fireEventFromSource(new StateChangeEvent(state), this);
    }

    /**
     * overridden to respond to the user clicking "ok"
     */
    public abstract void ok();

    /**
     * overridden to respond to the user clicking "cancel"
     */
    public abstract void cancel();

    /**
     * returns the worksheet id entered by the user
     */
    public Integer getWorksheetId() {
        return worksheetId;
    }

    @UiHandler("okButton")
    protected void ok(ClickEvent event) {
        Validation validation;

        finishEditing();

        validation = validate();

        if (validation.getStatus() == Status.ERRORS) {
            setError(Messages.get().gen_correctErrors());
            return;
        }

        window.close();
        ok();
    }

    @UiHandler("cancelButton")
    protected void cancel(ClickEvent event) {
        window.close();
        cancel();
    }
}