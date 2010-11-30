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
package org.openelis.modules.sample.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.AuxDataViewDO;
import org.openelis.domain.AuxFieldValueViewDO;
import org.openelis.domain.AuxFieldViewDO;
import org.openelis.exception.ParseException;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.LocalizedException;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.GetMatchesEvent;
import org.openelis.gwt.event.GetMatchesHandler;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.AutoCompleteValue;
import org.openelis.gwt.widget.Button;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.DateHelper;
import org.openelis.gwt.widget.Item;
import org.openelis.gwt.widget.ModalWindow;
import org.openelis.gwt.widget.Window;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.table.Row;
import org.openelis.gwt.widget.table.Table;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.gwt.widget.table.event.CellEditedEvent;
import org.openelis.gwt.widget.table.event.CellEditedHandler;
import org.openelis.gwt.widget.table.event.UnselectionEvent;
import org.openelis.gwt.widget.table.event.UnselectionHandler;
import org.openelis.manager.AuxDataManager;
import org.openelis.manager.AuxFieldManager;
import org.openelis.manager.AuxFieldValueManager;
import org.openelis.manager.HasAuxDataInt;
import org.openelis.meta.SampleMeta;
import org.openelis.utilcommon.ResultValidator;
import org.openelis.utilcommon.ResultValidator.Type;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;

public class AuxDataTab extends Screen implements GetMatchesHandler {
    private boolean                 loaded;

    protected AuxGroupLookupScreen  auxGroupScreen;
    protected Table                 auxValsTable;
    protected Button                addAuxButton, removeAuxButton;
    protected TextBox               auxMethod, auxUnits, auxDesc;
    protected AutoComplete          ac, auxField;
    protected Integer               alphaLowerId, alphaUpperId, alphaMixedId, timeId, numericId,
                                    dateId, dateTimeId, dictionaryId;
    protected boolean               queryFieldEntered;
    protected HasAuxDataInt         parentMan;
    protected AuxDataManager        manager;

    public AuxDataTab(ScreenDefInt def, Window window) {
        service = new ScreenService("OpenELISServlet?service=org.openelis.modules.auxiliary.server.AuxiliaryService");
        setDefinition(def);
        setWindow(window);

        initialize();
        initializeDropdowns();
    }

