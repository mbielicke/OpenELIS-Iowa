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

import static org.openelis.modules.main.client.Logger.*;
import static org.openelis.ui.screen.State.*;

import java.util.ArrayList;
import java.util.logging.Level;

import org.openelis.cache.SectionCache;
import org.openelis.cache.UserCache;
import org.openelis.constants.Messages;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.domain.SectionViewDO;
import org.openelis.domain.StorageLocationViewDO;
import org.openelis.domain.StorageViewDO;
import org.openelis.manager.SampleManager1;
import org.openelis.modules.storage.client.StorageService;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.SectionPermission;
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
import com.google.gwt.event.logical.shared.VisibleEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

public class StorageTabUI extends Screen {

    @UiTemplate("StorageTab.ui.xml")
    interface StorageTabUIBinder extends UiBinder<Widget, StorageTabUI> {
    };

    private static StorageTabUIBinder uiBinder = GWT.create(StorageTabUIBinder.class);

    @UiField
    protected Table                   table;

    @UiField
    protected Button                  addStorageButton, removeStorageButton;

    @UiField
    protected AutoComplete            location;

    protected Screen                  parentScreen;

    protected EventBus                parentBus;

    protected boolean                 canEdit, isVisible, redraw;

    protected SampleManager1          manager;

    protected AnalysisViewDO          analysis;

    protected SampleItemViewDO        sampleItem;

    public StorageTabUI(Screen parentScreen) {
        this.parentScreen = parentScreen;
        this.parentBus = parentScreen.getEventBus();
        this.window = parentScreen.getWindow();
        initWidget(uiBinder.createAndBindUi(this));
        initialize();

        manager = null;
    }

