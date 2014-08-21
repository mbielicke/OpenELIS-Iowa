package org.openelis.modules.main.client.event;

import com.google.gwt.event.shared.GwtEvent;


public class ShowScreenEvent extends GwtEvent<ShowScreenHandler> {

    Type<ShowScreenHandler> type;
    
    public ShowScreenEvent(Type<ShowScreenHandler> type) {
        this.type = type;
    }
    
    @Override
    public Type<ShowScreenHandler> getAssociatedType() {
        return type;
    }

    @Override
    protected void dispatch(ShowScreenHandler handler) {
        handler.showScreen();
    }
 
}
