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
package org.openelis.modules.report.dataView1.client;

import static org.openelis.modules.main.client.Logger.*;
import static org.openelis.ui.screen.State.*;

import org.openelis.constants.Messages;
import org.openelis.modules.sample1.client.SampleItemPopoutLookupUI;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.resources.UIResources;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.ModalWindow;
import org.openelis.ui.widget.TabLayoutPanel;
import org.openelis.ui.widget.WindowInt;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;

public class DataViewScreenUI extends Screen {
    
    @UiTemplate("DataView.ui.xml")
    interface DataViewScreenUiBinder extends UiBinder<Widget, DataViewScreenUI> {
    };

    private static DataViewScreenUiBinder     uiBinder = GWT.create(DataViewScreenUiBinder.class);
    
    @UiField
    protected Button                           openQueryButton, saveQueryButton,
                                                executeQueryButton;
    @UiField
    protected TabLayoutPanel                   tabPanel;
    
    @UiField(provided = true)
    protected QueryTabUI                       queryTab;
    
    @UiField(provided = true)
    protected CommonTabUI                      commonTab;

    @UiField(provided = true)
    protected EnvironmentalTabUI               environmentalTab;

    @UiField(provided = true)
    protected PrivateWellTabUI                 privateWellTab;

    @UiField(provided = true)
    protected SDWISTabUI                       sdwisTab;

    @UiField(provided = true)
    protected NeonatalTabUI                    neonatalTab;

    @UiField(provided = true)
    protected ClinicalTabUI                    clinicalTab;
    
    @UiField(provided = true)
    protected PTTabUI                          ptTab;
    
    protected FilterScreenUI                   filterScreen;
    
    public DataViewScreenUI(WindowInt window) throws Exception {
        setWindow(window);
        
        queryTab = new QueryTabUI(this);
        commonTab = new CommonTabUI(this);
        environmentalTab = new EnvironmentalTabUI(this);
        privateWellTab = new PrivateWellTabUI(this);
        sdwisTab = new SDWISTabUI(this);
        clinicalTab = new ClinicalTabUI(this);
        neonatalTab = new NeonatalTabUI(this);
        ptTab = new PTTabUI(this);
        
        initWidget(uiBinder.createAndBindUi(this));

        initialize();
        setData();
        setState(DEFAULT);
        fireDataChange();

        logger.fine("Data View Screen Opened");
    }

    private void initialize() {
        addScreenHandler(openQueryButton, "openQueryButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                openQueryButton.setEnabled(true);
            }
        });
        
        addScreenHandler(saveQueryButton, "saveQueryButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                saveQueryButton.setEnabled(true);
            }
        });
        
        addScreenHandler(executeQueryButton, "executeQueryButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                executeQueryButton.setEnabled(true);
            }
        });
    }
    
    private void setData() {
        // TODO Auto-generated method stub
    }
    
    @UiHandler("executeQueryButton")
    protected void executeQuery(ClickEvent event) {
        ModalWindow modal;
        
        if (filterScreen == null) {
            filterScreen = new FilterScreenUI() {
                @Override
                public void runReport() {
                    // TODO Auto-generated method stub
                    
                }

                @Override
                public void cancel() {
                    // TODO Auto-generated method stub
                    
                }            

            };
        }

        modal = new ModalWindow();
        modal.setSize("620px", "687px");
        modal.setName(Messages.get().filterScreen_testAnalyteAuxDataFilter());
        modal.setCSS(UIResources.INSTANCE.popupWindow());
        modal.setContent(filterScreen);

        filterScreen.setWindow(modal);
        //filterScreen.setData(manager, state);
    }
}
