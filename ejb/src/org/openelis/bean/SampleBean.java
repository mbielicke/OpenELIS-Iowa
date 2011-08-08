/**
 * Exhibit A - UIRF Open-source Based Public Software License.
 * 
 * The contents of this file are subject to the UIRF Open-source Based Public
 * Software License(the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * openelis.uhl.uiowa.edu
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * The Initial Developer of the Original Code is The University of Iowa.
 * Portions created by The University of Iowa are Copyright 2006-2008. All
 * Rights Reserved.
 * 
 * Contributor(s): ______________________________________.
 * 
 * Alternatively, the contents of this file marked "Separately-Licensed" may be
 * used under the terms of a UIRF Software license ("UIRF Software License"), in
 * which case the provisions of a UIRF Software License are applicable instead
 * of those above.
 */
package org.openelis.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.IdAccessionVO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.SampleDO;
import org.openelis.domain.SampleStatusWebReportVO;
import org.openelis.entity.Sample;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.local.SampleLocal;
import org.openelis.meta.SampleMeta;
import org.openelis.remote.SampleRemote;
import org.openelis.util.QueryBuilderV2;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("sample-select")
public class SampleBean implements SampleLocal, SampleRemote {

    @PersistenceContext(unitName = "openelis")
    private EntityManager           manager;

    private static final SampleMeta meta = new SampleMeta();

    private static HashMap<String, String> wellOrgFieldMap, reportToAddressFieldMap;

    static {
        wellOrgFieldMap = new HashMap<String, String>();
        wellOrgFieldMap.put(SampleMeta.getWellOrganizationName(),
                            SampleMeta.getWellOrganizationName());
        wellOrgFieldMap.put(SampleMeta.getWellReportToAddressMultipleUnit(),
                            SampleMeta.getWellReportToAddressMultipleUnit());
        wellOrgFieldMap.put(SampleMeta.getWellReportToAddressStreetAddress(),
                            SampleMeta.getWellReportToAddressStreetAddress());
        wellOrgFieldMap.put(SampleMeta.getWellReportToAddressCity(),
                            SampleMeta.getWellReportToAddressCity());
        wellOrgFieldMap.put(SampleMeta.getWellReportToAddressState(),
                            SampleMeta.getWellReportToAddressState());
        wellOrgFieldMap.put(SampleMeta.getWellReportToAddressZipCode(),
                            SampleMeta.getWellReportToAddressZipCode());
        wellOrgFieldMap.put(SampleMeta.getWellReportToAddressWorkPhone(),
                            SampleMeta.getWellReportToAddressWorkPhone());
        wellOrgFieldMap.put(SampleMeta.getWellReportToAddressFaxPhone(),
                            SampleMeta.getWellReportToAddressFaxPhone());

        reportToAddressFieldMap = new HashMap<String, String>();
        reportToAddressFieldMap.put(SampleMeta.getWellReportToName(),
                                    SampleMeta.getWellReportToName());
        reportToAddressFieldMap.put(SampleMeta.getAddressMultipleUnit(),
                                    SampleMeta.getAddressMultipleUnit());
        reportToAddressFieldMap.put(SampleMeta.getAddressStreetAddress(),
                                    SampleMeta.getAddressStreetAddress());
        reportToAddressFieldMap.put(SampleMeta.getAddressCity(), SampleMeta.getAddressCity());
        reportToAddressFieldMap.put(SampleMeta.getAddressState(), SampleMeta.getAddressState());
        reportToAddressFieldMap.put(SampleMeta.getAddressZipCode(), SampleMeta.getAddressZipCode());
        reportToAddressFieldMap.put(SampleMeta.getAddressWorkPhone(),
                                    SampleMeta.getAddressWorkPhone());
        reportToAddressFieldMap.put(SampleMeta.getAddressFaxPhone(),
                                    SampleMeta.getAddressFaxPhone());
    }

