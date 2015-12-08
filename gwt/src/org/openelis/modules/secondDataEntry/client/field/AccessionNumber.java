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

import org.openelis.constants.Messages;
import org.openelis.manager.SampleManager1;
import org.openelis.meta.SampleMeta;
import org.openelis.modules.main.client.resources.OpenELISResources;
import org.openelis.modules.sample1.client.SampleService1;
import org.openelis.modules.secondDataEntry.client.VerificationScreen;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.widget.TextBox;

import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * This class manages the widgets used for verifying accession number
 */
public class AccessionNumber extends SingleField<TextBox<Integer>> {
    public AccessionNumber(VerificationScreen parentScreen, TableRowElement tableRowElement,
                           TextBox<Integer> editableWidget, TextBox<Integer> nonEditableWidget,
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
        setRowVisible();

        parentScreen.addScreenHandler(editableWidget,
                                      SampleMeta.ACCESSION_NUMBER,
                                      new ScreenHandler<Integer>() {
                                          public void onDataChange(DataChangeEvent<Integer> event) {
                                              clear();
                                          }

                                          public void onValueChange(ValueChangeEvent<Integer> event) {
                                              /*
                                               * update the number of times the
                                               * widget has been edited
                                               */
                                              numEdit++ ;

                                              /*
                                               * verify whether the value
                                               * entered is different from the
                                               * one in the manager; if it is
                                               * and this widget has been edited
                                               * multiple times, show the value
                                               * in the manager in the widget on
                                               * the right; blank the icon for
                                               * the direction of copy because
                                               * the current value was not
                                               * copied to or from the manager
                                               */
                                              verify();
                                              if ( !isVerified) {
                                                  copyImage.setResource(OpenELISResources.INSTANCE.blankIcon());
                                                  if (numEdit > 1)
                                                      nonEditableWidget.setValue(parentScreen.getManager()
                                                                                             .getSample()
                                                                                             .getAccessionNumber());
                                                  /*
                                                   * set the focus back on the
                                                   * widget if the user pressed
                                                   * Tab
                                                   */
                                                  if (parentScreen.getTabFocusLostWidget() == editableWidget)
                                                      refocus();
                                              }
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
     * Copies accession number from the manager to the editable widget
     */
    public void copyFromSample() {
        SampleManager1 sm;

        sm = parentScreen.getManager();
        if (numEdit > 1 &&
            DataBaseUtil.isDifferent(editableWidget.getValue(), sm.getSample().getAccessionNumber())) {
            editableWidget.setValue(sm.getSample().getAccessionNumber());
            matchImage.setResource(OpenELISResources.INSTANCE.commit());
            copyImage.setResource(OpenELISResources.INSTANCE.arrowLeftImage());
            isVerified = true;
        }
    }

    /**
     * Copies the value in the editable widget to the manager if it's not used
     * as the accession number of some other sample; otherwise shows an error
     */
    public void copyToSample() {
        Integer accession;
        SampleManager1 sm, sm1;

        sm = parentScreen.getManager();
        accession = editableWidget.getValue();
        if (numEdit <= 1 || editableWidget.hasExceptions() ||
            !DataBaseUtil.isDifferent(accession, sm.getSample().getAccessionNumber()))
            return;

        if (accession != null) {
            try {
                sm1 = SampleService1.get().fetchByAccession(accession);
                if (!sm1.getSample().getId().equals(sm.getSample().getId())) {
                    Window.alert(Messages.get().sample_accessionNumberDuplicate(accession));
                    return;
                }
            } catch (NotFoundException e) {
                //ignore
            } catch (Exception e) {
                Window.alert(e.getMessage());
                logger.log(Level.SEVERE, e.getMessage(), e);
                return;
            }
        }
        sm.getSample().setAccessionNumber(accession);
        nonEditableWidget.setValue(accession);
        matchImage.setResource(OpenELISResources.INSTANCE.commit());
        copyImage.setResource(OpenELISResources.INSTANCE.arrowRightImage());
        isVerified = true;
    }

    /**
     * Verifies whether the value entered by the user is the same as the
     * sample's accession number
     */
    public void verify() {
        isVerified = !DataBaseUtil.isDifferent(editableWidget.getValue(),
                                               parentScreen.getManager()
                                                           .getSample()
                                                           .getAccessionNumber());
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