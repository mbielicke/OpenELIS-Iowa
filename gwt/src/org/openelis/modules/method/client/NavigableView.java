package org.openelis.modules.method.client;

import static org.openelis.ui.screen.State.DEFAULT;
import static org.openelis.ui.screen.State.DISPLAY;

import org.openelis.ui.annotation.Enable;
import org.openelis.ui.annotation.Shortcut;
import org.openelis.ui.screen.Presenter;
import org.openelis.ui.screen.View;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.AtoZButtons;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.Menu;
import org.openelis.ui.widget.MenuItem;
import org.openelis.ui.widget.table.Table;

import com.google.gwt.uibinder.client.UiField;

public abstract class NavigableView extends View {
	
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

}
