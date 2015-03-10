package org.openelis.modules.organization.client;

import java.util.ArrayList;

import javax.inject.Inject;

import org.openelis.cache.CategoryCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.OrganizationParameterDO;
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
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;

public class ParameterPresenter extends Presenter<OrganizationManager> {
    
    private OrganizationManager manager;
    
    ParameterViewImpl view;
    
    State state;
    
    @Inject
    public ParameterPresenter() {
    	this.view = new ParameterViewImpl();
    	view.setPresenter(this);

        initialize();

        initializeDropdowns();
    }

    private void initialize() {
    }

    @Handler("table")
    public void onCellUpdated(CellEditedEvent event) {
    	int r, c;
    	Object val = null;
    	OrganizationParameterDO data;

    	if (state == State.QUERY)
    		return;

    	r = event.getRow();
    	c = event.getCol();
    	view.table.getValueAt(r, c);

    	try {
    		data = manager.getParameters().getParameterAt(r);
    	} catch (Exception e) {
    		Window.alert(e.getMessage());
    		return;
    	}

    	switch (c) {
    		case 0:
    			data.setTypeId((Integer)val);
    			break;
    		case 1:
    			data.setValue((String)val);
    			break;
    	}
    }
    
    @Handler("table")
    public void onRowAdded(RowAddedEvent event) {
    	try {
    		manager.getParameters().addParameter(new OrganizationParameterDO());
    	} catch (Exception e) {
    		Window.alert(e.getMessage());
    	}
    }

    @Handler("table")
    public void onRowDeleted(RowDeletedEvent event) {
    	try {
    		manager.getParameters().removeParameterAt(event.getIndex());
    	} catch (Exception e) {
    		Window.alert(e.getMessage());
    	}
    }


    @UiHandler("remove")
    protected void remove(ClickEvent event) {
        int r;

        r = view.table.getSelectedRow();
        if (r > -1 && view.table.getRowCount() > 0)
            view.table.removeRowAt(r);
    }

    @UiHandler("remove")
    protected void add(ClickEvent event) {
        int n;

        view.table.addRow();
        n = view.table.getRowCount() - 1;
        view.table.selectRowAt(n);
        view.table.scrollToVisible(view.table.getSelectedRow());
        view.table.startEditing(n, 0);
    }

    private void initializeDropdowns() {
        ArrayList<Item<Integer>> model;
        ArrayList<DictionaryDO> list;
        Item<Integer> row;

        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        list = CategoryCache.getBySystemName("parameter_type");
        for (DictionaryDO d : list) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled( ("Y".equals(d.getIsActive())));
            model.add(row);
        }
        view.setTypeModel(model);
    }

    private ArrayList<Row> getTableModel() {
        int i;
        Row row;
        OrganizationParameterDO data;
        ArrayList<Row> model;

        model = null;
        if (manager == null)
            return model;

        try {
            model = new ArrayList<Row>();
            for (i = 0; i < manager.getParameters().count(); i++ ) {
                data = (OrganizationParameterDO)manager.getParameters().getParameterAt(i);

                row = new Row(2);
                row.setCell(0, data.getTypeId());
                row.setCell(1, data.getValue());
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
