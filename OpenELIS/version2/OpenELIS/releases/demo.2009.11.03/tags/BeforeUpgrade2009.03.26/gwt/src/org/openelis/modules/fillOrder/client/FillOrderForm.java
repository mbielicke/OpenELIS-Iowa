package org.openelis.modules.fillOrder.client;

import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.data.TableField;

import com.google.gwt.xml.client.Node;

public class FillOrderForm extends Form{
    private static final long serialVersionUID = 1L;

    public TableField<FillOrderItemInfoRPC> fillItemsTable;
    public FillOrderItemInfoForm itemInformation;
    
    public FillOrderForm() {
       fields.put("fillItemsTable", fillItemsTable = new TableField<FillOrderItemInfoRPC>());
       fields.put("itemInformation", itemInformation = new FillOrderItemInfoForm());
   }
   
   public FillOrderForm(Node node) {
       this();
       createFields(node);
   }
}