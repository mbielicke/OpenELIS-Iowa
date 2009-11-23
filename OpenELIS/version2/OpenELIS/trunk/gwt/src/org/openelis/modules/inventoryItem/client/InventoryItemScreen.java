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

import org.openelis.cache.DictionaryCache;
import org.openelis.common.NotesTab;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdNameVO;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.SecurityException;
import org.openelis.gwt.common.SecurityModule;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.screen.ScreenNavigator;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.ButtonGroup;
import org.openelis.gwt.widget.CheckBox;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.AppButton.ButtonState;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.manager.InventoryItemManager;
import org.openelis.metamap.InventoryItemMetaMap;
import org.openelis.modules.main.client.openelis.OpenELIS;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TabPanel;

public class InventoryItemScreen extends Screen {

    private InventoryItemMetaMap  meta = new InventoryItemMetaMap();
    private InventoryItemManager  manager;
    private SecurityModule        security;

    private ButtonGroup           atoz;
    private ScreenNavigator       nav;

    private ComponentTab          componentTab;
    /*
     * private LocationTab locationTab; private AdditionalTab additionalTab;
     * private ManufacturingTab manufacturingTab;
     */
    private NotesTab              noteTab;
    private Tabs                  tab;

    private AppButton             queryButton, previousButton, nextButton, addButton, updateButton,
                                  commitButton, abortButton;
    private TextBox               id, name, description, quantityMinLevel, quantityToReorder,
                                  quantityMaxLevel, productUri, parentRatio, averageLeadTime, averageCost,
                                  averageDailyUse;
    private CheckBox              isActive, isReorderAuto, isLotMaintained, isSerialMaintained,
                                  isBulk, isNotForSale, isSubAssembly, isLabor, isNotInventoried;
    private Dropdown<String>      categoryId, storeId, dispensedUnitsId;
    private AutoComplete<Integer> parentInventoryItemId;
    private TabPanel              tabPanel;

    private enum Tabs {
        COMPONENT, LOCATION, ADDITIONAL, MANUFACTURING, NOTE
    };

    public InventoryItemScreen() throws Exception {
        super((ScreenDefInt)GWT.create(InventoryItemDef.class));
        service = new ScreenService("controller?service=org.openelis.modules.inventoryItem.server.InventoryItemService");

        security = OpenELIS.security.getModule("inventoryitem");
        if (security == null)
            throw new SecurityException("screenPermException", "Inventory Item Screen");

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
    private void initialize() {
        queryButton = (AppButton)def.getWidget("query");
        addScreenHandler(queryButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                query();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                queryButton.enable(EnumSet.of(State.DEFAULT, State.DISPLAY)
                                          .contains(event.getState()) &&
                                   security.hasSelectPermission());
                if (event.getState() == State.QUERY)
                    queryButton.setState(ButtonState.LOCK_PRESSED);
            }
        });

        previousButton = (AppButton)def.getWidget("previous");
        addScreenHandler(previousButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                previous();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                previousButton.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        nextButton = (AppButton)def.getWidget("next");
        addScreenHandler(nextButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                next();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                nextButton.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        addButton = (AppButton)def.getWidget("add");
        addScreenHandler(addButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                add();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addButton.enable(EnumSet.of(State.DEFAULT, State.DISPLAY)
                                        .contains(event.getState()) &&
                                 security.hasAddPermission());
                if (event.getState() == State.ADD)
                    addButton.setState(ButtonState.LOCK_PRESSED);
            }
        });

