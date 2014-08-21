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
package org.openelis.modules.verification.client;

import org.openelis.cache.UserCache;
import org.openelis.domain.Constants;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.LocalizedException;
import org.openelis.gwt.common.ModulePermission;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.PermissionException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.widget.TextBox;
import org.openelis.manager.SampleManager;
import org.openelis.modules.sample.client.SampleService;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;

public class VerificationScreen extends Screen {

    private ModulePermission userPermission;
    
    protected Integer       notVerifiedId, loggedInId;
    protected TextBox       barcode;
    
    public VerificationScreen() throws Exception {
       super((ScreenDefInt)GWT.create(VerificationDef.class));

        userPermission = UserCache.getPermission().getModule("verification");
        if (userPermission == null)
            throw new PermissionException("screenPermException", "Verification Screen");

        DeferredCommand.addCommand(new Command() {
            public void execute() {
                postConstructor();
            }
        });
    }

    /**
     * This method is called to set the initial state of widgets after the
     * screen is attached to the browser. It is usually called in deferred
     * command.
     */
    private void postConstructor() {
        initialize();
        setState(State.DEFAULT);
        setFocus(barcode);
    }

    private void initialize() {
        barcode = (TextBox)def.getWidget("barcode");
        addScreenHandler(barcode, new ScreenEventHandler<String>() {
            public void onValueChange(ValueChangeEvent<String> event) {
                verifySample(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                barcode.enable(true);
            }
        });

        /*
         * We add this handler here because the usual practice of setting tab =
         * "key, key" in the xsl file, to specify the widget that gets the focus
         * when tab is pressed is not working in this case, probably because
         * both keys are for the same widget and that widget i.e. this textbox
         * is the only editable widget on the screen. So if we don't do this
         * then once the textbox loses focus, it doesn't gain focus unless it is
         * clicked into.
         */
        barcode.addBlurHandler(new BlurHandler() {
            public void onBlur(BlurEvent event) {
                setFocus(barcode);
            }
        });
    }

    public void reset() {
        setFocus(barcode);
        barcode.setValue(null);
        window.setDone(consts.get("done"));
    }

    private void verifySample(String code) {
        LocalizedException le;
        SampleManager manager;

        if (DataBaseUtil.isEmpty(code))
            return;

        if (code.matches("[0-9]+-[0-9]+"))
            code = code.substring(0, code.indexOf("-"));

        if (code.matches("[0-9]+")) {
            try {
                window.setBusy(consts.get("updating"));

                manager = SampleService.get().fetchByAccessionNumber(new Integer(code));
                if ( !Constants.dictionary().SAMPLE_NOT_VERIFIED.equals(manager.getSample()
                                                                               .getStatusId())) {
                    window.setError(consts.get("wrongStatusForVerifying"));
                    return;
                }

                manager = manager.fetchForUpdate();
                manager.getSample().setStatusId(Constants.dictionary().SAMPLE_LOGGED_IN);
                try {
                    manager.validate();
                    manager.update();
                    window.setDone(consts.get("updatingComplete"));
                } catch (ValidationErrorsList e) {
                    if (e.hasErrors()) {
                        showErrors(e);
                    } else if (e.hasWarnings()) {
                        manager.setStatusWithError(true);
                        manager.update();
                        window.setDone(consts.get("updatingComplete"));
                    }
                }
            } catch (NotFoundException nfE) {
                le = new LocalizedException("invalidEntryException", code);
                window.setError(le.getMessage());
            } catch (Exception anyE) {
                Window.alert(anyE.getMessage());
                anyE.printStackTrace();
                window.clearStatus();
            } finally {
                setFocus(barcode);
                barcode.setValue(null);
            }
        } else {
            le = new LocalizedException("invalidEntryException", code);
            window.setError(le.getMessage());
        }
    }
}
