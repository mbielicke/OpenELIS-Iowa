package org.openelis.modules.todo1.client;

import static org.openelis.modules.main.client.Logger.logger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import org.openelis.cache.CategoryCache;
import org.openelis.cache.UserCache;
import org.openelis.constants.Messages;
import org.openelis.domain.AnalysisViewVO;
import org.openelis.domain.DictionaryDO;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.SystemUserPermission;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.table.Row;
import org.openelis.ui.widget.table.Table;
import org.openelis.ui.widget.table.event.BeforeCellEditedEvent;
import org.openelis.ui.widget.table.event.BeforeCellEditedHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.VisibleEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

public class ReleasedTabUI extends Screen {

    @UiTemplate("ReleasedTab.ui.xml")
    interface ReleasedTabUiBinder extends UiBinder<Widget, ReleasedTabUI> {
    };

    private static ReleasedTabUiBinder uiBinder = GWT.create(ReleasedTabUiBinder.class);

    @UiField
    protected Table                    table;

    @UiField
    protected Dropdown<String>         domain;

    protected Screen                   parentScreen;

    protected EventBus                 parentBus;

    private boolean                    loadedFromCache;

    private boolean                    isVisible;

    private String                     loadBySection;

    private ArrayList<AnalysisViewVO>  fullList;

    public ReleasedTabUI(Screen parentScreen) {
        this.parentScreen = parentScreen;
        parentBus = parentScreen.getEventBus();
        initWidget(uiBinder.createAndBindUi(this));
        initialize();
    }

    private void initialize() {
        ArrayList<Item<String>> model;
        Item<String> row;
        List<DictionaryDO> list;

        addScreenHandler(table, "releasedTable", new ScreenHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent event) {
                loadTableModel();
            }

            public void onStateChange(StateChangeEvent event) {
                table.setEnabled(true);
            }
        });

        table.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                event.cancel();
            }
        });

        addVisibleHandler(new VisibleEvent.Handler() {
            public void onVisibleOrInvisible(VisibleEvent event) {
                isVisible = event.isVisible();
                draw(loadBySection);
            }
        });

        loadBySection = "N";

        model = new ArrayList<Item<String>>();
        list = CategoryCache.getBySystemName("sample_domain");
        for (DictionaryDO data : list) {
            row = new Item<String>(data.getCode(), data.getEntry());
            model.add(row);
        }
        domain.setModel(model);
    }

    public void onDataChange(String mySection) {
        loadedFromCache = false;
        draw(mySection);
    }

    public Integer getSelectedId() {
        Row row;
        AnalysisViewVO data;

        row = table.getRowAt(table.getSelectedRow());
        if (row == null) {
            return null;
        }
        data = (AnalysisViewVO)row.getData();
        return data.getSampleId();
    }

    public void draw(String loadBySection) {
        if ( !isVisible) {
            return;
        }
        if ( ( !loadedFromCache) || ( !loadBySection.equals(this.loadBySection))) {
            this.loadBySection = loadBySection;
            fireDataChange();
        }
        loadedFromCache = true;
    }

    private void loadTableModel() {
        if (loadedFromCache) {
            ArrayList<Row> model = getTableModel();
            table.setModel(model);
            sortRows(0, 1);
        } else {
            parentScreen.setBusy(Messages.get().fetching());
            ToDoService1Impl.INSTANCE.getReleased(new AsyncCallback<ArrayList<AnalysisViewVO>>() {
                public void onSuccess(ArrayList<AnalysisViewVO> result) {
                    fullList = result;
                    table.setModel(getTableModel());
                    parentScreen.clearStatus();
                }

                public void onFailure(Throwable error) {
                    if (error instanceof NotFoundException) {
                        parentScreen.setDone(Messages.get().gen_noRecordsFound());
                    } else {
                        Window.alert(error.getMessage());
                        logger.log(Level.SEVERE, error.getMessage(), error);
                        parentScreen.clearStatus();
                    }

                }
            });
        }
    }

    private ArrayList<Row> getTableModel() {
        boolean sectOnly;
        String sectName;
        Date temp;
        Datetime scd, sct;
        Row row;
        SystemUserPermission perm;
        ArrayList<Row> model;

        model = new ArrayList<Row>();
        perm = UserCache.getPermission();
        sectOnly = "Y".equals(loadBySection);
        try {
            for (AnalysisViewVO data : fullList) {
                sectName = data.getSectionName();
                if ( ( !sectOnly) || (perm.getSection(sectName) != null)) {
                    row = new Row(10);
                    row.setCell(0, data.getAccessionNumber());
                    row.setCell(1, data.getDomain());
                    row.setCell(2, sectName);
                    row.setCell(3, data.getTestName());
                    row.setCell(4, data.getMethodName());

                    scd = data.getCollectionDate();
                    sct = data.getCollectionTime();
                    if (scd != null) {
                        temp = scd.getDate();
                        if (sct == null) {
                            temp.setHours(0);
                            temp.setMinutes(0);
                        } else {
                            temp.setHours(sct.getDate().getHours());
                            temp.setMinutes(sct.getDate().getMinutes());
                        }
                        row.setCell(5, Datetime.getInstance((byte)0, (byte)4, temp));
                    }
                    row.setCell(6, data.getReleasedDate());
                    row.setCell(7, data.getAnalysisResultOverride());
                    row.setCell(8, data.getToDoDescription());
                    row.setCell(9, data.getPrimaryOrganizationName());
                    row.setData(data);
                    model.add(row);
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            Window.alert(e.getMessage());
        }
        return model;
    }

    private void sortRows(int col, int dir) {
        try {
            table.applySort(col, dir, null);
        } catch (Exception e) {
            Window.alert("error: " + e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}
