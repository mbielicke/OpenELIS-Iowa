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

import static org.openelis.modules.main.client.Logger.*;
import static org.openelis.ui.screen.State.*;

import java.util.ArrayList;
import java.util.logging.Level;

import org.openelis.cache.CategoryCache;
import org.openelis.constants.Messages;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.PWSDO;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.manager.SampleManager1;
import org.openelis.meta.SampleMeta;
import org.openelis.modules.pws.client.PWSScreen;
import org.openelis.modules.pws.client.PWSService;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.TextBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.VisibleEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

public class SDWISTabUI extends Screen {
    @UiTemplate("SDWISTab.ui.xml")
    interface SDWISTabUIBinder extends UiBinder<Widget, SDWISTabUI> {
    };

    private static SDWISTabUIBinder uiBinder = GWT.create(SDWISTabUIBinder.class);

    @UiField
    protected TextBox<String>       sdwisPwsNumber0, sdwisPwsName, sdwisFacilityId,
                    sdwisSamplePointId, sdwisLocation, sdwisCollector;

    @UiField
    protected Dropdown<Integer>     sdwisSampleTypeId, sdwisSampleCategoryId;

    @UiField
    protected TextBox<Integer>      sdwisPriority, sdwisStateLabId;

    @UiField
    protected Button                pwsLookupButton;

    protected SampleManager1        manager;

    protected Screen                parentScreen;

    protected SDWISTabUI            screen;

    protected EventBus              parentBus;

    protected boolean               canEdit, isVisible, redraw, canQuery;

    public SDWISTabUI(Screen parentScreen) {
        this.parentScreen = parentScreen;
        this.parentBus = parentScreen.getEventBus();
        initWidget(uiBinder.createAndBindUi(this));
        initialize();

        manager = null;
    }

