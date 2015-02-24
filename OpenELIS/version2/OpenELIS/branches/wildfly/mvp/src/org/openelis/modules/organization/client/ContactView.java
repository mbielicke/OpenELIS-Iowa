package org.openelis.modules.organization.client;

import java.util.ArrayList;

import org.openelis.meta.OrganizationMeta;
import org.openelis.ui.annotation.Enable;
import org.openelis.ui.annotation.Validate;
import org.openelis.ui.annotation.View;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.table.Row;
import org.openelis.ui.widget.table.Table;
import org.openelis.ui.mvp.Presenter;
import org.openelis.ui.screen.State;

import com.google.gwt.uibinder.client.UiField;

@View(template="ContactTab.ui.xml",presenter=ContactPresenter.class)
public class ContactView extends org.openelis.ui.mvp.View {

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
                        qd.setKey(OrganizationMeta.getContactContactTypeId());
                        break;
                    case 1:
                        qd.setKey(OrganizationMeta.getContactName());
                        break;
                    case 2:
                        qd.setKey(OrganizationMeta.getContactAddressWorkPhone());
                        break;
                    case 3:
                        qd.setKey(OrganizationMeta.getContactAddressHomePhone());
                        break;
                    case 4:
                        qd.setKey(OrganizationMeta.getContactAddressCellPhone());
                        break;
                    case 5:
                        qd.setKey(OrganizationMeta.getContactAddressFaxPhone());
                        break;
                    case 6:
                        qd.setKey(OrganizationMeta.getContactAddressEmail());
                        break;
                    case 7:
                        qd.setKey(OrganizationMeta.getContactAddressMultipleUnit());
                        break;
                    case 8:
                        qd.setKey(OrganizationMeta.getContactAddressStreetAddress());
                        break;
                    case 9:
                        qd.setKey(OrganizationMeta.getContactAddressCity());
                        break;
                    case 10:
                        qd.setKey(OrganizationMeta.getContactAddressState());
                        break;
                    case 11:
                        qd.setKey(OrganizationMeta.getContactAddressZipCode());
                        break;
                    case 12:
                        qd.setKey(OrganizationMeta.getAddressCountry());
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
