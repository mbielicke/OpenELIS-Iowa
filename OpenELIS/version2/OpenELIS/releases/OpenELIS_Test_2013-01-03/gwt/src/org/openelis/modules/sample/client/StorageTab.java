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
package org.openelis.modules.sample.client;

import java.util.ArrayList;
import java.util.EnumSet;

import org.openelis.cache.SectionCache;
import org.openelis.cache.UserCache;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.SectionViewDO;
import org.openelis.domain.StorageLocationViewDO;
import org.openelis.domain.StorageViewDO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.LocalizedException;
import org.openelis.gwt.common.SectionPermission;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.GetMatchesEvent;
import org.openelis.gwt.event.GetMatchesHandler;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.gwt.widget.ScreenWindowInt;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.CellEditedEvent;
import org.openelis.gwt.widget.table.event.CellEditedHandler;
import org.openelis.gwt.widget.table.event.RowAddedEvent;
import org.openelis.gwt.widget.table.event.RowAddedHandler;
import org.openelis.gwt.widget.table.event.RowDeletedEvent;
import org.openelis.gwt.widget.table.event.RowDeletedHandler;
import org.openelis.manager.AnalysisManager;
import org.openelis.manager.SampleDataBundle;
import org.openelis.manager.SampleItemManager;
import org.openelis.manager.StorageLocationManager;
import org.openelis.manager.StorageManager;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Window;

public class StorageTab extends Screen {
    private boolean                 loaded;

    protected AutoComplete<Integer> location;
    protected TableWidget           storageTable;
    protected AppButton             addStorageButton, removeStorageButton;

    protected SampleDataBundle      bundle;
    protected StorageManager        manager;

    public StorageTab(ScreenDefInt def, ScreenWindowInt window) {
        service = new ScreenService("controller?service=org.openelis.modules.storage.server.StorageService");
        setDefinition(def);
        setWindow(window);

        initialize();
    }