    public void initialize() {
        Item<Integer> row;
        ArrayList<Item<Integer>> model;

        screen = this;

        addScreenHandler(sdwisPwsNumber0,
                         SampleMeta.getSDWISPwsNumber0(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 sdwisPwsNumber0.setValue(getPwsNumber0());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setPwsNumber0(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 sdwisPwsNumber0.setEnabled( (isState(QUERY) && canQuery) ||
                                                            (canEdit && isState(ADD, UPDATE)));
                                 sdwisPwsNumber0.setQueryMode( (isState(QUERY) && canQuery));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? sdwisStateLabId : sdwisCollector;
                             }
                         });

        addScreenHandler(pwsLookupButton, "pwsLookupButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                pwsLookupButton.setEnabled(isState(DISPLAY) || (canEdit && isState(ADD, UPDATE)));
            }
        });

        addScreenHandler(sdwisPwsName, "sdwisPwsName", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                sdwisPwsName.setValue(getPwsName());
            }

            public void onStateChange(StateChangeEvent event) {
                sdwisPwsName.setEnabled(false);
            }

            public Object getQuery() {
                /*
                 * since this field is not set in query mode, the value doesn't
                 * get cleared when StateChangeEvent is fired; it also doesn't
                 * get cleared if the tab is not visible, because
                 * DataChangeEvent is not fired in that case; this makes sure
                 * that the value doesn't get included in the query sent to the
                 * back-end
                 */
                return null;
            }
        });

        addScreenHandler(sdwisStateLabId,
                         SampleMeta.getSDWISStateLabId(),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent event) {
                                 sdwisStateLabId.setValue(getStateLabId());
                             }

                             public void onValueChange(ValueChangeEvent<Integer> event) {
                                 setStateLabId(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 sdwisStateLabId.setEnabled( (isState(QUERY) && canQuery) ||
                                                            (canEdit && isState(ADD, UPDATE)));
                                 sdwisStateLabId.setQueryMode( (isState(QUERY) && canQuery));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? sdwisFacilityId : sdwisPwsNumber0;
                             }
                         });

        addScreenHandler(sdwisFacilityId,
                         SampleMeta.getSDWISFacilityId(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 sdwisFacilityId.setValue(getFacilityId());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setFacilityId(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 sdwisFacilityId.setEnabled( (isState(QUERY) && canQuery) ||
                                                            (canEdit && isState(ADD, UPDATE)));
                                 sdwisFacilityId.setQueryMode( (isState(QUERY) && canQuery));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? sdwisSampleTypeId : sdwisStateLabId;
                             }
                         });

        addScreenHandler(sdwisSampleTypeId,
                         SampleMeta.getSDWISSampleTypeId(),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent event) {
                                 sdwisSampleTypeId.setValue(getSampleTypeId());
                             }

                             public void onValueChange(ValueChangeEvent<Integer> event) {
                                 setSampleTypeId(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 sdwisSampleTypeId.setEnabled( (isState(QUERY) && canQuery) ||
                                                              (canEdit && isState(ADD, UPDATE)));
                                 sdwisSampleTypeId.setQueryMode( (isState(QUERY) && canQuery));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? sdwisSampleCategoryId : sdwisFacilityId;
                             }
                         });

        addScreenHandler(sdwisSampleCategoryId,
                         SampleMeta.getSDWISSampleCategoryId(),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent event) {
                                 sdwisSampleCategoryId.setValue(getSampleCategoryId());
                             }

                             public void onValueChange(ValueChangeEvent<Integer> event) {
                                 setSampleCategoryId(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 sdwisSampleCategoryId.setEnabled( (isState(QUERY) && canQuery) ||
                                                                  (canEdit && isState(ADD, UPDATE)));
                                 sdwisSampleCategoryId.setQueryMode( (isState(QUERY) && canQuery));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? sdwisSamplePointId : sdwisSampleTypeId;
                             }
                         });

        addScreenHandler(sdwisSamplePointId,
                         SampleMeta.getSDWISSamplePointId(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 sdwisSamplePointId.setValue(getSamplePointId());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setSamplePointId(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 sdwisSamplePointId.setEnabled( (isState(QUERY) && canQuery) ||
                                                               (canEdit && isState(ADD, UPDATE)));
                                 sdwisSamplePointId.setQueryMode( (isState(QUERY) && canQuery));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? sdwisLocation : sdwisSampleCategoryId;
                             }
                         });

        addScreenHandler(sdwisLocation, SampleMeta.getSDWISLocation(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                sdwisLocation.setValue(getLocation());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                setLocation(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                sdwisLocation.setEnabled( (isState(QUERY) && canQuery) ||
                                         (canEdit && isState(ADD, UPDATE)));
                sdwisLocation.setQueryMode( (isState(QUERY) && canQuery));
            }

            public Widget onTab(boolean forward) {
                return forward ? sdwisPriority : sdwisSamplePointId;
            }
        });

        addScreenHandler(sdwisPriority,
                         SampleMeta.getSDWISPriority(),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent event) {
                                 sdwisPriority.setValue(getPriority());
                             }

                             public void onValueChange(ValueChangeEvent<Integer> event) {
                                 setPriority(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 sdwisPriority.setEnabled((isState(QUERY) && canQuery) ||
                                                          (canEdit && isState(ADD, UPDATE)));
                                 sdwisPriority.setQueryMode(isState(QUERY) && canQuery);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? sdwisCollector : sdwisLocation;
                             }
                         });

        addScreenHandler(sdwisCollector,
                         SampleMeta.getSDWISCollector(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 sdwisCollector.setValue(getCollector());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setCollector(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 sdwisCollector.setEnabled( (isState(QUERY) && canQuery) ||
                                                           (canEdit && isState(ADD, UPDATE)));
                                 sdwisCollector.setQueryMode( (isState(QUERY) && canQuery));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? sdwisPwsNumber0 : sdwisPriority;
                             }
                         });

        addVisibleHandler(new VisibleEvent.Handler() {
            public void onVisibleOrInvisible(VisibleEvent event) {
                isVisible = event.isVisible();
                displaySampleSDWIS();
            }
        });

        model = new ArrayList<Item<Integer>>();
        for (DictionaryDO d : CategoryCache.getBySystemName("sdwis_sample_type")) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled("Y".equals(d.getIsActive()));
            model.add(row);
        }
        sdwisSampleTypeId.setModel(model);

        model = new ArrayList<Item<Integer>>();
        for (DictionaryDO d : CategoryCache.getBySystemName("sdwis_sample_category")) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled("Y".equals(d.getIsActive()));
            model.add(row);
        }
        sdwisSampleCategoryId.setModel(model);
    }

    public void setData(SampleManager1 manager) {
        this.manager = manager;
    }

    public void setState(State state) {
        evaluateEdit();
        this.state = state;
        bus.fireEventFromSource(new StateChangeEvent(state), this);
    }

    public void setCanQuery(boolean canQuery) {
        this.canQuery = canQuery;
    }

    /**
     * notifies the tab that it may need to refresh the display in its widgets;
     * if the data currently showing in the widgets is the same as the data in
     * the latest manager then the widgets are not refreshed
     */
    public void onDataChange() {
        redraw = true;
        displaySampleSDWIS();
    }

    @UiHandler("pwsLookupButton")
    protected void pwsLookup(ClickEvent event) {
        String number0;
        PWSScreen pwsScreen;
        ScreenWindow modal;

        try {
            modal = new ScreenWindow(ScreenWindow.Mode.LOOK_UP);
            modal.setName(Messages.get().pwsInformation());
            /*
             * make sure that the number0 passed to PWS screen is not null
             * otherwise it won't show the "Select" button
             */
            number0 = getPwsNumber0() != null ? getPwsNumber0() : "";
            pwsScreen = new PWSScreen(number0, modal);
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
            return;
        }

        pwsScreen.addActionHandler(new ActionHandler<PWSScreen.Action>() {
            public void onAction(ActionEvent<PWSScreen.Action> event) {
                PWSDO data;

                if (isState(ADD, State.UPDATE) && event.getAction() == PWSScreen.Action.SELECT) {
                    data = (PWSDO)event.getData();
                    setPwsId(data.getId());
                    setPwsNumber0(data.getNumber0());
                    setPwsName(data.getName());

                    sdwisPwsNumber0.clearExceptions();
                    sdwisPwsNumber0.setValue(getPwsNumber0());
                    sdwisPwsName.setValue(getPwsName());
                    sdwisPwsNumber0.setFocus(true);
                }
            }
        });

        modal.setContent(pwsScreen);
        pwsScreen.initialize();
    }

    private void displaySampleSDWIS() {
        if ( !isVisible)
            return;

        if (redraw) {
            redraw = false;
            fireDataChange();
        }
    }

    /**
     * determines if the fields on the screen can be edited based on the data
     */
    private void evaluateEdit() {
        canEdit = (manager != null && !Constants.dictionary().SAMPLE_RELEASED.equals(manager.getSample()
                                                                                            .getStatusId()));
    }

    /**
     * sets the pws id
     */
    private void setPwsId(Integer pwsId) {
        manager.getSampleSDWIS().setPwsId(pwsId);
    }

    /**
     * returns the pws number0 or null if the manager is null or if this is not
     * a SDWIS sample
     */
    private String getPwsNumber0() {
        if (manager == null || manager.getSampleSDWIS() == null)
            return null;
        return manager.getSampleSDWIS().getPwsNumber0();
    }

    /**
     * if there is a PWS with the passed value as its number0, then sets it as
     * the sample's PWS; otherwise, blanks the sample's PWS fields
     */
    private void setPwsNumber0(String number0) {
        PWSDO data;

        data = null;
        if ( !DataBaseUtil.isEmpty(number0)) {
            try {
                data = PWSService.get().fetchPwsByNumber0(number0);
            } catch (ValidationErrorsList e) {
                showErrors(e);
            } catch (NotFoundException e) {
                // ignore
            } catch (Exception e) {
                Window.alert(e.getMessage());
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        }

        if (data != null) {
            setPwsId(data.getId());
            setPwsName(data.getName());
            manager.getSampleSDWIS().setPwsNumber0(data.getNumber0());
            sdwisPwsName.setValue(data.getName());
        } else {
            setPwsId(null);
            setPwsName(null);
            manager.getSampleSDWIS().setPwsNumber0(null);
            sdwisPwsNumber0.setValue(null);
            sdwisPwsName.setValue(null);
        }
    }

    /**
     * returns the pws name or null if the manager is null or if this is not a
     * SDWIS sample
     */
    private String getPwsName() {
        if (manager == null || manager.getSampleSDWIS() == null)
            return null;
        return manager.getSampleSDWIS().getPwsName();
    }

    /**
     * sets the pws name
     */
    private void setPwsName(String pwsName) {
        manager.getSampleSDWIS().setPwsName(pwsName);
    }

    /**
     * returns the state lab id or null if the manager is null or if this is not
     * a SDWIS sample
     */
    private Integer getStateLabId() {
        if (manager == null || manager.getSampleSDWIS() == null)
            return null;
        return manager.getSampleSDWIS().getStateLabId();
    }

    /**
     * sets the state lab id
     */
    private void setStateLabId(Integer stateLabId) {
        manager.getSampleSDWIS().setStateLabId(stateLabId);
    }

    /**
     * returns the facility id or null if the manager is null or if this is not
     * a SDWIS sample
     */
    private String getFacilityId() {
        if (manager == null || manager.getSampleSDWIS() == null)
            return null;
        return manager.getSampleSDWIS().getFacilityId();
    }

    /**
     * sets the facility id
     */
    private void setFacilityId(String facilityId) {
        manager.getSampleSDWIS().setFacilityId(facilityId);
    }

    /**
     * returns the sample type id or null if the manager is null or if this is
     * not a SDWIS sample
     */
    private Integer getSampleTypeId() {
        if (manager == null || manager.getSampleSDWIS() == null)
            return null;
        return manager.getSampleSDWIS().getSampleTypeId();
    }

    /**
     * sets the sample type id
     */
    private void setSampleTypeId(Integer sampleTypeId) {
        manager.getSampleSDWIS().setSampleTypeId(sampleTypeId);
    }

    /**
     * returns the sample category id or null if the manager is null or if this
     * is not a SDWIS sample
     */
    private Integer getSampleCategoryId() {
        if (manager == null || manager.getSampleSDWIS() == null)
            return null;
        return manager.getSampleSDWIS().getSampleCategoryId();
    }

    /**
     * sets the sample category id
     */
    private void setSampleCategoryId(Integer sampleCategoryId) {
        manager.getSampleSDWIS().setSampleCategoryId(sampleCategoryId);

    }

    /**
     * returns the sample point id or null if the manager is null or if this is
     * not a SDWIS sample
     */
    private String getSamplePointId() {
        if (manager == null || manager.getSampleSDWIS() == null)
            return null;
        return manager.getSampleSDWIS().getSamplePointId();
    }

    /**
     * sets the sample point id
     */
    private void setSamplePointId(String samplePointId) {
        manager.getSampleSDWIS().setSamplePointId(samplePointId);
    }

    /**
     * returns the priority or null if the manager is null or if this is not an
     * sdwis sample
     */
    private Integer getPriority() {
        if (manager == null || manager.getSampleSDWIS() == null)
            return null;
        return manager.getSampleSDWIS().getPriority();
    }

    /**
     * sets the priority
     */
    private void setPriority(Integer priority) {
        manager.getSampleSDWIS().setPriority(priority);
    }

    /**
     * returns the location or null if the manager is null or if this is not a
     * SDWIS sample
     */
    private String getLocation() {
        if (manager == null || manager.getSampleSDWIS() == null)
            return null;
        return manager.getSampleSDWIS().getLocation();
    }

    /**
     * sets the location
     */
    private void setLocation(String location) {
        manager.getSampleSDWIS().setLocation(location);
    }

    /**
     * returns the collector or null if the manager is null or if this is not a
     * SDWIS sample
     */
    private String getCollector() {
        if (manager == null || manager.getSampleSDWIS() == null)
            return null;
        return manager.getSampleSDWIS().getCollector();
    }

    /**
     * sets the collector
     */
    private void setCollector(String collector) {
        manager.getSampleSDWIS().setCollector(collector);
    }
}