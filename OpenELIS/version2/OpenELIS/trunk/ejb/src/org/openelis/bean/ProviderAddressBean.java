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

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.ProviderAddressDO;
import org.openelis.entity.ProviderAddress;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.AddressLocal;
import org.openelis.local.ProviderAddressLocal;
import org.openelis.metamap.ProviderMetaMap;
import org.openelis.utilcommon.DataBaseUtil;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("provider-select")
public class ProviderAddressBean implements ProviderAddressLocal {

    @PersistenceContext(name = "openelis")
    private EntityManager                manager;

    @EJB
    private AddressLocal                 addressBean;

    private static final ProviderMetaMap meta = new ProviderMetaMap();

    @SuppressWarnings("unchecked")
    public ArrayList<ProviderAddressDO> fetchByProviderId(Integer id) throws Exception {
        Query query;
        List list;

        query = manager.createNamedQuery("ProviderAddress.FetchByProviderId");
        query.setParameter("id", id);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(list);

    }

    public ProviderAddressDO add(ProviderAddressDO data) throws Exception {
        ProviderAddress entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        addressBean.add(data.getAddressDO());
        entity = new ProviderAddress();
        entity.setAddressId(data.getAddressDO().getId());
        entity.setLocation(data.getLocation());
        entity.setExternalId(data.getExternalId());
        entity.setProviderId(data.getProviderId());

        manager.persist(entity);
        data.setId(entity.getId());

        return data;
    }

    public ProviderAddressDO update(ProviderAddressDO data) throws Exception {
        ProviderAddress entity;

        if ( !data.isChanged())
            return data;

        manager.setFlushMode(FlushModeType.COMMIT);
        addressBean.update(data.getAddressDO());
        entity = manager.find(ProviderAddress.class, data.getId());
        entity.setLocation(data.getLocation());
        entity.setExternalId(data.getExternalId());

        return data;
    }

    public void delete(ProviderAddressDO data) throws Exception {
        ProviderAddress entity;

        manager.setFlushMode(FlushModeType.COMMIT);
        entity = manager.find(ProviderAddress.class, data.getId());
        if (entity != null) {
            addressBean.delete(entity.getAddressId());
            manager.remove(entity);
        }
    }

    public void validate(ProviderAddressDO data) throws Exception {
        ValidationErrorsList list;

        list = new ValidationErrorsList();
        if (DataBaseUtil.isEmpty(data.getLocation()))
            list.add(new FieldErrorException("fieldRequiredException", meta.getProviderAddress()
                                                                           .getLocation()));

        if (list.size() > 0)
            throw list;
    }
}
