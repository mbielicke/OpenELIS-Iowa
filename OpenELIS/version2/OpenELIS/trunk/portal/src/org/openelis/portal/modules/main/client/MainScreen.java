package org.openelis.portal.modules.main.client;

import static org.openelis.portal.client.Logger.remote;

import java.util.logging.Level;

import org.openelis.portal.client.OpenELISService;
import org.openelis.portal.messages.Messages;
import org.openelis.portal.modules.dataView.client.DataViewScreen;
import org.openelis.portal.modules.emailNotification.client.EmailNotificationScreen;
import org.openelis.portal.modules.finalReport.client.FinalReportScreen;
import org.openelis.portal.modules.message.client.MessageScreen;
import org.openelis.portal.modules.message.client.MessageService;
import org.openelis.portal.modules.sampleStatus.client.SampleStatusScreen;
import org.openelis.ui.widget.PortalWindow;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;

public class MainScreen extends Composite {

    MainUI                ui = GWT.create(MainUIImpl.class);
    PortalWindow          window;
    private MessageScreen messageScreen;

    public MainScreen() {
        initWidget(ui.asWidget());
        initialize();
    }

    protected void initialize() {
        String messageOfTheDay;

        window = new PortalWindow();

        if (ui.logo() != null) {
            ui.logo().addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    ui.main().clear();
                    if (messageScreen != null)
                        ui.main().add(messageScreen);
                }
            });
        }

        ui.navigation().finalReport().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ui.main().clear();
                FinalReportScreen screen = new FinalReportScreen();
                screen.setWindow(window);
                ui.main().add(screen);
            }
        });

        ui.navigation().sampleStatus().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ui.main().clear();
                SampleStatusScreen screen = new SampleStatusScreen();
                screen.setWindow(window);
                ui.main().add(screen);
            }
        });

        // ui.navigation().cases.addClickHandler(new ClickHandler() {
        // @Override
        // public void onClick(ClickEvent event) {
        // ui.main().clear();
        // try {
        // final CasesScreen screen = new CasesScreen();
        // screen.setSize((Window.getClientWidth()-20)+"px",(Window.getClientHeight()-ui.main().getAbsoluteTop()-10)+"px");
        // screen.setWindow(window);
        // ui.main().add(screen);
        // //Window.addResizeHandler(new ResizeHandler() {
        // //@Override
        // //public void onResize(ResizeEvent event) {
        // //screen.setSize((Window.getClientWidth()-20)+"px",(Window.getClientHeight()-ui.main().getAbsoluteTop()-10)+"px");
        // //}
        // //});
        // }catch(Exception e) {
        // e.printStackTrace();
        // Window.alert(e.getMessage());
        // }
        // }
        // });

        ui.navigation().dataView().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ui.main().clear();
                DataViewScreen screen = new DataViewScreen();
                screen.setWindow(window);
                ui.main().add(screen);
            }
        });

        ui.navigation().emailNotification().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ui.main().clear();
                EmailNotificationScreen screen = new EmailNotificationScreen();
                screen.setWindow(window);
                ui.main().add(screen);
            }
        });

        ui.navigation().changePassword().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                try {
                    Window.open("https://www.shl.uiowa.edu/pwm", "password", null);
                } catch (Throwable e) {
                    e.printStackTrace();
                    Window.alert(e.getMessage());
                }
            }
        });

        ui.navigation().logout().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ui.popup();
            }
        });

        /*
         * open the message of the day on login
         */
        messageScreen = new MessageScreen();
        messageScreen.setWindow(window);
        try {
            messageOfTheDay = MessageService.get().getMessage();
            messageScreen.setMessage(messageOfTheDay);
            ui.main().add(messageScreen);
        } catch (Exception e) {
            Window.alert(Messages.get().error_messageFileNotFound());
        }
    }

    public static void logout() {
        try {
            OpenELISService.get().logout();
        } catch (Exception e) {
            remote().log(Level.SEVERE, e.getMessage(), e);
            Window.alert(e.getMessage());
        }

        Window.open("/portal/Portal.html", "_self", null);
    }
}
