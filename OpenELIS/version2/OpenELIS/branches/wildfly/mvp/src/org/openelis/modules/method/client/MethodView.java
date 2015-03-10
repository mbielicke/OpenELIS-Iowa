package org.openelis.modules.method.client;


import org.openelis.domain.MethodDO;
import org.openelis.meta.MethodMeta;
import org.openelis.ui.annotation.Field;
import org.openelis.ui.annotation.View;
import org.openelis.ui.screen.NavigableView;
import org.openelis.ui.screen.Presenter;
import org.openelis.ui.widget.CheckBox;
import org.openelis.ui.widget.TextBox;
import org.openelis.ui.widget.calendar.Calendar;

import com.google.gwt.uibinder.client.UiField;

@View(template="Method.ui.xml",presenter=MethodPresenter.class)
public class MethodView extends NavigableView<MethodDO> {
	
	@UiField
	@Field(meta=MethodMeta.ACTIVE_BEGIN,tab={"activeEnd","isActive"})
    protected Calendar activeBegin;
    
	@UiField
    @Field(meta=MethodMeta.ACTIVE_END,tab={"name","activeBegin"})
    protected Calendar activeEnd;

	@UiField
    @Field(meta=MethodMeta.NAME,tab={"description","activeEnd"})
    protected TextBox<String> name;
    
	@UiField
	@Field(meta=MethodMeta.DESCRIPTION,tab={"reportingDescription","name"})
    protected TextBox<String> description;
    
	@UiField
	@Field(meta=MethodMeta.REPORTING_DESCRIPTION,tab={"isActive","reportingDescription"})
    protected TextBox<String> reportingDescription;
    
	@UiField
	@Field(meta=MethodMeta.IS_ACTIVE,tab={"activeBegin","reportingDescription"})
    protected CheckBox isActive;
            
    public void setData(MethodDO data) {
    	name.setValue(data.getName());
    	description.setValue(data.getDescription());
    	reportingDescription.setValue(data.getReportingDescription());
    	isActive.setValue(data.getIsActive());
    	activeBegin.setValue(data.getActiveBegin());
    	activeEnd.setValue(data.getActiveEnd());
    }

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
		
	}

}
