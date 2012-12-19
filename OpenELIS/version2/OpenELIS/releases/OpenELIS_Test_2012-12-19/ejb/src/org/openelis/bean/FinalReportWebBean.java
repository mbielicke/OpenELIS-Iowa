package org.openelis.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.FinalReportWebVO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.SampleProjectViewDO;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.local.DictionaryLocal;
import org.openelis.local.ProjectLocal;
import org.openelis.local.SampleProjectLocal;
import org.openelis.local.SessionCacheLocal;
import org.openelis.meta.SampleWebMeta;
import org.openelis.remote.FinalReportWebRemote;
import org.openelis.util.QueryBuilderV2;
import org.openelis.utils.EJBFactory;

@Stateless
@SecurityDomain("openelis")

public class FinalReportWebBean implements FinalReportWebRemote {

    @EJB
    private SessionCacheLocal          session;

    @EJB
    private DictionaryLocal            dictionary;

    @EJB
    private SampleProjectLocal         sampleProject;
    
    @EJB
    private ProjectLocal               project;
    
    @PersistenceContext(unitName = "openelis")
    private EntityManager              manager;

    private static Integer             organizationReportToId, sampleInErrorId, analysisReleasedId;

    private static final SampleWebMeta meta = new SampleWebMeta();

    private static final Logger        log = Logger.getLogger("openelis");

    @PostConstruct
    public void init() {
        try {
            organizationReportToId = dictionary.fetchBySystemName("org_report_to").getId();
            sampleInErrorId = dictionary.fetchBySystemName("sample_error").getId();
            analysisReleasedId = dictionary.fetchBySystemName("analysis_released").getId();
        } catch (Throwable e) {
            log.log(Level.SEVERE, "Failed to lookup constants for dictionary entries", e);
        }
    }     

    @RolesAllowed("w_final_environmental-select")
    public ArrayList<FinalReportWebVO> getSampleEnvironmentalList(ArrayList<QueryData> fields) throws Exception {
        Integer id;
        String clause, projName;
        Query query;        
        QueryBuilderV2 builder;
        Date collDateTime, collTime;
        List<Object[]> results;
        ArrayList<SampleProjectViewDO> sprjList;
        ArrayList<FinalReportWebVO> returnList;

        /*
         * Retrieving the organization Ids to which the user belongs to from the
         * security clause in the userPermission.
         */
        clause = EJBFactory.getUserCache()
                           .getPermission()
                           .getModule("w_final_environmental")
                           .getClause();
        /*
         * if clause is null, then the previous method returns an empty HashMap,
         * so we need to check if orgIds and projIds are empty or not.
         */
        if (clause == null)
            return new ArrayList<FinalReportWebVO>();

        builder = new QueryBuilderV2();
        builder.setMeta(meta);
        builder.setSelect("distinct " +SampleWebMeta.getId() + ", " +
                          SampleWebMeta.getAccessionNumber() + ", " +
                          SampleWebMeta.getRevision() + ", " +
                          SampleWebMeta.getDomain() + ", " +
                          SampleWebMeta.getCollectionDate() + ", " +
                          SampleWebMeta.getCollectionTime() + ", " +
                          SampleWebMeta.getStatusId() + ", " +
                          SampleWebMeta.getEnvLocation() + ", " +
                          SampleWebMeta.getEnvCollector() + ", " +
                          SampleWebMeta.getLocationAddrCity() + ", " +
                          SampleWebMeta.getSampleOrgOrganizationId());
        builder.constructWhere(fields);       
        builder.addWhere("("+clause+")");
        builder.addWhere(SampleWebMeta.getEnvSampleId() + "=" + SampleWebMeta.getId());
        builder.addWhere(SampleWebMeta.getSampleOrgTypeId() + "=" + organizationReportToId);
        builder.addWhere(SampleWebMeta.getStatusId() + "!=" + sampleInErrorId);
        builder.addWhere(SampleWebMeta.getItemSampleId() + "=" + SampleWebMeta.getId());
        builder.addWhere(SampleWebMeta.getAnalysisSampleItemId() + "=" + SampleWebMeta.getItemId());
        builder.addWhere(SampleWebMeta.getAnalysisStatusId() + "=" + analysisReleasedId);
        builder.addWhere(SampleWebMeta.getAnalysisIsReportable() + "=" + "'Y'");

        builder.setOrderBy(SampleWebMeta.getAccessionNumber());

        query = manager.createQuery(builder.getEJBQL());
        builder.setQueryParams(query, fields);
        results = query.getResultList();

        returnList = new ArrayList<FinalReportWebVO>();
        for (Object[] result : results) {
            collDateTime = (Date)result[4];
            collTime = (Date)result[5];
            
            if (collDateTime != null) {
                if (collTime == null) {
                    collDateTime.setHours(0);
                    collDateTime.setMinutes(0);
                } else {
                    collDateTime.setHours(collTime.getHours());
                    collDateTime.setMinutes(collTime.getMinutes());
                }
            }
            returnList.add(new FinalReportWebVO((Integer)result[0], (Integer)result[1],
                                                (Integer)result[2], (String)result[3],
                                                collDateTime, (Integer)result[6],
                                                (String)result[7], (String)result[8],
                                                (String)result[9], (Integer)result[10], 
                                                null, null, null, null, null));
        }
        /*
         * From the retrieved list of samples, find the first permanent project
         * they belong to
         */
        for (FinalReportWebVO webVO : returnList) {
            id = webVO.getId();
            try {
                sprjList = sampleProject.fetchPermanentBySampleId(id);
                projName = sprjList.get(0).getProjectName();
            } catch (NotFoundException e) {
                projName ="";
            }
            webVO.setProjectName(projName);
        }
        /*
         * push the retrieved list of samples into session so that the system
         * can find the list of samples from the back end and use the indices
         * the user selects in the front end to select the samples to run the
         * report for.
         */
        session.setAttribute("sampleList", returnList);
        return returnList;
    }

