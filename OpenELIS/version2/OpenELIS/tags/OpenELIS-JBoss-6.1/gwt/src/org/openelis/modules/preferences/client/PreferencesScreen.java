package org.openelis.modules.preferences.client;

import java.util.ArrayList;
import java.util.EnumSet;

import org.openelis.cache.CategoryCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.OptionListItem;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.event.BeforeCloseEvent;
import org.openelis.gwt.event.BeforeCloseHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.screen.Screen.State;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.AppButton.ButtonState;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.manager.Preferences;
import org.openelis.meta.PreferencesMeta;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;

public class PreferencesScreen extends Screen {
	
	AppButton         updateButton,commitButton,abortButton; 
	Dropdown<String>  defaultPrinter,defaultBarCodePrinter;
	Dropdown<Integer> location;
	Preferences       prefs;
	
	public PreferencesScreen() throws Exception {
		super((ScreenDefInt)GWT.create(PreferencesDef.class));
        service = new ScreenService("controller?service=org.openelis.modules.preferences.server.PreferencesService");
        
        DeferredCommand.addCommand(new Command() {			
			@Override
			public void execute() {
				postConstructor();
			}
		});
	}
	
	private void postConstructor() {
		try {
			prefs = Preferences.userRoot();
		}catch(Exception e) {
			e.printStackTrace();
		}
		initialize();
		initializeDropdowns();
		setState(State.DEFAULT);
		DataChangeEvent.fire(this);
	}
	
	private void initialize() {
        updateButton = (AppButton)def.getWidget("update");
        addScreenHandler(updateButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                update();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                updateButton.enable(EnumSet.of(State.DISPLAY,State.DEFAULT).contains(event.getState()));
                if (event.getState() == State.UPDATE)
                    updateButton.setState(ButtonState.LOCK_PRESSED);
            }
        });

        commitButton = (AppButton)def.getWidget("commit");
        addScreenHandler(commitButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                commit();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                commitButton.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                           .contains(event.getState()));
            }
        });

        abortButton = (AppButton)def.getWidget("abort");
        addScreenHandler(abortButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                abort();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                abortButton.enable(EnumSet.of(State.UPDATE).contains(event.getState()));
            }
        });
        
        defaultPrinter = (Dropdown<String>)def.getWidget(PreferencesMeta.getDefaultPrinter());
        addScreenHandler(defaultPrinter, new ScreenEventHandler<String>() {
        	public void onDataChange(DataChangeEvent event) {
        		defaultPrinter.setValue(prefs.get("default_printer", null));
        	}
        	public void onStateChange(StateChangeEvent<State> event) {
        		defaultPrinter.enable(EnumSet.of(State.UPDATE).contains(event.getState()));
        	}
        	public void onValueChange(ValueChangeEvent<String> event) {
        		prefs.put("default_printer",event.getValue());
        	}
        });
        
        defaultBarCodePrinter = (Dropdown<String>)def.getWidget(PreferencesMeta.getDefaultBarCodePrinter());
        addScreenHandler(defaultBarCodePrinter, new ScreenEventHandler<String>() {
        	public void onDataChange(DataChangeEvent event) {
        		defaultBarCodePrinter.setValue(prefs.get("default_bar_code_printer", null));
        	}
        	public void onStateChange(StateChangeEvent event) {
        		defaultBarCodePrinter.enable(EnumSet.of(State.UPDATE).contains(event.getState()));
        	}
        	public void onValueChange(ValueChangeEvent<String> event) {
        		prefs.put("default_bar_code_printer",event.getValue());
        	}
        });
        
        location = (Dropdown<Integer>)def.getWidget(PreferencesMeta.getLocation());
        addScreenHandler(location, new ScreenEventHandler<Integer>() {
        	public void onDataChange(DataChangeEvent event) {
        		location.setValue(prefs.getInt("location", -1));
        	}
        	public void onStateChange(StateChangeEvent<State> event) {
        		location.enable(EnumSet.of(State.UPDATE).contains(event.getState()));
        	}
        	public void onValueChange(ValueChangeEvent<Integer> event) {
        		prefs.putInt("location", event.getValue());
        	}
        });
        
        window.addBeforeClosedHandler(new BeforeCloseHandler<ScreenWindow>() {
            public void onBeforeClosed(BeforeCloseEvent<ScreenWindow> event) {                
                if (EnumSet.of(State.ADD, State.UPDATE).contains(state)) {
                    event.cancel();
                    window.setError(consts.get("mustCommitOrAbort"));
                }
            }
        });
	}
	
	private void initializeDropdowns() {
		ArrayList<TableDataRow> model;
		ArrayList<DictionaryDO> list;
		ArrayList<OptionListItem> options = null;
		TableDataRow row;
		
		model = new ArrayList<TableDataRow>();
		try {
			options = service.callList("getPrinters","pdf");
			
			for(OptionListItem item : options)
				model.add(new TableDataRow(item.getKey(),item.getLabel()));

		}catch(Exception e) {
			e.printStackTrace();
		}
		
		defaultPrinter.setModel(model);
		
		model = new ArrayList<TableDataRow>();
		try {
			options = service.callList("getPrinters","zpl");
		
			for(OptionListItem item : options)
				model.add(new TableDataRow(item.getKey(),item.getLabel()));
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		defaultBarCodePrinter.setModel(model);
		
		model = new ArrayList<TableDataRow>();
        list = CategoryCache.getBySystemName("laboratory_location");
        model.add(new TableDataRow(-1,""));
        for (DictionaryDO d : list) {
            row = new TableDataRow(d.getId(), d.getEntry());
            row.enabled = ("Y".equals(d.getIsActive()));
            model.add(row);
        }
        
        location.setModel(model);
	}
	
    protected void update() {
        try {
            setState(State.UPDATE);
            setFocus(defaultPrinter);
        } catch (Exception e) {
            Window.alert(e.getMessage());
        }
        window.clearStatus();
    }

    protected void commit() {
        setFocus(null);

        if ( !validate()) {
            window.setError(consts.get("correctErrors"));
            return;
        }

        if (state == State.UPDATE) {
            window.setBusy(consts.get("updating"));
            try {
            	prefs.flush();
                setState(State.DISPLAY);
                DataChangeEvent.fire(this);
                window.setDone(consts.get("updatingComplete"));
            } catch (ValidationErrorsList e) {
                showErrors(e);
            } catch (Exception e) {
                Window.alert("commitUpdate(): " + e.getMessage());
                window.clearStatus();
            }
        }
    }

    protected void abort() {
        setFocus(null);
        clearErrors();
        window.setBusy(consts.get("cancelChanges"));

        if (state == State.UPDATE) {
            try {
                prefs = Preferences.userRoot();
                setState(State.DISPLAY);
                DataChangeEvent.fire(this);
            } catch (Exception e) {
                Window.alert(e.getMessage());
            }
            window.setDone(consts.get("updateAborted"));
        } else {
            window.clearStatus();
        }
    }

}
