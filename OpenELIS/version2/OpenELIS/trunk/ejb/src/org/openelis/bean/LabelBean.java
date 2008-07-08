/**
* The contents of this file are subject to the Mozilla Public License
* Version 1.1 (the "License"); you may not use this file except in
* compliance with the License. You may obtain a copy of the License at
* http://www.mozilla.org/MPL/
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations under
* the License.
* 
* The Original Code is OpenELIS code.
* 
* Copyright (C) OpenELIS.  All Rights Reserved.
*/
package org.openelis.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.LabelDO;
import org.openelis.entity.Label;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.RPCException;
import org.openelis.local.LockLocal;
import org.openelis.metamap.LabelMetaMap;
import org.openelis.remote.LabelRemote;
import org.openelis.security.domain.SystemUserDO;
import org.openelis.security.local.SystemUserUtilLocal;
import org.openelis.util.NewQueryBuilder;
import org.openelis.utils.GetPage;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("label-select")
public class LabelBean implements LabelRemote {
    
    @PersistenceContext(name = "openelis")
    private EntityManager manager;

    
    @EJB
    private SystemUserUtilLocal sysUser;
    
    @Resource
    private SessionContext ctx;
    
    private LockLocal lockBean;
    
    private static final LabelMetaMap Meta = new LabelMetaMap();
    
    {
        try {
            InitialContext cont = new InitialContext();
            lockBean =  (LockLocal)cont.lookup("openelis/LockBean/local");
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public LabelDO getLabel(Integer labelId) {
        Query query = manager.createNamedQuery("Label.Label");
        query.setParameter("id", labelId);
        LabelDO label = (LabelDO)query.getSingleResult();  
        return label;
    }

    @RolesAllowed("label-update")
    public LabelDO getLabelAndLock(Integer labelId) throws Exception {
        Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "label");
        lockBean.getLock((Integer)query.getSingleResult(),labelId);
        
        return getLabel(labelId);
    }

    public LabelDO getLabelAndUnlock(Integer labelId) {
        Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "label");
        lockBean.giveUpLock((Integer)query.getSingleResult(),labelId);
        
        return getLabel(labelId);
    }   

    public Integer getSystemUserId() {
        try {
            SystemUserDO systemUserDO = sysUser.getSystemUser(ctx.getCallerPrincipal()
                                                                 .getName());
            return systemUserDO.getId();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
        }
        
    }

    public List query(HashMap fields, int first, int max) throws Exception {
        StringBuffer sb = new StringBuffer();
        NewQueryBuilder qb = new NewQueryBuilder();    
        
        //LabelMeta labelMeta = LabelMeta.getInstance();
        //qb.addMeta(new Meta[]{labelMeta});
        qb.setMeta(Meta);
        qb.setSelect("distinct new org.openelis.domain.IdNameDO("+ Meta.getId() + " , "+ Meta.getName() + ") ");
        //qb.addTable(labelMeta);
        
        //this method is going to throw an exception if a column doesnt match
        qb.addWhere(fields);
        
        qb.setOrderBy(Meta.getName());
        
        sb.append(qb.getEJBQL());            
        Query query = manager.createQuery(sb.toString());
        
        if(first > -1 && max > -1)
            query.setMaxResults(first+max);
                
        //***set the parameters in the query
        qb.setQueryParams(query);
        
        List returnList = GetPage.getPage(query.getResultList(), first, max);
        if(returnList == null)
         throw new LastPageException();
        else
         return returnList;
    }

    @RolesAllowed("label-update")
    public Integer updateLabel(LabelDO labelDO) throws Exception {
        Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "label");
        Integer labelReferenceId = (Integer)query.getSingleResult();
        
        if(labelDO.getId() != null){
            lockBean.getLock(labelReferenceId,labelDO.getId());
        }
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        Label label = null;
        List<Exception> exceptionList = new ArrayList<Exception>();
        validateLabel(labelDO,exceptionList);
        if(exceptionList.size() > 0){
            throw (RPCException)exceptionList.get(0);
        } 
        
        if(labelDO.getId()==null){
            label = new Label();            
        }else{
            label = manager.find(Label.class, labelDO.getId());
        }                       
        
        label.setName(labelDO.getName());
        label.setDescription(labelDO.getDescription());
        label.setPrinterTypeId(labelDO.getPrinterType());
        label.setScriptletId(labelDO.getScriptlet());
        
        if(label.getId() == null){
            manager.persist(label);
        }
                
        lockBean.giveUpLock(labelReferenceId,label.getId()); 
        return label.getId();          
    }
    
    public List getScriptlets() {
        Query query = manager.createNamedQuery("Scriptlet.Scriptlet");                               
        List scriptlets = query.getResultList();         
        return scriptlets;
    }
    
    public void validateLabel(LabelDO labelDO, List<Exception> exceptionList){

         if("".equals(labelDO.getName())){          
             exceptionList.add(new FieldErrorException("fieldRequiredException",Meta.getName()));                 

          }                       
                                
          if(labelDO.getPrinterType()==null){              
              exceptionList.add(new FieldErrorException("fieldRequiredException",Meta.getPrinterTypeId()));          
          } 
          
          if(labelDO.getScriptlet()==null){              
              exceptionList.add(new FieldErrorException("fieldRequiredException",Meta.getScriptletId()));
         }
                   
          
          
    }    

    @RolesAllowed("label-delete")
    public void deleteLabel(Integer labelId) throws Exception {
        Query lockQuery = manager.createNamedQuery("getTableId");
        lockQuery.setParameter("name", "label");
        Integer labelTableId = (Integer)lockQuery.getSingleResult();
        lockBean.getLock(labelTableId, labelId);
        
        manager.setFlushMode(FlushModeType.COMMIT);
        List<Exception> exceptionList = new ArrayList<Exception>();
        validateForDelete(labelId);
        if(exceptionList.size() > 0){
            throw (RPCException)exceptionList.get(0);
        }
        Label label = null;
        
             label = manager.find(Label.class, labelId);
             try{
                 manager.remove(label);
             }catch (Exception e) {
                 e.printStackTrace();
                 throw e;
             }
             
             lockBean.giveUpLock(labelTableId, labelId); 
         }


    public List<Exception> validateForAdd(LabelDO labelDO) {
        List<Exception> exceptionList = new ArrayList<Exception>();
        validateLabel(labelDO,exceptionList);
        return exceptionList;
    }

    public List<Exception> validateForUpdate(LabelDO labelDO) {
        List<Exception> exceptionList = new ArrayList<Exception>();
        validateLabel(labelDO,exceptionList);
        return exceptionList;
    }
    
    public List<Exception> validateForDelete(Integer labelId){
        List<Exception> exceptionList = new ArrayList<Exception>();        
        List <Object[]> tests  = null;
        try{               
            Query query = manager.createNamedQuery("Test.IdByLabel");
            query.setParameter("id", labelId);
            tests = query.getResultList();
        }catch(NoResultException nrex){
            nrex.printStackTrace();
        }
        
       
        if(tests!=null){  //done to make sure that if an exception was thrown during the execution of the above query 
                          //then the call tests.size() doesn't make a NullPointerException be thrown            
            if(tests.size() > 0){ 
                exceptionList.add(new FormErrorException("labelDeleteException"));
            }
         }
        return exceptionList;
     }
    
}
