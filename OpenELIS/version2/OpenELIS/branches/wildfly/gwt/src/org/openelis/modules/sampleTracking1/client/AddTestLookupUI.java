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
package org.openelis.modules.sampleTracking1.client;

import static org.openelis.modules.main.client.Logger.logger;

import java.util.ArrayList;
import java.util.logging.Level;

import org.openelis.constants.Messages;
import org.openelis.domain.TestMethodVO;
import org.openelis.modules.panel1.client.PanelService1Impl;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.GetMatchesEvent;
import org.openelis.ui.event.GetMatchesHandler;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.Screen.Validation.Status;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.AutoComplete;
import org.openelis.ui.widget.AutoCompleteValue;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.QueryFieldUtil;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

/**
 * This class is used to allow users to add a test to a sample
 */
public abstract class AddTestLookupUI extends Screen {

    @UiTemplate("AddTestLookup.ui.xml")
    interface AddTestLookupUIBinder extends UiBinder<Widget, AddTestLookupUI> {
    };

    private static AddTestLookupUIBinder uiBinder = GWT.create(AddTestLookupUIBinder.class);

    @UiField
    protected AutoComplete               test;

    @UiField
    protected Button                     okButton, cancelButton;

    protected Integer                    sampleType;

    protected TestMethodVO               selectedTest;

    public AddTestLookupUI() {
        initWidget(uiBinder.createAndBindUi(this));
        initialize();
    }

    public void initialize() {
        addScreenHandler(test, "test", new ScreenHandler<AutoCompleteValue>() {
            public void onDataChange(DataChangeEvent event) {
                test.setValue(null, null);
            }

            public void onStateChange(StateChangeEvent event) {
                test.setEnabled(true);
            }

            public Widget onTab(boolean forward) {
                return forward ? okButton : okButton;
            }
        });

        test.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                Integer key;
                Query query;
                QueryData field;
                Item<Integer> row;
                ArrayList<QueryData> fields;
                ArrayList<Item<Integer>> model;
                ArrayList<TestMethodVO> tests;

                if (sampleType == null) {
                    setError(Messages.get().sample_sampleItemTypeRequired());
                    return;
                }

                fields = new ArrayList<QueryData>();
                query = new Query();

                try {
                    field = new QueryData();
                    field.setQuery(QueryFieldUtil.parseAutocomplete(event.getMatch()) + "%");
                    fields.add(field);

                    field = new QueryData();
                    field.setQuery(String.valueOf(sampleType));
                    fields.add(field);

                    query.setFields(fields);

                    setBusy();

                    tests = PanelService1Impl.INSTANCE.fetchByNameSampleTypeWithTests(query);

                    model = new ArrayList<Item<Integer>>();
                    for (TestMethodVO t : tests) {
                        /*
                         * Since the keys of the rows need to be unique and it
                         * can happen that a panel has the same id as a test, a
                         * negative number is used as the key for a row showing
                         * a panel and a positive one for a row showing a test.
                         * An index in a loop can't be used here because it can
                         * clash with an id and two different rows may be
                         * treated as the same.
                         */

                        key = t.getMethodId() == null ? -t.getTestId() : t.getTestId();

                        row = new Item<Integer>(3);
                        row.setKey(key);
                        row.setCell(0, t.getTestName());
                        row.setCell(1, t.getMethodName());
                        row.setCell(2, t.getTestDescription());
                        row.setData(t);

                        model.add(row);
                    }

                    test.showAutoMatches(model);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                }

                clearStatus();
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
    }

    public void setSampleType(Integer sampleType) {
        this.sampleType = sampleType;
        selectedTest = null;
        clearErrors();
        setState(state);
        fireDataChange();
    }

    public void setState(State state) {
        this.state = state;
        bus.fireEventFromSource(new StateChangeEvent(state), this);
    }

    /**
     * overridden to respond to the user clicking "ok"
     */
    public abstract void ok();

    /**
     * overridden to respond to the user clicking "cancel"
     */
    public abstract void cancel();

    /**
     * returns the test selected to be added
     */
    public TestMethodVO getTest() {
        return selectedTest;
    }

    @UiHandler("okButton")
    protected void ok(ClickEvent event) {
        finishEditing();

        if (validate().getStatus() == Status.ERRORS) {
            setError(Messages.get().gen_correctErrors());
            return;
        }

        selectedTest = (TestMethodVO)test.getValue().getData();

        window.close();
        ok();
    }

    @UiHandler("cancelButton")
    protected void cancel(ClickEvent event) {
        clearErrors();
        window.close();
        cancel();
    }
}