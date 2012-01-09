package org.openelis.modules.completeRelease.client;

import java.util.EnumSet;

import org.openelis.cache.DictionaryCache;
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
import org.openelis.gwt.widget.ScreenWindowInt;
import org.openelis.gwt.widget.TextBox;
import org.openelis.manager.SampleManager;
import org.openelis.meta.SampleMeta;
import org.openelis.modules.sample.client.AccessionNumberUtility;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Window;

public class SampleTab extends Screen {
	
	TextBox<Integer> orderNumber;
	TextBox<Datetime> collectedTime;
	CalendarLookUp collectedDate,receivedDate;
	Dropdown<Integer> statusId;
	TextBox accessionNumber, clientReference;
	
	SampleManager manager;
	
	boolean loaded;

    private Integer sampleReleasedId;

    protected AccessionNumberUtility accessionNumUtil;

    public SampleTab(ScreenDefInt def, ScreenWindowInt window) {
		setDefinition(def);
		setWindow(window);
		
		initialize();
		initializeDropdowns();
	}
	
	public void initialize() {
        accessionNumber = (TextBox<Integer>)def.getWidget(SampleMeta.getAccessionNumber());
        addScreenHandler(accessionNumber, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                accessionNumber.setValue(Util.toString(manager.getSample().getAccessionNumber()));
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                Integer oldNumber, val;                
                String oldNumStr;
                SampleManager quickEntryMan;

                val = event.getValue();
                if (val == null)
                    return;
                oldNumber = manager.getSample().getAccessionNumber();
                oldNumStr = Util.toString(oldNumber);                
                if (oldNumber != null) {                    
                    if (!Window.confirm(consts.get("accessionNumberEditConfirm"))) {
                        accessionNumber.setValue(oldNumStr);
                        setFocus(accessionNumber);
                        return;
                    }
                }
                try {                    
                    manager.getSample().setAccessionNumber(val);

                    if (accessionNumUtil == null)
                        accessionNumUtil = new AccessionNumberUtility();

                    quickEntryMan = accessionNumUtil.accessionNumberEntered(manager.getSample());
                    if (quickEntryMan != null)
                        throw new Exception(consts.get("quickEntryNumberExists"));
                } catch (ValidationErrorsList e) {
                    showErrors(e);
                    accessionNumber.setValue(oldNumStr);
                    manager.getSample().setAccessionNumber(oldNumber);
                    setFocus(accessionNumber);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    accessionNumber.setValue(oldNumStr);
                    manager.getSample().setAccessionNumber(oldNumber);
                    setFocus(accessionNumber);
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                accessionNumber.enable(canEdit() && EnumSet.of(State.UPDATE).contains(event.getState()));
                accessionNumber.setQueryMode(event.getState() == State.QUERY);
                
                if (EnumSet.of(State.UPDATE).contains(event.getState()))
                    accessionNumber.setFocus(true);
            }
        });

        orderNumber = (TextBox<Integer>)def.getWidget(SampleMeta.getOrderId());
        addScreenHandler(orderNumber, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                orderNumber.setValue(Util.toString(manager.getSample().getOrderId()));
            }

            public void onStateChange(StateChangeEvent<State> event) {
                orderNumber.enable(false);
                orderNumber.setQueryMode(event.getState() == State.QUERY);
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
                collectedDate.enable(canEdit() && EnumSet.of(State.UPDATE).contains(event.getState()));
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
                collectedTime.enable(canEdit() && EnumSet.of(State.UPDATE).contains(event.getState()));
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
                receivedDate.enable(canEdit() && EnumSet.of(State.UPDATE).contains(event.getState()));
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
                statusId.enable(false);
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
                clientReference.enable(canEdit() && EnumSet.of(State.UPDATE).contains(event.getState()));
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

    private void initializeDropdowns() {
        try {
            sampleReleasedId = DictionaryCache.getIdBySystemName("sample_released");
        } catch (Exception e) {
            Window.alert(e.getMessage());
            window.close();
        }
    }
    
    private boolean canEdit() {
        return (manager != null && !sampleReleasedId.equals(manager.getSample().getStatusId()));
    }
}
