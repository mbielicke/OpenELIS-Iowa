/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.modules.test.client;

import java.util.List;

import org.openelis.gwt.common.FormRPC;
import org.openelis.gwt.common.data.CollectionField;
import org.openelis.gwt.common.data.Data;
import org.openelis.gwt.common.data.DataMap;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.KeyListManager;
import org.openelis.gwt.common.data.NumberObject;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TreeDataItem;
import org.openelis.gwt.screen.CommandChain;
import org.openelis.gwt.screen.ScreenTableWidget;
import org.openelis.gwt.screen.ScreenTextBox;
import org.openelis.gwt.screen.ScreenTreeWidget;
import org.openelis.gwt.screen.ScreenVertical;
import org.openelis.gwt.screen.ScreenWindow;
import org.openelis.gwt.widget.AToZTable;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.CollapsePanel;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.FormInt;
import org.openelis.gwt.widget.AppButton.ButtonState;
import org.openelis.gwt.widget.table.QueryTable;
import org.openelis.gwt.widget.table.TableDropdown;
import org.openelis.gwt.widget.table.TableManager;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.SourcesTableWidgetEvents;
import org.openelis.gwt.widget.table.event.TableWidgetListener;
import org.openelis.gwt.widget.tree.TreeManager;
import org.openelis.gwt.widget.tree.TreeRow;
import org.openelis.gwt.widget.tree.TreeWidget;
import org.openelis.metamap.TestMetaMap;
import org.openelis.modules.dictionaryentrypicker.client.DictionaryEntryPickerScreen;
import org.openelis.modules.main.client.OpenELISScreenForm;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SourcesTabEvents;
import com.google.gwt.user.client.ui.TabListener;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.XMLParser;

