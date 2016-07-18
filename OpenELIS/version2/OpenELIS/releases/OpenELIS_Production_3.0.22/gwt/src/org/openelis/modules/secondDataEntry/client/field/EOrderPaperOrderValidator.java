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

import org.openelis.constants.Messages;
import org.openelis.domain.EOrderDO;
import org.openelis.manager.SampleManager1;
import org.openelis.meta.SampleMeta;
import org.openelis.modules.eorder.client.EOrderLookupUI;
import org.openelis.modules.main.client.resources.OpenELISResources;
import org.openelis.modules.secondDataEntry.client.VerificationScreen;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.widget.ModalWindow;
import org.openelis.ui.widget.TextBox;

import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * This class manages the widgets used for verifying e-order paper order
 * validator
 */
public class EOrderPaperOrderValidator extends SingleField<TextBox<String>> {
    private EOrderLookupUI eorderLookup;

    public EOrderPaperOrderValidator(VerificationScreen parentScreen,
                                     TableRowElement tableRowElement,
                                     TextBox<String> editableWidget,
                                     TextBox<String> nonEditableWidget, Image matchImage,
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

        key = SampleMeta.EORDER_PAPER_ORDER_VALIDATOR;
        parentScreen.addScreenHandler(editableWidget,
                                      key,
                                      new ScreenHandler<Integer>() {
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
     * Copies paper order validator from the manager to the editable widget
     */
    public void copyFromSample() {
        if (numEdit > 1 &&
            DataBaseUtil.isDifferent(editableWidget.getValue(), getPaperOrderValidator())) {
            editableWidget.setValue(getPaperOrderValidator());
            matchImage.setResource(OpenELISResources.INSTANCE.commit());
            copyImage.setResource(OpenELISResources.INSTANCE.arrowLeftImage());
            isVerified = true;
            operation = 1;
        }
    }

    /**
     * Copies the value in the editable widget to the manager if it's a valid
     * paper order validator; otherwise shows an error
     */
    public void copyToSample() {
        String pov;
        ModalWindow modal;

        pov = editableWidget.getValue();
        if (numEdit <= 1 || editableWidget.hasExceptions() ||
            !DataBaseUtil.isDifferent(pov, getPaperOrderValidator()))
            return;

        if (DataBaseUtil.isEmpty(pov)) {
            setEOrder(null);
        } else {
            /*
             * bring up the e-order look up screen; set the order selected by
             * the user on that screen as the sample's order
             */
            if (eorderLookup == null) {
                eorderLookup = new EOrderLookupUI() {
                    @Override
                    public void select() {
                        EOrderDO data;

                        data = eorderLookup.getSelectedEOrder();
                        if (data != null)
                            setEOrder(data);
                        editableWidget.setFocus(true);
                    }

                    @Override
                    public void cancel() {
                        editableWidget.setFocus(true);
                    }
                };
            }

            modal = new ModalWindow();
            modal.setSize("735px", "350px");
            modal.setName(Messages.get().eorder_eorderLookup());
            modal.setContent(eorderLookup);

            eorderLookup.setWindow(modal);
            eorderLookup.setState(UPDATE);
            eorderLookup.setPaperOrderValidator(pov, parentScreen.getManager()
                                                                 .getSample()
                                                                 .getOrderId());
        }
    }

    /**
     * Verifies whether the value entered by the user is the same as the
     * sample's paper order validator; increments the number of times the value
     * has been changed; if the values are different and the value has been
     * changed more than once, shows the sample's paper order validator to the
     * user
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
                nonEditableWidget.setValue(getPaperOrderValidator());
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
     * sample's paper order validator
     */
    public void verify() {
        isVerified = !DataBaseUtil.isDifferent(editableWidget.getValue(), getPaperOrderValidator());
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

    /**
     * Returns the paper order validator for the current sample depending upon
     * whether it's a clinical or neonatal sample; returns null if the sample is
     * of some other domain
     */
    private String getPaperOrderValidator() {
        SampleManager1 sm;

        sm = parentScreen.getManager();
        if (sm.getSampleClinical() != null)
            return sm.getSampleClinical().getPaperOrderValidator();
        else if (sm.getSampleNeonatal() != null)
            return sm.getSampleNeonatal().getPaperOrderValidator();

        return null;
    }

    /**
     * Sets the passed e-order's id and paper order validator in the sample;
     * sets both values as null if the passed e-order is null; also updates the
     * widgets and sets this field as verified
     */
    private void setEOrder(EOrderDO data) {
        Integer id;
        String pov;
        SampleManager1 sm;

        id = data != null ? data.getId() : null;
        pov = data != null ? data.getPaperOrderValidator() : null;

        sm = parentScreen.getManager();
        sm.getSample().setOrderId(id);
        if (sm.getSampleClinical() != null)
            sm.getSampleClinical().setPaperOrderValidator(pov);
        else if (sm.getSampleNeonatal() != null)
            sm.getSampleNeonatal().setPaperOrderValidator(pov);
        editableWidget.setValue(pov);
        nonEditableWidget.setValue(pov);
        matchImage.setResource(OpenELISResources.INSTANCE.commit());
        copyImage.setResource(OpenELISResources.INSTANCE.arrowRightImage());
        isVerified = true;
        operation = 2;
    }
}