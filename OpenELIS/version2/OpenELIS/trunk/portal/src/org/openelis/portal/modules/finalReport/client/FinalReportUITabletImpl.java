package org.openelis.portal.modules.finalReport.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

public class FinalReportUITabletImpl extends ResizeComposite implements FinalReportUI {

    @UiTemplate("FinalReportTablet.ui.xml")
    interface FinalReportUiBinder extends UiBinder<Widget, FinalReportUITabletImpl> {
    };

    protected static final FinalReportUiBinder uiBinder = GWT.create(FinalReportUiBinder.class);

    public FinalReportUITabletImpl() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public Widget asWidget() {
        return this;
    }

}
