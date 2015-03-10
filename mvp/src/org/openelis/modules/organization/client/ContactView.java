package org.openelis.modules.organization.client;

import java.util.ArrayList;

import org.openelis.manager.OrganizationManager;
import org.openelis.meta.AddressMeta;
import org.openelis.meta.OrganizationContactMeta;
import org.openelis.ui.annotation.Enable;
import org.openelis.ui.annotation.Validate;
import org.openelis.ui.annotation.View;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.table.Row;
import org.openelis.ui.widget.table.Table;
import org.openelis.ui.screen.Presenter;
import org.openelis.ui.screen.State;

import com.google.gwt.uibinder.client.UiField;

@View(template="ContactTab.ui.xml",presenter=ContactPresenter.class)
public class ContactView extends org.openelis.ui.screen.View<OrganizationManager> {

    @UiField
    @Enable({State.ADD,State.UPDATE,State.QUERY})
    @Validate
    protected Table table;
    
    @UiField
    @Enable({State.ADD,State.UPDATE,State.QUERY})
    protected Button remove, add;

    @UiField
    protected Dropdown<Integer> type;

    @UiField
    protected Dropdown<String>  state, country;

    ContactPresenter presenter;
    
    public void setData(ArrayList<Row> model) {
    	table.setModel(model);
    }
    
    public void setTypeModel(ArrayList<Item<Integer>> model) {
    	type.setModel(model);
    }
    
    public void setStateModel(ArrayList<Item<String>> model) {
    	state.setModel(model);
    }
    
    public void setCountryModel(ArrayList<Item<String>> model) {
    	country.setModel(model);
    }
    
    @Override
    public void getQueryFields(ArrayList<QueryData> qds) {
    	QueryData qd;
        for (int i = 0; i < 12; i++ ) {
            qd = (QueryData) ((org.openelis.ui.widget.Queryable)table.getColumnWidget(i)).getQuery();
            if (qd != null) {
                switch (i) {
                    case 0:
                        qd.setKey(OrganizationContactMeta.CONT_CONTACT_TYPE_ID);
                        break;
                    case 1:
                        qd.setKey(OrganizationContactMeta.CONT_NAME);
                        break;
                    case 2:
                        qd.setKey("organizationContact.address."+AddressMeta.WORK_PHONE);
                        break;
                    case 3:
                        qd.setKey("organizationContact.address."+AddressMeta.HOME_PHONE);
                        break;
                    case 4:
                        qd.setKey("organizationContact.address."+AddressMeta.CELL_PHONE);
                        break;
                    case 5:
                        qd.setKey("organizationContact.address."+AddressMeta.FAX_PHONE);
                        break;
                    case 6:
                        qd.setKey("organizationContact.address."+AddressMeta.EMAIL);
                        break;
                    case 7:
                        qd.setKey("organizationContact.address."+AddressMeta.MULTIPLE_UNIT);
                        break;
                    case 8:
                        qd.setKey("organizationContact.address."+AddressMeta.STREET_ADDRESS);
                        break;
                    case 9:
                        qd.setKey("organizationContact.address."+AddressMeta.CITY);
                        break;
                    case 10:
                        qd.setKey("organizationContact.address."+AddressMeta.STATE);
                        break;
                    case 11:
                        qd.setKey("organizationContact.address."+AddressMeta.ZIP_CODE);
                        break;
                    case 12:
                        qd.setKey("organizationContact.address."+AddressMeta.COUNTRY);
                        break;
                }
                qds.add(qd);
            }
        }
    }

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = (ContactPresenter)presenter;
	}
    
}
