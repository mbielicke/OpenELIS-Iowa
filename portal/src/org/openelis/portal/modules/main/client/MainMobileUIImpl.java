package org.openelis.portal.modules.main.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

public class MainMobileUIImpl extends ResizeComposite implements MainUI {

    @UiTemplate("MainMobile.ui.xml")
    interface MainUiBinder extends UiBinder<Widget, MainMobileUIImpl> {
    };

    protected static final MainUiBinder uiBinder = GWT.create(MainUiBinder.class);

    @UiField
    AbsolutePanel                       main;

    @UiField
    Navigation                          navigation;

    public MainMobileUIImpl() {
        initWidget(uiBinder.createAndBindUi(this));
        main.getElement().getStyle().setOverflow(Overflow.AUTO);
    }

    public AbsolutePanel main() {
        return main;
    }

    public Navigation navigation() {
        return navigation;
    }

    public FocusPanel logo() {
        return null;
    }

}
