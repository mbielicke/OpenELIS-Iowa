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
import org.openelis.domain.IdNameVO;
import org.openelis.domain.ReferenceTable;
import org.openelis.domain.SampleDO;
import org.openelis.entity.Sample;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.local.LockLocal;
import org.openelis.local.SampleLocal;
import org.openelis.manager.SampleManager;
import org.openelis.meta.SampleMeta;
import org.openelis.remote.SampleRemote;
import org.openelis.util.QueryBuilderV2;
import org.openelis.utilcommon.DataBaseUtil;

@Stateless

@SecurityDomain("openelis")
//@RolesAllowed("inventory-select")
public class SampleBean implements SampleLocal, SampleRemote {

    @PersistenceContext(name = "openelis")
    private EntityManager manager;

    //declare the locals
    @EJB private LockLocal lockBean;
    
    private static int sampleRefTableId;
    private static final SampleMeta meta = new SampleMeta();
    
    public SampleBean(){
        sampleRefTableId = ReferenceTable.SAMPLE;
    }
    
    public ArrayList<IdNameVO> query(ArrayList<QueryData> fields, int first, int max) throws Exception {
        Query query;
        QueryBuilderV2 builder;
        List list;

        //for the well screen we have to link to the org table and the adress table
        //with the same textboxes.  So if they queried by these fields we need to add
        //a link to the 2nd table
        addWellWhereClause(fields);
        
        builder = new QueryBuilderV2();
        builder.setMeta(meta);
        builder.setSelect("distinct new org.openelis.domain.IdNameVO(" + SampleMeta.getId() + ",'') ");
        builder.constructWhere(fields);
        builder.setOrderBy(SampleMeta.getId());

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
    
    public SampleDO fetchById(Integer sampleId) throws Exception {
        Query query = manager.createNamedQuery("Sample.SampleById");
        query.setParameter("id", sampleId);
        
        return (SampleDO)query.getSingleResult();
    }
    
    public SampleDO fetchByAccessionNumber(Integer accessionNumber) throws Exception {
        Query query = manager.createNamedQuery("Sample.SampleByAccessionNumber");
        query.setParameter("id", accessionNumber);
        
        return (SampleDO)query.getSingleResult();
    }
    
    public SampleDO add(SampleDO data) {
        Sample entity;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        entity = new Sample();
        
        entity.setAccessionNumber(data.getAccessionNumber());
        entity.setClientReference(data.getClientReference());
        entity.setCollectionDate(data.getCollectionDate());
        entity.setCollectionTime(data.getCollectionTime());
        entity.setDomain(data.getDomain());
        entity.setEnteredDate(data.getEnteredDate());
        entity.setNextItemSequence(data.getNextItemSequence());
        entity.setOrderId(data.getOrderId());
        entity.setPackageId(data.getPackageId());
        entity.setReceivedById(data.getReceivedById());
        entity.setReceivedDate(data.getReceivedDate());
        entity.setReleasedDate(data.getReleasedDate());
        entity.setRevision(data.getRevision());
        entity.setStatusId(data.getStatusId());
        
        manager.persist(entity);
        data.setId(entity.getId());
        
        return data;
    }

    public SampleDO update(SampleDO data) throws Exception {
        Sample entity;
        
        if (!data.isChanged())
            return data;
        
        lockBean.validateLock(sampleRefTableId, data.getId());
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        entity = manager.find(Sample.class, data.getId());

        entity.setAccessionNumber(data.getAccessionNumber());
        entity.setClientReference(data.getClientReference());
        entity.setCollectionDate(data.getCollectionDate());
        entity.setCollectionTime(data.getCollectionTime());
        entity.setDomain(data.getDomain());
        entity.setEnteredDate(data.getEnteredDate());
        entity.setNextItemSequence(data.getNextItemSequence());
        entity.setOrderId(data.getOrderId());
        entity.setPackageId(data.getPackageId());
        entity.setReceivedById(data.getReceivedById());
        entity.setReceivedDate(data.getReceivedDate());
        entity.setReleasedDate(data.getReleasedDate());
        entity.setRevision(data.getRevision());
        entity.setStatusId(data.getStatusId());
        
        lockBean.giveUpLock(sampleRefTableId, data.getId());
        
        return data;
    }
    
    private void validate() throws Exception {
        
    }
    
    private void addWellWhereClause(ArrayList<QueryData> fields){
        String dataKey, domain, orgName, addressMult, addressStreet,
        addressCity, addressState, addressZip, addressWorkPhone,
        addressFaxPhone;
        QueryData data;
        
        domain = null;
        orgName = null;
        addressMult = null;
        addressStreet = null;
        addressCity = null;
        addressState = null;
        addressZip = null;
        addressWorkPhone = null;
        addressFaxPhone = null;
        for(int i=0; i<fields.size(); i++){
            data = fields.get(i);
            dataKey = data.key;
            
            if(SampleMeta.getDomain().equals(dataKey))
                domain = data.query;
            else if(SampleMeta.getOrgName().equals(dataKey))
                orgName = data.query;
            else if(SampleMeta.getAddressMultipleUnit().equals(dataKey))
                addressMult = data.query;
            else if(SampleMeta.getAddressStreetAddress().equals(dataKey))
                addressStreet = data.query;
            else if(SampleMeta.getAddressCity().equals(dataKey))
                addressCity = data.query;
            else if(SampleMeta.getAddressState().equals(dataKey))
                addressState = data.query;
            else if(SampleMeta.getAddressZipCode().equals(dataKey))
                addressZip = data.query;
            else if(SampleMeta.getAddressWorkPhone().equals(dataKey))
                addressWorkPhone = data.query;
            else if(SampleMeta.getAddressFaxPhone().equals(dataKey))
                addressFaxPhone = data.query;
        }
        
        if(SampleManager.WELL_DOMAIN_FLAG.equals(domain)){
            if(orgName != null){
             data = new QueryData();
             data.key = SampleMeta.getWellReportToName();
             data.type = QueryData.Type.STRING;
             data.query = orgName;
             fields.add(data);
            }else if(addressMult != null){
                data = new QueryData();
                data.key = SampleMeta.getWellReportToAddressMultipleUnit();
                data.type = QueryData.Type.STRING;
                data.query = addressMult;
                fields.add(data);
            }else if(addressStreet != null){
                data = new QueryData();
                data.key = SampleMeta.getWellReportToAddressStreetAddress();
                data.type = QueryData.Type.STRING;
                data.query = addressStreet;
                fields.add(data);
            }else if(addressCity != null){
                data = new QueryData();
                data.key = SampleMeta.getWellReportToAddressCity();
                data.type = QueryData.Type.STRING;
                data.query = addressCity;
                fields.add(data);
            }else if(addressState != null){
                data = new QueryData();
                data.key = SampleMeta.getWellReportToAddressState();
                data.type = QueryData.Type.STRING;
                data.query = addressState;
                fields.add(data);
            }else if(addressZip != null){ 
                data = new QueryData();
                data.key = SampleMeta.getWellReportToAddressZipCode();
                data.type = QueryData.Type.STRING;
                data.query = addressZip;
                fields.add(data);
            }else if(addressWorkPhone != null){
                data = new QueryData();
                data.key = SampleMeta.getWellReportToAddressWorkPhone();
                data.type = QueryData.Type.STRING;
                data.query = addressWorkPhone;
                fields.add(data);
            }else if(addressFaxPhone != null){
                data = new QueryData();
                data.key = SampleMeta.getWellReportToAddressFaxPhone();
                data.type = QueryData.Type.STRING;
                data.query = addressFaxPhone;
                fields.add(data);   
            }   
        }
    }
}
