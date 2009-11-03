package org.openelis.modules.fillOrder.client;

import org.openelis.gwt.common.data.deprecated.AbstractField;
import org.openelis.gwt.common.data.deprecated.TableDataRow;
import org.openelis.gwt.common.data.deprecated.TableField;
import org.openelis.gwt.common.deprecated.Form;

import com.google.gwt.xml.client.Node;

public class FillOrderForm extends Form<Integer> {
    private static final long serialVersionUID = 1L;

    public TableField<TableDataRow<FillOrderItemInfoForm>> fillItemsTable;
    public FillOrderItemInfoForm itemInformation;
    public Integer orderPendingValue;
    
    public FillOrderForm() {
        fillItemsTable = new TableField<TableDataRow<FillOrderItemInfoForm>>("fillItemsTable");
        itemInformation = new FillOrderItemInfoForm("itemInformation");
   }
   
   public FillOrderForm(Node node) {
       this();
       createFields(node);
   }
   
   public AbstractField[] getFields() {
       return new AbstractField[] {
                                   fillItemsTable,
                                   itemInformation
       };
   }
}