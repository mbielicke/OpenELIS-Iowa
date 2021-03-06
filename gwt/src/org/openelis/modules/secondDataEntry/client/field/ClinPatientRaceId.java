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

import java.util.ArrayList;

import org.openelis.cache.CategoryCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.manager.SampleManager1;
import org.openelis.meta.SampleMeta;
import org.openelis.modules.main.client.resources.OpenELISResources;
import org.openelis.modules.sample1.client.PatientChangeEvent;
import org.openelis.modules.secondDataEntry.client.VerificationScreen;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.Item;

import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * This class manages the widgets used for verifying clinical patient race id
 */
public class ClinPatientRaceId extends SingleField<Dropdown<Integer>> {
    public ClinPatientRaceId(VerificationScreen parentScreen, TableRowElement tableRowElement,
                             Dropdown<Integer> editableWidget, Dropdown<Integer> nonEditableWidget,
                             Image matchImage, Image copyImage, int rowIndex) {
        super(parentScreen, tableRowElement, editableWidget, nonEditableWidget, matchImage,
              copyImage, rowIndex);
        init();
    }

    /**
     * Makes the row in which the widgets are shown, visible and sets its style;
     * adds handlers to the widgets in the row
     */
    protected void init() {
        String r;
        Item<Integer> row;
        ArrayList<Item<Integer>> model;

        setRowVisible();

        key = SampleMeta.CLIN_PATIENT_RACE_ID;
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

        /*
         * the handler for the event fired when the sample's patient is changed,
         * e.g. by using the patient lookup screen
         */
        parentScreen.getEventBus().addHandler(PatientChangeEvent.getType(),
                                              new PatientChangeEvent.Handler() {
                                                  @Override
                                                  public void onPatientChange(PatientChangeEvent event) {
                                                      /*
                                                       * if a value was entered
                                                       * in the editable widget,
                                                       * verify it against the
                                                       * new patient's race; if
                                                       * the previous patient's
                                                       * race was being shown in
                                                       * the non editable
                                                       * widget, replace that
                                                       * with the new patient's
                                                       * race; if no value was
                                                       * entered in the editable
                                                       * widget, mark the field
                                                       * as not verified because
                                                       * the patient has
                                                       * changed; the field
                                                       * could have been marked
                                                       * verified when the user
                                                       * tried to commit
                                                       */
                                                      if (numEdit > 0) {
                                                          verify();
                                                          if ( !isVerified)
                                                              copyImage.setResource(OpenELISResources.INSTANCE.blankIcon());
                                                          if (numEdit > 1)
                                                              nonEditableWidget.setValue(parentScreen.getManager()
                                                                                                     .getSampleClinical()
                                                                                                     .getPatient()
                                                                                                     .getRaceId());
                                                      } else {
                                                          isVerified = false;
                                                          matchImage.setResource(OpenELISResources.INSTANCE.blankIcon());
                                                      }
                                                  }
                                              });

        /*
         * show the combination of code (1,2,3 etc.) and entry("White", "Black")
         * for each race
         */
        model = new ArrayList<Item<Integer>>();
        for (DictionaryDO d : CategoryCache.getBySystemName("race")) {
            r = DataBaseUtil.concatWithSeparator(d.getCode(), " - ", d.getEntry());
            row = new Item<Integer>(d.getId(), r);
            row.setEnabled( ("Y".equals(d.getIsActive())));
            model.add(row);
        }
        editableWidget.setModel(model);
        nonEditableWidget.setModel(model);
    }

    /**
     * Copies clinical patient race id from the manager to the editable widget
     */
    public void copyFromSample() {
        SampleManager1 sm;

        sm = parentScreen.getManager();
        if (numEdit > 1 &&
            DataBaseUtil.isDifferent(editableWidget.getValue(), sm.getSampleClinical()
                                                                  .getPatient()
                                                                  .getRaceId())) {
            editableWidget.setValue(sm.getSampleClinical().getPatient().getRaceId());
            matchImage.setResource(OpenELISResources.INSTANCE.commit());
            copyImage.setResource(OpenELISResources.INSTANCE.arrowLeftImage());
            isVerified = true;
            operation = 1;
            fireScriptletEvent();
        }
    }

    /**
     * Copies clinical patient race id from the editable widget to the manager
     */
    public void copyToSample() {
        Integer raceId;
        SampleManager1 sm;

        sm = parentScreen.getManager();
        raceId = editableWidget.getValue();
        if (numEdit > 1 && !editableWidget.hasExceptions() &&
            DataBaseUtil.isDifferent(raceId, sm.getSampleClinical().getPatient().getRaceId())) {
            sm.getSampleClinical().getPatient().setRaceId(raceId);
            nonEditableWidget.setValue(raceId);
            matchImage.setResource(OpenELISResources.INSTANCE.commit());
            copyImage.setResource(OpenELISResources.INSTANCE.arrowRightImage());
            isVerified = true;
            operation = 2;
            fireScriptletEvent();
        }
    }

    /**
     * Verifies whether the value entered by the user is the same as the
     * sample's clinical patient race id; increments the number of times the
     * value has been changed; if the values are different and the value has
     * been changed more than once, shows the sample's clinical patient race to
     * the user
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
                                                       .getRaceId());
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
     * sample's clinical patient race id
     */
    public void verify() {
        isVerified = !DataBaseUtil.isDifferent(editableWidget.getValue(),
                                               parentScreen.getManager()
                                                           .getSampleClinical()
                                                           .getPatient()
                                                           .getRaceId());
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