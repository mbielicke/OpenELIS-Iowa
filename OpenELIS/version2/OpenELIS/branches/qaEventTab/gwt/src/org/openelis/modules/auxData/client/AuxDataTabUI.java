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
package org.openelis.modules.auxData.client;

import static org.openelis.modules.main.client.Logger.*;
import static org.openelis.ui.screen.State.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

import org.openelis.cache.CacheProvider;
import org.openelis.cache.DictionaryCache;
import org.openelis.constants.Messages;
import org.openelis.domain.AuxDataViewDO;
import org.openelis.domain.AuxFieldViewDO;
import org.openelis.domain.Constants;
import org.openelis.exception.ParseException;
import org.openelis.manager.AuxFieldGroupManager;
import org.openelis.manager.AuxFieldManager;
import org.openelis.modules.auxiliary.client.AuxiliaryService;
import org.openelis.modules.sample1.client.ResultCell;
import org.openelis.modules.sample1.client.ResultCell.Value;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.resources.UIResources;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.ModalWindow;
import org.openelis.ui.widget.TextBox;
import org.openelis.ui.widget.table.Row;
import org.openelis.ui.widget.table.Table;
import org.openelis.ui.widget.table.event.BeforeCellEditedEvent;
import org.openelis.ui.widget.table.event.BeforeCellEditedHandler;
import org.openelis.ui.widget.table.event.CellEditedEvent;
import org.openelis.ui.widget.table.event.CellEditedHandler;
import org.openelis.utilcommon.ResultFormatter;
import org.openelis.utilcommon.ResultFormatter.FormattedValue;
import org.openelis.utilcommon.ResultHelper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.VisibleEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

public abstract class AuxDataTabUI extends Screen {

    @UiTemplate("AuxDataTab.ui.xml")
    interface AuxDataTabUIBinder extends UiBinder<Widget, AuxDataTabUI> {
    };

    private static AuxDataTabUIBinder                   uiBinder = GWT.create(AuxDataTabUIBinder.class);

    @UiField
    protected Table                                     table;

    @UiField
    protected TextBox<String>                           auxMethod, auxUnits, auxDesc;

    @UiField
    protected Button                                    addAuxButton, removeAuxButton;

    @UiField
    protected Dropdown<Integer>                         analyte;

    protected Screen                                    parentScreen;
    
    protected AuxDataTabUI                              screen;
    
    protected AuxGroupLookupUI                          auxGroupLookup;

    protected HashMap<String, ArrayList<Item<Integer>>> dictionaryModel;

    protected boolean                                   canEdit, isVisible, redraw;

    public AuxDataTabUI(Screen parentScreen, EventBus bus) {
        this.parentScreen = parentScreen;
        setEventBus(bus);
        initWidget(uiBinder.createAndBindUi(this));
        initialize();
    }

