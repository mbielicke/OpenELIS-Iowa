package org.openelis.modules.todo1.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;
import org.openelis.ui.screen.Screen;

public class InstrumentTabUI extends Screen {

    @UiTemplate("InstrumentTab.ui.xml")
    interface InstrumentTabUiBinder extends UiBinder<Widget, InstrumentTabUI> {
    };

    private static InstrumentTabUiBinder uiBinder = GWT.create(InstrumentTabUiBinder.class);
    protected Screen                     parentScreen;
    protected EventBus                   parentBus;
    private boolean                      loadedFromCache;

    public InstrumentTabUI(Screen parentScreen) {
        this.parentScreen = parentScreen;
        this.parentBus = parentScreen.getEventBus();
        initWidget((Widget)uiBinder.createAndBindUi(this));
        initialize();
    }

    private void initialize() {
    }

    public void onDataChange(String mySection) {
        this.loadedFromCache = false;
        draw(mySection);
    }

    public Integer getSelectedId() {
        return null;
    }

    public void draw(String loadBySection) {
    }
}
