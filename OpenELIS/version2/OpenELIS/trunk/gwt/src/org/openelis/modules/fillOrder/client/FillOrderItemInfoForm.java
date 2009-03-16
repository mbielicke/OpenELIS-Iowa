package org.openelis.modules.fillOrder.client;

import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.TreeField;
import org.openelis.metamap.FillOrderMetaMap;

import com.google.gwt.xml.client.Node;

public class FillOrderItemInfoForm extends Form{
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
    
    public FillOrderItemInfoForm() {
       FillOrderMetaMap meta = new FillOrderMetaMap();
       fields.put(meta.getRequestedBy(), requestedBy = new StringField());
       fields.put(meta.getCostCenterId(), costCenterId = new DropDownField<Integer>());
       fields.put("orderItemsTree", displayOrderItemsTree = new TreeField());
       fields.put("orderShippingNotes", orderShippingNotes = new StringField());
       fields.put(meta.ORDER_ORGANIZATION_META.ADDRESS.getMultipleUnit(), multUnit = new StringField());
       fields.put(meta.ORDER_ORGANIZATION_META.ADDRESS.getStreetAddress(), streetAddress = new StringField());
       fields.put(meta.ORDER_ORGANIZATION_META.ADDRESS.getCity(), city = new StringField());
       fields.put(meta.ORDER_ORGANIZATION_META.ADDRESS.getState(), state = new StringField());
       fields.put(meta.ORDER_ORGANIZATION_META.ADDRESS.getZipCode(), zipCode = new StringField());
       originalOrderItemsTree = new TreeField();
   }
   
   public FillOrderItemInfoForm(Node node) {
       this();
       createFields(node);
   }
}