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
package org.openelis.modules.sample1.client;

import java.util.ArrayList;
import java.util.EnumSet;

import org.openelis.cache.CategoryCache;
import org.openelis.domain.AddressDO;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.OrganizationDO;
import org.openelis.domain.SampleOrganizationDO;
import org.openelis.domain.SampleOrganizationViewDO;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.LocalizedException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.GetMatchesEvent;
import org.openelis.gwt.event.GetMatchesHandler;
import org.openelis.gwt.event.HasActionHandlers;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.CellEditedEvent;
import org.openelis.gwt.widget.table.event.CellEditedHandler;
import org.openelis.gwt.widget.table.event.RowAddedEvent;
import org.openelis.gwt.widget.table.event.RowAddedHandler;
import org.openelis.gwt.widget.table.event.RowDeletedEvent;
import org.openelis.gwt.widget.table.event.RowDeletedHandler;
import org.openelis.manager.SampleManager1;
import org.openelis.modules.organization.client.OrganizationService;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;

public class SampleOrganizationLookupScreen1 extends Screen implements HasActionHandlers<SampleOrganizationLookupScreen1.Action> {

    private SampleManager1          manager;
    protected AppButton             organizationRemoveButton;
    protected AutoComplete<Integer> organization;

    public enum Action {
        OK
    };

    private TableWidget table;

    public SampleOrganizationLookupScreen1() throws Exception {
        super((ScreenDefInt)GWT.create(SampleOrganizationLookupDef1.class));

        // Setup link between Screen and widget Handlers
        initialize();

        // Initialize Screen
        setState(State.DEFAULT);

        initializeDropdowns();
    }

