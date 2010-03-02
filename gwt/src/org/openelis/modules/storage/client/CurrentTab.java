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
package org.openelis.modules.storage.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.openelis.domain.StorageLocationViewDO;
import org.openelis.domain.StorageViewDO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Calendar;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.gwt.widget.tree.TreeDataItem;
import org.openelis.gwt.widget.tree.TreeWidget;
import org.openelis.manager.StorageLocationChildManager;
import org.openelis.manager.StorageLocationManager;
import org.openelis.manager.StorageManager;
import org.openelis.manager.StorageViewManager;
import org.openelis.modules.main.client.openelis.OpenELIS;
import org.openelis.modules.storageLocation.client.StorageLocationLookupScreen;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.user.client.Window;

public class CurrentTab extends Screen {

    private StorageViewManager                       manager;
    private TreeWidget                               storageCurrentTree;
    private AppButton                                moveItemsButton, discardItemsButton;
    private boolean                                  loaded, treeFetched;
    private HashMap<Integer, TreeDataItem>           idItemMap;
    private StorageLocationLookupScreen              storageLocationLookup;
    private HashMap<Integer, StorageManager>         storageCache;
    private HashMap<Integer, StorageLocationManager> storageLocationCache;

    public CurrentTab(ScreenDefInt def, ScreenWindow window) {
        setDef(def);
        setWindow(window);
        initialize();
    }

    private void initialize() {
        idItemMap = new HashMap<Integer, TreeDataItem>();
        storageCache = new HashMap<Integer, StorageManager>();
        storageLocationCache = new HashMap<Integer, StorageLocationManager>();

        storageCurrentTree = (TreeWidget)def.getWidget("storageCurrentTree");
        addScreenHandler(storageCurrentTree, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                storageCurrentTree.load(getTreeModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                storageCurrentTree.enable(true);
            }
        });

