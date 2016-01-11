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

import static org.openelis.modules.main.client.Logger.*;
import static org.openelis.ui.screen.State.*;

import java.util.logging.Level;

import org.openelis.domain.SampleClinicalViewDO;
import org.openelis.manager.SampleManager1;
import org.openelis.meta.SampleMeta;
import org.openelis.modules.main.client.resources.OpenELISResources;
import org.openelis.modules.patient.client.PatientLookupUI;
import org.openelis.modules.patient.client.PatientService;
import org.openelis.modules.sample1.client.PatientChangeEvent;
import org.openelis.modules.secondDataEntry.client.VerificationScreen;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.TextArea;
import org.openelis.ui.widget.TextBox;

import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * This class manages the widgets used for verifying clinical patient last name
 */
public class ClinPatientLastName extends SingleField<TextBox<String>> {
    private Button          patientSearchButton;
    private TextArea        patientId;
    private PatientLookupUI patientLookup;

    public ClinPatientLastName(VerificationScreen parentScreen, TableRowElement tableRowElement,
                               TextBox<String> editableWidget, TextBox<String> nonEditableWidget,
                               Image matchImage, Image copyImage, int rowIndex,
                               Button patientSearchButton, TextArea patientId) {
        super(parentScreen, tableRowElement, editableWidget, nonEditableWidget, matchImage,
              copyImage, rowIndex);
        this.patientSearchButton = patientSearchButton;
        this.patientId = patientId;
        init();
    }

