package org.openelis.modules.organization.client;

import static org.openelis.ui.screen.State.ADD;
import static org.openelis.ui.screen.State.DEFAULT;
import static org.openelis.ui.screen.State.DISPLAY;
import static org.openelis.ui.screen.State.QUERY;
import static org.openelis.ui.screen.State.UPDATE;

import java.util.ArrayList;

import org.openelis.manager.OrganizationManager;
import org.openelis.meta.AddressMeta;
import org.openelis.meta.OrganizationMeta;
import org.openelis.ui.annotation.Enable;
import org.openelis.ui.annotation.Impl;
import org.openelis.ui.annotation.Meta;
import org.openelis.ui.annotation.Queryable;
import org.openelis.ui.annotation.Shortcut;
import org.openelis.ui.annotation.Tab;
import org.openelis.ui.annotation.View;
import org.openelis.ui.screen.Presenter;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.AtoZButtons;
import org.openelis.ui.widget.AutoComplete;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.CheckBox;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.MenuItem;
import org.openelis.ui.widget.NotesPanel;
import org.openelis.ui.widget.TabLayoutPanel;
import org.openelis.ui.widget.TextBox;
import org.openelis.ui.widget.table.Table;

import com.google.gwt.uibinder.client.UiField;

@View(template="Organization.ui.xml",presenter=OrganizationPresenter.class)
public class OrganizationView extends org.openelis.ui.screen.NavigableView<OrganizationManager> {
        
	@UiField
	@Impl(ContactViewImpl.class)
    protected ContactViewImpl contactTab;
    
	@UiField
	@Impl(ParameterViewImpl.class)
    protected ParameterViewImpl parameterTab;
    
    @UiField
    protected NotesPanel notesPanel;

    
    @UiField
    @Enable(State.DISPLAY)
    protected MenuItem    orgAddressHistory, orgContactHistory,
                                        orgContactAddressHistory, orgParameterHistory;

    @UiField
    @Meta(OrganizationMeta.ID)
    @Enable(State.QUERY)
    @Queryable
    @Tab("name")
    protected TextBox<Integer> id;
    
    @UiField
    @Meta(OrganizationMeta.NAME)
    @Enable({State.ADD,State.UPDATE,State.QUERY})
    @Queryable
    @Tab({"city","id"})
    protected TextBox<String> name;
    
    @UiField
    @Meta(OrganizationMeta.ADDR_CITY)
    @Enable({State.ADD,State.UPDATE,State.QUERY})
    @Queryable
    @Tab({"multipleUnit","name"})
    protected TextBox<String> city;
    
    @UiField
    @Meta(OrganizationMeta.ADDR_MULTIPLE_UNIT)
    @Enable({State.ADD,State.UPDATE,State.QUERY})
    @Queryable
    @Tab({"state","city"})
    protected TextBox<String> multipleUnit;
    
    @UiField
    @Meta(OrganizationMeta.ADDR_ZIP_CODE)
    @Enable({State.ADD,State.UPDATE,State.QUERY})
    @Queryable
    @Tab({"streetAddress","state"})
    protected TextBox<String> zipCode;
    
    @UiField
    @Meta(OrganizationMeta.ADDR_STREET_ADDRESS)
    @Enable({State.ADD,State.UPDATE,State.QUERY})
    @Queryable
    @Tab({"country","zipCode"})
    protected TextBox<String> streetAddress;
    
    @UiField
    @Meta(OrganizationMeta.IS_ACTIVE)
    @Enable({State.ADD,State.UPDATE,State.QUERY})
    @Queryable
    @Tab({"name","parentName"})
    protected CheckBox isActive;
    
    @UiField
    @Meta(OrganizationMeta.ADDR_STATE)
    @Enable({State.ADD,State.UPDATE,State.QUERY})
    @Queryable
    @Tab({"zipCode","multipleUnit"})
    protected Dropdown<String> state;
    
    @UiField
    @Meta(OrganizationMeta.ADDR_COUNTRY)
    @Enable({State.ADD,State.UPDATE,State.QUERY})
    @Queryable
    //@Tab({"zipCode","multipleUnit"})
    protected Dropdown<String> country;
    
    @UiField
    @Meta(OrganizationMeta.ADDR_STATE)
    @Enable({State.ADD,State.UPDATE,State.QUERY})
    @Queryable
    @Tab({"isActive","country"})
    protected AutoComplete parentName;

    @UiField
    protected TabLayoutPanel tabPanel;
    
    OrganizationPresenter presenter;
    
    public void setData(OrganizationManager manager) {
    	 id.setValue(manager.getOrganization().getId());
    	 name.setValue(manager.getOrganization().getName());
    	 city.setValue(manager.getOrganization().getAddress().getCity());
    	 multipleUnit.setValue(manager.getOrganization().getAddress().getMultipleUnit());
    	 state.setValue(manager.getOrganization().getAddress().getState());
    	 zipCode.setValue(manager.getOrganization().getAddress().getZipCode());
    	 streetAddress.setValue(manager.getOrganization().getAddress().getStreetAddress());
    	 country.setValue(manager.getOrganization().getAddress().getCountry());
    	 parentName.setValue(manager.getOrganization().getParentOrganizationId(),manager.getOrganization().getParentOrganizationName());
    	 isActive.setValue(manager.getOrganization().getIsActive());
    }
    
    public void setState(State state) {

    }
    
    public void setCountryModel(ArrayList<Item<String>> model) {
    	country.setModel(model);
    }
    
    public void setStateModel(ArrayList<Item<String>> model) {
    	state.setModel(model);
    }

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = (OrganizationPresenter)presenter;
		
	}
}
