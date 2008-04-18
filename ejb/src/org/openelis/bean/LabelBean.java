package org.openelis.bean;

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
import org.openelis.gwt.common.LastPageException;
import org.openelis.local.LockLocal;
import org.openelis.meta.LabelMeta;
import org.openelis.remote.LabelRemote;
import org.openelis.util.Meta;
import org.openelis.util.QueryBuilder;
import org.openelis.utils.GetPage;

import edu.uiowa.uhl.security.domain.SystemUserDO;
import edu.uiowa.uhl.security.local.SystemUserUtilLocal;

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
    
    {
        try {
            InitialContext cont = new InitialContext();
            lockBean =  (LockLocal)cont.lookup("openelis/LockBean/local");
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public LabelDO getLabel(Integer labelId) {
        Query query = manager.createNamedQuery("getLabel");
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
        QueryBuilder qb = new QueryBuilder();    
        
        LabelMeta labelMeta = LabelMeta.getInstance();
        qb.addMeta(new Meta[]{labelMeta});
        qb.setSelect("distinct "+ LabelMeta.ID + " , "+ LabelMeta.NAME);
        qb.addTable(labelMeta);
        
        //this method is going to throw an exception if a column doesnt match
        qb.addWhere(fields);
        
        qb.setOrderBy(LabelMeta.NAME);
        
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
        try{
            manager.setFlushMode(FlushModeType.COMMIT);
            
            Query query = manager.createNamedQuery("getTableId");
            query.setParameter("name", "qaevent");
            Integer labelReferenceId = (Integer)query.getSingleResult();
            
            Label label = null;
            
            validate(labelDO);
            
            if(labelDO.getId()==null){
                label = new Label();            
            }else{
                label = manager.find(Label.class, labelDO.getId());
            }                       
            
            label.setName(labelDO.getName());
            label.setDescription(labelDO.getDescription());
            label.setPrinterType(labelDO.getPrinterType());
            label.setScriptlet(labelDO.getScriptlet());
            
            if(label.getId() == null){
                manager.persist(label);
            }
                    
            lockBean.giveUpLock(labelReferenceId,label.getId()); 
            return label.getId();
           }catch(Exception ex){
               ex.printStackTrace();
               throw ex;
           } 
    }
    
    public List<Object[]> getScriptlets() {
        Query query = manager.createNamedQuery("getScriptlets");                               
        List<Object[]> scriptlets = query.getResultList();         
        return scriptlets;
    }
    
    public void validate(LabelDO labelDO)throws Exception{
        if(labelDO.getName()==null){            
            throw new Exception("Name must be specified for a Label");   
          }else {
              if(("").equals(labelDO.getName().trim())){          
               throw new Exception("Name must be specified for a Label");                 
           }
          }                       
                                
          if(labelDO.getPrinterType()==null){              
              throw new Exception("Printer Type must be specified for a Label");              
          } 
          
          if(labelDO.getScriptlet()==null){              
              throw new Exception("A Scriptlet must be specified for a Label"); 
          }
    }    

    @RolesAllowed("label-delete")
    public void deleteLabel(Integer labelId) throws Exception {
        manager.setFlushMode(FlushModeType.COMMIT);
        Label label = null;
        List <Object[]> tests  = null;
        try{
            Query query = manager.createNamedQuery("getReferringTests");
            query.setParameter("id", labelId);
            tests = query.getResultList();
        }catch(NoResultException nrex){
            nrex.printStackTrace();
        }
                
         if(tests!=null){             
            if(tests.size() > 0){ 
             throw new Exception("This label cannot be deleted because it is linked to one or more tests.");
            } 
         else {
             label = manager.find(Label.class, labelId);
             try{
                 manager.remove(label);
             }catch (Exception e) {
                 e.printStackTrace();
                 throw e;
             }
         }
       }
    }
}
