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
package org.openelis.modules.inventoryItem.client;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdNameStoreVO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.InventoryComponentViewDO;
import org.openelis.domain.InventoryItemDO;
import org.openelis.domain.InventoryLocationViewDO;
import org.openelis.domain.ReferenceTable;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.ModulePermission;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.PermissionException;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.gwt.event.BeforeCloseEvent;
import org.openelis.gwt.event.BeforeCloseHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.GetMatchesEvent;
import org.openelis.gwt.event.GetMatchesHandler;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.screen.ScreenNavigator;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.Button;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.ButtonGroup;
import org.openelis.gwt.widget.CheckBox;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.Item;
import org.openelis.gwt.widget.MenuItem;
import org.openelis.gwt.widget.Window;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.table.Row;
import org.openelis.gwt.widget.table.Table;
import org.openelis.manager.InventoryComponentManager;
import org.openelis.manager.InventoryItemManager;
import org.openelis.manager.InventoryLocationManager;
import org.openelis.manager.StorageLocationManager;
import org.openelis.meta.InventoryItemMeta;
import org.openelis.modules.history.client.HistoryScreen;
import org.openelis.modules.main.client.openelis.OpenELIS;
import org.openelis.modules.note.client.NotesTab;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TabPanel;

public class InventoryItemScreen extends Screen {

    private InventoryItemManager  manager;
    private ModulePermission      userPermission;

    private ButtonGroup           atoz;
    private ScreenNavigator       nav;

    private ComponentTab          componentTab;
    private LocationTab           locationTab;
    private ManufacturingTab      manufacturingTab;
    private NotesTab              noteTab;
    private Tabs                  tab;

    private Button                queryButton, previousButton, nextButton, addButton, updateButton,
                                  commitButton, abortButton;
    protected MenuItem            invItemHistory,invComponentHistory,invLocationHistory;
    private TextBox               id, name, description, quantityMinLevel, quantityToReorder,
                                  quantityMaxLevel, productUri, parentRatio, averageLeadTime, averageCost,
                                  averageDailyUse;
    private CheckBox              isActive, isReorderAuto, isLotMaintained, isSerialMaintained,
                                  isBulk, isNotForSale, isSubAssembly, isLabor, isNotInventoried;
    private Dropdown<Integer>      categoryId, storeId, dispensedUnitsId;
    private AutoComplete          parentInventoryItemId;
    private TabPanel              tabPanel;

    private enum Tabs {
        COMPONENT, LOCATION, ADDITIONAL, MANUFACTURING, NOTE
    };

    public InventoryItemScreen() throws Exception {
        super((ScreenDefInt)GWT.create(InventoryItemDef.class));
        service = new ScreenService("controller?service=org.openelis.modules.inventoryItem.server.InventoryItemService");

        userPermission = OpenELIS.getSystemUserPermission().getModule("inventoryitem");
        if (userPermission == null)
            throw new PermissionException("screenPermException", "Inventory Item Screen");

        DeferredCommand.addCommand(new Command() {
            public void execute() {
                postConstructor();
            }
        });
    }

    /**
     * This method is called to set the initial state of widgets after the
     * screen is attached to the browser. It is usually called in deferred
     * command.
     */
    private void postConstructor() {
        tab = Tabs.COMPONENT;
        manager = InventoryItemManager.getInstance();

        initialize();
        setState(State.DEFAULT);
        initializeDropdowns();
        DataChangeEvent.fire(this);
    }