    @RolesAllowed("w_final_privatewell-select")
    public ArrayList<FinalReportWebVO> getSamplePrivateWellList(ArrayList<QueryData> fields) throws Exception {
        String clause;
        Query query;
        QueryBuilderV2 builder;
        List<Object[]> results;
        Date collDateTime, collTime;
        ArrayList<FinalReportWebVO> returnList;        
        /*
         * Retrieving the organization Ids to which the user belongs to from the
         * security clause in the userPermission.
         */
        clause = EJBFactory.getUserCache()
                           .getPermission()
                           .getModule("w_final_privatewell")
                           .getClause();
        /*
         * if clause is null, then the previous method returns an empty HashMap,
         * so we need to check if orgIds is empty or not.
         */
        if (clause == null)
            return new ArrayList<FinalReportWebVO>();

        builder = new QueryBuilderV2();
        builder.setMeta(meta);
        builder.setSelect("distinct " +SampleWebMeta.getId() + ", " +
                          SampleWebMeta.getAccessionNumber() + ", " +
                          SampleWebMeta.getRevision() + ", " +
                          SampleWebMeta.getDomain() + ", " +
                          SampleWebMeta.getCollectionDate() + ", " +
                          SampleWebMeta.getCollectionTime() + ", " +
                          SampleWebMeta.getStatusId() + ", " + 
                          SampleWebMeta.getWellLocation() + ", " +
                          SampleWebMeta.getWellCollector() + ", " +
                          SampleWebMeta.getWellOrganizationAddrCity() + ", " +
                          SampleWebMeta.getWellOrganizationId() + ", " +
                          SampleWebMeta.getWellOwner());
        builder.constructWhere(fields);
        builder.addWhere("("+clause+")");
        builder.addWhere(SampleWebMeta.getWellSampleId() + "=" + SampleWebMeta.getId());
        builder.addWhere(SampleWebMeta.getWellOrganizationAddressId() + "=" +
                         SampleWebMeta.getWellOrganizationAddrId());
        builder.addWhere(SampleWebMeta.getStatusId() + " !=" + sampleInErrorId);
        builder.addWhere(SampleWebMeta.getItemSampleId() + "=" + SampleWebMeta.getId());
        builder.addWhere(SampleWebMeta.getAnalysisSampleItemId() + "=" + SampleWebMeta.getItemId());
        builder.addWhere(SampleWebMeta.getAnalysisStatusId() + "=" + analysisReleasedId);
        builder.addWhere(SampleWebMeta.getAnalysisIsReportable() + "=" + "'Y'");

        builder.setOrderBy(SampleWebMeta.getAccessionNumber());

        query = manager.createQuery(builder.getEJBQL());
        builder.setQueryParams(query, fields);
        results = query.getResultList();

        returnList = new ArrayList<FinalReportWebVO>();
        for (Object[] result : results) {
            collDateTime = (Date)result[4];
            collTime = (Date)result[5];
            
            if (collDateTime != null) {
                if (collTime == null) {
                    collDateTime.setHours(0);
                    collDateTime.setMinutes(0);
                } else {
                    collDateTime.setHours(collTime.getHours());
                    collDateTime.setMinutes(collTime.getMinutes());
                }
            }
            returnList.add(new FinalReportWebVO((Integer)result[0], (Integer)result[1],
                                                (Integer)result[2], (String)result[3],
                                                collDateTime, (Integer)result[6],
                                                (String)result[7], (String)result[8],
                                                (String)result[9], (Integer)result[10], 
                                                null, null, null, null, null));
        }
        /*
         * push the retrieved list of samples into session so that the system
         * can find the list of samples from the back end and use the indices
         * the user selects in the front end to select the samples to run the
         * report for.
         */
        session.setAttribute("sampleList", returnList);
        return returnList;
    }

