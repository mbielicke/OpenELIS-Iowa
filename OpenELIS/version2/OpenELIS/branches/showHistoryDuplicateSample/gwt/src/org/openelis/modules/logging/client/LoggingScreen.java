package org.openelis.modules.logging.client;

import static org.openelis.modules.main.client.Logger.getLogPanel;
import static org.openelis.modules.main.client.Logger.logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openelis.ui.screen.Screen;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.CheckBox;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.WindowInt;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.logging.client.SimpleRemoteLogHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import org.openelis.ui.messages.Messages;

/**
 * This screen will give the users a chance to view the logs output by the
 * Application in a window in the browser. They can also change the log level.
 * 
 */
public class LoggingScreen extends Screen {
    @UiTemplate("Logging.ui.xml")
    interface LoggingUiBinder extends UiBinder<Widget, LoggingScreen> {};

    public static final LoggingUiBinder     uiBinder = GWT.create(LoggingUiBinder.class);

    @UiField
    protected Dropdown<String>              logLevel;
    @UiField
    protected Button                        clearLog;
    @UiField
    protected CheckBox                      remoteSwitch;
    @UiField
    protected ScrollPanel                   logContainer;

    protected HasWidgets                    logPanel;

    protected static SimpleRemoteLogHandler remoteLogger;

    /**
     * No arg-constructor
     */
    public LoggingScreen(WindowInt window) {
        setWindow(window);
        
        initWidget(uiBinder.createAndBindUi(this));
        window.setContent(this);

        initialize();
        initializeDropdowns();

        logPanel = (HasWidgets)getLogPanel();

        logContainer.setWidget((IsWidget)logPanel);
    }

    /**
     * Method to initialize widgets used in the screen.
     */
    private void initialize() {
        logLevel.setEnabled(true);

        logLevel.addValueChangeHandler(new ValueChangeHandler<String>() {
            public void onValueChange(ValueChangeEvent<String> event) {
                logger().setLevel(Level.parse( (event.getValue())));
            }
        });

        clearLog.setEnabled(true);

        clearLog.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                logPanel.clear();
            }
        });

        remoteSwitch.setEnabled(true);

        remoteSwitch.addValueChangeHandler(new ValueChangeHandler<String>() {
            public void onValueChange(ValueChangeEvent<String> event) {
                if ("Y".equals(event.getValue())) {
                    if (remoteLogger == null)
                        remoteLogger = new SimpleRemoteLogHandler();

                    logger().addHandler(remoteLogger);
                } else {
                    logger().removeHandler(remoteLogger);
                }
            }
        });

        if (remoteLogger != null && Arrays.asList(logger().getHandlers()).contains(remoteLogger))
            remoteSwitch.setValue("Y");
    }

    /**
     * Method initializes dropdown model and sets value to the current log level
     * of Application.logger
     */
    private void initializeDropdowns() {
        ArrayList<Item<String>> model;
        Logger logger;

        model = new ArrayList<Item<String>>();

        model.add(new Item<String>(Level.SEVERE.toString(), Messages.get().log_severe()));
        model.add(new Item<String>(Level.WARNING.toString(), Messages.get().log_warning()));
        model.add(new Item<String>(Level.INFO.toString(), Messages.get().log_info()));
        model.add(new Item<String>(Level.CONFIG.toString(), Messages.get().log_config()));
        model.add(new Item<String>(Level.FINE.toString(), Messages.get().log_fine()));
        model.add(new Item<String>(Level.FINER.toString(), Messages.get().log_finer()));
        model.add(new Item<String>(Level.FINEST.toString(), Messages.get().log_finest()));
        model.add(new Item<String>(Level.ALL.toString(), Messages.get().log_all()));

        logLevel.setModel(model);

        logger = logger();
        while (logger.getLevel() == null)
            logger = logger.getParent();

        logLevel.setValue(logger.getLevel().toString());
    }

}
