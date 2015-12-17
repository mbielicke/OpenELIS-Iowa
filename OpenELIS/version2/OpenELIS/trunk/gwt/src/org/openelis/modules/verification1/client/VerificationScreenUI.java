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
package org.openelis.modules.verification1.client;

import static org.openelis.modules.main.client.Logger.*;
import static org.openelis.ui.screen.State.*;

import java.util.logging.Level;

import org.openelis.cache.UserCache;
import org.openelis.constants.Messages;
import org.openelis.domain.Constants;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.manager.SampleManager1;
import org.openelis.meta.SampleMeta;
import org.openelis.modules.sample1.client.SampleService1;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.ModulePermission;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.PermissionException;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.widget.TextBox;
import org.openelis.ui.widget.WindowInt;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

public class VerificationScreenUI extends Screen {

    @UiTemplate("Verification.ui.xml")
    interface VerificationUiBinder extends UiBinder<Widget, VerificationScreenUI> {
    };

    private static VerificationUiBinder uiBinder = GWT.create(VerificationUiBinder.class);

    @UiField
    protected TextBox<String>           barcode;

    protected ModulePermission          userPermission;

    protected VerificationScreenUI      screen;

    /**
     * Check the permissions for this screen, initialize the widgets
     */
    public VerificationScreenUI(WindowInt window) throws Exception {
        setWindow(window);

        userPermission = UserCache.getPermission().getModule("verification");
        if (userPermission == null)
            throw new PermissionException(Messages.get().screenPermException("Verification Screen"));

        initWidget(uiBinder.createAndBindUi(this));

        initialize();
        setState(DEFAULT);
        fireDataChange();

        /*
         * the following is used instead of a ScheduledCommand to make sure
         * that the focus gets set after the widget gets attached to the DOM,
         * which ScheduledCommand doesn't do, as it executes after the creation
         * of the widget, which doesn't mean that the widget is attached
         */
        Scheduler.get().scheduleIncremental(new Scheduler.RepeatingCommand() {
            @Override
            public boolean execute() {
                if (barcode.isAttached()) {
                    barcode.setFocus(true);
                    return false;
                }
                return true;
            }
        });

        logger.fine("Verification Screen Opened");
    }

    private void initialize() {
        screen = this;

        addScreenHandler(barcode, SampleMeta.getClientReference(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                barcode.setValue(null);
            }

            public void onStateChange(StateChangeEvent event) {
                barcode.setEnabled(isState(DEFAULT));
            }
            
            public Widget onTab(boolean forward) {
                return barcode;
            }
        });

        /*
         * KeyUpEvent is used instead of ValueChangeEvent because the former is
         * fired for Tab and Enter keys both, whereas the latter is fired only
         * for the Tab key because the new framework supresses the browser's
         * default behavior for keys. Also, a call to the back-end to verify the
         * sample is only made if the entered text is a valid number and after
         * each call the text box is cleared even if there's an error so
         * ValueChangeEvent is not very useful here.
         */
        barcode.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                if (KeyCodes.KEY_TAB == event.getNativeKeyCode() ||
                    KeyCodes.KEY_ENTER == event.getNativeKeyCode())
                    verifySample(barcode.getText());
            }
        });
    }

    private void verifySample(String code) {
        int i;
        SampleManager1 sm;
        SampleItemViewDO item;

        if (DataBaseUtil.isEmpty(code))
            return;

        if (code.matches("[0-9]+-[0-9]+"))
            code = code.substring(0, code.indexOf("-"));

        if ( !code.matches("[0-9]+")) {
            setError(Messages.get().sample_invalidEntryException(code));
            return;
        }

        sm = null;
        try {
            setBusy(Messages.get().gen_updating());

            sm = SampleService1.get().fetchByAccession(new Integer(code));
            /*
             * the sample must be in "Not Verified" status and must not be quick
             * entry
             */
            if ( !Constants.dictionary().SAMPLE_NOT_VERIFIED.equals(sm.getSample().getStatusId())) {
                setError(Messages.get().verification_wrongStatusForVerifying());
                return;
            } else if (Constants.domain().QUICKENTRY.equals(sm.getSample().getDomain())) {
                setError(Messages.get().verification_cantVerifyQuickEntry());
                return;
            }

            /*
             * samples with no analyses can't be verified
             */
            for (i = 0; i < sm.item.count(); i++ ) {
                item = sm.item.get(i);
                if (sm.analysis.count(item) > 0)
                    break;
            }
            if (i >= sm.item.count()) {
                setError(Messages.get().verification_mustHaveAnalysesToVerify());
                return;
            }

            sm = SampleService1.get().fetchForUpdate(sm.getSample().getId(),
                                                     SampleManager1.Load.ANALYSISUSER,
                                                     SampleManager1.Load.AUXDATA,
                                                     SampleManager1.Load.NOTE,
                                                     SampleManager1.Load.ORGANIZATION,
                                                     SampleManager1.Load.PROJECT,
                                                     SampleManager1.Load.QA,
                                                     SampleManager1.Load.RESULT,
                                                     SampleManager1.Load.STORAGE);
            sm.getSample().setStatusId(Constants.dictionary().SAMPLE_LOGGED_IN);
            try {
                SampleService1.get().update(sm, false);
                setDone(Messages.get().gen_updatingComplete());
            } catch (ValidationErrorsList e) {
                if (e.hasErrors()) {
                    showErrors(e);
                    SampleService1.get().unlock(sm.getSample().getId());
                } else if (e.hasWarnings()) {
                    SampleService1.get().update(sm, true);
                    setDone(Messages.get().gen_updatingComplete());
                }
            }
        } catch (NotFoundException e) {
            setError(Messages.get().sample_invalidEntryException(code));
        } catch (ValidationErrorsList e) {
            showErrors(e);
            try {
                /*
                 * if update with warnings fails because of some validation
                 * errors then the sample needs to be unlocked
                 */
                SampleService1.get().unlock(sm.getSample().getId());
            } catch (Exception anyE) {
                // ignore
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
            clearStatus();
        } finally {
            barcode.setFocus(true);
            fireDataChange();
        }
    }
}