    public ArrayList<IdAccessionVO> query(ArrayList<QueryData> fields, int first, int max) throws Exception {
        String queryString, whereForFrom, sampleWhere, privateWellWhere;
        Query query;
        QueryBuilderV2 builder;
        List list;
        ArrayList<QueryData> wellFields;

        builder = new QueryBuilderV2();
        builder.setMeta(meta);
        builder.setSelect("distinct new org.openelis.domain.IdAccessionVO(" + SampleMeta.getId() +
                          "," + SampleMeta.getAccessionNumber() + ") ");
        builder.constructWhere(fields);
        builder.setOrderBy(SampleMeta.getAccessionNumber());

        whereForFrom = builder.getWhereClause();

        // for the well screen we have to link to the org table and the address
        // table
        // with the same textboxes. So if they queried by these fields we need
        // to add
        // a link to the 2nd table
        wellFields = new ArrayList<QueryData>();
        privateWellWhere = createWhereFromWellFields(fields, wellFields);
        builder.clearWhereClause();
        builder.constructWhere(fields);
        sampleWhere = builder.getWhereClause();

        queryString = builder.getSelectClause() + builder.getFromClause(whereForFrom) +
                      sampleWhere + privateWellWhere + builder.getOrderBy();

        query = manager.createQuery(queryString);
        query.setMaxResults(first + max);

        // add the well fields we created
        fields.addAll(wellFields);
        builder.setQueryParams(query, fields);

        try {
            list = query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

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
        try {
            return (SampleDO)query.getSingleResult();
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }

    }

    public SampleDO fetchByAccessionNumber(Integer accessionNumber) throws Exception {
        Query query;

        query = manager.createNamedQuery("Sample.FetchByAccessionNumber");
        query.setParameter("accession", accessionNumber);
        try {
            return (SampleDO)query.getSingleResult();
        } catch (NoResultException e) {
            throw new NotFoundException("noRecordsFound");
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
    }

    public ArrayList<SampleDO> fetchSDWISByReleasedAndLocation(Date startDate, Date endDate, String location) throws Exception {
        List<SampleDO> list;
        Query query;

        query = manager.createNamedQuery("Sample.FetchSDWISByReleasedAndLocation");
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        query.setParameter("location", "%"+location);
        
        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();
        
        return DataBaseUtil.toArrayList(list);
    }

    public ArrayList<Object[]> fetchSamplesForFinalReportBatch() throws Exception {
        Query query;

        query = manager.createNamedQuery("Sample.FetchSamplesForFinalReportBatch");
        return DataBaseUtil.toArrayList(query.getResultList());
    }

    public ArrayList<Object[]> fetchSamplesForFinalReportSingle(Integer sampleId) throws Exception {
        Query query;

        query = manager.createNamedQuery("Sample.FetchSamplesForFinalReportSingle");
        query.setParameter("sampleId", sampleId);
        return DataBaseUtil.toArrayList(query.getResultList());
    }

    public ArrayList<Object[]> fetchSamplesForFinalReportPreview(Integer sampleId) throws Exception {
        Query query;

        query = manager.createNamedQuery("Sample.FetchSamplesForFinalReportPreview");
        query.setParameter("sampleId", sampleId);
        return DataBaseUtil.toArrayList(query.getResultList());
    }
    
    public ArrayList<Object[]> fetchForClientEmailReceivedReport(Date stDate, Date endDate) throws Exception {
        Query query;
        ArrayList<Object[]> list;       
        query = manager.createNamedQuery("Sample.FetchForClientEmailReceivedReport");     
        query.setParameter("start_received_date", stDate);
        query.setParameter("end_received_date", endDate);
            
        try {
            list = DataBaseUtil.toArrayList(query.getResultList());
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        if (list.isEmpty()|| list==null)
            throw new NotFoundException("noRecordsFound");
        else
            return list;        
    } 
    
    public ArrayList<Object[]> fetchForClientEmailReleasedReport(Date stDate, Date endDate) throws Exception {
        Query query;
        ArrayList<Object[]> list;       
        query = manager.createNamedQuery("Sample.FetchForClientEmailReleasedReport");     
        query.setParameter("start_received_date", stDate);
        query.setParameter("end_received_date", endDate);
        
        try {
            list = DataBaseUtil.toArrayList(query.getResultList());
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        if (list.isEmpty()|| list==null)
            throw new NotFoundException("noRecordsFound");
        else
            return list;        
    } 

    public ArrayList<IdNameVO> fetchProjectsForOrganizations(ArrayList<Integer> organizationIdList) throws Exception {
        Query query;

        query = manager.createNamedQuery("Sample.FetchProjectsForOrganizations");
        query.setParameter("organizationIds", organizationIdList);
        return DataBaseUtil.toArrayList(query.getResultList());
    }
    
    public ArrayList<IdNameVO> fetchProjectsForPrivateOrganizations(ArrayList<Integer> organizationIdList) throws Exception {
        Query query;
               
        query = manager.createNamedQuery("Sample.FetchProjectsForPrivateOrganizations");
        query.setParameter("organizationIds", organizationIdList);
        return DataBaseUtil.toArrayList(query.getResultList());
    }
    
    public ArrayList<IdNameVO> fetchProjectsForSampleStatusReport(ArrayList<Integer> organizationIdList) throws Exception {
        Query query;
        ArrayList<Object[]> list; 
        ArrayList<IdNameVO> returnList;
        IdNameVO obj;
        Object[] result;
        Integer id;
        String description;
        
        returnList = new ArrayList<IdNameVO>();
        query = manager.createNamedQuery("Sample.FetchProjectsForSampleStatusReport");
        query.setParameter("organizationIds", organizationIdList);
       
        try {
            list = DataBaseUtil.toArrayList(query.getResultList());
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        
        for (int i = 0; i < list.size(); i++ ) {
            result = list.get(i);
            id = (Integer)result[0];
            description = (String)result[1];
            obj = new IdNameVO(id, description);
            returnList.add(obj);
        }
       return returnList;     
    }
    
    public ArrayList<SampleStatusWebReportVO> fetchSampleAnalysisInfoForSampleStatusReportEnvironmental(ArrayList<Integer> sampleIdList) throws Exception {
        Query query;
               
        query = manager.createNamedQuery("Sample.FetchSampleAnalysisInfoForSampleStatusReportEnvironmental");
        query.setParameter("sampleIds", sampleIdList);
        return DataBaseUtil.toArrayList(query.getResultList());
    }

    public ArrayList<SampleStatusWebReportVO> fetchSampleAnalysisInfoForSampleStatusReportPrivateWell(ArrayList<Integer> sampleIdList) throws Exception {
        Query query;
               
        query = manager.createNamedQuery("Sample.FetchSampleAnalysisInfoForSampleStatusReportPrivateWell");
        query.setParameter("sampleIds", sampleIdList);
        return DataBaseUtil.toArrayList(query.getResultList());
    }
    
    public ArrayList<SampleStatusWebReportVO> fetchSampleAnalysisInfoForSampleStatusReportSDWIS(ArrayList<Integer> sampleIdList) throws Exception {
        Query query;
               
        query = manager.createNamedQuery("Sample.FetchSampleAnalysisInfoForSampleStatusReportSDWIS");
        query.setParameter("sampleIds", sampleIdList);
        return DataBaseUtil.toArrayList(query.getResultList());
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

        if ( !data.isChanged())
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

    private String createWhereFromWellFields(ArrayList<QueryData> fields,
                                             ArrayList<QueryData> wellFields) {
        int i;
        String whereClause;
        QueryFieldUtil qField;
        QueryData field;

        whereClause = "";
        //
        // we extract the fields that belong only to sample private well
        // and its related organization/report to; we need to remove these
        // fields from the list sent to the bean because we don't want them to
        // be included in the where clause created from those fields, because we
        // would get repetitive and thus erroneous clauses
        //
        for (i = 0; i < fields.size(); i++ ) {
            field = fields.get(i);
            qField = new QueryFieldUtil();
            qField.parse(field.query);

            if (wellOrgFieldMap.get(field.key) != null) {
                wellFields.add(fields.remove(i));
                i-- ;
            } else if (reportToAddressFieldMap.get(field.key) != null) {
                wellFields.add(fields.remove(i));
                i-- ;
            }
        }

        //
        // we create a where clause from the fields obtained from the previous
        // code
        //
        for (i = 0; i < wellFields.size(); i++ ) {
            field = wellFields.get(i);
            qField = new QueryFieldUtil();
            qField.parse(field.query);

            if (i % 2 == 0) {
                whereClause += " and ( " + QueryBuilderV2.getQueryNoOperand(qField, field.key);
            } else {
                whereClause += " or " + QueryBuilderV2.getQueryNoOperand(qField, field.key) + " ) ";
            }
        }
        return whereClause;
    }

}
