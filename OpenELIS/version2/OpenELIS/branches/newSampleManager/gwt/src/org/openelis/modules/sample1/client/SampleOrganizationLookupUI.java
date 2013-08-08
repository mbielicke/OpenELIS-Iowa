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

import static org.openelis.ui.screen.State.*;

import java.util.ArrayList;

import org.openelis.cache.CategoryCache;
import org.openelis.constants.Messages;
import org.openelis.domain.AddressDO;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.OrganizationDO;
import org.openelis.domain.SampleOrganizationDO;
import org.openelis.domain.SampleOrganizationViewDO;
import org.openelis.manager.SampleManager1;
import org.openelis.modules.organization.client.OrganizationService;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.FormErrorException;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.GetMatchesEvent;
import org.openelis.ui.event.GetMatchesHandler;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.AutoComplete;
import org.openelis.ui.widget.AutoCompleteValue;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.QueryFieldUtil;
import org.openelis.ui.widget.table.Row;
import org.openelis.ui.widget.table.Table;
import org.openelis.ui.widget.table.event.BeforeCellEditedEvent;
import org.openelis.ui.widget.table.event.BeforeCellEditedHandler;
import org.openelis.ui.widget.table.event.CellEditedEvent;
import org.openelis.ui.widget.table.event.CellEditedHandler;
import org.openelis.ui.widget.table.event.RowAddedEvent;
import org.openelis.ui.widget.table.event.RowAddedHandler;
import org.openelis.ui.widget.table.event.RowDeletedEvent;
import org.openelis.ui.widget.table.event.RowDeletedHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

/**
 * This class is used to allow users to add/remove/change organizations related
 * to a sample
 */
public abstract class SampleOrganizationLookupUI extends Screen {

    @UiTemplate("SampleOrganizationLookup.ui.xml")
    interface SampleOrganizationLookupUIBinder extends UiBinder<Widget, SampleOrganizationLookupUI> {
    };

    private static SampleOrganizationLookupUIBinder uiBinder = GWT.create(SampleOrganizationLookupUIBinder.class);

    protected SampleManager1                        manager;

    @UiField
    protected Table                                 table;

    @UiField
    protected Dropdown<Integer>                     type;

    @UiField
    protected Dropdown<String>                      orgState, country;

    @UiField
    protected AutoComplete                          organization;

    @UiField
    protected Button                                addOrganizationButton,
                    removeOrganizationButton, okButton;

    public SampleOrganizationLookupUI() {
        initWidget(uiBinder.createAndBindUi(this));
        initialize();
    }

