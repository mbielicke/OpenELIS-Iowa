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

import java.util.ArrayList;
import java.util.logging.Level;

import org.openelis.domain.ProviderDO;
import org.openelis.domain.SampleClinicalViewDO;
import org.openelis.meta.SampleMeta;
import org.openelis.modules.main.client.resources.OpenELISResources;
import org.openelis.modules.provider1.client.ProviderService1;
import org.openelis.modules.secondDataEntry.client.VerificationScreen;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.GetMatchesEvent;
import org.openelis.ui.event.GetMatchesHandler;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.widget.AutoComplete;
import org.openelis.ui.widget.AutoCompleteValue;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.QueryFieldUtil;
import org.openelis.ui.widget.TextArea;

import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * This class manages the widgets used for verifying clinical provider
 */
public class ClinProviderLastName extends SingleField<AutoComplete> {
    private TextArea providerFirstName, providerFirstName1;

    public ClinProviderLastName(VerificationScreen parentScreen, TableRowElement tableRowElement,
                                AutoComplete editableWidget, AutoComplete nonEditableWidget,
                                Image matchImage, Image copyImage, int rowIndex,
                                TextArea providerFirstName, TextArea providerFirstName1) {
        super(parentScreen, tableRowElement, editableWidget, nonEditableWidget, matchImage,
              copyImage, rowIndex);
        this.providerFirstName = providerFirstName;
        this.providerFirstName1 = providerFirstName1;
        init();
    }

    /**
     * Makes the row in which the widgets are shown, visible and sets its style;
     * adds handlers to the widgets in the row
     */
    protected void init() {
        setRowVisible();

        key = SampleMeta.CLIN_PROVIDER_LAST_NAME;
        parentScreen.addScreenHandler(editableWidget, key, new ScreenHandler<AutoCompleteValue>() {
            public void onDataChange(DataChangeEvent<AutoCompleteValue> event) {
                clear();
            }

            public void onValueChange(ValueChangeEvent<AutoCompleteValue> event) {
                valueChanged();
                parentScreen.setTabFocusLostWidget(null);
            }

            public void onStateChange(StateChangeEvent event) {
                editableWidget.setEnabled(parentScreen.isState(UPDATE));
                nonEditableWidget.setEnabled(false);
                providerFirstName.setEnabled(false);
                providerFirstName1.setEnabled(false);
            }

            public Widget onTab(boolean forward) {
                parentScreen.setTabFocusLostWidget(editableWidget);
                return forward ? nextTabWidget : prevTabWidget;
            }
        });

        editableWidget.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                ProviderDO data;
                ArrayList<ProviderDO> list;
                Item<Integer> row;
                ArrayList<Item<Integer>> model;

                parentScreen.setBusy();
                try {
                    list = ProviderService1.get()
                                           .fetchByLastNameNpiExternalId(QueryFieldUtil.parseAutocomplete(event.getMatch()));
                    model = new ArrayList<Item<Integer>>();
                    for (int i = 0; i < list.size(); i++ ) {
                        data = list.get(i);
                        row = new Item<Integer>(data.getId(),
                                                data.getLastName(),
                                                data.getFirstName(),
                                                data.getMiddleName(),
                                                data.getNpi());
                        row.setData(data);
                        model.add(row);
                    }
                    editableWidget.showAutoMatches(model);
                } catch (Throwable e) {
                    Window.alert(e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                }
                parentScreen.clearStatus();
            }
        });
    }

    /**
     * Copies clinical provider from the manager to the editable widget
     */
    public void copyFromSample() {
        Integer provId, sprovId;
        String lname;
        SampleClinicalViewDO data;

        data = parentScreen.getManager().getSampleClinical();
        provId = editableWidget.getValue() != null ? editableWidget.getValue().getId() : null;
        sprovId = data.getProviderId();
        lname = data.getProvider() != null ? data.getProvider().getLastName() : null;
        if (numEdit > 1 && DataBaseUtil.isDifferent(provId, sprovId)) {
            editableWidget.setValue(sprovId, lname);
            providerFirstName.setValue(lname);
            matchImage.setResource(OpenELISResources.INSTANCE.commit());
            copyImage.setResource(OpenELISResources.INSTANCE.arrowLeftImage());
            isVerified = true;
            operation = 1;
        }
    }

    /**
     * Copies clinical provider from the editable widget to the manager
     */
    public void copyToSample() {
        Integer provId, sprovId;
        String fname;
        SampleClinicalViewDO data;
        ProviderDO prov;

        data = parentScreen.getManager().getSampleClinical();
        prov = editableWidget.getValue() != null ? (ProviderDO)editableWidget.getValue().getData()
                                                : null;
        provId = prov != null ? prov.getId() : null;
        fname = prov != null ? prov.getFirstName() : null;
        sprovId = data.getProvider() != null ? data.getProvider().getId() : null;
        if (numEdit > 1 && !editableWidget.hasExceptions() &&
            DataBaseUtil.isDifferent(provId, sprovId)) {
            data.setProviderId(provId);
            data.setProvider(prov);
            nonEditableWidget.setValue(editableWidget.getValue());
            providerFirstName1.setValue(fname);
            matchImage.setResource(OpenELISResources.INSTANCE.commit());
            copyImage.setResource(OpenELISResources.INSTANCE.arrowRightImage());
            isVerified = true;
            operation = 2;
        }
    }

    /**
     * Verifies whether the value entered by the user is the same as the
     * sample's clinical provider last name; increments the number of times the
     * value has been changed; if the values are different and the value has
     * been changed more than once, shows the sample's clinical provider to the
     * user
     */
    public void valueChanged() {
        Integer id;
        String lname, fname;
        ProviderDO prov, sprov;
        AutoCompleteValue val;

        /*
         * increment the number of times the value has been changed
         */
        numEdit++ ;

        /*
         * show the selected provider's first name
         */
        val = editableWidget.getValue();
        prov = val != null ? (ProviderDO)val.getData() : null;
        fname = prov != null ? prov.getFirstName() : null;
        providerFirstName.setValue(fname);

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
            if (numEdit > 1) {
                sprov = parentScreen.getManager().getSampleClinical().getProvider();
                id = sprov != null ? sprov.getId() : null;
                lname = sprov != null ? sprov.getLastName() : null;
                fname = sprov != null ? sprov.getFirstName() : null;
                nonEditableWidget.setValue(id, lname);
                providerFirstName1.setValue(fname);
            }
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
     * sample's clinical provider
     */
    public void verify() {
        Integer provId, sprovId;
        ProviderDO sprov;

        provId = editableWidget.getValue() != null ? editableWidget.getValue().getId() : null;
        sprov = parentScreen.getManager().getSampleClinical().getProvider();
        sprovId = sprov != null ? sprov.getId() : null;
        isVerified = !DataBaseUtil.isDifferent(provId, sprovId);
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
        providerFirstName.setValue(null);
        providerFirstName1.setValue(null);
        matchImage.setResource(OpenELISResources.INSTANCE.blankIcon());
        copyImage.setResource(OpenELISResources.INSTANCE.blankIcon());
    }
}