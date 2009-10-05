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

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.IdNameDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.SampleEnvironmentalDO;
import org.openelis.entity.SampleEnvironmental;
import org.openelis.exception.NotFoundException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.local.AddressLocal;
import org.openelis.local.SampleEnvironmentalLocal;
import org.openelis.manager.SampleManager;
import org.openelis.metamap.SampleEnvironmentalMetaMap;
import org.openelis.remote.SampleEnvironmentalRemote;
import org.openelis.util.QueryBuilder;
import org.openelis.util.QueryBuilderV2;
import org.openelis.utilcommon.DataBaseUtil;
import org.openelis.utils.GetPage;

@Stateless

@SecurityDomain("openelis")
//@RolesAllowed("inventory-select")
public class SampleEnvironmentalBean implements SampleEnvironmentalRemote, SampleEnvironmentalLocal {
    @PersistenceContext(name = "openelis")
    private EntityManager manager;
    
    @EJB private AddressLocal addressBean;
   
    private static final SampleEnvironmentalMetaMap Meta = new SampleEnvironmentalMetaMap();
    
    public SampleEnvironmentalDO fetchBySampleId(Integer sampleId) throws Exception {
        Query query = manager.createNamedQuery("SampleEnvironmental.SampleEnvironmentalBySampleId");
        query.setParameter("id", sampleId);
        
        SampleEnvironmentalDO envDO = (SampleEnvironmentalDO) query.getSingleResult();
        return envDO;
    }

    public ArrayList<IdNameVO> query(ArrayList<QueryData> fields, int first, int max) throws Exception {
        StringBuffer sb = new StringBuffer();
        QueryBuilderV2 qb = new QueryBuilderV2();
        List list;
        
        qb.setMeta(Meta);
      
        qb.setSelect("distinct new org.openelis.domain.IdNameVO("+Meta.SAMPLE.getId()+", '') ");
       
        //this method is going to throw an exception if a column doesnt match
        qb.constructWhere(fields);     
        
        qb.addWhere(Meta.SAMPLE.getDomain() + " = '" + SampleManager.ENVIRONMENTAL_DOMAIN_FLAG + "'");

        //qb.setOrderBy(Meta.SAMPLE.getAccessionNumber());
       
        sb.append(qb.getEJBQL());

        Query query = manager.createQuery(sb.toString());
        
        if(first > -1 && max > -1)
         query.setMaxResults(first+max);
        
//      ***set the parameters in the query
        qb.setQueryParams(query, fields);
        
        ArrayList<IdNameDO> returnList = (ArrayList<IdNameDO>)GetPage.getPage(query.getResultList(), first, max);
        
        list = query.getResultList();
        
        if (list.isEmpty())
            throw new NotFoundException();

        list = (ArrayList<IdNameVO>)DataBaseUtil.subList(list, first, max);
        
        if (list == null)
            throw new LastPageException();

        return (ArrayList<IdNameVO>)list;
    }
    
    public void add(SampleEnvironmentalDO envSampleDO) {
        manager.setFlushMode(FlushModeType.COMMIT);

        SampleEnvironmental environmental = new SampleEnvironmental();
        
        addressBean.add(envSampleDO.getAddressDO());
        
        environmental.setAddressId(envSampleDO.getAddressDO().getId());
        environmental.setCollector(envSampleDO.getCollector());
        environmental.setCollectorPhone(envSampleDO.getCollectorPhone());
        environmental.setDescription(envSampleDO.getDescription());
        environmental.setIsHazardous(envSampleDO.getIsHazardous());
        environmental.setSampleId(envSampleDO.getSampleId());
        environmental.setSamplingLocation(envSampleDO.getSamplingLocation());
        
        manager.persist(environmental);
        
        envSampleDO.setId(environmental.getId());
    }

    public void update(SampleEnvironmentalDO envSampleDO) {
        manager.setFlushMode(FlushModeType.COMMIT);
        
        SampleEnvironmental environmental = manager.find(SampleEnvironmental.class, envSampleDO.getId());
        
        addressBean.update(envSampleDO.getAddressDO());
        
        environmental.setAddressId(envSampleDO.getAddressDO().getId());
        environmental.setCollector(envSampleDO.getCollector());
        environmental.setCollectorPhone(envSampleDO.getCollectorPhone());
        environmental.setDescription(envSampleDO.getDescription());
        environmental.setIsHazardous(envSampleDO.getIsHazardous());
        environmental.setSampleId(envSampleDO.getSampleId());
        environmental.setSamplingLocation(envSampleDO.getSamplingLocation());
    }

    public void validateDomain() throws Exception {
        
    }
}