    private void initialize() {
        final AuxDataTab tab = this;
        AuxTableColumn auxCol;

        auxValsTable = (Table)def.getWidget("auxValsTable");
        
        auxCol = new AuxTableColumn();
        auxCol.setScreen(this);
        auxValsTable.setColumnAt(2, auxCol);
        auxCol.setWidth(303);
        
        addScreenHandler(auxValsTable, new ScreenEventHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent event) {
                auxValsTable.setModel(getTableModel());
                queryFieldEntered = false;
            }

            public void onStateChange(StateChangeEvent<State> event) {
                auxValsTable.setEnabled(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                           .contains(event.getState()));
                // auxValsTable.setQueryMode(event.getState() == State.QUERY);
                // ((HasField)auxValsTable.getColumns().get(1).colWidget).setQueryMode(false);
                // auxValsTable.fireEvents(true);
            }
        });

        auxValsTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                int r, c;
                Object val;
                c = event.getCol();
                r = event.getRow();

                window.clearStatus();
                
                if (state == State.QUERY){
                    val = auxValsTable.getValueAt(r, c);
                    
                    if(c == 0)
                        event.cancel();
                    else if(c == 2 && queryFieldEntered && (val == null || "".equals(val))){
                        event.cancel();
                        window.setError(consts.get("auxDataOneQueryException"));
                    }
                }
            }
        });
        auxValsTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                Row row;
                Object val;
                AuxDataViewDO data;
                AuxFieldViewDO fieldDO;
                AuxDataBundle adb;
                ResultValidator rv;

                r = event.getRow();
                c = event.getCol();
                val = auxValsTable.getValueAt(r, c);
                data = null;

                if (state == State.QUERY){
                    if(val == null || "".equals(val))
                        queryFieldEntered = false;
                    else
                        queryFieldEntered = true;
                    return;
                }
                
                try {
                    data = manager.getAuxDataAt(r);
                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                    return;
                }

                switch (c) {
                    case 0:
                        data.setIsReportable((String)val);
                        break;
                    case 2:
                        row = auxValsTable.getRowAt(r);
                        adb = (AuxDataBundle)row.getData();
                        rv = adb.validator;
                        fieldDO = (AuxFieldViewDO)adb.fieldDO;
                        data.setValue(getCorrectManValueByType(val, fieldDO.getTypeId()));

                        if (rv != null && getCorrectManValueByType(val, fieldDO.getTypeId()) != null) {
                            try {
                                auxValsTable.clearExceptions(r, c);
                                
                                if(!DataBaseUtil.isEmpty(val)) 
                                    rv.validate(null, getCorrectManValueByType(val, fieldDO.getTypeId()));                                                                                                   
                            } catch (ParseException e) {
                                auxValsTable.addException(r, c, e);
                            } catch (Exception e) {
                                com.google.gwt.user.client.Window.alert(e.getMessage());
                            }
                        }

                        break;
                }
            }
        });

        auxValsTable.addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {
            public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {
                // always allow selection
            }
        });

        auxValsTable.addSelectionHandler(new SelectionHandler<Integer>() {
            public void onSelection(SelectionEvent<Integer> event) {
                Row row;
                AuxDataBundle bundle;

                row = auxValsTable.getRowAt(event.getSelectedItem());
                bundle = (AuxDataBundle)row.getData();
                if (bundle != null) {
                    AuxFieldViewDO fieldDO = bundle.fieldDO;

                    auxMethod.setValue(fieldDO.getMethodName());
                    auxDesc.setValue(fieldDO.getDescription());
                    auxUnits.setValue(fieldDO.getUnitOfMeasureName());
                }

                if (EnumSet.of(State.ADD, State.UPDATE, State.QUERY).contains(state))
                    removeAuxButton.setEnabled(true);
            };
        });

        auxValsTable.addUnselectionHandler(new UnselectionHandler<Integer>() {
            public void onUnselection(UnselectionEvent<Integer> event) {
                auxMethod.setValue(null);
                auxDesc.setValue(null);
                auxUnits.setValue(null);
            }
        });

        addAuxButton = (Button)def.getWidget("addAuxButton");
        addScreenHandler(addAuxButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                if (auxGroupScreen == null) {
                    try {
                        auxGroupScreen = new AuxGroupLookupScreen();
                    } catch (Exception e) {
                        com.google.gwt.user.client.Window.alert(e.getMessage());
                    }
                    auxGroupScreen.addActionHandler(new ActionHandler<AuxGroupLookupScreen.Action>() {
                        public void onAction(ActionEvent<AuxGroupLookupScreen.Action> event) {
                            groupsSelectedFromLookup((ArrayList<AuxFieldManager>)event.getData());
                        }
                    });
                }

                ModalWindow modal = new ModalWindow();
                modal.setName(consts.get("auxGroupSelection"));
                modal.setContent(auxGroupScreen);
                auxGroupScreen.draw();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addAuxButton.setEnabled(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                           .contains(event.getState()));
            }
        });

        removeAuxButton = (Button)def.getWidget("removeAuxButton");
        addScreenHandler(removeAuxButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                if (auxValsTable.getSelectedRow() == -1)
                    return;

                if (com.google.gwt.user.client.Window.confirm(consts.get("removeAuxMessage")))
                    manager.removeAuxDataGroupAt(auxValsTable.getSelectedRow());

                removeAuxButton.setEnabled(false);

                // reload the tab
                DataChangeEvent.fire(tab);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeAuxButton.setEnabled(false);
            }
        });

        auxMethod = (TextBox)def.getWidget("auxMethod");
        addScreenHandler(auxMethod, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                auxMethod.setValue(null);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                auxMethod.setEnabled(false);
            }
        });

        auxUnits = (TextBox)def.getWidget("auxUnits");
        addScreenHandler(auxUnits, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                auxUnits.setValue(null);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                auxUnits.setEnabled(false);
            }
        });

        auxDesc = (TextBox)def.getWidget("auxDesc");
        addScreenHandler(auxDesc, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                auxDesc.setValue(null);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                auxDesc.setEnabled(false);
            }
        });
    }

    private ArrayList<Row> getTableModel() {
        int i;
        Row row;
        AuxDataViewDO data;
        AuxFieldViewDO field;
        ArrayList<AuxFieldValueViewDO> values;
        AuxFieldValueViewDO val;
        ArrayList<Row> model;
        AuxDataBundle adb;
        ResultValidator validatorItem;

        model = new ArrayList<Row>();
        if (manager == null)
            return model;

        try {
            for (i = 0; i < manager.count(); i++ ) {
                data = manager.getAuxDataAt(i);
                field = manager.getFieldsAt(i).getAuxFieldAt(0);
                values = manager.getFieldsAt(i).getValuesAt(0).getValues();
                val = values.get(0);

                row = new Row(3);
                row.setCell(0,data.getIsReportable());
                row.setCell(1,field.getAnalyteName());
                row.setCell(2,getCorrectColValueByType(data.getValue(),
                                                       data.getDictionary(),
                                                       val.getTypeId()));

                field.setTypeId(val.getTypeId());

                validatorItem = getDataItemForRow(field.getTypeId(), values);
                adb = new AuxDataBundle(validatorItem, field);
                row.setData(adb);

                model.add(row);
            }
        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert(e.getMessage());
            e.printStackTrace();
        }
        return model;
    }

    private void groupsSelectedFromLookup(ArrayList<AuxFieldManager> fields) {
        AuxFieldManager man;
        AuxFieldViewDO fieldDO;
        ArrayList<AuxFieldValueViewDO> values;
        AuxFieldValueViewDO valueDO, defaultValue;
        AuxDataViewDO dataDO;
        Row row;
        AuxDataBundle adb;
        ResultValidator validatorItem;

        try {
            //auxValsTable.fireEvents(false);
            for (int i = 0; i < fields.size(); i++ ) {
                man = fields.get(i);

                for (int j = 0; j < man.count(); j++ ) {
                    fieldDO = man.getAuxFieldAt(j);

                    if ("Y".equals(fieldDO.getIsActive())) {
                        values = man.getValuesAt(j).getValues();
                        defaultValue = man.getValuesAt(j).getDefaultValue();
                        valueDO = values.get(0);
                        dataDO = new AuxDataViewDO();

                        dataDO.setTypeId(valueDO.getTypeId());
                        dataDO.setAuxFieldId(fieldDO.getId());
                        dataDO.setIsReportable(fieldDO.getIsReportable());
                        // dataDO.setValue(valueDO.getValue());
                        dataDO.setDictionary(valueDO.getDictionary());
                        fieldDO.setTypeId(valueDO.getTypeId());

                        manager.addAuxDataFieldsAndValues(dataDO, fieldDO, values);

                        row = new Row(3);
                        row.setCell(0,fieldDO.getIsReportable());
                        row.setCell(1,fieldDO.getAnalyteName());

                        validatorItem = getDataItemForRow(valueDO.getTypeId(), values);
                        adb = new AuxDataBundle(validatorItem, fieldDO);
                        row.setData(adb);
                        
                        auxValsTable.addRow(row);

                        if (defaultValue != null && state != State.QUERY) {
                            try {
                                if (dictionaryId.equals(valueDO.getTypeId())) {
                                    String dictionary, value;
                                    AuxFieldValueViewDO a;

                                    dictionary = defaultValue.getValue();

                                    value = null;
                                    for (int k = 0; k < values.size(); k++ ) {
                                        a = values.get(k);
                                        if (a.getDictionary().equals(dictionary)) {
                                            value = a.getValue();
                                            break;
                                        }
                                    }

                                    if (value == null)
                                        throw new Exception();
                                    else
                                        row.setCell(2,getCorrectColValueByType(value,
                                                                               dictionary,
                                                                               valueDO.getTypeId()));

                                } else
                                    row.setCell(2,getCorrectColValueByType(defaultValue.getValue(),
                                                                           null,
                                                                           valueDO.getTypeId()));

                            } catch (Exception e) {
                                auxValsTable.addException(auxValsTable.getRowCount()-1, 2,new LocalizedException("illegalDefaultValueException"));
                            }
                        }
                        
                    }
                }
            }
            //auxValsTable.fireEvents(true);

        } catch (Exception e) {
        	e.printStackTrace();
            com.google.gwt.user.client.Window.alert(e.getMessage());
        }
    }

    private Object getCorrectColValueByType(String value, String dictionary, Integer typeId)
                                                                                            throws Exception {
        if (value == null)
            return null;

        if (alphaLowerId.equals(typeId) || alphaUpperId.equals(typeId) ||
            alphaMixedId.equals(typeId) || timeId.equals(typeId))
            return value;

        else if (numericId.equals(typeId))
            return new Double(value);

        else if (dateId.equals(typeId))
            return new Datetime(Datetime.YEAR, Datetime.DAY, new Date(value));

        else if (dateTimeId.equals(typeId))
            return new Datetime(Datetime.YEAR, Datetime.MINUTE, new Date(value));

        else if (dictionaryId.equals(typeId)) {
            if (dictionary == null)
                throw new Exception();

            return new AutoCompleteValue(new Integer(value), dictionary);
        }

        return null;
    }

    private ResultValidator getDataItemForRow(Integer typeId, ArrayList<AuxFieldValueViewDO> values) {
        ResultValidator rv;
        AuxFieldValueViewDO af;

        rv = new ResultValidator();
        // we only need to validate numerics and times because the widget will
        // validate everything else
        if (numericId.equals(typeId) || timeId.equals(typeId)) {
            try {
                for (int i = 0; i < values.size(); i++ ) {
                    af = values.get(i);
                    if (numericId.equals(typeId))
                        rv.addResult(af.getId(), null, Type.NUMERIC, null, null, af.getValue());
                    else
                        rv.addResult(af.getId(), null, Type.TIME, null, null, af.getValue());
                }
            } catch (Exception e) {
                com.google.gwt.user.client.Window.alert(e.getMessage());
                return null;
            }
        } else
            rv = null;

        return rv;
    }

    private String getCorrectManValueByType(Object value, Integer typeId) {
        DateHelper df;
        if (alphaLowerId.equals(typeId) || alphaUpperId.equals(typeId) ||
            alphaMixedId.equals(typeId) || timeId.equals(typeId))
            return (String)value;
        
        else if(numericId.equals(typeId))
            return ((Integer)value).toString();

        else if (dateId.equals(typeId)) {
            df = new DateHelper();
            df.setBegin(Datetime.YEAR);
            df.setEnd(Datetime.DAY);
            df.setPattern(consts.get("datePattern"));
            return df.format((Datetime)value).replaceAll("-", "/");
        } else if (dateTimeId.equals(typeId)) {
            df = new DateHelper();
            df.setBegin(Datetime.YEAR);
            df.setEnd(Datetime.MINUTE);
            df.setPattern(consts.get("dateTimePattern"));
            return  df.format((Datetime)value).replaceAll("-", "/");
        } else if (dictionaryId.equals(typeId))
            return ((Item<Integer>)value).getKey().toString();

        return null;
    }

    public void onGetMatches(GetMatchesEvent event) {
        ArrayList<Item<Integer>> model;
        int index;
        AuxFieldValueViewDO valDO;
        String match;
        boolean showWholeList;

        index = auxValsTable.getSelectedRow();
        model = new ArrayList<Item<Integer>>();
        match = event.getMatch();

        showWholeList = "".equals(match.trim());

        // give them the dictionary entries
        try {
            AuxFieldValueManager valMan = manager.getFieldsAt(index).getValuesAt(0);

            for (int i = 0; i < valMan.count(); i++ ) {
                valDO = valMan.getAuxFieldValueAt(i);

                if (valDO.getDictionary() != null &&
                    (showWholeList || valDO.getDictionary().startsWith(match)))
                    model.add(new Item<Integer>(new Integer(valDO.getValue()), valDO.getDictionary()));
            }
        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert(e.getMessage());
            return;
        }

        ((AutoComplete)event.getSource()).showAutoMatches(model);
    }

    public ArrayList<QueryData> getQueryFields() {
        ArrayList<QueryData> fieldList;
        Row row;
        AuxDataBundle adb;
        QueryData field;
        
        fieldList = new ArrayList<QueryData>();

        for (int i = 0; i < auxValsTable.getRowCount(); i++ ) {
            row = auxValsTable.getRowAt(i);
            adb = (AuxDataBundle)row.getData();

            if (row.getCell(2) != null) {                                
                
                field = new QueryData();
                field.key = SampleMeta.getAuxDataAuxFieldId();
                field.type = QueryData.Type.INTEGER;
                field.query = String.valueOf(adb.fieldDO.getId());
                fieldList.add(field);

                // aux data value
                field = new QueryData();
                field.key = SampleMeta.getAuxDataValue();
                field.type = QueryData.Type.STRING;
                field.query = getCorrectManValueByType(row.getCell(2),
                                                       adb.fieldDO.getTypeId());
                fieldList.add(field);
            }
        }

        return fieldList;
    }

    private void initializeDropdowns() {
        try {
            alphaLowerId = DictionaryCache.getIdFromSystemName("aux_alpha_lower");
            alphaUpperId = DictionaryCache.getIdFromSystemName("aux_alpha_upper");
            alphaMixedId = DictionaryCache.getIdFromSystemName("aux_alpha_mixed");
            timeId = DictionaryCache.getIdFromSystemName("aux_time");
            numericId = DictionaryCache.getIdFromSystemName("aux_numeric");
            dateId = DictionaryCache.getIdFromSystemName("aux_date");
            dateTimeId = DictionaryCache.getIdFromSystemName("aux_date_time");
            dictionaryId = DictionaryCache.getIdFromSystemName("aux_dictionary");
        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert(e.getMessage());
            window.close();
        }
    }

    public void setManager(HasAuxDataInt parentMan) {
        this.parentMan = parentMan;
        loaded = false;
    }

    public void draw() {
        if ( !loaded) {
            try {
                manager = parentMan.getAuxData();
                loaded = true;

                if (state != State.QUERY)
                    StateChangeEvent.fire(this, state);

                DataChangeEvent.fire(this);

            } catch (Exception e) {
                com.google.gwt.user.client.Window.alert(e.getMessage());
            }
        }
    }
}
