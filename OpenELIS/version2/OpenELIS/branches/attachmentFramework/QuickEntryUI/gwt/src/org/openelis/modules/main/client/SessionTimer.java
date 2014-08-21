package org.openelis.modules.main.client;

import static org.openelis.modules.main.client.Logger.remote;

import java.util.Date;
import java.util.logging.Level;

import org.openelis.constants.OpenELISConstants;
import org.openelis.ui.common.Datetime;
import org.openelis.gwt.widget.Confirm;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class SessionTimer {

    private Confirm             timeoutPopup;
    private static Timer        timeoutTimer, forceTimer;
    private static int          SESSION_TIMEOUT = 1000 * 60 * 30, // Time to allow before asking to extend/logout
                                FORCE_TIMEOUT = 1000 * 60,        // Time allowed to answer extend/logout before forcing logout
                                CHECK_TIMEOUT = 1000 * 60 * 5;    // How often to poll the server for last access
    
    private HandlerRegistration closeHandler;
    private OpenELISConstants consts = GWT.create(OpenELISConstants.class);

    public static void start() {
        new SessionTimer();
    }

    private SessionTimer() {
        /*
         * add session timeout dialog box and timers
         */
        timeoutPopup = new Confirm(Confirm.Type.WARN,
                                   consts.timeoutHeader(),
                                   consts.timeoutWarning(),
                                   consts.timeoutExtendTime(),
                                   consts.timeoutLogout());
        timeoutPopup.addSelectionHandler(new SelectionHandler<Integer>() {
            public void onSelection(SelectionEvent<Integer> event) {
                if (event.getSelectedItem() == 0) {
                    forceTimer.cancel();
                    resetServerTimeout();
                    resetTimeout();
                } else {
                    logout();
                }

            }
        });

        /*
         * if they don't answer the dialog box, we are going to log them out
         * automatically
         */
        forceTimer = new Timer() {
            public void run() {
                logout();
            }
        };

        /*
         * timer that goes off regularly to ask the server for the last access
         * time
         */
        timeoutTimer = new Timer() {
            public void run() {
                checkLastAccess();
            }
        };

        /*
         * Handler that catches the user closing the browser or navigating away
         * and logs them out.
         */
        closeHandler = Window.addWindowClosingHandler(new Window.ClosingHandler() {
            public void onWindowClosing(ClosingEvent event) {
                logout();
            }
        });
        
        OpenELISService.get().keepAlive(new AsyncCallback<Void>() {

            @Override
            public void onFailure(Throwable caught) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void onSuccess(Void result) {
                // TODO Auto-generated method stub
                
            }
            
        });

        resetTimeout();
    }

    /**
     * Check the last time the server was accessed
     */
    private void checkLastAccess() {
        OpenELISService.get().getLastAccess(new AsyncCallback<Datetime>() {
            public void onSuccess(Datetime result) {
                Datetime check;
                
                check = Datetime.getInstance(Datetime.YEAR,
                                             Datetime.MINUTE,
                                             new Date(new Date().getTime() - SESSION_TIMEOUT));

                if (result.before(check)) {
                    forceTimer.schedule(FORCE_TIMEOUT);
                    timeoutPopup.show();
                } else
                    resetTimeout();
            }

            public void onFailure(Throwable caught) {
                remote().log(Level.SEVERE,caught.getMessage(),caught);
                Window.alert(caught.getMessage());
            }
        });
    }

    /**
     * ping the server so the session does not expire
     */
    private void resetServerTimeout() {
        OpenELISService.get().keepAlive(new AsyncCallback<Void>() {
            public void onSuccess(Void result) {

            }

            public void onFailure(Throwable caught) {
                remote().log(Level.SEVERE,caught.getMessage(),caught);
                Window.alert(caught.getMessage());
            }
        });
    }

    /**
     * resets the timeout timer to check the server
     */
    public void resetTimeout() {
        timeoutTimer.schedule(CHECK_TIMEOUT);
    }

    /**
     * logout the user
     */
    private void logout() {
        //
        // close the handler so we don't get called again
        //
        if (closeHandler == null)
            closeHandler.removeHandler();

        try {
            OpenELISService.get().logout();
        } catch (Exception e) {
            remote().log(Level.SEVERE,e.getMessage(),e);
            Window.alert(e.getMessage());
        }

        Window.open("/openelis/OpenELIS.html", "_self", null);
    }

}
