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

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.openelis.domain.AddressDO;
import org.openelis.domain.AnalysisQaEventViewDO;
import org.openelis.domain.AnalysisUserViewDO;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.AuxDataDataViewVO;
import org.openelis.domain.AuxDataViewDO;
import org.openelis.domain.AuxFieldDataViewVO;
import org.openelis.domain.CategoryCacheVO;
import org.openelis.domain.DataViewAuxDataFetchVO;
import org.openelis.domain.DataViewResultFetchVO;
import org.openelis.domain.DataViewVO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.OrganizationDO;
import org.openelis.domain.PWSDO;
import org.openelis.domain.ReferenceTable;
import org.openelis.domain.ResultDataViewVO;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.SampleDO;
import org.openelis.domain.SampleEnvironmentalDO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.domain.SampleOrganizationViewDO;
import org.openelis.domain.SamplePrivateWellViewDO;
import org.openelis.domain.SampleProjectViewDO;
import org.openelis.domain.SampleSDWISViewDO;
import org.openelis.domain.TestAnalyteDataViewVO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.InconsistencyException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ReportStatus;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.local.AnalysisLocal;
import org.openelis.local.AnalysisQAEventLocal;
import org.openelis.local.AnalysisUserLocal;
import org.openelis.local.AuxDataLocal;
import org.openelis.local.CategoryCacheLocal;
import org.openelis.local.DictionaryCacheLocal;
import org.openelis.local.DictionaryLocal;
import org.openelis.local.PWSLocal;
import org.openelis.local.ProjectLocal;
import org.openelis.local.ResultLocal;
import org.openelis.local.SampleEnvironmentalLocal;
import org.openelis.local.SampleItemLocal;
import org.openelis.local.SampleLocal;
import org.openelis.local.SampleOrganizationLocal;
import org.openelis.local.SamplePrivateWellLocal;
import org.openelis.local.SampleProjectLocal;
import org.openelis.local.SampleQAEventLocal;
import org.openelis.local.SampleSDWISLocal;
import org.openelis.local.SessionCacheLocal;
import org.openelis.local.UserCacheLocal;
import org.openelis.meta.SampleWebMeta;
import org.openelis.remote.DataViewRemote;
import org.openelis.util.QueryBuilderV2;
import org.openelis.util.UTFResource;
import org.openelis.utils.EJBFactory;
import org.openelis.utils.ReportUtil;

@Stateless
@SecurityDomain("openelis")

public class DataViewBean implements DataViewRemote {
    
    @PersistenceContext(unitName = "openelis")
    private EntityManager                  manager;

    @EJB
    private SessionCacheLocal              session;

    @EJB
    private ProjectLocal                   project;
    
    @EJB
    private SampleProjectLocal             sampleProject;

    @EJB
    private SampleQAEventLocal             sampleQaEvent;

    @EJB
    private AnalysisQAEventLocal           analysisQaEvent;

    @EJB
    private ResultLocal                    result;

    @EJB
    private AuxDataLocal                   auxData;

    @EJB
    private DictionaryLocal                dictionary;

    @EJB
    private SampleLocal                    sample;
    
    @EJB
    private SampleOrganizationLocal        sampleOrganization;
    
    @EJB
    private SampleItemLocal                sampleItem; 
    
    @EJB
    private AnalysisLocal                  analysis;
    
    @EJB
    private AnalysisUserLocal              analysisUser;
    
    @EJB
    private SampleEnvironmentalLocal       sampleEnvironmental;  
    
    @EJB
    private SamplePrivateWellLocal         samplePrivateWell;  
    
    @EJB
    private SampleSDWISLocal               sampleSDWIS;
    
    @EJB
    private PWSLocal                       pws;
    
    @EJB
    private DictionaryCacheLocal           dictionaryCache; 
    
    @EJB
    private UserCacheLocal                 userCache; 
    
    private static Integer                organizationReportToId, sampleInErrorId, 
                                            resultDictId, auxFieldValueDictId, 
                                            completedActionId, releasedActionId, 
                                            releasedStatusId, qaResultOverrideTypeId,
                                            qaInternalTypeId;   

    private static final SampleWebMeta    meta = new SampleWebMeta();

    private static final Logger           log = Logger.getLogger("openelis");

    private static UTFResource              resource;

    private static HashMap<Integer, String> dictEntryMap;
    
    @PostConstruct
    public void init() {
        ArrayList<DictionaryDO> list;
        CategoryCacheVO cat;
        String locale;      
        CategoryCacheLocal ccl;
        
        if (resultDictId == null) {
            ccl = EJBFactory.getCategoryCache();
            try {
                resultDictId = dictionary.fetchBySystemName("test_res_type_dictionary").getId();
                auxFieldValueDictId = dictionary.fetchBySystemName("aux_dictionary").getId();
                completedActionId = dictionary.fetchBySystemName("an_user_ac_completed").getId(); 
                releasedActionId = dictionary.fetchBySystemName("an_user_ac_released").getId();
                organizationReportToId = dictionary.fetchBySystemName("org_report_to").getId();
                qaResultOverrideTypeId = dictionary.fetchBySystemName("qaevent_override").getId();
                qaInternalTypeId = dictionary.fetchBySystemName("qaevent_internal").getId();
                
                dictEntryMap = new HashMap<Integer, String>();
                
                cat = ccl.getBySystemName("sample_status");
                list = cat.getDictionaryList();
                for (DictionaryDO data : list) {
                    if ("sample_error".equals(data.getSystemName()))
                        sampleInErrorId = data.getId();
                    dictEntryMap.put(data.getId(), data.getEntry());
                }
                
                cat = ccl.getBySystemName("analysis_status");
                list = cat.getDictionaryList();
                for (DictionaryDO data : list) {
                    if ("analysis_released".equals(data.getSystemName()))
                        releasedStatusId = data.getId();
                    dictEntryMap.put(data.getId(), data.getEntry());
                }
                
                cat = ccl.getBySystemName("sdwis_sample_type");
                list = cat.getDictionaryList();
                for (DictionaryDO data : list) 
                    dictEntryMap.put(data.getId(), data.getEntry()); 
                
                cat = ccl.getBySystemName("sdwis_sample_category");
                list = cat.getDictionaryList();
                for (DictionaryDO data : list) 
                    dictEntryMap.put(data.getId(), data.getEntry());
                                
            } catch (Throwable e) {
                log.log(Level.SEVERE, "Failed to lookup constants for dictionary entries", e);
            }
        }
        
        try {
            if (resource == null) {
                try {
                    locale = EJBFactory.getUserCache().getLocale();
                } catch (Exception e) {
                    locale = "en";
                }
                resource = UTFResource.getBundle("org.openelis.constants.OpenELISConstants",
                                                 new Locale(locale));
            }
        } catch (Throwable e) {
            log.log(Level.SEVERE, "Failed to initialize resource bundle", e);
        }
    }   
    
