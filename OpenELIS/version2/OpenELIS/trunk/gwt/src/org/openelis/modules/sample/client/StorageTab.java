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
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.table.TableDataRow;
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
import org.openelis.modules.main.client.openelis.OpenELIS;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.Window;

public class StorageTab extends Screen {
    private boolean                 loaded;

    protected AutoComplete<Integer> location;
    protected TableWidget           storageTable;
    protected AppButton             addStorageButton, removeStorageButton;
    protected Integer               userId;
    protected String                userName;

    protected SampleDataBundle      bundle;
    protected StorageManager        manager;

    private Integer                 analysisCancelledId, analysisReleasedId;

    public StorageTab(ScreenDefInt def, ScreenWindow window) {
        service = new ScreenService("OpenELISServlet?service=org.openelis.modules.storage.server.StorageService");
        setDef(def);
        setWindow(window);

        userName = OpenELIS.security.getSystemUserName();
        userId = OpenELIS.security.getSystemUserId();

        initialize();
        initializeDropdowns();
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
                row = event.getRow();
                col = event.getCol();
                StorageViewDO storageDO;
                TableDataRow tableRow = storageTable.getRow(row);
                try {
                    storageDO = manager.getStorageAt(row);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    return;
                }

                Object val = tableRow.cells.get(col).value;
                Datetime checkin, checkout;

                switch (col) {
                    case 0:
                        storageDO.setSystemUserId((Integer)val);
                        break;
                    case 1:
                        TableDataRow selection = location.getSelection();
                        storageDO.setStorageLocationId((Integer) ((TableDataRow)val).key);
                        storageDO.setStorageLocation((String)selection.getCells().get(0));
                        break;
                    case 2:
                        storageDO.setCheckin((Datetime)val);

                        checkin = (Datetime)tableRow.cells.get(2).value;
                        checkout = (Datetime)tableRow.cells.get(3).value;

                        if (checkin != null && checkout != null && checkout.compareTo(checkin) <= 0)
                            storageTable.setCellException(
                                                          row,
                                                          col,
                                                          new LocalizedException(
                                                                                 "checkinDateAfterCheckoutDateException"));
                        break;
                    case 3:
                        storageDO.setCheckout((Datetime)val);

                        checkin = (Datetime)tableRow.cells.get(2).value;
                        checkout = (Datetime)tableRow.cells.get(3).value;

                        if (checkin != null && checkout != null && checkout.compareTo(checkin) <= 0)
                            storageTable.setCellException(
                                                          row,
                                                          col,
                                                          new LocalizedException(
                                                                                 "checkinDateAfterCheckoutDateException"));
                        break;
                }
            }
        });

        storageTable.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                try {
                    TableDataRow selectedRow = storageTable.getRow(0);
                    StorageViewDO storageDO = new StorageViewDO();
                    storageDO.setCheckin((Datetime)selectedRow.cells.get(2).value);
                    storageDO.setSystemUserId(userId);
                    storageDO.setUserName(userName);

                    manager.addStorage(storageDO);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });

        storageTable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                try {
                    manager.removeStorageAt(event.getIndex());

                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });

        location.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                String locationName;
                QueryFieldUtil parser;
                TableDataRow row;
                StorageLocationViewDO data;
                ArrayList<StorageLocationViewDO> list;
                ArrayList<TableDataRow> model;

                parser = new QueryFieldUtil();
                parser.parse(event.getMatch());

                window.setBusy();

                try {
                    list = service.callList("fetchAvailableByName", parser.getParameter().get(0));
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
                newRow.cells.get(0).value = userName;
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
                removeStorageButton.enable(canEdit() &&
                                           EnumSet.of(State.ADD, State.UPDATE)
                                                  .contains(event.getState()));
            }
        });

    }

    private ArrayList<TableDataRow> getTableModel() {
        ArrayList<TableDataRow> model = new ArrayList<TableDataRow>();

        if (manager == null)
            return model;

        try {
            for (int iter = 0; iter < manager.count(); iter++ ) {
                StorageViewDO storageDO = manager.getStorageAt(iter);

                TableDataRow row = new TableDataRow(4);
                row.key = storageDO.getId();

                row.cells.get(0).value = storageDO.getUserName();
                row.cells.get(1).value = new TableDataRow(storageDO.getStorageLocationId(),
                                                          storageDO.getStorageLocation());
                row.cells.get(2).value = storageDO.getCheckin();
                row.cells.get(3).value = storageDO.getCheckout();

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
            Window.alert("storageTab canEdit: "+e.getMessage());
            return false;
        }
    }

    private void initializeDropdowns() {
        try {
            analysisCancelledId = DictionaryCache.getIdFromSystemName("analysis_cancelled");
            analysisReleasedId = DictionaryCache.getIdFromSystemName("analysis_released");

        } catch (Exception e) {
            Window.alert(e.getMessage());
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
