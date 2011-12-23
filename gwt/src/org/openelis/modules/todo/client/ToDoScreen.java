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
package org.openelis.modules.todo.client;

import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.CheckBox;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.manager.SampleManager;
import org.openelis.modules.sampleTracking.client.SampleTrackingScreen;
import org.openelis.modules.worksheetCompletion.client.WorksheetCompletionScreen;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.TabPanel;

public class ToDoScreen extends Screen {

    private ToDoScreen                screen;
    private CheckBox                  mySection;
    private AppButton                 refreshButton, trackingButton, exportToXl;
    private TabPanel                  tabPanel;
    private Tabs                      tab;
    private LoggedInTab               loggedIntab;
    private InitiatedTab              initiatedTab;
    private CompletedTab              completedTab;
    private ReleasedTab               releasedTab;
    private ToBeVerifiedTab           toBeVerifiedTab;
    private OtherTab                  otherTab;
    private WorksheetTab              worksheetTab;
    private SampleTrackingScreen      sampleTrackingScreen;
    
    private enum Tabs {
        LOGGED_IN, INITIATED, WORKSHEET, COMPLETED, RELEASED, TO_BE_VERIFIED, OTHER, INSTRUMENT;
    };
    
    public ToDoScreen() throws Exception {
        super((ScreenDefInt)GWT.create(ToDoDef.class));

        DeferredCommand.addCommand(new Command() {
            public void execute() {
                postConstructor();
            }
        });
    }

    private void postConstructor() {
        tab = Tabs.LOGGED_IN;
    
        initialize();
        mySection.setValue("Y");
        setState(State.DEFAULT);
        DataChangeEvent.fire(this);
    }

