package org.openelis.modules.organization.client;

import java.util.ArrayList;

import javax.inject.Inject;

import org.openelis.cache.CategoryCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.OrganizationContactDO;
import org.openelis.manager.OrganizationManager;
import org.openelis.ui.annotation.Handler;
import org.openelis.ui.common.ModulePermission;
import org.openelis.ui.screen.Presenter;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.table.Row;
import org.openelis.ui.widget.table.event.CellEditedEvent;
import org.openelis.ui.widget.table.event.RowAddedEvent;
import org.openelis.ui.widget.table.event.RowDeletedEvent;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.Window;

public class ContactPresenter extends Presenter<OrganizationManager> {

    private OrganizationManager       manager;
    
    ContactViewImpl view;
    
    State state;

    @Inject
    public ContactPresenter() {
    	view = new ContactViewImpl();
    	view.setPresenter(this);
        
        initialize();
        initializeDropdowns();
    }

    private void initialize() {

    }
    
    @Handler("table")
    public void onCellUpdated(CellEditedEvent event) {
    	int r, c;
    	Object val;
    	OrganizationContactDO data;

    	r = event.getRow();
    	c = event.getCol();
    	val = view.table.getValueAt(r, c);

    	try {
    		data = manager.getContacts().getContactAt(r);
    	} catch (Exception e) {
    		Window.alert(e.getMessage());
    		return;
    	}

    	switch (c) {
    		case 0:
    			data.setContactTypeId((Integer)val);
    			break;
    		case 1:
    			data.setName((String)val);
    			break;
    		case 2:
    			data.getAddress().setWorkPhone((String)val);
    			break;
    		case 3:
    			data.getAddress().setHomePhone((String)val);
    			break;
    		case 4:
    			data.getAddress().setCellPhone((String)val);
    			break;
    		case 5:
    			data.getAddress().setFaxPhone((String)val);
    			break;
    		case 6:
    			data.getAddress().setEmail((String)val);
    			break;
    		case 7:
    			data.getAddress().setMultipleUnit((String)val);
    			break;
    		case 8:
    			data.getAddress().setStreetAddress((String)val);
    			break;
    		case 9:
    			data.getAddress().setCity((String)val);
    			break;
    		case 10:
    			data.getAddress().setState((String)val);
    			break;
    		case 11:
    			data.getAddress().setZipCode((String)val);
    			break;
    		case 12:
    			data.getAddress().setCountry((String)val);
    			break;
    	}
    }

    @Handler("table")
    public void onRowAdded(RowAddedEvent event) {
        try {
            manager.getContacts().addContact(new OrganizationContactDO());
        } catch (Exception e) {
            Window.alert(e.getMessage());
        }
    }

    @Handler("table")
    public void onRowDeleted(RowDeletedEvent event) {
        try {
            manager.getContacts().removeContactAt(event.getIndex());
        } catch (Exception e) {
            Window.alert(e.getMessage());
        }
    }


    @Handler("remove")
    protected void remove(ClickEvent event) {
        int r;

        r = view.table.getSelectedRow();
        if (r > -1 && view.table.getRowCount() > 0)
            view.table.removeRowAt(r);
    }

    @Handler("add")
    public void add(ClickEvent event) {
        int n;

        view.table.addRow();
        n = view.table.getRowCount() - 1;
        view.table.selectRowAt(n);
        view.table.scrollToVisible(view.table.getSelectedRow());
        view.table.startEditing(n, 0);
    }

    private void initializeDropdowns() {
        ArrayList<Item<String>> smodel;
        ArrayList<Item<Integer>> imodel;
        ArrayList<DictionaryDO> list;
        Item<Integer> irow;
        Item<String> srow;

        imodel = new ArrayList<Item<Integer>>();
        imodel.add(new Item<Integer>(null, ""));
        list = CategoryCache.getBySystemName("contact_type");
        for (DictionaryDO d : list) {
            irow = new Item<Integer>(d.getId(), d.getEntry());
            irow.setEnabled( ("Y".equals(d.getIsActive())));
            imodel.add(irow);
        }
        view.setTypeModel(imodel);

        smodel = new ArrayList<Item<String>>();
        smodel.add(new Item<String>(null, ""));
        list = CategoryCache.getBySystemName("state");
        for (DictionaryDO d : list) {
            srow = new Item<String>(d.getEntry(), d.getEntry());
            srow.setEnabled( ("Y".equals(d.getIsActive())));
            smodel.add(srow);
        }
        view.setStateModel(smodel);

        smodel = new ArrayList<Item<String>>();
        smodel.add(new Item<String>(null, ""));
        list = CategoryCache.getBySystemName("country");
        for (DictionaryDO d : list) {
            srow = new Item<String>(d.getEntry(), d.getEntry());
            srow.setEnabled( ("Y".equals(d.getIsActive())));
            smodel.add(srow);
        }
        view.setCountryModel(smodel);
    }

    private ArrayList<Row> getTableModel() {
        int i;
        Row row;
        OrganizationContactDO data;
        ArrayList<Row> model;

        model = new ArrayList<Row>();
        if (manager == null)
            return model;

        try {
            for (i = 0; i < manager.getContacts().count(); i++ ) {
                data = (OrganizationContactDO)manager.getContacts().getContactAt(i);

                row = new Row(13);
                row.setCell(0, data.getContactTypeId());
                row.setCell(1, data.getName());
                row.setCell(2, data.getAddress().getWorkPhone());
                row.setCell(3, data.getAddress().getHomePhone());
                row.setCell(4, data.getAddress().getCellPhone());
                row.setCell(5, data.getAddress().getFaxPhone());
                row.setCell(6, data.getAddress().getEmail());
                row.setCell(7, data.getAddress().getMultipleUnit());
                row.setCell(8, data.getAddress().getStreetAddress());
                row.setCell(9, data.getAddress().getCity());
                row.setCell(10, data.getAddress().getState());
                row.setCell(11, data.getAddress().getZipCode());
                row.setCell(12, data.getAddress().getCountry());
                model.add(row);
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
        }
        return model;
    }

    public void setData(OrganizationManager manager) {
        this.manager = manager;
        view.setData(getTableModel());
    }
    
    public void setState(State state) {
    	this.state = state;
    	view.setState(state);
    }

	@Override
	public ModulePermission permissions() {
		// TODO Auto-generated method stub
		return null;
	}

}