public class TestScreen extends OpenELISScreenForm implements
                                                  ClickListener,
                                                  TabListener,
                                                  ChangeListener,
                                                  TableManager,
                                                  TableWidgetListener,
                                                  TreeManager {
    private static boolean loaded = false;

    private DataModel methodDropdown, labelDropdown,
                    testTrailerDropdown, scriptletDropDown, sampleTypeDropDown,
                    measureUnitDropDown, prepTestDropDown,
                    reflexTestFlagsDropDown, revisionMethodDropDown,
                    testFormatDropDown, testWSItemTypeDropDown,
                    testWSNumFormatDropDown, reportingMethodDropDown,
                    sortingMethodDropDown, testSectionFlagDropDown,
                    testAnalyteTypeDropDown, sectionDropDown,
                    testResultFlagDropDown, testResultTypeDropDown,
                    roundingMethodDropDown;

    private DataModel resultGroupDropDown;

    private AppButton removeSampleTypeButton, removePrepTestButton,
                    addAnalyteButton,
                    addGroupButton,
                    // addAnalyteBeforeButton,addGroupBeforeButton,
                    removeReflexTestButton, removeWSItemButton, deleteButton,
                    removeTestSectionButton, dictionaryLookUpButton,
                    removeTestResultButton, addResultTabButton;

    private TreeWidget analyteTreeController = null;

    private int gid = 0;

    private int group = 0;

    private TableWidget reflexTestWidget, prepTestWidget, wsItemWidget,
                    sampleTypeWidget, sectionWidget, resultWidget;
    
    private DataMap resultDropDownModelMap;

    private TestMetaMap TestMeta = new TestMetaMap();
    private KeyListManager keyList = new KeyListManager();

    private ScreenTextBox testId;
    private TextBox testName;

    private TabPanel resultPanel;

    private CollectionField resultModelCollection;
    
    private DataModel defaultModel;

    private static String panelString = "<VerticalPanel/>";

    public TestScreen() {
        super("org.openelis.modules.test.server.TestService", !loaded);

    }
    
    public void afterDraw(boolean success) {

        ButtonPanel bpanel, atozButtons;                     
        CommandChain chain;                
        FormRPC analyteRPC;
        TabPanel testTabPanel;
        AToZTable atozTable;
        QueryTable q;
        ScreenTableWidget s;
        ScreenTreeWidget analyteTree;
        
        atozTable = (AToZTable)getWidget("azTable");
        atozButtons = (ButtonPanel)getWidget("atozButtons");
        ((CollapsePanel)getWidget("collapsePanel")).addChangeListener(atozTable);
        
        bpanel = (ButtonPanel)getWidget("buttons");       
        bpanel.enableButton("delete", false);

        testTabPanel = (TabPanel)getWidget("testTabPanel");
        //
        // this is done to remove an unwanted tab that gets added to 
        // testTabPanel for some reason, when you put a tab panel inside one
        // of its tabs  
        //
        testTabPanel.remove(2);
        
        //
        // we are interested in getting button actions in two places,
        // modelwidget and this screen.
        //
        chain = new CommandChain();
        chain.addCommand(this);
        chain.addCommand(atozTable);
        chain.addCommand(atozButtons);
        chain.addCommand(keyList);
        chain.addCommand(bpanel);
        
        
        //
        // initializing the buttons that are not part of any button panel 
        //
        removeSampleTypeButton = (AppButton)getWidget("removeSampleTypeButton");
        removePrepTestButton = (AppButton)getWidget("removePrepTestButton");
        removeReflexTestButton = (AppButton)getWidget("removeReflexTestButton");
        removeWSItemButton = (AppButton)getWidget("removeWSItemButton");
        deleteButton = (AppButton)getWidget("deleteButton");
        addAnalyteButton = (AppButton)getWidget("addAnalyteButton");
        addGroupButton = (AppButton)getWidget("addGroupButton");        
        removeTestSectionButton = (AppButton)getWidget("removeTestSectionButton");
        dictionaryLookUpButton = (AppButton)getWidget("dictionaryLookUpButton");
        removeTestResultButton = (AppButton)getWidget("removeTestResultButton");
        addResultTabButton = (AppButton)getWidget("addResultTabButton");
        
        testId = (ScreenTextBox)widgets.get(TestMeta.getId());
        testName = (TextBox)getWidget(TestMeta.getName());        
        
        //
        // initializing the tree-table widget
        //
        analyteTree = (ScreenTreeWidget)widgets.get("analyteTree");
        analyteTreeController = (TreeWidget)analyteTree.getWidget();

        //
        // we load all the dropdowns the first time so we don't have to incur
        // the cost
        // of re-load everytime from server -- see the constructor for loaded
        // flag.
        //
        if (!loaded) {
            methodDropdown = (DataModel)initData.get("methods");
            labelDropdown = (DataModel)initData.get("labels");
            testTrailerDropdown = (DataModel)initData.get("testTrailers");
            scriptletDropDown = (DataModel)initData.get("scriptlets");
            sampleTypeDropDown = (DataModel)initData.get("sampleTypes");
            measureUnitDropDown = (DataModel)initData.get("unitsOfMeasure");
            prepTestDropDown = (DataModel)initData.get("prepTests");
            reflexTestFlagsDropDown = (DataModel)initData.get("testReflexFlags");
            revisionMethodDropDown = (DataModel)initData.get("revisionMethods");
            testFormatDropDown = (DataModel)initData.get("testFormats");
            testWSItemTypeDropDown = (DataModel)initData.get("testWSItemTypes");
            testWSNumFormatDropDown = (DataModel)initData.get("testWSNumFormats");
            testAnalyteTypeDropDown = (DataModel)initData.get("testAnalyteTypes");
            reportingMethodDropDown = (DataModel)initData.get("reportingMethods");
            sortingMethodDropDown = (DataModel)initData.get("sortingMethods");
            testSectionFlagDropDown = (DataModel)initData.get("testSectionFlags");
            sectionDropDown = (DataModel)initData.get("sections");
            testResultFlagDropDown = (DataModel)initData.get("testResultFlags");
            testResultTypeDropDown = (DataModel)initData.get("testResultTypes");
            roundingMethodDropDown = (DataModel)initData.get("roundingMethods");
            
            loaded = true;
        }
        
        //
        // see setDropdownModel(String field, DataModel model)  
        //
        setDropdownModel(TestMeta.getMethodId(), methodDropdown);
        setDropdownModel(TestMeta.getLabelId(), labelDropdown);
        setDropdownModel(TestMeta.getTestTrailerId(), testTrailerDropdown);
        setDropdownModel(TestMeta.getScriptletId(), scriptletDropDown);
        setDropdownModel(TestMeta.getRevisionMethodId(), revisionMethodDropDown);
        setDropdownModel(TestMeta.getTestFormatId(), testFormatDropDown);
        setDropdownModel(TestMeta.getSortingMethodId(), sortingMethodDropDown);
        setDropdownModel(TestMeta.getReportingMethodId(),
                         reportingMethodDropDown);
        setDropdownModel(TestMeta.getTestWorksheet().getScriptletId(),
                         scriptletDropDown);
        setDropdownModel(TestMeta.getTestWorksheet().getNumberFormatId(),
                         testWSNumFormatDropDown);
        //
        // set the model for each column. Note that we have to do it twice:
        // once for the normal table and once for query table.
        // see setTableDropdownModel(TableWidget widget,int column,DataModel model);
        //
        s = (ScreenTableWidget)widgets.get("sampleTypeTable");
        sampleTypeWidget = (TableWidget)s.getWidget();
        q = (QueryTable)s.getQueryWidget().getWidget();
        setTableDropdownModel(sampleTypeWidget,0, sampleTypeDropDown);
        setTableDropdownModel(q,0, sampleTypeDropDown);        
        setTableDropdownModel(sampleTypeWidget,1, measureUnitDropDown);
        setTableDropdownModel(q,1, measureUnitDropDown);        

        s = (ScreenTableWidget)widgets.get("testPrepTable");
        prepTestWidget = (TableWidget)s.getWidget();
        q = (QueryTable)s.getQueryWidget().getWidget();
        setTableDropdownModel(prepTestWidget,0, prepTestDropDown);
        setTableDropdownModel(q,0, prepTestDropDown);

        s = (ScreenTableWidget)widgets.get("testReflexTable");
        reflexTestWidget = (TableWidget)s.getWidget();
        q = (QueryTable)s.getQueryWidget().getWidget();       
        setTableDropdownModel(reflexTestWidget,0, prepTestDropDown);
        setTableDropdownModel(q,0, prepTestDropDown);        
        setTableDropdownModel(reflexTestWidget,3, reflexTestFlagsDropDown);
        setTableDropdownModel(q,3, reflexTestFlagsDropDown);

        s = (ScreenTableWidget)widgets.get("worksheetTable");
        wsItemWidget = (TableWidget)s.getWidget();
        q = (QueryTable)s.getQueryWidget().getWidget();
        setTableDropdownModel(wsItemWidget,1, testWSItemTypeDropDown);
        setTableDropdownModel(q,1, testWSItemTypeDropDown);       

        s = (ScreenTableWidget)widgets.get("sectionTable");
        sectionWidget = (TableWidget)s.getWidget();
        sectionWidget.addTableWidgetListener(this);
        q = (QueryTable)s.getQueryWidget().getWidget();        
        setTableDropdownModel(sectionWidget,0, sectionDropDown);
        setTableDropdownModel(q,0, sectionDropDown);        
        setTableDropdownModel(sectionWidget,1, testSectionFlagDropDown);
        setTableDropdownModel(q,1, testSectionFlagDropDown);      
        
        s = (ScreenTableWidget)widgets.get("testResultsTable");
        resultWidget = (TableWidget)s.getWidget();
        resultWidget.addTableWidgetListener(this);        
        ((TableDropdown)resultWidget.columns.get(0).getColumnWidget()).setModel(testResultTypeDropDown);
        ((TableDropdown)resultWidget.columns.get(3).getColumnWidget()).setModel(testResultFlagDropDown);
        ((TableDropdown)resultWidget.columns.get(4).getColumnWidget()).setModel(roundingMethodDropDown);        
        setTableDropdownModel(resultWidget,0, testResultTypeDropDown);
        setTableDropdownModel(resultWidget,3, testResultFlagDropDown);
        setTableDropdownModel(resultWidget,4, roundingMethodDropDown);   
                
        //
        // set dropdown models for column 1 and 3 
        //
        setTableDropdownModel(analyteTreeController,1,"analyte",testAnalyteTypeDropDown);
        setTableDropdownModel(analyteTreeController,3,"analyte",scriptletDropDown);  

        analyteTreeController.model.manager = this;
        analyteTreeController.enabled(false);

        //override the callbacks
        updateChain.add(afterUpdate);

        super.afterDraw(success);

        analyteTree.enable(true);
        
        //
        // Each TableField or TreeField from the rpc is set the DataModel of the TableWidget or TreeWidget 
        // that it represents. This is done in order to make sure that the defaultSet
        // variable in default model of the field is iniliazed and is not null,
        // so that when the first time fetch is done for that field,on the server side 
        // when a new DataSet is to created from the model in the field for creating a 
        // new row in the table on the screen, there isn't a NullPointerException,
        // as creating a new dataset from the model involves the defaultSet for that model.
        //
        ((FormRPC)rpc.getField("sampleType")).setFieldValue("sampleTypeTable",
                                                            sampleTypeWidget.model.getData());

        ((FormRPC)rpc.getField("worksheet")).setFieldValue("worksheetTable",
                                                           wsItemWidget.model.getData());

        ((FormRPC)rpc.getField("prepAndReflex")).setFieldValue("testPrepTable",
                                                               prepTestWidget.model.getData());

        ((FormRPC)rpc.getField("prepAndReflex")).setFieldValue("testReflexTable",
                                                               reflexTestWidget.model.getData());

        ((FormRPC)rpc.getField("details")).setFieldValue("sectionTable",
                                                         sectionWidget.model.getData());

        analyteRPC = (FormRPC)(rpc.getField("testAnalyte"));
        
        //
        // the submit method puts the TreeDataModel from the TreeWidget in the 
        // tree field from the rpc
        //
        analyteTree.submit(analyteRPC.getField("analyteTree"));

        analyteRPC.setFieldValue("testResultsTable",
                                 resultWidget.model.getData());
        
        defaultModel = (DataModel)resultWidget.model.getData().clone();
    }

    /**
     * This function is for responding to the various events that take place on 
     * the screen and that can be identified by the value of the argument action.
     * This is different from the handling of normal click events, because more 
     * than one widget can send te same action and also the widget sending an action
     * may not be a button. This is called for all the objects added to the 
     * CommandChain for the screen in the afterDraw method
     */
    public void performCommand(Enum action, Object obj) {
        if (action == KeyListManager.Action.FETCH) {
            key = (DataSet)((Object[])obj)[0];
            //
            // See fillTestAnalyteDropDown(), fillTestResultDropDown(), fillModelMap();   
            // 
            fillTestAnalyteDropDown();
            fillTestResultDropDown();
            fillModelMap();                
        } else if(action == ButtonPanel.Action.QUERY && obj instanceof AppButton) {
            String query = ((AppButton)obj).action;
            if (query.indexOf(":") != -1)
                getTests(query.substring(6));
        }           
                  
        super.performCommand(action, obj);

    }

    /**
     * Overridden to manage various clicks that don't come through perform
     * command.
     */
    public void onClick(Widget sender) {
        if (sender == removeSampleTypeButton)
            onSampleTypeRowButtonClick();
        else if (sender == removePrepTestButton)
            onPrepTestRowButtonClick();
        else if (sender == removeReflexTestButton)
            onReflexTestRowButtonClick();
        else if (sender == removeWSItemButton)
            onWSItemRowButtonClick();
        else if (sender == addAnalyteButton)
            onAddAnalyteButtonClicked();
        else if (sender == addGroupButton)
            onAddGroupButtonClicked();
        // else if(sender == addAnalyteBeforeButton)
        // onAddAnalyteBeforeButtonClicked();
        // else if(sender == addGroupBeforeButton)
        // onAddGroupBeforeButtonClicked();
        else if (sender == deleteButton)
            onDeleteTreeItemButtonClicked();
        else if (sender == removeTestSectionButton)
            onTestSectionRowButtonClicked();
        else if (sender == dictionaryLookUpButton)
            onDictionaryLookUpButtonClicked();
        else if (sender == addResultTabButton)
            onAddResultTabButtonClicked();
    }

    
    
    public void query() {
        super.query();       
        enableTableAutoAdd(false);
        testId.setFocus(true);        
        removeSampleTypeButton.changeState(ButtonState.DISABLED);
        removePrepTestButton.changeState(ButtonState.DISABLED);
        removeReflexTestButton.changeState(ButtonState.DISABLED);
        removeWSItemButton.changeState(ButtonState.DISABLED);
        deleteButton.changeState(ButtonState.DISABLED);
        addAnalyteButton.changeState(ButtonState.DISABLED);
        addGroupButton.changeState(ButtonState.DISABLED);
        removeTestResultButton.changeState(ButtonState.DISABLED);
        dictionaryLookUpButton.changeState(ButtonState.DISABLED);
    }

    public void add() {
        super.add();      
        
        //resultGroupDropDown = null;
        enableTableAutoAdd(true);
        //
        // disable anything that is not editable and set focus to the widget 
        // which should get the first focus for editing 
        //
        testId.enable(false);                
        testName.setFocus(true);
    }      
    
    public void abort() {
        group = 0;     
        enableTableAutoAdd(false);
        super.abort();
    }

    protected AsyncCallback afterUpdate = new AsyncCallback() {
        public void onFailure(Throwable caught) {
            Window.alert(caught.getMessage());
        }

        public void onSuccess(Object result) {
            //
            // disable anything that is not editable and set focus to the widget 
            // which should get the first focus for editing 
            //
            enableTableAutoAdd(true);
            testId.enable(false);
            testName.setFocus(true);
        }
    };

    /*
     * public void doSubmit(){ FormRPC analyteRPC =
     * (FormRPC)(rpc.getField("testAnalyte")); CollectionField collField =
     * (CollectionField)(analyteRPC.getField("resultModelMap"));
     * 
     * //Iterator<DataModel> iter = ((ArrayList<DataModel>)resultMap.getValue()).iterator();
     * //while (iter.hasNext()){ // DataModel model = iter.next();
     * collField.setValue(resultMap); //}
     * 
     * super.doSubmit(rpc);
     *  }
     */

    public void onTabSelected(SourcesTabEvents sender, int tabIndex) {
    }

    //
    // Overriden to allow lazy loading various tabs of the various tab panels on
    // the screen 
    //
    public boolean onBeforeTabSelected(SourcesTabEvents sender, int tabIndex) {
        if (sender == resultPanel) {
            // resultWidget = (TableWidget)resultPanel.getWidget(tabIndex);
        } else {
            if (state != FormInt.State.QUERY) {
                if (tabIndex == 0 && !((FormRPC)rpc.getField("details")).load) {
                    fillTestDetails();
                } else if (tabIndex == 1 && !((FormRPC)rpc.getField("testAnalyte")).load) {
                    fillTestAnalyte();
                } else if (tabIndex == 2 && !((FormRPC)rpc.getField("prepAndReflex")).load) {
                    fillPrepTestsReflexTests();
                } else if (tabIndex == 3 && !((FormRPC)rpc.getField("sampleType")).load) {
                    fillSampleTypes();
                } else if (tabIndex == 4 && !((FormRPC)rpc.getField("worksheet")).load) {
                    fillWorksheetLayout();
                }
            }
        }
        return true;
    }

    //
    // This method fetches the data model stored in resultDropDownModelMap with
    // the analyte's id as the key and sets the model as the model for the "Results" column's dropdown
    // in the Reflexive Tests table 
    //
    public void setTestResultsForAnalyte(Integer analyteId, DataSet set) {
        if (analyteId != null) {
            DataModel model = (DataModel)resultDropDownModelMap.get(analyteId.toString());
            DropDownField field = (DropDownField)set.get(2);
            DataSet prevSet = new DataSet(new NumberObject((Integer)field.getValue()));
            DataSet blankSet = new DataSet(new NumberObject(-1));

            if (model != null && !model.equals(field.getModel())) {
                field.setModel(model);
                // if the new model doesn't contain the dataset that was selected 
                // from the previous one, then it may cause an ArrayIndexOutOfBoundsException
                // when you try to edit the Results column, this code makes sure
                // that in that situation the blank set is set as the value so that it becomes the
                // one that's selected and one that still belongs to the current data model.
                // Otherwise the previously selected set is set as the value
                if (model.contains(prevSet))
                    field.setValue(prevSet);
                else
                    field.setValue(blankSet);
            }

        }
    }
    
    

    public boolean canAdd(TableWidget widget, DataSet set, int row) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean canAutoAdd(TableWidget widget, DataSet addRow) {
        if (widget == wsItemWidget) {
            return addRow.get(0).getValue() != null;
        } /*
             * else if(widget == resultWidget){ int index =
             * resultPanel.getTabBar().getSelectedTab(); DataMap data = new
             * DataMap(); data.put("resultGroup",new NumberObject(index));
             * addRow.setData(data); }
             */
        return addRow.get(0).getValue() != null && !addRow.get(0)
                                                          .getValue()
                                                          .equals(-1);
    }

    public boolean canDelete(TableWidget widget, DataSet set, int row) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean canEdit(TableWidget widget, DataSet set, int row, int col) {
        if (state == State.UPDATE || state == State.ADD || state == State.QUERY) {
           // column 2 is for the Results dropdown 
            if (widget == reflexTestWidget && col == 2) {
                DropDownField ddfield = (DropDownField)set.get(1);
                Integer analyteId = (Integer)ddfield.getValue();
                setTestResultsForAnalyte(analyteId, set);
            }
            return true;
        }

        return false;
    }

    public boolean canSelect(TableWidget widget, DataSet set, int row) {
        if (state == State.UPDATE || state == State.ADD || state == State.QUERY)
            return true;
        return false;
    }

    public boolean canDrag(TableWidget widget, DataSet item, int row) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean canDrop(TableWidget widget,
                           Widget dragWidget,
                           DataSet dropTarget,
                           int targetRow) {
        // TODO Auto-generated method stub
        return false;
    }

    public void drop(TableWidget widget,
                     Widget dragWidget,
                     DataSet dropTarget,
                     int targetRow) {

    }

    public void drop(TableWidget widget, Widget dragWidget) {
        // TODO Auto-generated method stub

    }
     
    //
    // This function is called whenever a given cell in a table looses focus, 
    // which signifies that it is no longer being edited 
    //
    public void finishedEditing(SourcesTableWidgetEvents sender,
                                int row,
                                int col) {
        if (sender == sectionWidget && col == 1) {
            final int currRow = row;
            final Integer selValue = (Integer)sectionWidget.model.getRow(row)
                                                                 .get(col)
                                                                 .getValue();
            // 
            // This code is to find out which option was chosen in the "Options"
            // column of the Test Section table in the Test Details tab, so that 
            // the values for the other rows can be changed accordingly  
            //
            screenService.getObject("getCategorySystemName",
                                    new Data[] {new NumberObject(selValue)},
                                    new AsyncCallback<StringObject>() {
                                        public void onSuccess(StringObject result) {
                                            if ("test_section_default".equals((String)result.getValue())) {
                                                for (int iter = 0; iter < sectionWidget.model.numRows(); iter++) {
                                                    if (iter != currRow) {
                                                       // 
                                                       // if the option chosen is "Default" for this row, 
                                                       // then all the other rows must be set to the blank option
                                                       // 
                                                        DropDownField field = (DropDownField)sectionWidget.model.getRow(iter)
                                                                                                                .get(1);
                                                        field.setValue(new DataSet(new NumberObject(-1)));
                                                    }
                                                }
                                                sectionWidget.model.refresh();
                                            } else {
                                                if (selValue.intValue() != -1) {
                                                    for (int iter = 0; iter < sectionWidget.model.numRows(); iter++) {
                                                        DropDownField field = (DropDownField)sectionWidget.model.getRow(iter)
                                                                                                                .get(1);
//                                                      //
                                                        // if the option chosen is "Ask" or "Match User Location" for this row, 
                                                        // then all the other rows must be set to the same option which is this one
                                                        // 
                                                        field.setValue(new DataSet(new NumberObject(selValue)));
                                                    }
                                                }
                                                sectionWidget.model.refresh();
                                            }
                                        }

                                        public void onFailure(Throwable caught) {
                                            Window.alert(caught.getMessage());
                                            window.setStatus("", "");
                                        }
                                    });

        } else if (sender == resultWidget) {
            final String value = (String)resultWidget.model.getRow(row)
                                                           .get(1)
                                                           .getValue();
            if (col == 1 && !"".equals(value.trim())) {
                final int currRow = row;
                final Integer selValue = (Integer)resultWidget.model.getRow(row)
                                                                    .get(0)
                                                                    .getValue();
                //              
                // This code is to find out which option was chosen in the "Type"
                // column of the Test Results table in the "Analyte" tab, so that error checking 
                // or formatting can be done for the value set in the "Value" column    
                //
                screenService.getObject("getCategorySystemName",
                                        new Data[] {new NumberObject(selValue)},
                                        new AsyncCallback<StringObject>() {
                                            public void onSuccess(StringObject result) {
                                                Double[] darray = new Double[2];
                                                String finalValue = "";
                                                if ("test_res_type_dictionary".equals((String)result.getValue())) {
//                                                  //
                                                    // Find out if this value is stored in the database if the type chosen was "Dictionary"
                                                    //
                                                    screenService.getObject("getEntryId",
                                                                            new Data[] {new StringObject(value)},
                                                                            new AsyncCallback<NumberObject>() {

                                                                                public void onSuccess(NumberObject result1) {                                                                                    
                                                                                    //
                                                                                    // If this value is not stored in the database
                                                                                    // then add error to this cell in the "Value"
                                                                                    // column
                                                                                    if (result1.getValue() == null) {
                                                                                        resultWidget.model.setCellError(currRow,
                                                                                             1,"Not valid dictionary entry");
                                                                                    }
                                                                                }

                                                                                public void onFailure(Throwable caught) {
                                                                                    Window.alert(caught.getMessage());
                                                                                    window.setStatus("",
                                                                                                     "");
                                                                                }
                                                                            });
                                                   
                                                } else if ("test_res_type_numeric".equals((String)result.getValue())) {
                                                    //
                                                    // Get the string that was entered if the type chosen was "Numeric" and try to 
                                                    // break it up at the "," if it follows the pattern number,number 
                                                    //
                                                    String[] strList = value.split(",");
                                                    boolean convert = false;
                                                    if (strList.length == 2) {
                                                        for (int iter = 0; iter < strList.length; iter++) {
                                                            String token = strList[iter];
                                                            try {
                                                                // 
                                                                // Convert each number obtained from the string and store 
                                                                // its value converted to double if its a valid number, into an array
                                                                //
                                                                Double doubleVal = Double.valueOf(token);
                                                                darray[iter] = doubleVal.doubleValue();
                                                                convert = true;                                                                
                                                            } catch (NumberFormatException ex) {
                                                                convert = false;
                                                            }
                                                        }

                                                        if (convert)
                                                            //
                                                            // If its a valid string store the converted string back into the column
                                                            // otherwise add an error to the cell and store empty string into the cell
                                                            // 
                                                            finalValue = darray[0].toString() + ","
                                                                         + darray[1].toString();
                                                        else {
                                                            resultWidget.model.setCellError(currRow,
                                                                                            1,
                                                                                            "Invalid format for input string: " + value);                                                            
                                                        }

                                                    }
                                                    resultWidget.model.getRow(currRow)
                                                                      .get(1)
                                                                      .setValue(finalValue);
                                                    resultWidget.model.refresh();

                                                } else if ("test_res_type_titer".equals((String)result.getValue())) {
                                                    //
                                                    // Get the string that was entered if the type chosen was "Titer" and try to 
                                                    // break it up at the ":" if it follows the pattern "number:number" 
                                                    //
                                                    String[] strList = value.split(":");
                                                    boolean convert = false;
                                                    if (strList.length == 2) {
                                                        for (int iter = 0; iter < strList.length; iter++) {
                                                            String token = strList[iter];
                                                            try {
//                                                              //
                                                                // Convert each number obtained from the string and store 
                                                                // its value converted to double if its a valid number, into an array
                                                                //
                                                                Double doubleVal = Double.valueOf(token);
                                                                darray[iter] = doubleVal.doubleValue();
                                                                convert = true;
                                                            } catch (NumberFormatException ex) {
                                                                convert = false;
                                                            }
                                                        }

                                                        if (convert)
                                                            //
                                                            // If its a valid string store the converted string back into the column
                                                            // otherwise add an error to the cell and store empty string into the cell
                                                            //
                                                            finalValue = darray[0].toString() + ":"
                                                                         + darray[1].toString();
                                                        else {
                                                            resultWidget.model.setCellError(currRow,
                                                                                            1,
                                                                                            "Invalid format for input string: " + value);                                                            
                                                        }

                                                    }

                                                    resultWidget.model.getRow(currRow)
                                                                      .get(1)
                                                                      .setValue(finalValue);
                                                    resultWidget.model.refresh();
                                                }

                                            }

                                            public void onFailure(Throwable caught) {
                                                Window.alert(caught.getMessage());
                                                window.setStatus("", "");
                                            }
                                        });

            }
        }

    }

    public void startEditing(SourcesTableWidgetEvents sender, int row, int col) {
        // TODO Auto-generated method stub

    }

    public void stopEditing(SourcesTableWidgetEvents sender, int row, int col) {
        // TODO Auto-generated method stub

    }
    
    //
    // This function is called when the dictionary entry lookup screen is closed.
    // It add new rows to the Results table with the values for the "Value" column
    // being set to the entries that were chosen through the lookup screen 
    //    
    public void addResultRows(List<String> dictionaryEntries) {
        for (int iter = 0; iter < dictionaryEntries.size(); iter++) {
            DataSet row = resultWidget.model.createRow();
            String entry = dictionaryEntries.get(iter);
            row.get(1).setValue(entry);
            resultWidget.model.addRow(row);

            int index = resultPanel.getTabBar().getSelectedTab();
            DataMap data = new DataMap();
            data.put("resultGroup", new NumberObject(index));
            row.setData(data);
        }

        resultWidget.model.refresh();
    }

    public boolean canAdd(TreeWidget widget, TreeDataItem set, int row) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean canClose(TreeWidget widget, TreeDataItem set, int row) {
        // TODO Auto-generated method stub
        return true;
    }

    public boolean canDelete(TreeWidget widget, TreeDataItem set, int row) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean canDrag(TreeWidget widget, TreeDataItem item, int row) {
        if ("top".equals(item.leafType))
            return false;
        return true;
    }

    public boolean canDrop(TreeWidget widget,
                           Widget dragWidget,
                           TreeDataItem dropTarget,
                           int targetRow) {
        if ("analyte".equals(dropTarget.leafType))
            return false;
        return true;
    }

    public boolean canEdit(TreeWidget widget, TreeDataItem set, int row, int col) {
        return true;
    }

    public boolean canOpen(TreeWidget widget, TreeDataItem addRow, int row) {
        // TODO Auto-generated method stub
        return true;
    }

    public boolean canSelect(TreeWidget widget, TreeDataItem set, int row) {
        // TODO Auto-generated method stub
        return true;
    }

    public void drop(TreeWidget widget,
                     Widget dragWidget,
                     TreeDataItem dropTarget,
                     int targetRow) {
        TreeRow row = (TreeRow)dragWidget;
        if ("top".equals(dropTarget.leafType)) {
            dropTarget.addItem((TreeDataItem)row.item);
            analyteTreeController.model.deleteRow(row.modelIndex);
            analyteTreeController.model.refresh();
        } else if ("analyte".equals(dropTarget.leafType)) {
        }
    }

    public void drop(TreeWidget widget, Widget dragWidget) {

    }

    //
    // TreeManager functions end
    // 

    private void getTests(String query) {
        if (state == FormInt.State.DISPLAY || state == FormInt.State.DEFAULT) {

            FormRPC rpc;

            rpc = (FormRPC)this.forms.get("queryByLetter");
            rpc.setFieldValue(TestMeta.getName(), query);
            commitQuery(rpc);
        }
    }

    //
    // This function sets the Data Model for a dropdown field on the screen specified by "fieldName",
    // that's not inside a tree or table etc, to the argument "model"  
    //
    private void setDropdownModel(String fieldName, DataModel model) {
        Dropdown drop = (Dropdown)getWidget(fieldName);
        drop.setModel(model);
    }
    
    //
    // This function sets the Data Model for a dropdown field that's inside the 
    // TableWidget "widget" in the column "column" to the argument "model"  
    //
    private void setTableDropdownModel(TableWidget widget,int column, DataModel model) {
        ((TableDropdown)widget.columns.get(column).getColumnWidget()).setModel(model);
    }
    
    //
    // This function sets the Data Model for a dropdown field that's inside the 
    // TreeWidget "widget" in the column "column" of the node with leafType "leafType" to the argument "model"  
    //
    private void setTableDropdownModel(TreeWidget widget,int column,String leafType,DataModel model) {
        ((TableDropdown)widget.columns.get(column).getColumnWidget(leafType)).setModel(model);
    }

    //          
    // The function for loading the fields in "details" sub-rpc section as
    // defined in the xsl file for the screen. It's mapped to the "Test Details"
    // tab in the tab-panel on the screen
    //
    private void fillTestDetails() {
        if (key == null)
            return;

        window.setStatus("", "spinnerIcon");

        screenService.getObject("loadTestDetails",
                                new Data[] {key, rpc.getField("details")},
                                new AsyncCallback<FormRPC>() {
                                    public void onSuccess(FormRPC result) {
                                        load((FormRPC)result);
                                        rpc.setField("details", (FormRPC)result);
                                        window.setStatus("", "");

                                    }

                                    public void onFailure(Throwable caught) {
                                        Window.alert(caught.getMessage());
                                        window.setStatus("", "");
                                    }
                                });
    }

    //  
    // The function for loading the fields in the "prepAndReflex" sub-rpc section as
    // defined in the xsl file for the screen. It's mapped to the "Prep Test & Reflexive Test"
    // tab in the tab-panel on the screen
    //
    private void fillPrepTestsReflexTests() {
        if (key == null)
            return;

        window.setStatus("", "spinnerIcon");

        screenService.getObject("loadPrepTestsReflexTests",
                                new Data[] {key, rpc.getField("prepAndReflex")},
                                new AsyncCallback<FormRPC>() {
                                    public void onSuccess(FormRPC result) {
                                        load(result);
                                        rpc.setField("prepAndReflex",
                                                     (FormRPC)result);
                                        window.setStatus("", "");

                                    }

                                    public void onFailure(Throwable caught) {
                                        Window.alert(caught.getMessage());
                                        window.setStatus("", "");
                                    }
                                });
    }
        
    //  
    // The function for loading the fields in "sampleType" sub-rpc section as
    // defined in the xsl file for the screen. It's mapped to the "Sample Type"
    // tab in the tab-panel on the screen
    //
    private void fillSampleTypes() {
        if (key == null)
            return;

        window.setStatus("", "spinnerIcon");

        screenService.getObject("loadSampleTypes",
                                new Data[] {key, rpc.getField("sampleType")},
                                new AsyncCallback<FormRPC>() {
                                    public void onSuccess(FormRPC result) {
                                        load(result);
                                        rpc.setField("sampleType",
                                                     (FormRPC)result);
                                        window.setStatus("", "");

                                    }

                                    public void onFailure(Throwable caught) {
                                        Window.alert(caught.getMessage());
                                        window.setStatus("", "");
                                    }
                                });
    }

    //  
    // The function for loading the fields in "worksheet" sub-rpc section as
    // defined in the xsl file for the screen. It's mapped to the "WorkSheet Layout"
    // tab in the tab-panel on the screen
    //
    private void fillWorksheetLayout() {
        if (key == null)
            return;

        window.setStatus("", "spinnerIcon");

        screenService.getObject("loadWorksheetLayout",
                                new Data[] {key, rpc.getField("worksheet")},
                                new AsyncCallback<FormRPC>() {
                                    public void onSuccess(FormRPC result) {
                                        load(result);
                                        rpc.setField("worksheet", result);
                                        window.setStatus("", "");

                                    }

                                    public void onFailure(Throwable caught) {
                                        Window.alert(caught.getMessage());
                                        window.setStatus("", "");
                                    }
                                });
    }
   
    //  
    // The function for loading the fields in "testAnalyte" sub-rpc section as
    // defined in the xsl file for the screen. It's mapped to the "Analyte"
    // tab in the tab-panel on the screen
    //
    private void fillTestAnalyte() {
        if (key == null)
            return;

        window.setStatus("", "spinnerIcon");

        screenService.getObject("loadTestAnalyte",
                                new Data[] {key, rpc.getField("testAnalyte")},
                                new AsyncCallback<FormRPC>() {
                                    public void onSuccess(FormRPC result) {
                                        load(result);
                                        rpc.setField("testAnalyte", result);
                                        window.setStatus("", "");
                                    }

                                    public void onFailure(Throwable caught) {
                                        Window.alert(caught.getMessage());
                                        window.setStatus("", "");
                                    }
                                });
    }

    //
    // This function loads the Analyte dropdown in the Reflexive Test table with
    // the analytes added to the test, the data for which was the most recently fetched
    // 
    private void fillTestAnalyteDropDown() {
        if (key == null) {
            return;
        }

        NumberObject testId = (NumberObject)key.getKey();
        screenService.getObject("getTestAnalyteModel",
                                new Data[] {testId},
                                new AsyncCallback<DataModel>() {
                                    public void onSuccess(DataModel result) {
                                        DataModel model = result;
                                        ((TableDropdown)reflexTestWidget.columns.get(1)
                                                                                .getColumnWidget()).setModel(model);

                                    }

                                    public void onFailure(Throwable caught) {
                                        Window.alert(caught.getMessage());
                                        window.setStatus("", "");
                                    }
                                });
    }

    //
    // This function loads the Result dropdown in the Reflexive Test table with
    // the analytes added to the test, the data for which was the most recently fetched
    //
    private void fillTestResultDropDown() {
        if (key == null) {
            return;
        }
        NumberObject testId = (NumberObject)key.getKey();
        screenService.getObject("getTestResultModel",
                                new Data[] {testId},
                                new AsyncCallback<DataModel>() {
                                    public void onSuccess(DataModel result) {
                                        DataModel model = result;
                                        ((TableDropdown)reflexTestWidget.columns.get(2)
                                                                                .getColumnWidget()).setModel(model);
                                        ((TableDropdown)analyteTreeController.columns.get(4)
                                                                                     .getColumnWidget("analyte")).setModel(model);
                                        resultGroupDropDown = model;
                                        group = model.size() - 1;
                                    }

                                    public void onFailure(Throwable caught) {
                                        Window.alert(caught.getMessage());
                                        window.setStatus("", "");
                                    }
                                });
    }

    //
    // This function loads the resultDropDownModelMap variable, which is a DataMap
    // with the key-value pairs, such that the keys are the ids of the rows in 
    // the test_analyte table that correspond to this test and the corresponding
    // values are the lists of result values that are associated with that test_analyte. 
    // This map is then used to for faster access to these models, 
    // so that they don't have to fetched from the database every time
    //
    private void fillModelMap() {
        if (key == null) {
            return;
        }
        NumberObject testId = (NumberObject)key.getKey();
        DataMap dataMap = new DataMap();
        screenService.getObject("getTestResultModelMap",
                                new Data[] {testId, dataMap},
                                new AsyncCallback<DataMap>() {
                                    public void onSuccess(DataMap result) {
                                        DataMap map = result;
                                        resultDropDownModelMap = map;
                                    }

                                    public void onFailure(Throwable caught) {
                                        Window.alert(caught.getMessage());
                                        window.setStatus("", "");
                                    }
                                });
    }

    //
    // The functions for handling the click events associated with the
    // those buttons on the screen which remove selected rows from particular tables
    //
    private void onSampleTypeRowButtonClick() {
        if (sampleTypeWidget.model.getData().getSelectedIndex() > -1)
            sampleTypeWidget.model.deleteRow(sampleTypeWidget.model.getData()
                                                                   .getSelectedIndex());
    }

    private void onPrepTestRowButtonClick() {
        if (prepTestWidget.model.getData().getSelectedIndex() > -1)
            prepTestWidget.model.deleteRow(prepTestWidget.model.getData()
                                                               .getSelectedIndex());
    }

    private void onWSItemRowButtonClick() {
        if (wsItemWidget.model.getData().getSelectedIndex() > -1)
            wsItemWidget.model.deleteRow(wsItemWidget.model.getData()
                                                           .getSelectedIndex());
    }

    private void onReflexTestRowButtonClick() {
        if (reflexTestWidget.model.getData().getSelectedIndex() > -1)
            reflexTestWidget.model.deleteRow(reflexTestWidget.model.getData()
                                                                   .getSelectedIndex());
    }

    private void onTestSectionRowButtonClicked() {
        if (sectionWidget.model.getData().getSelectedIndex() > -1) {
            sectionWidget.model.deleteRow(sectionWidget.model.getData()
                                                             .getSelectedIndex());
        }
    }

    private void onAddResultTabButtonClicked() {
        group++;
        Document document = XMLParser.parse(panelString);
        ScreenVertical vertical = new ScreenVertical(document.getDocumentElement(),
                                                     this);
        VerticalPanel dummy = (VerticalPanel)vertical.getWidget();
         resultPanel.add(dummy,new Integer(group).toString());

    }

    private void onAddGroupButtonClicked() {
        gid++;
        TreeDataItem item = analyteTreeController.model.getData()
                                                       .createTreeItem("top",
                                                                       new NumberObject(gid));
        item.get(0).setValue("Group");
        analyteTreeController.model.addRow(item);
        analyteTreeController.model.refresh();
    }
    
    //
    // This function adds a group node in the analyte tree before the node that's selected currently
    //
    private void onAddGroupBeforeButtonClicked() {
        // TreeDataItem selItem =
        // analyteTreeController.model.getData().getSelected();
        int index = analyteTreeController.model.getSelectedRowIndex();
        if (index > -1) {
            // int index =
            // analyteTreeController.model.getData().indexOf(selItem);
            gid++;
            TreeDataItem item = analyteTreeController.model.getData()
                                                           .createTreeItem("top",
                                                                           new NumberObject(gid));
            item.get(0).setValue("Group");
            if (index == 0)
                analyteTreeController.model.addRow(index, item);
            else
                analyteTreeController.model.addRow(index - 1, item);
            analyteTreeController.model.refresh();
        }
    }

    //
    // This function adds an analyte to the analyte tree. It adds an analyte
    // after the currently selected node if it's an Analyte node, or as the child node to the currently
    // selected node if it's a Group node.
    //
    private void onAddAnalyteButtonClicked() {
        // TreeDataItem item =
        // analyteTreeController.model.getData().getSelected();
        int index = analyteTreeController.model.getSelectedRowIndex();
        if (index > -1) {
            TreeDataItem item = analyteTreeController.model.getRow(index);
            if ("top".equals(item.leafType)) {
                gid++;
                TreeDataItem newItem = analyteTreeController.model.getData()
                                                                  .createTreeItem("analyte",
                                                                                  new NumberObject(gid));
                item.addItem(newItem);
                analyteTreeController.model.refresh();
            } else {
                gid++;
                TreeDataItem newItem = analyteTreeController.model.getData()
                                                                  .createTreeItem("analyte",
                                                                                  new NumberObject(gid));
                analyteTreeController.model.addRow(newItem);
                analyteTreeController.model.refresh();
            }
        } else {
            gid++;
            TreeDataItem newItem = analyteTreeController.model.getData()
                                                              .createTreeItem("analyte",
                                                                              new NumberObject(gid));
            analyteTreeController.model.addRow(newItem);
            analyteTreeController.model.refresh();
        }
    }

    //
    // This function adds an analyte node in the analyte tree before the node that's selected currently
    //
    private void onAddAnalyteBeforeButtonClicked() {
        int index = analyteTreeController.model.getSelectedRowIndex();
        if (index > -1) {
            TreeDataItem item = analyteTreeController.model.getRow(index);

            if (item.parent == null) {
                gid++;
                TreeDataItem newItem = analyteTreeController.model.getData()
                                                                  .createTreeItem("analyte",
                                                                                  new NumberObject(gid));
                if (index == 0)
                    analyteTreeController.model.addRow(index, newItem);
                else
                    analyteTreeController.model.addRow(index - 1, newItem);
                analyteTreeController.model.refresh();
            }
        }// else {
        // gid++;
        // TreeDataItem newItem =
        // analyteTreeController.model.getData().createTreeItem("analyte",new
        // NumberObject(gid));
        // TreeDataItem parent = newItem.parent;
        // int chindex = parent.indexOf(selItem);

        // analyteTreeController.model.refresh();
        // }
    }
 
    private void onDeleteTreeItemButtonClicked() {
        /*
         * int index = analyteTreeController.model.getSelectedIndex(); if(index >
         * -1){
         * 
         * analyteTreeController.model.deleteRow(index); //}
         * analyteTreeController.model.refresh(); }
         */
        TreeDataItem item = analyteTreeController.model.getData().getSelected();
        if (item != null) {
            int index = analyteTreeController.model.getData().indexOf(item);
            analyteTreeController.model.deleteRow(index);
            analyteTreeController.model.refresh();
        }
    }

    //
    // This function opens a modal window which allows the users to select one or
    // more dictionary entries to be added to the Test Results table   
    //
    private void onDictionaryLookUpButtonClicked() {
        PopupPanel standardNotePopupPanel = new PopupPanel(false, true);
        ScreenWindow pickerWindow = new ScreenWindow(standardNotePopupPanel,
                                                     "Choose Dictionary Entry",
                                                     "dictionaryEntryPicker",
                                                     "Loading...");
        pickerWindow.setContent(new DictionaryEntryPickerScreen(this));

        standardNotePopupPanel.add(pickerWindow);
        int left = this.getAbsoluteLeft();
        int top = this.getAbsoluteTop();
        standardNotePopupPanel.setPopupPosition(left, top);
        standardNotePopupPanel.show();
    }
    
    private void enableTableAutoAdd(boolean enable){
        reflexTestWidget.model.enableAutoAdd(enable);
        wsItemWidget.model.enableAutoAdd(enable);
        prepTestWidget.model.enableAutoAdd(enable);
        sampleTypeWidget.model.enableAutoAdd(enable);
        sectionWidget.model.enableAutoAdd(enable);
        resultWidget.model.enableAutoAdd(enable);
    }
    
    private void loadModel(String category,TableDropdown drop){
        StringObject catObj = new StringObject(category);
        screenService.getObject("getInitialModel",
                                new Data[] {catObj},
                                new AsyncCallback<DataModel>() {
                                    public void onSuccess(DataModel result) {
                                        //setDropdownModel(category, result);
                                    }

                                    public void onFailure(Throwable caught) {
                                        Window.alert(caught.getMessage());
                                        window.setStatus("", "");
                                    }
                                });
        
    }

}