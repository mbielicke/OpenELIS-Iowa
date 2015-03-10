package org.openelis.client;

import javax.inject.Inject;

import org.openelis.client.event.ShowScreenEvent;
import org.openelis.di.AppDI;
import org.openelis.ui.common.ModulePermission;
import org.openelis.ui.mvp.Presenter;
import org.openelis.ui.screen.State;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;

public class OpenELISPresenter extends Presenter {
	
	ScreenBus screenBus = AppDI.INSTANCE.screenBus();

	OpenELISViewImpl view;

	public OpenELISPresenter(OpenELISViewImpl view) {
		this.view = view; 
		view.setPresenter(this);
		initialize();
		view.setState(State.DEFAULT);
	}
	
	private void initialize() {
		view.method.addCommand(new Command() {	
			public void execute() {
		        ShowScreenEvent event = new ShowScreenEvent(ScreenBus.METHOD);
		        screenBus.fireEvent(event);
			}
		});
		view.organization.addCommand(new Command() {
			public void execute() {
				ShowScreenEvent event = new ShowScreenEvent(ScreenBus.ORGANIZATION);
				screenBus.fireEvent(event);
			}
		});
	}
	
	public IsWidget getView() {
		return view;
	}
	
	@Override
	public ModulePermission permissions() {
		return null;
	}

}
