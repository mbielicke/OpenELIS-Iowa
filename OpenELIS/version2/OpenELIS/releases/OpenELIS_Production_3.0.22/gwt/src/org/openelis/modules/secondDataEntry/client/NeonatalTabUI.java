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
package org.openelis.modules.secondDataEntry.client;

import org.openelis.domain.Constants;
import org.openelis.manager.SampleManager1;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.State;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;

public class NeonatalTabUI extends Screen {
    @UiTemplate("NeonatalTab.ui.xml")
    interface NeonatalTabUiBinder extends UiBinder<Widget, NeonatalTabUI> {
    };

    private static NeonatalTabUiBinder                     uiBinder = GWT.create(NeonatalTabUiBinder.class);

    
    protected SampleManager1                            manager;

    protected Screen                                    parentScreen;

    protected EventBus                                  parentBus;

    protected NeonatalTabUI                               screen;
    
    protected boolean                                   canEdit;

    public NeonatalTabUI(Screen parentScreen) {
        this.parentScreen = parentScreen;
        this.parentBus = parentScreen.getEventBus();
        initWidget(uiBinder.createAndBindUi(this));
    }

    /**
     * Setup state and data change handles for every widget on the screen
     */
    private void initialize() {        
        screen = this;
    }

    public void setData(SampleManager1 manager) {
        this.manager = manager;
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
     * Show widgets used for verification for this domain
     */
    public void showWidgets() {
        initialize();
    }

    /**
     * Returns the text for the event log for this sample; the text shows which
     * operation, e.g. copy from or to the sample, was performed on which field
     */
    public String getLogText() {
        return null;
    }

    private void evaluateEdit() {
        canEdit = manager != null &&
                  Constants.domain().NEONATAL.equals(manager.getSample().getDomain());
    }
}