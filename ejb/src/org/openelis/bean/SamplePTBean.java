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

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.constants.Messages;
import org.openelis.domain.SamplePTDO;
import org.openelis.entity.SamplePT;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.FormErrorException;
import org.openelis.ui.common.ValidationErrorsList;

@Stateless
@SecurityDomain("openelis")

public class SamplePTBean {

    @PersistenceContext(unitName = "openelis")
    private EntityManager manager;   
    
    public ArrayList<SamplePTDO> fetchBySampleIds(ArrayList<Integer> sampleIds) {
        Query query;

        query = manager.createNamedQuery("SamplePT.FetchBySampleIds");
        query.setParameter("ids", sampleIds);

        return DataBaseUtil.toArrayList(query.getResultList());
    }
    
    public SamplePTDO add(SamplePTDO data) throws Exception {
        SamplePT entity;
        
        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new SamplePT();        
        entity.setSampleId(data.getSampleId());
        entity.setPTProviderId(data.getPTProviderId());
        entity.setSeries(data.getSeries());
        entity.setDueDate(data.getDueDate());
        entity.setAdditionalDomain(data.getAdditionalDomain());
        
        manager.persist(entity);
        
        data.setId(entity.getId());
        
        return data;
    }

    public SamplePTDO update(SamplePTDO data) throws Exception {
        SamplePT entity;
        
        if (!data.isChanged())
            return data;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        entity = manager.find(SamplePT.class, data.getId());
        
        entity.setSampleId(data.getSampleId());
        entity.setPTProviderId(data.getPTProviderId());
        entity.setSeries(data.getSeries());
        entity.setDueDate(data.getDueDate());
        entity.setAdditionalDomain(data.getAdditionalDomain());
        
        return data;
    }

    public void delete(SamplePTDO data) throws Exception {
        SamplePT entity;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        entity = manager.find(SamplePT.class, data.getId());
        if (entity != null)
            manager.remove(entity);
    }
       
    public void validate(SamplePTDO data, Integer accession) throws Exception {
        ValidationErrorsList e;
        
        e = new ValidationErrorsList();
        
        /*
         * for display
         */
        if (accession == null)
            accession = 0;
        
        if (data.getPTProviderId() == null)
            e.add(new FormErrorException(Messages.get().samplePT_providerRequiredException(accession)));        
        
        if (data.getSeries() == null)
            e.add(new FormErrorException(Messages.get().samplePT_seriesRequiredException(accession)));
        
        if (e.size() > 0)
            throw e;
    }
}