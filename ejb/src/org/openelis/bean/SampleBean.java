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

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.IdAccessionVO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.ReferenceTable;
import org.openelis.domain.SampleDO;
import org.openelis.entity.Sample;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.gwt.widget.QueryFieldUtil;
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

    @PersistenceContext(unitName = "openelis")
    private EntityManager manager;

    //declare the locals
    @EJB private LockLocal lockBean;
    
    private static int sampleRefTableId;
    private static final SampleMeta meta = new SampleMeta();
    
    public SampleBean(){
        sampleRefTableId = ReferenceTable.SAMPLE;
    }
    
    public ArrayList<IdAccessionVO> query(ArrayList<QueryData> fields, int first, int max) throws Exception {
        String queryString, whereForFrom, where;
        Query query;
        QueryBuilderV2 builder;
        List list;
        ArrayList<QueryData> wellFields;

        builder = new QueryBuilderV2();
        builder.setMeta(meta);
        builder.setSelect("distinct new org.openelis.domain.IdAccessionVO(" + SampleMeta.getId() + ","+SampleMeta.getAccessionNumber()+") ");
        builder.constructWhere(fields);
        builder.setOrderBy(SampleMeta.getAccessionNumber());
        
        whereForFrom = builder.getWhereClause();
        
        //for the well screen we have to link to the org table and the address table
        //with the same textboxes.  So if they queried by these fields we need to add
        //a link to the 2nd table
        wellFields = removeWellReportToFields(fields);
        builder.clearWhereClause();
        builder.constructWhere(fields);
        where = builder.getWhereClause();
        
        queryString = builder.getSelectClause() + builder.getFromClause(whereForFrom) + where + 
        createWhereFromWellFields(wellFields) + builder.getOrderBy();

        query = manager.createQuery(queryString);
        query.setMaxResults(first + max);
        
        //add the well fields we created
        fields.addAll(wellFields);
        builder.setQueryParams(query, fields);

        list = query.getResultList();
        
        if (list.isEmpty())
            throw new NotFoundException();
        
        list = (ArrayList<IdAccessionVO>)DataBaseUtil.subList(list, first, max);
        if (list == null)
            throw new LastPageException();

        return (ArrayList<IdAccessionVO>)list;
    }
    
    public SampleDO fetchById(Integer sampleId) throws Exception {
        Query query = manager.createNamedQuery("Sample.FetchById");
        query.setParameter("id", sampleId);
        
        return (SampleDO)query.getSingleResult();
    }
    
    public SampleDO fetchByAccessionNumber(Integer accessionNumber) throws Exception {
        Query query = manager.createNamedQuery("Sample.FetchByAccessionNumber");
        query.setParameter("id", accessionNumber);
        
        return (SampleDO)query.getSingleResult();
    }
    
    public SampleDO add(SampleDO data) {
        Sample entity;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        entity = new Sample();
        entity.setNextItemSequence(data.getNextItemSequence());
        entity.setDomain(data.getDomain());
        entity.setAccessionNumber(data.getAccessionNumber());
        entity.setRevision(data.getRevision());
        entity.setOrderId(data.getOrderId());
        entity.setEnteredDate(data.getEnteredDate());
        entity.setReceivedDate(data.getReceivedDate());
        entity.setReceivedById(data.getReceivedById());
        entity.setCollectionDate(data.getCollectionDate());
        entity.setCollectionTime(data.getCollectionTime());
        entity.setStatusId(data.getStatusId());
        entity.setPackageId(data.getPackageId());
        entity.setClientReference(data.getClientReference());
        entity.setReleasedDate(data.getReleasedDate());
        
        manager.persist(entity);
        data.setId(entity.getId());
        
        return data;
    }

    public SampleDO update(SampleDO data) throws Exception {
        Sample entity;
        
        if (!data.isChanged())
            return data;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        entity = manager.find(Sample.class, data.getId());
        entity.setNextItemSequence(data.getNextItemSequence());
        entity.setDomain(data.getDomain());
        entity.setAccessionNumber(data.getAccessionNumber());
        entity.setRevision(data.getRevision());
        entity.setOrderId(data.getOrderId());
        entity.setEnteredDate(data.getEnteredDate());
        entity.setReceivedDate(data.getReceivedDate());
        entity.setReceivedById(data.getReceivedById());
        entity.setCollectionDate(data.getCollectionDate());
        entity.setCollectionTime(data.getCollectionTime());
        entity.setStatusId(data.getStatusId());
        entity.setPackageId(data.getPackageId());
        entity.setClientReference(data.getClientReference());
        entity.setReleasedDate(data.getReleasedDate());
        
        return data;
    }
    
    private void validate() throws Exception {
        
    }
    
    private ArrayList<QueryData> removeWellReportToFields(ArrayList<QueryData> fields){
        ArrayList<QueryData> returnList;
        String dataKey, domain, orgName, addressMult, addressStreet,
            addressCity, addressState, addressZip, addressWorkPhone,
            addressFaxPhone;
        QueryData data;
        
        returnList = new ArrayList<QueryData>();
        domain = null;
        orgName = null;
        addressMult = null;
        addressStreet = null;
        addressCity = null;
        addressState = null;
        addressZip = null;
        addressWorkPhone = null;
        addressFaxPhone = null;
        
        for(int i=fields.size()-1; i>=0; i--){
            data = fields.get(i);
            dataKey = data.key;
            
            if(SampleMeta.getDomain().equals(dataKey))
                domain = data.query;
            else if(SampleMeta.getWellOrganizationName().equals(dataKey)){
                orgName = data.query;
                returnList.add(fields.remove(i));
                
                data = new QueryData();
                data.key = SampleMeta.getWellReportToName();
                data.type = QueryData.Type.STRING;
                data.query = orgName;
                returnList.add(data);
            }else if(SampleMeta.getWellReportToAddressMultipleUnit().equals(dataKey)){
                addressMult = data.query;
                returnList.add(fields.remove(i));
                
                data = new QueryData();
                data.key = SampleMeta.getAddressMultipleUnit();
                data.type = QueryData.Type.STRING;
                data.query = addressMult;
                returnList.add(data);
            }else if(SampleMeta.getWellReportToAddressStreetAddress().equals(dataKey)){
                addressStreet = data.query;
                returnList.add(fields.remove(i));
                
                data = new QueryData();
                data.key = SampleMeta.getAddressStreetAddress();
                data.type = QueryData.Type.STRING;
                data.query = addressStreet;
                returnList.add(data);
            }else if(SampleMeta.getWellReportToAddressCity().equals(dataKey)){
                addressCity = data.query;
                returnList.add(fields.remove(i));
                
                data = new QueryData();
                data.key = SampleMeta.getAddressCity();
                data.type = QueryData.Type.STRING;
                data.query = addressCity;
                returnList.add(data);
            }else if(SampleMeta.getWellReportToAddressState().equals(dataKey)){
                addressState = data.query;
                returnList.add(fields.remove(i));
                
                data = new QueryData();
                data.key = SampleMeta.getAddressState();
                data.type = QueryData.Type.STRING;
                data.query = addressState;
                returnList.add(data);
            }else if(SampleMeta.getWellReportToAddressZipCode().equals(dataKey)){
                addressZip = data.query;
                returnList.add(fields.remove(i));
                
                data = new QueryData();
                data.key = SampleMeta.getAddressZipCode();
                data.type = QueryData.Type.STRING;
                data.query = addressZip;
                returnList.add(data);
            }else if(SampleMeta.getWellReportToAddressWorkPhone().equals(dataKey)){
                addressWorkPhone = data.query;
                returnList.add(fields.remove(i));
                
                data = new QueryData();
                data.key = SampleMeta.getAddressWorkPhone();
                data.type = QueryData.Type.STRING;
                data.query = addressWorkPhone;
                returnList.add(data);
            }else if(SampleMeta.getWellReportToAddressFaxPhone().equals(dataKey)){
                addressFaxPhone = data.query;
                returnList.add(fields.remove(i));
                
                data = new QueryData();
                data.key = SampleMeta.getAddressFaxPhone();
                data.type = QueryData.Type.STRING;
                data.query = addressFaxPhone;
                returnList.add(data);   
            }
        }
        
        if(!SampleManager.WELL_DOMAIN_FLAG.equals(domain))
            return new ArrayList<QueryData>();
        
        return returnList;
    }
    
    private String createWhereFromWellFields(ArrayList<QueryData> wellFields){
        String whereClause;
        QueryBuilderV2 qb;
        QueryFieldUtil qField;
        QueryData field;
        
        qb = new QueryBuilderV2();
        whereClause = "";
        
        for(int i=0; i<wellFields.size(); i++){
            field = wellFields.get(i);
            qField = new QueryFieldUtil();
            qField.parse(field.query);
            
            if(i % 2 == 0){
                whereClause += " and ( "+qb.getQueryNoOperand(qField, field.key);
                
            }else{
                whereClause += " or "+qb.getQueryNoOperand(qField, field.key) + " ) ";
            }
        }
        
        return whereClause;
    }
}
