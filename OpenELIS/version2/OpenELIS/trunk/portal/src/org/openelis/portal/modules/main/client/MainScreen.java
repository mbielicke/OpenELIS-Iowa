package org.openelis.portal.modules.main.client;

import org.openelis.portal.modules.finalReport.client.FinalReportScreen;
import org.openelis.portal.modules.sampleStatus.client.SampleStatusScreen;
import org.openelis.ui.screen.Screen;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.LayoutPanel;

public class MainScreen extends Screen {

    MainUI ui = GWT.create(MainUIImpl.class);

    public MainScreen() {
        initWidget(ui.asWidget());
        initialize();
    }

    protected void initialize() {
        ui.navigation().finalReport().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ui.main().clear();
                FinalReportScreen screen = new FinalReportScreen();
                ui.main().add(screen);
                ((LayoutPanel)ui.main()).setWidgetTopBottom(screen, 0, Unit.PX, 0, Unit.PX);
            }
        });

        ui.navigation().sampleStatus().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ui.main().clear();
                SampleStatusScreen screen = new SampleStatusScreen();
                ui.main().add(screen);
                ((LayoutPanel)ui.main()).setWidgetTopBottom(screen, 0, Unit.PX, 0, Unit.PX);
            }
        });
    }

}
