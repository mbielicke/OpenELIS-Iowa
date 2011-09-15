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

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.openelis.domain.AddressDO;
import org.openelis.domain.AnalysisQaEventViewDO;
import org.openelis.domain.AnalysisUserViewDO;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.AuxDataDumpVO;
import org.openelis.domain.AuxDataViewDO;
import org.openelis.domain.AuxFieldDataDumpVO;
import org.openelis.domain.CategoryCacheVO;
import org.openelis.domain.DataDumpVO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.OrganizationDO;
import org.openelis.domain.PWSDO;
import org.openelis.domain.ReferenceTable;
import org.openelis.domain.ResultDataDumpVO;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.SampleDO;
import org.openelis.domain.SampleEnvironmentalDO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.domain.SampleOrganizationViewDO;
import org.openelis.domain.SamplePrivateWellViewDO;
import org.openelis.domain.SampleProjectViewDO;
import org.openelis.domain.SampleSDWISViewDO;
import org.openelis.domain.TestAnalyteDataDumpVO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.Datetime;
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
import org.openelis.manager.SampleManager;
import org.openelis.meta.SampleWebMeta;
import org.openelis.remote.DataDumpRemote;
import org.openelis.util.QueryBuilderV2;
import org.openelis.util.UTFResource;
import org.openelis.utils.ReportUtil;

@Stateless

@SecurityDomain("openelis")
public class DataDumpBean implements DataDumpRemote {
    
    @PersistenceContext(unitName = "openelis")
    private EntityManager                   manager;

    @EJB
    private SessionCacheLocal               session;

    @EJB
    private SampleProjectLocal              sampleProject;

    @EJB
    private SampleQAEventLocal              sampleQaEvent;

    @EJB
    private AnalysisQAEventLocal            analysisQaEvent;

    @EJB
    private ResultLocal                     result;

    @EJB
    private AuxDataLocal                    auxData;

    @EJB
    private DictionaryLocal                 dictionary;

    @EJB
    private DictionaryCacheLocal            dictionaryCache;

    @EJB
    private CategoryCacheLocal              categoryCache;

    @EJB
    private UserCacheLocal                  userCache;

    @EJB
    private SampleLocal                     sample;
    
    @EJB
    private SampleOrganizationLocal         sampleOrganization;
    
    @EJB
    private SampleItemLocal                 sampleItem; 
    
    @EJB
    private AnalysisLocal                   analysis;
    
    @EJB
    private AnalysisUserLocal               analysisUser;
    
    @EJB
    private SampleEnvironmentalLocal        sampleEnvironmental;  
    
    @EJB
    private SamplePrivateWellLocal          samplePrivateWell;  
    
    @EJB
    private SampleSDWISLocal                sampleSDWIS;
    
    @EJB
    private PWSLocal                        pws;
    
    private static Integer                  resultDictId, auxFieldValueDictId, 
                                            completedActId, releasedActId ;   

    private static final SampleWebMeta      meta = new SampleWebMeta();

    private static final Logger             log  = Logger.getLogger(DataDumpBean.class);

    private static UTFResource              resource;

    private static HashMap<Integer, String> dictEntryMap;
    
    @PostConstruct
    public void init() {
        ArrayList<DictionaryDO> list;
        CategoryCacheVO cat;
        String locale;        
        
        if (resultDictId == null) {
            try {
                resultDictId = dictionary.fetchBySystemName("test_res_type_dictionary").getId();
                auxFieldValueDictId = dictionary.fetchBySystemName("aux_dictionary").getId();
                completedActId = dictionary.fetchBySystemName("an_user_ac_completed").getId(); 
                releasedActId = dictionary.fetchBySystemName("an_user_ac_released").getId();
                
                dictEntryMap = new HashMap<Integer, String>();
                
                cat = categoryCache.getBySystemName("sample_status");
                list = cat.getDictionaryList();
                for (DictionaryDO data : list) 
                    dictEntryMap.put(data.getId(), data.getEntry());
                
                cat = categoryCache.getBySystemName("analysis_status");
                list = cat.getDictionaryList();
                for (DictionaryDO data : list) 
                    dictEntryMap.put(data.getId(), data.getEntry()); 
                
                cat = categoryCache.getBySystemName("sdwis_sample_type");
                list = cat.getDictionaryList();
                for (DictionaryDO data : list) 
                    dictEntryMap.put(data.getId(), data.getEntry()); 
                
                cat = categoryCache.getBySystemName("sdwis_sample_category");
                list = cat.getDictionaryList();
                for (DictionaryDO data : list) 
                    dictEntryMap.put(data.getId(), data.getEntry());
                                
            } catch (Throwable e) {
                log.error("Failed to lookup constants for dictionary entries", e);
            }
        }
        
        try {
            if (resource == null) {
                try {
                    locale = userCache.getLocale();
                } catch (Exception e) {
                    locale = "en";
                }
                resource = UTFResource.getBundle("org.openelis.constants.OpenELISConstants",
                                                 new Locale(locale));
            }
        } catch (Throwable e) {
            log.error("Failed to initialize resource bundle", e);
        }
    }
    
