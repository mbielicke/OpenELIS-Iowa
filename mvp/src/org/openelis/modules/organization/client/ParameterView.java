package org.openelis.modules.organization.client;

import java.util.ArrayList;

import org.openelis.manager.OrganizationManager;
import org.openelis.meta.OrganizationMeta;
import org.openelis.meta.OrganizationParameterMeta;
import org.openelis.ui.annotation.Enable;
import org.openelis.ui.annotation.Validate;
import org.openelis.ui.annotation.View;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.screen.Presenter;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.Queryable;
import org.openelis.ui.widget.table.Row;
import org.openelis.ui.widget.table.Table;

import com.google.gwt.uibinder.client.UiField;

@View(template="ParameterTab.ui.xml",presenter=ParameterPresenter.class)
public class ParameterView extends org.openelis.ui.screen.View<OrganizationManager> {

    @UiField
    @Enable({State.ADD,State.QUERY,State.UPDATE})
    @Validate
    protected Table                     table;
    
    @UiField
    @Enable({State.ADD,State.QUERY,State.UPDATE})
    protected Button                    remove, add;

    @UiField
    protected Dropdown<Integer>         type;
    
    ParameterPresenter presenter;
    
    public void setData(ArrayList<Row> model) {
    	table.setModel(model);
    }
    
    public void setTypeModel(ArrayList<Item<Integer>> model) {
    	type.setModel(model);
    }
    
    @Override
    public void getQueryFields(ArrayList<QueryData> qds) {
        QueryData qd;

        for (int i = 0; i < 2; i++ ) {
            qd = (QueryData) ((Queryable)table.getColumnWidget(i)).getQuery();
            if (qd != null) {
                switch (i) {
                    case 0:
                        qd.setKey(OrganizationParameterMeta.PARM_TYPE_ID);
                        break;
                    case 1:
                        qd.setKey(OrganizationParameterMeta.PARM_VALUE);
                        break;
                }
                qds.add(qd);
            }
        }
    }

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = (ParameterPresenter)presenter;
	}
	
}
