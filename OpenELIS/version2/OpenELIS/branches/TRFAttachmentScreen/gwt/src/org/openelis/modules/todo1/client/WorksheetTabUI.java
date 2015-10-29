package org.openelis.modules.todo1.client;

import static org.openelis.modules.main.client.Logger.logger;

import java.util.ArrayList;
import java.util.logging.Level;

import org.openelis.cache.UserCache;
import org.openelis.constants.Messages;
import org.openelis.domain.ToDoWorksheetVO;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.SystemUserPermission;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
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

public class WorksheetTabUI extends Screen {
    @UiTemplate("WorksheetTab.ui.xml")
    interface WorksheetTabUiBinder extends UiBinder<Widget, WorksheetTabUI> {
    };

    private static WorksheetTabUiBinder uiBinder = GWT.create(WorksheetTabUiBinder.class);

    @UiField
    protected Table                     table;

    protected Screen                    parentScreen;

    protected EventBus                  parentBus;

    private boolean                     loadedFromCache;

    private boolean                     isVisible;

    private String                      loadBySection;

    private ArrayList<ToDoWorksheetVO>  fullList;

    public WorksheetTabUI(Screen parentScreen) {
        this.parentScreen = parentScreen;
        parentBus = parentScreen.getEventBus();
        initWidget((Widget)uiBinder.createAndBindUi(this));
        initialize();
    }

    private void initialize() {
        addScreenHandler(table, "worksheetTable", new ScreenHandler<ArrayList<Row>>() {
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
    }

    public void onDataChange(String mySection) {
        loadedFromCache = false;
        draw(mySection);
    }

    public Integer getSelectedId() {
        Row row;
        ToDoWorksheetVO data;

        row = table.getRowAt(table.getSelectedRow());
        if (row == null) {
            return null;
        }
        data = (ToDoWorksheetVO)row.getData();
        return data.getId();
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
            table.setModel(getTableModel());
        } else {
            parentScreen.setBusy(Messages.get().fetching());
            ToDoService1Impl.INSTANCE.getWorksheet(new AsyncCallback<ArrayList<ToDoWorksheetVO>>() {
                public void onSuccess(ArrayList<ToDoWorksheetVO> result) {
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
        Row row;
        SystemUserPermission perm;
        ArrayList<Row> model;

        model = new ArrayList<Row>();
        perm = UserCache.getPermission();
        sectOnly = "Y".equals(loadBySection);
        try {
            for (ToDoWorksheetVO data : fullList) {
                sectName = data.getSectionName();
                if ( ( !sectOnly) || (perm.getSection(sectName) != null)) {
                    row = new Row(5);
                    row.setCell(0, data.getId());
                    row.setCell(1, data.getSystemUserName());
                    row.setCell(2, sectName);
                    row.setCell(3, data.getDescription());
                    row.setCell(4, data.getCreatedDate());
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
}
