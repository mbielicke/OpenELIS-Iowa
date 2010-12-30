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

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.StorageLocationViewDO;
import org.openelis.domain.StorageViewDO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.LocalizedException;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.GetMatchesEvent;
import org.openelis.gwt.event.GetMatchesHandler;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.AutoCompleteValue;
import org.openelis.gwt.widget.Button;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.Item;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.gwt.widget.Window;
import org.openelis.gwt.widget.table.Row;
import org.openelis.gwt.widget.table.Table;
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
import org.openelis.modules.main.client.openelis.OpenELIS;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;

public class StorageTab extends Screen {
    private boolean                 loaded;

    protected AutoComplete          location;
    protected Table                 storageTable;
    protected Button                addStorageButton, removeStorageButton;

    protected SampleDataBundle      bundle;
    protected StorageManager        manager;

    private Integer                 analysisCancelledId, analysisReleasedId;

    public StorageTab(ScreenDefInt def, Window window) {
        service = new ScreenService("controller?service=org.openelis.modules.storage.server.StorageService");
        setDefinition(def);
        setWindow(window);

        initialize();
        initializeDropdowns();
    }

    private void initialize() {
        storageTable = (Table)def.getWidget("storageTable");
        addScreenHandler(storageTable, new ScreenEventHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent event) {
                storageTable.setModel(getTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                storageTable.setEnabled(canEdit() &&
                                    EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()));
                storageTable.setQueryMode(event.getState() == State.QUERY);
            }
        });

        location = ((AutoComplete)storageTable.getColumnWidget(1));
        storageTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int row, col;
                row = event.getRow();
                col = event.getCol();
                StorageViewDO storageDO;
                Row tableRow = storageTable.getRowAt(row);
                try {
                    storageDO = manager.getStorageAt(row);
                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                    return;
                }

                Object val = tableRow.getCell(col);
                Datetime checkin, checkout;

                switch (col) {
                    case 0:
                        storageDO.setSystemUserId((Integer)val);
                        break;
                    case 1:
                        storageDO.setStorageLocationId(((AutoCompleteValue)val).getId());
                        storageDO.setStorageLocation(((AutoCompleteValue)val).getDisplay());
                        break;
                    case 2:
                        storageDO.setCheckin((Datetime)val);

                        checkin = (Datetime)tableRow.getCell(2);
                        checkout = (Datetime)tableRow.getCell(3);

                        if (checkin != null && checkout != null && checkout.compareTo(checkin) <= 0)
                            storageTable.addException(
                                                          row,
                                                          col,
                                                          new LocalizedException(
                                                                                 "checkinDateAfterCheckoutDateException"));
                        break;
                    case 3:
                        storageDO.setCheckout((Datetime)val);

                        checkin = (Datetime)tableRow.getCell(2);
                        checkout = (Datetime)tableRow.getCell(3);

                        if (checkin != null && checkout != null && checkout.compareTo(checkin) <= 0)
                            storageTable.addException(
                                                          row,
                                                          col,
                                                          new LocalizedException(
                                                                                 "checkinDateAfterCheckoutDateException"));
                        break;
                }
            }
        });
        
        storageTable.addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {
            public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {
                //always allow selection
            }
        });

        storageTable.addSelectionHandler(new SelectionHandler<Integer>() {
            public void onSelection(SelectionEvent<Integer> event) {
                if(EnumSet.of(State.ADD, State.UPDATE).contains(state))
                    removeStorageButton.setEnabled(true);
            }
        });
        
        storageTable.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                try {
                    Row selectedRow = storageTable.getRowAt(0);
                    StorageViewDO storageDO = new StorageViewDO();
                    storageDO.setCheckin((Datetime)selectedRow.getCell(2));
                    storageDO.setSystemUserId(OpenELIS.getSystemUserPermission().getSystemUserId());
                    storageDO.setUserName(OpenELIS.getSystemUserPermission().getLoginName());

                    manager.addStorage(storageDO);
                    removeStorageButton.setEnabled(true);
                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }
            }
        });

        storageTable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                try {
                    manager.removeStorageAt(event.getIndex());
                    removeStorageButton.setEnabled(false);
                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }
            }
        });

        location.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                String locationName;
                QueryFieldUtil parser;
                Item<Integer> row;
                StorageLocationViewDO data;
                ArrayList<StorageLocationViewDO> list;
                ArrayList<Item<Integer>> model;

                parser = new QueryFieldUtil();
                try {
                	parser.parse(event.getMatch());
                }catch(Exception e) {
                	
                }

                window.setBusy();

                try {
                    list = service.callList("fetchAvailableByName", parser.getParameter().get(0));
                    model = new ArrayList<Item<Integer>>();
                    for (int i = 0; i < list.size(); i++ ) {
                        row = new Item<Integer>(3);
                        data = list.get(i);

                        locationName = StorageLocationManager.getLocationForDisplay(data.getName(), data.getStorageUnitDescription(),
                                                                                    data.getLocation());
                        row.setKey(data.getId());
                        row.setCell(0,data.getName());
                        row.setCell(1,data.getStorageUnitDescription());
                        row.setCell(2,data.getLocation());
                        //row.setDisplay(locationName);
                        
                        model.add(row);
                    }
                    location.showAutoMatches(model);

                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }
                window.clearStatus();
            }
        });

        addStorageButton = (Button)def.getWidget("addStorageButton");
        addScreenHandler(addStorageButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                Datetime date = Datetime.getInstance(Datetime.YEAR, Datetime.MINUTE);

                if (storageTable.getRowCount() > 0 && storageTable.getValueAt(0, 3) == null) {
                    manager.getStorageAt(0).setCheckout(date);
                    storageTable.setValueAt(0, 3, date);
                }

                Row newRow = new Row(4);
                newRow.setCell(0,OpenELIS.getSystemUserPermission().getLoginName());
                newRow.setCell(2,date);
                storageTable.addRowAt(0, newRow);
                storageTable.selectRowAt(0);
                storageTable.scrollToVisible(0);
                storageTable.startEditing(0, 1);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addStorageButton.setEnabled(canEdit() &&
                                        EnumSet.of(State.ADD, State.UPDATE)
                                               .contains(event.getState()));
            }
        });

        removeStorageButton = (Button)def.getWidget("removeStorageButton");
        addScreenHandler(removeStorageButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int selectedRow = storageTable.getSelectedRow();
                if (selectedRow > -1 && storageTable.getRowCount() > 0) {
                    storageTable.removeRowAt(selectedRow);
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeStorageButton.setEnabled(false);
            }
        });

    }

    private ArrayList<Row> getTableModel() {
        ArrayList<Row> model = new ArrayList<Row>();

        if (manager == null)
            return model;

        try {
            for (int iter = 0; iter < manager.count(); iter++ ) {
                StorageViewDO storageDO = manager.getStorageAt(iter);

                Row row = new Row(4);
                //row.key = storageDO.getId();

                row.setCell(0,storageDO.getUserName());
                row.setCell(1,new AutoCompleteValue(storageDO.getStorageLocationId(),
                                                          storageDO.getStorageLocation()));
                row.setCell(2,storageDO.getCheckin());
                row.setCell(3,storageDO.getCheckout());

                model.add(row);
            }
        } catch (Exception e) {

            e.printStackTrace();
            return null;
        }

        return model;
    }

    public boolean canEdit() {
        try{
        AnalysisViewDO anDO;
        if (bundle != null) {
            if (SampleDataBundle.Type.ANALYSIS.equals(bundle.getType())) {
                anDO = bundle.getSampleManager().getSampleItems().getAnalysisAt(bundle.getSampleItemIndex()).getAnalysisAt(bundle.getAnalysisIndex());
                
                return (anDO != null &&
                        !analysisCancelledId.equals(anDO.getStatusId()) && !analysisReleasedId.equals(anDO.getStatusId()));
            } else
                return true;
        }
        
        return false;
        }catch(Exception e){
            com.google.gwt.user.client.Window.alert("storageTab canEdit: "+e.getMessage());
            return false;
        }
    }

    private void initializeDropdowns() {
        try {
            analysisCancelledId = DictionaryCache.getIdFromSystemName("analysis_cancelled");
            analysisReleasedId = DictionaryCache.getIdFromSystemName("analysis_released");

        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert(e.getMessage());
            window.close();
        }
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
        	e.printStackTrace();
            com.google.gwt.user.client.Window.alert("storageTab draw: "+ e.getMessage());
        }

        loaded = true;
    }

    public boolean validate() {
        if ( !loaded)
            return true;

        Datetime checkout, checkin;
        boolean returnValue = true;

        for (int i = 0; i < storageTable.getRowCount(); i++ ) {
            checkin = (Datetime)storageTable.getValueAt(i, 2);
            checkout = (Datetime)storageTable.getValueAt(i, 3);

            if (checkin != null && checkout != null && checkout.compareTo(checkin) <= 0) {
                storageTable.addException(
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
