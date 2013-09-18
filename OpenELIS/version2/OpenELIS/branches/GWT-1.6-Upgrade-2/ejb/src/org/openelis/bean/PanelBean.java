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
import javax.ejb.EJB;
import javax.ejb.EJBs;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.PanelDO;
import org.openelis.domain.PanelItemDO;
import org.openelis.entity.Panel;
import org.openelis.entity.PanelItem;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.RPCException;
import org.openelis.local.LockLocal;
import org.openelis.metamap.PanelMetaMap;
import org.openelis.remote.PanelRemote;
import org.openelis.util.QueryBuilder;
import org.openelis.utils.GetPage;

@Stateless
@EJBs({
    @EJB(name="ejb/Lock",beanInterface=LockLocal.class)
})
@SecurityDomain("openelis")
public class PanelBean implements PanelRemote {
    
    @PersistenceContext(name = "openelis")
    private EntityManager manager;

    @Resource
    private SessionContext ctx;
    
    private LockLocal lockBean;
    
    private static final PanelMetaMap PanelMeta = new PanelMetaMap(); 
    

    @PostConstruct
    private void init()
    {
        lockBean =  (LockLocal)ctx.lookup("ejb/Lock");
        
    }
    
    public PanelDO getPanel(Integer panelId) {
        Query query = manager.createNamedQuery("Panel.PanelById");
        query.setParameter("id", panelId);
        PanelDO panelDO = (PanelDO) query.getSingleResult();
        return panelDO;
    }

