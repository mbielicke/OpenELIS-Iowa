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

import org.openelis.domain.IdNameVO;
import org.openelis.domain.LabelViewDO;
import org.openelis.entity.Label;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.SecurityModule.ModuleFlags;
import org.openelis.gwt.common.data.deprecated.AbstractField;
import org.openelis.local.LockLocal;
import org.openelis.metamap.LabelMetaMap;
import org.openelis.remote.LabelRemote;
import org.openelis.util.QueryBuilder;
import org.openelis.utilcommon.DataBaseUtil;
import org.openelis.utils.GetPage;
import org.openelis.utils.ReferenceTableCache;
import org.openelis.utils.SecurityInterceptor;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
@RolesAllowed("label-select")
public class LabelBean implements LabelRemote {
    
    @PersistenceContext(name = "openelis")
    private EntityManager manager;

    @Resource
    private SessionContext ctx;
    
    @EJB
    private LockLocal lockBean;
    private static int labelRefTableId;
    private static final LabelMetaMap Meta = new LabelMetaMap();

    public LabelBean(){
        labelRefTableId = ReferenceTableCache.getReferenceTable("label");
    }
    
    
    public LabelViewDO getLabel(Integer labelId) {
        Query query = manager.createNamedQuery("Label.Label");
        query.setParameter("id", labelId);
        LabelViewDO label = (LabelViewDO)query.getSingleResult();  
        return label;
    }

    @RolesAllowed("label-update")
    public LabelViewDO getLabelAndLock(Integer labelId, String session) throws Exception {
        SecurityInterceptor.applySecurity(ctx.getCallerPrincipal().getName(), "label", ModuleFlags.UPDATE);
        lockBean.getLock(labelRefTableId, labelId);
        
        return getLabel(labelId);
    }

    public LabelViewDO getLabelAndUnlock(Integer labelId, String session) {
        lockBean.giveUpLock(labelRefTableId, labelId);
        
        return getLabel(labelId);
    }   

    public List query(ArrayList<AbstractField> fields, int first, int max) throws Exception {
        StringBuffer sb = new StringBuffer();
        QueryBuilder qb = new QueryBuilder();    
        
        qb.setMeta(Meta);
        qb.setSelect("distinct new org.openelis.domain.IdNameDO("+ Meta.getId() + " , "+ Meta.getName() + ") ");
        
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
    public Integer updateLabel(LabelViewDO labelDO) throws Exception {
        SecurityInterceptor.applySecurity(ctx.getCallerPrincipal().getName(), "label", ModuleFlags.UPDATE);
        Integer labelId;
        Label label;
                
        labelId = labelDO.getId();
        if(labelId != null){
            lockBean.validateLock(labelRefTableId, labelId);                       
        }
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        label = null;        
        validateLabel(labelDO);
                 
        if(labelId == null){
            label = new Label();            
        }else{
            label = manager.find(Label.class, labelId);
        }                       
        
        label.setName(labelDO.getName());
        label.setDescription(labelDO.getDescription());
        label.setPrinterTypeId(labelDO.getPrinterTypeId());
        label.setScriptletId(labelDO.getScriptletId());
        
        if(label.getId() == null){
            manager.persist(label);
        }
                
        lockBean.giveUpLock(labelRefTableId, label.getId()); 
        return label.getId();          
    }
    
    public List getScriptlets() {
        Query query = manager.createNamedQuery("Scriptlet.Scriptlet");                               
        List scriptlets = query.getResultList();         
        return scriptlets;
    }
    
    public void validateLabel(LabelViewDO labelDO) throws Exception{
        ValidationErrorsList exceptionList;
        List <Object[]> tests;
        Query query;
         
        exceptionList = new ValidationErrorsList();
        
      /*  if(!labelDO.getDelete()) {
            if("".equals(labelDO.getName())){          
                exceptionList.add(new FieldErrorException("fieldRequiredException",Meta.getName()));                 
    
            }                       
                                    
            if(labelDO.getPrinterTypeId()==null){              
                exceptionList.add(new FieldErrorException("fieldRequiredException",Meta.getPrinterTypeId()));          
            } 
              
            if(labelDO.getScriptletId()==null){              
                exceptionList.add(new FieldErrorException("fieldRequiredException",Meta.getScriptletId()));
            }
        } else {
            tests = null;
            try{               
                query = manager.createNamedQuery("Label.FetchTestForDeleteCheck");
                query.setParameter("id", labelDO.getId());
                tests = query.getResultList();
            }catch(NoResultException nrex){
                nrex.printStackTrace();
            }
            
           
            if(tests != null){ //done to make sure that if an exception was thrown during the execution of the above query 
                               //then the call tests.size() doesn't make a NullPointerException be thrown            
                if(tests.size() > 0){ 
                    exceptionList.add(new FormErrorException("labelDeleteException"));
                }
             }
        }  */
        
        if(exceptionList.size() > 0)
            throw exceptionList;
                                       
    }    

    @RolesAllowed("label-delete")
    public void deleteLabel(LabelViewDO labelDO) throws Exception {
        Integer labelId;
        Label label;
        
        labelId = labelDO.getId();                
        validateLabel(labelDO);
        
        lockBean.getLock(labelRefTableId, labelId);
        
        manager.setFlushMode(FlushModeType.COMMIT);
        label = null;
        
        label = manager.find(Label.class, labelId);
        try{
            manager.remove(label);
        }catch (Exception e) {
            throw e;
        }
             
        lockBean.giveUpLock(labelRefTableId, labelId); 
    }

    public List getLabelAutoCompleteByName(String match, int maxResults) {
        Query query;
        
        query = manager.createNamedQuery("Label.FetchByName");
        query.setParameter("name", match);
        query.setMaxResults(maxResults);
        
        return query.getResultList();
    }


    public ArrayList<IdNameVO> fetchByName(String match, int max) throws Exception {
        Query query;
        
        query = manager.createNamedQuery("Label.FetchByName");
        query.setParameter("name", match);
        query.setMaxResults(max);
        
        return DataBaseUtil.toArrayList(query.getResultList());
        
    }    
    
}
