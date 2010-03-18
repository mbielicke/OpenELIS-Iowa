package org.openelis.modules.orderFill.client;


public class OrderFillItemInfoForm {
}
/*
private static final long serialVersionUID = 1L;

    public StringField requestedBy;
    public DropDownField<Integer> costCenterId;
    public StringField orderShippingNotes;
    
    public StringField multUnit;
    public StringField streetAddress;
    public StringField city;
    public StringField state;
    public StringField zipCode;
    
    public TreeField displayOrderItemsTree;
    public TreeField originalOrderItemsTree;
    public boolean changed = false;
    
    public TableDataModel<TableDataRow<Integer>> tableData;
    
    public FillOrderItemInfoForm() {
       FillOrderMetaMap meta = new FillOrderMetaMap();
       requestedBy = new StringField(meta.getRequestedBy());
       costCenterId = new DropDownField<Integer>(meta.getCostCenterId());
       displayOrderItemsTree = new TreeField("orderItemsTree");
       orderShippingNotes = new StringField("orderShippingNotes");
       multUnit = new StringField(meta.ORDER_ORGANIZATION_META.ADDRESS.getMultipleUnit());
       streetAddress = new StringField(meta.ORDER_ORGANIZATION_META.ADDRESS.getStreetAddress());
       city = new StringField(meta.ORDER_ORGANIZATION_META.ADDRESS.getCity());
       state = new StringField(meta.ORDER_ORGANIZATION_META.ADDRESS.getState());
       zipCode = new StringField(meta.ORDER_ORGANIZATION_META.ADDRESS.getZipCode());
       originalOrderItemsTree = new TreeField("originalTree");
   }
   
   public FillOrderItemInfoForm(Node node) {
       this();
       createFields(node);
   }
   
   public FillOrderItemInfoForm(String key) {
       this();
       this.key = key;
   }
   
   public AbstractField[] getFields() {
       return new AbstractField[] {
                                   requestedBy,
                                   costCenterId,
                                   displayOrderItemsTree,
                                   orderShippingNotes,
                                   multUnit,
                                   streetAddress,
                                   city,
                                   state,
                                   zipCode
       };
   }
*/