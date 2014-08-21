package org.openelis.web.modules.followup.client;

import java.util.ArrayList;

import org.openelis.domain.EncounterDO;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.screen.Screen;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;

public class CaseHistoryTab extends Screen {
    
    @UiTemplate("CaseHistory.ui.xml")
    interface CaseHistoryUiBinder extends UiBinder<Widget, CaseHistoryTab>{};
    public static final CaseHistoryUiBinder uiBinder = GWT.create(CaseHistoryUiBinder.class);
    
    @UiField
    EncounterTable     encounters;
    
    @UiConstructor
    public CaseHistoryTab() {
        initWidget(uiBinder.createAndBindUi(this));
        
        ArrayList<EncounterDO> data = new ArrayList<EncounterDO>();
        
        EncounterDO enc = new EncounterDO();
        enc.setType("C");
        enc.setOccurred(Datetime.getInstance(Datetime.YEAR,Datetime.MINUTE));
        enc.setComments("Testing encounter table\nSecond line");
        enc.setContact1(new Integer(1));
        enc.setContact2(new Integer(2));
        enc.setContactMethod(new Integer(3));
        enc.setEnteredBy(new Integer(4));
        
        data.add(enc);
        
        enc = new EncounterDO();
        enc.setType("C");
        enc.setOccurred(Datetime.getInstance(Datetime.YEAR,Datetime.MINUTE));
        enc.setComments("2nd Testing encounter table\nSecond line");
        enc.setContact1(new Integer(16));
        enc.setContact2(new Integer(23));
        enc.setContactMethod(new Integer(43));
        enc.setEnteredBy(new Integer(45));
        
        data.add(enc);
        
        encounters.setModel(data);
    }

}
