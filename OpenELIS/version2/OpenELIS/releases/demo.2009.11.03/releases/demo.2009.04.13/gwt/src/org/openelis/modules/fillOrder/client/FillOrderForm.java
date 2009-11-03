package org.openelis.modules.fillOrder.client;

import com.google.gwt.xml.client.Node;

import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.TableDataModel;
import org.openelis.gwt.common.data.TableDataRow;
import org.openelis.gwt.common.data.TableField;

public class FillOrderForm extends Form<Integer> {
    private static final long serialVersionUID = 1L;

    public TableField<TableDataRow<FillOrderItemInfoForm>> fillItemsTable;
    public FillOrderItemInfoForm itemInformation;
    
    public TableDataModel<TableDataRow<Integer>> costCenters;
    public TableDataModel<TableDataRow<Integer>> shipFroms;
    public TableDataModel<TableDataRow<Integer>> statuses;
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