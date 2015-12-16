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

import org.openelis.constants.Messages;
import org.openelis.domain.Constants;
import org.openelis.domain.OrganizationDO;
import org.openelis.domain.SampleOrganizationViewDO;
import org.openelis.manager.SampleManager1;
import org.openelis.modules.main.client.resources.OpenELISResources;
import org.openelis.modules.organization1.client.OrganizationService1Impl;
import org.openelis.modules.sample1.client.SampleOrganizationUtility1;
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
 * This class manages the widgets used for verifying sample organization of a
 * specific type e.g. report-to
 */
public class SampleOrganization extends SingleField<AutoComplete> {
    private Integer type;
    private TextArea details, details1;

    public SampleOrganization(VerificationScreen parentScreen, TableRowElement tableRowElement,
                              AutoComplete editableWidget, AutoComplete nonEditableWidget,
                              Image matchImage, Image copyImage, int rowIndex, Integer type,
                              TextArea details, TextArea details1) {
        super(parentScreen, tableRowElement, editableWidget, nonEditableWidget, matchImage,
              copyImage, rowIndex);
        this.type = type;
        this.details = details;
        this.details1 = details1;
        init();
    }

    /**
     * Makes the row in which the widgets are shown, visible and sets its style;
     * adds handlers to the widgets in the row
     */
    protected void init() {
        String key;

        setRowVisible();

        /*
         * set the key in the handler according to the type
         */
        key = null;
        if (Constants.dictionary().ORG_REPORT_TO.equals(type))
            key = "reportTo";
        else if (Constants.dictionary().ORG_BILL_TO.equals(type))
            key = "billTo";
        else if (Constants.dictionary().ORG_BIRTH_HOSPITAL.equals(type))
            key = "birthHospital";

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
                details.setEnabled(false);
                details1.setEnabled(false);
            }

