package org.openelis.client;

import org.openelis.ui.common.ModulePermission;
import org.openelis.ui.mvp.Presenter;

import com.google.gwt.user.client.ui.IsWidget;

public class OpenELIS extends Presenter {

	OpenELISViewImpl view;

	public OpenELIS() {
		view = new OpenELISViewImpl(this);
	}
	
	public IsWidget getView() {
		return view;
	}
	
	@Override
	public ModulePermission permissions() {
		return null;
	}

}
