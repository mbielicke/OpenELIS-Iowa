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
import org.openelis.ui.widget.CheckBox;
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

public class EnvironmentalTabUI extends Screen {
    @UiTemplate("EnvironmentalTab.ui.xml")
    interface EnvironmentalTabUIBinder extends UiBinder<Widget, EnvironmentalTabUI> {
    };

    private static EnvironmentalTabUIBinder uiBinder = GWT.create(EnvironmentalTabUIBinder.class);
    
    @UiField
    protected CheckBox                      isHazardous;
    
    @UiField
    protected TextBox<Integer>              priority;

    @UiField
    protected TextBox<String>               collector, collectorPhone, location, description,
                    locationAddressMultipleUnit, locationAddressStreetAddress, locationAddressCity,
                    locationAddressZipCode;

    // @UiField
    // protected Button locationLookupButton;    

    @UiField
    protected Dropdown<String>            locationAddressState, locationAddressCountry;

    protected SampleManager1                manager;

    protected Screen                        parentScreen;

    protected EventBus                      parentBus;

    protected boolean                       canEdit, isBusy, isVisible, redraw;

    public EnvironmentalTabUI(Screen parentScreen) {
        this.parentScreen = parentScreen;
        this.parentBus = parentScreen.getEventBus();
        initWidget(uiBinder.createAndBindUi(this));
        initialize();

        manager = null;
    }