            public Widget onTab(boolean forward) {
                parentScreen.setTabFocusLostWidget(editableWidget);
                return forward ? nextTabWidget : prevTabWidget;
            }
        });

        editableWidget.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                Item<Integer> row;
                ArrayList<OrganizationDO> list;
                ArrayList<Item<Integer>> model;

                parentScreen.getWindow().setBusy();
                try {
                    list = OrganizationService1Impl.INSTANCE.fetchByIdOrName(QueryFieldUtil.parseAutocomplete(event.getMatch()));
                    model = new ArrayList<Item<Integer>>();
                    for (OrganizationDO data : list) {
                        row = new Item<Integer>(data.getId(),
                                                data.getName(),
                                                data.getAddress().getMultipleUnit(),
                                                data.getAddress().getStreetAddress(),
                                                data.getAddress().getCity(),
                                                data.getAddress().getState());
                        row.setData(data);
                        model.add(row);
                    }
                    editableWidget.showAutoMatches(model);
                } catch (Throwable e) {
                    Window.alert(e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                }
                parentScreen.getWindow().clearStatus();
            }
        });
    }

    /**
     * Copies organization of the specific type from the manager to the editable
     * widget
     */
    public void copyFromSample() {
        Integer orgId, sorgId;
        String sorgName;
        SampleOrganizationViewDO sorg;

        /*
         * copy value from the manager to the editable widget
         */
        orgId = editableWidget.getValue() != null ? editableWidget.getValue().getId() : null;
        sorg = getSampleOrganization();
        sorgId = sorg != null ? sorg.getOrganizationId() : null;
        sorgName = sorg != null ? sorg.getOrganizationName() : null;
        if (numEdit > 1 && DataBaseUtil.isDifferent(orgId, sorgId)) {
            editableWidget.setValue(sorgId, sorgName);
            details.setValue(getOrganizationDetails(sorg));
            matchImage.setResource(OpenELISResources.INSTANCE.commit());
            copyImage.setResource(OpenELISResources.INSTANCE.arrowLeftImage());
            isVerified = true;
        }
    }

    /**
     * Copies organization of the specific type from the editable widget to the
     * manager
     */
    public void copyToSample() {
        Integer orgId, sorgId;
        OrganizationDO org;
        SampleOrganizationViewDO sorg;

        /*
         * copy value from the editable widget to the manager
         */
        org = editableWidget.getValue() != null ? (OrganizationDO)editableWidget.getValue()
                                                                                .getData() : null;
        orgId = org != null ? org.getId() : null;
        sorg = getSampleOrganization();
        sorgId = sorg != null ? sorg.getOrganizationId() : null;
        if (numEdit > 1 && !editableWidget.hasExceptions() &&
            DataBaseUtil.isDifferent(orgId, sorgId)) {
            changeOrganization(org);
            nonEditableWidget.setValue(editableWidget.getValue());
            details1.setValue(getOrganizationDetails(org));
            matchImage.setResource(OpenELISResources.INSTANCE.commit());
            copyImage.setResource(OpenELISResources.INSTANCE.arrowRightImage());
            isVerified = true;
        }
    }

    /**
     * Verifies whether the value entered by the user is the same as the
     * sample's organization of the specific type; increments the number of
     * times the value has been changed; if the values are different and the
     * value has been changed more than once, shows the sample's organization to
     * the user
     */
    public void valueChanged() {
        Integer id;
        String name;
        OrganizationDO org;
        SampleOrganizationViewDO sorg;
        AutoCompleteValue val;

        /*
         * increment the number of times the value has been changed
         */
        numEdit++ ;

        /*
         * show the details for the selected organization
         */
        val = editableWidget.getValue();
        org = val != null ? (OrganizationDO)val.getData() : null;
        details.setValue(getOrganizationDetails(org));

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
                sorg = getSampleOrganization();
                id = sorg != null ? sorg.getOrganizationId() : null;
                name = sorg != null ? sorg.getOrganizationName() : null;
                nonEditableWidget.setValue(id, name);
                details1.setValue(getOrganizationDetails(sorg));
            }
            /*
             * set the focus back on the widget if the user pressed Tab
             */
            if (parentScreen.getTabFocusLostWidget() == editableWidget)
                refocus();
        }
    }

    /**
     * Verifies whether the value entered by the user is the same as the
     * sample's organization of the specific type
     */
    public void verify() {
        Integer orgId, sorgId;
        SampleOrganizationViewDO sorg;

        /*
         * verify whether the organization entered by the user is the same as
         * the organization of this type in the sample
         */
        orgId = editableWidget.getValue() != null ? editableWidget.getValue().getId() : null;
        sorg = getSampleOrganization();
        sorgId = sorg != null ? sorg.getOrganizationId() : null;
        isVerified = !DataBaseUtil.isDifferent(orgId, sorgId);
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
        details.setValue(null);
        details1.setValue(null);
        matchImage.setResource(OpenELISResources.INSTANCE.blankIcon());
        copyImage.setResource(OpenELISResources.INSTANCE.blankIcon());
    }

    /**
     * Returns the details shown in the text area for an organization, created
     * from the passed organization
     */
    private String getOrganizationDetails(OrganizationDO org) {
        Integer id;
        String name, multiUnit, address, city, state, zip, country;

        id = org != null ? org.getId() : null;
        name = org != null ? org.getName() : null;
        multiUnit = org != null ? org.getAddress().getMultipleUnit() : null;
        address = org != null ? org.getAddress().getStreetAddress() : null;
        city = org != null ? org.getAddress().getCity() : null;
        state = org != null ? org.getAddress().getState() : null;
        zip = org != null ? org.getAddress().getZipCode() : null;
        country = org != null ? org.getAddress().getCountry() : null;

        return getOrganizationDetails(id, name, multiUnit, address, city, state, zip, country);
    }

    /**
     * Returns the details shown in the text area for an organization, created
     * from the passed sample organization
     */
    private String getOrganizationDetails(SampleOrganizationViewDO sorg) {
        Integer id;
        String name, multiUnit, address, city, state, zip, country;

        id = sorg != null ? sorg.getOrganizationId() : null;
        name = sorg != null ? sorg.getOrganizationName() : null;
        multiUnit = sorg != null ? sorg.getOrganizationMultipleUnit() : null;
        address = sorg != null ? sorg.getOrganizationStreetAddress() : null;
        city = sorg != null ? sorg.getOrganizationCity() : null;
        state = sorg != null ? sorg.getOrganizationState() : null;
        zip = sorg != null ? sorg.getOrganizationZipCode() : null;
        country = sorg != null ? sorg.getOrganizationCountry() : null;

        return getOrganizationDetails(id, name, multiUnit, address, city, state, zip, country);
    }

    /**
     * Returns the details shown in the text area for an organization type,
     * created from the passed values
     */
    private String getOrganizationDetails(Integer id, String name, String multiUnit,
                                          String address, String city, String state, String zip,
                                          String country) {
        String details;

        /*
         * DataBaseUtil.trim is used here to make sure that if "details" is an
         * empty string, it gets converted to null; otherwise
         * DataBaseUtil.concatWithSeparator will concatenate the delimiter to
         * "details" even if its third argument is null
         */
        details = DataBaseUtil.concatWithSeparator(id, "\n", multiUnit);
        details = DataBaseUtil.concatWithSeparator(DataBaseUtil.trim(details), "\n", address);
        details = DataBaseUtil.concatWithSeparator(DataBaseUtil.trim(details), "\n", city);
        details = DataBaseUtil.concatWithSeparator(DataBaseUtil.trim(details),
                                                   ", ",
                                                   DataBaseUtil.concatWithSeparator(state,
                                                                                    ", ",
                                                                                    zip));
        details = DataBaseUtil.concatWithSeparator(DataBaseUtil.trim(details), "\n", country);

        return details;
    }

    /**
     * Adds or updates the sample organization of the specified type if the
     * passed organization is not null, otherwise deletes the sample
     * organization of this type. Also refreshes the display of the autocomplete
     * showing the organization of this type.
     */
    private void changeOrganization(OrganizationDO org) {
        SampleManager1 sm;
        SampleOrganizationViewDO sorg;

        sm = parentScreen.getManager();
        sorg = getSampleOrganization();
        if (org == null) {
            /*
             * this method is called only when the organization changes and if
             * there isn't a sample organization of this type selected
             * currently, then there must have been before, thus it needs to be
             * removed from the manager
             */
            sm.organization.remove(sorg);
        } else {
            if (sorg == null) {
                /*
                 * an organization was selected by the user but there isn't one
                 * present in the manager, thus it needs to be added
                 */
                sorg = sm.organization.add(org);
                sorg.setTypeId(type);
            } else {
                /*
                 * the organization was changed, so the sample organization
                 * needs to be updated
                 */
                sorg.setOrganizationId(org.getId());
                sorg.setOrganizationName(org.getName());
                sorg.setOrganizationMultipleUnit(org.getAddress().getMultipleUnit());
                sorg.setOrganizationStreetAddress(org.getAddress().getStreetAddress());
                sorg.setOrganizationCity(org.getAddress().getCity());
                sorg.setOrganizationState(org.getAddress().getState());
                sorg.setOrganizationZipCode(org.getAddress().getZipCode());
                sorg.setOrganizationCountry(org.getAddress().getCountry());
            }

            /*
             * warn the user if samples from this organization are to held or
             * refused
             */
            try {
                if (SampleOrganizationUtility1.isHoldRefuseSampleForOrg(org.getId()))
                    Window.alert(Messages.get().gen_orgMarkedAsHoldRefuseSample(org.getName()));
            } catch (Exception e) {
                Window.alert(e.getMessage());
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

    /**
     * Returns the organization of the specific type from the manager or null if
     * the manager is null or doesn't have an organization of this type
     */
    private SampleOrganizationViewDO getSampleOrganization() {
        ArrayList<SampleOrganizationViewDO> sorgs;

        sorgs = parentScreen.getManager().organization.getByType(type);
        if (sorgs != null && sorgs.size() > 0)
            return sorgs.get(0);

        return null;
    }
}