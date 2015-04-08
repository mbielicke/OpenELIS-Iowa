package org.openelis.portal.modules.main.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Widget;

public class MainUIImpl extends Composite implements MainUI {

    @UiTemplate("Main.ui.xml")
    interface MainUiBinder extends UiBinder<Widget, MainUIImpl> {
    };

    protected static final MainUiBinder uiBinder = GWT.create(MainUiBinder.class);

    @UiField
    AbsolutePanel                       main;

    @UiField
    FocusPanel                          logo;

    @UiField
    Navigation                          navigation;

    public MainUIImpl() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    public AbsolutePanel main() {
        return main;
    }

    public Navigation navigation() {
        return navigation;
    }

    public FocusPanel logo() {
        return logo;
    }

    public void popup() {
        QuestionPopupUI.popup("500px", "100px", 550);
    }
}
