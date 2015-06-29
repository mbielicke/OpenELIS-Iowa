package org.openelis.bean;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.constants.Messages;
import org.openelis.domain.OrganizationParameterDO;
import org.openelis.domain.ProviderLocationDO;
import org.openelis.entity.ProviderLocation;
import org.openelis.meta.ProviderMeta;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.FieldErrorException;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.ValidationErrorsList;

@Stateless
@SecurityDomain("openelis")
public class ProviderLocationBean {

    @PersistenceContext(unitName = "openelis")
    private EntityManager manager;

    @EJB
    private AddressBean   addressBean;

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

    @SuppressWarnings("unchecked")
    public ArrayList<ProviderLocationDO> fetchByProviderIds(ArrayList<Integer> ids) throws Exception {
        Query query;
        List<ProviderLocationDO> o;
        ArrayList<Integer> r;

        query = manager.createNamedQuery("ProviderLocation.FetchByProviderIds");
        o = new ArrayList<ProviderLocationDO>();
        r = DataBaseUtil.createSubsetRange(ids.size());
        for (int i = 0; i < r.size() - 1; i++ ) {
            query.setParameter("ids", ids.subList(r.get(i), r.get(i + 1)));
            o.addAll(query.getResultList());
        }

        return DataBaseUtil.toArrayList(o);
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

        if ( !data.isChanged() && !data.getAddress().isChanged())
            return data;

        manager.setFlushMode(FlushModeType.COMMIT);
        entity = manager.find(ProviderLocation.class, data.getId());
        entity.setLocation(data.getLocation());
        entity.setExternalId(data.getExternalId());

        if (data.getAddress().isChanged())
            addressBean.update(data.getAddress());

        return data;
    }

    public void delete(ProviderLocationDO data) throws Exception {
        ProviderLocation entity;

        manager.setFlushMode(FlushModeType.COMMIT);
        entity = manager.find(ProviderLocation.class, data.getId());
        if (entity != null) {
            manager.remove(entity);
            addressBean.delete(data.getAddress());
        }
    }

    public void validate(ProviderLocationDO data) throws Exception {
        ValidationErrorsList list;

        list = new ValidationErrorsList();
        if (DataBaseUtil.isEmpty(data.getLocation()))
            list.add(new FieldErrorException(Messages.get().fieldRequiredException(),
                                             ProviderMeta.getProviderLocationLocation()));

        if (list.size() > 0)
            throw list;
    }
}