    public ArrayList<IdNameVO> fetchPermanentProjectList() throws Exception {
        return sampleProject.fetchPermanentProjectList();
    }

    @TransactionTimeout(180)
    public DataDumpVO fetchAnalyteResultAndAuxData(ArrayList<QueryData> fields) throws Exception {
        int i;
        Integer samId, prevSamId, analysisId;
        String exOverride;
        Query query;
        QueryBuilderV2 builder;
        List<Object[]> list;
        ArrayList<Integer> anIdList, samIdList;
        ArrayList<ResultViewDO> resList;
        ArrayList<AuxDataViewDO> auxList;
        Object[] vo;
        DataDumpVO data;

        exOverride = null;
        for (QueryData field : fields) {
            if ("excludeResultOverride".equals(field.key)) {
                exOverride = field.query;
                fields.remove(field);
                break;
            }
        }
        builder = new QueryBuilderV2();
        builder.setMeta(meta);
        builder.setSelect("distinct " + SampleWebMeta.getAnalysisId() + ", " + SampleWebMeta.getId() + " ");
        builder.constructWhere(fields);
        builder.addWhere(SampleWebMeta.getItemId() + "=" + SampleWebMeta.getAnalysisSampleItemId());
        builder.setOrderBy(SampleWebMeta.getId());

        query = manager.createQuery(builder.getEJBQL());
        builder.setQueryParams(query, fields);
        list = (List<Object[]>)query.getResultList();

        if (list.isEmpty())
            throw new NotFoundException();

        anIdList = new ArrayList<Integer>();
        samIdList = new ArrayList<Integer>();
        prevSamId = null;
        if ("Y".equals(exOverride)) {
            /*
             * if there are qa event(s) of type result override found for a
             * sample then all the results under it are excluded, i.e. the
             * analysis's id is not added to the list that's used for the query
             * executed to fetch the results, whereas if an analysis has such
             * qa event(s) then only its results are excluded
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
                        samIdList.add(samId);
                    }
                }
                try {
                    // we found result override qa event(s) for an analysis
                    analysisQaEvent.fetchResultOverrideByAnalysisId(analysisId);                    
                } catch (NotFoundException e1) {
                    anIdList.add(analysisId);
                }
                prevSamId = samId;
            }
        } else {
            for (i = 0; i < list.size(); i++ ) {
                vo = list.get(i);
                anIdList.add((Integer)vo[0]);
                samId = (Integer)vo[1];
                if ( !samId.equals(prevSamId))
                    samIdList.add(samId);
                prevSamId = samId;
            }
        }

        data = new DataDumpVO();
        resList = null;
        try {
            if (anIdList.size() > 0) {
                resList = result.fetchForDataDump(anIdList);
                data.setAnalytes(getTestAnalytes(resList));
            }
        } catch (NotFoundException e) {
            // ignore
        }

        auxList = null;
        try {
            if (samIdList.size() > 0) {
                auxList = auxData.fetchForDataDump(samIdList, ReferenceTable.SAMPLE);
                data.setAuxFields(getAuxFields(auxList));
            }
        } catch (NotFoundException e) {
            // ignore
        }

        if (resList == null && auxList == null)
            throw new NotFoundException();

        return data;
    }
    
    public ReportStatus runReport(DataDumpVO data) throws Exception {
        String exOverride;
        FileOutputStream out;
        File tempFile;
        List<Object[]> results;
        ArrayList<TestAnalyteDataDumpVO> anaList;
        ArrayList<AuxFieldDataDumpVO> auxList;
        ReportStatus status;
        Query query;
        QueryBuilderV2 builder;
        ArrayList<QueryData> fields;
        HashMap<String, String> resultMap, valueMap;
        HashMap<Integer, HashMap<String, String>> analyteResultMap, auxFieldValueMap;
        HSSFWorkbook wb;
        
        status = new ReportStatus();
        status.setMessage("Initializing report");
        session.setAttribute("DataDump", status);
        //TODO comment getting ids of all the chosen results
        anaList = data.getTestAnalytes();
        analyteResultMap = null;
        if (anaList != null) {
            /*
             * the analytes and results selected by the user are stored in this 
             * hash map so that they can be used later on to select or rejects
             * samples, tests etc.
             */
            analyteResultMap = new HashMap<Integer, HashMap<String,String>>();
            for (TestAnalyteDataDumpVO ana : anaList) {
                if ("N".equals(ana.getIsIncluded()))
                    continue;  
                resultMap = new HashMap<String, String>();
                for (ResultDataDumpVO res : ana.getResults()) {
                    if ("Y".equals(res.getIsIncluded()))
                        resultMap.put(res.getValue(), res.getValue());
                }
                analyteResultMap.put(ana.getAnalyteId(), resultMap);
            }                        
        }
        
        auxList = data.getAuxFields();
        auxFieldValueMap = null;
        if (auxList != null) {
            auxFieldValueMap = new HashMap<Integer, HashMap<String,String>>();            
            for (AuxFieldDataDumpVO af : auxList) {
                if ("N".equals(af.getIsIncluded()))
                    continue;
                valueMap = new HashMap<String, String>();
                for (AuxDataDumpVO val : af.getValues()) {
                    if ("Y".equals(val.getIsIncluded()))
                        valueMap.put(val.getValue(), val.getValue());
                }
                auxFieldValueMap.put(af.getAnalyteId(), valueMap);
            }                        
        }
        
        fields = data.getQuery().getFields();
        for (QueryData field : fields) {
            if ("excludeResultOverride".equals(field.key)) {
                exOverride = field.query;
                fields.remove(field);
                break;
            }
        }
        builder = new QueryBuilderV2();
        builder.setMeta(meta);        
        
        builder.setSelect("distinct " + SampleWebMeta.getAccessionNumber() +                          
                          ", " + SampleWebMeta.getResultAnalyteName() +  
                          ", " + SampleWebMeta.getId() + 
                          ", " + SampleWebMeta.getDomain() +
                          ", " + SampleWebMeta.getItemId() +
                          ", " + SampleWebMeta.getResultAnalysisid() +
                          ", " + SampleWebMeta.getResultIsColumn() +
                          ", " + SampleWebMeta.getResultAnalyteId() +
                          ", " + SampleWebMeta.getResultTypeId() +
                          ", " + SampleWebMeta.getResultValue());
        builder.constructWhere(fields);        
        if (analyteResultMap != null && analyteResultMap.size() > 0) {
            builder.addWhere(SampleWebMeta.getItemId() + "=" + SampleWebMeta.getAnalysisSampleItemId());
            builder.addWhere(SampleWebMeta.getResultIsReportable()+ "=" + "'Y'");
            builder.addWhere(SampleWebMeta.getResultIsColumn()+ "=" + "'N'");
            builder.addWhere(SampleWebMeta.getResultValue()+ "!=" + "null");
            builder.addWhere(SampleWebMeta.getResultAnalyteId()+ getListParam(analyteResultMap.keySet())+")");
            builder.setOrderBy(SampleWebMeta.getAccessionNumber()+","+SampleWebMeta.getResultAnalyteName());
        }        
        //if (auxIds != null && auxIds.size() > 0)
         //   builder.addWhere(SampleWebMeta.getAuxDataId() + getListParam(auxIds));
        query = manager.createQuery(builder.getEJBQL());
        builder.setQueryParams(query, fields);
        results = query.getResultList(); 
                
        wb = getWorkbook(results,data, analyteResultMap);        
        if (wb != null) {
            out = null;
            try {
                status.setMessage("Outputing report").setPercentComplete(20);
                tempFile = File.createTempFile("datadump", ".xls", new File("/tmp"));
                
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
    
    private HSSFWorkbook getWorkbook(List<Object[]> results, DataDumpVO data,
                                     HashMap<Integer, HashMap<String, String>> analyteResultMap) throws Exception {
        int i, j; 
        boolean showSample, showOrg, showItem, showAnalysis, showEnv, showWell, showSDWIS;
        Integer samId, prevSamId, dictId, itemId, prevItemId, anaId, prevAnaId;        
        String value, domain, qaeNames, compByNames, relByNames, userName;
        StringBuffer buf;
        HSSFWorkbook wb;
        HSSFSheet sheet;
        Row row;
        Cell cell;
        SampleDO sam;        
        SampleProjectViewDO proj;
        SampleOrganizationViewDO org;
        SampleEnvironmentalDO env;
        SamplePrivateWellViewDO well;
        SampleSDWISViewDO sdwis;
        SampleItemViewDO item;
        AnalysisViewDO ana;
        HashMap<String, String> resValueMap;
        AnalysisQaEventViewDO aqe;
        ArrayList<String> allCols, cols;
        ArrayList<SampleProjectViewDO> projList;
        ArrayList<SampleOrganizationViewDO> orgList;
        ArrayList<AnalysisQaEventViewDO> aqeList;
        ArrayList<AnalysisUserViewDO> anaCompList, anaRelList; 
        HashMap<Integer, PWSDO> pwsMap;
                
        if (results == null || results.size() == 0)
            return null;
                
        /*
         * we iterate through the results and for each analyte id in analyteResultMap
         * check to see if a result's value matches one of the values selected for
         * that analyte and if it does then we fetch the additional data needed 
         * for the columns in the sheet like the ones for sample, organization etc. 
         */
        allCols = new ArrayList<String>();
        allCols.add(resource.getString("analyte"));
        
        showSample = false;        
        cols = getSampleHeaders(data);         
        if (cols.size() > 0) {            
            allCols.addAll(cols);
            showSample = true;
        }        
        
        cols = getOrganizationHeaders(data);    
        showOrg = false;
        if (cols.size() > 0) {            
            allCols.addAll(cols);
            showOrg = true;
        }
        
        cols = getSampleItemHeaders(data);
        showItem = false;
        if (cols.size() > 0) {            
            allCols.addAll(cols);
            showItem = true;
        }
        
        cols = getAnalysisHeaders(data);    
        showAnalysis = false;
        if (cols.size() > 0) {            
            allCols.addAll(cols);
            showAnalysis = true;
        }
        
        cols = getEnvironmentalHeaders(data);
        showEnv = false;
        if (cols.size() > 0) {            
            allCols.addAll(cols);
            showEnv = true;
        }
                
        cols = getPrivateWellHeaders(data);    
        showWell = false;
        if (cols.size() > 0) {            
            allCols.addAll(cols);
            showWell = true;
        }
        
        cols = getSDWISHeaders(data);    
        showSDWIS = false;
        if (cols.size() > 0) {            
            allCols.addAll(cols);
            showSDWIS = true;
        }
                
        allCols.add(resource.getString("value"));        
        
        wb = new HSSFWorkbook();
        sheet = wb.createSheet();
        row  = sheet.createRow(1);
        //
        // add all the columns to the header 
        //
        for (i = 0; i < allCols.size(); i++) {
            cell = row.createCell(i);
            cell.setCellValue(allCols.get(i));   
        }
                
        i = 2;
        prevItemId = null;
        prevSamId = null;
        prevAnaId = null;
        sam = null;
        proj = null;
        org = null;
        env = null;
        well = null;
        sdwis = null;
        item = null;
        ana = null;
        aqeList = null;
        anaCompList = null;
        anaRelList = null;
        relByNames = null;
        buf = null;
        qaeNames = null;
        compByNames = null;  
        pwsMap = null;
        
        for (Object res[] : results) {
            resValueMap = analyteResultMap.get(res[7]);
            value = (String)res[9];
            if (resultDictId.equals(res[8])) {
                dictId = Integer.parseInt(value);
                value = dictionaryCache.getById(dictId).getEntry();                
            }
            if (resValueMap.get(value) == null)
                continue;
                        
            row  = sheet.createRow(i++);      
            cell = row.createCell(0);
            cell.setCellValue(((String)res[1]).trim());
            
            samId = (Integer)res[2];
            domain = (String)res[3];
            itemId = (Integer)res[4];
            anaId = (Integer)res[5]; 
            if (!samId.equals(prevSamId)) {
                sam = null;
                proj = null;        
                org = null;
                env = null;
                well = null;
                sdwis = null;
            }
            if (showSample) {
                if (sam == null)
                    sam = sample.fetchById(samId);
                if ("Y".equals(data.getProjectName())) {
                    if (proj == null) {
                        try {
                            /*
                             * we fetch the sample project here and not in the
                             * method that adds the cells for the sample because
                             * the data for the project needs to be fetched only
                             * once for a sample and that method is called for
                             * each analyte under a sample
                             */
                            projList = sampleProject.fetchPermanentBySampleId(samId);
                            proj = projList.get(0);
                        } catch (NotFoundException e) {
                            // ignore
                        }
                    }
                }
                setSampleCells(row, row.getPhysicalNumberOfCells(), data, sam, proj);
            }            
                                 
            if (showOrg) {
                if (SampleManager.WELL_DOMAIN_FLAG.equals(domain)) {
                    if (well == null) 
                        well = samplePrivateWell.fetchBySampleId(samId);                    
                    setPrivateWellOrganizationCells(row, row.getPhysicalNumberOfCells(), data, well);
                } else {
                    if (org == null) {
                        try {
                            orgList = sampleOrganization.fetchReportToBySampleId(samId);
                            org = orgList.get(0);
                        } catch (NotFoundException e) {
                            // ignore
                        }
                    }
                    setOrganizationCells(row, row.getPhysicalNumberOfCells(), data, org);
                }
            }
            
            if (showItem) {
                if (!itemId.equals(prevItemId)) 
                    item = sampleItem.fetchById(itemId);
                setSampleItemCells(row, row.getPhysicalNumberOfCells(), data, item);
            }
            
            if (showAnalysis) {
                if (!anaId.equals(prevAnaId)) {
                    ana = analysis.fetchById(anaId);
                    aqeList = null;
                    anaCompList = null;
                    anaRelList = null;
                    qaeNames = null;
                    compByNames = null;
                    relByNames = null;
                    
                    if ("Y".equals(data.getAnalysisQaName()) && aqeList == null) {
                        try {
                            aqeList = analysisQaEvent.fetchByAnalysisId(anaId);                            
                            buf = new StringBuffer();                            
                            for (j = 0; j < aqeList.size(); j++) {
                                aqe = aqeList.get(j);
                                buf.append(aqe.getQaEventName());
                                if (j < aqeList.size() - 1)
                                    buf.append(", ");
                                
                            }
                            qaeNames = buf.toString();
                        } catch (NotFoundException ignE) {
                            // ignore
                        }
                    }                    
                    if ("Y".equals(data.getAnalysisCompletedBy()) && anaCompList == null) {
                        try {
                            anaCompList = analysisUser.fetchByActionAndAnalysisId(anaId, completedActId);  
                            buf = new StringBuffer();                            
                            for (j = 0; j < anaCompList.size(); j++) {
                                userName = anaCompList.get(j).getSystemUser();
                                /*
                                 * the user's login name could be null in this DO
                                 * if there was a problem with fetching the data
                                 * from security
                                 */
                                if (userName != null)
                                    buf.append(userName);
                                if (j < anaCompList.size() - 1)
                                    buf.append(", ");
                                
                            }
                            compByNames = buf.toString();
                        } catch (NotFoundException ignE) {
                            // ignore
                        }
                    }                    
                    if ("Y".equals(data.getAnalysisReleasedBy()) && anaRelList == null) {
                        try {
                            anaRelList = analysisUser.fetchByActionAndAnalysisId(anaId, releasedActId);
                            relByNames = anaRelList.get(0).getSystemUser();
                        } catch (NotFoundException ignE) {
                            // ignore
                        }
                    }                    
                }
                
                setAnalysisCells(row, row.getPhysicalNumberOfCells(), data, ana, qaeNames, compByNames, relByNames);            
            }         
            
            /*
             * we need to make sure that a given sample is of a given domain
             * before fetching the data for that domain, but we need to add 
             * cells (filled or not) for the fields from that domain in the file
             * for a given row regardless, if the user selected them to be shown 
             */
            if (showEnv) {
                if (SampleManager.ENVIRONMENTAL_DOMAIN_FLAG.equals(domain) && env == null) 
                        env = sampleEnvironmental.fetchBySampleId(samId);                
                setEnvironmentalCells(row, row.getPhysicalNumberOfCells(), data, env);
            } 
            
            if (showWell) {
                if (SampleManager.WELL_DOMAIN_FLAG.equals(domain) && well == null) 
                        well = samplePrivateWell.fetchBySampleId(samId);                
                setPrivateWellCells(row, row.getPhysicalNumberOfCells(), data, well);
            } 
            
            if (showSDWIS) {
                if (SampleManager.SDWIS_DOMAIN_FLAG.equals(domain) && sdwis == null) { 
                    sdwis = sampleSDWIS.fetchBySampleId(samId);                
                    if ("Y".equals(data.getSampleSDWISPwsId()) && pwsMap == null) 
                        pwsMap = new HashMap<Integer, PWSDO>();                                            
                }
                setSDWISCells(row, row.getPhysicalNumberOfCells(), data, sdwis, pwsMap);
            }
            
            cell = row.createCell(row.getPhysicalNumberOfCells());
            cell.setCellValue(value.trim());
            
            anaId = prevAnaId;
            prevItemId = itemId;
            prevSamId = samId;            
        }
        
        for (i = 0; i < allCols.size(); i++) 
            sheet.autoSizeColumn(i);        
        
        return wb;
    }  
    
    private ArrayList<TestAnalyteDataDumpVO> getTestAnalytes(ArrayList<ResultViewDO> resList) throws Exception {
        Integer taId, dictId;
        String value;
        TestAnalyteDataDumpVO anadd;
        ResultDataDumpVO resdd;
        ArrayList<TestAnalyteDataDumpVO> anaddList;
        ArrayList<ResultDataDumpVO> resddList;
        HashMap<Integer, TestAnalyteDataDumpVO> anaMap;     
        HashMap<String, String> resMap; 
        
        if (resList == null)
            return null;
        
        taId = null;
        resddList = null;
        anaddList = new ArrayList<TestAnalyteDataDumpVO>();
        anaMap = new HashMap<Integer, TestAnalyteDataDumpVO>();
        resMap = null;
        // TODO comments
        for (ResultViewDO res : resList) {
            taId = res.getAnalyteId();
            anadd = anaMap.get(taId);
            if (anadd == null) {                
                anadd = new TestAnalyteDataDumpVO();
                anadd.setAnalyteId(res.getAnalyteId());
                anadd.setAnalyteName(res.getAnalyte());
                anadd.setTestAnalyteId(res.getTestAnalyteId());
                anadd.setIsIncluded("N");
                resddList = new ArrayList<ResultDataDumpVO>();
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
                value = dictionaryCache.getById(dictId).getEntry();
            }
            
            /*
             * we don't allow the same value to be shown more than once for the 
             * same analyte
             */
            if (resMap.get(value)!= null)
                continue;
            resMap.put(value, value);
            
            resdd = new ResultDataDumpVO();
            resdd.setValue(value);
            resdd.setIsIncluded("N");
            resddList.add(resdd);
        }
        return anaddList;
    }
    
    private ArrayList<AuxFieldDataDumpVO> getAuxFields(ArrayList<AuxDataViewDO> valList) throws Exception {
        Integer taId, dictId;
        String value;
        AuxFieldDataDumpVO anadd;
        AuxDataDumpVO resdd;
        ArrayList<AuxFieldDataDumpVO> anaddList;
        ArrayList<AuxDataDumpVO> resddList;
        HashMap<Integer, AuxFieldDataDumpVO> anaMap;  
        HashMap<String, String> resMap; 
        
        if (valList == null)
            return null;
        
        taId = null;
        resddList = null;
        anaddList = new ArrayList<AuxFieldDataDumpVO>();
        anaMap = new HashMap<Integer, AuxFieldDataDumpVO>();
        resMap = null;
        // TODO comments
        for (AuxDataViewDO res : valList) {
            taId = res.getAnalyteId();
            anadd = anaMap.get(taId);
            if (anadd == null) {       
                anadd = new AuxFieldDataDumpVO();
                anadd.setAnalyteId(res.getAnalyteId());
                anadd.setAnalyteName(res.getAnalyteName());
                anadd.setAuxFieldId(res.getAuxFieldId());    
                anadd.setIsIncluded("N");
                resddList = new ArrayList<AuxDataDumpVO>();
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
                value = dictionaryCache.getById(dictId).getEntry();
            }
            
            /*
             * we don't allow the same value to be shown more than once for the 
             * same analyte
             */
            if (resMap.get(value)!= null)
                continue;
            
            resdd = new AuxDataDumpVO();
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
    
    private ArrayList<String> getSampleHeaders(DataDumpVO data) {
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
    
    private ArrayList<String> getOrganizationHeaders(DataDumpVO data) {
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
        if ("Y".equals(data.getOrganizationAddressCountry()))
            headers.add(resource.getString("country"));
        
        return headers;            
    }
        
    private ArrayList<String> getSampleItemHeaders(DataDumpVO data) {
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
        
        return headers;            
    }
    
    private ArrayList<String> getAnalysisHeaders(DataDumpVO data) {
        ArrayList<String> headers;
        
        headers = new ArrayList<String>();
        if ("Y".equals(data.getAnalysisTestNameHeader()))
            headers.add(resource.getString("test"));
        if ("Y".equals(data.getAnalysisTestMethodNameHeader()))
            headers.add(resource.getString("method"));
        if ("Y".equals(data.getAnalysisStatusIdHeader()))
            headers.add(resource.getString("analysisStatus"));
        if ("Y".equals(data.getAnalysisRevision()))
            headers.add(resource.getString("analysisRevision"));
        if ("Y".equals(data.getAnalysisIsReportable()))
            headers.add(resource.getString("reportable"));
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
        
        return headers;            
    }
    
    private ArrayList<String> getEnvironmentalHeaders(DataDumpVO data) {
        ArrayList<String> headers;
        
        headers = new ArrayList<String>();
        if ("Y".equals(data.getSampleEnvironmentalIsHazardous()))
            headers.add(resource.getString("hazardous"));
        if ("Y".equals(data.getSampleEnvironmentalPriority()))
            headers.add(resource.getString("priority"));
        if ("Y".equals(data.getSampleEnvironmentalCollector()))
            headers.add(resource.getString("collector"));
        if ("Y".equals(data.getSampleEnvironmentalCollectorPhone()))
            headers.add(resource.getString("phone"));
        if ("Y".equals(data.getSampleEnvironmentalLocation()))
            headers.add(resource.getString("location"));        
        if ("Y".equals(data.getSampleEnvironmentalLocationAddressCity()))
            headers.add(resource.getString("locationCity"));
        if ("Y".equals(data.getSampleEnvironmentalDescription()))
            headers.add(resource.getString("description"));
        
        return headers;            
    }
    
    private ArrayList<String> getPrivateWellHeaders(DataDumpVO data) {
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
        if ("Y".equals(data.getSamplePrivateWellLocationAddressCity()))
            headers.add(resource.getString("locationCity"));
        
        return headers;            
    }
    
    private ArrayList<String> getSDWISHeaders(DataDumpVO data) {
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
    
    private void setSampleCells(Row row, int startCol, DataDumpVO data, 
                                SampleDO sample, SampleProjectViewDO project) {  
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
            dt = sample.getCollectionDate();
            if (dt != null) 
                cell.setCellValue(dt.toString());            
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
                cell.setCellValue(project.getProjectDescription());
        }
        if ("Y".equals(data.getClientReferenceHeader())) {
            cell = row.createCell(startCol++);
            cell.setCellValue(sample.getClientReference());
        }        
    }
    
    private void setOrganizationCells(Row row, int startCol, DataDumpVO data, 
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
        if ("Y".equals(data.getOrganizationAddressCountry())) {
            cell = row.createCell(startCol++);
            //cell.setCellValue(org.getOrganization());
        }         
    }
    
    private void setPrivateWellOrganizationCells(Row row, int startCol, DataDumpVO data, 
                                                 SamplePrivateWellViewDO spw) {  
        Cell cell;
        OrganizationDO org;
        AddressDO addr;
        
        org = spw.getOrganization();
        addr = null;
        if (org != null)
            addr = org.getAddress();
                
        if ("Y".equals(data.getOrganizationId())) {
            cell = row.createCell(startCol++);
            if (org != null)
                cell.setCellValue(org.getId());
        }             
        if ("Y".equals(data.getOrganizationName())) {
            cell = row.createCell(startCol++);
            if (org != null)
                cell.setCellValue(org.getName());
        }             
        if ("Y".equals(data.getOrganizationAttention())) {
            cell = row.createCell(startCol++);
            cell.setCellValue(spw.getReportToAttention());
        }       
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
        if ("Y".equals(data.getOrganizationAddressCountry())) {
            cell = row.createCell(startCol++);
            //cell.setCellValue(org.getOrganization());
        }         
    }
    
    private void setSampleItemCells(Row row, int startCol, DataDumpVO data, SampleItemViewDO item) {
        Integer id;
        Cell cell;
        DictionaryDO dict;

        if ("Y".equals(data.getSampleItemTypeofSampleId())) {
            cell = row.createCell(startCol++);
            id = item.getTypeOfSampleId();
            if (id != null) {
                try {
                    dict = dictionaryCache.getById(id);
                    cell.setCellValue(dict.getEntry());
                } catch (Exception e) {
                    log.error("Failed to lookup constants for dictionary entry: " + id, e);
                }
            }
        }
        if ("Y".equals(data.getSampleItemSourceOfSampleId())) {
            cell = row.createCell(startCol++);
            id = item.getSourceOfSampleId();
            if (id != null) {
                try {
                    dict = dictionaryCache.getById(id);
                    cell.setCellValue(dict.getEntry());
                } catch (Exception e) {
                    log.error("Failed to lookup constants for dictionary entry: " + id, e);
                }
            }
        }
        if ("Y".equals(data.getSampleItemSourceOther())) {
            cell = row.createCell(startCol++ );
            cell.setCellValue(item.getSourceOther());
        }
        if ("Y".equals(data.getSampleItemContainerId())) {
            cell = row.createCell(startCol++ );
            id = item.getContainerId();
            if (id != null) {
                try {
                    dict = dictionaryCache.getById(id);
                    cell.setCellValue(dict.getEntry());
                } catch (Exception e) {
                    log.error("Failed to lookup constants for dictionary entry: " + id, e);
                }
            }
        }
    }
    
    private void setAnalysisCells(Row row, int startCol, DataDumpVO data,
                                  AnalysisViewDO analysis, String qaeNames,
                                  String compByNames, String relByNames) {
        Cell cell;
        Datetime dt;
        boolean isRep;
        
        if ("Y".equals(data.getAnalysisTestNameHeader())) {
            cell = row.createCell(startCol++ );
            cell.setCellValue(analysis.getTestName());
        }
        if ("Y".equals(data.getAnalysisTestMethodNameHeader())) {
            cell = row.createCell(startCol++ );
            cell.setCellValue(analysis.getMethodName());
        }
        if ("Y".equals(data.getAnalysisStatusIdHeader())) {
            cell = row.createCell(startCol++);
            cell.setCellValue(dictEntryMap.get(analysis.getStatusId()));
        }
        if ("Y".equals(data.getAnalysisRevision())) {
            cell = row.createCell(startCol++);
            cell.setCellValue(analysis.getRevision());
        }
        if ("Y".equals(data.getAnalysisIsReportable())) {
            cell = row.createCell(startCol++);
            isRep = "Y".equals(analysis.getIsReportable());
            cell.setCellValue(isRep ? resource.getString("yes") : resource.getString("no"));
        }
        if ("Y".equals(data.getAnalysisQaName())) {
            cell = row.createCell(startCol++);
            if (qaeNames != null)           
                cell.setCellValue(qaeNames);                            
        }
        if ("Y".equals(data.getAnalysisCompletedDate())) {
            cell = row.createCell(startCol++);
            dt = analysis.getCompletedDate();
            if (dt != null) 
                cell.setCellValue(dt.toString());                            
        }
        if ("Y".equals(data.getAnalysisCompletedBy())) {
            cell = row.createCell(startCol++ );
            if (compByNames != null)
                cell.setCellValue(compByNames);
        }
        if ("Y".equals(data.getAnalysisReleasedDate())) {
            cell = row.createCell(startCol++);
            dt = analysis.getCompletedDate();
            if (dt != null) 
                cell.setCellValue(dt.toString()); 
        }
        if ("Y".equals(data.getAnalysisReleasedBy())) {
            cell = row.createCell(startCol++ );
            if (relByNames != null)
                cell.setCellValue(relByNames);
        }
        if ("Y".equals(data.getAnalysisStartedDate())) {
            cell = row.createCell(startCol++);
            dt = analysis.getStartedDate();
            if (dt != null) 
                cell.setCellValue(dt.toString());                            
        }
        if ("Y".equals(data.getAnalysisPrintedDate())) {
            cell = row.createCell(startCol++);
            dt = analysis.getPrintedDate();
            if (dt != null) 
                cell.setCellValue(dt.toString());                            
        }
    }
    
    private void setEnvironmentalCells(Row row, int startCol, DataDumpVO data, 
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
        if ("Y".equals(data.getSampleEnvironmentalCollector())) {
            cell = row.createCell(startCol++ );
            if (env != null)
                cell.setCellValue(env.getCollector());
        }
        if ("Y".equals(data.getSampleEnvironmentalCollectorPhone())) {
            cell = row.createCell(startCol++ );
            if (env != null)
                cell.setCellValue(env.getCollectorPhone());
        }
        if ("Y".equals(data.getSampleEnvironmentalLocation())) {
            cell = row.createCell(startCol++ );
            if (env != null)
                cell.setCellValue(env.getLocation());
        }
        if ("Y".equals(data.getSampleEnvironmentalLocationAddressCity())) {
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
    
    private void setPrivateWellCells(Row row, int startCol, DataDumpVO data, 
                                       SamplePrivateWellViewDO well) {
        Integer wn;
        Cell cell;    
        AddressDO repTo;
        
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
        if ("Y".equals(data.getSamplePrivateWellLocationAddressCity())) {
            cell = row.createCell(startCol++ );
            if (well != null)
                cell.setCellValue(well.getLocationAddress().getCity());
        }
    }
    
    private void setSDWISCells(Row row, int startCol, DataDumpVO data, SampleSDWISViewDO sdwis, HashMap<Integer, PWSDO> pwsMap) {
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
                    log.error("Failed to lookup pws for id: " + id, e);
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
}