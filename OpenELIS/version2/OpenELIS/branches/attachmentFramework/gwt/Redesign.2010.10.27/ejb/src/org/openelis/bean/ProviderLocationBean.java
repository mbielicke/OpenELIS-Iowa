package org.openelis.bean;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.ProviderLocationDO;
import org.openelis.entity.ProviderLocation;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.AddressLocal;
import org.openelis.local.ProviderLocationLocal;
import org.openelis.meta.ProviderMeta;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("provider-select")
public class ProviderLocationBean implements ProviderLocationLocal {

    @PersistenceContext(unitName = "openelis")
    private EntityManager                manager;

    @EJB
    private AddressLocal                 addressBean;

    @SuppressWarnings("unchecked")
    public ArrayList<ProviderLocationDO> fetchByProviderId(Integer id) throws Exception {
        Query query;
        List list;

        query = manager.createNamedQuery("ProviderLocation.FetchByProviderId");
        query.setParameter("id", id);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(list);

    }

    public ProviderLocationDO add(ProviderLocationDO data) throws Exception {
        ProviderLocation entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        addressBean.add(data.getAddress());
        
        entity = new ProviderLocation();
        entity.setAddressId(data.getAddress().getId());
        entity.setLocation(data.getLocation());
        entity.setExternalId(data.getExternalId());
        entity.setProviderId(data.getProviderId());

        manager.persist(entity);
        data.setId(entity.getId());

        return data;
    }

    public ProviderLocationDO update(ProviderLocationDO data) throws Exception {
        ProviderLocation entity;

        if (!data.isChanged() && !data.getAddress().isChanged())
            return data;
        
        manager.setFlushMode(FlushModeType.COMMIT);            
        entity = manager.find(ProviderLocation.class, data.getId());
        entity.setLocation(data.getLocation());
        entity.setExternalId(data.getExternalId());

        if (data.getAddress().isChanged()) {
            entity.setAuditAddressId(true);
            addressBean.update(data.getAddress());
        }

        return data;
    }

    public void delete(ProviderLocationDO data) throws Exception {
        ProviderLocation entity;

        manager.setFlushMode(FlushModeType.COMMIT);
        entity = manager.find(ProviderLocation.class, data.getId());
        if (entity != null) {
            addressBean.delete(data.getAddress());
            manager.remove(entity);
        }
    }

    public void validate(ProviderLocationDO data) throws Exception {
        ValidationErrorsList list;

        list = new ValidationErrorsList();
        if (DataBaseUtil.isEmpty(data.getLocation()))
            list.add(new FieldErrorException("fieldRequiredException", ProviderMeta.getProviderLocationLocation()));

        if (list.size() > 0)
            throw list;
    }
}