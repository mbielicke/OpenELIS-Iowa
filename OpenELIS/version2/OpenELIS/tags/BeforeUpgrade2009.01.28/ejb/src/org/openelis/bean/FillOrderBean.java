/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.EJBs;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.NumberField;
import org.openelis.gwt.common.data.QueryNumberField;
import org.openelis.local.LockLocal;
import org.openelis.metamap.OrderMetaMap;
import org.openelis.persistence.CachingManager;
import org.openelis.remote.FillOrderRemote;
import org.openelis.security.domain.SystemUserDO;
import org.openelis.util.QueryBuilder;
import org.openelis.utils.GetPage;

@Stateless
@EJBs({
    @EJB(name="ejb/Lock",beanInterface=LockLocal.class)
})
@SecurityDomain("openelis")
@RolesAllowed("fillorder-select")
public class FillOrderBean implements FillOrderRemote {

    @PersistenceContext(name = "openelis")
    private EntityManager manager;
    
    @Resource
    private SessionContext ctx;
    
    private LockLocal lockBean;
    private static final OrderMetaMap OrderMap = new OrderMetaMap();
    
    @PostConstruct
    private void init()
    {
        lockBean =  (LockLocal)ctx.lookup("ejb/Lock");
    }
    
    public Integer getSystemUserId() {
        try {
            SystemUserDO systemUserDO = (SystemUserDO)CachingManager.getElement("security", ctx.getCallerPrincipal().getName()+"userdo");
            return systemUserDO.getId();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List query(HashMap fields, int first, int max) throws Exception {
        StringBuffer sb = new StringBuffer();
        QueryBuilder qb = new QueryBuilder();

        qb.setMeta(OrderMap);

        qb.setSelect("distinct new org.openelis.domain.FillOrderDO(" +
                     OrderMap.getId()+", " +
                     OrderMap.getStatusId()+", " +
                     OrderMap.getOrderedDate()+", " +
                     OrderMap.getShipFromId()+", " +
                     OrderMap.ORDER_ORGANIZATION_META.getId()+", " +
                     OrderMap.ORDER_ORGANIZATION_META.getName()+", " +
                     OrderMap.getDescription()+", " +
                     OrderMap.getNeededInDays()+", "+
                     //"("+OrderMap.getNeededInDays()+"-(" +
                            //"current_date()-date("+OrderMap.getOrderedDate()+"), "+
                     OrderMap.getRequestedBy()+", "+
                     OrderMap.getCostCenterId()+" ,"+
                     OrderMap.getIsExternal()+" ,"+
                     OrderMap.ORDER_ORGANIZATION_META.ADDRESS.getMultipleUnit()+", "+
                     OrderMap.ORDER_ORGANIZATION_META.ADDRESS.getStreetAddress()+", "+
                     OrderMap.ORDER_ORGANIZATION_META.ADDRESS.getCity()+", "+
                     OrderMap.ORDER_ORGANIZATION_META.ADDRESS.getState()+", "+
                     OrderMap.ORDER_ORGANIZATION_META.ADDRESS.getZipCode()+") ");

        //this method is going to throw an exception if a column doesnt match
        qb.addWhere(fields); 

        qb.addWhere(OrderMap.ORDER_ITEM_META.getOrderId() + " = " + OrderMap.getId());
        //qb.addWhere(OrderMap.ORDER_ORGANIZATION_META.getId() + " = " + OrderMap.getOrganizationId());
        qb.addWhere(OrderMap.getIsExternal()+"='N'");
        
        qb.setOrderBy(OrderMap.ORDER_ORGANIZATION_META.getName()+" DESC, "+OrderMap.getId());

        sb.append(qb.getEJBQL());

        Query query = manager.createQuery(sb.toString());
    
        if(first > -1 && max > -1)
         query.setMaxResults(first+max);
        
        //set the parameters in the query
        qb.setQueryParams(query);
        
        List returnList = GetPage.getPage(query.getResultList(), first, max);
        
        if(returnList == null)
         throw new LastPageException();
        else
         return returnList;
    }

    public List queryAndUnlock(HashMap fields, DataModel model, int first, int max) throws Exception {
        //try and unlock the necessary records
        unlockRecords(model);
        
        List queryResultList = query(fields, first, max);
                
        return queryResultList;
    }
    
    public List getOrderItems(Integer orderId) {
        Query query = manager.createNamedQuery("OrderItem.OrderItemsByOrderId");
        query.setParameter("id", orderId);
        
        return query.getResultList();
    }

    public List validateForProcess(List orders) {
        List exceptionList = new ArrayList();
        //all ship froms need to match
        
        //all ship tos need to match
        return null;
    }
    
    @RolesAllowed("fillorder-update")
    public List getOrderAndLock(Integer orderId) throws Exception {
        Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "order");
        lockBean.getLock((Integer)query.getSingleResult(), orderId);

        HashMap map = new HashMap();
        QueryNumberField orderField = new QueryNumberField();
        orderField.setType("integer");
        orderField.setValue(orderId.toString());
        orderField.setKey(OrderMap.getId());
        map.put(OrderMap.getId(), orderField);
        
       return query(map,0,1);
    }
    
    public List getOrderAndUnlock(Integer orderId) throws Exception{
        Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "order");
        lockBean.giveUpLock((Integer)query.getSingleResult(), orderId);

        HashMap map = new HashMap();
        QueryNumberField orderField = new QueryNumberField();
        orderField.setType("integer");
        orderField.setValue(orderId.toString());
        orderField.setKey(OrderMap.getId());
        map.put(OrderMap.getId(), orderField);
        
       return query(map,0,1);
    }

    private void unlockRecords(DataModel orders) throws Exception{
        if(orders.size() == 0)
            return;
        
        Integer orderTableId = null;
        
        Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "order");
        orderTableId = (Integer)query.getSingleResult();
        
        for(int i=0; i<orders.size(); i++){
            Integer orderId = (Integer)((NumberField)orders.get(i).getKey()).getValue();
        
            if(orderId != null)
                lockBean.giveUpLock(orderTableId, orderId);
        }
    }

    public Integer getOrderItemReferenceTableId() {
        Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "order_item");
        
        return (Integer)query.getSingleResult();
    }
}
