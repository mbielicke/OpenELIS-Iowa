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

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.SampleEnvironmentalDO;
import org.openelis.entity.SampleEnvironmental;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.local.LockLocal;
import org.openelis.local.SampleEnvironmentalLocal;
import org.openelis.manager.SampleDomainInt;
import org.openelis.manager.SampleEnvironmentalManager;
import org.openelis.manager.SampleManager;
import org.openelis.metamap.SampleEnvironmentalMetaMap;
import org.openelis.remote.SampleEnvironmentalRemote;
import org.openelis.util.QueryBuilder;
import org.openelis.utils.GetPage;

@Stateless

@SecurityDomain("openelis")
//@RolesAllowed("inventory-select")
public class SampleEnvironmentalBean implements SampleEnvironmentalRemote, SampleEnvironmentalLocal {
    @PersistenceContext(name = "openelis")
    private EntityManager manager;
   
    @Resource
    private SessionContext ctx;

    //declare the locals
    @EJB private LockLocal lockBean;
    
    private static final SampleEnvironmentalMetaMap Meta = new SampleEnvironmentalMetaMap();
    
    public SampleEnvironmentalDO getEnvBySampleId(Integer sampleId){
        Query query = manager.createNamedQuery("SampleEnvironmental.SampleEnvironmentalBySampleId");
        query.setParameter("id", sampleId);
        List results = query.getResultList();
        
        if(results.size() == 0)
            return null;
        
        return (SampleEnvironmentalDO)results.get(0);
    }
    
    public Integer update(SampleDomainInt sampleDomain){
        System.out.println("start env up method");
        SampleEnvironmentalManager envManager = (SampleEnvironmentalManager)sampleDomain;
        SampleEnvironmentalDO envDO = envManager.getEnvironmental();
        
        //validate the sample domain
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        //update the sample domain
        SampleEnvironmental environmental = null;
        
        if (envDO.getId() == null)
            environmental = new SampleEnvironmental();
       else
           environmental = manager.find(SampleEnvironmental.class, envDO.getId());
        
        environmental.setAddressId(envDO.getAddressId());
        environmental.setCollector(envDO.getCollector());
        environmental.setCollectorPhone(envDO.getCollectorPhone());
        environmental.setDescription(envDO.getDescription());
        environmental.setIsHazardous(envDO.getIsHazardous());
        environmental.setSampleId(envManager.getSampleId());
        environmental.setSamplingLocation(envDO.getSamplingLocation());
        
        if(environmental.getId() == null)
            manager.persist(environmental);
        
        return environmental.getId();
    }
    
    public void validateDomain() throws Exception {
        
    }

    public List query(ArrayList<AbstractField> fields, int first, int max) throws Exception {
        StringBuffer sb = new StringBuffer();
        QueryBuilder qb = new QueryBuilder();

        qb.setMeta(Meta);
      
        qb.setSelect("distinct new org.openelis.domain.IdNameDO("+Meta.SAMPLE.getId()+") ");
       
        //this method is going to throw an exception if a column doesnt match
        qb.addWhere(fields);     
        
        qb.addWhere(Meta.SAMPLE.getDomain() + " = '" + SampleManager.ENVIRONMENTAL_DOMAIN_FLAG + "'");

        //qb.setOrderBy(Meta.SAMPLE.getAccessionNumber());
       
        sb.append(qb.getEJBQL());

        Query query = manager.createQuery(sb.toString());
        
        if(first > -1 && max > -1)
         query.setMaxResults(first+max);
        
//      ***set the parameters in the query
        qb.setQueryParams(query);
        
        List returnList = GetPage.getPage(query.getResultList(), first, max);
        
        if(returnList == null)
         throw new LastPageException();
        else
         return returnList;
    }
}
