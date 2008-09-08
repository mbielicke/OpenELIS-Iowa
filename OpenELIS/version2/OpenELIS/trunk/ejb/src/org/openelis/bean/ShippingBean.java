package org.openelis.bean;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBs;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.ShippingAddAutoFillDO;
import org.openelis.local.LockLocal;
import org.openelis.metamap.ShippingMetaMap;
import org.openelis.remote.ShippingRemote;

@Stateless
@EJBs({
    @EJB(name="ejb/Lock",beanInterface=LockLocal.class)
})
@SecurityDomain("openelis")
//@RolesAllowed("qaevent-select")
public class ShippingBean implements ShippingRemote{

    @PersistenceContext(name = "openelis")
    private EntityManager manager;
    
    @Resource
    private SessionContext ctx;
    
    private LockLocal lockBean;
    
    private static final ShippingMetaMap ShippingMeta = new ShippingMetaMap();

    @PostConstruct
    private void init()
    {
        lockBean =  (LockLocal)ctx.lookup("ejb/Lock");
    }

    public ShippingAddAutoFillDO getAddAutoFillValues() throws Exception {
        ShippingAddAutoFillDO autoFillDO = new ShippingAddAutoFillDO();
        //status integer
        Query query = manager.createNamedQuery("Dictionary.IdBySystemName");
        query.setParameter("systemName", "order_status_shipped");

        //default to shipped status
        autoFillDO.setStatus((Integer)query.getSingleResult());

        //default to today
        autoFillDO.setProcessedDate(new Date());
        
        //default to today
        autoFillDO.setShippedDate(new Date());

        //default to current user
        autoFillDO.setProcessedBy(ctx.getCallerPrincipal().getName());
        
        return autoFillDO;
    }
    
}