    @RolesAllowed("w_final_sdwis-select")
    public ArrayList<FinalReportWebVO> getSampleSDWISList(ArrayList<QueryData> fields) throws Exception {
        String clause;
        Query query;
        QueryBuilderV2 builder;
        List<Object[]> results;
        ArrayList<FinalReportWebVO> returnList;
        Date collDateTime, collTime;
        /*
         * Retrieving the organization Ids to which the user belongs to from the
         * security clause in the userPermission.
         */
        clause = EJBFactory.getUserCache().getPermission().getModule("w_final_sdwis").getClause();

        if (clause == null)
            return new ArrayList<FinalReportWebVO>();
        
        builder = new QueryBuilderV2();
        builder.setMeta(meta);
        builder.setSelect("distinct " + SampleWebMeta.getId() + ", " +
                          SampleWebMeta.getAccessionNumber() + ", " +
                          SampleWebMeta.getRevision() + ", " +
                          SampleWebMeta.getDomain() + ", " +
                          SampleWebMeta.getCollectionDate() + ", " +
                          SampleWebMeta.getCollectionTime() + ", " + 
                          SampleWebMeta.getStatusId() + ", " +
                          SampleWebMeta.getSDWISLocation() + ", " +
                          SampleWebMeta.getSDWISCollector() + ", " +
                          SampleWebMeta.getSampleOrgOrganizationId() + ", " +
                          SampleWebMeta.getPwsNumber0() + ", " +
                          SampleWebMeta.getSDWISFacilityId() + ", " +
                          SampleWebMeta.getPwsName());
        builder.constructWhere(fields);
        builder.addWhere("("+clause+")");
        builder.addWhere(SampleWebMeta.getSDWISSampleId() + "=" + SampleWebMeta.getId());
        builder.addWhere(SampleWebMeta.getSDWISPwsId() + "=" + SampleWebMeta.getPwsId());
        builder.addWhere(SampleWebMeta.getSampleOrgTypeId() + "=" + organizationReportToId);
        builder.addWhere(SampleWebMeta.getStatusId() + "!=" + sampleInErrorId);
        builder.addWhere(SampleWebMeta.getItemSampleId() + "=" + SampleWebMeta.getId());
        builder.addWhere(SampleWebMeta.getAnalysisSampleItemId() + "=" + SampleWebMeta.getItemId());
        builder.addWhere(SampleWebMeta.getAnalysisStatusId() + "=" + analysisReleasedId);
        builder.addWhere(SampleWebMeta.getAnalysisIsReportable() + "=" + "'Y'");

        builder.setOrderBy(SampleWebMeta.getAccessionNumber());

        query = manager.createQuery(builder.getEJBQL());
        builder.setQueryParams(query, fields);
        results = query.getResultList();

        returnList = new ArrayList<FinalReportWebVO>();
        for (Object[] result : results) {
            collDateTime = (Date)result[4];
            collTime = (Date)result[5];
            
            if (collDateTime != null) {
                if (collTime == null) {
                    collDateTime.setHours(0);
                    collDateTime.setMinutes(0);
                } else {
                    collDateTime.setHours(collTime.getHours());
                    collDateTime.setMinutes(collTime.getMinutes());
                }
            }
            returnList.add(new FinalReportWebVO((Integer)result[0], (Integer)result[1],
                                                (Integer)result[2], (String)result[3],
                                                collDateTime, (Integer)result[6],
                                                (String)result[7], (String)result[8],
                                                null, (Integer)result[9], null, 
                                                null, (String)result[10], (String)result[11],
                                                (String)result[12]));
        }
        /*
         * push the retrieved list of samples into session so that the system
         * can find the list of samples from the back end and use the indices
         * the user selects in the front end to select the samples to run the
         * report for.
         */
        session.setAttribute("sampleList", returnList);
        return returnList;
    }

    public ArrayList<IdNameVO> getEnvironmentalProjectList() throws Exception {
        String clause;

        clause = EJBFactory.getUserCache()
                           .getPermission()
                           .getModule("w_final_environmental")
                           .getClause();
        /*
         * if clause is null, then the previous method returns an empty HashMap,
         * so we need to check if the list is empty or not. We only return the
         * list of projects
         */
        if (clause != null)             
            return project.fetchForOrganizations(clause);        
        
        return new ArrayList<IdNameVO>();
    }

    public ArrayList<IdNameVO> getPrivateWellProjectList() throws Exception {
        String clause;

        clause = EJBFactory.getUserCache()
                           .getPermission()
                           .getModule("w_final_privatewell")
                           .getClause();

        /*
         * if clause is null, then the previous method returns an empty HashMap,
         * so we need to check if the list is empty or not.
         */
        if (clause != null)
            return project.fetchForOrganizations(clause);

        return new ArrayList<IdNameVO>();
    }
    
    public ArrayList<IdNameVO> getSDWISProjectList() throws Exception {
        String clause;

        clause = EJBFactory.getUserCache()
                           .getPermission()
                           .getModule("w_final_sdwis")
                           .getClause();

        /*
         * if clause is null, then the previous method returns an empty HashMap,
         * so we need to check if the list is empty or not.
         */
        if (clause != null)
            return project.fetchForOrganizations(clause);

        return new ArrayList<IdNameVO>();
    }
}