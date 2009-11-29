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
package org.openelis.modules.note.client;

import org.openelis.domain.NoteViewDO;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.HasActionHandlers;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.richtext.RichTextWidget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerRegistration;

public class EditRichTextScreen extends Screen implements HasActionHandlers<EditRichTextScreen.Action> {

    private NoteViewDO     managerNoteDO, screenNoteDO;
    private RichTextWidget text;
    private AppButton      okButton, cancelButton;

    public enum Action {
        OK, CANCEL
    };

    public EditRichTextScreen() throws Exception {
        super((ScreenDefInt)GWT.create(EditRichTextDef.class));

        initialize();
        setState(State.DEFAULT);
    }

    private void initialize() {
        text = (RichTextWidget)def.getWidget("text");
        addScreenHandler(text, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                text.setValue(screenNoteDO.getText());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                screenNoteDO.setText(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                text.enable(true);
            }
        });

        okButton = (AppButton)def.getWidget("ok");
        addScreenHandler(okButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                ok();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                okButton.enable(true);
            }
        });

        cancelButton = (AppButton)def.getWidget("cancel");
        addScreenHandler(cancelButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                cancel();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                cancelButton.enable(true);
            }
        });
    }

    public void ok() {
        clearErrors();
        if (validate()) {
            managerNoteDO.copy(screenNoteDO);
            setState(State.DEFAULT);
            ActionEvent.fire(this, Action.OK, null);
            clearErrors();
            window.close();
        }
    }

    public void cancel() {
        setState(State.DEFAULT);
        ActionEvent.fire(this, Action.CANCEL, null);
        clearErrors();
        window.close();
    }

    public void setScreenState(State state) {
        setState(state);
    }

    public void setNote(NoteViewDO note) {
        screenNoteDO = new NoteViewDO();
        screenNoteDO.copy(note);
        managerNoteDO = note;

        DataChangeEvent.fire(this);
    }

    public HandlerRegistration addActionHandler(ActionHandler<Action> handler) {
        return addHandler(handler, ActionEvent.getType());
    }
}
