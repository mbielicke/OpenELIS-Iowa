package org.openelis.web.modules.followup.client;

import org.openelis.domain.CaseTagDO;
import org.openelis.meta.CaseTagMeta;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.TextArea;
import org.openelis.ui.widget.TextBox;
import org.openelis.ui.widget.calendar.Calendar;
import org.openelis.web.cache.UserCache;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;

public class CallBackScreen extends Screen {
    
    @UiTemplate("CallBack.ui.xml")
    interface CallBackUiBinder extends UiBinder<Widget, CallBackScreen>{};
    public static final CallBackUiBinder uiBinder = GWT.create(CallBackUiBinder.class);
    
    @UiField
    protected TextBox<String> user;
    
    @UiField
    protected Calendar        created,reminder;
    
    @UiField
    protected TextArea        note;
    
    @UiField
    protected Button          ok,cancel;
    
    CaseTagDO                 data;
    
    public CallBackScreen() {
        initWidget(uiBinder.createAndBindUi(this));
        initialize();
    }
    
    private void initialize() {
        
        addScreenHandler(user, CaseTagMeta.getSystemUserId(), new ScreenHandler<String>() {
            @Override
            public void onDataChange(DataChangeEvent event) {
                try {
                    user.setValue(UserCache.getSystemUser(data.getSystemUserId()).getLoginName());
                }catch(Exception e) {
                    
                }
            }
        });
        
        addScreenHandler(created, CaseTagMeta.getCreatedDate(), new ScreenHandler<Datetime>() {
            @Override
            public void onDataChange(DataChangeEvent event) {
                created.setValue(data.getCreatedDate());
            } 
           
            @Override
            public void onValueChange(ValueChangeEvent<Datetime> event) {
                data.setCreatedDate(event.getValue());
            }
           
            @Override
            public void onStateChange(StateChangeEvent event) { 
                created.setEnabled(true);
            }
            
            @Override
            public Widget onTab(boolean forward) {
                return forward ? reminder : note;
            }
        });
        
        addScreenHandler(reminder, CaseTagMeta.getReminderDate(), new ScreenHandler<Datetime>() {
            @Override
            public void onDataChange(DataChangeEvent event) {
                reminder.setValue(data.getReminderDate());
            }
            @Override
            public void onValueChange(ValueChangeEvent<Datetime> event) {
                data.setReminderDate(event.getValue());
            }
            @Override
            public void onStateChange(StateChangeEvent event) {
                reminder.setEnabled(true);
            }
            @Override
            public Widget onTab(boolean forward) {
                return forward ? note : created;
            }
        });
        
        addScreenHandler(note, CaseTagMeta.getNote(), new ScreenHandler<String>() {
            @Override
            public void onDataChange(DataChangeEvent event) {
                note.setValue(data.getNote());
            } 
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                data.setNote(event.getValue());
            }
            @Override
            public void onStateChange(StateChangeEvent event) {
                note.setEnabled(true);
            }
            @Override
            public Widget onTab(boolean forward) {
                return forward ? created : reminder;
            }
        });
        
        
        
    }

}
