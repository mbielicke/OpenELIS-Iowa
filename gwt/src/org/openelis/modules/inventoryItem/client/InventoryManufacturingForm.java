package org.openelis.modules.inventoryItem.client;

import org.openelis.gwt.common.data.deprecated.AbstractField;
import org.openelis.gwt.common.data.deprecated.StringField;
import org.openelis.gwt.common.deprecated.Form;

import com.google.gwt.xml.client.Node;

public class InventoryManufacturingForm  extends Form<Integer> {
    private static final long serialVersionUID = 1L;
    
    public StringField manufacturingText;
    public Integer id;
    
    public InventoryManufacturingForm() {
        manufacturingText = new StringField("manufacturingText");
    }
    
    public InventoryManufacturingForm(Node node){
        this();
        createFields(node);
    }
    
    public InventoryManufacturingForm(String key) {
        this();
        this.key = key;
    }
    
    public AbstractField[] getFields() {
        return new AbstractField[] {
                                    manufacturingText
        };
    }
}