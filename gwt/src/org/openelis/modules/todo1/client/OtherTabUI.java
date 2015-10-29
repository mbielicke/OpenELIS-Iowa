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

public class OtherTabUI extends Screen {
    @UiTemplate("OtherTab.ui.xml")
    interface OtherTabUiBinder extends UiBinder<Widget, OtherTabUI> {
    };

    private static OtherTabUiBinder   uiBinder = GWT.create(OtherTabUiBinder.class);

    @UiField
    protected Table                   table;

    @UiField
    protected Dropdown<String>        domain;

    @UiField
    protected Dropdown<Integer>       status;

    protected Screen                  parentScreen;

    protected EventBus                parentBus;

    private boolean                   loadedFromCache;

    private boolean                   isVisible;

    private String                    loadBySection;

    private ArrayList<AnalysisViewVO> fullList;

    public OtherTabUI(Screen parentScreen) {
        this.parentScreen = parentScreen;
        parentBus = parentScreen.getEventBus();
        initWidget(uiBinder.createAndBindUi(this));
        initialize();
    }

    private void initialize() {
        ArrayList<Item<String>> smodel;
        ArrayList<Item<Integer>> model;
        Item<String> srow;
        Item<Integer> row;
        List<DictionaryDO> list;

        addScreenHandler(table, "otherTable", new ScreenHandler<ArrayList<Row>>() {
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

        smodel = new ArrayList<Item<String>>();
        list = CategoryCache.getBySystemName("sample_domain");
        for (DictionaryDO data : list) {
            srow = new Item<String>(data.getCode(), data.getEntry());
            smodel.add(srow);
        }
        domain.setModel(smodel);

        model = new ArrayList<Item<Integer>>();
        list = CategoryCache.getBySystemName("sample_status");
        for (DictionaryDO data : list) {
            row = new Item<Integer>(data.getId(), data.getEntry());
            model.add(row);
        }
        status.setModel(model);
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
            table.setModel(getTableModel());
        } else {
            parentScreen.setBusy(Messages.get().fetching());
            ToDoService1Impl.INSTANCE.getOther(new AsyncCallback<ArrayList<AnalysisViewVO>>() {
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
                    row = new Row(11);
                    row.setCell(0, data.getAccessionNumber());
                    row.setCell(1, data.getDomain());
                    row.setCell(2, sectName);
                    row.setCell(3, data.getAnalysisStatusId());
                    row.setCell(4, data.getTestName());
                    row.setCell(5, data.getMethodName());

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
                        row.setCell(6, Datetime.getInstance((byte)0, (byte)4, temp));
                    }
                    row.setCell(7, data.getReceivedDate());
                    row.setCell(8, data.getAnalysisResultOverride());
                    row.setCell(9, data.getToDoDescription());
                    row.setCell(10, data.getPrimaryOrganizationName());
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
