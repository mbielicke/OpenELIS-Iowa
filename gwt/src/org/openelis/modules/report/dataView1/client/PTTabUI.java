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
package org.openelis.modules.report.dataView1.client;

import static org.openelis.modules.main.client.Logger.*;
import static org.openelis.ui.screen.State.*;

import java.util.ArrayList;
import java.util.Map;

import org.openelis.domain.Constants;
import org.openelis.domain.DataViewVO1;
import org.openelis.meta.SampleWebMeta;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.CheckBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;

public class PTTabUI extends Screen {
    @UiTemplate("PTTab.ui.xml")
    interface PTTabUIBinder extends UiBinder<Widget, PTTabUI> {
    };

    private static PTTabUIBinder uiBinder = GWT.create(PTTabUIBinder.class);

    @UiField
    protected CheckBox           ptPTProviderId, ptSeries, ptDueDate, receivedById;

    protected Screen             parentScreen;

    protected EventBus           parentBus;

    protected DataViewVO1        data;

    protected String             domain;

    protected boolean            canEdit;

    public PTTabUI(Screen parentScreen) {
        this.parentScreen = parentScreen;
        this.parentBus = parentScreen.getEventBus();
        initWidget(uiBinder.createAndBindUi(this));
        initialize();

        data = null;
        domain = null;
    }

    public void initialize() {
        addScreenHandler(ptPTProviderId,
                         SampleWebMeta.getPTPTProviderId(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 ptPTProviderId.setValue(getValue(SampleWebMeta.getPTPTProviderId()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 ptPTProviderId.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? ptSeries : receivedById;
                             }
                         });

        addScreenHandler(ptSeries, SampleWebMeta.getPTSeries(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                ptSeries.setValue(getValue(SampleWebMeta.getPTSeries()));
            }

            public void onStateChange(StateChangeEvent event) {
                ptSeries.setEnabled(isState(DEFAULT) && canEdit);
            }

            public Widget onTab(boolean forward) {
                return forward ? ptDueDate : ptPTProviderId;
            }
        });

        addScreenHandler(ptDueDate, SampleWebMeta.getPTDueDate(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                ptDueDate.setValue(getValue(SampleWebMeta.getPTDueDate()));
            }

            public void onStateChange(StateChangeEvent event) {
                ptDueDate.setEnabled(isState(DEFAULT) && canEdit);
            }

            public Widget onTab(boolean forward) {
                return forward ? receivedById : ptSeries;
            }
        });

        addScreenHandler(receivedById,
                         SampleWebMeta.getReceivedById(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 receivedById.setValue(getValue(SampleWebMeta.getReceivedById()));
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 receivedById.setEnabled(isState(DEFAULT) && canEdit);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? ptPTProviderId : ptDueDate;
                             }
                         });

        parentBus.addHandler(DomainChangeEvent.getType(), new DomainChangeEvent.Handler() {
            @Override
            public void onDomainChange(DomainChangeEvent event) {
                /*
                 * the widgets in this tab need to be enabled or disabled based
                 * on the current domain
                 */
                domain = event.getDomain();
                setState(state);
            }
        });
    }

    public void setData(DataViewVO1 data) {
        this.data = data;
    }

    public void setState(State state) {
        evaluateEdit();
        this.state = state;
        bus.fireEventFromSource(new StateChangeEvent(state), this);
    }

    public void onDataChange() {
        fireDataChange();
    }
    
    /**
     * Adds the keys for all checked checkboxes to the list of columns shown
     * in the generated excel file
     */
    public void addColumns(ArrayList<String> columns) {
        Widget w;
        CheckBox cb;

        if (!canEdit)
            return;
        
        for (Map.Entry<String, ScreenHandler<?>> entry : handlers.entrySet()) {
            w = entry.getValue().widget;
            if (w instanceof CheckBox) {
                cb = (CheckBox)w;
                if ("Y".equals(cb.getValue()))
                    columns.add(entry.getKey());
            }
        }
    }

    private String getValue(String column) {
        if (data == null || data.getColumns() == null)
            return "N";
        return data.getColumns().contains(column) ? "Y" : "N";
    }

    private void evaluateEdit() {
        canEdit = (domain == null || Constants.domain().PT.equals(domain));
    }
}