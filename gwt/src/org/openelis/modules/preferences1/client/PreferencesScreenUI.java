package org.openelis.modules.preferences1.client;

import static org.openelis.modules.main.client.Logger.logger;
import static org.openelis.ui.screen.State.DEFAULT;

import java.util.ArrayList;
import java.util.logging.Level;

import org.openelis.cache.CategoryCache;
import org.openelis.constants.Messages;
import org.openelis.domain.DictionaryDO;
import org.openelis.manager.Preferences1;
import org.openelis.meta.PreferencesMeta;
import org.openelis.ui.common.OptionListItem;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.WindowInt;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

public class PreferencesScreenUI extends Screen {

    @UiTemplate("Preferences.ui.xml")
    interface PreferencesUiBinder extends UiBinder<Widget, PreferencesScreenUI> {
    };

    public static final PreferencesUiBinder uiBinder = GWT.create(PreferencesUiBinder.class);

    private Preferences1                    prefs;

    @UiField
    protected Dropdown<String>              defaultPrinter, defaultBarCodePrinter;

    @UiField
    protected Dropdown<Integer>             location;

    @UiField
    protected Button                        okButton, cancelButton;

    private PreferencesService1Impl         service  = PreferencesService1Impl.INSTANCE;

    public PreferencesScreenUI(WindowInt window) throws Exception {
        setWindow(window);

        try {
            CategoryCache.getBySystemNames("laboratory_location");
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
            window.close();
        }
        try {
            prefs = service.userRoot();
        } catch (Exception error) {
            logger.log(Level.SEVERE, error.getMessage(), error);
        }
        initWidget(uiBinder.createAndBindUi(this));

        initialize();
        setState(DEFAULT);
        fireDataChange();

        logger.fine("Preferences Screen Opened");
    }

    private void initialize() {
        ArrayList<Item<Integer>> model;
        ArrayList<Item<String>> smodel;
        ArrayList<DictionaryDO> list;
        ArrayList<OptionListItem> options;
        Item<Integer> row;

        addScreenHandler(defaultPrinter,
                         PreferencesMeta.getDefaultPrinter(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 defaultPrinter.setValue(prefs.get("default_printer", null));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 prefs.put("default_printer", event.getValue());
                                 okButton.setEnabled(true);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? defaultBarCodePrinter : cancelButton;
                             }
                         });

        addScreenHandler(defaultBarCodePrinter,
                         PreferencesMeta.getDefaultBarCodePrinter(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 defaultBarCodePrinter.setValue(prefs.get("default_bar_code_printer",
                                                                          null));
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 prefs.put("default_bar_code_printer", event.getValue());
                                 okButton.setEnabled(true);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? location : defaultPrinter;
                             }
                         });

        addScreenHandler(location, PreferencesMeta.getLocation(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                location.setValue(prefs.getInt("location", -1));
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                prefs.putInt("location", event.getValue());
                okButton.setEnabled(true);
            }

            public Widget onTab(boolean forward) {
                finishEditing();
                return forward ? okButton : defaultBarCodePrinter;
            }
        });

        addScreenHandler(okButton, "okButton", new ScreenHandler<Integer>() {
            public Widget onTab(boolean forward) {
                return forward ? cancelButton : location;
            }
        });

        addScreenHandler(cancelButton, "cancelButton", new ScreenHandler<Integer>() {
            public Widget onTab(boolean forward) {
                return forward ? defaultPrinter : okButton;
            }
        });

        smodel = new ArrayList<Item<String>>();
        try {
            options = PrinterService1Impl.INSTANCE.getPrinters("pdf");

            for (OptionListItem item : options)
                smodel.add(new Item<String>(item.getKey(), item.getLabel()));

        } catch (Exception error) {
            logger.log(Level.SEVERE, error.getMessage(), error);
        }

        defaultPrinter.setModel(smodel);

        smodel = new ArrayList<Item<String>>();
        try {
            options = PrinterService1Impl.INSTANCE.getPrinters("zpl");

            for (OptionListItem item : options)
                smodel.add(new Item<String>(item.getKey(), item.getLabel()));
        } catch (Exception error) {
            logger.log(Level.SEVERE, error.getMessage(), error);
        }

        defaultBarCodePrinter.setModel(smodel);

        model = new ArrayList<Item<Integer>>();
        list = CategoryCache.getBySystemName("laboratory_location");
        for (DictionaryDO d : list) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled("Y".equals(d.getIsActive()));
            model.add(row);
        }

        location.setModel(model);
    }

    @UiHandler("okButton")
    protected void ok(ClickEvent event) {
        commit();
    }

    @UiHandler("cancelButton")
    protected void cancel(ClickEvent event) {
        window.close();
    }

    protected void commit() {
        Validation validation;

        finishEditing();
        validation = validate();

        switch (validation.getStatus()) {
            case WARNINGS:
                break;
            case FLAGGED:
                /*
                 * some part of the screen has some operation that needs to be
                 * completed before committing the data
                 */
                return;
            case ERRORS:
                setError(Messages.get().gen_correctErrors());
                return;
        }

        setBusy(Messages.get().updating());
        try {
            service.flush(prefs);
            window.close();
        } catch (ValidationErrorsList e) {
            showErrors(e);
        } catch (Exception e) {
            Window.alert("commitUpdate(): " + e.getMessage());
            clearStatus();
        }
    }
}
