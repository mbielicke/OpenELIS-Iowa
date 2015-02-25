package org.openelis.modules.method.client;


import static org.openelis.ui.screen.State.DEFAULT;
import static org.openelis.ui.screen.State.DISPLAY;

import org.openelis.domain.MethodDO;
import org.openelis.meta.MethodMeta;
import org.openelis.ui.annotation.Enable;
import org.openelis.ui.annotation.Field;
import org.openelis.ui.annotation.Meta;
import org.openelis.ui.annotation.Queryable;
import org.openelis.ui.annotation.Shortcut;
import org.openelis.ui.annotation.Tab;
import org.openelis.ui.annotation.Validate;
import org.openelis.ui.annotation.View;
import org.openelis.ui.mvp.Presenter;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.AtoZButtons;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.CheckBox;
import org.openelis.ui.widget.Menu;
import org.openelis.ui.widget.MenuItem;
import org.openelis.ui.widget.TextBox;
import org.openelis.ui.widget.calendar.Calendar;
import org.openelis.ui.widget.table.Table;

import com.google.gwt.uibinder.client.UiField;

@View(template="Method.ui.xml",presenter=MethodPresenter.class)
public class MethodView extends org.openelis.ui.mvp.View {

	@Field(meta=MethodMeta.ACTIVE_BEGIN,tab={"activeEnd","isActive"})
    protected Calendar activeBegin;
    
    @Field(meta=MethodMeta.ACTIVE_END,tab={"name","activeBegin"})
    protected Calendar activeEnd;

    @Field(meta=MethodMeta.NAME,tab={"description","activeEnd"})
    protected TextBox<String> name;
    
	@Field(meta=MethodMeta.DESCRIPTION,tab={"reportingDescription","name"})
    protected TextBox<String> description;
    
	@Field(meta=MethodMeta.REPORTING_DESCRIPTION,tab={"isActive","reportingDescription"})
    protected TextBox<String> reportingDescription;
    
	@Field(meta=MethodMeta.IS_ACTIVE,tab={"activeBegin","reportingDescription"})
    protected CheckBox isActive;
    
	@UiField
    @Enable({State.DEFAULT,State.DISPLAY,State.QUERY})
    @Shortcut("q")
    protected Button query;
    
	@UiField
    @Enable({State.DISPLAY})
    @Shortcut("p")
    protected Button previous;
    
	@UiField
    @Enable({State.DISPLAY})
    @Shortcut("n")
    protected Button next;
    
	@UiField
    @Enable({State.DEFAULT,State.DISPLAY,State.ADD})
    @Shortcut("a")
    protected Button add;
    
	@UiField
    @Enable({State.DEFAULT,State.DISPLAY,State.UPDATE})
    @Shortcut("u")
    protected Button update;
    
	@UiField
    @Enable({State.QUERY,State.ADD,State.UPDATE})
    @Shortcut("m")
    protected Button commit;
    
	@UiField
    @Enable({State.ADD,State.UPDATE,State.QUERY})
    @Shortcut("o")
    protected Button abort;
    
	@UiField
    protected Button atozNext, atozPrev; 
    
	@UiField
    @Enable(State.DISPLAY)
    protected Button optionsButton;

	@UiField
    @Enable(State.DISPLAY)
    protected Menu optionsMenu;
    
	@UiField
    @Enable(State.DISPLAY)
    protected MenuItem history;
    
    @UiField
    protected AtoZButtons atozButtons;

    @UiField
    protected Table atozTable;
    
    Presenter presenter;
    
    public MethodView() { 
    }
    
    public void setState(State state) {
    	query.setEnabled(presenter.permissions().hasSelectPermission());
    	add.setEnabled(presenter.permissions().hasAddPermission());
    	update.setEnabled(presenter.permissions().hasUpdatePermission());
        switch (state) {
        	case QUERY : 
        		query.lock();
        		query.setPressed(true);
        		break;
        	case ADD : 
        		add.lock();
        		add.setPressed(true);
        		break;
        	case UPDATE :
        		update.lock();
        		update.setPressed(true);
        		break;
        	default :
        }
        boolean enable;
        enable = isState(DEFAULT, DISPLAY).contains(state) && presenter.permissions().hasSelectPermission();
        atozButtons.setEnabled(enable);
    }
    
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