        storageCurrentTree.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                event.cancel();
            }
        });

        storageCurrentTree.addBeforeSelectionHandler(new BeforeSelectionHandler<TreeDataItem>() {
            public void onBeforeSelection(BeforeSelectionEvent<TreeDataItem> event) {
                TreeDataItem item;
                boolean isStorage;
                StorageViewDO data;

                item = event.getItem();
                isStorage = "storage".equals(item.leafType);

                if (state == State.UPDATE) {
                    moveItemsButton.enable(isStorage);
                    discardItemsButton.enable(isStorage);

                    if (isStorage) {
                        data = (StorageViewDO)item.data;
                        if (data.getCheckout() != null) {
                            window.setError(consts.get("cantSelectItem"));
                            event.cancel();
                        } else {
                            window.clearStatus();
                        }
                    }
                }
            }
        });

        storageCurrentTree.multiSelect(true);

        moveItemsButton = (AppButton)def.getWidget("moveItemsButton");
        addScreenHandler(moveItemsButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                showStorageLocation();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                moveItemsButton.enable(false);
            }
        });

        discardItemsButton = (AppButton)def.getWidget("discardItemsButton");
        addScreenHandler(discardItemsButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                discardStorageItems();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                discardItemsButton.enable(false);
            }
        });

    }

    private ArrayList<TreeDataItem> getTreeModel() {
        int i, count;
        Integer id;
        TreeDataItem item;
        StorageLocationViewDO data;
        ArrayList<TreeDataItem> model;
        StorageLocationChildManager slcm;
        StorageLocationManager slm;

        model = new ArrayList<TreeDataItem>();

        if (manager == null)
            return model;

        slm = manager.getStorageLocation();

        if (slm == null)
            return model;

        try {
            idItemMap.clear();

            slcm = slm.getChildren();
            count = slcm.count();

            if (count == 0) {
                data = slm.getStorageLocation();
                item = new TreeDataItem(1);
                item.leafType = "locationName";
                item.close();
                id = data.getId();
                item.key = id;
                item.cells.get(0).setValue(data.getName());
                item.checkForChildren(false);
                idItemMap.put(id, item);
                model.add(item);
            }

            for (i = 0; i < count; i++ ) {
                data = slcm.getChildAt(i);
                item = new TreeDataItem(1);
                item.leafType = "locationName";
                item.close();
                id = data.getId();
                item.key = data.getId();
                item.cells.get(0).setValue(
                                           data.getStorageUnitDescription() + "," +
                                                           data.getLocation());
                item.checkForChildren(false);
                idItemMap.put(id, item);
                model.add(item);
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
        }

        addStorageItems();
        return model;
    }

    private void addStorageItems() {
        int i;
        TreeDataItem row;
        StorageViewDO data;
        StorageManager sm;
        TreeDataItem item;

        if (manager == null || treeFetched)
            return;
        try {
            window.setBusy(consts.get("fetching"));
            sm = manager.getCurrent();
            for (i = 0; i < sm.count(); i++ ) {
                data = sm.getStorageAt(i);
                item = idItemMap.get(data.getStorageLocationId());
                item.checkForChildren(true);
                row = new TreeDataItem(4);
                row.leafType = "storage";
                row.cells.get(0).setValue(data.getItemDescription());
                row.cells.get(1).setValue(data.getUserName());
                row.cells.get(2).setValue(data.getCheckin());
                row.cells.get(3).setValue(data.getCheckout());
                row.data = data;

                item.addItem(row);
            }

            treeFetched = true;
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
        }

        window.clearStatus();
    }

    public void setManager(StorageViewManager manager) {
        this.manager = manager;
        loaded = false;
    }

    public void draw() {
        if ( !loaded) {
            storageCache = new HashMap<Integer, StorageManager>();
            storageLocationCache = new HashMap<Integer, StorageLocationManager>();
            treeFetched = false;
            DataChangeEvent.fire(this);
        }

        loaded = true;
    }

    public Collection<StorageManager> getStorageList() {
        return storageCache.values();
    }

    public Collection<StorageLocationManager> getLocationList() {
        return storageLocationCache.values();
    }

    private void showStorageLocation() {
        ScreenWindow modal;

        if (storageLocationLookup == null) {
            try {
                storageLocationLookup = new StorageLocationLookupScreen();
            } catch (Exception e) {
                e.printStackTrace();
                Window.alert("StorageLocationLookup Error: " + e.getMessage());
                return;
            }

            storageLocationLookup.addActionHandler(new ActionHandler<StorageLocationLookupScreen.Action>() {
                public void onAction(ActionEvent<StorageLocationLookupScreen.Action> event) {
                    StorageLocationViewDO data;
                    Integer parentId, locId, userId;
                    StorageManager man;
                    StorageViewDO tmpstorage, oldstorage;
                    StorageLocationManager locMan;
                    TreeDataItem item;
                    ArrayList<TreeDataItem> list;
                    Datetime current;

                    if (event.getAction() == StorageLocationLookupScreen.Action.OK) {
                        data = (StorageLocationViewDO)event.getData();
                        if (data != null) {
                            parentId = data.getParentStorageLocationId();
                            if (parentId == null)
                                parentId = data.getId();
                            try {
                                //
                                // we need to get a lock on the parent storage
                                // location
                                // of the storage location that was selected
                                // through the
                                // pop up screen; thus we call fetchForUpdate
                                // for
                                // the id of the parent storage location and put
                                // it in the
                                // hashmap so that we won't try to lock the same
                                // id more
                                // than once
                                //
                                locMan = storageLocationCache.get(parentId);
                                if (locMan == null) {
                                    locMan = StorageLocationManager.getInstance();
                                    locMan.setStorageLocationId(parentId);
                                    locMan = locMan.fetchForUpdate();
                                    storageLocationCache.put(parentId, locMan);
                                }

                                locId = data.getId();
                                man = storageCache.get(locId);
                                if (man == null) {
                                    man = StorageManager.getInstance();
                                    storageCache.put(locId, man);
                                }

                                list = storageCurrentTree.getSelections();
                                current = Calendar.getCurrentDatetime(Datetime.YEAR,
                                                                      Datetime.MINUTE);
                                userId = OpenELIS.security.getSystemUserId();
                                for (int i = 0; i < list.size(); i++ ) {
                                    item = list.get(i);
                                    oldstorage = (StorageViewDO)item.data;

                                    if (oldstorage.getStorageLocationId().equals(data.getId())) {
                                        window.setError(consts.get("itemsCantBeMoved"));
                                        continue;
                                    }

                                    oldstorage.setCheckout(current);

                                    tmpstorage = new StorageViewDO();
                                    tmpstorage.setCheckin(current);
                                    tmpstorage.setCheckout(null);
                                    tmpstorage.setItemDescription(oldstorage.getItemDescription());
                                    tmpstorage.setReferenceId(oldstorage.getReferenceId());
                                    tmpstorage.setReferenceTableId(oldstorage.getReferenceTableId());
                                    tmpstorage.setStorageLocationId(locId);
                                    tmpstorage.setSystemUserId(userId);
                                    man.addStorage(tmpstorage);

                                    item.cells.get(3).setValue(current);
                                    storageCurrentTree.refreshRow(item);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Window.alert("StorageLocationLookup Error: " + e.getMessage());
                            }
                        }
                    }
                }
            });
        }
        window.clearStatus();
        modal = new ScreenWindow(ScreenWindow.Mode.DIALOG);
        modal.setName(consts.get("storageLocationSelection"));
        modal.setContent(storageLocationLookup);
        storageLocationLookup.setScreenState(State.DEFAULT);
        storageLocationLookup.clearFields();
    }

    private void discardStorageItems() {
        StorageViewDO oldstorage;
        TreeDataItem item;
        ArrayList<TreeDataItem> list;
        Datetime current;

        try {
            list = storageCurrentTree.getSelections();
            current = Calendar.getCurrentDatetime(Datetime.YEAR, Datetime.MINUTE);
            for (int i = 0; i < list.size(); i++ ) {
                item = list.get(i);
                oldstorage = (StorageViewDO)item.data;

                //
                // if checkout date is not null then we
                // know that this item has already been
                // moved to some other location
                //
                if (oldstorage.getCheckout() == null) {
                    oldstorage.setCheckout(current);

                    item.cells.get(3).setValue(current);

                    storageCurrentTree.refreshRow(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
        }

    }
}