    @RolesAllowed("w_dataview_environmental-select")
    public ArrayList<IdNameVO> fetchEnvironmentalProjectListForWeb() throws Exception {
        String clause;

        clause = EJBFactory.getUserCache()
                           .getPermission()
                           .getModule("w_dataview_environmental")
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
    
    @TransactionTimeout(180)
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public DataViewVO fetchAnalyteAndAuxField(DataViewVO data) throws Exception {
        ArrayList<QueryData> fields;
        
        if (data == null)
            throw new InconsistencyException("You may not execute an empty query");      
        
        fields = data.getQueryFields();
        if (fields == null || fields.size() == 0)
            throw new InconsistencyException("You may not execute an empty query");      
                
        return fetchAnalyteAndAuxField(data, null);
    }
    
    @TransactionTimeout(180)
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public DataViewVO fetchAnalyteAndAuxFieldForWebEnvironmental(DataViewVO data) throws Exception {
        ArrayList<QueryData> fields;
        QueryData field;
       
        if (data == null)
            throw new InconsistencyException("You may not execute an empty query");      
        
        fields = data.getQueryFields();
        if (fields == null || fields.size() == 0)
            throw new InconsistencyException("You may not execute an empty query");
        
        field = new QueryData();
        field.key = SampleWebMeta.getDomain();
        field.query = "E";
        field.type = QueryData.Type.STRING;
        
        fields.add(field);
        
        return fetchAnalyteAndAuxField(data, "w_dataview_environmental");
    }
    

    @RolesAllowed("w_dataview_environmental-select")
    @TransactionTimeout(600)    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public ReportStatus runReportForWebEnvironmental(DataViewVO data) throws Exception {
        ArrayList<QueryData> fields;
        QueryData field;
        
        fields = data.getQueryFields();
        if (fields == null || fields.size() == 0)
            throw new InconsistencyException("You may not execute an empty query");
        
        field = new QueryData();
        field.key = SampleWebMeta.getDomain();
        field.query = "E";
        field.type = QueryData.Type.STRING;
        
        fields.add(field);
        
        return runReport(data, "w_dataview_environmental", true);
    }

    @TransactionTimeout(600)
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public ReportStatus runReport(DataViewVO data) throws Exception {
        return runReport(data, null, false);
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public ReportStatus saveQuery(DataViewVO data) throws Exception {
        FileOutputStream fos;
        File tempFile;
        ReportStatus status;
        XMLEncoder enc;
        
        status = new ReportStatus();
        status.setMessage("Initializing report");
        session.setAttribute("DataViewQuery", status);
        fos = null;
        enc = null;
        try {
            status.setMessage("Saving query").setPercentComplete(20);
            tempFile = File.createTempFile("query", ".xml", new File("/tmp"));
            
            status.setPercentComplete(100);

            fos = new FileOutputStream(tempFile);
            enc = new XMLEncoder(fos);
            enc.writeObject(data);
            /*
             * the FileOutputStream gets closed by the XMLEncoder, and so we don't
             * close it explicitly because trying to do so throws an exception  
             */
            enc.close();
            tempFile = ReportUtil.saveForUpload(tempFile);
            status.setMessage(tempFile.getName())
                .setPath(ReportUtil.getSystemVariableValue("upload_stream_directory"))
                .setStatus(ReportStatus.Status.SAVED);            
        } catch (Exception e) {            
            if (fos != null) 
                fos.close();
            if (enc != null) 
                enc.close();
            e.printStackTrace();
            throw e;
        } 
        
        return status;
    }   
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public DataViewVO loadQuery(String path) throws Exception {
        DataViewVO data;
        XMLDecoder dec;
                
        dec = new XMLDecoder(new FileInputStream(path));
        data = (DataViewVO)dec.readObject();        
        dec.close(); 
        
        return data;
    }
       
    private DataViewVO fetchAnalyteAndAuxField(DataViewVO data, String moduleName) throws Exception {
        int i;
        boolean excludeOverride, excludeResults, excludeAuxData;
        Integer samId, prevSamId, analysisId;
        QueryBuilderV2 builder;
        QueryData reportTo;
        ArrayList<QueryData> fields;
        List<Object[]> list;
        ArrayList<Integer> analysisIds, sampleIds;
        ArrayList<ResultViewDO> resList;
        ArrayList<AuxDataViewDO> auxList;
        Object[] vo;
    
        excludeOverride = "Y".equals(data.getExcludeResultOverride());
        excludeResults = "Y".equals(data.getExcludeResults());
        excludeAuxData = "Y".equals(data.getExcludeAuxData());
        
        fields = data.getQueryFields();
                
        builder = new QueryBuilderV2();
        builder.setMeta(meta);
        builder.setSelect("distinct " + SampleWebMeta.getAnalysisId() + ", " + SampleWebMeta.getId() + " ");
        /*
         * if moduleName is not null, then this query is being executed for the 
         * web and we need to report only released analysis
         */
        if (moduleName != null) {
            builder.addWhere(SampleWebMeta.getAnalysisStatusId() + "=" + releasedStatusId);
            builder.addWhere("("+getClause(moduleName)+")");
            builder.addWhere(SampleWebMeta.getAnalysisIsReportable() + "="+ "'Y'");
        }        
        
        reportTo = null;
        for (QueryData f : fields) {
            if ("reportToOrganizationName".equals(f.key)) {
                reportTo = f;
                break;
            }
        }
        list = new ArrayList<Object[]>();
        
        if (reportTo != null) { 
            reportTo.key = SampleWebMeta.getSampleOrgOrganizationName();
            builder.addWhere(SampleWebMeta.getSampleOrgTypeId() +" = "+ organizationReportToId);
            builder.addWhere(SampleWebMeta.getItemId() + "=" + SampleWebMeta.getAnalysisSampleItemId());            
            list.addAll(fetchAnalyteAndAuxField(SampleWebMeta.getId(), builder, fields));   
            
            builder.clearWhereClause();
            
            reportTo.key = SampleWebMeta.getWellOrganizationName();
            builder.addWhere(SampleWebMeta.getItemId() + "=" + SampleWebMeta.getAnalysisSampleItemId());
            list.addAll(fetchAnalyteAndAuxField(SampleWebMeta.getId(), builder, fields));
        } else {
            builder.addWhere(SampleWebMeta.getItemId() + "=" + SampleWebMeta.getAnalysisSampleItemId());   
            list = fetchAnalyteAndAuxField(SampleWebMeta.getId(), builder, fields);
        }
                
        if (list.isEmpty())
            throw new NotFoundException();
    
        analysisIds = new ArrayList<Integer>();
        sampleIds = new ArrayList<Integer>();
        prevSamId = null;        
        if (excludeOverride) {
            /*
             * if qa event(s) of type result override are found for a sample then
             * all the results under it are excluded, i.e. the sample's id is
             * not added to the list that's used for the query executed to fetch
             * the results, whereas if an analysis has such qa event(s) then only
             * its results are excluded
             */
            i = 0;
            while (i < list.size()) {
                vo = list.get(i++);
                analysisId = (Integer)vo[0];
                samId = (Integer)vo[1];
                if ( !samId.equals(prevSamId)) {
                    try {
                        sampleQaEvent.fetchResultOverrideBySampleId(samId);
                        // we found result override qa event(s) for the sample
                        while (i < list.size() && samId.equals(list.get(i)[1]))
                            i++ ;
                        prevSamId = null;
                        continue;
                    } catch (NotFoundException e) {
                        /*
                         * add the id to the list of samples that will be used
                         * to query for aux data
                         */
                        sampleIds.add(samId);
                    }
                }
                if ( !excludeResults) {
                    try {
                        // we found result override qa event(s) for an analysis
                        analysisQaEvent.fetchResultOverrideByAnalysisId(analysisId);
                    } catch (NotFoundException e1) {
                        analysisIds.add(analysisId);
                    }
                }
                prevSamId = samId;
            }
        } else {
            for (i = 0; i < list.size(); i++ ) {
                vo = list.get(i);
                analysisIds.add((Integer)vo[0]);
                samId = (Integer)vo[1];
                /*
                 * add the id to the list of samples that will be used
                 * to query for aux data
                 */
                if ( !samId.equals(prevSamId))
                    sampleIds.add(samId);
                prevSamId = samId;
            }
        }
        
        resList = null;
        if ( !excludeResults) {
            try {
                if (analysisIds.size() > 0) {
                    /*
                     * fetch the results belonging to the analyses that weren't
                     * left out, if any, by the code above because of their
                     * results being overridden
                     */
                    resList = result.fetchForDataViewByAnalysisIds(analysisIds);
                    data.setAnalytes(getTestAnalytes(resList));
                }
            } catch (NotFoundException e) {
                // ignore
            }
        }
        
        auxList = null;
        if ( !excludeAuxData) {
            try {
                /*
                 * fetch all the aux data belonging to the samples that we
                 * fetched the results for in the previous query, i.e. if a
                 * sample was excluded because of its results being overriden
                 * then it won't be included in this list
                 */
                if (sampleIds.size() > 0) {
                    auxList = auxData.fetchForDataView(ReferenceTable.SAMPLE, sampleIds);
                    data.setAuxFields(getAuxFields(auxList));
                }
            } catch (NotFoundException e) {
                // ignore
            }
        }
        
        if (resList == null && auxList == null)
            throw new NotFoundException();
    
        return data;
    }

    private ReportStatus runReport(DataViewVO data, String moduleName, boolean showReportableColumnsOnly) throws Exception {
        boolean excludeResults, excludeAuxData, runForWeb, addSampleCells,  addOrgCells,
                addItemCells, addAnalysisCells, addEnvCells, addWellCells, addSDWISCells;
        ArrayList<String> allCols, cols;
        FileOutputStream out;
        File tempFile;
        List<DataViewResultFetchVO> resultList, noResAuxList;
        List<DataViewAuxDataFetchVO> auxDataList;
        ArrayList<TestAnalyteDataViewVO> anaList;
        ArrayList<AuxFieldDataViewVO> auxList;
        ReportStatus status;
        QueryBuilderV2 builder;
        QueryData reportTo;
        ArrayList<QueryData> fields;
        HashMap<String, String> resultMap, valueMap;
        HashMap<Integer, HashMap<String, String>> analyteResultMap, auxFieldValueMap;
        HSSFWorkbook wb;
        DataViewComparator comparator;
        
        status = new ReportStatus();
        status.setMessage("Initializing report");
        session.setAttribute("DataView", status);        
        
        fields = data.getQueryFields();
        
        excludeResults = "Y".equals(data.getExcludeResults());
        excludeAuxData = "Y".equals(data.getExcludeAuxData());
        
        builder = new QueryBuilderV2();
        builder.setMeta(meta);     
        
        resultList = noResAuxList = null;
        auxDataList = null;
        analyteResultMap = auxFieldValueMap = null;
        reportTo = null;
        
        for (QueryData f : fields) {
            if ("reportToOrganizationName".equals(f.key)) {
                reportTo = f;
                break;
            }
        }
        
        addSampleCells = addOrgCells = addItemCells = addAnalysisCells = addEnvCells = addWellCells = addSDWISCells = false;        
        allCols = new ArrayList<String>(); 
        //
        // get the labels to be displayed in the headers for the various fields 
        //
        cols = getSampleHeaders(data);         
        if (cols.size() > 0) {            
            allCols.addAll(cols);
            addSampleCells = true;
        }        
        
        cols = getOrganizationHeaders(data);    
        if (cols.size() > 0) {            
            allCols.addAll(cols);
            addOrgCells = true;
        }
        
        cols = getSampleItemHeaders(data);
        if (cols.size() > 0) {            
            allCols.addAll(cols);
            addItemCells = true;
        }
        
        cols = getAnalysisHeaders(data);    
        if (cols.size() > 0) {            
            allCols.addAll(cols);
            addAnalysisCells = true;
        }
        
        cols = getEnvironmentalHeaders(data);
        if (cols.size() > 0) {            
            allCols.addAll(cols);
            addEnvCells = true;
        }
                
        cols = getPrivateWellHeaders(data);    
        if (cols.size() > 0) {            
            allCols.addAll(cols);
            addWellCells = true;
        }
        
        cols = getSDWISHeaders(data);    
        if (cols.size() > 0) {            
            allCols.addAll(cols);
            addSDWISCells = true;
        }
        
        if ( !excludeResults || !excludeAuxData) {
            allCols.add(resource.getString("analyte"));
            allCols.add(resource.getString("value"));        
        }
        
        comparator = new DataViewComparator();
        
        if (excludeResults && excludeAuxData) {
            noResAuxList = getResults(addEnvCells, addSDWISCells, addWellCells, moduleName, 
                                      builder, reportTo, analyteResultMap, data, comparator);           
        } else {
            if ( !excludeResults) {
                anaList = data.getTestAnalytes();
                if (anaList != null) {
                    /*
                     * the analytes and results selected by the user are stored
                     * in this hashmap so that they can be used later on to
                     * select or reject adding a row for a result based on
                     * whether or not it belongs in the hashmap
                     */
                    analyteResultMap = new HashMap<Integer, HashMap<String, String>>();
                    for (TestAnalyteDataViewVO ana : anaList) {
                        if ("N".equals(ana.getIsIncluded()))
                            continue;
                        resultMap = new HashMap<String, String>();
                        for (ResultDataViewVO res : ana.getResults()) {
                            if ("Y".equals(res.getIsIncluded()))
                                resultMap.put(res.getValue(), res.getValue());
                        }
                        analyteResultMap.put(ana.getAnalyteId(), resultMap);
                    }
                }
                /*
                 * fetch fields related to results based on the analytes and
                 * values selected by the user from the lists associated with
                 * test analytes
                 */
                if (analyteResultMap != null && analyteResultMap.size() > 0) {
                    resultList = getResults(addEnvCells, addSDWISCells, addWellCells, moduleName, 
                                            builder, reportTo, analyteResultMap, data, comparator);
                }
            }
            
            if ( !excludeAuxData) {
                auxList = data.getAuxFields();
                if (auxList != null) {
                    /*
                     * the analytes and values selected by the user are stored
                     * in this hashmap so that they can be used later on to
                     * select or reject adding a row for an aux data based on
                     * whether or not it belongs in the hashmap
                     */
                    auxFieldValueMap = new HashMap<Integer, HashMap<String, String>>();
                    for (AuxFieldDataViewVO af : auxList) {
                        if ("N".equals(af.getIsIncluded()))
                            continue;
                        valueMap = new HashMap<String, String>();
                        for (AuxDataDataViewVO val : af.getValues()) {
                            if ("Y".equals(val.getIsIncluded()))
                                valueMap.put(val.getValue(), val.getValue());
                        }
                        auxFieldValueMap.put(af.getAnalyteId(), valueMap);
                    }
                }
                /*
                 * fetch fields related to aux data based on the analytes and
                 * values selected by the user from the lists associated with
                 * aux fields
                 */
                builder.clearWhereClause();
                if (auxFieldValueMap != null && auxFieldValueMap.size() > 0)
                    auxDataList = getAuxData(addEnvCells, addSDWISCells, addWellCells, moduleName, builder,
                                             reportTo, auxFieldValueMap, data, comparator);
            }
        }
        
        if ((resultList == null || resultList.size() == 0) && (auxDataList == null || auxDataList.size() == 0) &&
                        (noResAuxList == null || noResAuxList.size() == 0)) 
            throw new NotFoundException();
        
        runForWeb = (moduleName != null);
        wb = getWorkbook(resultList, auxDataList, noResAuxList, analyteResultMap,
                         auxFieldValueMap, allCols, runForWeb, showReportableColumnsOnly, 
                         addSampleCells,  addOrgCells,  addItemCells, addAnalysisCells,
                         addEnvCells, addWellCells, addSDWISCells, data);
        
        if (wb != null) {
            out = null;
            try {
                status.setMessage("Outputing report").setPercentComplete(20);
                tempFile = File.createTempFile("dataview", ".xls", new File("/tmp"));
                
                status.setPercentComplete(100);

                out = new FileOutputStream(tempFile);
                wb.write(out);
                out.close();
                tempFile = ReportUtil.saveForUpload(tempFile);
                status.setMessage(tempFile.getName())
                    .setPath(ReportUtil.getSystemVariableValue("upload_stream_directory"))
                    .setStatus(ReportStatus.Status.SAVED);
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            } finally {
                try {
                    if (out != null)
                        out.close();
                } catch (Exception e) {
                    // ignore
                }
            }
        }

        return status;
    }      
    
    private List<DataViewResultFetchVO> fetchResults(String moduleName, QueryBuilderV2 builder,
                                                     HashMap<Integer, HashMap<String, String>> analyteResultMap,
                                                     DataViewVO data) throws Exception {
        boolean excludeResults;
        ArrayList<String> orderBy;
        Query query;
        ArrayList<QueryData> fields;

        excludeResults = "Y".equals(data.getExcludeResults());
        fields = data.getQueryFields();

        if (excludeResults) {
            builder.setSelect("distinct new org.openelis.domain.DataViewResultFetchVO(" +
                              SampleWebMeta.getAccessionNumber() + ", " + SampleWebMeta.getId() + ", " +
                              SampleWebMeta.getDomain() + ", " + SampleWebMeta.getItemId() + ", " +
                              SampleWebMeta.getAnalysisId() + ")");
        } else {
            builder.setSelect("distinct new org.openelis.domain.DataViewResultFetchVO(" +
                              SampleWebMeta.getAccessionNumber() + ", " +
                              SampleWebMeta.getResultAnalysisid() + ", " + 
                              SampleWebMeta.getResultAnalyteName() + ", " +
                              SampleWebMeta.getId() + ", " + SampleWebMeta.getDomain() + ", " +
                              SampleWebMeta.getItemId() + ", " + 
                              SampleWebMeta.getResultIsColumn() + ", " +
                              SampleWebMeta.getResultAnalyteId() + ", " +
                              SampleWebMeta.getResultTypeId() + ", " +
                              SampleWebMeta.getResultValue() + ", " +
                              SampleWebMeta.getResultSortOrder() + ", " +
                              SampleWebMeta.getResultTestAnalyteRowGroup() + ")");
        }

        builder.constructWhere(fields);
        /*
         * If moduleName is present, then it means that this report is being run
         * for the samples belonging to the list of organizations specified in
         * this user's system_user_module for a specific domain.
         */
        if (moduleName != null) {
            builder.addWhere("(" + getClause(moduleName) + ")");
            builder.addWhere(SampleWebMeta.getStatusId() + "!=" + sampleInErrorId);
            builder.addWhere(SampleWebMeta.getAnalysisStatusId() + "=" + releasedStatusId);
            builder.addWhere(SampleWebMeta.getAnalysisIsReportable() + "="+ "'Y'");
        }

        builder.addWhere(SampleWebMeta.getItemId() + "=" + SampleWebMeta.getAnalysisSampleItemId());

        orderBy = new ArrayList<String>();
        orderBy.add(SampleWebMeta.getAccessionNumber());
        if ( !excludeResults) {
            builder.addWhere(SampleWebMeta.getResultIsReportable() + "=" + "'Y'");
            builder.addWhere(SampleWebMeta.getResultIsColumn() + "=" + "'N'");
            builder.addWhere(SampleWebMeta.getResultValue() + "!=" + "null");
            builder.addWhere(SampleWebMeta.getResultAnalyteId() +
                             getListParam(analyteResultMap.keySet()) + ")");
            orderBy.add(SampleWebMeta.getResultAnalysisid());
            orderBy.add(SampleWebMeta.getResultAnalyteName());
        }
        
        builder.setOrderBy(DataBaseUtil.concatWithSeparator(orderBy, ", "));
        query = manager.createQuery(builder.getEJBQL());
        builder.setQueryParams(query, fields);
        return query.getResultList();
    }

    private List<DataViewAuxDataFetchVO> fetchAuxData(String moduleName, QueryBuilderV2 builder,
                                                      HashMap<Integer, HashMap<String, String>> auxFieldValueMap,
                                                      DataViewVO data) throws Exception {
        Query query;
        ArrayList<QueryData> fields;

        fields = data.getQueryFields();
        
        builder.setSelect("distinct new org.openelis.domain.DataViewAuxDataFetchVO(" +
                          SampleWebMeta.getAccessionNumber() + ", " + 
                          SampleWebMeta.getAuxDataAuxFieldAnalyteName() + ", " +
                          SampleWebMeta.getId() + ", " + SampleWebMeta.getDomain() + ", " +
                          SampleWebMeta.getAuxDataAuxFieldAnalyteId() + ", " +
                          SampleWebMeta.getAuxDataTypeId() + ", " + SampleWebMeta.getAuxDataValue()+ ")");              
        builder.constructWhere(fields);
        /*
         * If moduleName is present, then it means that this report is being
         * run for the samples belonging to the list of organizations specified
         * in this user's system_user_module for a specific domain.
         */
        if (moduleName != null) {
            builder.addWhere("("+getClause(moduleName)+")");
            builder.addWhere(SampleWebMeta.getSampleOrgTypeId() + "=" + organizationReportToId);
            builder.addWhere(SampleWebMeta.getStatusId() + "!=" + sampleInErrorId); 
        }       
        
        builder.addWhere(SampleWebMeta.getAuxDataIsReportable() + "=" + "'Y'");
        builder.addWhere(SampleWebMeta.getAuxDataValue() + "!=" + "null");
        builder.addWhere(SampleWebMeta.getAuxDataAuxFieldAnalyteId() +
                         getListParam(auxFieldValueMap.keySet()) + ")");
        builder.setOrderBy(SampleWebMeta.getAccessionNumber() + "," +
                           SampleWebMeta.getAuxDataAuxFieldAnalyteName());
        query = manager.createQuery(builder.getEJBQL());
        builder.setQueryParams(query, fields);
        return query.getResultList();
    }    

    private HSSFWorkbook getWorkbook(List<DataViewResultFetchVO> resultList, 
                                     List<DataViewAuxDataFetchVO> auxDataList,     
                                     List<DataViewResultFetchVO> noResAuxList,
                                     HashMap<Integer, HashMap<String, String>> analyteResultMap,
                                     HashMap<Integer, HashMap<String, String>> auxFieldValueMap,
                                     ArrayList<String> allCols, boolean runForWeb,
                                     boolean showReportableColumnsOnly,
                                     boolean addSampleCells, boolean addOrgCells,
                                     boolean addItemCells, boolean addAnalysisCells,
                                     boolean addEnvCells, boolean addWellCells,
                                     boolean addSDWISCells, DataViewVO data) throws Exception {
        boolean excludeOverride, excludeResults, excludeAuxData, sampleOverriden,
                anaOverriden, addResultRow, addAuxDataRow, addNoResAuxRow;
        int     rowIndex, resIndex, auxIndex, noResAuxIndex, numResults, numAuxVals, 
                numNoResAuxVals, i, lastColumn;         
        Integer resAccNum, auxAccNum, sampleId, resSamId, auxSamId, itemId, analysisId, 
                prevSamId, prevItemId, prevAnalysisId, rowGroup, prevRowGroup, sortOrder,
                currSortOrder, prevSortOrder, currColumn, anaIndex;
        String  resultVal, auxDataVal, domain, qaeNames, compByNames, relByNames,
                userName, anaName;
        StringBuffer buf;
        DataViewResultFetchVO res, noResAux;
        DataViewAuxDataFetchVO aux;
        HSSFWorkbook wb;
        HSSFSheet sheet;
        Row headerRow, resRow, auxRow, noResAuxRow, currRow, prevRow;
        Cell cell;             
        CellStyle headerStyle;
        Datetime collDateTime, collDate, collTime;
        Date dc;  
        SampleDO sam;        
        SampleProjectViewDO proj;
        SampleOrganizationViewDO org;
        SampleEnvironmentalDO env;
        SamplePrivateWellViewDO well;
        SampleSDWISViewDO sdwis;
        SampleItemViewDO item;
        AnalysisViewDO ana;
        AnalysisQaEventViewDO aqe;
        AnalysisUserViewDO anaUser;
        HashMap<Integer, PWSDO> pwsMap;
        HashMap<Integer, ArrayList<ResultViewDO>> groupResMap;
        HashMap<String ,Integer> colIndexAnaMap;
        ArrayList<SampleProjectViewDO> projList;
        ArrayList<AnalysisQaEventViewDO> aqeList;
        ArrayList<AnalysisUserViewDO> anaUserList;    
        ArrayList<ResultViewDO> rowGrpResList;  
        
        excludeOverride = "Y".equals(data.getExcludeResultOverride());
        excludeResults = "Y".equals(data.getExcludeResults());
        excludeAuxData = "Y".equals(data.getExcludeAuxData());        
        
        wb = new HSSFWorkbook();
        sheet = wb.createSheet();       
        
        headerRow  = sheet.createRow(0);
        headerStyle = createStyle(wb);
        //
        // add cells for the header and set their style
        //
        for (i = 0; i < allCols.size(); i++) {
            cell = headerRow.createCell(i);
            cell.setCellValue(allCols.get(i));   
            cell.setCellStyle(headerStyle);
        }
                
        rowIndex = 1;
        resIndex = 0;
        auxIndex = 0;
        noResAuxIndex = 0;
        sampleId = itemId = analysisId = null;
        domain =  null;
        qaeNames = null; 
        compByNames = null;
        relByNames = null;
        resultVal = null;
        auxDataVal = null;
        prevSamId = null;
        prevItemId = null;
        prevAnalysisId = null; 
        collDateTime = null;
        sam = null;
        proj = null;
        org = null;
        well = null;
        env = null;
        sdwis = null;
        item = null;
        pwsMap = null;
        addResultRow = false;
        addAuxDataRow = false;
        addNoResAuxRow = false;
        res = null;
        aux = null;
        noResAux = null;
        ana = null;
        aqeList = null;
        anaUserList = null;
        sampleOverriden = anaOverriden = false;
        groupResMap = null;
        rowGroup = prevRowGroup = null;
        rowGrpResList = null;
        colIndexAnaMap = new HashMap<String ,Integer>();
        lastColumn = 0;        
        numResults = resultList == null ? 0: resultList.size();
        numAuxVals = auxDataList == null ? 0: auxDataList.size();
        numNoResAuxVals = noResAuxList == null ? 0: noResAuxList.size();
        currRow = prevRow = null;
        
         /*
          * the list of results and that of aux data are iterated through until
          * there are no more elements left in each of them to read from 
          */
        while (resIndex < numResults || auxIndex < numAuxVals || noResAuxIndex < numNoResAuxVals) {
            if (excludeResults && excludeAuxData) {
                if (noResAuxIndex < numNoResAuxVals) {
                    noResAux = noResAuxList.get(noResAuxIndex++);
                    sampleId = noResAux.getSampleId();
                    domain = noResAux.getSampleDomain();
                    itemId = noResAux.getSampleItemId();
                    analysisId = noResAux.getAnalysisId();
                    addNoResAuxRow = true;
                }
            } else {
                if (resIndex < numResults && auxIndex < numAuxVals) {
                    res = resultList.get(resIndex);
                    aux = auxDataList.get(auxIndex);
                    resAccNum = res.getSampleAccessionNumber();
                    auxAccNum = aux.getSampleAccessionNumber();
                    resSamId = res.getSampleId();
                    auxSamId = aux.getSampleId();

                    /*
                     * If this result's accession number is less than or equal to
                     * this aux data's then add a row for this result, otherwise
                     * add a row for the aux data. This makes sure that the results
                     * for a sample are shown before the aux data. Every time a 
                     * row for a result is added the index keeping track of the
                     * next item in that list is incremented and the same is done
                     * for the corresponding index for aux data if a row for it 
                     * is added. We compare accession numbers instead of sample 
                     * ids because the former is the field shown in the sheet and not the latter.
                     */
                    if (resAccNum <= auxAccNum) {
                        addResultRow = true;
                        addAuxDataRow = false;
                        resIndex++;
                        sampleId = resSamId;
                        domain = res.getSampleDomain();
                        itemId = res.getSampleItemId();
                        analysisId = res.getAnalysisId();
                    } else {
                        addAuxDataRow = true;
                        addResultRow = false;
                        auxIndex++ ;
                        sampleId = auxSamId;
                        domain = aux.getSampleDomain();
                    }
                } else if (resIndex < numResults) {
                    addResultRow = true;
                    addAuxDataRow = false;
                    //
                    // no more aux data left to add to the sheet
                    //
                    res = resultList.get(resIndex);

                    resIndex++ ;
                    sampleId = res.getSampleId();
                    domain = res.getSampleDomain();
                    itemId = res.getSampleItemId();
                    analysisId = res.getAnalysisId();
                } else if (auxIndex < numAuxVals) {
                    addAuxDataRow = true;
                    addResultRow = false;
                    //
                    // no more results left to add to the sheet
                    //
                    aux = auxDataList.get(auxIndex);
                    auxIndex++ ;
                    sampleId = aux.getSampleId();
                    domain = aux.getSampleDomain();
                }
            }     
            
            /*
             * skip showing any data for this sample if ths user asked to exclude
             * samples/analyses with results overriden and this sample has such 
             * a qa event  
             */
            if ( !sampleId.equals(prevSamId)) {
                try {
                    sampleQaEvent.fetchResultOverrideBySampleId(sampleId);
                    sampleOverriden = true;
                    if (excludeOverride) {
                        prevSamId = sampleId;
                        continue;
                    }
                } catch (NotFoundException e) {
                    sampleOverriden = false;
                }

                sam = null;
                proj = null;
                org = null;
                env = null;
                well = null;
                sdwis = null;
                collDateTime = null;
            } else if (sampleOverriden && excludeOverride) {
                continue;
            }
            
            if (addResultRow) {
                /*
                 * skip showing any data for this analysis if ths user asked to
                 * exclude samples/analyses with results overriden and this
                 * analysis has such a qa event
                 */
                if (!analysisId.equals(prevAnalysisId)) {
                    anaOverriden = false;
                    aqeList = null;
                    try {
                        aqeList = analysisQaEvent.fetchByAnalysisId(analysisId);
                        for (i = 0; i < aqeList.size(); i++ ) {
                            aqe = aqeList.get(i);
                            if (qaResultOverrideTypeId.equals(aqe.getTypeId())) {
                                anaOverriden = true;
                                if (excludeOverride) {
                                    addResultRow = false;
                                    prevAnalysisId = analysisId;
                                }
                                break;
                            }
                        }
                    } catch (NotFoundException e) {
                        anaOverriden = false;
                    }
                } else if (anaOverriden && excludeOverride) {
                    addResultRow = false;
                }
            }
            
            resRow = null;
            if (addResultRow) {
                /*
                 * check to see if the value of this result was selected by
                 * the user to be shown in the sheet and if it was add a row 
                 * for it to the sheet otherwise don't
                 */
                resultVal = getResultValue(analyteResultMap, res);
                if (resultVal != null)
                    currRow = resRow = sheet.createRow(rowIndex++);
                else
                    addResultRow = false;                
            }
            
            auxRow = null;
            if (addAuxDataRow) {
                /*
                 * check to see if the value of this aux data was selected by the
                 * user to be shown in the sheet and if it was add a row for it
                 * to the sheet otherwise don't
                 */
                auxDataVal = getAuxDataValue(auxFieldValueMap, aux);
                if (auxDataVal != null) 
                    currRow = auxRow = sheet.createRow(rowIndex++);
                else 
                    addAuxDataRow = false;                               
            }
            
            noResAuxRow = null;
            if (addNoResAuxRow) 
                currRow = noResAuxRow = sheet.createRow(rowIndex++);                                   
            
            if (addNoResAuxRow && !analysisId.equals(prevAnalysisId))
                aqeList = null;
            
            if (!addResultRow && !addAuxDataRow && !addNoResAuxRow)
                continue;
            
            /*
             * The following code adds the cells to be shown under the headers
             * added previously to the sheet based on the fields selected by the
             * user. Cells are added even if there's no data to be shown for given
             * fields e.g. "Project Name" because all rows need to contain the same
             * number of cells. Also depending upon whether a row was added for 
             * a result and/or an aux data, we set the values of some cells to empty
             * in a row because some fields don't make sense for that row, e.g. 
             * the fields from sample item and analysis for aux data. 
             */            
            if (addSampleCells) {
                //
                // add cells for the selected fields belonging to samples   
                //
                if (sam == null)
                    sam = sample.fetchById(sampleId);
                if ("Y".equals(data.getProjectName()) && proj == null) {
                    try {
                        /*
                         * we fetch the sample project here and not in the
                         * method that adds the cells for the sample because the
                         * data for the project needs to be fetched only once
                         * for a sample and that method is called for each
                         * analyte under a sample
                         */
                        projList = sampleProject.fetchPermanentBySampleId(sampleId);
                        proj = projList.get(0);
                    } catch (NotFoundException e) {
                        // ignore
                    }
                }
                /*
                 * since collection date and time are two separate fields in the
                 * database, we have to put them together using an instance of 
                 * Datetime, thus we do it only once per sample to avoid creating
                 * unnecessary objects for each row for that sample  
                 */
                if ("Y".equals(data.getCollectionDate()) && collDateTime == null) {
                    collDate = sam.getCollectionDate();
                    collTime = sam.getCollectionTime();
                    if (collDate != null) {
                        dc = collDate.getDate();
                        if (collTime == null) {
                            dc.setHours(0);
                            dc.setMinutes(0);
                        } else {
                            dc.setHours(collTime.getDate().getHours());
                            dc.setMinutes(collTime.getDate().getMinutes());
                        }

                        collDateTime = Datetime.getInstance(Datetime.YEAR, Datetime.MINUTE, dc);
                    }
                }
                if (addResultRow)
                    addSampleCells(resRow, resRow.getPhysicalNumberOfCells(), data, sam, collDateTime, proj);
                if (addAuxDataRow)
                    addSampleCells(auxRow, auxRow.getPhysicalNumberOfCells(), data, sam, collDateTime, proj);
                if (addNoResAuxRow)
                    addSampleCells(noResAuxRow, noResAuxRow.getPhysicalNumberOfCells(), data, sam, collDateTime, proj);                
            }
                        
            if (addOrgCells) {
                /*
                 * add cells for the selected fields belonging to sample organization
                 * or the organization directly linked to a private well sample
                 */
                if ("W".equals(domain)) {
                    if (well == null) 
                        well = samplePrivateWell.fetchBySampleId(sampleId);   
                    if (addResultRow) 
                        addPrivateWellOrganizationCells(resRow, resRow.getPhysicalNumberOfCells(), data, well);
                    if (addAuxDataRow)
                        addPrivateWellOrganizationCells(auxRow, auxRow.getPhysicalNumberOfCells(), data, well);
                    if (addNoResAuxRow)
                        addPrivateWellOrganizationCells(noResAuxRow, noResAuxRow.getPhysicalNumberOfCells(), data, well);
                } else {
                    if (org == null) {
                        try {
                            org = sampleOrganization.fetchReportToBySampleId(sampleId);
                        } catch (NotFoundException e) {
                            // ignore
                        }
                    }
                    if (addResultRow) 
                        addOrganizationCells(resRow, resRow.getPhysicalNumberOfCells(), data, org);
                    if (addAuxDataRow)
                        addOrganizationCells(auxRow, auxRow.getPhysicalNumberOfCells(), data, org);
                    if (addNoResAuxRow)
                        addOrganizationCells(noResAuxRow, noResAuxRow.getPhysicalNumberOfCells(), data, org);
                }
            }
            
            if (addItemCells) {
                //
                // add cells for the selected fields belonging to sample item
                //
                if (addResultRow || addNoResAuxRow) {
                    if (!itemId.equals(prevItemId)) {
                        item = sampleItem.fetchById(itemId);
                        prevItemId = itemId;
                    }
                    if (addResultRow)
                        addSampleItemCells(resRow, resRow.getPhysicalNumberOfCells(), data, item);
                    if (addNoResAuxRow)
                        addSampleItemCells(noResAuxRow, noResAuxRow.getPhysicalNumberOfCells(), data, item);
                    
                } 
                if (addAuxDataRow)
                    addSampleItemCells(auxRow, auxRow.getPhysicalNumberOfCells(), data, null);      
            }
            
            if (addAnalysisCells) {
                /*
                 * add cells for the selected fields belonging to sample organization
                 * or the organization directly linked to a private well sample
                 */
                if (addResultRow || addNoResAuxRow) {                    
                    if (!analysisId.equals(prevAnalysisId)) {
                        groupResMap = new HashMap<Integer, ArrayList<ResultViewDO>>();
                        ana = analysis.fetchById(analysisId);
                        anaUserList = null;
                        qaeNames = null;
                        compByNames = null;
                        relByNames = null;
                        
                        if ("Y".equals(data.getAnalysisQaName())) {
                                /*
                                 * if this analysis has any qa events linked to it,
                                 * fetch them and create a string by concatinating
                                 * their names together  
                                 */
                            if (aqeList == null) {
                                try {
                                    aqeList = analysisQaEvent.fetchByAnalysisId(analysisId);
                                } catch (NotFoundException ignE) {
                                    qaeNames = null;
                                }
                            } 
                            
                            if (aqeList != null) {
                                buf = new StringBuffer();
                                for (i = 0; i < aqeList.size(); i++ ) {
                                    aqe = aqeList.get(i);
                                    /*
                                     * if the file is being generated for an
                                     * external client then we show the
                                     * reporting text and not name of the qa
                                     * event and we show it only if the qa event
                                     * is not internal
                                     */
                                    if (runForWeb) {
                                        if ( !qaInternalTypeId.equals(aqe.getTypeId())) {
                                            if (buf.length() > 0)
                                                buf.append(" ");
                                            buf.append(aqe.getQaEventReportingText());   
                                        }
                                    } else {
                                        if (buf.length() > 0)
                                            buf.append(", ");
                                        buf.append(aqe.getQaEventName());
                                    }                                    
                                }
                                qaeNames = buf.toString();
                            }                                                          
                        }                    
                        if ("Y".equals(data.getAnalysisCompletedBy()) && anaUserList == null) {
                            try {
                                anaUserList = analysisUser.fetchByAnalysisId(analysisId);  
                                buf = new StringBuffer();                            
                                for (i = 0; i < anaUserList.size(); i++) {
                                    anaUser = anaUserList.get(i);
                                    if (!completedActionId.equals(anaUser.getActionId()))
                                        continue;
                                    if (buf.length() > 0)
                                        buf.append(", ");
                                    userName = anaUser.getSystemUser();
                                    /*
                                     * the user's login name could be null in this DO
                                     * if there was a problem with fetching the data
                                     * from security
                                     */                                    
                                    if (userName != null)
                                        buf.append(userName);
                                }
                                compByNames = buf.toString();
                            } catch (NotFoundException ignE) {
                                // ignore
                            }
                        }                    
                        if ("Y".equals(data.getAnalysisReleasedBy())) {
                            if (anaUserList == null) {
                                try {
                                    anaUserList = analysisUser.fetchByAnalysisId(analysisId);                                    
                                } catch (NotFoundException ignE) {
                                    // ignore
                                }
                            }
                            if (anaUserList != null && relByNames == null) {
                                for (i = 0; i < anaUserList.size(); i++ ) {
                                    anaUser = anaUserList.get(i);
                                    if ( releasedActionId.equals(anaUser.getActionId())) {
                                        relByNames = anaUser.getSystemUser();
                                        break;
                                    }
                                }
                            }
                        }                        
                    }
                    if (addResultRow)
                        addAnalysisCells(resRow, resRow.getPhysicalNumberOfCells(), data, runForWeb, ana, qaeNames, compByNames, relByNames);
                    if (addNoResAuxRow)
                        addAnalysisCells(noResAuxRow, noResAuxRow.getPhysicalNumberOfCells(), data, runForWeb, ana, qaeNames, compByNames, relByNames);
                } 
                if (addAuxDataRow) 
                    addAnalysisCells(auxRow, auxRow.getPhysicalNumberOfCells(), data, runForWeb, null, null, null, null);                
            }
            
            /*
             * we need to make sure that a given sample is of a given domain
             * before fetching the data for that domain, but we need to add 
             * cells (filled or not) for the fields from that domain in the file
             * for a given row regardless, if the user selected them to be shown 
             */
            if (addEnvCells) {
                if ("E".equals(domain) && env == null) 
                        env = sampleEnvironmental.fetchBySampleId(sampleId);        
                if (addResultRow) 
                    addEnvironmentalCells(resRow, resRow.getPhysicalNumberOfCells(), data, env);
                if (addAuxDataRow) 
                    addEnvironmentalCells(auxRow, auxRow.getPhysicalNumberOfCells(), data, env);
                if (addNoResAuxRow)
                    addEnvironmentalCells(noResAuxRow, noResAuxRow.getPhysicalNumberOfCells(), data, env);
            } 
            
            if (addWellCells) {
                if ("W".equals(domain) && well == null) 
                        well = samplePrivateWell.fetchBySampleId(sampleId);  
                if (addResultRow) 
                    addPrivateWellCells(resRow, resRow.getPhysicalNumberOfCells(), data, well);
                if (addAuxDataRow) 
                    addPrivateWellCells(auxRow, auxRow.getPhysicalNumberOfCells(), data, well);
                if (addNoResAuxRow)
                    addPrivateWellCells(noResAuxRow, noResAuxRow.getPhysicalNumberOfCells(), data, well);
            } 
            
            if (addSDWISCells) {
                if ("S".equals(domain) && sdwis == null) { 
                    sdwis = sampleSDWIS.fetchBySampleId(sampleId);                
                    if ("Y".equals(data.getSampleSDWISPwsId()) && pwsMap == null) 
                        pwsMap = new HashMap<Integer, PWSDO>();                                            
                }
                if (addResultRow) 
                    addSDWISCells(resRow, resRow.getPhysicalNumberOfCells(), data, sdwis, pwsMap);
                if (addAuxDataRow) 
                    addSDWISCells(auxRow, auxRow.getPhysicalNumberOfCells(), data, sdwis, pwsMap);
                if (addNoResAuxRow)
                    addSDWISCells(noResAuxRow, noResAuxRow.getPhysicalNumberOfCells(), data, sdwis, pwsMap);
            }

            if (addResultRow) { 
                //
                // set the analyte's name and the result's value 
                //
                cell = resRow.createCell(resRow.getPhysicalNumberOfCells());
                cell.setCellValue(res.getAnalyteName());                
                cell = resRow.createCell(resRow.getPhysicalNumberOfCells());
                
                /*
                 * results for an analysis are not shown if it or the sample
                 * that it belongs to has a qa event of type "result override" 
                 */
                if (!anaOverriden && !sampleOverriden)
                    cell.setCellValue(resultVal);
                
                sortOrder = (Integer)res.getResultSortOrder();
                rowGroup = (Integer)res.getResultTestAnalyteRowGroup();      
                if (!analysisId.equals(prevAnalysisId)) {
                    groupResMap = new HashMap<Integer, ArrayList<ResultViewDO>>();
                    rowGrpResList = null;
                } else if ( !rowGroup.equals(prevRowGroup)) { 
                    rowGrpResList = groupResMap.get(rowGroup);
                }
                
                //
                // fetch the column analytes if there are any
                //
                if (rowGrpResList == null) {
                    try {
                        /*
                         * this is the list of all the results belonging to the
                         * same row group as the test analyte of this result and
                         * for which is_column = "Y"
                         */
                        rowGrpResList = result.fetchForDataViewByAnalysisIdAndRowGroup(analysisId, rowGroup);
                        groupResMap.put(rowGroup, rowGrpResList);
                    } catch (NotFoundException e) {
                        // ignore
                    }
                }                
                                
                /*
                 * if there are column analytes with values then the names of
                 * the analytes are added to the header such that if an analyte B
                 * is found first for any reason then it's added to the header before
                 * another analyte A even if A's column appears to the left of B's
                 * in this test or any other 
                 */
                if (rowGrpResList != null) {
                    if (lastColumn == 0)
                        lastColumn = resRow.getPhysicalNumberOfCells();
                    currColumn = resRow.getPhysicalNumberOfCells();
                    prevSortOrder = sortOrder;
                    for (ResultViewDO rvdo : rowGrpResList) {
                        currSortOrder = rvdo.getSortOrder();                        
                        if (showReportableColumnsOnly && "N".equals(rvdo.getIsReportable())) {
                            prevSortOrder = currSortOrder;
                            continue;
                        }
                            
                        /*
                         * we only show those analytes' values in this row in the
                         * sheet that belong to the row in the test starting with
                         * the analyte selected by the user and none before it    
                         */
                        if (currSortOrder < sortOrder) {
                            prevSortOrder = currSortOrder;
                            continue;
                        } 
                        
                        /* 
                         * The first check is done to know when the row starting
                         * with the selected analyte has ended (the sort order of
                         * the next analyte is 2 more than the previous one's, i.e.
                         * the next one is a column analyte in the next row). The
                         * second check is done to know when the row starting with
                         * the selected analyte begins, i.e. the first column analyte's
                         * sort order is one more than that of the selected analyte.         
                         */
                        if (currSortOrder > prevSortOrder + 1 && currSortOrder > sortOrder + 1)
                            break;
                        
                        anaName = rvdo.getAnalyte();
                        anaIndex = colIndexAnaMap.get(anaName); 

                        if (anaIndex == null) {    
                            /*
                             * If an analyte's name is not found in the map then we
                             * create a new column in the header row and set its value
                             * as the name. We also start adding values under that column 
                             */
                            anaIndex = lastColumn++;
                            colIndexAnaMap.put(anaName, anaIndex);
                            cell = headerRow.createCell(anaIndex);
                            cell.setCellValue(anaName);    
                            cell.setCellStyle(headerStyle);
                            
                            resultVal = getValue(rvdo.getValue(), resultDictId, rvdo.getTypeId());
                            cell = resRow.createCell(anaIndex);
                        } else if (anaIndex == currColumn) {   
                            /*
                             * we set the value in this cell if this result's analyte
                             * is shown in this column
                             */
                            resultVal = getValue(rvdo.getValue(), resultDictId, rvdo.getTypeId());                            
                            cell = resRow.createCell(currColumn++);
                        } else {
                            /*
                             * if this result's analyte is not shown in this column
                             * then we set the value in the appropriate column 
                             */
                            resultVal = getValue(rvdo.getValue(), resultDictId, rvdo.getTypeId());
                            cell = resRow.createCell(anaIndex);
                        }
                        
                        /*
                         * results for an analysis are not shown if it or the sample
                         * that it belongs to has a qa event of type "result override" 
                         */
                        if (!anaOverriden && !sampleOverriden)
                            cell.setCellValue(resultVal);
                        
                        prevSortOrder = currSortOrder;
                    }                   
                }
            }
            
            if (addAuxDataRow) { 
                //
                // set the analyte's name and the aux data's value                
                //
                cell = auxRow.createCell(auxRow.getPhysicalNumberOfCells());
                cell.setCellValue(aux.getAnalyteName());
                cell = auxRow.createCell(auxRow.getPhysicalNumberOfCells());
                cell.setCellValue(auxDataVal);
            }                        
                
            prevAnalysisId = analysisId;
            prevSamId = sampleId;
            prevRowGroup = rowGroup;
            
            /*
             * An empty row can't be created and then added it to the sheet, it has
             * to be obtained from the sheet. Thus it has to be removed if we don't
             * want to show it. We do so if two consecutive rows have the same data
             * in all cells. It can happen if, for example, a user chose to see 
             * sample items but all the ones under a sample have the same container
             * and sample type and those were the only fields chosen to be shown.  
             */
            if (isSameDataInRows(currRow, prevRow)) {
                sheet.removeRow(currRow);
                rowIndex--;
            } else {
                prevRow = currRow;
            }
        }
        
        //
        // make each column wide enough to show the longest string in it  
        //
        for (i = 0; i < headerRow.getPhysicalNumberOfCells(); i++) 
            sheet.autoSizeColumn(i);        
        
        return wb;
    }  
        
    private ArrayList<TestAnalyteDataViewVO> getTestAnalytes(ArrayList<ResultViewDO> resList) throws Exception {
        Integer taId, dictId;
        String value;
        TestAnalyteDataViewVO anadd;
        ResultDataViewVO resdd;
        ArrayList<TestAnalyteDataViewVO> anaddList;
        ArrayList<ResultDataViewVO> resddList;
        HashMap<Integer, TestAnalyteDataViewVO> anaMap;     
        HashMap<String, String> resMap; 
        DictionaryCacheLocal dcl;
        
        if (resList == null)
            return null;
        
        taId = null;
        resddList = null;
        anaddList = new ArrayList<TestAnalyteDataViewVO>();
        anaMap = new HashMap<Integer, TestAnalyteDataViewVO>();
        resMap = null;
        dcl = EJBFactory.getDictionaryCache();
        /*
         * a TestAnalyteDataViewVO is created for an analyte only once, no matter
         * how many times it appears in the list of ResultViewDOs
         */
        for (ResultViewDO res : resList) {
            taId = res.getAnalyteId();
            anadd = anaMap.get(taId);
            if (anadd == null) {                
                anadd = new TestAnalyteDataViewVO();
                anadd.setAnalyteId(res.getAnalyteId());
                anadd.setAnalyteName(res.getAnalyte());
                anadd.setTestAnalyteId(res.getTestAnalyteId());
                anadd.setIsIncluded("N");
                resddList = new ArrayList<ResultDataViewVO>();
                anadd.setResults(resddList);
                anaddList.add(anadd);
                anaMap.put(taId,anadd);
                resMap = new HashMap<String, String>();
            } else {
                resddList = anadd.getResults();
            }

            value = res.getValue();
            if (resultDictId.equals(res.getTypeId())) {
                dictId = Integer.parseInt(value);
                value = dcl.getById(dictId).getEntry();
            }
            
            /*
             * we don't allow the same value to be shown more than once for the 
             * same analyte
             */
            if (resMap.get(value)!= null)
                continue;
            resMap.put(value, value);
            
            resdd = new ResultDataViewVO();
            resdd.setValue(value);
            resdd.setIsIncluded("N");
            resddList.add(resdd);
        }
        return anaddList;
    }
    
    private ArrayList<AuxFieldDataViewVO> getAuxFields(ArrayList<AuxDataViewDO> valList) throws Exception {
        Integer taId, dictId;
        String value;
        AuxFieldDataViewVO anadd;
        AuxDataDataViewVO resdd;
        ArrayList<AuxFieldDataViewVO> anaddList;
        ArrayList<AuxDataDataViewVO> resddList;
        HashMap<Integer, AuxFieldDataViewVO> anaMap;  
        HashMap<String, String> resMap; 
        DictionaryCacheLocal dcl;
        
        if (valList == null)
            return null;
        
        taId = null;
        resddList = null;
        anaddList = new ArrayList<AuxFieldDataViewVO>();
        anaMap = new HashMap<Integer, AuxFieldDataViewVO>();
        resMap = null;
        dcl = EJBFactory.getDictionaryCache();
        /*
         * an AuxFieldDataViewVO is created for an analyte only once, no matter
         * how many times it appears in the list of AuxDataViewDOs
         */
        for (AuxDataViewDO res : valList) {
            taId = res.getAnalyteId();
            anadd = anaMap.get(taId);
            if (anadd == null) {       
                anadd = new AuxFieldDataViewVO();
                anadd.setAnalyteId(res.getAnalyteId());
                anadd.setAnalyteName(res.getAnalyteName());
                anadd.setAuxFieldId(res.getAuxFieldId());    
                anadd.setIsIncluded("N");
                resddList = new ArrayList<AuxDataDataViewVO>();
                anadd.setValues(resddList);
                anaddList.add(anadd);
                anaMap.put(taId,anadd);
                resMap = new HashMap<String, String>();
            } else {
                resddList = anadd.getValues();
            }

            value = res.getValue();    
            
            if (auxFieldValueDictId.equals(res.getTypeId())) {
                dictId = Integer.parseInt(value);
                value = dcl.getById(dictId).getEntry();
            }
            
            /*
             * we don't allow the same value to be shown more than once for the 
             * same analyte
             */
            if (resMap.get(value)!= null)
                continue;
            
            resdd = new AuxDataDataViewVO();
            resdd.setValue(value);
            resdd.setIsIncluded("N");
            resddList.add(resdd);
        }
        return anaddList;
    }
    
    private String getListParam(Set<Integer> set) {
        StringBuffer buf;
        Object arr[];
        
        buf = new StringBuffer();
        arr = set.toArray();
        buf.append(" in (");
        for (int i = 0;  i < arr.length; i++) {
            buf.append(arr[i]);
            if (i < arr.length-1)
                buf.append(",");
        }
        buf.append(") ");
        
        return buf.toString();
    }
    
    private ArrayList<String> getSampleHeaders(DataViewVO data) {
        ArrayList<String> headers;
        
        headers = new ArrayList<String>();
        if ("Y".equals(data.getAccessionNumber()))
            headers.add(resource.getString("accessionNum"));
        if ("Y".equals(data.getRevision()))
            headers.add(resource.getString("sampleRevision"));
        if ("Y".equals(data.getCollectionDate()))
            headers.add(resource.getString("collectionDate"));
        if ("Y".equals(data.getReceivedDate()))
            headers.add(resource.getString("receivedDate"));
        if ("Y".equals(data.getEnteredDate()))
            headers.add(resource.getString("enteredDate"));
        if ("Y".equals(data.getReleasedDate()))
            headers.add(resource.getString("releasedDate"));
        if ("Y".equals(data.getStatusId()))
            headers.add(resource.getString("sampleStatus"));
        if ("Y".equals(data.getProjectName())) 
            headers.add(resource.getString("project"));        
        if ("Y".equals(data.getClientReferenceHeader()))
            headers.add(resource.getString("clntRef"));
        
        return headers;            
    }
    
    private ArrayList<String> getOrganizationHeaders(DataViewVO data) {
        ArrayList<String> headers;
        
        headers = new ArrayList<String>();
        if ("Y".equals(data.getOrganizationId()))
            headers.add(resource.getString("organizationNum"));
        if ("Y".equals(data.getOrganizationName()))
            headers.add(resource.getString("organizationName"));
        if ("Y".equals(data.getOrganizationAttention()))
            headers.add(resource.getString("attention"));
        if ("Y".equals(data.getOrganizationAddressMultipleUnit()))
            headers.add(resource.getString("aptSuite"));
        if ("Y".equals(data.getOrganizationAddressAddress()))
            headers.add(resource.getString("address"));
        if ("Y".equals(data.getOrganizationAddressCity()))
            headers.add(resource.getString("city"));
        if ("Y".equals(data.getOrganizationAddressState()))
            headers.add(resource.getString("state"));
        if ("Y".equals(data.getOrganizationAddressZipCode())) 
            headers.add(resource.getString("zipcode"));        
        
        return headers;            
    }
        
    private ArrayList<String> getSampleItemHeaders(DataViewVO data) {
        ArrayList<String> headers;
        
        headers = new ArrayList<String>();
        if ("Y".equals(data.getSampleItemTypeofSampleId()))
            headers.add(resource.getString("sampleType"));
        if ("Y".equals(data.getSampleItemSourceOfSampleId()))
            headers.add(resource.getString("source"));
        if ("Y".equals(data.getSampleItemSourceOther()))
            headers.add(resource.getString("sourceOther"));
        if ("Y".equals(data.getSampleItemContainerId()))
            headers.add(resource.getString("container"));
        if ("Y".equals(data.getSampleItemContainerReference()))
            headers.add(resource.getString("containerReference"));
        if ("Y".equals(data.getSampleItemItemSequence()))
            headers.add(resource.getString("sequence"));
        
        return headers;            
    }
    
    private ArrayList<String> getAnalysisHeaders(DataViewVO data) {
        ArrayList<String> headers;
        
        headers = new ArrayList<String>();
        if ("Y".equals(data.getAnalysisId()))
            headers.add(resource.getString("analysisId"));
        if ("Y".equals(data.getAnalysisTestNameHeader()))
            headers.add(resource.getString("test"));
        if ("Y".equals(data.getAnalysisTestMethodNameHeader()))
            headers.add(resource.getString("method"));
        if ("Y".equals(data.getAnalysisStatusIdHeader()))
            headers.add(resource.getString("analysisStatus"));
        if ("Y".equals(data.getAnalysisRevision()))
            headers.add(resource.getString("analysisRevision"));
        if ("Y".equals(data.getAnalysisIsReportableHeader()))
            headers.add(resource.getString("reportable"));
        if ("Y".equals(data.getAnalysisUnitOfMeasureId()))
            headers.add(resource.getString("unit"));
        if ("Y".equals(data.getAnalysisQaName()))
            headers.add(resource.getString("QAEvent"));
        if ("Y".equals(data.getAnalysisCompletedDate()))
            headers.add(resource.getString("completedDate"));
        if ("Y".equals(data.getAnalysisCompletedBy()))
            headers.add(resource.getString("completedBy"));
        if ("Y".equals(data.getAnalysisReleasedDate()))
            headers.add(resource.getString("releasedDate"));
        if ("Y".equals(data.getAnalysisReleasedBy()))
            headers.add(resource.getString("releasedBy"));
        if ("Y".equals(data.getAnalysisStartedDate()))
            headers.add(resource.getString("startedDate"));
        if ("Y".equals(data.getAnalysisPrintedDate()))
            headers.add(resource.getString("printedDate"));
        if ("Y".equals(data.getAnalysisSectionName()))
            headers.add(resource.getString("section"));
        
        return headers;            
    }
    
    private ArrayList<String> getEnvironmentalHeaders(DataViewVO data) {
        ArrayList<String> headers;
        
        headers = new ArrayList<String>();
        if ("Y".equals(data.getSampleEnvironmentalIsHazardous()))
            headers.add(resource.getString("hazardous"));
        if ("Y".equals(data.getSampleEnvironmentalPriority()))
            headers.add(resource.getString("priority"));
        if ("Y".equals(data.getSampleEnvironmentalCollectorHeader()))
            headers.add(resource.getString("collector"));
        if ("Y".equals(data.getSampleEnvironmentalCollectorPhone()))
            headers.add(resource.getString("phone"));
        if ("Y".equals(data.getSampleEnvironmentalLocationHeader()))
            headers.add(resource.getString("location"));        
        if ("Y".equals(data.getSampleEnvironmentalLocationAddressCityHeader()))
            headers.add(resource.getString("locationCity"));
        if ("Y".equals(data.getSampleEnvironmentalDescription()))
            headers.add(resource.getString("description"));
        
        return headers;            
    }
    
    private ArrayList<String> getPrivateWellHeaders(DataViewVO data) {
        ArrayList<String> headers;
        
        headers = new ArrayList<String>();
        if ("Y".equals(data.getSamplePrivateWellOwner()))
            headers.add(resource.getString("owner"));
        if ("Y".equals(data.getSamplePrivateWellCollector()))
            headers.add(resource.getString("collector"));
        if ("Y".equals(data.getSamplePrivateWellWellNumber()))
            headers.add(resource.getString("wellNum"));
        if ("Y".equals(data.getSamplePrivateWellReportToAddressWorkPhone()))
            headers.add(resource.getString("phone"));
        if ("Y".equals(data.getSamplePrivateWellReportToAddressFaxPhone()))
            headers.add(resource.getString("faxNumber"));
        if ("Y".equals(data.getSamplePrivateWellLocation()))
            headers.add(resource.getString("location"));
        if ("Y".equals(data.getSamplePrivateWellLocationAddressMultipleUnit()))
            headers.add(resource.getString("locationAptSuite"));
        if ("Y".equals(data.getSamplePrivateWellLocationAddressStreetAddress()))
            headers.add(resource.getString("locationAddress"));
        if ("Y".equals(data.getSamplePrivateWellLocationAddressCity()))
            headers.add(resource.getString("locationCity"));
        if ("Y".equals(data.getSamplePrivateWellLocationAddressState()))
            headers.add(resource.getString("locationState"));
        if ("Y".equals(data.getSamplePrivateWellLocationAddressZipCode()))
            headers.add(resource.getString("locationZipCode"));
        
        return headers;            
    }
    
    private ArrayList<String> getSDWISHeaders(DataViewVO data) {
        ArrayList<String> headers;
        
        headers = new ArrayList<String>();
        if ("Y".equals(data.getSampleSDWISPwsId()))
            headers.add(resource.getString("pwsId"));
        if ("Y".equals(data.getSampleSDWISPwsName()))
            headers.add(resource.getString("pwsName"));
        if ("Y".equals(data.getSampleSDWISStateLabId()))
            headers.add(resource.getString("stateLabNo"));
        if ("Y".equals(data.getSampleSDWISFacilityId()))
            headers.add(resource.getString("facilityId"));
        if ("Y".equals(data.getSampleSDWISSampleTypeId()))
            headers.add(resource.getString("sampleType"));
        if ("Y".equals(data.getSampleSDWISSampleCategoryId()))
            headers.add(resource.getString("sampleCat"));
        if ("Y".equals(data.getSampleSDWISSamplePointId()))
            headers.add(resource.getString("samplePtId"));
        if ("Y".equals(data.getSampleSDWISLocation()))
            headers.add(resource.getString("location"));
        if ("Y".equals(data.getSampleSDWISCollector()))
            headers.add(resource.getString("collector"));
        
        return headers;            
    }
    
    private void addSampleCells(Row row, int startCol, DataViewVO data,
                                SampleDO sample, Datetime colDateTime,
                                SampleProjectViewDO project) {  
        Cell cell;
        Datetime dt;

        if ("Y".equals(data.getAccessionNumber())) {
            cell = row.createCell(startCol++);
            cell.setCellValue(sample.getAccessionNumber());
        }        
        if ("Y".equals(data.getRevision())) {
            cell = row.createCell(startCol++);
            cell.setCellValue(sample.getRevision());
        }        
        if ("Y".equals(data.getCollectionDate())) {
            cell = row.createCell(startCol++);            
            if (colDateTime != null)
                cell.setCellValue(colDateTime.toString());
        }        
        if ("Y".equals(data.getReceivedDate())) {
            cell = row.createCell(startCol++);
            dt = sample.getReceivedDate();
            if (dt != null) 
                cell.setCellValue(dt.toString());
        }        
        if ("Y".equals(data.getEnteredDate())) {
            cell = row.createCell(startCol++);
            dt = sample.getEnteredDate();
            if (dt != null) 
                cell.setCellValue(dt.toString());
        }        
        if ("Y".equals(data.getReleasedDate())) {
            cell = row.createCell(startCol++);
            dt = sample.getReleasedDate();
            if (dt != null) 
                cell.setCellValue(dt.toString());
        }        
        if ("Y".equals(data.getStatusId())) {
            cell = row.createCell(startCol++);
            cell.setCellValue(dictEntryMap.get(sample.getStatusId()));
        }        
        if ("Y".equals(data.getProjectName())) {
            cell = row.createCell(startCol++);
            if (project != null)    
                cell.setCellValue(project.getProjectName());
        }
        if ("Y".equals(data.getClientReferenceHeader())) {
            cell = row.createCell(startCol++);
            cell.setCellValue(sample.getClientReference());
        }        
    }
    
    private void addOrganizationCells(Row row, int startCol, DataViewVO data, 
                                      SampleOrganizationViewDO org) {  
        Cell cell;
                
        if ("Y".equals(data.getOrganizationId())) {
            cell = row.createCell(startCol++);
            if (org != null)
                cell.setCellValue(org.getOrganizationId());
        }        
        if ("Y".equals(data.getOrganizationName())) {
            cell = row.createCell(startCol++);
            if (org != null)
                cell.setCellValue(org.getOrganizationName());
        }     
        if ("Y".equals(data.getOrganizationAttention())) {
            cell = row.createCell(startCol++);
            if (org != null)
                cell.setCellValue(org.getOrganizationAttention());
        }       
        if ("Y".equals(data.getOrganizationAddressMultipleUnit())) {
            cell = row.createCell(startCol++);
            if (org != null)
                cell.setCellValue(org.getOrganizationMultipleUnit());
        }       
        if ("Y".equals(data.getOrganizationAddressAddress())) {
            cell = row.createCell(startCol++);
            if (org != null)
                cell.setCellValue(org.getOrganizationStreetAddress());
        }       
        if ("Y".equals(data.getOrganizationAddressCity())) {
            cell = row.createCell(startCol++);
            if (org != null)
                cell.setCellValue(org.getOrganizationCity());
        }        
        if ("Y".equals(data.getOrganizationAddressState())) {
            cell = row.createCell(startCol++);
            if (org != null)
                cell.setCellValue(org.getOrganizationState());
        }      
        if ("Y".equals(data.getOrganizationAddressZipCode())) {
            cell = row.createCell(startCol++);
            if (org != null)
                cell.setCellValue(org.getOrganizationZipCode());
        }         
    }
    
    private void addPrivateWellOrganizationCells(Row row, int startCol, DataViewVO data, 
                                                 SamplePrivateWellViewDO spw) {  
        Cell cell;
        OrganizationDO org;
        AddressDO addr;
        
        org = spw.getOrganization();
                
        if ("Y".equals(data.getOrganizationId())) {
            cell = row.createCell(startCol++);
            if (org != null)
                cell.setCellValue(org.getId());
        }             
        if ("Y".equals(data.getOrganizationName())) {
            cell = row.createCell(startCol++);
            cell.setCellValue(org != null ? org.getName() : spw.getReportToName());
        }             
        if ("Y".equals(data.getOrganizationAttention())) {
            cell = row.createCell(startCol++);
            cell.setCellValue(spw.getReportToAttention());
        }       
        
        addr = (org != null ? org.getAddress() : spw.getReportToAddress());
        
        if ("Y".equals(data.getOrganizationAddressMultipleUnit())) {
            cell = row.createCell(startCol++);
            if (addr != null)
                cell.setCellValue(addr.getMultipleUnit());
        }        
        if ("Y".equals(data.getOrganizationAddressAddress())) {
            cell = row.createCell(startCol++);
            if (addr != null)
                cell.setCellValue(addr.getStreetAddress());
        }       
        if ("Y".equals(data.getOrganizationAddressCity())) {
            cell = row.createCell(startCol++);
            if (addr != null)
                cell.setCellValue(addr.getCity());
        }        
        if ("Y".equals(data.getOrganizationAddressState())) {
            cell = row.createCell(startCol++);
            if (addr != null)
                cell.setCellValue(addr.getState());
        }              
        if ("Y".equals(data.getOrganizationAddressZipCode())) {
            cell = row.createCell(startCol++);
            if (addr != null)
                cell.setCellValue(addr.getZipCode());
        }         
    }
    
    private void addSampleItemCells(Row row, int startCol, DataViewVO data, SampleItemViewDO item) {
        Integer id;
        Cell cell;
        DictionaryDO dict;
        
        if ("Y".equals(data.getSampleItemTypeofSampleId())) {
            cell = row.createCell(startCol++);
            if (item != null) {
                id = item.getTypeOfSampleId();
                if (id != null) {
                    try {
                        dict = dictionaryCache.getById(id);
                        cell.setCellValue(dict.getEntry());
                    } catch (Exception e) {
                        log.log(Level.SEVERE, "Failed to lookup constants for dictionary entry: " + id, e);
                    }
                }
            }            
        }
        if ("Y".equals(data.getSampleItemSourceOfSampleId())) {
            cell = row.createCell(startCol++);
            if (item != null) {
                id = item.getSourceOfSampleId();
                if (id != null) {
                    try {
                        dict = dictionaryCache.getById(id);
                        cell.setCellValue(dict.getEntry());
                    } catch (Exception e) {
                        log.log(Level.SEVERE, "Failed to lookup constants for dictionary entry: " + id, e);
                    }
                }
            }
        }
        if ("Y".equals(data.getSampleItemSourceOther())) {
            cell = row.createCell(startCol++ );
            if (item != null) {
                cell.setCellValue(item.getSourceOther());
            }
        }
        if ("Y".equals(data.getSampleItemContainerId())) {
            cell = row.createCell(startCol++ );
            if (item != null) {
                id = item.getContainerId();
                if (id != null) {
                    try {
                        dict = dictionaryCache.getById(id);
                        cell.setCellValue(dict.getEntry());
                    } catch (Exception e) {
                        log.log(Level.SEVERE, "Failed to lookup constants for dictionary entry: " + id, e);
                    }
                }
            }
        }        
        if ("Y".equals(data.getSampleItemContainerReference())) {
            cell = row.createCell(startCol++);
            if (item != null) {
                cell.setCellValue(item.getContainerReference());
            }
        }
        if ("Y".equals(data.getSampleItemItemSequence())) {
            cell = row.createCell(startCol++);
            if (item != null) {
                cell.setCellValue(item.getItemSequence());
            }
        }
    }
    
    private void addAnalysisCells(Row row, int startCol, DataViewVO data,
                                  boolean runForWeb, AnalysisViewDO analysis,
                                  String qaeNames, String compByNames, String relByNames) {
        boolean isRep;
        Integer id;
        Cell cell;
        DictionaryDO dict;
        Datetime dt;
        
        if ("Y".equals(data.getAnalysisId())) {
            cell = row.createCell(startCol++);
            if (analysis != null) 
                cell.setCellValue(analysis.getId());            
        }
        
        if ("Y".equals(data.getAnalysisTestNameHeader())) {
            cell = row.createCell(startCol++);
            if (analysis != null) 
                cell.setCellValue(runForWeb ? analysis.getTestReportingDescription() : analysis.getTestName());            
        }
        if ("Y".equals(data.getAnalysisTestMethodNameHeader())) {
            cell = row.createCell(startCol++);
            if (analysis != null)
                cell.setCellValue(runForWeb ? analysis.getMethodReportingDescription() : analysis.getMethodName()); 
        }
        if ("Y".equals(data.getAnalysisStatusIdHeader())) {
            cell = row.createCell(startCol++);
            if (analysis != null)
                cell.setCellValue(dictEntryMap.get(analysis.getStatusId()));
        }
        if ("Y".equals(data.getAnalysisRevision())) {
            cell = row.createCell(startCol++);
            if (analysis != null)
                cell.setCellValue(analysis.getRevision());
        }
        if ("Y".equals(data.getAnalysisUnitOfMeasureId())) {
            cell = row.createCell(startCol++ );
            if (analysis != null) {
                id = analysis.getUnitOfMeasureId();
                if (id != null) {
                    try {
                        dict = dictionaryCache.getById(id);
                        cell.setCellValue(dict.getEntry());
                    } catch (Exception e) {
                        log.log(Level.SEVERE, "Failed to lookup constants for dictionary entry: " + id, e);
                    }
                }
            }
        }
        if ("Y".equals(data.getAnalysisIsReportableHeader())) {
            cell = row.createCell(startCol++);
            if (analysis != null) {
                isRep = "Y".equals(analysis.getIsReportable());
                cell.setCellValue(isRep ? resource.getString("yes") : resource.getString("no"));
            }
        }
        if ("Y".equals(data.getAnalysisQaName())) {
            cell = row.createCell(startCol++); 
            if (qaeNames != null)           
                cell.setCellValue(qaeNames);            
        }
        if ("Y".equals(data.getAnalysisCompletedDate())) {
            cell = row.createCell(startCol++);
            if (analysis != null) {
                dt = analysis.getCompletedDate();
                if (dt != null) 
                    cell.setCellValue(dt.toString());
            }
        }
        if ("Y".equals(data.getAnalysisCompletedBy())) {
            cell = row.createCell(startCol++ );
            if (compByNames != null)
                cell.setCellValue(compByNames);
        }
        if ("Y".equals(data.getAnalysisReleasedDate())) {
            cell = row.createCell(startCol++);
            if (analysis != null) {
                dt = analysis.getReleasedDate();
                if (dt != null) 
                    cell.setCellValue(dt.toString());
            }
        }
        if ("Y".equals(data.getAnalysisReleasedBy())) {
            cell = row.createCell(startCol++ );
            if (relByNames != null)
                cell.setCellValue(relByNames);
        }
        if ("Y".equals(data.getAnalysisStartedDate())) {
            cell = row.createCell(startCol++);
            if (analysis != null) {
                dt = analysis.getStartedDate();
                if (dt != null) 
                    cell.setCellValue(dt.toString());
            }
        }
        if ("Y".equals(data.getAnalysisPrintedDate())) {
            cell = row.createCell(startCol++);
            if (analysis != null) {
                dt = analysis.getPrintedDate();
                if (dt != null) 
                    cell.setCellValue(dt.toString());
            }
        }
        if ("Y".equals(data.getAnalysisSectionName())) {
            cell = row.createCell(startCol++);
            if (analysis != null && analysis.getSectionName() != null) {
                cell.setCellValue(analysis.getSectionName());
            }
        }        
    }
    
    private void addEnvironmentalCells(Row row, int startCol, DataViewVO data, 
                                       SampleEnvironmentalDO env) {
        Integer pr;
        Cell cell;        
        boolean isHaz;
        /*
         * the SampleEnvironmentalDO can be null if a sample is not an environmental 
         * sample but the user has requested to view environmental fields and so
         * we need to add empty cells in the columns for those fields, because
         * otherwise the data in the cells to the right of those columns will be
         * shifted to the left 
         */
        if ("Y".equals(data.getSampleEnvironmentalIsHazardous())) {
            cell = row.createCell(startCol++ );
            if (env != null) {
                isHaz = "Y".equals(env.getIsHazardous());
                cell.setCellValue(isHaz ? resource.getString("yes") : resource.getString("no"));
            }
        }
        if ("Y".equals(data.getSampleEnvironmentalPriority())) {
            cell = row.createCell(startCol++ );
            if (env != null) {
                pr = env.getPriority();
                if (pr != null)
                    cell.setCellValue(pr);
            }
        }
        if ("Y".equals(data.getSampleEnvironmentalCollectorHeader())) {
            cell = row.createCell(startCol++ );
            if (env != null)
                cell.setCellValue(env.getCollector());
        }
        if ("Y".equals(data.getSampleEnvironmentalCollectorPhone())) {
            cell = row.createCell(startCol++ );
            if (env != null)
                cell.setCellValue(env.getCollectorPhone());
        }
        if ("Y".equals(data.getSampleEnvironmentalLocationHeader())) {
            cell = row.createCell(startCol++ );
            if (env != null)
                cell.setCellValue(env.getLocation());
        }
        if ("Y".equals(data.getSampleEnvironmentalLocationAddressCityHeader())) {
            cell = row.createCell(startCol++ );
            if (env != null)
                cell.setCellValue(env.getLocationAddress().getCity());
        }
        if ("Y".equals(data.getSampleEnvironmentalDescription())) {
            cell = row.createCell(startCol++ );
            if (env != null)
                cell.setCellValue(env.getDescription());
        }
    }
    
    private void addPrivateWellCells(Row row, int startCol, DataViewVO data, 
                                       SamplePrivateWellViewDO well) {
        Integer wn;
        Cell cell;    
        AddressDO repTo, loc;
        
        /*
         * the SamplePrivateWellViewDO can be null if a sample is not a private
         * well sample but the user has requested to view private well fields and 
         * so we need to add empty cells in the columns for those fields, because
         * otherwise the data in the cells to the right of those columns will be
         * shifted to the left 
         */        
        if ("Y".equals(data.getSamplePrivateWellOwner())) {
            cell = row.createCell(startCol++ );
            if (well != null)
                cell.setCellValue(well.getOwner());
        }
        if ("Y".equals(data.getSamplePrivateWellCollector())) {
            cell = row.createCell(startCol++ );
            if (well != null)
                cell.setCellValue(well.getCollector());
        }
        if ("Y".equals(data.getSamplePrivateWellWellNumber())) {
            cell = row.createCell(startCol++ );
            if (well != null) {
                wn = well.getWellNumber();
                if (wn != null)
                    cell.setCellValue(wn);
            }
        }
        
        repTo = null;        
        if (well != null)
            repTo = well.getReportToAddress();        
        
        if ("Y".equals(data.getSamplePrivateWellReportToAddressWorkPhone())) {
            cell = row.createCell(startCol++ );          
            if (repTo != null)
                cell.setCellValue(repTo.getWorkPhone());
        }
        if ("Y".equals(data.getSamplePrivateWellReportToAddressFaxPhone())) {
            cell = row.createCell(startCol++ );
            if (repTo != null)
                cell.setCellValue(repTo.getFaxPhone());
        }
        if ("Y".equals(data.getSamplePrivateWellLocation())) {
            cell = row.createCell(startCol++ );
            if (well != null)
                cell.setCellValue(well.getLocation());
        }
        
        loc = null;        
        if (well != null)
            loc = well.getLocationAddress();
        
        if ("Y".equals(data.getSamplePrivateWellLocationAddressMultipleUnit())) {
            cell = row.createCell(startCol++ );
            if (loc != null)
                cell.setCellValue(loc.getMultipleUnit());
        }
        if ("Y".equals(data.getSamplePrivateWellLocationAddressStreetAddress())) {
            cell = row.createCell(startCol++ );
            if (loc != null)
                cell.setCellValue(loc.getStreetAddress());
        }
        if ("Y".equals(data.getSamplePrivateWellLocationAddressCity())) {
            cell = row.createCell(startCol++ );
            if (loc != null)
                cell.setCellValue(loc.getCity());
        }
        if ("Y".equals(data.getSamplePrivateWellLocationAddressState())) {
            cell = row.createCell(startCol++ );
            if (loc != null)
                cell.setCellValue(loc.getState());
        }
        if ("Y".equals(data.getSamplePrivateWellLocationAddressZipCode())) {
            cell = row.createCell(startCol++ );
            if (loc != null)
                cell.setCellValue(loc.getZipCode());
        }
    }
    
    private void addSDWISCells(Row row, int startCol, DataViewVO data, SampleSDWISViewDO sdwis, HashMap<Integer, PWSDO> pwsMap) {
        Integer id;
        Cell cell;    
        PWSDO pwsDO;
        
        /*
         * the SampleSDWISViewDO can be null if a sample is not a SDWIS sample but
         * the user has requested to view SDWIS fields and so we need to add empty
         * cells in the columns for those fields, because otherwise the data in 
         * the cells to the right of those columns will be shifted to the left 
         */        
        if ("Y".equals(data.getSampleSDWISPwsId())) {
            cell = row.createCell(startCol++);
            if (sdwis != null && pwsMap != null) {
                id = sdwis.getPwsId();
                pwsDO =  pwsMap.get(id);
                try {
                    if (pwsDO == null) {
                        pwsDO = pws.fetchById(id);
                        pwsMap.put(id, pwsDO);
                    }
                    cell.setCellValue(pwsDO.getNumber0());
                } catch (Exception e) {
                    log.log(Level.SEVERE, "Failed to lookup pws for id: " + id, e);
                }
            }
        } 
        if ("Y".equals(data.getSampleSDWISPwsName())) {
            cell = row.createCell(startCol++);
            if (sdwis != null)
                cell.setCellValue(sdwis.getPwsName());
        }
        if ("Y".equals(data.getSampleSDWISStateLabId())) {
            cell = row.createCell(startCol++);
            if (sdwis != null) {
                id = sdwis.getStateLabId();
                if (id != null)
                    cell.setCellValue(id);
            }
        }
        if ("Y".equals(data.getSampleSDWISFacilityId())) {
            cell = row.createCell(startCol++);
            if (sdwis != null)
                cell.setCellValue(sdwis.getFacilityId());
        }
        if ("Y".equals(data.getSampleSDWISSampleTypeId())) {
            cell = row.createCell(startCol++);
            if (sdwis != null)
                cell.setCellValue(dictEntryMap.get(sdwis.getSampleTypeId()));
        }
        if ("Y".equals(data.getSampleSDWISSampleCategoryId())) {
            cell = row.createCell(startCol++);
            if (sdwis != null)
                cell.setCellValue(dictEntryMap.get(sdwis.getSampleCategoryId()));
        }
        if ("Y".equals(data.getSampleSDWISSamplePointId())) {
            cell = row.createCell(startCol++);
            if (sdwis != null)
                cell.setCellValue(sdwis.getSamplePointId());
        }
        if ("Y".equals(data.getSampleSDWISLocation())) {
            cell = row.createCell(startCol++);
            if (sdwis != null)
                cell.setCellValue(sdwis.getLocation());
        }
        if ("Y".equals(data.getSampleSDWISCollector())) {
            cell = row.createCell(startCol++);
            if (sdwis != null)
                cell.setCellValue(sdwis.getCollector());
        }
    }
    
    private String getResultValue(HashMap<Integer, HashMap<String, String>> analyteResultMap,
                                  DataViewResultFetchVO res) throws Exception {
        String value;
        HashMap<String, String> valMap;        
        
        valMap = analyteResultMap.get(res.getAnalyteId());
        value = getValue(res.getValue(), resultDictId, res.getTypeId());
        if (valMap == null || valMap.get(value) == null)
            return null;
                
        return value;
    }
    
    private String getAuxDataValue(HashMap<Integer, HashMap<String, String>> auxFieldValueMap,
                                   DataViewAuxDataFetchVO aux) throws Exception {
        HashMap<String, String> valMap;
        String value;
        
        valMap = auxFieldValueMap.get(aux.getAnalyteId());
        value = getValue(aux.getValue(), auxFieldValueDictId, aux.getTypeId());
        if (valMap == null || valMap.get(value) == null)
            return null;                
                
        return value;
    }
    
    private String getValue(String value, Integer dictionaryId, Integer typeId) throws Exception {        
        Integer id;
        
        if (DataBaseUtil.isEmpty(value))
            return "";
        if (dictionaryId.equals(typeId)) {
            id = Integer.parseInt(value);
            value = dictionaryCache.getById(id).getEntry();                
        }
        
        return value;
    }
    
    private String getClause(String moduleName) throws Exception {
        /*
         * retrieving the organization Ids to which the user belongs from the
         * security clause in the userPermission
         */
        return userCache.getPermission().getModule(moduleName).getClause();        
    }
    
    private CellStyle createStyle(HSSFWorkbook wb) {
        CellStyle headerStyle;
        Font      font;

        font = wb.createFont();
        font.setColor(IndexedColors.WHITE.getIndex());
        headerStyle = wb.createCellStyle();
        headerStyle.setAlignment(CellStyle.ALIGN_LEFT);
        headerStyle.setVerticalAlignment(CellStyle.VERTICAL_BOTTOM);
        headerStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_80_PERCENT.getIndex());
        headerStyle.setFont(font);
        
        return headerStyle;
    }     
    
    private boolean isSameDataInRows(Row currRow, Row prevRow) {
        int prevType, currType;
        Cell prevCell, currCell;

        if (currRow == null || prevRow == null)
            return false;
        
        for (int i = 0; i < prevRow.getPhysicalNumberOfCells(); i++ ) {
            prevCell = prevRow.getCell(i);
            currCell = currRow.getCell(i);
            
            if (prevCell == null) {
                if (currCell == null)
                    continue;
                else
                    return false;
            } else if (currCell == null) {
                    return false;
            }
            
            prevType = prevCell.getCellType();
            currType = currCell.getCellType();
            
            if (prevType != currType)
                return false;
            
            switch (prevType) {
                case Cell.CELL_TYPE_STRING:
                    if ( !DataBaseUtil.isSame(prevCell.getStringCellValue(),
                                              currCell.getStringCellValue()))
                        return false;
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    if ( !DataBaseUtil.isSame(prevCell.getNumericCellValue(),
                                              currCell.getNumericCellValue()))
                        return false;
                    break;
                case Cell.CELL_TYPE_BOOLEAN:
                    if ( !DataBaseUtil.isSame(prevCell.getBooleanCellValue(),
                                              currCell.getBooleanCellValue()))
                        return false;
                    break;
            }
        }

        return true;
    } 
    
    private List<Object[]> fetchAnalyteAndAuxField(String key, QueryBuilderV2 builder,
                                                   ArrayList<QueryData> fields) throws Exception {
        Query query;
                
        builder.constructWhere(fields);
        builder.setOrderBy(key);
        query = manager.createQuery(builder.getEJBQL());
        builder.setQueryParams(query, fields);
        return ((List<Object[]>)query.getResultList());
    }
    
    private ArrayList<DataViewResultFetchVO> getResults(boolean addEnvCells, boolean addSDWISCells, boolean addWellCells,                                                                 
                                                          String moduleName, QueryBuilderV2 builder, QueryData reportTo,
                                                          HashMap<Integer, HashMap<String, String>> analyteResultMap, 
                                                          DataViewVO data, Comparator<Object> comparator) throws Exception {
        List<DataViewResultFetchVO> list;


        if (reportTo == null) {    
            /*
             * if the user didn't choose to specify the "report to" organization's
             * name then the doesn't query include it
             */
            list = fetchResults(moduleName, builder, analyteResultMap, data);            
        } else if (addEnvCells || addSDWISCells) {
            /*
             * if the user chose to see data belonging to a domain other than private
             * well then we only run the query to look for samples having a sample
             * organization of type report to with a name matching the query
             */
            reportTo.key = SampleWebMeta.getSampleOrgOrganizationName();
            builder.addWhere(SampleWebMeta.getSampleOrgTypeId() +" = "+ organizationReportToId);
            list = fetchResults(moduleName, builder, analyteResultMap, data);
        } else if (addWellCells) {
            /*
             * if the user chose to see data belonging to the domain private well
             * then we only run the query to look for private well samples having
             * their report to as an organization with a name matching the query
             */
            reportTo.key = SampleWebMeta.getWellOrganizationName();
            list = fetchResults(moduleName, builder, analyteResultMap, data);
        } else {
            /*
             * if the user did not choose a domain then we run a query for each
             * of the above two cases
             */
            list = new ArrayList<DataViewResultFetchVO>();
            
            reportTo.key = SampleWebMeta.getSampleOrgOrganizationName();
            builder.addWhere(SampleWebMeta.getSampleOrgTypeId() +" = "+ organizationReportToId);
            list.addAll(fetchResults(moduleName, builder, analyteResultMap, data));     
                        
            builder.clearWhereClause();
            
            reportTo.key = SampleWebMeta.getWellOrganizationName();
            list.addAll(fetchResults(moduleName, builder, analyteResultMap, data));
            
            Collections.sort(list, comparator);
        }
        
        return DataBaseUtil.toArrayList(list);
    }
    
    private ArrayList<DataViewAuxDataFetchVO> getAuxData(boolean addEnvCells, boolean addSDWISCells,
                                                         boolean addWellCells, String moduleName,
                                                         QueryBuilderV2 builder, QueryData reportTo,
                                                         HashMap<Integer, HashMap<String, String>> auxFieldValueMap, 
                                                         DataViewVO data, Comparator<Object> comparator) throws Exception {
         List<DataViewAuxDataFetchVO> list;        
         
         if (reportTo == null) {          
             /*
              * if the user didn't choose to specify the "report to" organization's
              * name then the doesn't query include it
              */
             list = fetchAuxData(moduleName, builder, auxFieldValueMap, data);
         } else if (addEnvCells || addSDWISCells) {
             /*
              * if the user chose to see data belonging to a domain other than private
              * well then we only run the query to look for samples having a sample
              * organization of type report to with a name matching the query
              */
             reportTo.key = SampleWebMeta.getSampleOrgOrganizationName();
             builder.addWhere(SampleWebMeta.getSampleOrgTypeId() +" = "+ organizationReportToId);             
             list = fetchAuxData(moduleName, builder, auxFieldValueMap, data);
         } else if (addWellCells) {       
             /*
              * if the user chose to see data belonging to the domain private well
              * then we only run the query to look for private well samples having
              * their report to as an organization with a name matching the query
              */
             reportTo.key = SampleWebMeta.getWellOrganizationName();
             list = fetchAuxData(moduleName, builder, auxFieldValueMap, data);
         } else {       
             /*
              * if the user did not choose a domain then we run a query for each
              * of the above two cases
              */
             list = new ArrayList<DataViewAuxDataFetchVO>();
             
             reportTo.key = SampleWebMeta.getSampleOrgOrganizationName();
             builder.addWhere(SampleWebMeta.getSampleOrgTypeId() +" = "+ organizationReportToId);
             list.addAll(fetchAuxData(moduleName, builder, auxFieldValueMap, data));  
             
             builder.clearWhereClause();
             
             reportTo.key = SampleWebMeta.getWellOrganizationName();
             list.addAll(fetchAuxData(moduleName, builder, auxFieldValueMap, data));
             
             Collections.sort(list, comparator);
         }
         
         return DataBaseUtil.toArrayList(list);
     }
    
    private class DataViewComparator implements Comparator<Object> {
        public int compare(Object dv1, Object dv2) {            
            int diff;
            DataViewResultFetchVO res1, res2;
            DataViewAuxDataFetchVO aux1, aux2;
            Integer accNum1, accNum2, analysisId1, analysisId2;
            String analyte1, analyte2;

            accNum1 = accNum2 = 0;
            analysisId1 = analysisId2 = null;
            analyte1 = analyte2 = null;
            
            if (dv1 instanceof DataViewResultFetchVO && dv2 instanceof DataViewResultFetchVO) {
                res1 = (DataViewResultFetchVO) dv1;
                res2 = (DataViewResultFetchVO) dv2;
                accNum1 = res1.getSampleAccessionNumber();
                accNum2 = res2.getSampleAccessionNumber();
                analysisId1 = res1.getAnalysisId();
                analysisId2 = res2.getAnalysisId();
                analyte1 = res1.getAnalyteName();
                analyte2 = res2.getAnalyteName();
            } else if (dv1 instanceof DataViewAuxDataFetchVO && dv2 instanceof DataViewAuxDataFetchVO) {
                aux1 = (DataViewAuxDataFetchVO) dv1;
                aux2 = (DataViewAuxDataFetchVO) dv2;
                accNum1 = aux1.getSampleAccessionNumber();
                accNum2 = aux2.getSampleAccessionNumber();
                analyte1 = aux1.getAnalyteName();
                analyte2 = aux2.getAnalyteName();
            }            

            /*
             * If the accession numbers are different then we don't compare the
             * names of the analytes. If the numbers are the same then the ids of
             * the analyses are compared so that the analytes can be grouped by 
             * analysis and if they turn out to be the same then a comparison by
             * String is used for the names.
             */
            diff = accNum1 - accNum2;
            if (diff != 0) {
                return diff;
            } else {
                if ((analysisId1 != null && analysisId2 != null)) {
                    diff = analysisId1.compareTo(analysisId2);
                    return (diff != 0) ? diff : compare(analyte1, analyte2);
                } else { 
                    return compare(analyte1, analyte2);
                }
            }
        }

        private int compare(String anaName1, String anaName2) {
            if (anaName1 == null) {
                return (anaName2 == null) ? 0 : 1;
            } else {
                return (anaName2 == null) ? -1 : anaName1.compareTo(anaName2);                
            }
        }
    }     
    
}