package org.openelis.modules.provider.client;

import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.data.TableField;

import com.google.gwt.xml.client.Node;

public class AddressesForm extends Form {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public TableField<Integer> providerAddressTable;
    
    public AddressesForm() {
        fields.put("providerAddressTable", providerAddressTable = new TableField<Integer>());        
    }
    
    public AddressesForm(Node node) {
        this();
        createFields(node);
    }
        
}
