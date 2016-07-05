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
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.ProviderDO;
import org.openelis.manager.SampleManager1;
import org.openelis.meta.SampleMeta;
import org.openelis.modules.patient.client.PatientLookupUI;
import org.openelis.modules.provider1.client.ProviderService1;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.GetMatchesEvent;
import org.openelis.ui.event.GetMatchesHandler;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.AutoComplete;
import org.openelis.ui.widget.AutoCompleteValue;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.QueryFieldUtil;
import org.openelis.ui.widget.TextBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.VisibleEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.Widget;

public class AnimalTabUI extends Screen {
    @UiTemplate("AnimalTab.ui.xml")
    interface AnimalTabUIBinder extends UiBinder<Widget, AnimalTabUI> {
    };

    private static AnimalTabUIBinder uiBinder = GWT.create(AnimalTabUIBinder.class);

    protected SampleManager1         manager;

    @UiField
    protected TextBox<String>        location, locationAddressMultipleUnit,
                    locationAddressStreetAddress, locationAddressCity, locationAddressZipCode,
                    providerFirstName, providerPhone;

    @UiField
    protected Dropdown<Integer>      animalCommonName, animalScientificName;

    @UiField
    protected Dropdown<String>       locationAddressState;

    @UiField
    protected AutoComplete           providerLastName;

    protected Screen                 parentScreen;

    protected AnimalTabUI            screen;

    protected PatientLookupUI        patientLookup;

    protected Focusable              focusedWidget;

    protected EventBus               parentBus;

    protected boolean                canEdit, isBusy, isVisible, redraw, canQuery;

    public AnimalTabUI(Screen parentScreen) {
        this.parentScreen = parentScreen;
        this.parentBus = parentScreen.getEventBus();
        this.window = parentScreen.getWindow();
        initWidget(uiBinder.createAndBindUi(this));
        initialize();

        manager = null;
    }

    private void initialize() {
        Item<Integer> row;
        Item<String> strow;
        ArrayList<Item<Integer>> model;
        ArrayList<Item<String>> stmodel;

        screen = this;

        addScreenHandler(animalCommonName,
                         SampleMeta.getAnimalAnimalCommonNameId(),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent<Integer> event) {
                                 animalCommonName.setValue(getAnimalCommonNameId());
                             }

                             public void onValueChange(ValueChangeEvent<Integer> event) {
                                 setAnimalCommonNameId(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 animalCommonName.setEnabled(isState(QUERY) ||
                                                             (canEdit && isState(ADD, UPDATE)));
                                 animalCommonName.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? animalScientificName : providerPhone;
                             }
                         });

        addScreenHandler(animalScientificName,
                         SampleMeta.getAnimalAnimalScientificNameId(),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent<Integer> event) {
                                 animalScientificName.setValue(getAnimalScientificNameId());
                             }

