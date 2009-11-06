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
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.SampleEnvironmentalDO;
import org.openelis.entity.SampleEnvironmental;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.local.AddressLocal;
import org.openelis.local.SampleEnvironmentalLocal;
import org.openelis.manager.SampleManager;
import org.openelis.metamap.SampleEnvironmentalMetaMap;
import org.openelis.remote.SampleEnvironmentalRemote;
import org.openelis.util.QueryBuilderV2;
import org.openelis.utilcommon.DataBaseUtil;

@Stateless

@SecurityDomain("openelis")
@RolesAllowed("sample_environmental-select")
public class SampleEnvironmentalBean implements SampleEnvironmentalRemote, SampleEnvironmentalLocal {
    @PersistenceContext(name = "openelis")
    private EntityManager manager;
    
    @EJB private AddressLocal addressBean;
   
    private static final SampleEnvironmentalMetaMap meta = new SampleEnvironmentalMetaMap();
    
    public SampleEnvironmentalDO fetchBySampleId(Integer id) throws Exception {
        Query query;
        
        query = manager.createNamedQuery("SampleEnvironmental.FetchBySampleId");
        query.setParameter("id", id);

        return (SampleEnvironmentalDO) query.getSingleResult();
    }

    public ArrayList<IdNameVO> query(ArrayList<QueryData> fields, int first, int max) throws Exception {
        Query query;
        QueryBuilderV2 builder;
        List list;

        builder = new QueryBuilderV2();
        builder.setMeta(meta);
        builder.setSelect("distinct new org.openelis.domain.IdNameVO(" + meta.SAMPLE.getId() + ",'') ");
        builder.constructWhere(fields);
        builder.addWhere(meta.SAMPLE.getDomain() + "='" + SampleManager.ENVIRONMENTAL_DOMAIN_FLAG + "'");
        builder.setOrderBy(meta.SAMPLE.getId());

        query = manager.createQuery(builder.getEJBQL());
        query.setMaxResults(first + max);
        builder.setQueryParams(query, fields);

        list = query.getResultList();
        
        if (list.isEmpty())
            throw new NotFoundException();
        list = (ArrayList<IdNameVO>)DataBaseUtil.subList(list, first, max);
        if (list == null)
            throw new LastPageException();

        return (ArrayList<IdNameVO>)list;
    }
    
    public void add(SampleEnvironmentalDO envSampleDO) throws Exception {
        manager.setFlushMode(FlushModeType.COMMIT);

        SampleEnvironmental environmental = new SampleEnvironmental();
        
        addressBean.add(envSampleDO.getAddressDO());
        
        environmental.setAddressId(envSampleDO.getAddressDO().getId());
        environmental.setCollector(envSampleDO.getCollector());
        environmental.setCollectorPhone(envSampleDO.getCollectorPhone());
        environmental.setDescription(envSampleDO.getDescription());
        environmental.setIsHazardous(envSampleDO.getIsHazardous());
        environmental.setPriority(envSampleDO.getPriority());
        environmental.setSampleId(envSampleDO.getSampleId());
        environmental.setSamplingLocation(envSampleDO.getSamplingLocation());
        
        manager.persist(environmental);
        
        envSampleDO.setId(environmental.getId());
    }

    public void update(SampleEnvironmentalDO envSampleDO) throws Exception {
        manager.setFlushMode(FlushModeType.COMMIT);
        
        SampleEnvironmental environmental = manager.find(SampleEnvironmental.class, envSampleDO.getId());
        
        addressBean.update(envSampleDO.getAddressDO());
        
        environmental.setAddressId(envSampleDO.getAddressDO().getId());
        environmental.setCollector(envSampleDO.getCollector());
        environmental.setCollectorPhone(envSampleDO.getCollectorPhone());
        environmental.setDescription(envSampleDO.getDescription());
        environmental.setIsHazardous(envSampleDO.getIsHazardous());
        environmental.setPriority(envSampleDO.getPriority());
        environmental.setSampleId(envSampleDO.getSampleId());
        environmental.setSamplingLocation(envSampleDO.getSamplingLocation());
    }

    public void validate() throws Exception {
    }
}
