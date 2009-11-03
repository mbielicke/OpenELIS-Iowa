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
package org.openelis.modules.sampleLocation.client;

import org.openelis.gwt.common.Query;
import org.openelis.gwt.common.data.KeyListManager;
import org.openelis.gwt.common.data.TableDataModel;
import org.openelis.gwt.common.data.TableDataRow;
import org.openelis.gwt.screen.CommandChain;
import org.openelis.gwt.widget.ButtonPanel;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.metamap.SampleEnvironmentalMetaMap;
import org.openelis.modules.environmentalSampleLogin.client.SampleLocationForm;
import org.openelis.modules.environmentalSampleLogin.client.SampleProjectForm;
import org.openelis.modules.main.client.OpenELISScreenForm;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TextBox;

public class SampleLocationScreen extends OpenELISScreenForm<SampleLocationForm,Query<TableDataRow<Integer>>> {

    private Dropdown               states, countries;
    private TextBox                 location;
    
    private KeyListManager keyList = new KeyListManager();
    
    private SampleEnvironmentalMetaMap Meta = new SampleEnvironmentalMetaMap();

    AsyncCallback<SampleLocationForm> checkModels = new AsyncCallback<SampleLocationForm>() {
        public void onSuccess(SampleLocationForm rpc) {
            if(rpc.countries != null) {
                setCountriesModel(rpc.countries);
                rpc.countries = null;
            }
            if(rpc.states != null) {
                setStatesModel(rpc.states);
                rpc.states = null;
            }
        }
        
        public void onFailure(Throwable caught) {
            
        }
    };

    public SampleLocationScreen() {          
        this(new SampleLocationForm());
    }
    
    public SampleLocationScreen(SampleLocationForm form) {                
        super("org.openelis.modules.sampleLocation.server.SampleLocationService");
        query = new Query<TableDataRow<Integer>>();

        getScreen(form);
    }
    
    public void setForm(SampleLocationForm form){
        this.form = form;
        load(form);
    }
    
    public void afterDraw(boolean success) {
        ButtonPanel bpanel = (ButtonPanel) getWidget("buttons");
        
        states = (Dropdown)getWidget(Meta.ADDRESS.getState());
        countries = (Dropdown)getWidget(Meta.ADDRESS.getCountry());
        location = (TextBox)getWidget(Meta.getSamplingLocation());
        
        CommandChain chain = new CommandChain();
        chain.addCommand(this);
        chain.addCommand(keyList);
        chain.addCommand(bpanel);
        
        setCountriesModel(form.countries);
        setStatesModel(form.states);
        
        form.countries = null;
        form.states = null;
        
        super.afterDraw(success);
        
        load(form);
        location.setFocus(true);
    }
    
    public void commit() {
        window.close();
    }

    public void abort() {
        window.close();
    }
    
    public void setCountriesModel(TableDataModel<TableDataRow<String>> countriesModel) {
        countries.setModel(countriesModel);
    }
    
    public void setStatesModel(TableDataModel<TableDataRow<String>> statesModel) {
        states.setModel(statesModel);
    }
}