    private void initialize() {
        storageTable = (TableWidget)def.getWidget("storageTable");
        addScreenHandler(storageTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                storageTable.load(getTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                storageTable.enable(canEdit() &&
                                    EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()));
                storageTable.setQueryMode(event.getState() == State.QUERY);
            }
        });

        location = ((AutoComplete<Integer>)storageTable.getColumns().get(1).colWidget);
        storageTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int row, col;
                Object val;
                StorageViewDO data;
                StorageLocationViewDO sloc;
                Datetime checkin, checkout;
                TableDataRow selection, tableRow;
                
                row = event.getRow();
                col = event.getCol();
                tableRow = storageTable.getRow(row);
                try {
                    data = manager.getStorageAt(row);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    return;
                }

                val = tableRow.cells.get(col).value;
                
                switch (col) {
                    case 0:
                        data.setSystemUserId((Integer)val);
                        break;
                    case 1:
                        selection = location.getSelection();
                        if (selection != null) {
                            sloc = (StorageLocationViewDO)selection.data;
                            data.setStorageLocationId(sloc.getId());
                            data.setStorageLocationName(sloc.getName());
                            data.setStorageLocationLocation(sloc.getLocation());
                            data.setStorageUnitDescription(sloc.getStorageUnitDescription());
                        } else {
                            data.setStorageLocationId(null);
                            data.setStorageLocationName(null);
                            data.setStorageLocationLocation(null);
                            data.setStorageUnitDescription(null);
                        }                       
                        break;
                    case 2:
                        data.setCheckin((Datetime)val);

                        checkin = (Datetime)tableRow.cells.get(2).value;
                        checkout = (Datetime)tableRow.cells.get(3).value;

                        if (checkin != null && checkout != null && checkout.compareTo(checkin) <= 0)
                            storageTable.setCellException(row, col,
                                                          new LocalizedException("checkinDateAfterCheckoutDateException"));
                        break;
                    case 3:
                        data.setCheckout((Datetime)val);

                        checkin = (Datetime)tableRow.cells.get(2).value;
                        checkout = (Datetime)tableRow.cells.get(3).value;

                        if (checkin != null && checkout != null && checkout.compareTo(checkin) <= 0)
                            storageTable.setCellException(row, col,
                                                          new LocalizedException("checkinDateAfterCheckoutDateException"));
                        break;
                }
            }
        });
        
        storageTable.addBeforeSelectionHandler(new BeforeSelectionHandler<TableRow>() {
            public void onBeforeSelection(BeforeSelectionEvent<TableRow> event) {
                //always allow selection
            }
        });

        storageTable.addSelectionHandler(new SelectionHandler<TableRow>() {
            public void onSelection(SelectionEvent<TableRow> event) {
                if(EnumSet.of(State.ADD, State.UPDATE).contains(state))
                    removeStorageButton.enable(true);
            }
        });
        
        storageTable.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                try {
                    TableDataRow selectedRow = storageTable.getRow(0);
                    StorageViewDO storageDO = new StorageViewDO();
                    storageDO.setCheckin((Datetime)selectedRow.cells.get(2).value);
                    storageDO.setSystemUserId(UserCache.getPermission().getSystemUserId());
                    storageDO.setUserName(UserCache.getPermission().getLoginName());

                    manager.addStorage(storageDO);
                    removeStorageButton.enable(true);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });

        storageTable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                try {
                    manager.removeStorageAt(event.getIndex());
                    removeStorageButton.enable(false);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });

        location.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                String locationName;
                TableDataRow row;
                StorageLocationViewDO data;
                ArrayList<StorageLocationViewDO> list;
                ArrayList<TableDataRow> model;

                window.setBusy();

                try {
                    list = service.callList("fetchAvailableByName", QueryFieldUtil.parseAutocomplete(event.getMatch()));
                    model = new ArrayList<TableDataRow>();
                    for (int i = 0; i < list.size(); i++ ) {
                        row = new TableDataRow(3);
                        data = list.get(i);

                        locationName = StorageLocationManager.getLocationForDisplay(data.getName(), data.getStorageUnitDescription(),
                                                                                    data.getLocation());
                        row.key = data.getId();
                        row.cells.get(0).value = data.getName();
                        row.cells.get(1).value = data.getStorageUnitDescription();
                        row.cells.get(2).value = data.getLocation();
                        row.display = locationName;
                        row.data = data;
                        model.add(row);
                    }
                    location.showAutoMatches(model);

                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
                window.clearStatus();
            }
        });

        addStorageButton = (AppButton)def.getWidget("addStorageButton");
        addScreenHandler(addStorageButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                Datetime date = Datetime.getInstance(Datetime.YEAR, Datetime.MINUTE);

                if (storageTable.numRows() > 0 && storageTable.getCell(0, 3).value == null) {
                    manager.getStorageAt(0).setCheckout(date);
                    storageTable.setCell(0, 3, date);
                }

                TableDataRow newRow = new TableDataRow(4);
                newRow.cells.get(0).value = UserCache.getPermission().getLoginName();
                newRow.cells.get(2).value = date;
                storageTable.addRow(0, newRow);
                storageTable.selectRow(0);
                storageTable.scrollToSelection();
                storageTable.startEditing(0, 1);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addStorageButton.enable(canEdit() &&
                                        EnumSet.of(State.ADD, State.UPDATE)
                                               .contains(event.getState()));
            }
        });

        removeStorageButton = (AppButton)def.getWidget("removeStorageButton");
        addScreenHandler(removeStorageButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int selectedRow = storageTable.getSelectedRow();
                if (selectedRow > -1 && storageTable.numRows() > 0) {
                    storageTable.deleteRow(selectedRow);
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeStorageButton.enable(false);
            }
        });

    }

    private ArrayList<TableDataRow> getTableModel() {
        String locationName;
        StorageViewDO data;
        TableDataRow row;
        ArrayList<TableDataRow> model;

        model = new ArrayList<TableDataRow>();
        if (manager == null)
            return model;

        try {
            for (int iter = 0; iter < manager.count(); iter++ ) {
                data = manager.getStorageAt(iter);

                row = new TableDataRow(4);
                row.key = data.getId();

                row.cells.get(0).value = data.getUserName();
                locationName = StorageLocationManager.getLocationForDisplay(data.getStorageLocationName(), 
                                                                            data.getStorageUnitDescription(),
                                                                            data.getStorageLocationLocation());
                row.cells.get(1).value = new TableDataRow(data.getStorageLocationId(),
                                                          locationName);
                row.cells.get(2).value = data.getCheckin();
                row.cells.get(3).value = data.getCheckout();

                model.add(row);
            }
        } catch (Exception e) {

            e.printStackTrace();
            return null;
        }

        return model;
    }

    public boolean canEdit() {
        AnalysisViewDO    anDO;
        SectionPermission perm;
        SectionViewDO     sectionVDO;

        if (bundle != null) {
            if (SampleDataBundle.Type.ANALYSIS.equals(bundle.getType())) {
                try {
                    anDO = bundle.getSampleManager().getSampleItems().getAnalysisAt(bundle.getSampleItemIndex()).getAnalysisAt(bundle.getAnalysisIndex());
                    if (anDO != null && anDO.getSectionId() != null) {
                        sectionVDO = SectionCache.getById(anDO.getSectionId());
                        perm = UserCache.getPermission().getSection(sectionVDO.getName());
                        return !Constants.dictionary().ANALYSIS_CANCELLED.equals(anDO.getStatusId()) &&
                               perm != null &&
                               (perm.hasAssignPermission() || perm.hasCompletePermission());
                    }
                } catch(Exception e) {
                    Window.alert("storageTab canEdit: "+e.getMessage());
                    return false;
                }
            } else {
                return true;
            }
        }
            
        return false;
    }

    public void setData(SampleDataBundle data) {
        bundle= data;
        manager = null;
        loaded = false;
    }

    public void draw() {
        SampleItemManager itemMan;
        AnalysisManager anMan;
        
        try {
            if ( !loaded) {
                if(bundle == null){
                    manager = StorageManager.getInstance();
                    StateChangeEvent.fire(this, State.DEFAULT);
                    
                }else if(bundle.getType().equals(SampleDataBundle.Type.SAMPLE_ITEM)){
                    itemMan = bundle.getSampleManager().getSampleItems();
                    manager = itemMan.getStorageAt(bundle.getSampleItemIndex());

                    if (state == State.ADD || state == State.UPDATE)
                        StateChangeEvent.fire(this, State.UPDATE);
                    
                }else if(bundle.getType().equals(SampleDataBundle.Type.ANALYSIS)){
                    anMan = bundle.getSampleManager().getSampleItems().getAnalysisAt(bundle.getSampleItemIndex());
                    manager = anMan.getStorageAt(bundle.getAnalysisIndex());

                    if (state == State.ADD || state == State.UPDATE)
                        StateChangeEvent.fire(this, State.UPDATE);
                    
                }
            }

            DataChangeEvent.fire(this);

        } catch (Exception e) {
            Window.alert("storageTab draw: "+ e.getMessage());
        }

        loaded = true;
    }

    public boolean validate() {
        if ( !loaded)
            return true;

        Datetime checkout, checkin;
        boolean returnValue = true;

        for (int i = 0; i < storageTable.numRows(); i++ ) {
            checkin = (Datetime)storageTable.getObject(i, 2);
            checkout = (Datetime)storageTable.getObject(i, 3);

            if (checkin != null && checkout != null && checkout.compareTo(checkin) <= 0) {
                storageTable.setCellException(
                                              i,
                                              3,
                                              new LocalizedException(
                                                                     "checkinDateAfterCheckoutDateException"));
                returnValue = false;
            }
        }

        return returnValue;
    }
}