    /**
     * Setup state and data change handles for every widget on the screen
     */
    @SuppressWarnings("unchecked")
    private void initialize() {
        queryButton = (Button)def.getWidget("query");
        addScreenHandler(queryButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                query();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                queryButton.setEnabled(EnumSet.of(State.DEFAULT, State.DISPLAY)
                                          .contains(event.getState()) &&
                                   userPermission.hasSelectPermission());
                if (event.getState() == State.QUERY) {
                    queryButton.setPressed(true);
                    queryButton.lock();
                }
            }
        });

        previousButton = (Button)def.getWidget("previous");
        addScreenHandler(previousButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                previous();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                previousButton.setEnabled(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        nextButton = (Button)def.getWidget("next");
        addScreenHandler(nextButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                next();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                nextButton.setEnabled(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        addButton = (Button)def.getWidget("add");
        addScreenHandler(addButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                add();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addButton.setEnabled(EnumSet.of(State.DEFAULT, State.DISPLAY)
                                        .contains(event.getState()) &&
                                 userPermission.hasAddPermission());
                if (event.getState() == State.ADD) {
                    addButton.setPressed(true);
                    addButton.lock();
                }
            }
        });

        updateButton = (Button)def.getWidget("update");
        addScreenHandler(updateButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                update();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                updateButton.setEnabled(EnumSet.of(State.DISPLAY).contains(event.getState()) &&
                                    userPermission.hasUpdatePermission());
                if (event.getState() == State.UPDATE) {
                    updateButton.setPressed(true);
                    updateButton.lock();
                }
            }
        });

        commitButton = (Button)def.getWidget("commit");
        addScreenHandler(commitButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                commit();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                commitButton.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE, State.DELETE)
                                           .contains(event.getState()));
            }
        });

        abortButton = (Button)def.getWidget("abort");
        addScreenHandler(abortButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                abort();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                abortButton.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE, State.DELETE)
                                          .contains(event.getState()));
            }
        });
        
        invItemHistory = (MenuItem)def.getWidget("invItemHistory");
        addScreenHandler(invItemHistory, new ScreenEventHandler<Object>() {
            public void onStateChange(StateChangeEvent<State> event) {
                invItemHistory.setEnabled(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });
        
        invItemHistory.addCommand(new Command() {
			public void execute() {
				invItemHistory();
			}
		});
        
        invComponentHistory = (MenuItem)def.getWidget("invComponentHistory");
        addScreenHandler(invComponentHistory, new ScreenEventHandler<Object>() {
            public void onStateChange(StateChangeEvent<State> event) {
                invComponentHistory.setEnabled(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });
        
        invComponentHistory.addCommand(new Command() {
			public void execute() {
				invComponentHistory();
			}
		});
        
        invLocationHistory = (MenuItem)def.getWidget("invLocationHistory");
        addScreenHandler(invLocationHistory, new ScreenEventHandler<Object>() {
            public void onStateChange(StateChangeEvent<State> event) {
                invLocationHistory.setEnabled(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });
        
        invLocationHistory.addCommand(new Command() {
			public void execute() {
				invLocationHistory();
			}
		});

        id = (TextBox)def.getWidget(InventoryItemMeta.getId());
        addScreenHandler(id, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                id.setValue(manager.getInventoryItem().getId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getInventoryItem().setId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                id.setEnabled(event.getState() == State.QUERY);
                id.setQueryMode(event.getState() == State.QUERY);
            }
        });

        name = (TextBox)def.getWidget(InventoryItemMeta.getName());
        addScreenHandler(name, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                name.setValue(manager.getInventoryItem().getName());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getInventoryItem().setName(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                name.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                   .contains(event.getState()));
                name.setQueryMode(event.getState() == State.QUERY);
            }
        });

        description = (TextBox)def.getWidget(InventoryItemMeta.getDescription());
        addScreenHandler(description, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                description.setValue(manager.getInventoryItem().getDescription());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getInventoryItem().setDescription(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                description.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                          .contains(event.getState()));
                description.setQueryMode(event.getState() == State.QUERY);
            }
        });

        categoryId = (Dropdown<Integer>)def.getWidget(InventoryItemMeta.getCategoryId());
        addScreenHandler(categoryId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                categoryId.setValue(manager.getInventoryItem().getCategoryId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getInventoryItem().setCategoryId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                categoryId.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                         .contains(event.getState()));
                categoryId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        storeId = (Dropdown)def.getWidget(InventoryItemMeta.getStoreId());
        addScreenHandler(storeId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                storeId.setValue(manager.getInventoryItem().getStoreId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getInventoryItem().setStoreId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                storeId.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                      .contains(event.getState()));
                storeId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        quantityMinLevel = (TextBox)def.getWidget(InventoryItemMeta.getQuantityMinLevel());
        addScreenHandler(quantityMinLevel, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                quantityMinLevel.setValue(manager.getInventoryItem().getQuantityMinLevel());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getInventoryItem().setQuantityMinLevel(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                quantityMinLevel.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                               .contains(event.getState()));
                quantityMinLevel.setQueryMode(event.getState() == State.QUERY);
            }
        });

        quantityToReorder = (TextBox)def.getWidget(InventoryItemMeta.getQuantityToReorder());
        addScreenHandler(quantityToReorder, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                quantityToReorder.setValue(manager.getInventoryItem().getQuantityToReorder());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getInventoryItem().setQuantityToReorder(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                quantityToReorder.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                                .contains(event.getState()));
                quantityToReorder.setQueryMode(event.getState() == State.QUERY);
            }
        });

        quantityMaxLevel = (TextBox)def.getWidget(InventoryItemMeta.getQuantityMaxLevel());
        addScreenHandler(quantityMaxLevel, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                quantityMaxLevel.setValue(manager.getInventoryItem().getQuantityMaxLevel());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getInventoryItem().setQuantityMaxLevel(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                quantityMaxLevel.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                               .contains(event.getState()));
                quantityMaxLevel.setQueryMode(event.getState() == State.QUERY);
            }
        });

        dispensedUnitsId = (Dropdown)def.getWidget(InventoryItemMeta.getDispensedUnitsId());
        addScreenHandler(dispensedUnitsId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                dispensedUnitsId.setValue(manager.getInventoryItem().getDispensedUnitsId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getInventoryItem().setDispensedUnitsId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                dispensedUnitsId.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                               .contains(event.getState()));
                dispensedUnitsId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        isActive = (CheckBox)def.getWidget(InventoryItemMeta.getIsActive());
        addScreenHandler(isActive, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                isActive.setValue(manager.getInventoryItem().getIsActive());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getInventoryItem().setIsActive(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                isActive.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                       .contains(event.getState()));
                isActive.setQueryMode(event.getState() == State.QUERY);
            }
        });

        isReorderAuto = (CheckBox)def.getWidget(InventoryItemMeta.getIsReorderAuto());
        addScreenHandler(isReorderAuto, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                isReorderAuto.setValue(manager.getInventoryItem().getIsReorderAuto());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getInventoryItem().setIsReorderAuto(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                isReorderAuto.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                            .contains(event.getState()));
                isReorderAuto.setQueryMode(event.getState() == State.QUERY);
            }
        });

        isLotMaintained = (CheckBox)def.getWidget(InventoryItemMeta.getIsLotMaintained());
        addScreenHandler(isLotMaintained, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                isLotMaintained.setValue(manager.getInventoryItem().getIsLotMaintained());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getInventoryItem().setIsLotMaintained(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                isLotMaintained.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                              .contains(event.getState()));
                isLotMaintained.setQueryMode(event.getState() == State.QUERY);
            }
        });

        isSerialMaintained = (CheckBox)def.getWidget(InventoryItemMeta.getIsSerialMaintained());
        addScreenHandler(isSerialMaintained, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                isSerialMaintained.setValue(manager.getInventoryItem().getIsSerialMaintained());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getInventoryItem().setIsSerialMaintained(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                isSerialMaintained.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                                 .contains(event.getState()));
                isSerialMaintained.setQueryMode(event.getState() == State.QUERY);
            }
        });

        isBulk = (CheckBox)def.getWidget(InventoryItemMeta.getIsBulk());
        addScreenHandler(isBulk, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                isBulk.setValue(manager.getInventoryItem().getIsBulk());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getInventoryItem().setIsBulk(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                isBulk.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                     .contains(event.getState()));
                isBulk.setQueryMode(event.getState() == State.QUERY);
            }
        });

        isNotForSale = (CheckBox)def.getWidget(InventoryItemMeta.getIsNotForSale());
        addScreenHandler(isNotForSale, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                isNotForSale.setValue(manager.getInventoryItem().getIsNotForSale());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getInventoryItem().setIsNotForSale(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                isNotForSale.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                           .contains(event.getState()));
                isNotForSale.setQueryMode(event.getState() == State.QUERY);
            }
        });

        isSubAssembly = (CheckBox)def.getWidget(InventoryItemMeta.getIsSubAssembly());
        addScreenHandler(isSubAssembly, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                isSubAssembly.setValue(manager.getInventoryItem().getIsSubAssembly());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getInventoryItem().setIsSubAssembly(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                isSubAssembly.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                            .contains(event.getState()));
                isSubAssembly.setQueryMode(event.getState() == State.QUERY);
            }
        });

        isLabor = (CheckBox)def.getWidget(InventoryItemMeta.getIsLabor());
        addScreenHandler(isLabor, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                isLabor.setValue(manager.getInventoryItem().getIsLabor());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getInventoryItem().setIsLabor(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                isLabor.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                      .contains(event.getState()));
                isLabor.setQueryMode(event.getState() == State.QUERY);
            }
        });

        isNotInventoried = (CheckBox)def.getWidget(InventoryItemMeta.getIsNotInventoried());
        addScreenHandler(isNotInventoried, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                isNotInventoried.setValue(manager.getInventoryItem().getIsNotInventoried());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getInventoryItem().setIsNotInventoried(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                isNotInventoried.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                            .contains(event.getState()));
                isNotInventoried.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        productUri = (TextBox)def.getWidget(InventoryItemMeta.getProductUri());
        addScreenHandler(productUri, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                productUri.setValue(manager.getInventoryItem().getProductUri());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getInventoryItem().setProductUri(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                productUri.setEnabled(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                productUri.setQueryMode(event.getState() == State.QUERY);
            }
        });

        parentInventoryItemId = (AutoComplete)def.getWidget(InventoryItemMeta.getParentInventoryItemName());
        addScreenHandler(parentInventoryItemId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                parentInventoryItemId.setValue(manager.getInventoryItem().getParentInventoryItemId(),
                                                   manager.getInventoryItem().getParentInventoryItemName());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getInventoryItem().setParentInventoryItemId(event.getValue());
                manager.getInventoryItem().setParentInventoryItemName(parentInventoryItemId.getDisplay());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                parentInventoryItemId.setEnabled(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                parentInventoryItemId.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        parentInventoryItemId.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                InventoryItemDO data;
                Item<Integer> row;
                ArrayList<InventoryItemDO> list;
                ArrayList<Item<Integer>> model;
                DictionaryDO store, units;

                try {
                    list = service.callList("fetchActiveByName", event.getMatch());
                    model = new ArrayList<Item<Integer>>();

                    for (int i = 0; i < list.size(); i++ ) {
                        data = (InventoryItemDO) list.get(i);
                        store = DictionaryCache.getEntryFromId(data.getStoreId());
                        units = DictionaryCache.getEntryFromId(data.getDispensedUnitsId());
                        row = new Item<Integer>(data.getId(), data.getName(),
                                               store.getEntry(), units.getEntry());
                        row.setData(data);
                        model.add(row);
                    }
                    parentInventoryItemId.showAutoMatches(model);
                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }
            }                                        
        });

        parentRatio = (TextBox)def.getWidget(InventoryItemMeta.getParentRatio());
        addScreenHandler(parentRatio, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                parentRatio.setValue(manager.getInventoryItem().getParentRatio());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getInventoryItem().setParentRatio(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                parentRatio.setEnabled(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                parentRatio.setQueryMode(event.getState() == State.QUERY);
            }
        });

        averageLeadTime = (TextBox)def.getWidget(InventoryItemMeta.getAverageLeadTime());
        addScreenHandler(averageLeadTime, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                averageLeadTime.setValue(manager.getInventoryItem().getAverageLeadTime());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
            }

            public void onStateChange(StateChangeEvent<State> event) {
                averageLeadTime.setEnabled(false);
            }
        });

        averageCost = (TextBox)def.getWidget(InventoryItemMeta.getAverageCost());
        addScreenHandler(averageCost, new ScreenEventHandler<Double>() {
            public void onDataChange(DataChangeEvent event) {
                averageCost.setValue(manager.getInventoryItem().getAverageCost());
            }

            public void onValueChange(ValueChangeEvent<Double> event) {
            }

            public void onStateChange(StateChangeEvent<State> event) {
                averageCost.setEnabled(false);
            }
        });

        averageDailyUse = (TextBox)def.getWidget(InventoryItemMeta.getAverageDailyUse());
        addScreenHandler(averageDailyUse, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                averageDailyUse.setValue(manager.getInventoryItem().getAverageDailyUse());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
            }

            public void onStateChange(StateChangeEvent<State> event) {
                averageDailyUse.setEnabled(false);
            }
        });
        
        //
        // tabs
        //
        tabPanel = (TabPanel)def.getWidget("tabPanel");
        tabPanel.addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {
            public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {
                int i;

                // tab screen order should be the same as enum or this will
                // not work
                i = event.getItem().intValue();
                tab = Tabs.values()[i];

                window.setBusy();
                drawTabs();
                window.clearStatus();
            }
        });

        componentTab = new ComponentTab(def, window);
        addScreenHandler(componentTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                componentTab.setManager(manager);
                if (tab == Tabs.COMPONENT)
                    drawTabs();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                componentTab.setState(event.getState());
            }
        });

        locationTab = new LocationTab(def, window);
        addScreenHandler(locationTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                locationTab.setManager(manager);
                if (tab == Tabs.LOCATION)
                    drawTabs();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                locationTab.setState(event.getState());
            }
        });

        manufacturingTab = new ManufacturingTab(def, window, "manufacturingPanel", "editManufacturingButton");
        addScreenHandler(manufacturingTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                manufacturingTab.setManager(manager);
                if (tab == Tabs.MANUFACTURING)
                    drawTabs();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                manufacturingTab.setState(event.getState());
            }
        });

        noteTab = new NotesTab(def, window, "notesPanel", "standardNoteButton");
        addScreenHandler(noteTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                noteTab.setManager(manager);
                if (tab == Tabs.NOTE)
                    drawTabs();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                noteTab.setState(event.getState());
            }
        });

        //
        // left hand navigation panel
        //
        nav = new ScreenNavigator(def) {
            public void executeQuery(final Query query) {
                window.setBusy(consts.get("querying"));

                service.callList("query", query, new AsyncCallback<ArrayList<IdNameStoreVO>>() {
                    public void onSuccess(ArrayList<IdNameStoreVO> result) {
                        setQueryResult(result);
                    }

                    public void onFailure(Throwable error) {
                        setQueryResult(null);
                        if (error instanceof NotFoundException) {
                            window.setDone(consts.get("noRecordsFound"));
                            setState(State.DEFAULT);
                        } else if (error instanceof LastPageException) {
                            window.setError("No more records in this direction");
                        } else {
                            com.google.gwt.user.client.Window.alert("Error: InventoryItem call query failed; " +
                                         error.getMessage());
                            window.setError(consts.get("queryFailed"));
                        }
                    }
                });
            }

            public boolean fetch(RPC entry) {
                return fetchById( (entry == null) ? null : ((IdNameStoreVO)entry).getId());
            }

            public ArrayList<Item<Integer>> getModel() {
                ArrayList<IdNameStoreVO> result;
                ArrayList<Item<Integer>> model;

                model = null;
                result = nav.getQueryResult();
                if (result != null) {
                    model = new ArrayList<Item<Integer>>();
                    for (IdNameStoreVO entry : result)
                        model.add(new Item<Integer>(entry.getId(), entry.getName(), entry.getStore()));
                }
                return model;
            }
        };

        atoz = (ButtonGroup)def.getWidget("atozButtons");
        addScreenHandler(atoz, new ScreenEventHandler<Object>() {
            public void onStateChange(StateChangeEvent<State> event) {
                boolean enable;
                enable = EnumSet.of(State.DEFAULT, State.DISPLAY).contains(event.getState()) &&
                         userPermission.hasSelectPermission();
                atoz.setEnabled(enable);
                nav.enable(enable);
            }

            public void onClick(ClickEvent event) {
                Query query;
                QueryData field;

                field = new QueryData(InventoryItemMeta.getName(),QueryData.Type.STRING,((Button)event.getSource()).getAction());

                query = new Query();
                query.setFields(field);
                nav.setQuery(query);
            }
        });
        
        window.addBeforeClosedHandler(new BeforeCloseHandler<Window>() {
            public void onBeforeClosed(BeforeCloseEvent<Window> event) {                
                if (EnumSet.of(State.ADD, State.UPDATE).contains(state)) {
                    event.cancel();
                    window.setError(consts.get("mustCommitOrAbort"));
                }
            }
        });
    }

    private void initializeDropdowns() {
        Table atozTable;
        Dropdown<Integer> atozStoreId;
        ArrayList<Item<Integer>> model;
        List<DictionaryDO> list;
        Item<Integer> row;

        // category dropdown
        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        list = DictionaryCache.getListByCategorySystemName("inventory_category");
        for (DictionaryDO d : list) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled("Y".equals(d.getIsActive()));
            model.add(row);
        }
        categoryId.setModel(model);

        // stores dropdown
        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        list = DictionaryCache.getListByCategorySystemName("inventory_store");
        for (DictionaryDO d : DictionaryCache.getListByCategorySystemName("inventory_store")) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled("Y".equals(d.getIsActive()));
            model.add(row);
        }
        storeId.setModel(model);
        
        // add the same store model to left hand side atoz table
        atozTable = (Table)def.getWidget("atozTable");
        atozStoreId = (Dropdown)atozTable.getColumnWidget(1);
        atozStoreId.setModel(model);
        
        // units dropdown
        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        list = DictionaryCache.getListByCategorySystemName("inventory_unit");
        for (DictionaryDO d : list) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled("Y".equals(d.getIsActive()));
            model.add(row);
        }
        dispensedUnitsId.setModel(model);
    }

    /*
     * basic button methods
     */

    protected void query() {
        manager = InventoryItemManager.getInstance();

        setState(State.QUERY);
        DataChangeEvent.fire(this);

        // clear all the tabs
        componentTab.draw();
        locationTab.draw();
        manufacturingTab.draw();
        noteTab.draw();
        setFocus(id);
        window.setDone(consts.get("enterFieldsToQuery"));
    }

    protected void previous() {
        nav.previous();
    }

    protected void next() {
        nav.next();
    }

    protected void add() {
        manager = InventoryItemManager.getInstance();
        manager.getInventoryItem().setIsReorderAuto("N");
        manager.getInventoryItem().setIsLotMaintained("N");
        manager.getInventoryItem().setIsSerialMaintained("N");
        manager.getInventoryItem().setIsActive("Y");
        manager.getInventoryItem().setIsBulk("N");
        manager.getInventoryItem().setIsNotForSale("N");
        manager.getInventoryItem().setIsSubAssembly("N");
        manager.getInventoryItem().setIsLabor("N");
        manager.getInventoryItem().setIsNotInventoried("N");

        setState(State.ADD);
        DataChangeEvent.fire(this);

        setFocus(name);
        window.setDone(consts.get("enterInformationPressCommit"));
    }

    protected void update() {
        window.setBusy(consts.get("lockForUpdate"));

        try {
            manager = manager.fetchForUpdate();

            setState(State.UPDATE);
            DataChangeEvent.fire(this);
            setFocus(name);
        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert(e.getMessage());
        }
        window.clearStatus();
    }

    public void commit() {
        setFocus(null);

        if ( !validate()) {
            window.setError(consts.get("correctErrors"));
            return;
        }

        if (state == State.QUERY) {
            Query query;

            query = new Query();
            query.setFields(getQueryFields());
            nav.setQuery(query);
        } else if (state == State.ADD) {
            window.setBusy(consts.get("adding"));
            try {
                manager = manager.add();

                setState(State.DISPLAY);
                DataChangeEvent.fire(this);
                window.setDone(consts.get("addingComplete"));
            } catch (ValidationErrorsList e) {
                showErrors(e);
            } catch (Exception e) {
                com.google.gwt.user.client.Window.alert("commitAdd(): " + e.getMessage());
                window.clearStatus();
            }
        } else if (state == State.UPDATE) {
            window.setBusy(consts.get("updating"));
            try {
                manager = manager.update();

                setState(State.DISPLAY);
                DataChangeEvent.fire(this);
                window.setDone(consts.get("updatingComplete"));
            } catch (ValidationErrorsList e) {
                showErrors(e);
            } catch (Exception e) {
                com.google.gwt.user.client.Window.alert("commitUpdate(): " + e.getMessage());
                window.clearStatus();
            }
        }
    }

    protected void abort() {
        setFocus(null);
        clearErrors();
        window.setBusy(consts.get("cancelChanges"));

        if (state == State.QUERY) {
            fetchById(null);
            window.setDone(consts.get("queryAborted"));
        } else if (state == State.ADD) {
            fetchById(null);
            window.setDone(consts.get("addAborted"));
        } else if (state == State.UPDATE) {
            try {
                manager = manager.abortUpdate();
                setState(State.DISPLAY);
                DataChangeEvent.fire(this);
            } catch (Exception e) {
                com.google.gwt.user.client.Window.alert(e.getMessage());
                fetchById(null);
            }
            window.setDone(consts.get("updateAborted"));
        } else {
            window.clearStatus();
        }
    }
    
    protected void invItemHistory() {
        IdNameVO hist;
        
        hist = new IdNameVO(manager.getInventoryItem().getId(), manager.getInventoryItem().getName());
        HistoryScreen.showHistory(consts.get("invItemHistory"),
                                  ReferenceTable.INVENTORY_ITEM, hist);                
    }
    
    protected void invComponentHistory() {
        int i, count;
        IdNameVO refVoList[];
        InventoryComponentManager man;
        InventoryComponentViewDO data;

        try {
            man = manager.getComponents();
            count = man.count();
            refVoList = new IdNameVO[count];
            for (i = 0; i < count; i++ ) {
                data = man.getComponentAt(i);
                refVoList[i] = new IdNameVO(data.getId(), data.getComponentName());
            }
        } catch (Exception e) {
            e.printStackTrace();
            com.google.gwt.user.client.Window.alert(e.getMessage());
            return;
        }

        HistoryScreen.showHistory(consts.get("invComponentHistory"),
                                  ReferenceTable.INVENTORY_COMPONENT, refVoList);
    }
    
    protected void invLocationHistory() {
        int i, count;
        String locationName;
        IdNameVO refVoList[];
        InventoryLocationManager man;
        InventoryLocationViewDO data;

        try {
            man = manager.getLocations();
            count = man.count();
            refVoList = new IdNameVO[count];
            for (i = 0; i < count; i++ ) {
                data = man.getLocationAt(i);
                locationName = StorageLocationManager.getLocationForDisplay(data.getStorageLocationName(),
                                                                            data.getStorageLocationUnitDescription(),
                                                                            data.getStorageLocationLocation());
                refVoList[i] = new IdNameVO(data.getId(), locationName);
            }
        } catch (Exception e) {
            e.printStackTrace();
            com.google.gwt.user.client.Window.alert(e.getMessage());
            return;
        }

        HistoryScreen.showHistory(consts.get("invLocationHistory"),
                                  ReferenceTable.INVENTORY_LOCATION, refVoList);
    }

    protected boolean fetchById(Integer id) {
        if (id == null) {
            manager = InventoryItemManager.getInstance();
            setState(State.DEFAULT);
        } else {
            window.setBusy(consts.get("fetching"));
            try {
                switch (tab) {
                    case COMPONENT:
                        manager = InventoryItemManager.fetchWithComponents(id);
                        break;
                    case LOCATION:
                        manager = InventoryItemManager.fetchWithLocations(id);
                        break;
                    case ADDITIONAL:
                        manager = InventoryItemManager.fetchById(id);
                        break;
                    case MANUFACTURING:
                        manager = InventoryItemManager.fetchWithManufacturing(id);
                        break;
                    case NOTE:
                        manager = InventoryItemManager.fetchWithNotes(id);
                        break;
                }
                setState(State.DISPLAY);
            } catch (NotFoundException e) {
                fetchById(null);
                window.setDone(consts.get("noRecordsFound"));
                return false;
            } catch (Exception e) {
                fetchById(null);
                e.printStackTrace();
                com.google.gwt.user.client.Window.alert(consts.get("fetchFailed") + e.getMessage());
                return false;
            }
        }
        DataChangeEvent.fire(this);
        window.clearStatus();

        return true;
    }

    private void drawTabs() {

        switch (tab) {
            case COMPONENT:
                componentTab.draw();
                break;
            case LOCATION:
                locationTab.draw();
                break;
            case MANUFACTURING:
                manufacturingTab.draw();
                break;
            case NOTE:
                noteTab.draw();
                break;
        }
    }
}