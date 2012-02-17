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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFName;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.openelis.domain.DictionaryViewDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.SystemVariableDO;
import org.openelis.domain.WorksheetCreationVO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.local.DictionaryLocal;
import org.openelis.local.OrganizationLocal;
import org.openelis.local.SampleOrganizationLocal;
import org.openelis.local.SystemVariableLocal;
import org.openelis.manager.AnalysisQaEventManager;
import org.openelis.manager.SampleManager;
import org.openelis.manager.SampleQaEventManager;
import org.openelis.meta.WorksheetCreationMeta;
import org.openelis.remote.WorksheetCreationRemote;
import org.openelis.util.QueryBuilderV2;

@Stateless
@SecurityDomain("openelis")

public class WorksheetCreationBean implements WorksheetCreationRemote {

    @EJB
    DictionaryLocal dictionary;
    @EJB
    OrganizationLocal organization;
    @EJB
    SampleOrganizationLocal sampleOrganization;
    @EJB
    SystemVariableLocal systemVariable;

    @PersistenceContext(unitName = "openelis")
    private EntityManager manager;

    private static final Logger                log  = Logger.getLogger(WorksheetCreationBean.class);
    private static final WorksheetCreationMeta meta = new WorksheetCreationMeta();
    
    public WorksheetCreationBean() {
    }