    private void initialize() {
        ArrayList<Item<Integer>> model;
        Item<Integer> row;

        screen = this;
        
        addScreenHandler(table, "table", new ScreenHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent event) {
                table.setModel(getTableModel());
            }

            public void onStateChange(StateChangeEvent event) {
                table.setEnabled(isState(QUERY, DISPLAY) || (canEdit && isState(ADD, UPDATE)));
            }
        });

        table.addSelectionHandler(new SelectionHandler<Integer>() {
            public void onSelection(SelectionEvent<Integer> event) {
                Row row;

                if (canEdit && isState(ADD, UPDATE)) {
                    removeAuxButton.setEnabled(true);
                    row = table.getRowAt(event.getSelectedItem());
                    setAuxFieldWidgets((AuxDataViewDO)row.getData());
                } else {
                    removeAuxButton.setEnabled(false);
                    setAuxFieldWidgets(null);
                }
            };
        });

        table.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                int r, c;
                Row row;
                AuxFieldViewDO af;
                AuxDataViewDO data;

                r = event.getRow();
                c = event.getCol();

                if (canEdit && isState(ADD, UPDATE)) {
                    if (c == 1) {
                        event.cancel();
                    } else if (c == 2) {
                        data = table.getRowAt(r).getData();
                        try {
                            setDictionaryModel(c, data.getGroupId(), data.getAuxFieldId());
                        } catch (Exception e) {
                            Window.alert(e.getMessage());
                            logger.log(Level.SEVERE, e.getMessage(), e);
                            event.cancel();
                        }
                    }
                } else if (isState(QUERY)) {
                    if (c == 0) {
                        event.cancel();
                    } else if (c == 2) {
                        /*
                         * The AuxFieldViewDO set as the data of the selected
                         * item in the dropdown for analytes, is used to find
                         * the aux group and aux field ids to get the correct
                         * formatter. We can't rely on table.getValueAt() for
                         * this purpose, because it returns only the key of the
                         * selected item and not its data.
                         */
                        row = analyte.getSelectedItem();
                        if (row != null) {
                            af = row.getData();
                            try {
                                setDictionaryModel(c, af.getAuxFieldGroupId(), af.getId());
                            } catch (Exception e) {
                                Window.alert(e.getMessage());
                                logger.log(Level.SEVERE, e.getMessage(), e);
                                event.cancel();
                            }
                        } else {
                            parentScreen.getWindow()
                                        .setError(Messages.get().aux_selectAnalyteBeforeValue());
                            event.cancel();
                        }
                    }
                } else {
                    event.cancel();
                }
            }
        });

        table.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                Object val;
                Value value;
                AuxFieldGroupManager agm;
                AuxDataViewDO data;
                ResultFormatter rf;

                if (isState(QUERY))
                    return;

                r = event.getRow();
                c = event.getCol();
                val = table.getValueAt(r, c);
                data = get(r);

                switch (c) {
                    case 0:
                        data.setIsReportable((String)val);
                        break;
                    case 2:
                        value = (Value)val;
                        table.clearExceptions(r, c);
                        if ( !DataBaseUtil.isEmpty(value.getDisplay())) {
                            /*
                             * validate the value entered by the user
                             */
                            try {
                                agm = getAuxFieldGroupManager(data.getGroupId());
                                rf = agm.getFormatter();
                                ResultHelper.formatValue(data, value.getDisplay(), rf);
                            } catch (ParseException e) {
                                /*
                                 * the value is not valid
                                 */
                                table.addException(r, c, e);
                                data.setValue(value.getDisplay());
                                data.setTypeId(null);
                                return;
                            } catch (Exception e) {
                                Window.alert(e.getMessage());
                                logger.log(Level.SEVERE, e.getMessage(), e);
                                return;
                            }
                        } else {
                            data.setValue(null);
                            data.setTypeId(null);
                        }

                        /*
                         * Set the formatted and validated value as the
                         * displayed text, but only if the type is not
                         * dictionary, because the text for a valid dictionary
                         * value is already being displayed.
                         */
                        if ( !Constants.dictionary().AUX_DICTIONARY.equals(data.getTypeId()))
                            value.setDisplay(data.getValue());

                        table.setValueAt(r, c, value);

                        break;
                }
            }
        });

        addScreenHandler(removeAuxButton, "removeAuxButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                removeAuxButton.setEnabled(false);
            }
        });

        addScreenHandler(addAuxButton, "addAuxButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                addAuxButton.setEnabled(canEdit && isState(ADD, UPDATE));
            }
        });

        addScreenHandler(auxMethod, "auxMethod", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                setAuxMethod(null);
            }

            public void onStateChange(StateChangeEvent event) {
                auxMethod.setEnabled(false);
            }
        });

        addScreenHandler(auxUnits, "auxUnits", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                setAuxUnits(null);
            }

            public void onStateChange(StateChangeEvent event) {
                auxUnits.setEnabled(false);
            }
        });

        addScreenHandler(auxDesc, "auxDesc", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                setAuxDescription(null);
            }

            public void onStateChange(StateChangeEvent event) {
                auxDesc.setEnabled(false);
            }
        });

        addVisibleHandler(new VisibleEvent.Handler() {
            public void onVisibleOrInvisible(VisibleEvent event) {
                isVisible = event.isVisible();
                displayAuxData();
            }
        });

        bus.addHandler(AddAuxGroupEvent.getType(), new AddAuxGroupEvent.Handler() {
            @Override
            public void onAddAuxGroup(AddAuxGroupEvent event) {
                if (screen != event.getSource()) {
                    redraw = true;
                    displayAuxData();
                }
            }
        });
        
        bus.addHandler(RemoveAuxGroupEvent.getType(), new RemoveAuxGroupEvent.Handler() {
            @Override
            public void onRemoveAuxGroup(RemoveAuxGroupEvent event) {
                if (screen != event.getSource()) {
                    redraw = true;
                    displayAuxData();
                }
            }
        });

        /*
         * aux field analyte dropdown
         */
        model = new ArrayList<Item<Integer>>();
        try {
            for (AuxFieldViewDO a : AuxiliaryService.get().fetchAll()) {
                row = new Item<Integer>(2);
                row.setKey(a.getId());
                row.setCell(0, a.getAnalyteName());
                row.setCell(1, a.getAuxFieldGroupName());
                row.setData(a);
                model.add(row);
            }
            analyte.setModel(model);
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
            parentScreen.getWindow().close();
        }

        /*
         * handlers for the events fired by the screen containing this tab
         */
        bus.addHandlerToSource(StateChangeEvent.getType(),
                               parentScreen,
                               new StateChangeEvent.Handler() {
                                   public void onStateChange(StateChangeEvent event) {
                                       canEdit = evaluateEdit();
                                       setState(event.getState());
                                   }
                               });

        bus.addHandlerToSource(DataChangeEvent.getType(),
                               parentScreen,
                               new DataChangeEvent.Handler() {
                                   public void onDataChange(DataChangeEvent event) {
                                       int count1, count2;
                                       AuxDataViewDO aux1, aux2;
                                       
                                       count1 = table.getRowCount();
                                       count2 = count();

                                       /*
                                        * find out if there's any difference between the aux data being
                                        * displayed and the aux data to be displayed
                                        */
                                       if (count1 == count2) {
                                           for (int i = 0; i < count1; i++ ) {
                                               aux1 = table.getRowAt(i).getData();
                                               aux2 = get(i);

                                               if (DataBaseUtil.isDifferent(aux1.getTypeId(), aux2.getTypeId()) ||
                                                   DataBaseUtil.isDifferent(aux1.getValue(), aux2.getValue()) ||
                                                   DataBaseUtil.isDifferent(aux1.getAnalyteId(), aux2.getAnalyteId()) ||
                                                   DataBaseUtil.isDifferent(aux1.getGroupId(), aux2.getGroupId())) {
                                                   redraw = true;
                                                   break;
                                               }
                                           }
                                       } else {
                                           redraw = true;
                                       }
                                       
                                       displayAuxData();
                                   }
                               });
    }

    public void setState(State state) {
        this.state = state;
        bus.fireEventFromSource(new StateChangeEvent(state), this);
    }

    /**
     * overridden to determine whether the data in the manager allows the
     * widgets on the tab to be edited
     */
    public abstract boolean evaluateEdit();

    /**
     * overridden to return the total number of aux data in the manager
     */
    public abstract int count();

    /**
     * overridden to return the aux data at the specified index in the manager
     */
    public abstract AuxDataViewDO get(int i);

    /**
     * overridden to return the key for aux field ID from the meta used by the
     * main screen
     */
    public abstract String getAuxFieldMetaKey();

    /**
     * overridden to return the key for aux field value from the meta used by
     * the main screen
     */
    public abstract String getValueMetaKey();

    public ArrayList<QueryData> getQueryFields() {
        String query;
        AuxFieldViewDO data;
        Item<Integer> row;
        QueryData field;
        ResultCell.Value value;
        ArrayList<QueryData> fields;

        table.finishEditing();
        /*
         * get the aux field selected in the dropdown
         */
        row = analyte.getSelectedItem();
        value = table.getValueAt(0, 2);
        fields = new ArrayList<QueryData>();

        if (value.getDictId() != null)
            query = value.getDictId();
        else
            query = value.getDisplay();

        if (row != null && !DataBaseUtil.isEmpty(query)) {
            fields = new ArrayList<QueryData>();
            data = row.getData();

            field = new QueryData();
            field.setKey(getAuxFieldMetaKey());
            field.setType(QueryData.Type.INTEGER);
            field.setQuery(String.valueOf(data.getId()));
            fields.add(field);

            /*
             * aux data value
             */
            field = new QueryData();
            field.setKey(getValueMetaKey());
            field.setType(QueryData.Type.STRING);
            field.setQuery(query);
            fields.add(field);
        }

        return fields;
    }

    @UiHandler("addAuxButton")
    protected void addAux(ClickEvent event) {
        ModalWindow modal;

        if (auxGroupLookup == null) {
            auxGroupLookup = new AuxGroupLookupUI() {
                @Override
                public void ok() {
                    ArrayList<Integer> ids;
                    
                    ids = auxGroupLookup.getGroupIds();
                    if (ids != null && ids.size() > 0)
                        screen.getEventBus().fireEventFromSource(new AddAuxGroupEvent(ids), this);
                }

                @Override
                public void cancel() {
                    // ignore
                }
            };
        }

        modal = new ModalWindow();
        modal.setSize("500px", "325px");
        modal.setName(Messages.get().auxGroupSelection());
        modal.setCSS(UIResources.INSTANCE.popupWindow());
        modal.setContent(auxGroupLookup);

        auxGroupLookup.setWindow(modal);
        auxGroupLookup.setData();
    }

    @UiHandler("removeAuxButton")
    protected void removeAux(ClickEvent event) {
        int r;
        AuxDataViewDO data;
        ArrayList<Integer> ids;

        r = table.getSelectedRow();
        if (r == -1)
            return;
        if (Window.confirm(Messages.get().removeAuxMessage())) {
            data = (AuxDataViewDO)table.getRowAt(r).getData();
            ids = new ArrayList<Integer>(1);
            ids.add(data.getGroupId());
            bus.fireEventFromSource(new RemoveAuxGroupEvent(ids), this);
        }
        removeAuxButton.setEnabled(false);
    }

    private void setAuxMethod(String method) {
        auxMethod.setValue(method);
    }

    private void setAuxDescription(String desc) {
        auxDesc.setValue(desc);
    }

    private void setAuxUnits(String units) {
        auxUnits.setValue(units);
    }

    private void displayAuxData() {
        if ( !isVisible)
            return;

        /*
         * Reset the table's view, so that if its model is changed, it shows its
         * headers and columns correctly. Otherwise, problems like widths of the
         * columns not being correct or the headers not showing may happen.
         */
        table.onResize();

        if (redraw) {
            /*
             * don't redraw unless the data has changed
             */
            redraw = false;
            canEdit = evaluateEdit();
            setState(state);
            fireDataChange();
        }
    }

    private ArrayList<Row> getTableModel() {
        boolean validateResults;
        Integer groupId, dictId;
        Row row;
        AuxDataViewDO data;
        AuxFieldGroupManager afgm;
        ResultFormatter rf;
        ResultCell.Value value;
        ArrayList<Row> model;
        ArrayList<Integer> dictIds;

        model = new ArrayList<Row>();
        table.clearExceptions();

        if (isState(QUERY)) {
            row = new Row(3);
            row.setCell(2, new ResultCell.Value(null, null));
            model.add(row);
            return model;
        }

        groupId = null;
        rf = null;
        validateResults = canEdit && isState(ADD, UPDATE);
        dictIds = new ArrayList<Integer>();

        try {
            for (int i = 0; i < count(); i++ ) {
                row = new Row(3);
                data = get(i);
                row.setCell(0, data.getIsReportable());
                row.setCell(1, data.getAuxFieldId());

                if (validateResults && data.getValue() != null && data.getTypeId() == null) {
                    if ( !data.getGroupId().equals(groupId)) {
                        afgm = getAuxFieldGroupManager(data.getGroupId());
                        rf = afgm.getFormatter();
                        groupId = data.getGroupId();
                    }

                    /*
                     * Since the type is not set, the value was either not
                     * validated and formatted before or the validation didn't
                     * succeed. Thus to format the value and set the type or to
                     * show an error, validate it here.
                     */
                    try {
                        ResultHelper.formatValue(data, data.getValue(), rf);
                    } catch (Exception e) {
                        table.addException(row, 2, e);
                    }
                }

                /*
                 * create the value to be set in the cell for this aux data
                 */
                if (Constants.dictionary().AUX_DICTIONARY.equals(data.getTypeId())) {
                    if (data.getValue() != null) {
                        dictId = Integer.valueOf(data.getValue());
                        dictIds.add(dictId);
                    }
                    value = new ResultCell.Value(null, data.getValue());
                } else {
                    value = new ResultCell.Value(data.getValue(), null);
                }

                row.setCell(2, value);
                row.setData(data);
                model.add(row);
            }

            /*
             * For type dictionary, the displayed text is looked up from the
             * cache. The following is done to fetch and put the dictionary
             * records needed for the aux data, in the cache, all at once so
             * that they won't have to be fetched one at a time.
             */
            if (dictIds.size() > 0)
                DictionaryCache.getByIds(dictIds);
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
        }

        return model;
    }

    private AuxFieldGroupManager getAuxFieldGroupManager(Integer groupId) throws Exception {
        if ( ! (parentScreen instanceof CacheProvider))
            throw new Exception("Parent screen must implement " + CacheProvider.class.toString());

        return ((CacheProvider)parentScreen).get(groupId, AuxFieldGroupManager.class);
    }

    private void setDictionaryModel(int col, Integer groupId, Integer fieldId) throws Exception {
        String key;
        ResultCell rc;
        ResultFormatter rf;
        ArrayList<Item<Integer>> model;
        ArrayList<FormattedValue> values;
        AuxFieldGroupManager agm;

        if (dictionaryModel == null)
            dictionaryModel = new HashMap<String, ArrayList<Item<Integer>>>();

        key = groupId + ":" + fieldId;
        model = dictionaryModel.get(key);
        if (model == null) {
            agm = getAuxFieldGroupManager(groupId);
            rf = agm.getFormatter();
            /*
             * if all the values for this aux field are dictionary values, then
             * create a dropdown model from them
             */
            if (rf.hasAllDictionary(fieldId, null)) {
                values = rf.getDictionaryValues(fieldId, null);
                if (values != null) {
                    model = new ArrayList<Item<Integer>>();
                    for (FormattedValue v : values)
                        model.add(new Item<Integer>(v.getId(), v.getDisplay()));
                }
            }
        }
        /*
         * this ensures that even if a model was not found above, it's not
         * looked up again
         */
        dictionaryModel.put(key, model);
        rc = (ResultCell)table.getColumnAt(col).getCellEditor();
        rc.setModel(model);
    }

    /**
     * sets values in the widgets, like description, that show the data from the
     * aux field corresponding to this aux data
     */
    private void setAuxFieldWidgets(AuxDataViewDO data) {
        String desc, method, unit;
        AuxFieldViewDO af;
        AuxFieldGroupManager afgm;
        AuxFieldManager afm;

        desc = null;
        method = null;
        unit = null;
        if (data != null) {
            /*
             * find the aux group and aux field corresponding to this aux data
             * and set the values in the non-editable widgets
             */
            try {
                afgm = getAuxFieldGroupManager(data.getGroupId());
                afm = afgm.getFields();
                for (int i = 0; i < afm.count(); i++ ) {
                    af = afm.getAuxFieldAt(i);
                    if (af.getId().equals(data.getAuxFieldId())) {
                        desc = af.getDescription();
                        method = af.getMethodName();
                        unit = af.getUnitOfMeasureName();
                        break;
                    }
                }
            } catch (Exception e) {
                Window.alert(e.getMessage());
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        }

        setAuxDescription(desc);
        setAuxMethod(method);
        setAuxUnits(unit);
    }
}