                             public void onValueChange(ValueChangeEvent<Integer> event) {
                                 setAnimalScientificNameId(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 animalScientificName.setEnabled(isState(QUERY) ||
                                                                 (canEdit && isState(ADD,
                                                                                           UPDATE)));
                                 animalScientificName.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? location : animalCommonName;
                             }
                         });

        addScreenHandler(location, SampleMeta.getAnimalLocation(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent<String> event) {
                location.setValue(getLocation());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                setLocation(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                location.setEnabled(isState(QUERY) || (canEdit && isState(ADD, UPDATE)));
                location.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? locationAddressMultipleUnit : animalScientificName;
            }
        });

        addScreenHandler(locationAddressMultipleUnit,
                         SampleMeta.getAnimalLocationAddrMultipleUnit(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 locationAddressMultipleUnit.setValue(getAnimalLocationAddrMultipleUnit());
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

                             public Widget onTab(boolean forward) {
                                 return forward ? locationAddressStreetAddress : location;
                             }
                         });

        addScreenHandler(locationAddressStreetAddress,
                         SampleMeta.getAnimalLocationAddrStreetAddress(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
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

                             public Widget onTab(boolean forward) {
                                 return forward ? locationAddressCity : locationAddressMultipleUnit;
                             }
                         });

        addScreenHandler(locationAddressCity,
                         SampleMeta.getAnimalLocationAddrCity(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 locationAddressCity.setValue(getLocationAddressCity());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setLocationAddressCity(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 locationAddressCity.setEnabled(isState(QUERY) ||
                                                                (canEdit && isState(ADD,
                                                                                          UPDATE)));
                                 locationAddressCity.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? locationAddressState
                                               : locationAddressStreetAddress;
                             }
                         });

        addScreenHandler(locationAddressState,
                         SampleMeta.getAnimalLocationAddrState(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 locationAddressState.setValue(getLocationAddressState());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setLocationAddressState(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 locationAddressState.setEnabled(isState(QUERY) ||
                                                                 (canEdit && isState(ADD,
                                                                                           UPDATE)));
                                 locationAddressState.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? locationAddressZipCode : locationAddressCity;
                             }
                         });

        addScreenHandler(locationAddressZipCode,
                         SampleMeta.getAnimalLocationAddrZipCode(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 locationAddressZipCode.setValue(getLocationAddressZipCode());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setLocationAddressZipCode(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 locationAddressZipCode.setEnabled( (isState(QUERY)) ||
                                                                   (canEdit && isState(ADD,
                                                                                             UPDATE)));
                                 locationAddressZipCode.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? providerLastName : locationAddressState;
                             }
                         });

        addScreenHandler(providerLastName,
                         SampleMeta.getAnimalProviderLastName(),
                         new ScreenHandler<AutoCompleteValue>() {
                             public void onDataChange(DataChangeEvent<AutoCompleteValue> event) {
                                 providerLastName.setValue(getProviderId(), getProviderLastName());
                             }

                             public void onValueChange(ValueChangeEvent<AutoCompleteValue> event) {
                                 ProviderDO data;

                                 data = null;
                                 if (event.getValue() != null)
                                     data = (ProviderDO)event.getValue().getData();
                                 setProvider(data);
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 providerLastName.setEnabled( (isState(QUERY) && canQuery) ||
                                                             (canEdit && isState(ADD, UPDATE)));
                                 providerLastName.setQueryMode( (isState(QUERY) && canQuery));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? providerFirstName : locationAddressZipCode;
                             }
                         });

        providerLastName.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                ProviderDO data;
                ArrayList<ProviderDO> list;
                Item<Integer> row;
                ArrayList<Item<Integer>> model;

                parentScreen.setBusy();
                try {
                    list = ProviderService1.get()
                                           .fetchByLastNameNpiExternalId(QueryFieldUtil.parseAutocomplete(event.getMatch()));
                    model = new ArrayList<Item<Integer>>();
                    for (int i = 0; i < list.size(); i++ ) {
                        row = new Item<Integer>(4);
                        data = list.get(i);

                        row.setKey(data.getId());
                        row.setData(data);
                        row.setCell(0, data.getLastName());
                        row.setCell(1, data.getFirstName());
                        row.setCell(2, data.getMiddleName());
                        row.setCell(3, data.getNpi());

                        model.add(row);
                    }
                    providerLastName.showAutoMatches(model);
                } catch (Throwable e) {
                    Window.alert(e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                }
                parentScreen.clearStatus();
            }
        });

        addScreenHandler(providerFirstName,
                         SampleMeta.getAnimalProviderFirstName(),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent<Integer> event) {
                                 providerFirstName.setValue(getProviderFirstName());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 providerFirstName.setEnabled( (isState(QUERY) && canQuery) ||
                                                              (canEdit && isState(ADD, UPDATE)));
                                 providerFirstName.setQueryMode( (isState(QUERY) && canQuery));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? providerPhone : providerLastName;
                             }
                         });

        addScreenHandler(providerPhone,
                         SampleMeta.getAnimalProviderPhone(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 providerPhone.setValue(getProviderPhone());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setProviderPhone(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 providerPhone.setEnabled( (isState(QUERY) && canQuery) ||
                                                          (canEdit && isState(ADD, UPDATE)));
                                 providerPhone.setQueryMode( (isState(QUERY) && canQuery));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? animalCommonName : providerFirstName;
                             }
                         });

        addVisibleHandler(new VisibleEvent.Handler() {
            public void onVisibleOrInvisible(VisibleEvent event) {
                isVisible = event.isVisible();
                displaySampleAnimal();
            }
        });

        model = new ArrayList<Item<Integer>>();
        for (DictionaryDO d : CategoryCache.getBySystemName("animal_scientific_name")) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled( ("Y".equals(d.getIsActive())));
            model.add(row);
        }

        animalScientificName.setModel(model);

        model = new ArrayList<Item<Integer>>();
        for (DictionaryDO d : CategoryCache.getBySystemName("animal_common_name")) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled( ("Y".equals(d.getIsActive())));
            model.add(row);
        }

        animalCommonName.setModel(model);

        stmodel = new ArrayList<Item<String>>();
        for (DictionaryDO d : CategoryCache.getBySystemName("state")) {
            strow = new Item<String>(d.getEntry(), d.getEntry());
            strow.setEnabled( ("Y".equals(d.getIsActive())));
            stmodel.add(strow);
        }

        locationAddressState.setModel(stmodel);
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
     * returns true if some operation performed by the tab needs to be completed
     * before the data can be committed
     */
    public boolean getIsBusy() {
        return isBusy;
    }

    /**
     * notifies the tab that it may need to refresh the display in its widgets;
     * if the data currently showing in the widgets is the same as the data in
     * the latest manager then the widgets are not refreshed
     */
    public void onDataChange() {
        redraw = true;
        displaySampleAnimal();
    }

    private void displaySampleAnimal() {
        if ( !isVisible)
            return;

        if (redraw) {
            redraw = false;
            fireDataChange();
        }
    }

    /**
     * Determines if the fields on the screen can be edited based on the data
     */
    private void evaluateEdit() {
        canEdit = (manager != null && !Constants.dictionary().SAMPLE_RELEASED.equals(manager.getSample()
                                                                                                  .getStatusId()));
    }

    /**
     * Returns the animal common name id or null if either the manager or the
     * sample animal DO is null
     */
    private Integer getAnimalCommonNameId() {
        if (manager == null || manager.getSampleAnimal() == null)
            return null;
        return manager.getSampleAnimal().getAnimalCommonNameId();
    }

    /**
     * Sets the animal common name id
     */
    private void setAnimalCommonNameId(Integer animalCommonNameId) {
        manager.getSampleAnimal().setAnimalCommonNameId(animalCommonNameId);
    }

    /**
     * Returns the animal scientific name id or null if either the manager or
     * the sample animal DO is null
     */
    private Integer getAnimalScientificNameId() {
        if (manager == null || manager.getSampleAnimal() == null)
            return null;
        return manager.getSampleAnimal().getAnimalScientificNameId();
    }

    /**
     * Sets the animal scientific name id
     */
    private void setAnimalScientificNameId(Integer animalScientificNameId) {
        manager.getSampleAnimal().setAnimalScientificNameId(animalScientificNameId);
    }

    /**
     * Returns the location or null if either the manager or the sample animal
     * DO is null
     */
    private String getLocation() {
        if (manager == null || manager.getSampleAnimal() == null)
            return null;
        return manager.getSampleAnimal().getLocation();
    }

    /**
     * Sets the location
     */
    private void setLocation(String location) {
        manager.getSampleAnimal().setLocation(location);
    }

    /**
     * Returns the location apt/suite# or null if either the manager or the
     * sample animal DO is null
     */
    private String getAnimalLocationAddrMultipleUnit() {
        if (manager == null || manager.getSampleAnimal() == null)
            return null;
        return manager.getSampleAnimal().getLocationAddress().getMultipleUnit();
    }

    /**
     * Sets the location apt/suite#
     */
    private void setLocationAddressMultipleUnit(String multipleUnit) {
        manager.getSampleAnimal().getLocationAddress().setMultipleUnit(multipleUnit);
    }

    /**
     * Returns the location street address or null if either the manager or the
     * sample animal DO is null
     */
    private String getLocationAddressStreetAddress() {
        if (manager == null || manager.getSampleAnimal() == null)
            return null;
        return manager.getSampleAnimal().getLocationAddress().getStreetAddress();
    }

    /**
     * Sets the location street address
     */
    private void setLocationAddressStreetAddress(String streetAddress) {
        manager.getSampleAnimal().getLocationAddress().setStreetAddress(streetAddress);

    }

    /**
     * Returns the location city or null if either the manager or the sample
     * animal DO is null
     */
    private String getLocationAddressCity() {
        if (manager == null || manager.getSampleAnimal() == null)
            return null;
        return manager.getSampleAnimal().getLocationAddress().getCity();
    }

    /**
     * Sets the location city
     */
    private void setLocationAddressCity(String city) {
        manager.getSampleAnimal().getLocationAddress().setCity(city);
    }

    /**
     * Returns the location state or null if either the manager or the sample
     * animal DO is null
     */
    private String getLocationAddressState() {
        if (manager == null || manager.getSampleAnimal() == null)
            return null;
        return manager.getSampleAnimal().getLocationAddress().getState();
    }

    /**
     * Sets the location state
     */
    private void setLocationAddressState(String state) {
        manager.getSampleAnimal().getLocationAddress().setState(state);
    }

    /**
     * Returns the location zip code or null if either the manager or the sample
     * animal DO is null
     */
    private String getLocationAddressZipCode() {
        if (manager == null || manager.getSampleAnimal() == null)
            return null;
        return manager.getSampleAnimal().getLocationAddress().getZipCode();
    }

    /**
     * Sets the location zip code
     */
    private void setLocationAddressZipCode(String zipCode) {
        manager.getSampleAnimal().getLocationAddress().setZipCode(zipCode);
    }

    /**
     * Returns the provider's id or null if the manager is null
     */
    private Integer getProviderId() {
        if (manager == null || manager.getSampleAnimal() == null)
            return null;
        return manager.getSampleAnimal().getProviderId();
    }

    /**
     * Sets the provider's id
     */
    private void setProviderId(Integer providerId) {
        manager.getSampleAnimal().setProviderId(providerId);
    }

    /**
     * Returns the provider's last name or null if the manager is null
     */
    private String getProviderLastName() {
        if (manager == null || manager.getSampleAnimal() == null ||
            manager.getSampleAnimal().getProvider() == null)
            return null;
        return manager.getSampleAnimal().getProvider().getLastName();
    }

    /**
     * Returns the provider's first name or null if the manager is null
     */
    private String getProviderFirstName() {
        if (manager == null || manager.getSampleAnimal() == null ||
            manager.getSampleAnimal().getProvider() == null)
            return null;
        return manager.getSampleAnimal().getProvider().getFirstName();
    }

    /**
     * Returns provider phone or null if the manager is null
     */
    private String getProviderPhone() {
        if (manager == null || manager.getSampleAnimal() == null ||
            manager.getSampleAnimal().getProvider() == null)
            return null;
        return manager.getSampleAnimal().getProviderPhone();
    }

    /**
     * Sets provider phone
     */
    private void setProviderPhone(String providerPhone) {
        manager.getSampleAnimal().setProviderPhone(providerPhone);
    }

    /**
     * If the passed provider is not null then sets it as the sample's provider,
     * otherwise, blanks the provider. Refreshes the display of the autcomplete
     * accordingly.
     */
    private void setProvider(ProviderDO data) {
        if (data == null || data.getId() == null)
            setProviderId(null);
        else
            setProviderId(data.getId());
        manager.getSampleAnimal().setProvider(data);
        providerLastName.setValue(getProviderId(), getProviderLastName());
        providerFirstName.setValue(getProviderFirstName());
    }
}