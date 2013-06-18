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
import java.util.EnumSet;

import org.openelis.cache.SectionCache;
import org.openelis.cache.UserCache;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.domain.SectionViewDO;
import org.openelis.domain.StorageLocationViewDO;
import org.openelis.domain.StorageViewDO;
import org.openelis.ui.event.GetMatchesEvent;
import org.openelis.ui.event.GetMatchesHandler;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.manager.SampleManager1;
import org.openelis.modules.storage.client.StorageService;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.SectionPermission;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.AutoComplete;
import org.openelis.ui.widget.AutoCompleteValue;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.table.Row;
import org.openelis.ui.widget.table.Table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.VisibleEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

public class StorageTabUI extends Screen {

    @UiTemplate("StorageTab.ui.xml")
    interface StorageTabUIBinder extends UiBinder<Widget, StorageTabUI> {
    };

    private static StorageTabUIBinder uiBinder = GWT.create(StorageTabUIBinder.class);

    @UiField
    protected Table                   storageTable;

    @UiField
    protected Button                  addStorageButton, removeStorageButton;

    @UiField
    protected AutoComplete            location;

    protected String                  displayedUid;

    protected Screen                  parentScreen;

    protected boolean                 canEdit, isVisible;

    protected SampleManager1          manager;

    protected AnalysisViewDO          analysis;

    protected SampleItemViewDO        sampleItem;

    public StorageTabUI(Screen parentScreen, EventBus bus) {
        this.parentScreen = parentScreen;
        setEventBus(bus);
        initWidget(uiBinder.createAndBindUi(this));
        initialize();

        manager = null;
        displayedUid = null;
    }

    private void initialize() {
        addScreenHandler(storageTable, "storageTable", new ScreenHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent event) {
                storageTable.setModel(getTableModel());
            }

            public void onStateChange(StateChangeEvent event) {
                storageTable.setEnabled(isState(QUERY) || (isState(ADD, UPDATE) && canEdit));
                storageTable.setQueryMode(isState(QUERY));
            }
        });

        location.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                Item<Integer> row;
                ArrayList<Item<Integer>> model;
                ArrayList<StorageLocationViewDO> list;

                //TODO how to use "window" in a tab
                //window.setBusy();

                try {
                    list = StorageService.get()
                                         .fetchAvailableByName(QueryFieldUtil.parseAutocomplete(event.getMatch()));
                    model = new ArrayList<Item<Integer>>();
                    for (StorageLocationViewDO data : list) {
                        row = new Item<Integer>(3);

                        row.setCell(0, data.getName());
                        row.setCell(1, data.getStorageUnitDescription());
                        row.setCell(2, data.getLocation());
                        //TODO what should be done about the display?
                        /*row.s .display = locationName;
                        locationName = getStorageLocation(data.getName(),
                                                          data.getStorageUnitDescription(),
                                                          data.getLocation());*/
                        row.setData(data);
                        row.setKey(data.getId());
                        model.add(row);
                    }
                    location.showAutoMatches(model);

                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
                
                //window.clearStatus();
            }
        });

        addScreenHandler(addStorageButton, "addStorageButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                addStorageButton.setEnabled(isState(ADD, UPDATE) && canEdit);
            }
        });

        addScreenHandler(removeStorageButton, "removeStorageButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                removeStorageButton.setEnabled(false);
            }
        });

        /*
         * handlers for the events fired by the screen containing this tab
         */
        bus.addHandlerToSource(StateChangeEvent.getType(),
                               parentScreen,
                               new StateChangeEvent.Handler() {
                                   public void onStateChange(StateChangeEvent event) {
                                       evaluateEdit();
                                       setState(event.getState());
                                   }
                               });

        bus.addHandler(SelectionEvent.getType(), new SelectionEvent.Handler() {
            public void onSelection(SelectionEvent event) {
                String uid;

                if (SelectedType.ANALYSIS.equals(event.getSelectedType()) ||
                    SelectedType.SAMPLE_ITEM.equals(event.getSelectedType()))
                    uid = event.getUid();
                else
                    uid = null;

                displayStorage(uid);
            }
        });

        addVisibleHandler(new VisibleEvent.Handler() {
            public void onVisibleOrInvisible(VisibleEvent event) {
                String uid;

                isVisible = event.isVisible();
                if (analysis != null)
                    uid = manager.getUid(analysis);
                else if (sampleItem != null)
                    uid = manager.getUid(sampleItem);
                else
                    uid = null;

                displayStorage(uid);
            }
        });
    }

    public void setData(SampleManager1 manager) {
        if ( !DataBaseUtil.isSame(this.manager, manager))
            this.manager = manager;
    }
    
    public void setState(State state) {
        this.state = state;
        bus.fireEventFromSource(new StateChangeEvent(state), this);
    }

    private ArrayList<Row> getTableModel() {
        ArrayList<Row> model;

        model = new ArrayList<Row>();
        if (analysis != null) {
            for (int i = 0; i < manager.storage.count(analysis); i++ )
                model.add(createStorageRow(manager.storage.get(analysis, i)));
        } else if (sampleItem != null) {
            for (int i = 0; i < manager.storage.count(sampleItem); i++ )
                model.add(createStorageRow(manager.storage.get(sampleItem, i)));
        }
        return model;
    }

    private Row createStorageRow(StorageViewDO s) {
        Row row;
        AutoCompleteValue val;

        row = new Row(4);
        row.setCell(0, s.getUserName());

        val = new AutoCompleteValue(s.getStorageLocationId(),
                                    getStorageLocation(s.getStorageLocationName(),
                                                       s.getStorageUnitDescription(),
                                                       s.getStorageLocationLocation()));
        row.setCell(1, val);
        row.setCell(2, s.getCheckin());
        row.setCell(3, s.getCheckout());

        return row;
    }

    private void displayStorage(String uid) {
        Object obj;
        /*
         * don't redraw unless the data has changed
         */
        if (uid != null) {
            obj = manager.getObject(uid);
            if (obj instanceof AnalysisViewDO) {
                analysis = (AnalysisViewDO)obj;
                sampleItem = null;
            } else if (obj instanceof SampleItemViewDO) {
                sampleItem = (SampleItemViewDO)obj;
                analysis = null;
            }
        } else {
            analysis = null;
            sampleItem = null;
        }

        if ( !isVisible)
            return;

        // TODO compare prev and new data
        if (DataBaseUtil.isDifferent(displayedUid, uid)) {
            displayedUid = uid;
            evaluateEdit();
            setState(state);
            fireDataChange();
        }
    }

    private void evaluateEdit() {
        Integer sectId, statId;
        SectionPermission perm;
        SectionViewDO sect;

        canEdit = false;
        if (manager != null) {
            if (analysis != null) {
                sectId = getSectionId();
                statId = getStatusId();
                try {
                    if (sectId != null) {
                        sect = SectionCache.getById(sectId);
                        perm = UserCache.getPermission().getSection(sect.getName());
                        canEdit = !Constants.dictionary().ANALYSIS_CANCELLED.equals(statId) &&
                                  perm != null &&
                                  (perm.hasAssignPermission() || perm.hasCompletePermission());
                    }
                } catch (Exception e) {
                    Window.alert("storageTab canEdit: " + e.getMessage());
                }
            } else if (sampleItem != null) {
                canEdit = true;
            }
        }
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

    private String getStorageLocation(String name, String description, String location) {
        ArrayList<String> loc;

        loc = new ArrayList<String>();

        loc.add(name);
        loc.add(", ");
        loc.add(description);
        loc.add(" ");
        loc.add(location);

        return DataBaseUtil.concatWithSeparator(loc, "");
    }
}