    private void initialize() {
        addScreenHandler(table, "table", new ScreenHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent event) {
                table.setModel(getTableModel());
            }

            public void onStateChange(StateChangeEvent event) {
                table.setEnabled(true);
            }

            public Widget onTab(boolean forward) {
                return forward ? addStorageButton : removeStorageButton;
            }
        });
        
        table.addSelectionHandler(new SelectionHandler<Integer>() {
            @Override
            public void onSelection(com.google.gwt.event.logical.shared.SelectionEvent<Integer> event) {
                removeStorageButton.setEnabled(isState(ADD, UPDATE) && canEdit);
            }
        });

        table.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                if ( !canEdit || !isState(ADD, UPDATE))
                    event.cancel();
            }
        });

        table.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                String uid;
                Object val;
                StorageViewDO data;
                StorageLocationViewDO loc;
                AutoCompleteValue sel;

                r = event.getRow();
                c = event.getCol();

                uid = table.getRowAt(r).getData();
                data = (StorageViewDO)manager.getObject(uid);

                val = table.getValueAt(r, c);

                switch (c) {
                    case 1:
                        sel = (AutoCompleteValue)val;
                        if (sel != null) {
                            loc = (StorageLocationViewDO)sel.getData();
                            data.setStorageLocationId(loc.getId());
                            data.setStorageLocationName(loc.getName());
                            data.setStorageLocationLocation(loc.getLocation());
                            data.setStorageUnitDescription(loc.getStorageUnitDescription());
                        } else {
                            data.setStorageLocationId(null);
                            data.setStorageLocationName(null);
                            data.setStorageLocationLocation(null);
                            data.setStorageUnitDescription(null);
                        }
                        break;
                    case 2:
                        data.setCheckin((Datetime)val);
                        if ( !isDateRangeValid(data))
                            table.addException(r,
                                               c,
                                               new Exception(Messages.get()
                                                                     .storage_invalidDateRangeException()));
                        else
                            table.clearExceptions(r, c);
                        break;
                    case 3:
                        data.setCheckout((Datetime)val);
                        if ( !isDateRangeValid(data))
                            table.addException(r,
                                               c,
                                               new Exception(Messages.get()
                                                                     .storage_invalidDateRangeException()));
                        else
                            table.clearExceptions(r, c);
                        break;
                }
            }
        });

        table.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                String uid;
                StorageViewDO data, prevData;
                Datetime date;

                if (analysis != null)
                    data = manager.storage.add(analysis);
                else
                    data = manager.storage.add(sampleItem);

                date = Datetime.getInstance(Datetime.YEAR, Datetime.MINUTE);
                data.setCheckin(date);
                data.setSystemUserId(UserCache.getPermission().getSystemUserId());
                data.setUserName(UserCache.getPermission().getLoginName());
                event.getRow().setData(Constants.uid().getStorage(data.getId()));

                /*
                 * if there were storages present before, then set the checkout
                 * date of the most recent one of them as the checkin date of
                 * the currently added storage, if it doesn't have a checkout
                 * date
                 */
                if (table.getRowCount() > 1) {
                    uid = table.getRowAt(event.getIndex() - 1).getData();
                    prevData = (StorageViewDO)manager.getObject(uid);
                    if (prevData.getCheckout() == null)
                        prevData.setCheckout(date);
                }

                removeStorageButton.setEnabled(true);
            }
        });

        table.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                if (analysis != null)
                    manager.storage.remove(analysis, event.getIndex());
                else
                    manager.storage.remove(sampleItem, event.getIndex());

                removeStorageButton.setEnabled(false);
            }
        });

        location.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                Item<Integer> row;
                ArrayList<Item<Integer>> model;
                ArrayList<StorageLocationViewDO> list;

                parentScreen.setBusy();

                try {
                    list = StorageService.get()
                                         .fetchAvailableByName(QueryFieldUtil.parseAutocomplete(event.getMatch()));
                    model = new ArrayList<Item<Integer>>();
                    for (StorageLocationViewDO data : list) {
                        row = new Item<Integer>(3);

                        row.setKey(data.getId());
                        row.setCell(0, data.getName());
                        row.setCell(1, data.getStorageUnitDescription());
                        row.setCell(2, data.getLocation());
                        row.setData(data);
                        model.add(row);
                    }
                    location.showAutoMatches(model);

                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                }

                parentScreen.clearStatus();
            }
        });

        location.setRenderer(new AutoComplete.Renderer() {
            @Override
            public String getDisplay(Row row) {
                StorageLocationViewDO data;
                /*
                 * for this autocomplete the display is different from the label
                 * shown in the first column of the table in the popup panel
                 */
                if (row == null)
                    return null;

                data = (StorageLocationViewDO)row.getData();
                return getLocationDisplay(data.getName(),
                                          data.getStorageUnitDescription(),
                                          data.getLocation());
            }
        });

        addScreenHandler(addStorageButton, "addStorageButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                addStorageButton.setEnabled(isState(ADD, UPDATE) && canEdit);
            }

            public Widget onTab(boolean forward) {
                return forward ? removeStorageButton : table;
            }
        });

        addScreenHandler(removeStorageButton, "removeStorageButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                removeStorageButton.setEnabled(false);
            }

            public Widget onTab(boolean forward) {
                return forward ? table : addStorageButton;
            }
        });

        addVisibleHandler(new VisibleEvent.Handler() {
            public void onVisibleOrInvisible(VisibleEvent event) {
                isVisible = event.isVisible();
                displayStorages();
            }
        });

        /*
         * handlers for the events fired by the screen containing this tab
         */
        parentBus.addHandler(SelectionEvent.getType(), new SelectionEvent.Handler() {
            public void onSelection(SelectionEvent event) {
                int i, count;
                String uid;
                Object obj;

                if (SelectedType.ANALYSIS.equals(event.getSelectedType()) ||
                    SelectedType.SAMPLE_ITEM.equals(event.getSelectedType()))
                    uid = event.getUid();
                else
                    uid = null;

                analysis = null;
                sampleItem = null;
                if (uid != null) {
                    obj = manager.getObject(uid);
                    if (obj instanceof AnalysisViewDO)
                        analysis = (AnalysisViewDO)obj;
                    else if (obj instanceof SampleItemViewDO)
                        sampleItem = (SampleItemViewDO)obj;
                }

                if (analysis != null) {
                    /*
                     * compare analysis storages
                     */
                    count = manager.storage.count(analysis);
                    if (count == table.getRowCount()) {
                        for (i = 0; i < count; i++ ) {
                            if (isDifferent(manager.storage.get(analysis, i), table.getRowAt(i))) {
                                redraw = true;
                                break;
                            }
                        }
                    } else {
                        redraw = true;
                    }
                } else if (sampleItem != null) {
                    /*
                     * compare sample item storages
                     */
                    count = manager.storage.count(sampleItem);
                    if (count == table.getRowCount()) {
                        for (i = 0; i < count; i++ ) {
                            if (isDifferent(manager.storage.get(sampleItem, i), table.getRowAt(i))) {
                                redraw = true;
                                break;
                            }
                        }
                    } else {
                        redraw = true;
                    }
                } else if (table.getRowCount() > 0) {
                    /*
                     * if neither an analysis nor a sample item is selected and
                     * the table has some data then remove that data
                     */
                    redraw = true;
                }
                setState(state);
                displayStorages();
            }
        });

        parentBus.addHandler(AnalysisChangeEvent.getType(), new AnalysisChangeEvent.Handler() {
            @Override
            public void onAnalysisChange(AnalysisChangeEvent event) {
                if (analysis != null &&
                    (AnalysisChangeEvent.Action.STATUS_CHANGED.equals(event.getAction()) || AnalysisChangeEvent.Action.SECTION_CHANGED.equals(event.getAction()))) {
                    /*
                     * reevaluate the permissions for this section or status to
                     * enable or disable the widgets in the tab
                     */
                    analysis = (AnalysisViewDO)manager.getObject(event.getUid());
                    sampleItem = (SampleItemViewDO)manager.getObject(Constants.uid()
                                                                              .getSampleItem(analysis.getSampleItemId()));
                    setState(state);
                }
            }
        });
    }

    public void setData(SampleManager1 manager) {
        this.manager = manager;
    }

    public void setState(State state) {
        evaluateEdit();
        this.state = state;
        bus.fireEventFromSource(new StateChangeEvent(state), this);
    }

    public void setFocus() {
        /*
         * set the first editable cell in the table in focus if there are any
         * rows, or the button for adding storages if a sample item or analysis
         * is selected in the tree
         */
        if (isState(ADD, UPDATE)) {
            if (table.getRowCount() > 0)
                table.startEditing(0, 2);
            else if (analysis != null || sampleItem != null)
                addStorageButton.setFocus(true);
        }
    }

    @UiHandler("addStorageButton")
    protected void addStorage(ClickEvent event) {
        int n;
        String uid;
        Row row;
        StorageViewDO data;

        table.finishEditing();

        n = table.getRowCount();
        row = table.addRow();
        /*
         * this uid and some other fields are set in the handler for
         * RowAddedEvent, which is fired as soon as addRow is called
         */
        uid = row.getData();
        data = (StorageViewDO)manager.getObject(uid);
        table.setValueAt(n, 0, data.getUserName());
        table.setValueAt(n, 2, data.getCheckin());

        /*
         * refresh the checkout date for the previous most recent storage
         */
        if (table.getRowCount() > 1) {
            uid = table.getRowAt(n - 1).getData();
            data = (StorageViewDO)manager.getObject(uid);
            table.setValueAt(n - 1, 3, data.getCheckout());
        }

        table.scrollToVisible(table.getSelectedRow());
        table.startEditing(n, 1);
    }

    @UiHandler("removeStorageButton")
    protected void removeStorage(ClickEvent event) {
        int r;

        r = table.getSelectedRow();
        if (r > -1 && table.getRowCount() > 0)
            table.removeRowAt(r);
    }

    private void evaluateEdit() {
        Integer sectId, statId;
        SectionPermission perm;
        SectionViewDO sect;

        canEdit = false;
        if (manager != null) {
            if (analysis != null) {
                perm = null;
                sectId = getSectionId();
                statId = getStatusId();
                try {
                    if (sectId != null) {
                        sect = SectionCache.getById(sectId);
                        perm = UserCache.getPermission().getSection(sect.getName());
                    }
                    canEdit = !Constants.dictionary().ANALYSIS_CANCELLED.equals(statId) &&
                              perm != null &&
                              (perm.hasAssignPermission() || perm.hasCompletePermission());
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                }
            } else if (sampleItem != null) {
                canEdit = true;
            }
        }
    }

    private void displayStorages() {
        if ( !isVisible)
            return;

        if (redraw) {
            redraw = false;
            fireDataChange();
        }
    }

    private ArrayList<Row> getTableModel() {
        int i;
        ArrayList<Row> model;

        model = new ArrayList<Row>();
        if (analysis != null) {
            for (i = 0; i < manager.storage.count(analysis); i++ )
                model.add(createRow(manager.storage.get(analysis, i)));
        } else if (sampleItem != null) {
            for (i = 0; i < manager.storage.count(sampleItem); i++ )
                model.add(createRow(manager.storage.get(sampleItem, i)));
        }
        return model;
    }

    private Row createRow(StorageViewDO data) {
        Row row;
        AutoCompleteValue val;

        row = new Row(4);
        row.setCell(0, data.getUserName());

        val = new AutoCompleteValue(data.getStorageLocationId(),
                                    getLocationDisplay(data.getStorageLocationName(),
                                                       data.getStorageUnitDescription(),
                                                       data.getStorageLocationLocation()));
        row.setCell(1, val);
        row.setCell(2, data.getCheckin());
        row.setCell(3, data.getCheckout());

        row.setData(Constants.uid().getStorage(data.getId()));
        return row;
    }

    private Integer getSectionId() {
        if (analysis != null)
            return analysis.getSectionId();

        return null;
    }

    private Integer getStatusId() {
        if (analysis != null)
            return analysis.getStatusId();

        return null;
    }

    private String getLocationDisplay(String name, String description, String location) {
        return DataBaseUtil.concatWithSeparator(name,
                                                ", ",
                                                DataBaseUtil.concatWithSeparator(description,
                                                                                 " ",
                                                                                 location));
    }

    /**
     * returns true if any field in the DO is different from its corresponding
     * cell in the row
     */
    private boolean isDifferent(StorageViewDO data, Row row) {
        Integer id;

        /*
         * the id of the storage location
         */
        if (row.getCell(1) != null)
            id = ((AutoCompleteValue)row.getCell(1)).getId();
        else
            id = null;

        return DataBaseUtil.isDifferent(data.getUserName(), row.getCell(0)) ||
               DataBaseUtil.isDifferent(data.getStorageLocationId(), id) ||
               DataBaseUtil.isDifferentYM(data.getCheckin(), (Datetime)row.getCell(2)) ||
               DataBaseUtil.isDifferentYM(data.getCheckout(), (Datetime)row.getCell(3));
    }

    /**
     * returns true if the DO's checkout date is after the checkin date or if
     * either of them is null
     */
    private boolean isDateRangeValid(StorageViewDO data) {
        return data.getCheckin() == null || data.getCheckout() == null ||
               data.getCheckout().compareTo(data.getCheckin()) > 0;
    }
}