    private void initialize() {
        ArrayList<DictionaryDO> list;
        Item<Integer> item;
        Item<String> sitem;
        ArrayList<Item<Integer>> model;
        ArrayList<Item<String>> smodel;

        addScreenHandler(table, "table", new ScreenHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent event) {
                table.setModel(getTableModel());
            }

            public void onStateChange(StateChangeEvent event) {
                table.setEnabled(true);
            }
        });

        table.addSelectionHandler(new SelectionHandler<Integer>() {
            @Override
            public void onSelection(com.google.gwt.event.logical.shared.SelectionEvent<Integer> event) {
                removeOrganizationButton.setEnabled(isState(ADD, UPDATE));
            }
        });

        table.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            @Override
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                if ( !isState(ADD, UPDATE) || event.getCol() > 2)
                    event.cancel();
            }
        });

        table.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                Object val;
                AutoCompleteValue row;
                OrganizationDO org;
                AddressDO addr;
                SampleOrganizationViewDO data;

                r = event.getRow();
                c = event.getCol();

                data = manager.organization.get(r);

                val = table.getValueAt(r, c);

                switch (c) {
                    case 0:
                        data.setTypeId((Integer)val);
                        break;
                    case 1:
                        data.setOrganizationAttention((String)val);
                        break;
                    case 2:
                        row = (AutoCompleteValue)val;
                        if (row != null) {
                            org = (OrganizationDO)row.getData();
                            data.setOrganizationId(org.getId());
                            data.setOrganizationName(org.getName());

                            addr = org.getAddress();
                            data.setOrganizationMultipleUnit(addr.getMultipleUnit());
                            data.setOrganizationStreetAddress(addr.getStreetAddress());
                            data.setOrganizationCity(addr.getCity());
                            data.setOrganizationState(addr.getState());
                            data.setOrganizationZipCode(addr.getZipCode());
                            data.setOrganizationCountry(addr.getCountry());
                        } else {
                            data.setOrganizationId(null);
                            data.setOrganizationName(null);
                            data.setOrganizationMultipleUnit(null);
                            data.setOrganizationStreetAddress(null);
                            data.setOrganizationCity(null);
                            data.setOrganizationState(null);
                            data.setOrganizationZipCode(null);
                            data.setOrganizationCountry(null);
                        }

                        table.setValueAt(r, 3, data.getOrganizationMultipleUnit());
                        table.setValueAt(r, 4, data.getOrganizationStreetAddress());
                        table.setValueAt(r, 5, data.getOrganizationCity());
                        table.setValueAt(r, 6, data.getOrganizationState());
                        table.setValueAt(r, 7, data.getOrganizationZipCode());
                        table.setValueAt(r, 8, data.getOrganizationCountry());

                        try {
                            if (SampleOrganizationUtility1.isHoldRefuseSampleForOrg(data.getOrganizationId()))
                                Window.alert(Messages.get()
                                                     .orgMarkedAsHoldRefuseSample(data.getOrganizationName()));
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
                Item<Integer> row;
                OrganizationDO data;
                ArrayList<OrganizationDO> list;
                ArrayList<Item<Integer>> model;

                window.setBusy();
                try {
                    list = OrganizationService.get()
                                              .fetchByIdOrName(QueryFieldUtil.parseAutocomplete(event.getMatch()));
                    model = new ArrayList<Item<Integer>>();
                    for (int i = 0; i < list.size(); i++ ) {
                        row = new Item<Integer>(4);
                        data = list.get(i);

                        row.setKey(data.getId());
                        row.setCell(0, data.getName());
                        row.setCell(1, data.getAddress().getStreetAddress());
                        row.setCell(2, data.getAddress().getCity());
                        row.setCell(3, data.getAddress().getState());
                        row.setData(data);
                        model.add(row);
                    }
                    organization.showAutoMatches(model);
                } catch (Throwable e) {
                    Window.alert(e.getMessage());
                }
                window.clearStatus();
            }
        });

        table.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                manager.organization.add();
                removeOrganizationButton.setEnabled(true);
            }
        });

        table.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                manager.organization.remove(event.getIndex());
                removeOrganizationButton.setEnabled(false);
            }
        });

        addScreenHandler(addOrganizationButton,
                         "addOrganizationButton",
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 addOrganizationButton.setEnabled(isState(ADD, UPDATE));
                             }
                         });

        addScreenHandler(removeOrganizationButton,
                         "removeOrganizationButton",
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 removeOrganizationButton.setEnabled(false);
                             }
                         });

        addScreenHandler(okButton, "okButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                okButton.setEnabled(true);
            }
        });

        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));

        list = CategoryCache.getBySystemName("organization_type");
        for (DictionaryDO d : list) {
            item = new Item<Integer>(d.getId(), d.getEntry());
            item.setEnabled("Y".equals(d.getIsActive()));
            model.add(item);
        }
        type.setModel(model);

        smodel = new ArrayList<Item<String>>();
        smodel.add(new Item<String>(null, ""));
        list = CategoryCache.getBySystemName("state");
        for (DictionaryDO d : list) {
            sitem = new Item<String>(d.getEntry(), d.getEntry());
            sitem.setEnabled("Y".equals(d.getIsActive()));
            smodel.add(sitem);
        }
        orgState.setModel(smodel);

        smodel = new ArrayList<Item<String>>();
        smodel.add(new Item<String>(null, ""));
        list = CategoryCache.getBySystemName("country");
        for (DictionaryDO d : list) {
            sitem = new Item<String>(d.getEntry(), d.getEntry());
            sitem.setEnabled("Y".equals(d.getIsActive()));
            smodel.add(sitem);
        }
        country.setModel(smodel);
    }

    /**
     * refreshes the screen's view by setting the state and loading the data in
     * the widgets
     */
    public void setData(SampleManager1 manager, State state) {
        this.manager = manager;
        /*
         * disable the types that are not allowed for this sample's domain
         */
        for (Item<Integer> row : type.getModel())
            row.setEnabled(canAddOrganizationType(row.getKey()));

        setState(state);
        fireDataChange();

        if (isState(ADD, UPDATE) && table.getRowCount() > 0)
            table.startEditing(0, 0);
    }

    public void setState(State state) {
        this.state = state;
        bus.fireEventFromSource(new StateChangeEvent(state), this);
    }

    /**
     * overridden to respond to the user clicking "ok"
     */
    public abstract void ok();

    @UiHandler("addOrganizationButton")
    protected void addOrganization(ClickEvent event) {
        int r;

        table.addRow();
        r = table.getRowCount() - 1;
        table.selectRowAt(r);
        table.scrollToVisible(r);
        table.startEditing(r, 0);
    }

    @UiHandler("removeOrganizationButton")
    protected void removeOrganization(ClickEvent event) {
        int r;

        r = table.getSelectedRow();
        if (r > -1 && table.getRowCount() > 0)
            table.removeRowAt(r);
    }

    @UiHandler("okButton")
    protected void ok(ClickEvent event) {
        int numbt, numrt, numbh;
        String accession;
        ValidationErrorsList e;
        SampleOrganizationDO org;

        table.finishEditing();

        if ( !validate()) {
            window.setError(Messages.get().correctErrors());
            return;
        }

        numbt = 0;
        numrt = 0;
        numbh = 0;

        e = new ValidationErrorsList();
        accession = DataBaseUtil.asString(manager.getSample().getAccessionNumber());
        /*
         * the sample can't have more than one report-to, bill-to or birthing
         * hospital
         */
        for (int i = 0; i < manager.organization.count(); i++ ) {
            org = manager.organization.get(i);
            if (Constants.dictionary().ORG_BILL_TO.equals(org.getTypeId()))
                numbt++ ;
            else if (Constants.dictionary().ORG_REPORT_TO.equals(org.getTypeId()))
                numrt++ ;
            else if (Constants.dictionary().ORG_BIRTH_HOSPITAL.equals(org.getTypeId()))
                numbh++ ;
        }

        if (numbt > 1)
            e.add(new FormErrorException(Messages.get().sample_multipleBillToException(accession)));

        if (numrt > 1)
            e.add(new FormErrorException(Messages.get().sample_multipleReportToException(accession)));

        if (numbh > 1)
            e.add(new FormErrorException(Messages.get()
                                                 .sample_multipleBirthHospException(accession)));

        if (e.size() > 0) {
            showErrors(e);
            return;
        }

        window.close();
        ok();
    }

    private ArrayList<Row> getTableModel() {
        ArrayList<Row> model;
        SampleOrganizationViewDO data;
        Row row;

        model = new ArrayList<Row>();

        for (int i = 0; i < manager.organization.count(); i++ ) {
            data = manager.organization.get(i);

            row = new Row(9);
            row.setCell(0, data.getTypeId());
            row.setCell(1, data.getOrganizationAttention());
            row.setCell(2, new AutoCompleteValue(data.getOrganizationId(),
                                                 data.getOrganizationName()));
            row.setCell(3, data.getOrganizationMultipleUnit());
            row.setCell(4, data.getOrganizationStreetAddress());
            row.setCell(5, data.getOrganizationCity());
            row.setCell(6, data.getOrganizationState());
            row.setCell(7, data.getOrganizationZipCode());
            row.setCell(8, data.getOrganizationCountry());
            row.setData(data);
            model.add(row);
        }

        return model;
    }

    private boolean canAddOrganizationType(Integer typeId) {
        String domain;

        domain = manager.getSample().getDomain();

        return (Constants.dictionary().ORG_SECOND_REPORT_TO.equals(typeId) ||
                Constants.dictionary().ORG_REPORT_TO.equals(typeId) ||
                (Constants.dictionary().ORG_BIRTH_HOSPITAL.equals(typeId) && Constants.domain().NEONATAL.equals(domain)));
        //Constants.dictionary().ORG_BILL_TO.equals(typeId) && !Constants.domain().NEONATAL.equals(domain))
    }
}