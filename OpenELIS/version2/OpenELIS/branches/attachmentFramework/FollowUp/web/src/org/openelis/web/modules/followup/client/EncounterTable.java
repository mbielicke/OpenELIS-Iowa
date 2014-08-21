package org.openelis.web.modules.followup.client;

import java.util.ArrayList;

import org.openelis.domain.EncounterDO;
import org.openelis.ui.widget.table.Row;
import org.openelis.ui.widget.table.Table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ResizeComposite;

public class EncounterTable extends ResizeComposite {
    
    @UiTemplate("EncounterTable.ui.xml")
    interface EventTableUiBinder extends UiBinder<LayoutPanel,EncounterTable>{};
    public static final EventTableUiBinder uiBinder = GWT.create(EventTableUiBinder.class);
    
    @UiField
    Table                     table;
        
    ArrayList<EncounterDO>    model;
    
    @UiConstructor
    public EncounterTable() {
        initWidget(uiBinder.createAndBindUi(this));
    }
    
    
    public void setModel(ArrayList<EncounterDO> model) {
        ArrayList<Row> rows;
        
        this.model = model;
        rows = new ArrayList<Row>();
        
        if(model != null) {
            
            for(EncounterDO enc : model) 
                rows.add(new Row(enc.getOccurred(),enc,enc.getComments()));
            
        }
        
        table.setModel(rows);
        
    }
    
    public ArrayList<EncounterDO> getModel() {
        return model;
    }
    
    public void addEncounter(EncounterDO enc) {
        
        table.addRow(new Row(enc.getOccurred(),enc,enc.getComments()));
        
    }
    
}
