package org.openelis.modules.completeRelease.client;

import java.util.EnumSet;

import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.Util;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.widget.CalendarLookUp;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.TextBox;
import org.openelis.manager.SampleManager;
import org.openelis.meta.SampleMeta;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Window;

public class SampleTab extends Screen {
	
	TextBox<Integer> accessionNumber, orderNumber;
	TextBox<Datetime> collectedTime;
	CalendarLookUp collectedDate,receivedDate;
	Dropdown<Integer> statusId;
	TextBox clientReference;
	
	SampleManager manager;
	
	boolean loaded;

	public SampleTab(ScreenDefInt def, ScreenWindow window) {
		setDefinition(def);
		setWindow(window);
		
		initialize();
	}
	
	public void initialize() {
        accessionNumber = (TextBox<Integer>)def.getWidget(SampleMeta.getAccessionNumber());
        addScreenHandler(accessionNumber, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                accessionNumber.setValue(Util.toString(manager.getSample().getAccessionNumber()));
            }

            public void onValueChange(final ValueChangeEvent<Integer> event) {
                try {
                    manager.getSample().setAccessionNumber(event.getValue());
                    //manager.validateAccessionNumber(manager.getSample());

                //} catch (ValidationErrorsList e) {
                 //   showErrors(e);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                accessionNumber.enable(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                              .contains(event.getState()));
                accessionNumber.setQueryMode(event.getState() == State.QUERY);

                if (EnumSet.of(State.ADD, State.UPDATE, State.QUERY).contains(event.getState()))
                    accessionNumber.setFocus(true);
            }
        });

        orderNumber = (TextBox<Integer>)def.getWidget("orderNumber");
        addScreenHandler(orderNumber, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                orderNumber.setValue(Util.toString(manager.getSample().getOrderId()));
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getSample().setOrderId(event.getValue());
                /*
                if (envOrderImport == null)
                    envOrderImport = new SampleEnvironmentalImportOrder();

                try {
                    envOrderImport.importOrderInfo(event.getValue(), manager);
                    DataChangeEvent.fire(envScreen);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
                */
            }

            public void onStateChange(StateChangeEvent<State> event) {
                orderNumber.enable(EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()));
            }
        });

        collectedDate = (CalendarLookUp)def.getWidget(SampleMeta.getCollectionDate());
        addScreenHandler(collectedDate, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                collectedDate.setValue(manager.getSample().getCollectionDate());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                manager.getSample().setCollectionDate(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                collectedDate.enable(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                            .contains(event.getState()));
                collectedDate.setQueryMode(event.getState() == State.QUERY);
            }
        });

        collectedTime = (TextBox<Datetime>)def.getWidget(SampleMeta.getCollectionTime());
        addScreenHandler(collectedTime, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {

                collectedTime.setValue(manager.getSample().getCollectionTime());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                manager.getSample().setCollectionTime(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                collectedTime.enable(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                            .contains(event.getState()));
                collectedTime.setQueryMode(event.getState() == State.QUERY);
            }
        });

        receivedDate = (CalendarLookUp)def.getWidget(SampleMeta.getReceivedDate());
        addScreenHandler(receivedDate, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                receivedDate.setValue(manager.getSample().getReceivedDate());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                manager.getSample().setReceivedDate(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                receivedDate.enable(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                           .contains(event.getState()));
                receivedDate.setQueryMode(event.getState() == State.QUERY);
            }
        });

        statusId = (Dropdown<Integer>)def.getWidget(SampleMeta.getStatusId());
        addScreenHandler(statusId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                statusId.setSelection(manager.getSample().getStatusId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getSample().setStatusId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                statusId.enable(EnumSet.of(State.QUERY).contains(event.getState()));
                statusId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        clientReference = (TextBox)def.getWidget(SampleMeta.getClientReference());
        addScreenHandler(clientReference, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                clientReference.setValue(manager.getSample().getClientReference());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getSample().setClientReference(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                clientReference.enable(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                              .contains(event.getState()));
                clientReference.setQueryMode(event.getState() == State.QUERY);
            }
        });
	}
	
    public void setData(SampleManager manager) {
        this.manager = manager;
        loaded = false;
    }

    public void draw() {
        if ( !loaded)
            DataChangeEvent.fire(this);

        loaded = true;
    }
}
