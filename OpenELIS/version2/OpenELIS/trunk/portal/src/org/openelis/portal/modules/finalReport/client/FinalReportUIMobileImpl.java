package org.openelis.portal.modules.finalReport.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

public class FinalReportUIMobileImpl extends ResizeComposite implements FinalReportUI {

    @UiTemplate("FinalReportMobile.ui.xml")
    interface FinalReportUiBinder extends UiBinder<Widget, FinalReportUIMobileImpl> {
    };

    protected static final FinalReportUiBinder uiBinder = GWT.create(FinalReportUiBinder.class);

    public FinalReportUIMobileImpl() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public Widget asWidget() {
        return this;
    }

}
