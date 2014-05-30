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

import java.util.ArrayList;

import org.openelis.cache.CategoryCache;
import org.openelis.constants.Messages;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.State;
import org.openelis.ui.screen.Screen.Validation;
import org.openelis.ui.screen.Screen.Validation.Status;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.Item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;

/**
 * This class is used to allow users to change a sample's domain
 */
public abstract class ChangeDomainLookupUI extends Screen {

    @UiTemplate("ChangeDomainLookup.ui.xml")
    interface ChangeDomainLookupUIBinder extends UiBinder<Widget, ChangeDomainLookupUI> {
    };

    private static ChangeDomainLookupUIBinder uiBinder = GWT.create(ChangeDomainLookupUIBinder.class);

    @UiField
    protected Dropdown<String>                domain;

    @UiField
    protected Button                          okButton, cancelButton;
    
    protected String selectedDomain;

    public ChangeDomainLookupUI() {
        initWidget(uiBinder.createAndBindUi(this));
        initialize();
    }

    public void initialize() {
        String dom;
        Item<String> row;
        ArrayList<Item<String>> model;
        ArrayList<DictionaryDO> list;
        
        addScreenHandler(domain, "domain", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                domain.setValue(selectedDomain);
            }
            
            public void onStateChange(StateChangeEvent event) {
                domain.setEnabled(true);
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

        model = new ArrayList<Item<String>>();
        list = CategoryCache.getBySystemName("sample_domain");
        for (DictionaryDO d : list) {
            dom = null;
            if (Constants.dictionary().ENVIRONMENTAL.equals(d.getId()))
                dom = Constants.domain().ENVIRONMENTAL;
            else if (Constants.dictionary().PRIVATE_WELL.equals(d.getId()))
                dom = Constants.domain().PRIVATEWELL;
            else if (Constants.dictionary().SDWIS.equals(d.getId()))
                dom = Constants.domain().SDWIS;
            else if (Constants.dictionary().NEWBORN.equals(d.getId()))
                dom = Constants.domain().NEONATAL;
            else if (Constants.dictionary().CLINICAL.equals(d.getId()))
                dom = Constants.domain().CLINICAL;

            if (dom != null) {
                row = new Item<String>(dom, d.getEntry());
                row.setEnabled( ("Y".equals(d.getIsActive())));
                model.add(row);
            }
        }

        domain.setModel(model);
    }

    public void setDomain(String domain) {
        selectedDomain = domain;
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
     * returns the selected domain
     */
    public String getDomain() {
        return selectedDomain;
    }

    @UiHandler("okButton")
    protected void ok(ClickEvent event) {
        Validation validation;

        finishEditing();

        validation = validate();
        
        if (validation.getStatus() == Status.ERRORS) {
            setError(Messages.get().gen_correctErrors());
            return;
        }
        
        selectedDomain = domain.getValue();
        window.close();
        ok();
    }

    @UiHandler("cancelButton")
    protected void cancel(ClickEvent event) {
        window.close();
        cancel();
    }
}