    private void initialize() {
        table = (TableWidget)def.getWidget("sampleOrganizationTable");
        addScreenHandler(table, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                table.load(getTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                table.enable(EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()));
                table.setQueryMode(event.getState() == State.QUERY);
            }
        });

        table.addSelectionHandler(new SelectionHandler<TableRow>() {
            public void onSelection(SelectionEvent<TableRow> event) {
                if (EnumSet.of(State.ADD, State.UPDATE).contains(state))
                    organizationRemoveButton.enable(true);
            }
        });

        organization = ((AutoComplete<Integer>)table.getColumns().get(2).colWidget);

        table.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                Integer typeId;
                Object val;
                TableDataRow row;
                OrganizationDO org;
                AddressDO addr;
                SampleOrganizationViewDO data;

                r = event.getRow();
                c = event.getCol();

                data = manager.organization.get(r);

                val = table.getObject(r, c);

                switch (c) {
                    case 0:
                        table.clearCellExceptions(r, c);
                        typeId = (Integer)val;
                        if (canAddOrganizationType(typeId))
                            data.setTypeId(typeId);
                        else
                            table.setCellException(r, c,  new LocalizedException("sample.cantAddOrgTypeToDomainException", 
                                                                                 manager.getSample().getAccessionNumber()));                        
                        break;
                    case 1:
                        data.setOrganizationAttention((String)val);
                        break;
                    case 2:
                        row = (TableDataRow)val;
                        if (row != null) {
                            org = (OrganizationDO)row.data;
                            data.setOrganizationId(org.getId());
                            data.setOrganizationName(org.getName());

                            addr = org.getAddress();
                            data.setOrganizationMultipleUnit(addr.getMultipleUnit());
                            data.setOrganizationStreetAddress(addr.getStreetAddress());
                            data.setOrganizationCity(addr.getCity());
                            data.setOrganizationState(addr.getState());
                            data.setOrganizationZipCode(addr.getZipCode());
                            data.setOrganizationCountry(addr.getCountry());

                            table.setCell(r, 3, addr.getMultipleUnit());
                            table.setCell(r, 4, addr.getStreetAddress());
                            table.setCell(r, 5, addr.getCity());
                            table.setCell(r, 6, addr.getState());
                            table.setCell(r, 7, addr.getZipCode());
                            table.setCell(r, 8, addr.getCountry());
                        } else {
                            data.setOrganizationId(null);
                            data.setOrganizationName(null);
                            data.setOrganizationMultipleUnit(null);
                            data.setOrganizationStreetAddress(null);
                            data.setOrganizationCity(null);
                            data.setOrganizationState(null);
                            data.setOrganizationZipCode(null);
                            data.setOrganizationCountry(null);

                            table.setCell(r, 3, null);
                            table.setCell(r, 4, null);
                            table.setCell(r, 5, null);
                            table.setCell(r, 6, null);
                            table.setCell(r, 7, null);
                            table.setCell(r, 8, null);
                        }

                        try {
                            if (SampleOrganizationUtility1.isHoldRefuseSampleForOrg(data.getOrganizationId()))
                                Window.alert(consts.get("orgMarkedAsHoldRefuseSample") + "'" +
                                             data.getOrganizationName() + "'");
                        } catch (Exception e) {
                            Window.alert(e.getMessage());
                            e.printStackTrace();
                        }
                        break;
                }
            }
        });

        organization.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                TableDataRow row;
                OrganizationDO data;
                ArrayList<OrganizationDO> list;
                ArrayList<TableDataRow> model;

                window.setBusy();
                try {
                    list = OrganizationService.get()
                                              .fetchByIdOrName(QueryFieldUtil.parseAutocomplete(event.getMatch()));
                    model = new ArrayList<TableDataRow>();
                    for (int i = 0; i < list.size(); i++ ) {
                        row = new TableDataRow(4);
                        data = list.get(i);

                        row.key = data.getId();
                        row.cells.get(0).value = data.getName();
                        row.cells.get(1).value = data.getAddress().getStreetAddress();
                        row.cells.get(2).value = data.getAddress().getCity();
                        row.cells.get(3).value = data.getAddress().getState();
                        row.data = data;
                        model.add(row);
                    }
                    organization.showAutoMatches(model);
                } catch (Throwable e) {
                    e.printStackTrace();
                    Window.alert(e.getMessage());
                }
                window.clearStatus();
            }
        });

        table.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                manager.organization.add();
                organizationRemoveButton.enable(true);
            }
        });

        table.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                manager.organization.remove(event.getIndex());
                organizationRemoveButton.enable(false);
            }
        });

        organizationRemoveButton = (AppButton)def.getWidget("organizationRemoveButton");
        addScreenHandler(organizationRemoveButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow > -1 && table.numRows() > 0) {
                    table.deleteRow(selectedRow);
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                organizationRemoveButton.enable(false);
            }

        });

        final AppButton organizationAddButton = (AppButton)def.getWidget("organizationAddButton");
        addScreenHandler(organizationAddButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                table.addRow();
                table.selectRow(table.numRows() - 1);
                table.scrollToSelection();
                table.startEditing(table.numRows() - 1, 0);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                organizationAddButton.enable(EnumSet.of(State.ADD, State.UPDATE)
                                                    .contains(event.getState()));
            }
        });

        final AppButton okButton = (AppButton)def.getWidget("ok");
        addScreenHandler(okButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                ok();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                okButton.enable(true);
            }
        });

    }

    public void ok() {
        table.finishEditing();
        if (validate()) {
            ActionEvent.fire(this, Action.OK, null);
            window.close();
        }
    }

    private ArrayList<TableDataRow> getTableModel() {
        ArrayList<TableDataRow> model;
        SampleOrganizationViewDO data;
        TableDataRow row;

        model = new ArrayList<TableDataRow>();
        if (manager == null)
            return model;

        for (int i = 0; i < manager.organization.count(); i++ ) {
            data = manager.organization.get(i);

            row = new TableDataRow(9);
            row.key = data.getId();
            row.cells.get(0).setValue(data.getTypeId());
            row.cells.get(1).setValue(data.getOrganizationAttention());
            row.cells.get(2).setValue(new TableDataRow(data.getOrganizationId(),
                                                       data.getOrganizationName()));
            row.cells.get(3).setValue(data.getOrganizationMultipleUnit());
            row.cells.get(4).setValue(data.getOrganizationStreetAddress());
            row.cells.get(5).setValue(data.getOrganizationCity());
            row.cells.get(6).setValue(data.getOrganizationState());
            row.cells.get(7).setValue(data.getOrganizationZipCode());
            row.cells.get(8).setValue(data.getOrganizationCountry());

            model.add(row);
        }

        return model;
    }

    private void initializeDropdowns() {
        ArrayList<DictionaryDO> list;
        ArrayList<TableDataRow> model;

        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));

        list = CategoryCache.getBySystemName("organization_type");
        for (DictionaryDO resultDO : list) {
            model.add(new TableDataRow(resultDO.getId(), resultDO.getEntry()));
        }
        ((Dropdown<Integer>)table.getColumns().get(0).getColumnWidget()).setModel(model);

        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        list = CategoryCache.getBySystemName("state");
        for (DictionaryDO data : list) {
            if ("Y".equals(data.getIsActive()))
                model.add(new TableDataRow(data.getEntry(), data.getEntry()));
        }
        ((Dropdown<Integer>)table.getColumns().get(6).getColumnWidget()).setModel(model);

        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        list = CategoryCache.getBySystemName("country");
        for (DictionaryDO data : list) {
            if ("Y".equals(data.getIsActive()))
                model.add(new TableDataRow(data.getEntry(), data.getEntry()));
        }
        ((Dropdown<Integer>)table.getColumns().get(8).getColumnWidget()).setModel(model);
    }

    public void setManager(SampleManager1 man) {
        manager = man;

        DataChangeEvent.fire(this);

        DeferredCommand.addCommand(new Command() {
            public void execute() {
                if (table.numRows() > 0)
                    table.select(0, 0);
            }
        });
    }

    public SampleManager1 getManager() {
        return manager;
    }

    public void setScreenState(State state) {
        setState(state);
    }

    public boolean validate() {
        int numBillTo, numReportTo, numBirthHosp;
        ValidationErrorsList errors;
        boolean superValue;

        superValue = super.validate();
        errors = new ValidationErrorsList();

        numBillTo = 0;
        numReportTo = 0;
        numBirthHosp = 0;

        for (int i = 0; i < manager.organization.count(); i++ ) {
            SampleOrganizationDO orgDO = manager.organization.get(i);
            if (Constants.dictionary().ORG_BILL_TO.equals(orgDO.getTypeId()))
                numBillTo++ ;

            if (Constants.dictionary().ORG_REPORT_TO.equals(orgDO.getTypeId()))
                numReportTo++ ;

            if (Constants.dictionary().ORG_BIRTH_HOSPITAL.equals(orgDO.getTypeId()))
                numBirthHosp++ ;
        }

        if (numBillTo > 1)
            errors.add(new FormErrorException("sample.multipleBillToException",
                                              manager.getSample().getAccessionNumber()));

        if (numReportTo > 1)
            errors.add(new FormErrorException("sample.multipleReportToException",
                                              manager.getSample().getAccessionNumber()));

        if (numBirthHosp > 1)
            errors.add(new FormErrorException("sample.multipleBirthHospException",
                                              manager.getSample().getAccessionNumber()));

        if (errors.size() > 0) {
            showErrors(errors);
            return false;
        }

        if ( !superValue)
            window.setError(consts.get("correctErrors"));

        return superValue;
    }

    public HandlerRegistration addActionHandler(ActionHandler<Action> handler) {
        return addHandler(handler, ActionEvent.getType());
    }

    private boolean canAddOrganizationType(Integer typeId) {
        String domain;

        domain = manager.getSample().getDomain();
        
        return (Constants.dictionary().ORG_SECOND_REPORT_TO.equals(typeId) || Constants.dictionary().ORG_BILL_TO.equals(typeId)
             || (Constants.dictionary().ORG_REPORT_TO.equals(typeId) && !Constants.domain().PRIVATEWELL.equals(domain))
             || (Constants.dictionary().ORG_BIRTH_HOSPITAL.equals(typeId) && Constants.domain().NEONATAL.equals(domain))); 
    }
}