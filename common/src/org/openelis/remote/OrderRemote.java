package org.openelis.remote;

import java.util.HashMap;
import java.util.List;

import javax.ejb.Remote;

import org.openelis.domain.BillToReportToDO;
import org.openelis.domain.NoteDO;
import org.openelis.domain.OrderAddAutoFillDO;
import org.openelis.domain.OrderDO;

@Remote
public interface OrderRemote {
    public static final String  INTERNAL          = "internal",
                                EXTERNAL          = "external",
                                KITS              = "kits";
    
    //method to return order record
    public OrderDO getOrder(Integer orderId, String orderType);
    
    //method to unlock entity and return order record
    public OrderDO getOrderAndUnlock(Integer orderId, String orderType);
    
    //method to lock entity and return order
    public OrderDO getOrderAndLock(Integer orderId, String orderType) throws Exception;
    
    //commit a change to order, or insert a new order record
    public Integer updateOrder(OrderDO orderDO, String orderType, List items, NoteDO customerNoteDO, NoteDO orderShippingNotes) throws Exception;
    
    //method to return customer notes
    public NoteDO getCustomerNote(Integer orderId);
    
    //method to return order/shipping notes
    public NoteDO getOrderShippingNote(Integer orderId);
    
    //method to return order items
    public List getOrderItems(Integer orderId, boolean withLocation);
    
    //method to return receipts
    public List getOrderReceipts(Integer orderId);
    
    //method to return report to bill to addresses
    public BillToReportToDO getBillToReportTo(Integer orderId);
    
    //method to query for orders
     public List query(HashMap fields, int first, int max, String orderType) throws Exception;
     
     //a way for the servlet to get the system user id
     public Integer getSystemUserId();
     
     public OrderAddAutoFillDO getAddAutoFillValues() throws Exception;
     
     //method to validate the fields before the backend updates it in the database
     public List validateForUpdate(OrderDO orderDO, String orderType, List items);
     
     //method to validate the fields before the backend updates it in the database
     public List validateForAdd(OrderDO orderDO, String orderType, List items);
}
