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

public class PrivateWellTabUI extends Screen {
    @UiTemplate("PrivateWellTab.ui.xml")
    interface PrivateWellTabUIBinder extends UiBinder<Widget, PrivateWellTabUI> {
    };

    private static PrivateWellTabUIBinder uiBinder = GWT.create(PrivateWellTabUIBinder.class);

    @UiField
    protected TextBox<String>             location, locationAddressMultipleUnit,
                    locationAddressStreetAddress, locationAddressCity, locationAddressZipCode,
                    wellOwner, collector;
    @UiField
    protected TextBox<Integer>            wellNumber;

    @UiField
    protected Dropdown<String>            locationAddressState;

    protected SampleManager1              manager;

    protected Screen                      parentScreen;

    protected EventBus                    parentBus;

    protected boolean                     canEdit, isBusy, isVisible, redraw, canQuery;

    public PrivateWellTabUI(Screen parentScreen) {
        this.parentScreen = parentScreen;
        this.parentBus = parentScreen.getEventBus();
        initWidget(uiBinder.createAndBindUi(this));
        initialize();

        manager = null;
    }

    public void initialize() {
        Item<String> row;
        ArrayList<Item<String>> model;
        
        addScreenHandler(location, SampleMeta.getWellLocation(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                location.setValue(getLocation());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                setLocation(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                location.setEnabled((isState(QUERY) && canQuery) || (canEdit && isState(ADD, UPDATE)));
                location.setQueryMode(isState(QUERY) && canQuery);
            }
            
            public Widget onTab(boolean forward) {
                return forward ? locationAddressMultipleUnit : wellNumber;
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
                                 locationAddressMultipleUnit.setEnabled((isState(QUERY) && canQuery) ||
                                                                        (canEdit && isState(ADD,
                                                                                            UPDATE)));
                                 locationAddressMultipleUnit.setQueryMode((isState(QUERY) && canQuery));
                             }
                             
                             public Widget onTab(boolean forward) {
                                 return forward ? locationAddressStreetAddress : location;
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
                                 locationAddressStreetAddress.setEnabled((isState(QUERY) && canQuery) ||
                                                                         (canEdit && isState(ADD,
                                                                                             UPDATE)));
                                 locationAddressStreetAddress.setQueryMode((isState(QUERY) && canQuery));
                             }
                             
                             public Widget onTab(boolean forward) {
                                 return forward ? locationAddressCity : locationAddressMultipleUnit;
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
                                 locationAddressCity.setEnabled((isState(QUERY) && canQuery) ||
                                                                (canEdit && isState(ADD, UPDATE)));
                                 locationAddressCity.setQueryMode((isState(QUERY) && canQuery));
                             }
                             
                             public Widget onTab(boolean forward) {
                                 return forward ? locationAddressState : locationAddressStreetAddress;
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
                                 locationAddressState.setEnabled((isState(QUERY) && canQuery) ||
                                                                 (canEdit && isState(ADD, UPDATE)));
                                 locationAddressState.setQueryMode((isState(QUERY) && canQuery));
                             }
                             
                             public Widget onTab(boolean forward) {
                                 return forward ? locationAddressZipCode : locationAddressCity;
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
                                 locationAddressZipCode.setEnabled((isState(QUERY) && canQuery) ||
                                                                   (canEdit && isState(ADD, UPDATE)));
                                 locationAddressZipCode.setQueryMode((isState(QUERY) && canQuery));
                             }
                             
                             public Widget onTab(boolean forward) {
                                 return forward ? wellOwner : locationAddressState;
                             }
                         });
        
        addScreenHandler(wellOwner, SampleMeta.getWellOwner(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                wellOwner.setValue(getOwner());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                setOwner(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                wellOwner.setEnabled((isState(QUERY) && canQuery) || (canEdit && isState(ADD, UPDATE)));
                wellOwner.setQueryMode((isState(QUERY) && canQuery));
            }
            
            public Widget onTab(boolean forward) {
                return forward ? collector : locationAddressZipCode;
            }
        });

        addScreenHandler(collector, SampleMeta.getWellCollector(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                collector.setValue(getCollector());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                setCollector(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                collector.setEnabled((isState(QUERY) && canQuery) || (canEdit && isState(ADD, UPDATE)));
                collector.setQueryMode((isState(QUERY) && canQuery));
            }
            
            public Widget onTab(boolean forward) {
                return forward ? wellNumber : wellOwner;
            }
        });

        addScreenHandler(wellNumber, SampleMeta.getWellWellNumber(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                wellNumber.setValue(getWellNumber());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                setWellNumber(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                wellNumber.setEnabled((isState(QUERY) && canQuery) || (canEdit && isState(ADD, UPDATE)));
                wellNumber.setQueryMode((isState(QUERY) && canQuery));
            }
            
            public Widget onTab(boolean forward) {
                return forward ? location : collector;
            }
        });
        
        addVisibleHandler(new VisibleEvent.Handler() {
            public void onVisibleOrInvisible(VisibleEvent event) {
                isVisible = event.isVisible();
                displaySamplePrivateWell();
            }
        });
        
        model = new ArrayList<Item<String>>();
        for (DictionaryDO d : CategoryCache.getBySystemName("state")) {
            row = new Item<String>(d.getEntry(), d.getEntry());
            row.setEnabled("Y".equals(d.getIsActive()));
            model.add(row);
        }
        locationAddressState.setModel(model);
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
        displaySamplePrivateWell();
    }

