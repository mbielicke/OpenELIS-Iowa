package org.openelis.modules.attachment.client;

import static org.openelis.modules.main.client.Logger.*;

import java.util.logging.Level;

import org.openelis.constants.Messages;
import org.openelis.modules.main.client.OpenELIS;
import org.openelis.modules.main.client.ScreenBus;
import org.openelis.modules.main.client.event.ShowScreenHandler;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.Window;

public class AttachmentEntry implements EntryPoint, ShowScreenHandler {

    @Override
    public void onModuleLoad() {
        ScreenBus.get().addHandler(ScreenBus.ATTACHMENT, this);
    }

    @Override
    public void showScreen() {
        GWT.runAsync(new RunAsyncCallback() {
            public void onSuccess() {
                try {
                    addScreen();
                } catch (Throwable e) {
                    remote().log(Level.SEVERE, e.getMessage(), e);
                    Window.alert(e.getMessage());
                }
            }

            public void onFailure(Throwable caught) {
                remote().log(Level.SEVERE, caught.getMessage(), caught);
                Window.alert(caught.getMessage());
            }
        });
    }
    
    public AttachmentScreenUI addScreen() throws Exception {
        org.openelis.ui.widget.Window window;
        AttachmentScreenUI screen;

        window = new org.openelis.ui.widget.Window();
        screen = new AttachmentScreenUI(window);
        initializeWindow(window, screen);
        OpenELIS.getBrowser().addWindow(window, "attachment");
        
        return screen;
    }
    
    public org.openelis.ui.widget.Window addTRFScreen(TRFAttachmentScreenUI screen, String key) throws Exception {
        org.openelis.ui.widget.Window window;
        
        window = new org.openelis.ui.widget.Window();
        window.setName(Messages.get().trfAttachment_dataEntryTRFAttachment());
        window.setSize("670px", "520px");
        screen.setWindow(window);
        window.setContent(screen);
        OpenELIS.getBrowser().addWindow(window, key);
        
        return window;
    }

    public void initializeWindow(org.openelis.ui.widget.Window window, AttachmentScreenUI screen) {
        window.setName(Messages.get().attachment_attachment());
        window.setSize("782px", "521px");
        window.setContent(screen);
    }
}