        updateButton = (AppButton)def.getWidget("update");
        addScreenHandler(updateButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                update();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                updateButton.enable(EnumSet.of(State.DISPLAY).contains(event.getState()) &&
                                    security.hasUpdatePermission());
                if (event.getState() == State.UPDATE)
                    updateButton.setState(ButtonState.LOCK_PRESSED);
            }
        });

        commitButton = (AppButton)def.getWidget("commit");
        addScreenHandler(commitButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                commit();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                commitButton.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE, State.DELETE)
                                           .contains(event.getState()));
            }
        });

        abortButton = (AppButton)def.getWidget("abort");
        addScreenHandler(abortButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                abort();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                abortButton.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE, State.DELETE)
                                          .contains(event.getState()));
            }
        });

        id = (TextBox)def.getWidget(meta.getId());
        addScreenHandler(id, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                id.setValue(manager.getInventoryItem().getId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getInventoryItem().setId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                id.enable(event.getState() == State.QUERY);
                id.setQueryMode(event.getState() == State.QUERY);
            }
        });

        name = (TextBox)def.getWidget(meta.getName());
        addScreenHandler(name, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                name.setValue(manager.getInventoryItem().getName());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getInventoryItem().setName(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                name.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                   .contains(event.getState()));
                name.setQueryMode(event.getState() == State.QUERY);
            }
        });

        description = (TextBox)def.getWidget(meta.getDescription());
        addScreenHandler(description, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                description.setValue(manager.getInventoryItem().getDescription());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getInventoryItem().setDescription(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                description.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                          .contains(event.getState()));
                description.setQueryMode(event.getState() == State.QUERY);
            }
        });

        categoryId = (Dropdown)def.getWidget(meta.getCategoryId());
        addScreenHandler(categoryId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                categoryId.setSelection(manager.getInventoryItem().getCategoryId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getInventoryItem().setCategoryId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                categoryId.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                         .contains(event.getState()));
                categoryId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        storeId = (Dropdown)def.getWidget(meta.getStoreId());
        addScreenHandler(storeId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                storeId.setSelection(manager.getInventoryItem().getStoreId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getInventoryItem().setStoreId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                storeId.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                      .contains(event.getState()));
                storeId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        quantityMinLevel = (TextBox)def.getWidget(meta.getQuantityMinLevel());
        addScreenHandler(quantityMinLevel, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                quantityMinLevel.setValue(manager.getInventoryItem().getQuantityMinLevel());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getInventoryItem().setQuantityMinLevel(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                quantityMinLevel.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                               .contains(event.getState()));
                quantityMinLevel.setQueryMode(event.getState() == State.QUERY);
            }
        });

        quantityToReorder = (TextBox)def.getWidget(meta.getQuantityToReorder());
        addScreenHandler(quantityToReorder, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                quantityToReorder.setValue(manager.getInventoryItem().getQuantityToReorder());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getInventoryItem().setQuantityToReorder(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                quantityToReorder.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                                .contains(event.getState()));
                quantityToReorder.setQueryMode(event.getState() == State.QUERY);
            }
        });

        quantityMaxLevel = (TextBox)def.getWidget(meta.getQuantityMaxLevel());
        addScreenHandler(quantityMaxLevel, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                quantityMaxLevel.setValue(manager.getInventoryItem().getQuantityMaxLevel());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getInventoryItem().setQuantityMaxLevel(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                quantityMaxLevel.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                               .contains(event.getState()));
                quantityMaxLevel.setQueryMode(event.getState() == State.QUERY);
            }
        });

        dispensedUnitsId = (Dropdown)def.getWidget(meta.getDispensedUnitsId());
        addScreenHandler(dispensedUnitsId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                dispensedUnitsId.setSelection(manager.getInventoryItem().getDispensedUnitsId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getInventoryItem().setDispensedUnitsId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                dispensedUnitsId.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                               .contains(event.getState()));
                dispensedUnitsId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        isActive = (CheckBox)def.getWidget(meta.getIsActive());
        addScreenHandler(isActive, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                isActive.setValue(manager.getInventoryItem().getIsActive());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getInventoryItem().setIsActive(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                isActive.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                       .contains(event.getState()));
                isActive.setQueryMode(event.getState() == State.QUERY);
            }
        });

        isReorderAuto = (CheckBox)def.getWidget(meta.getIsReorderAuto());
        addScreenHandler(isReorderAuto, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                isReorderAuto.setValue(manager.getInventoryItem().getIsReorderAuto());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getInventoryItem().setIsReorderAuto(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                isReorderAuto.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                            .contains(event.getState()));
                isReorderAuto.setQueryMode(event.getState() == State.QUERY);
            }
        });

        isLotMaintained = (CheckBox)def.getWidget(meta.getIsLotMaintained());
        addScreenHandler(isLotMaintained, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                isLotMaintained.setValue(manager.getInventoryItem().getIsLotMaintained());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getInventoryItem().setIsLotMaintained(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                isLotMaintained.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                              .contains(event.getState()));
                isLotMaintained.setQueryMode(event.getState() == State.QUERY);
            }
        });

        isSerialMaintained = (CheckBox)def.getWidget(meta.getIsSerialMaintained());
        addScreenHandler(isSerialMaintained, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                isSerialMaintained.setValue(manager.getInventoryItem().getIsSerialMaintained());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getInventoryItem().setIsSerialMaintained(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                isSerialMaintained.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                                 .contains(event.getState()));
                isSerialMaintained.setQueryMode(event.getState() == State.QUERY);
            }
        });

        isBulk = (CheckBox)def.getWidget(meta.getIsBulk());
        addScreenHandler(isBulk, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                isBulk.setValue(manager.getInventoryItem().getIsBulk());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getInventoryItem().setIsBulk(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                isBulk.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                     .contains(event.getState()));
                isBulk.setQueryMode(event.getState() == State.QUERY);
            }
        });

        isNotForSale = (CheckBox)def.getWidget(meta.getIsNotForSale());
        addScreenHandler(isNotForSale, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                isNotForSale.setValue(manager.getInventoryItem().getIsNotForSale());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getInventoryItem().setIsNotForSale(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                isNotForSale.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                           .contains(event.getState()));
                isNotForSale.setQueryMode(event.getState() == State.QUERY);
            }
        });

        isSubAssembly = (CheckBox)def.getWidget(meta.getIsSubAssembly());
        addScreenHandler(isSubAssembly, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                isSubAssembly.setValue(manager.getInventoryItem().getIsSubAssembly());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getInventoryItem().setIsSubAssembly(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                isSubAssembly.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                            .contains(event.getState()));
                isSubAssembly.setQueryMode(event.getState() == State.QUERY);
            }
        });

        isLabor = (CheckBox)def.getWidget(meta.getIsLabor());
        addScreenHandler(isLabor, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                isLabor.setValue(manager.getInventoryItem().getIsLabor());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getInventoryItem().setIsLabor(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                isLabor.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                      .contains(event.getState()));
                isLabor.setQueryMode(event.getState() == State.QUERY);
            }
        });

        isNotInventoried = (CheckBox)def.getWidget(meta.getIsNotInventoried());
        addScreenHandler(isNotInventoried, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                isNotInventoried.setValue(manager.getInventoryItem().getIsNotInventoried());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getInventoryItem().setIsNotInventoried(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                isNotInventoried.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                            .contains(event.getState()));
                isNotInventoried.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        productUri = (TextBox)def.getWidget(meta.getProductUri());
        addScreenHandler(productUri, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                productUri.setValue(manager.getInventoryItem().getProductUri());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getInventoryItem().setProductUri(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                productUri.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                productUri.setQueryMode(event.getState() == State.QUERY);
            }
        });

        parentInventoryItemId = (AutoComplete)def.getWidget(meta.getParentInventoryItem().getName());
        addScreenHandler(name, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                parentInventoryItemId.setSelection(manager.getInventoryItem().getParentInventoryItemId(),
                                                   manager.getInventoryItem().getParentInventoryItemName());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getInventoryItem().setParentInventoryItemId(event.getValue());
                manager.getInventoryItem().setParentInventoryItemName(parentInventoryItemId.getTextBoxDisplay());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                name.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                name.setQueryMode(event.getState() == State.QUERY);
            }
        });

        parentRatio = (TextBox)def.getWidget(meta.getParentRatio());
        addScreenHandler(parentRatio, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                parentRatio.setValue(manager.getInventoryItem().getParentRatio());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getInventoryItem().setParentRatio(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                parentRatio.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                parentRatio.setQueryMode(event.getState() == State.QUERY);
            }
        });

        averageLeadTime = (TextBox)def.getWidget(meta.getAverageLeadTime());
        addScreenHandler(averageLeadTime, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                averageLeadTime.setValue(manager.getInventoryItem().getAverageLeadTime());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
            }

            public void onStateChange(StateChangeEvent<State> event) {
                averageLeadTime.enable(false);
            }
        });

        averageCost = (TextBox)def.getWidget(meta.getAverageCost());
        addScreenHandler(averageCost, new ScreenEventHandler<Double>() {
            public void onDataChange(DataChangeEvent event) {
                averageCost.setValue(manager.getInventoryItem().getAverageCost());
            }

            public void onValueChange(ValueChangeEvent<Double> event) {
            }

            public void onStateChange(StateChangeEvent<State> event) {
                averageCost.enable(false);
            }
        });

        averageDailyUse = (TextBox)def.getWidget(meta.getAverageDailyUse());
        addScreenHandler(averageDailyUse, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                averageDailyUse.setValue(manager.getInventoryItem().getAverageDailyUse());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
            }

            public void onStateChange(StateChangeEvent<State> event) {
                averageDailyUse.enable(false);
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
/*
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

        additionalTab = new AdditionalTab(def, window);
        addScreenHandler(additionalTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                additionalTab.setManager(manager);
                if (tab == Tabs.ADDITIONAL)
                    drawTabs();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                additionalTab.setState(event.getState());
            }
        });

        manufacturingTab = new ManufacturingTab(def, window);
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

        noteTab = new NoteTab(def, window);
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
*/
        //
        // left hand navigation panel
        //
        nav = new ScreenNavigator(def) {
            public void executeQuery(final Query query) {
                window.setBusy(consts.get("querying"));

                service.callList("query", query, new AsyncCallback<ArrayList<IdNameVO>>() {
                    public void onSuccess(ArrayList<IdNameVO> result) {
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
                            Window.alert("Error: InventoryItem call query failed; " +
                                         error.getMessage());
                            window.setError(consts.get("queryFailed"));
                        }
                    }
                });
            }

            public boolean fetch(RPC entry) {
                return fetchById( (entry == null) ? null : ((IdNameVO)entry).getId());
            }

            public ArrayList<TableDataRow> getModel() {
                ArrayList<IdNameVO> result;
                ArrayList<TableDataRow> model;

                model = null;
                result = nav.getQueryResult();
                if (result != null) {
                    model = new ArrayList<TableDataRow>();
                    for (IdNameVO entry : result)
                        model.add(new TableDataRow(entry.getId(), entry.getName(), entry.getDescription()));
                }
                return model;
            }
        };

        atoz = (ButtonGroup)def.getWidget("atozButtons");
        addScreenHandler(atoz, new ScreenEventHandler<Object>() {
            public void onStateChange(StateChangeEvent<State> event) {
                boolean enable;
                enable = EnumSet.of(State.DEFAULT, State.DISPLAY).contains(event.getState()) &&
                         security.hasSelectPermission();
                atoz.enable(enable);
                nav.enable(enable);
            }

            public void onClick(ClickEvent event) {
                Query query;
                QueryData field;

                field = new QueryData();
                field.key = meta.getName();
                field.query = ((AppButton)event.getSource()).getAction();
                field.type = QueryData.Type.STRING;

                query = new Query();
                query.setFields(field);
                nav.setQuery(query);
            }
        });
    }

    private void initializeDropdowns() {
        ArrayList<TableDataRow> model;

        // category dropdown
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO d : DictionaryCache.getListByCategorySystemName("inventory_category"))
            model.add(new TableDataRow(d.getId(), d.getEntry()));

        categoryId.setModel(model);

        // stores dropdown
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO d : DictionaryCache.getListByCategorySystemName("inventory_store"))
            model.add(new TableDataRow(d.getId(), d.getEntry()));

        storeId.setModel(model);

        // units dropdown
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO d : DictionaryCache.getListByCategorySystemName("inventory_unit"))
            model.add(new TableDataRow(d.getId(), d.getEntry()));

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
/*        locationTab.draw();
        additionalTab.draw();
        manufacturingTab.draw();
        noteTab.draw(); */
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
        manager.getInventoryItem().setIsActive("Y");

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
            Window.alert(e.getMessage());
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
                Window.alert("commitAdd(): " + e.getMessage());
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
                Window.alert("commitUpdate(): " + e.getMessage());
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
                Window.alert(e.getMessage());
                fetchById(null);
            }
            window.setDone(consts.get("updateAborted"));
        } else {
            window.clearStatus();
        }
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
                Window.alert(consts.get("fetchFailed") + e.getMessage());
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
/*
            case LOCATION:
                locationTab.draw();
                break;
            case ADDITIONAL:
                additionalTab.draw();
                break;
            case MANUFACTURING:
                manufacturingTab.draw();
                break;
            case NOTE:
                noteTab.draw();
                break;
    */
        }
    }
}