    public void initialize() {
        Item<String> row;
        ArrayList<Item<String>> model;
        
        addScreenHandler(isHazardous, SampleMeta.getEnvLocation(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                isHazardous.setValue(getIsHazardous());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                setIsHazardous(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                isHazardous.setEnabled(isState(QUERY) || (canEdit && isState(ADD, UPDATE)));
                isHazardous.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? priority : description;
            }
        });

        addScreenHandler(priority, SampleMeta.getEnvPriority(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                priority.setValue(getPriority());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                setPriority(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                priority.setEnabled(isState(QUERY) || (canEdit && isState(ADD, UPDATE)));
                priority.setQueryMode(isState(QUERY));
            }
        });

        addScreenHandler(description, SampleMeta.getEnvDescription(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                description.setValue(getDescription());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                setDescription(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                description.setEnabled(isState(QUERY) || (canEdit && isState(ADD, UPDATE)));
                description.setQueryMode(isState(QUERY));
            }
        });

        addScreenHandler(collector, SampleMeta.getEnvCollector(), new ScreenHandler<String>() {
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

        addScreenHandler(collectorPhone,
                         SampleMeta.getEnvCollectorPhone(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 collectorPhone.setValue(getCollectorPhone());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setCollectorPhone(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 collectorPhone.setEnabled(isState(QUERY) ||
                                                           (canEdit && isState(ADD, UPDATE)));
                                 collectorPhone.setQueryMode(isState(QUERY));
                             }
                         });

        addScreenHandler(location, SampleMeta.getEnvLocation(), new ScreenHandler<String>() {
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
        
        addScreenHandler(locationAddressMultipleUnit,
                         SampleMeta.getWellLocationAddrMultipleUnit(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 locationAddressMultipleUnit.setValue(getLocationAddressMultipleUnit());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setLocationAddressMultipleUnit(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 locationAddressMultipleUnit.setEnabled(isState(QUERY) ||
                                                                        (canEdit && isState(ADD,
                                                                                            UPDATE)));
                                 locationAddressMultipleUnit.setQueryMode(isState(QUERY));
                             }
                         });

        addScreenHandler(locationAddressStreetAddress,
                         SampleMeta.getWellLocationAddrStreetAddress(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 locationAddressStreetAddress.setValue(getLocationAddressStreetAddress());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setLocationAddressStreetAddress(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 locationAddressStreetAddress.setEnabled(isState(QUERY) ||
                                                                         (canEdit && isState(ADD,
                                                                                             UPDATE)));
                                 locationAddressStreetAddress.setQueryMode(isState(QUERY));
                             }
                         });

        addScreenHandler(locationAddressCity,
                         SampleMeta.getWellLocationAddrCity(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 locationAddressCity.setValue(getLocationAddressCity());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setLocationAddressCity(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 locationAddressCity.setEnabled(isState(QUERY) ||
                                                                (canEdit && isState(ADD, UPDATE)));
                                 locationAddressCity.setQueryMode(isState(QUERY));
                             }
                         });

        addScreenHandler(locationAddressState,
                         SampleMeta.getWellLocationAddrState(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 locationAddressState.setValue(getLocationAddressState());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setLocationAddressState(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 locationAddressState.setEnabled(isState(QUERY) ||
                                                                 (canEdit && isState(ADD, UPDATE)));
                                 locationAddressState.setQueryMode(isState(QUERY));
                             }
                         });

        addScreenHandler(locationAddressZipCode,
                         SampleMeta.getWellLocationAddrZipCode(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 locationAddressZipCode.setValue(getLocationAddressZipCode());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setLocationAddressZipCode(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 locationAddressZipCode.setEnabled(isState(QUERY) ||
                                                                   (canEdit && isState(ADD, UPDATE)));
                                 locationAddressZipCode.setQueryMode(isState(QUERY));
                             }
                         });
        
        addScreenHandler(locationAddressCountry,
                         SampleMeta.getWellLocationAddrState(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 locationAddressCountry.setValue(getLocationAddressCountry());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setLocationAddressCountry(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 locationAddressCountry.setEnabled(isState(QUERY) ||
                                                                 (canEdit && isState(ADD, UPDATE)));
                                 locationAddressCountry.setQueryMode(isState(QUERY));
                             }
                         });

        // addScreenHandler(locationLookupButton, "locationLookupButton", new
        // ScreenHandler<Object>() {
        // public void onStateChange(StateChangeEvent event) {
        // locationLookupButton.setEnabled(isState(DISPLAY) ||
        // (canEdit && isState(ADD, UPDATE)));
        // }
        // });

        addVisibleHandler(new VisibleEvent.Handler() {
            public void onVisibleOrInvisible(VisibleEvent event) {
                isVisible = event.isVisible();
                displaySampleEnvironmental();
            }
        });
        
        model = new ArrayList<Item<String>>();
        for (DictionaryDO d : CategoryCache.getBySystemName("state")) {
            row = new Item<String>(d.getEntry(), d.getEntry());
            row.setEnabled("Y".equals(d.getIsActive()));
            model.add(row);
        }
        locationAddressState.setModel(model);
        
        model = new ArrayList<Item<String>>();
        for (DictionaryDO d : CategoryCache.getBySystemName("country")) {
            row = new Item<String>(d.getEntry(), d.getEntry());
            row.setEnabled("Y".equals(d.getIsActive()));
            model.add(row);
        }
        locationAddressCountry.setModel(model);
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
        displaySampleEnvironmental();
    }

    private void displaySampleEnvironmental() {
        if ( !isVisible)
            return;

        if (redraw) {
            /*
             * don't redraw unless the data has changed
             */
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

    /*
     * getters and setters for the fields at the domain level
     */

    /**
     * returns is hazardous or null if the manager is null or if this is not an
     * environmental sample
     */
    private String getIsHazardous() {
        if (manager == null || manager.getSampleEnvironmental() == null)
            return null;
        return manager.getSampleEnvironmental().getIsHazardous();
    }

    /**
     * sets is hazardous
     */
    private void setIsHazardous(String isHazardous) {
        manager.getSampleEnvironmental().setIsHazardous(isHazardous);
    }

    /**
     * returns the priority or null if the manager is null or if this is not an
     * environmental sample
     */
    private Integer getPriority() {
        if (manager == null || manager.getSampleEnvironmental() == null)
            return null;
        return manager.getSampleEnvironmental().getPriority();
    }

    /**
     * sets the priority
     */
    private void setPriority(Integer priority) {
        manager.getSampleEnvironmental().setPriority(priority);
    }

    /**
     * returns the collector or null if the manager is null or if this is not an
     * environmental sample
     */
    private String getCollector() {
        if (manager == null || manager.getSampleEnvironmental() == null)
            return null;
        return manager.getSampleEnvironmental().getCollector();
    }

    /**
     * sets the priority
     */
    private void setCollector(String collector) {
        manager.getSampleEnvironmental().setCollector(collector);
    }

    /**
     * returns the collector phone or null if the manager is null or if this is
     * not an environmental sample
     */
    private String getCollectorPhone() {
        if (manager == null || manager.getSampleEnvironmental() == null)
            return null;
        return manager.getSampleEnvironmental().getCollectorPhone();
    }

    /**
     * sets the collector phone
     */
    private void setCollectorPhone(String collectorPhone) {
        manager.getSampleEnvironmental().setCollectorPhone(collectorPhone);
    }

    /**
     * returns the location or null if the manager is null or if this is not an
     * environmental sample
     */
    private String getLocation() {
        if (manager == null || manager.getSampleEnvironmental() == null)
            return null;
        return manager.getSampleEnvironmental().getLocation();
    }

    /**
     * sets the location
     */
    private void setLocation(String location) {
        manager.getSampleEnvironmental().setLocation(location);
    }

    /**
     * returns the description or null if the manager is null or if this is not
     * an environmental sample
     */
    private String getDescription() {
        if (manager == null || manager.getSampleEnvironmental() == null)
            return null;
        return manager.getSampleEnvironmental().getDescription();
    }

    /**
     * sets the description
     */
    private void setDescription(String description) {
        manager.getSampleEnvironmental().setDescription(description);
    }
    
    /**
     * returns the location's multiple unit (apt/suite) or null if the manager
     * is null or if this is not an environmental sample
     */
    private String getLocationAddressMultipleUnit() {
        if (manager == null || manager.getSampleEnvironmental() == null)
            return null;
        return manager.getSampleEnvironmental().getLocationAddress().getMultipleUnit();
    }

    /**
     * sets the location's multiple unit (apt/suite)
     */
    private void setLocationAddressMultipleUnit(String multipleUnit) {
        manager.getSampleEnvironmental().getLocationAddress().setMultipleUnit(multipleUnit);

    }

    /**
     * returns the location's street address or null if the manager is null or
     * if this is not an environmental sample
     */
    private String getLocationAddressStreetAddress() {
        if (manager == null || manager.getSampleEnvironmental() == null)
            return null;
        return manager.getSampleEnvironmental().getLocationAddress().getStreetAddress();
    }

    /**
     * sets the location's street address
     */
    private void setLocationAddressStreetAddress(String streetAddress) {
        manager.getSampleEnvironmental().getLocationAddress().setStreetAddress(streetAddress);

    }

    /**
     * returns the location's city or null if the manager is null or if this is
     * not an environmental sample
     */
    private String getLocationAddressCity() {
        if (manager == null || manager.getSampleEnvironmental() == null)
            return null;
        return manager.getSampleEnvironmental().getLocationAddress().getCity();
    }

    /**
     * sets the location's city
     */
    private void setLocationAddressCity(String city) {
        manager.getSampleEnvironmental().getLocationAddress().setCity(city);
    }

    /**
     * returns the location's state or null if the manager is null or if this is
     * not an environmental sample
     */
    private String getLocationAddressState() {
        if (manager == null || manager.getSampleEnvironmental() == null)
            return null;
        return manager.getSampleEnvironmental().getLocationAddress().getState();
    }

    /**
     * sets the location's state
     */
    private void setLocationAddressState(String state) {
        manager.getSampleEnvironmental().getLocationAddress().setState(state);

    }

    /**
     * returns the location's zip code or null if the manager is null or if this
     * is not an environmental sample
     */
    private String getLocationAddressZipCode() {
        if (manager == null || manager.getSampleEnvironmental() == null)
            return null;
        return manager.getSampleEnvironmental().getLocationAddress().getZipCode();
    }

    /**
     * sets the location's zip code
     */
    private void setLocationAddressZipCode(String zipCode) {
        manager.getSampleEnvironmental().getLocationAddress().setZipCode(zipCode);
    }
    
    /**
     * returns the location's country or null if the manager is null or if this
     * is not an environmental sample
     */
    private String getLocationAddressCountry() {
        if (manager == null || manager.getSampleEnvironmental() == null)
            return null;
        return manager.getSampleEnvironmental().getLocationAddress().getCountry();
    }

    /**
     * sets the location's country
     */
    private void setLocationAddressCountry(String country) {
        manager.getSampleEnvironmental().getLocationAddress().setCountry(country);
    }
}