    public PanelDO getPanelAndLock(Integer panelId, String session) throws Exception {
        Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "panel");
        lockBean.getLock((Integer)query.getSingleResult(), panelId);
        return getPanel(panelId);
    }

    public PanelDO getPanelAndUnlock(Integer panelId, String session) {
        Query unlockQuery = manager.createNamedQuery("getTableId");
        unlockQuery.setParameter("name", "panel");
        lockBean.giveUpLock((Integer)unlockQuery.getSingleResult(), panelId);
        return getPanel(panelId);
    }

    public List<PanelItemDO> getPanelItems(Integer panelId) {
        Query query = manager.createNamedQuery("PanelItem.PanelItemsByPanelId");
        query.setParameter("id", panelId);
        List<PanelItemDO> list = query.getResultList();
        return list;
    }

    public List getTestMethodNames() {
        Query query = manager.createNamedQuery("Test.TestIdNameMethodSectionNames");
        query.setParameter("isActive", "Y");
        List testMethodList = query.getResultList();
        return testMethodList;
    }

    public List query(HashMap fields, int first, int max) throws Exception {
        StringBuffer sb = new StringBuffer();
        QueryBuilder qb = new QueryBuilder();
        qb.setMeta(PanelMeta);
        qb.setSelect("distinct new org.openelis.domain.IdNameDO(" + PanelMeta.getId()
                     + ", "
                     + PanelMeta.getName()                     
                     + ") ");
        
        qb.addWhere(fields);

        qb.setOrderBy(PanelMeta.getName());
        
        sb.append(qb.getEJBQL());                

        Query query = manager.createQuery(sb.toString());

        if (first > -1 && max > -1)
            query.setMaxResults(first + max);

        // ***set the parameters in the query
        qb.setQueryParams(query);

        List returnList = GetPage.getPage(query.getResultList(), first, max);

        if (returnList == null)
            throw new LastPageException();
        else
            return returnList;
    }

    public Integer updatePanel(PanelDO panelDO,
                               List<PanelItemDO> panelItemDOList) throws Exception {
     try {  
        Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "panel");
        Integer panelReferenceId = (Integer)query.getSingleResult();
        if (panelDO.getId() != null) {
            // we need to call lock one more time to make sure their lock
            // didn't expire and someone else grabbed the record
            lockBean.getLock(panelReferenceId, panelDO.getId());
        }
        
        manager.setFlushMode(FlushModeType.COMMIT);
        Panel panel = null;
        
        if(panelDO.getId()==null){
            panel = new Panel();
        }else {
            panel = manager.find(Panel.class,panelDO.getId());
        }
        
        List<Exception> exceptionList = new ArrayList<Exception>();                        
        
        validatePanel(exceptionList,panelDO);
        if(exceptionList.size() > 0){
            throw (RPCException)exceptionList.get(0);
        }
        
        panel.setDescription(panelDO.getDescription());
        panel.setName(panelDO.getName());
        
        if(panel.getId()==null){
            manager.persist(panel);
        }
        
        exceptionList = new ArrayList<Exception>();
        validatePanelItems(exceptionList,panelItemDOList);
        if(exceptionList.size() > 0){
            throw (RPCException)exceptionList.get(0);
        }
        
        for(int i=0; i < panelItemDOList.size();i++){
            PanelItemDO itemDO = panelItemDOList.get(i);
            if(itemDO!=null){
                PanelItem item = null;
                if(itemDO.getId()==null){
                    item =  new PanelItem();
                }else{
                    item = manager.find(PanelItem.class,itemDO.getId());
                }
                if(itemDO.getDelete() && itemDO.getId() != null){                    
                    manager.remove(item);                                                     
                }else{
                    item.setTestName(itemDO.getTestName());
                    item.setMethodName(itemDO.getMethodName());
                    item.setPanelId(panel.getId());
                    item.setSortOrder(itemDO.getSortOrder());                   
                   
                if(item.getId() == null){
                        manager.persist(item);
                    }  
                }
            }
         }

        lockBean.giveUpLock(panelReferenceId, panel.getId());
        return panel.getId();
       } catch (Exception ex) {
         ex.printStackTrace();
         throw ex;
      }
    }
    
    public void deletePanel(Integer panelId)throws Exception{
       try{ 
        Query lockQuery = manager.createNamedQuery("getTableId");
        lockQuery.setParameter("name", "panel");
        Integer panelTableId = (Integer)lockQuery.getSingleResult();
        lockBean.getLock(panelTableId, panelId);
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        Panel panel = manager.find(Panel.class, panelId);
        
        Query query = manager.createNamedQuery("PanelItem.PanelItemsByPanelId");
        query.setParameter("id",panelId);
        List<PanelItemDO> list = query.getResultList();
        for(int iter = 0; iter < list.size(); iter++){
          PanelItemDO itemDO = list.get(iter);
          PanelItem item = manager.find(PanelItem.class, itemDO.getId());
           manager.remove(item);
        }
         manager.remove(panel);
        lockBean.giveUpLock(panelTableId, panelId);
       }catch(Exception ex){
           ex.printStackTrace();
           throw ex;
       } 
       
    }

    public List validateForAdd(PanelDO panelDO,
                               List<PanelItemDO> panelItemDOList) {
        
        List<Exception> exceptionList = new ArrayList<Exception>();
        validatePanel(exceptionList, panelDO);
        validatePanelItems(exceptionList, panelItemDOList);
        return exceptionList;
    }

    public List validateForUpdate(PanelDO panelDO,
                                  List<PanelItemDO> panelItemDOList) {
        List<Exception> exceptionList = new ArrayList<Exception>();
        validatePanel(exceptionList, panelDO);
        validatePanelItems(exceptionList, panelItemDOList);
        return exceptionList;
    }
    
    private void validatePanel(List<Exception> exceptionList, PanelDO panelDO){
        boolean checkUnique = true;
        if (panelDO.getName() == null || "".equals(panelDO.getName())) {
            exceptionList.add(new FieldErrorException("fieldRequiredException",
                                                      PanelMeta.getName()));    
            checkUnique = false;
        }
        
        if(checkUnique){
            Query query = manager.createNamedQuery("Panel.PanelByName");
            query.setParameter("name", panelDO.getName());
            PanelDO pdo = null;
             try{
               pdo = (PanelDO)query.getSingleResult();
             }catch(NoResultException ex){
                 ex.printStackTrace();
             } 
           if(pdo!=null){  
            if(!pdo.getId().equals(panelDO.getId())){
              exceptionList.add(new FieldErrorException("fieldUniqueException",
                                                          PanelMeta.getName()));   
            }
           } 
        }
        
    }
    
    private void validatePanelItems(List<Exception> exceptionList,List<PanelItemDO> panelItemDOList){
        
    }

}