package org.openelis.modules.method.client;

import org.openelis.ui.annotation.Enable;
import org.openelis.ui.annotation.Meta;
import org.openelis.ui.annotation.Validate;
import org.openelis.ui.annotation.View;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.TextBox;

import com.google.gwt.uibinder.client.UiField;

@View(template="MethodMobile.ui.xml",presenter=MethodPresenter.class)
public class MethodMobileView extends MethodView {
	
	@UiField
	@Meta("_method.MobileBox")
	@Enable({State.ADD,State.UPDATE,State.QUERY})
	@Validate
	protected TextBox<String> mobileBox;
	

}
