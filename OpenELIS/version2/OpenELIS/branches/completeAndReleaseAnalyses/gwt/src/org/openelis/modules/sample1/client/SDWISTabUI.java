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

import static org.openelis.ui.screen.State.*;

import java.util.ArrayList;

import org.openelis.cache.CategoryCache;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.manager.SampleManager1;
import org.openelis.meta.SampleMeta;
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
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.VisibleEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;

public class SDWISTabUI extends Screen {
    @UiTemplate("SDWISTab.ui.xml")
    interface SDWISTabUIBinder extends UiBinder<Widget, SDWISTabUI> {
    };

    private static SDWISTabUIBinder uiBinder = GWT.create(SDWISTabUIBinder.class);

    @UiField
    protected Dropdown<Integer>     sampleType, sampleCategory;

    @UiField
    protected TextBox<String>       pwsNumber0, pwsName, facilityId, samplePointId, location,
                    collector;

    @UiField
    protected TextBox<Integer>      stateLabId;

    @UiField
    protected Button                pwsLookupButton;

    protected SampleManager1        manager;

    protected Screen                parentScreen;

    protected EventBus              parentBus;

    protected boolean              canEdit, isBusy, isVisible, redraw;

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

        addScreenHandler(pwsNumber0, SampleMeta.getSDWISPwsNumber0(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                pwsNumber0.setValue(getPwsNumber0());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                // boolean clearValue;
                // PWSDO data;
                //
                // clearValue = false;
                // if (!DataBaseUtil.isEmpty(event.getValue())) {
                // try {
                // data = PWSService.get().fetchPwsByNumber0(event.getValue());
                // getSDWISManager().getSDWIS().setPwsId(data.getId());
                // getSDWISManager().getSDWIS().setPwsName(data.getName());
                // getSDWISManager().getSDWIS().setPwsNumber0(data.getNumber0());
                // pwsName.setValue(data.getName());
                // } catch (ValidationErrorsList e) {
                // showErrors(e);
                // clearValue = true;
                // } catch (NotFoundException e) {
                // clearValue = true;
                // }catch (Exception e) {
                // Window.alert("pwsId valueChange: " + e.getMessage());
                // }
                // } else {
                // clearValue = true;
                // }
                //
                // if (clearValue) {
                // getSDWISManager().getSDWIS().setPwsId(null);
                // getSDWISManager().getSDWIS().setPwsName(null);
                // getSDWISManager().getSDWIS().setPwsNumber0(null);
                // pwsId.setValue(null);
                // pwsName.setValue(null);
                // }

            }

            public void onStateChange(StateChangeEvent event) {
                pwsNumber0.setEnabled(isState(QUERY) || (canEdit && isState(ADD, UPDATE)));
                pwsNumber0.setQueryMode(isState(QUERY));
            }
        });

        addScreenHandler(pwsLookupButton, "pwsLookupButton", new ScreenHandler<Object>() {

            public void onStateChange(StateChangeEvent event) {
                pwsLookupButton.setEnabled(isState(DISPLAY) || (canEdit && isState(ADD, UPDATE)));
            }
        });

        addScreenHandler(pwsName, "pwsName", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                pwsName.setValue(getPwsName());
            }

            public void onStateChange(StateChangeEvent event) {
                pwsName.setEnabled(false);
                pwsName.setQueryMode(isState(QUERY));
            }
        });

        addScreenHandler(stateLabId, SampleMeta.getSDWISStateLabId(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                stateLabId.setValue(getStateLabId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                setStateLabId(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                stateLabId.setEnabled(isState(QUERY) || (canEdit && isState(ADD, UPDATE)));
                stateLabId.setQueryMode(isState(QUERY));
            }
        });

        addScreenHandler(facilityId, SampleMeta.getSDWISFacilityId(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                facilityId.setValue(getFacilityId());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                setFacilityId(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                facilityId.setEnabled(isState(QUERY) || (canEdit && isState(ADD, UPDATE)));
                facilityId.setQueryMode(isState(QUERY));
            }
        });

        addScreenHandler(sampleType,
                         SampleMeta.getSDWISSampleTypeId(),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent event) {
                                 sampleType.setValue(getSampleTypeId());
                             }

                             public void onValueChange(ValueChangeEvent<Integer> event) {
                                 setSampleTypeId(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 sampleType.setEnabled(isState(QUERY) ||
                                                       (canEdit && isState(ADD, UPDATE)));
                                 sampleType.setQueryMode(isState(QUERY));
                             }
                         });

        addScreenHandler(sampleCategory,
                         SampleMeta.getSDWISSampleCategoryId(),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent event) {
                                 sampleCategory.setValue(getSampleCategoryId());
                             }

                             public void onValueChange(ValueChangeEvent<Integer> event) {
                                 setSampleCategoryId(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 sampleCategory.setEnabled(isState(QUERY) ||
                                                           (canEdit && isState(ADD, UPDATE)));
                                 sampleCategory.setQueryMode(isState(QUERY));
                             }
                         });

        addScreenHandler(samplePointId,
                         SampleMeta.getSDWISSamplePointId(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 samplePointId.setValue(getSamplePointId());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setSamplePointId(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 samplePointId.setEnabled(isState(QUERY) ||
                                                          (canEdit && isState(ADD, UPDATE)));
                                 samplePointId.setQueryMode(isState(QUERY));
                             }
                         });

        addScreenHandler(location, SampleMeta.getSDWISLocation(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                location.setValue(getLocation());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                setLocation(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                location.setEnabled(isState(QUERY) || (canEdit && isState(ADD, UPDATE)));
                location.setQueryMode(isState(QUERY));
            }
        });

        addScreenHandler(collector, SampleMeta.getSDWISCollector(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                collector.setValue(getCollector());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                setCollector(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                collector.setEnabled(isState(QUERY) || (canEdit && isState(ADD, UPDATE)));
                collector.setQueryMode(isState(QUERY));
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
        sampleType.setModel(model);

        model = new ArrayList<Item<Integer>>();
        for (DictionaryDO d : CategoryCache.getBySystemName("sdwis_sample_category")) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled("Y".equals(d.getIsActive()));
            model.add(row);
        }
        sampleCategory.setModel(model);
    }

    public void setData(SampleManager1 manager) {
        this.manager = manager;
    }

    public void setState(State state) {
        evaluateEdit();
        this.state = state;
        bus.fireEventFromSource(new StateChangeEvent(state), this);
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

    private void openPwsScreen() {
        // PWSScreen pwsScreen;
        // ScreenWindow modal;
        //
        // try {
        // modal = new ScreenWindow(ScreenWindow.Mode.LOOK_UP);
        // modal.setName(Messages.get().pwsInformation());
        //
        // pwsScreen = new PWSScreen(pwsId.getValue(),modal);
        //
        // pwsScreen.addActionHandler(new ActionHandler<PWSScreen.Action>() {
        // public void onAction(ActionEvent<PWSScreen.Action> event) {
        // PWSDO pwsDO;
        // if (state == State.ADD || state == State.UPDATE) {
        // if (event.getAction() == PWSScreen.Action.SELECT) {
        // pwsDO = (PWSDO)event.getData();
        // getSDWISManager().getSDWIS().setPwsId(pwsDO.getId());
        // getSDWISManager().getSDWIS().setPwsName(pwsDO.getName());
        // getSDWISManager().getSDWIS().setPwsNumber0(pwsDO.getNumber0());
        //
        // pwsId.clearExceptions();
        // DataChangeEvent.fire(screen);
        // setFocus(pwsId);
        //
        // }
        // }
        // }
        // });
        //
        // modal.setContent(pwsScreen);
        // pwsScreen.initialize();
        // } catch (Exception e) {
        // Window.alert("openPWSScreen: " + e.getMessage());
        // }
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
     * returns the pws name or null if the manager is null or if this is not a
     * SDWIS sample
     */
    private String getPwsName() {
        if (manager == null || manager.getSampleSDWIS() == null)
            return null;
        return manager.getSampleSDWIS().getPwsName();
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
     * returns the location or null if the manager is null or if this is not
     * a SDWIS sample
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
     * returns the collector or null if the manager is null or if this is not
     * a SDWIS sample
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