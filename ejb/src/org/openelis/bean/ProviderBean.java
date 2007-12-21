package org.openelis.bean;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.openelis.domain.NoteDO;
import org.openelis.domain.OrganizationAddressDO;
import org.openelis.domain.ProviderAddressDO;
import org.openelis.domain.ProviderDO;
import org.openelis.domain.ProviderTableRowDO;
import org.openelis.entity.Organization;
import org.openelis.entity.Provider;
import org.openelis.entity.ProviderAddress;
import org.openelis.gwt.common.QueryNumberField;
import org.openelis.gwt.common.QueryOptionField;
import org.openelis.gwt.common.QueryStringField;
import org.openelis.local.LockLocal;
import org.openelis.remote.AddressLocal;
import org.openelis.remote.ProviderRemote;
import org.openelis.util.QueryBuilder;

import edu.uiowa.uhl.security.domain.SystemUserDO;
import edu.uiowa.uhl.security.local.SystemUserUtilLocal;

@Stateless
public class ProviderBean implements ProviderRemote {

    @PersistenceContext(name = "openelis")
    private EntityManager manager;
    //private String className = this.getClass().getName();
   // private Logger log = Logger.getLogger(className);
    
    @EJB
    private SystemUserUtilLocal sysUser;
    
    @Resource
    private SessionContext ctx;
    
    private LockLocal lockBean;
    private AddressLocal addressBean;
    
    {
        try {
            InitialContext cont = new InitialContext();
            lockBean =  (LockLocal)cont.lookup("openelis/LockBean/local");
            addressBean =  (AddressLocal)cont.lookup("openelis/AddressBean/local");
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public ProviderDO getProvider(Integer providerId, boolean unlock) {
        if(unlock){
            Query query = manager.createNamedQuery("getTableId");
            query.setParameter("name", "provider");
            lockBean.giveUpLock((Integer)query.getSingleResult(),providerId);            
        }
        
        Query query = manager.createNamedQuery("getProvider");
        query.setParameter("id", providerId);
        ProviderDO provider = (ProviderDO) query.getSingleResult();// getting organization with address and contacts

        return provider;
    }

    public List getProviderAddresses(Integer providerId) {
        Query query = manager.createNamedQuery("getProviderAddresses");
        query.setParameter("id", providerId);
        
        List providerAddresses = query.getResultList();// getting list of addresses from the 
    
        return providerAddresses;
    }

    public List<ProviderTableRowDO> getProviderNameListByLetter(String letter,
                                            int startPos,
                                            int maxResults) {
        Query query = manager.createNamedQuery("getProviderNameRowsByLetter");
        query.setParameter("letter", letter);
        
        if(maxResults > 0){
            query.setFirstResult(startPos);
            query.setMaxResults(maxResults);
        }
        
        List<ProviderTableRowDO> orgList = query.getResultList();// getting a list of organizations
        
        return orgList;
       
    }

    public List getProviderNotes(Integer providerId, boolean topLevel) {
        
        return null;
    }

    public ProviderDO getProviderUpdate(Integer id) throws Exception {
        
        return null;
    }

    public Integer getSystemUserId(){
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

    public List query(HashMap fields, int first, int max) throws RemoteException {
        
        //Query refIdQuery = manager.createNamedQuery("getTableId");
        //refIdQuery.setParameter("name", "provider");
        //Integer providerReferenceId = (Integer)refIdQuery.getSingleResult();
        System.out.println("starting query");
        
        StringBuffer sb = new StringBuffer();
        sb.append("select distinct p.id, p.lastName, p.firstName from Provider p where 1=1 ");
        if(fields.containsKey("lastName"))
         sb.append(QueryBuilder.getQuery((QueryStringField)fields.get("lastName"), "p.lastName"));
        if(fields.containsKey("firstName"))
         sb.append(QueryBuilder.getQuery((QueryStringField)fields.get("firstName"), "p.firstName"));
        if(fields.containsKey("npi"))
         sb.append(QueryBuilder.getQuery((QueryStringField)fields.get("npi"), "p.npi"));
        if(fields.containsKey("middleName"))
         sb.append(QueryBuilder.getQuery((QueryStringField)fields.get("middleName"), "p.middleName"));
        if(fields.containsKey("providerType"))
            sb.append(QueryBuilder.getQuery((QueryOptionField)fields.get("type"), "p.type"));
        
        Query query = manager.createQuery(sb.toString()+" order by p.lastName, p.firstName ");
        
        if(first > -1)
         query.setFirstResult(first);
        if(max > -1)
         query.setMaxResults(max);
        
        if(fields.containsKey("lastName"))
            QueryBuilder.setParameters((QueryStringField)fields.get("lastName"), "p.lastName",query);
        if(fields.containsKey("firstName"))
            QueryBuilder.setParameters((QueryStringField)fields.get("firstName"), "p.firstName",query);
        if(fields.containsKey("npi"))
            QueryBuilder.setParameters((QueryStringField)fields.get("npi"), "p.npi",query);
        if(fields.containsKey("middleName"))
            QueryBuilder.setParameters((QueryStringField)fields.get("middleName"), "p.middleName",query);
        if(fields.containsKey("providerType"))
            QueryBuilder.setParameters((QueryOptionField)fields.get("type"), "p.type",query);
        
        System.out.println("ending query");
        return query.getResultList();
        
    }

    public Integer updateProvider(ProviderDO providerDO, NoteDO noteDO,List addresses) {
        manager.setFlushMode(FlushModeType.COMMIT);
        Provider provider = null;
        try{
            if (providerDO.getId() == null){
                provider = new Provider();
            } 
            else{
                provider = manager.find(Provider.class, providerDO.getId());
            }
            
            provider.setFirstName(providerDO.getFirstName());
            provider.setLastName(providerDO.getLastName());
            provider.setMiddleName(providerDO.getMiddleName());
            provider.setNpi(providerDO.getNpi());
            provider.setType(providerDO.getTypeId());
            
            if (provider.getId() == null) {
                manager.persist(provider);
            }
            
            for (Iterator iter = addresses.iterator(); iter.hasNext();) {
                ProviderAddressDO provAddDO = (ProviderAddressDO)iter.next();
                ProviderAddress provAdd = null;
                
                if (provAddDO.getId() == null){
                    provAdd = new ProviderAddress();
                } 
                else{
                    provAdd = manager.find(ProviderAddress.class, provAddDO.getId());
                }
                
                Integer addressId = addressBean.updateAddress(provAddDO.getAddressDO());
                
                provAdd.setExternalId(provAddDO.getExternalId());
                provAdd.setLocation(provAddDO.getLocation());
                provAdd.setProvider(provider.getId());
                provAdd.setAddress(addressId);
            }
            
        }catch(Exception ex){
           ex.printStackTrace(); 
        }
        
        return provider.getId();
    }

    public List<Object[]> getProviderTypes() {
        Query query = manager.createNamedQuery("getProviderTypes");        
        return query.getResultList();
    }

}
