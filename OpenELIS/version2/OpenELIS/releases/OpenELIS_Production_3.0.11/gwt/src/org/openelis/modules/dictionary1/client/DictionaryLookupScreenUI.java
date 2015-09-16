package org.openelis.modules.dictionary1.client;

import static org.openelis.modules.main.client.Logger.logger;
import static org.openelis.ui.screen.State.DEFAULT;

import java.util.ArrayList;
import java.util.Arrays;

import org.openelis.constants.Messages;
import org.openelis.domain.IdNameVO;
import org.openelis.meta.CategoryMeta;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.event.ActionEvent;
import org.openelis.ui.event.ActionHandler;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.HasActionHandlers;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.widget.AtoZButtons;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.TextBox;
import org.openelis.ui.widget.table.Row;
import org.openelis.ui.widget.table.Table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

public class DictionaryLookupScreenUI extends Screen
                                                    implements
                                                    HasActionHandlers<DictionaryLookupScreenUI.Action> {

    @UiTemplate("DictionaryLookup.ui.xml")
    interface DictionaryLookupUiBinder extends UiBinder<Widget, DictionaryLookupScreenUI> {
    };

    public static final DictionaryLookupUiBinder uiBinder = GWT.create(DictionaryLookupUiBinder.class);

    @UiField
    protected TextBox<String>                    findTextBox;

    @UiField
    protected Dropdown<Integer>                  category;

    @UiField
    protected Button                             find, ok, cancel;

    @UiField
    protected AtoZButtons                        atozButtons;

    @UiField
    protected Table                              table;

    protected DictionaryLookupScreenUI           screen;

    private ArrayList<Integer>                   selectionList;

    private DictionaryService1Impl               service  = DictionaryService1Impl.INSTANCE;

    public enum Action {
        OK, CANCEL
    }

    public DictionaryLookupScreenUI() throws Exception {
        setWindow(window);

        initWidget(uiBinder.createAndBindUi(this));

        initialize();
        setState(DEFAULT);

        logger.fine("Dictionary Lookup Screen Opened");
    }

    private void initialize() {
        ArrayList<Item<Integer>> model;
        Item<Integer> row;
        ArrayList<IdNameVO> list;

        screen = this;

        //
        // screen fields
        //
        addScreenHandler(findTextBox, "findTextBox", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                findTextBox.setValue(null);
            }

            public void onStateChange(StateChangeEvent event) {
                findTextBox.setEnabled(true);
            }

            public Widget onTab(boolean forward) {
                return forward ? category : category;
            }
        });

        addScreenHandler(category, "category", new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                category.setValue(null);
            }

            public void onStateChange(StateChangeEvent event) {
                category.setEnabled(true);
            }

            public Widget onTab(boolean forward) {
                return forward ? category : category;
            }
        });

        addScreenHandler(find, "find", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                find.setEnabled(true);
            }

            public Widget onTab(boolean forward) {
                return forward ? category : category;
            }
        });

        addScreenHandler(table, "table", new ScreenHandler<ArrayList<Row>>() {
            public void onStateChange(StateChangeEvent event) {
                table.setEnabled(true);
            }
        });

        addScreenHandler(ok, "ok", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                ok.setEnabled(true);
            }

            public Widget onTab(boolean forward) {
                return forward ? category : category;
            }
        });

        addScreenHandler(cancel, "cancel", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                cancel.setEnabled(true);
            }

            public Widget onTab(boolean forward) {
                return forward ? category : category;
            }
        });

        addScreenHandler(atozButtons, "atozButtons", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                atozButtons.setEnabled(true);
            }
        });

        model = null;
        try {
            list = service.fetchByCategoryName("%");
            model = new ArrayList<Item<Integer>>();
            for (IdNameVO data : list) {
                row = new Item<Integer>(data.getId(), data.getName());
                model.add(row);
            }

        } catch (Exception e) {
            Window.alert(e.getMessage());

        }
        category.setModel(model);
    }

    /*
     * basic button methods
     */
    @UiHandler("atozButtons")
    public void atozQuery(ClickEvent event) {
        String query;

        query = ((Button)event.getSource()).getAction();
        findTextBox.setText(query);
        executeQuery(query);
    }

    @UiHandler("ok")
    public void ok(ClickEvent event) {
        ArrayList<IdNameVO> list;
        Row row;

        list = null;

        selectionList = new ArrayList<Integer>(Arrays.asList(table.getSelectedRows()));
        if (selectionList != null) {
            list = new ArrayList<IdNameVO>();
            for (Integer i : selectionList) {
                row = table.getRowAt(i);
                list.add((IdNameVO)row.getData());
            }
        }

        ActionEvent.fire(this, Action.OK, list);
        window.close();
    }

    @UiHandler("cancel")
    public void cancel(ClickEvent event) {
        ActionEvent.fire(this, Action.CANCEL, null);
        window.close();
    }

    @Override
    public HandlerRegistration addActionHandler(ActionHandler<Action> handler) {
        return addHandler(handler, ActionEvent.getType());
    };

    public void clearFields() {
        fireDataChange();
    }

    public void executeQuery(String pattern) {
        Integer catId;
        Query query;
        QueryData field;
        ArrayList<QueryData> fields;

        if (DataBaseUtil.isEmpty(pattern))
            return;

        findTextBox.setText(pattern);

        query = new Query();
        fields = new ArrayList<QueryData>();
        catId = category.getValue();
        if (catId != null) {
            field = new QueryData();
            field.setKey(CategoryMeta.getId());
            field.setType(QueryData.Type.INTEGER);
            field.setQuery(catId.toString());
            fields.add(field);
        }
        field = new QueryData();
        field.setKey(CategoryMeta.getDictionaryEntry());
        field.setType(QueryData.Type.STRING);
        field.setQuery(pattern);
        fields.add(field);

        field = new QueryData();
        field.setKey(CategoryMeta.getIsSystem());
        field.setType(QueryData.Type.STRING);
        field.setQuery("N");
        fields.add(field);

        query.setFields(fields);

        setBusy(Messages.get().querying());

        service.fetchByEntry(query, new AsyncCallback<ArrayList<IdNameVO>>() {
            public void onSuccess(ArrayList<IdNameVO> result) {
                setQueryResult(result);
            }

            public void onFailure(Throwable error) {
                setQueryResult(null);
                if (error instanceof NotFoundException) {
                    setDone(Messages.get().noRecordsFound());
                } else {
                    Window.alert("Error: Query failed; " + error.getMessage());
                    setError(Messages.get().queryFailed());
                }
            }
        });
    }

    public void setQueryResult(String pattern, ArrayList<IdNameVO> list) {
        if (DataBaseUtil.isEmpty(pattern))
            return;

        findTextBox.setText(pattern);
        setQueryResult(list);
    }

    private void setQueryResult(ArrayList<IdNameVO> list) {
        ArrayList<Row> model;
        Row row;

        model = new ArrayList<Row>();
        if (list != null) {
            for (IdNameVO data : list) {
                row = new Row(data.getName(), data.getDescription());
                row.setData(data);
                model.add(row);
            }
        }

        table.setModel(model);

        setDone(Messages.get().loadCompleteMessage());
    }

}