    @SuppressWarnings("unchecked")
    public ArrayList<WorksheetCreationVO> query(ArrayList<QueryData> fields, 
                                                int first, int max) throws Exception {
        int                      i;
        List                     list = null;
        String                   description, reportToName;
        Query                    query;
        QueryBuilderV2           builder;
        AnalysisQaEventManager   analysisQaManager;
        SampleQaEventManager     sampleQaManager;
        WorksheetCreationVO      vo;

        builder = new QueryBuilderV2();
        builder.setMeta(meta);
        builder.setSelect("distinct new org.openelis.domain.WorksheetCreationVO("+
                          WorksheetCreationMeta.getAnalysisId()+", "+
                          WorksheetCreationMeta.getSampleId()+", "+
                          WorksheetCreationMeta.getSampleDomain()+", "+
                          WorksheetCreationMeta.getSampleAccessionNumber()+", "+
                          WorksheetCreationMeta.getSampleCollectionDate()+", "+
                          WorksheetCreationMeta.getSampleCollectionTime()+", "+
                          WorksheetCreationMeta.getSampleReceivedDate()+", "+
                          WorksheetCreationMeta.getSampleEnvironmentalLocation()+", "+
                          WorksheetCreationMeta.getSampleEnvironmentalPriority()+", "+
                          WorksheetCreationMeta.getSampleSDWISLocation()+", "+
                          WorksheetCreationMeta.getSamplePrivateWellLocation()+", "+
                          WorksheetCreationMeta.getSamplePrivateWellOrganizationId()+", "+
                          WorksheetCreationMeta.getSamplePrivateWellReportToName()+", "+
//                          WorksheetCreationMeta.getPatientLastName()+", "+
//                          WorksheetCreationMeta.getPatientFirstName()+", "+
                          WorksheetCreationMeta.getAnalysisTestId()+", " +
                          WorksheetCreationMeta.getAnalysisTestName()+", " +
                          WorksheetCreationMeta.getAnalysisTestMethodName()+", "+
                          WorksheetCreationMeta.getAnalysisTestTimeHolding()+", " +
                          WorksheetCreationMeta.getAnalysisTestTimeTaAverage()+", " +
                          WorksheetCreationMeta.getAnalysisSectionId()+", "+
                          WorksheetCreationMeta.getAnalysisPreAnalysisId()+", "+
                          WorksheetCreationMeta.getAnalysisStatusId()+", " +
                          WorksheetCreationMeta.getTestWorksheetFormatId()+") ");
        builder.constructWhere(fields);
        builder.setOrderBy(WorksheetCreationMeta.getSampleAccessionNumber());

        query = manager.createQuery(builder.getEJBQL());
        query.setMaxResults(first + max);

        builder.setQueryParams(query, fields);

        list = DataBaseUtil.toArrayList(query.getResultList());
        if (list.isEmpty()) {
            throw new NotFoundException();
        } else {
            for (i = 0; i < list.size(); i++) {
                vo = (WorksheetCreationVO) list.get(i);
                //
                // Set domain specific description
                //
                description = "";
                reportToName = "";
                if (SampleManager.ENVIRONMENTAL_DOMAIN_FLAG.equals(vo.getDomain())) {
                    if (vo.getEnvLocation() != null && vo.getEnvLocation().length() > 0)
                        description = "[loc]"+vo.getEnvLocation();
                    try {
                        reportToName = sampleOrganization.fetchReportToBySampleId(vo.getSampleId()).getOrganizationName();
                        if (reportToName != null && reportToName.length() > 0) {
                            if (description.length() > 0)
                                description += " ";
                            description += "[rpt]"+reportToName;
                        }
                    } catch (NotFoundException nfE) {
                        log.debug("Sample Environmental Report To not found: "+nfE.getMessage());
                    } catch (Exception anyE) {
                        log.error("Error looking up Sample Environmental Report To: "+anyE.getMessage());
                    }
                } else if (SampleManager.SDWIS_DOMAIN_FLAG.equals(vo.getDomain())) {
                    if (vo.getSDWISLocation() != null && vo.getSDWISLocation().length() > 0)
                        description = "[loc]"+vo.getSDWISLocation();
                    try {
                        reportToName = sampleOrganization.fetchReportToBySampleId(vo.getSampleId()).getOrganizationName();
                        if (reportToName != null && reportToName.length() > 0) {
                            if (description.length() > 0)
                                description += " ";
                            description += "[rpt]"+reportToName;
                        }
                    } catch (NotFoundException nfE) {
                        log.debug("Sample SDWIS Report To not found: "+nfE.getMessage());
                    } catch (Exception anyE) {
                        log.error("Error looking up Sample SDWIS Report To: "+anyE.getMessage());
                    }
                } else if (SampleManager.WELL_DOMAIN_FLAG.equals(vo.getDomain())) {
                    if (vo.getPrivateWellLocation() != null && vo.getPrivateWellLocation().length() > 0)
                        description = "[loc]"+vo.getPrivateWellLocation();
                    if (vo.getPrivateWellOrgId() != null) {
                        try {
                            reportToName = organization.fetchById(vo.getPrivateWellOrgId()).getName();
                        } catch (NotFoundException nfE) {
                            log.debug("Sample Private Well Report To not found: "+nfE.getMessage());
                        } catch (Exception anyE) {
                            log.error("Error looking up Sample Private Well Report To: "+anyE.getMessage());
                        }
                    } else {
                        reportToName = vo.getPrivateWellReportToName();
                    }
                    if (reportToName != null && reportToName.length() > 0) {
                        if (description.length() > 0)
                            description += " ";
                        description += "[rpt]"+reportToName;
                    }
                }
                vo.setDescription(description);
                //
                // Set QA Override Flag
                //
                vo.setHasQaOverride(Boolean.FALSE);
                try {
                    sampleQaManager = SampleQaEventManager.fetchBySampleId(vo.getSampleId());
                    if (sampleQaManager != null && sampleQaManager.hasResultOverrideQA())
                        vo.setHasQaOverride(Boolean.TRUE);
                } catch (NotFoundException ignE) {
                    // ignoring NotFoundException since you cannot override results
                    // without a QAEVent
                }
                try {
                    analysisQaManager = AnalysisQaEventManager.fetchByAnalysisId(vo.getAnalysisId());
                    if (analysisQaManager != null && analysisQaManager.hasResultOverrideQA())
                        vo.setHasQaOverride(Boolean.TRUE);
                } catch (NotFoundException ignE) {
                    // ignoring NotFoundException since you cannot override results
                    // without a QAEVent
                }
                //
                // Compute and set the number of days until the analysis is 
                // due to be completed based on when the sample was received,
                // what the tests average turnaround time is, and whether the
                // client requested a priority number of days.
                //
                if (vo.getPriority() != null)
                    vo.setDueDays(computeDueDays(vo.getReceivedDate(), vo.getPriority()));
                else
                    vo.setDueDays(computeDueDays(vo.getReceivedDate(), vo.getTimeTaAverage()));
                
                //
                // Compute and set the expiration date on the analysis based
                // on the collection date and the tests definition of holding
                // hours.
                //
                vo.setExpireDate(computeExpireDate(vo.getCollectionDate(), vo.getCollectionTime(), vo.getTimeHolding()));
            }
        }

        return (ArrayList<WorksheetCreationVO>)list;
    }
    
