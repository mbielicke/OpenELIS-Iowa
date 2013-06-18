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

import java.util.ArrayList;

import org.openelis.cache.CategoryCache;
import org.openelis.constants.Messages;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.TestMethodVO;
import org.openelis.modules.panel.client.PanelService;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;
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
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.QueryFieldUtil;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

/**
 * This class is used to allow users to choose a test from a list of tests that
 * have a specific sample type
 */
public abstract class TestLookupUI extends Screen {

    @UiTemplate("TestLookup.ui.xml")
    interface TestLookupUIBinder extends UiBinder<Widget, TestLookupUI> {
    };

    private static TestLookupUIBinder uiBinder = GWT.create(TestLookupUIBinder.class);

    @UiField
    protected Dropdown<Integer>       sampleType;

    @UiField
    protected AutoComplete            testName;

    @UiField
    protected Button                  okButton, cancelButton;

    protected Integer                 sampleTypeId;
    
    protected TestMethodVO            test;
    
    public TestLookupUI() {
        initWidget(uiBinder.createAndBindUi(this));
        initialize();
    }

    private void initialize() {
        Item<Integer> row;
        ArrayList<Item<Integer>> model;

        addScreenHandler(sampleType, "sampleTypeId", new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                sampleType.setValue(sampleTypeId);
            }
            
            public void onValueChange(ValueChangeEvent<Integer> event) {
                sampleType.setValue(event.getValue());
                setTest(event.getValue(), null);
            }
            
            public void onStateChange(StateChangeEvent event) {            
                sampleType.setEnabled(sampleTypeId == null);
            }
        });

        addScreenHandler(testName, "testName", new ScreenHandler<AutoCompleteValue>() {
            public void onDataChange(DataChangeEvent event) {
                setTest(sampleTypeId, null);
            }
            
            public void onValueChange(ValueChangeEvent<AutoCompleteValue> event) {
                setTest(sampleType.getValue(), event.getValue());
            }
            
            public void onStateChange(StateChangeEvent event) {            
                testName.setEnabled(sampleTypeId == null);
            }
        });

        testName.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                showTestMatches(event.getMatch());
            }
        });
        
        addScreenHandler(okButton, "okButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                okButton.setEnabled(true);
            }
        });

        addScreenHandler(cancelButton, "cancelButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                cancelButton.setEnabled(true);
            }
        });

        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        for (DictionaryDO d : CategoryCache.getBySystemName("type_of_sample")) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled("Y".equals(d.getIsActive()));
            model.add(row);
        }
        sampleType.setModel(model);
    }   
    
    public void setState(State state) {
        this.state = state;
        bus.fireEventFromSource(new StateChangeEvent(state), this);
    }

    public void setSampleTypeId(Integer sampleTypeId) {
        this.sampleTypeId = sampleTypeId;
        setState(state);
        fireDataChange();
        
        if (sampleTypeId == null)
            sampleType.setFocus(true);
        else 
            testName.setFocus(true);
    }
    
    public Integer getSampletypeId() {
        return sampleTypeId;
    }
    
    public TestMethodVO getTest() {
        return test;
    }
    
    /**
     * overridden to respond to the user clicking "ok"
     */
    public abstract void ok();

    /**
     * overridden to respond to the user clicking "cancel"
     */
    public abstract void cancel();
    
    @UiHandler("okButton")
    protected void ok(ClickEvent event) {
        AutoCompleteValue val;
        
        val = testName.getValue();
        if (val == null) {
            window.setError(Messages.get().sample_chooseTestOrPanel());
            return;
        }
        
        sampleTypeId = sampleType.getValue();
        test = (TestMethodVO)val.getData();        
        window.close();
        ok();
    }

    @UiHandler("cancelButton")
    protected void cancel(ClickEvent event) {
        window.close();
        cancel();
    }

    private void setTest(Integer sampleTypeId, AutoCompleteValue val) {
        testName.setValue(val);
        testName.setEnabled(sampleTypeId != null);
    }

    private void showTestMatches(String name) {
        Integer key;
        ArrayList<QueryData> fields;
        ArrayList<Item<Integer>> model;
        ArrayList<TestMethodVO> tests;
        Query query;
        QueryData field;
        Item<Integer> row;

        if (sampleType.getValue() == null) {
            window.setError(Messages.get().sampleItemTypeRequired());
            return;
        }

        fields = new ArrayList<QueryData>();
        query = new Query();

        try {
            field = new QueryData();
            field.setQuery(QueryFieldUtil.parseAutocomplete(name) + "%");
            fields.add(field);

            field = new QueryData();
            field.setQuery(String.valueOf(sampleType.getValue()));
            fields.add(field);

            query.setFields(fields);

            window.setBusy();
            tests = PanelService.get().fetchByNameSampleTypeWithTests(query);

            model = new ArrayList<Item<Integer>>();
            for (TestMethodVO t : tests) {
                /*
                 * Since the keys of the rows need to be unique and it can
                 * happen that a panel has the same id as a test, a negative
                 * number is used as the key for a row showing a panel and a
                 * positive one for a row showing a test. An index in a loop
                 * can't be used here because it can clash with an id and two
                 * different rows may be treated as the same.
                 */

                key = t.getMethodId() == null ? -t.getTestId() : t.getTestId();

                row = new Item<Integer>(4);
                row.setKey(key);
                row.setCell(0, t.getTestName());
                row.setCell(1, t.getMethodName());
                row.setCell(2, t.getTestDescription());
                row.setData(t);

                model.add(row);
            }

            testName.showAutoMatches(model);
        } catch (Exception e) {
            Window.alert(e.getMessage());
        }

        window.clearStatus();
    }
}