    private void initialize() {
        screen = this;
        
        mySection = (CheckBox)def.getWidget("mySection");
        addScreenHandler(mySection, new ScreenEventHandler<String>() {
            public void onValueChange(ValueChangeEvent<String> event) {
                drawTabs(false);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                mySection.enable(true);
            }
        });
        
        refreshButton = (AppButton)def.getWidget("refreshButton");
        addScreenHandler(refreshButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                DataChangeEvent.fire(screen);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                refreshButton.enable(true);
            }
        });
        
        trackingButton = (AppButton)def.getWidget("trackingButton");
        addScreenHandler(trackingButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                Integer id;
                
                try {
                    switch (tab) {
                        case LOGGED_IN:
                            id = loggedIntab.getSelectedId();
                            if (id != null)
                                showTrackingScreen(id);
                            break;
                        case INITIATED:
                            id = initiatedTab.getSelectedId();
                            if (id != null)
                                showTrackingScreen(id);
                            break;
                        case WORKSHEET:
                            id = worksheetTab.getSelectedId();
                            if (id != null)
                                showCompletionScreen(id);
                            break;    
                        case COMPLETED:
                            id = completedTab.getSelectedId();
                            if (id != null)
                                showTrackingScreen(id);
                            break;
                        case RELEASED:
                            id = releasedTab.getSelectedId();
                            if (id != null)
                                showTrackingScreen(id);
                            break;    
                        case TO_BE_VERIFIED:
                            id = toBeVerifiedTab.getSelectedId();
                            if (id != null)
                                showTrackingScreen(id);
                            break;
                        case OTHER:
                            id = otherTab.getSelectedId();
                            if (id != null)
                                showTrackingScreen(id);
                            break;    
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Window.alert(e.getMessage());
                }             
            }

            public void onStateChange(StateChangeEvent<State> event) {
                trackingButton.enable(true);
            }
        });
        
        exportToXl = (AppButton)def.getWidget("exportToExcelButton");
        addScreenHandler(exportToXl, new ScreenEventHandler<Object>() {
            public void onStateChange(StateChangeEvent<State> event) {
                exportToXl.enable(false);
            }
        });
        
        //
        // tabs
        //
        tabPanel = (TabPanel)def.getWidget("tabPanel");
        tabPanel.addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {
            public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {
                int i;

                // tab screen order should be the same as enum or this will
                // not work
                i = event.getItem().intValue();
                tab = Tabs.values()[i];
                
                drawTabs(false);                
            }
        });
        
        loggedIntab = new LoggedInTab(def, window);
        addScreenHandler(loggedIntab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                loggedIntab.reloadFromCache();
                if (tab == Tabs.LOGGED_IN)
                    drawTabs(false);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                loggedIntab.setState(event.getState());
            }
        });
        
        initiatedTab = new InitiatedTab(def, window);
        addScreenHandler(initiatedTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                initiatedTab.reloadFromCache();
                if (tab == Tabs.INITIATED)
                    drawTabs(false);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                initiatedTab.setState(event.getState());
            }
        });
        
        worksheetTab = new WorksheetTab(def, window);
        addScreenHandler(worksheetTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                worksheetTab.reloadFromCache();
                if (tab == Tabs.WORKSHEET)
                    drawTabs(false);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                worksheetTab.setState(event.getState());
            }
        });  
        
        completedTab = new CompletedTab(def, window);
        addScreenHandler(completedTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                completedTab.reloadFromCache();
                if (tab == Tabs.COMPLETED)
                    drawTabs(false);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                completedTab.setState(event.getState());
            }
        }); 
        
        releasedTab = new ReleasedTab(def, window);
        addScreenHandler(releasedTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                releasedTab.reloadFromCache();
                if (tab == Tabs.RELEASED)
                    drawTabs(false);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                releasedTab.setState(event.getState());
            }
        });
        
        toBeVerifiedTab = new ToBeVerifiedTab(def, window);
        addScreenHandler(toBeVerifiedTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                toBeVerifiedTab.reloadFromCache();
                if (tab == Tabs.TO_BE_VERIFIED)
                    drawTabs(false);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                toBeVerifiedTab.setState(event.getState());
            }
        });
        
        otherTab = new OtherTab(def, window);
        addScreenHandler(otherTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                otherTab.reloadFromCache();
                if (tab == Tabs.OTHER)
                    drawTabs(false);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                otherTab.setState(event.getState());
            }
        });             
    }
    
    protected void onAttach() {
        /*
         * When the screen is dragged around, the charts showing on the different
         * tabs get detached from the panels that they were initially attached to.
         * This makes sure that they are redrawn when dragging stops. 
         */
        super.onAttach();        
        drawTabs(true);            
    }
    
    private void drawTabs(boolean reattachChart) {
        String val;
        
        /*
         * since this method is called from onAttach(), mySection may not have been
         * initialized when that happens because onAttach() is also called when
         * the screen gets attached to the browser for the first time
         */        
        if (window == null || mySection == null)
            return;
        val = mySection.getValue();
        /*
         * This makes sure that the tabs know that the charts showing on them are
         * to be redrawn because the screen has been dragged. If a tab is not open
         * at this moment it will redraw its chart when it is opened. Calling draw()
         * on a tab after this makes the open tab to redraw its chart.
         */
        if (reattachChart) {
            loggedIntab.reattachChart();                    
            initiatedTab.reattachChart();
            completedTab.reattachChart();        
            toBeVerifiedTab.reattachChart();
        }

        switch (tab) {
            case LOGGED_IN:
                loggedIntab.draw(val);
                break;
            case INITIATED:
                initiatedTab.draw(val);                
                break;     
            case WORKSHEET:
                worksheetTab.draw(val);
                break;
            case COMPLETED:
                completedTab.draw(val);
                break;
            case RELEASED:
                releasedTab.draw(val);
                break;    
            case TO_BE_VERIFIED:
                toBeVerifiedTab.draw(val);
                break;
            case OTHER:
                otherTab.draw(val);
                break;      
        }
    }   

    private void showTrackingScreen(Integer id) throws Exception {
        SampleManager man;
        ScreenWindow modal;

        man = SampleManager.fetchById(id);
        modal = new ScreenWindow(ScreenWindow.Mode.LOOK_UP);
        modal.setName(consts.get("sampleTracking"));
        if (sampleTrackingScreen == null)
            sampleTrackingScreen = new SampleTrackingScreen(modal);

        modal.setContent(sampleTrackingScreen);
        sampleTrackingScreen.loadSample(man);
        window.clearStatus();
    }
    
    private void showCompletionScreen(Integer id) throws Exception {
        ScreenWindow modal;    
        WorksheetCompletionScreen wcScreen;
        
        modal = new ScreenWindow(ScreenWindow.Mode.LOOK_UP);
        modal.setName(consts.get("worksheetCompletion"));
        wcScreen = new WorksheetCompletionScreen(id);
        modal.setContent(wcScreen);
        window.clearStatus();
    }
}