    /*
     * Compute the number of days before the analysis is expected to be finshed
     */
    private Integer computeDueDays(Datetime received, Integer expectedDays) {
        long     due;
        Datetime now, expectedDate;
        
        if (received == null || expectedDays == null)
            return null;
        
        now = Datetime.getInstance(Datetime.YEAR, Datetime.MINUTE);
        
        expectedDate = received.add(expectedDays);
        
        due = expectedDate.getDate().getTime() - now.getDate().getTime();
        
        // convert from milliseconds to days
        due = due / 1000 / 60 / 60 / 24;
        
        return (int)due;
    }
    
    /*
     * Compute the Datetime after which the sample is no longer viable for analysis
     */
    private Datetime computeExpireDate(Datetime collectionDate, Datetime collectionTime, int holdingHours) {
        Calendar tempCal;
        Datetime expireDate;
        
        tempCal    = Calendar.getInstance();
        expireDate = null;
        if (collectionDate != null) {
            tempCal.setTime(collectionDate.getDate());
            if (collectionTime != null) {
                tempCal.set(Calendar.HOUR_OF_DAY, collectionTime.get(Datetime.HOUR));
                tempCal.set(Calendar.MINUTE, collectionTime.get(Datetime.MINUTE));
            }
            tempCal.add(Calendar.HOUR_OF_DAY, holdingHours);
            expireDate = new Datetime(Datetime.YEAR, Datetime.MINUTE, tempCal.getTime());
        }

        return expireDate;
    }

    @TransactionTimeout(600)
    public ArrayList<IdNameVO> getColumnNames(Integer formatId) throws Exception {
        int                 i;
        AreaReference       aref;
        ArrayList<IdNameVO> columnNames;
        CellReference       cref[];
        DictionaryViewDO    formatVDO;
        FileInputStream     in;
        HSSFName            name;
        HSSFWorkbook        wb;

        columnNames = new ArrayList<IdNameVO>();
        
        try {
            formatVDO = dictionary.fetchById(formatId);
        } catch (NotFoundException nfE) {
            formatVDO = new DictionaryViewDO();
            formatVDO.setEntry("DefaultTotal");
        } catch (Exception anyE) {
            throw new Exception("Error retrieving worksheet format: "+anyE.getMessage());
        }

        try {
            in = new FileInputStream(getWorksheetTemplateFileName(formatVDO));
        } catch (FileNotFoundException fnfE) {
            throw new Exception("Error loading template file: "+fnfE.getMessage());
        }
        
        try {
            wb = new HSSFWorkbook(in, true);
        } catch (IOException ioE) {
            throw new Exception("Error loading workbook from template file: "+ioE.getMessage());
        }

        for (i = 0; i < wb.getNumberOfNames(); i++) {
            name = wb.getNameAt(i);
            if (name.getRefersToFormula() != null) {
                aref = new AreaReference(name.getRefersToFormula());
                cref = aref.getAllReferencedCells();
                columnNames.add(new IdNameVO(new Integer(Short.valueOf(cref[0].getCol()).intValue()), name.getNameName()));
            }
        }
        
        return columnNames;
    }
    
    private String getWorksheetTemplateFileName(DictionaryViewDO formatVDO) throws Exception {
        ArrayList<SystemVariableDO> sysVars;
        String                      dirName;
        
        dirName = "";
        try {
            sysVars = systemVariable.fetchByName("worksheet_template_directory", 1);
            if (sysVars.size() > 0)
                dirName = ((SystemVariableDO)sysVars.get(0)).getValue();
        } catch (Exception anyE) {
            throw new Exception("Error retrieving temp directory variable: "+anyE.getMessage());
        }

        return dirName+"OEWorksheet"+formatVDO.getEntry()+".xls";
    }
}