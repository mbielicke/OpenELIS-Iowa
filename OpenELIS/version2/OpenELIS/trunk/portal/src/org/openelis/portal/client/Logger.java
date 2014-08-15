package org.openelis.portal.client;

import java.util.logging.LogRecord;

import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.logging.client.FirebugLogHandler;
import com.google.gwt.logging.client.HasWidgetsLogHandler;
import com.google.gwt.logging.client.HtmlLogFormatter;
import com.google.gwt.logging.client.SimpleRemoteLogHandler;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.VerticalPanel;

public class Logger {
    public static HasWidgets               logPanel;
    public static java.util.logging.Logger logger;
    public static java.util.logging.Logger remote;

    static {

        HasWidgetsLogHandler handler;
        HtmlLogFormatter formatter;

        logPanel = new VerticalPanel();
        logger = java.util.logging.Logger.getLogger("openelis");
        logger.addHandler(new FirebugLogHandler());
        logger.addHandler(new ConsoleLogHandler());
        handler = new HasWidgetsLogHandler(logPanel);
        formatter = new HtmlLogFormatter(true) {
            @Override
            protected String getHtmlPrefix(LogRecord event) {
                StringBuilder prefix = new StringBuilder();
                prefix.append("<span>");
                prefix.append("<code>");
                return prefix.toString();
            }
        };
        handler.setFormatter(formatter);
        logger.addHandler(handler);

        remote = java.util.logging.Logger.getLogger("openelis.server");
        remote.addHandler(new SimpleRemoteLogHandler());

    }

    public static HasWidgets getLogPanel() {
        return logPanel;
    }
    
    public static java.util.logging.Logger logger() {
        return logger;
    }
    
    public static java.util.logging.Logger remote() {
        return remote;
    }
}