    private void displaySamplePrivateWell() {
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
     * returns the location or null if the manager is null or if this is not a
     * private well sample
     */
    private String getLocation() {
        if (manager == null || manager.getSamplePrivateWell() == null)
            return null;
        return manager.getSamplePrivateWell().getLocation();
    }

    /**
     * sets the location
     */
    private void setLocation(String location) {
        manager.getSamplePrivateWell().setLocation(location);
    }

    /**
     * returns the location's multiple unit (apt/suite) or null if the manager
     * is null or if this is not a private well sample
     */
    private String getLocationAddressMultipleUnit() {
        if (manager == null || manager.getSamplePrivateWell() == null)
            return null;
        return manager.getSamplePrivateWell().getLocationAddress().getMultipleUnit();
    }

    /**
     * sets the location's multiple unit (apt/suite)
     */
    private void setLocationAddressMultipleUnit(String multipleUnit) {
        manager.getSamplePrivateWell().getLocationAddress().setMultipleUnit(multipleUnit);

    }

    /**
     * returns the location's street address or null if the manager is null or
     * if this is not a private well sample
     */
    private String getLocationAddressStreetAddress() {
        if (manager == null || manager.getSamplePrivateWell() == null)
            return null;
        return manager.getSamplePrivateWell().getLocationAddress().getStreetAddress();
    }

    /**
     * sets the location's street address
     */
    private void setLocationAddressStreetAddress(String streetAddress) {
        manager.getSamplePrivateWell().getLocationAddress().setStreetAddress(streetAddress);

    }

    /**
     * returns the location's city or null if the manager is null or if this is
     * not a private well sample
     */
    private String getLocationAddressCity() {
        if (manager == null || manager.getSamplePrivateWell() == null)
            return null;
        return manager.getSamplePrivateWell().getLocationAddress().getCity();
    }

    /**
     * sets the location's city
     */
    private void setLocationAddressCity(String city) {
        manager.getSamplePrivateWell().getLocationAddress().setCity(city);
    }

    /**
     * returns the location's state or null if the manager is null or if this is
     * not a private well sample
     */
    private String getLocationAddressState() {
        if (manager == null || manager.getSamplePrivateWell() == null)
            return null;
        return manager.getSamplePrivateWell().getLocationAddress().getState();
    }

    /**
     * sets the location's state
     */
    private void setLocationAddressState(String state) {
        manager.getSamplePrivateWell().getLocationAddress().setState(state);

    }

    /**
     * returns the location's zip code or null if the manager is null or if this
     * is not a private well sample
     */
    private String getLocationAddressZipCode() {
        if (manager == null || manager.getSamplePrivateWell() == null)
            return null;
        return manager.getSamplePrivateWell().getLocationAddress().getZipCode();
    }

    /**
     * sets the location's zip code
     */
    private void setLocationAddressZipCode(String zipCode) {
        manager.getSamplePrivateWell().getLocationAddress().setZipCode(zipCode);
    }

    /**
     * returns the owner or null if the manager is null or if this is not a
     * private well sample
     */
    private String getOwner() {
        if (manager == null || manager.getSamplePrivateWell() == null)
            return null;
        return manager.getSamplePrivateWell().getOwner();
    }

    /**
     * sets the owner
     */
    private void setOwner(String owner) {
        manager.getSamplePrivateWell().setOwner(owner);
    }

    /**
     * returns the collector or null if the manager is null or if this is not a
     * private well sample
     */
    private String getCollector() {
        if (manager == null || manager.getSamplePrivateWell() == null)
            return null;
        return manager.getSamplePrivateWell().getCollector();
    }

    /**
     * sets the collector
     */
    private void setCollector(String collector) {
        manager.getSamplePrivateWell().setCollector(collector);
    }

    /**
     * returns the well number or null if the manager is null or if this is not
     * a private well sample
     */
    private Integer getWellNumber() {
        if (manager == null || manager.getSamplePrivateWell() == null)
            return null;
        return manager.getSamplePrivateWell().getWellNumber();
    }

    /**
     * sets the well number
     */
    private void setWellNumber(Integer wellNumber) {
        manager.getSamplePrivateWell().setWellNumber(wellNumber);
    }
}