    /**
     * Makes the row in which the widgets are shown, visible and sets its style;
     * adds handlers to the widgets in the row
     */
    protected void init() {
        setRowVisible();

        key = SampleMeta.CLIN_PATIENT_LAST_NAME;
        parentScreen.addScreenHandler(editableWidget,
                                      SampleMeta.CLIN_PATIENT_LAST_NAME,
                                      new ScreenHandler<String>() {
                                          public void onDataChange(DataChangeEvent<String> event) {
                                              clear();
                                          }

                                          public void onValueChange(ValueChangeEvent<String> event) {
                                              valueChanged();
                                              parentScreen.setTabFocusLostWidget(null);
                                          }

                                          public void onStateChange(StateChangeEvent event) {
                                              editableWidget.setEnabled(parentScreen.isState(UPDATE));
                                              patientSearchButton.setEnabled(parentScreen.isState(UPDATE));
                                              nonEditableWidget.setEnabled(false);
                                              patientId.setEnabled(false);
                                          }

                                          public Widget onTab(boolean forward) {
                                              parentScreen.setTabFocusLostWidget(editableWidget);
                                              return forward ? nextTabWidget : prevTabWidget;
                                          }
                                      });

        /*
         * show the popup for patient lookup with no initial search specified
         */
        patientSearchButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (patientLookup == null) {
                    patientLookup = new PatientLookupUI() {
                        public void select() {
                            SampleClinicalViewDO data;

                            /*
                             * if the user selected a patient on the lookup
                             * screen and it's different from the sample's
                             * current patient, link the selected patient to the
                             * sample and show its id
                             */
                            data = parentScreen.getManager().getSampleClinical();
                            if (selectedPatient != null &&
                                !data.getPatientId().equals(selectedPatient.getId())) {
                                /*
                                 * if any of the previous patient's data was
                                 * changed, the patient could be currently
                                 * locked; this could have happened when the
                                 * user tried to commit and the patient was
                                 * locked because its data was changed but
                                 * commit failed because of validation errors in
                                 * the back-end; so unlock the patient
                                 */
                                if (data.getPatient().isChanged() ||
                                    data.getPatient().getAddress().isChanged()) {
                                    try {
                                        PatientService.get().abortUpdate(data.getPatientId());
                                    } catch (Exception e) {
                                        Window.alert(e.getMessage());
                                        logger.log(Level.SEVERE,
                                                   e.getMessage() != null ? e.getMessage() : "null",
                                                   e);
                                        return;
                                    }
                                }

                                data.setPatientId(selectedPatient.getId());
                                data.setPatient(selectedPatient);
                                patientId.setValue(DataBaseUtil.toString(selectedPatient.getId()));
                                /*
                                 * if a value was entered in the editable
                                 * widget, verify it against the new patient's
                                 * last name; if the previous patient's last
                                 * name was being shown in the non editable
                                 * widget, replace that with the new patient's
                                 * last name; if no value was entered in the
                                 * editable widget, mark the field as not
                                 * verified because the patient has changed; the
                                 * field could have been marked verified when
                                 * the user tried to commit
                                 */
                                if (numEdit > 0) {
                                    verify();
                                    if ( !isVerified)
                                        copyImage.setResource(OpenELISResources.INSTANCE.blankIcon());
                                    if (numEdit > 1)
                                        nonEditableWidget.setValue(selectedPatient.getLastName());
                                } else {
                                    isVerified = false;
                                    matchImage.setResource(OpenELISResources.INSTANCE.blankIcon());
                                }
                                parentScreen.getEventBus().fireEvent(new PatientChangeEvent());
                            }
                            editableWidget.setFocus(true);
                        }

                        public void cancel() {
                            editableWidget.setFocus(true);
                        }

                        public void patientsFound() {
                        }
                    };
                }
                patientLookup.query(null, false);
            }
        });
    }

    /**
     * Copies clinical patient last name from the manager to the editable widget
     */
    public void copyFromSample() {
        SampleManager1 sm;

        sm = parentScreen.getManager();
        if (numEdit > 1 &&
            DataBaseUtil.isDifferent(editableWidget.getValue(), sm.getSampleClinical()
                                                                  .getPatient()
                                                                  .getLastName())) {
            editableWidget.setValue(sm.getSampleClinical().getPatient().getLastName());
            matchImage.setResource(OpenELISResources.INSTANCE.commit());
            copyImage.setResource(OpenELISResources.INSTANCE.arrowLeftImage());
            isVerified = true;
            operation = 1;
        }
    }

    /**
     * Copies clinical patient last name from the editable widget to the manager
     */
    public void copyToSample() {
        String lname;
        SampleManager1 sm;

        sm = parentScreen.getManager();
        lname = editableWidget.getValue();
        if (numEdit > 1 && !editableWidget.hasExceptions() &&
            DataBaseUtil.isDifferent(lname, sm.getSampleClinical().getPatient().getLastName())) {
            sm.getSampleClinical().getPatient().setLastName(lname);
            nonEditableWidget.setValue(lname);
            matchImage.setResource(OpenELISResources.INSTANCE.commit());
            copyImage.setResource(OpenELISResources.INSTANCE.arrowRightImage());
            isVerified = true;
            operation = 2;
        }
    }

    /**
     * Verifies whether the value entered by the user is the same as the
     * sample's clinical patient last name; increments the number of times the
     * value has been changed; if the values are different and the value has
     * been changed more than once, shows the sample's clinical patient last
     * name to the user
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
                nonEditableWidget.setValue(parentScreen.getManager()
                                                       .getSampleClinical()
                                                       .getPatient()
                                                       .getLastName());
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
     * sample's clinical patient last name
     */
    public void verify() {
        isVerified = !DataBaseUtil.isDifferent(editableWidget.getValue(),
                                               parentScreen.getManager()
                                                           .getSampleClinical()
                                                           .getPatient()
                                                           .getLastName());
        matchImage.setResource(isVerified ? OpenELISResources.INSTANCE.commit()
                                         : OpenELISResources.INSTANCE.abort());
    }

    /**
     * Sets all the widgets and class fields to their default values
     */
    protected void clear() {
        Integer id;
        SampleManager1 sm;

        super.clear();
        editableWidget.setValue(null);
        nonEditableWidget.setValue(null);

        sm = parentScreen.getManager();
        id = sm != null ? sm.getSampleClinical().getPatientId() : null;
        patientId.setValue(DataBaseUtil.toString(id));

        matchImage.setResource(OpenELISResources.INSTANCE.blankIcon());
        copyImage.setResource(OpenELISResources.INSTANCE.blankIcon());
    }
}