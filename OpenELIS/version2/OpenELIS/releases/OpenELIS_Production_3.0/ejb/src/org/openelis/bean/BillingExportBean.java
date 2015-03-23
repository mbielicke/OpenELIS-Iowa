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

import static org.openelis.manager.SampleManager1Accessor.getAnalyses;
import static org.openelis.manager.SampleManager1Accessor.getAnalysisQAs;
import static org.openelis.manager.SampleManager1Accessor.getAuxiliary;
import static org.openelis.manager.SampleManager1Accessor.getOrganizations;
import static org.openelis.manager.SampleManager1Accessor.getProjects;
import static org.openelis.manager.SampleManager1Accessor.getResults;
import static org.openelis.manager.SampleManager1Accessor.getSample;
import static org.openelis.manager.SampleManager1Accessor.getSampleClinical;
import static org.openelis.manager.SampleManager1Accessor.getSampleEnvironmental;
import static org.openelis.manager.SampleManager1Accessor.getSampleNeonatal;
import static org.openelis.manager.SampleManager1Accessor.getSamplePrivateWell;
import static org.openelis.manager.SampleManager1Accessor.getSampleQAs;
import static org.openelis.manager.SampleManager1Accessor.getSampleSDWIS;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.ejb3.annotation.TransactionTimeout;
import org.jboss.security.annotation.SecurityDomain;
import org.openelis.constants.Messages;
import org.openelis.domain.AnalysisQaEventDO;
import org.openelis.domain.AnalysisReportFlagsDO;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.AuxDataViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.PatientDO;
import org.openelis.domain.ProviderDO;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.SampleOrganizationViewDO;
import org.openelis.domain.SampleProjectViewDO;
import org.openelis.domain.SampleQaEventViewDO;
import org.openelis.domain.SystemVariableDO;
import org.openelis.manager.SampleManager1;
import org.openelis.meta.SampleMeta;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.DatabaseException;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;
import org.openelis.utils.ReportUtil;

@Stateless
@SecurityDomain("openelis")
public class BillingExportBean {

    @PersistenceContext(unitName = "openelis")
    private EntityManager           manager;

    @EJB
    private SystemVariableBean      systemVariable;

    @EJB
    private SampleManager1Bean      sampleManager;

    @EJB
    private AnalyteBean             analyte;

    @EJB
    private AnalysisReportFlagsBean analysisReportFlags;
    
    @EJB
    private DictionaryCacheBean     dictionary;
    
    @EJB
    private ProviderBean            provider;

    @EJB
    private LockBean                lock;

    private static final Logger     log   = Logger.getLogger("openelis");

    /**
     * This method is called from a cron job to create and export a billing
     * file. The export file is placed into directory specified by
     * 'billing_directory' system variable. The report tracks the last time it was
     * run and only scans for sample that has been released since its last run.
     */
    @Asynchronous
    @TransactionTimeout(600)
    public void export() throws Exception {
        Date lastRunDate, currentRunDate, now;
        SystemVariableDO lastRun;
        Calendar cal;
        Query  query;
        ArrayList<SampleManager1> sms;
        SimpleDateFormat df;
        OutputStream out;
        Path tempFile;

        /*
         * System variable used by billing export
         */
        df = new SimpleDateFormat(Messages.get().dateTimePattern());
        lastRun = null;
        try {
            lastRun = systemVariable.fetchForUpdateByName("last_billing_report_run");
            lastRunDate = df.parse(lastRun.getValue());
        } catch (Exception e) {
            log.log(Level.SEVERE,
                    "System variable 'last_billing_report_run' is not available or valid",
                    e);
            if (lastRun != null)
                systemVariable.abortUpdate(lastRun.getId());
            return;
        }

        /*
         * this is the time at which the current run is being executed and it
         * will be used as the last run time for the next run
         */
        cal = Calendar.getInstance();
        now = cal.getTime();
        cal.add(Calendar.MINUTE, -1);
        currentRunDate = cal.getTime();

        if (currentRunDate.before(lastRunDate)) {
            log.severe("System variable 'last_billing_report_run' > now");
            systemVariable.abortUpdate(lastRun.getId());
            return;
        }

        /*
         * query to fetch all the samples between last run and now
         */
        query = new Query();
        query.setFields(new QueryData(SampleMeta.getReleasedDate(),
                                      QueryData.Type.DATE,
                                      df.format(lastRunDate) + ".." +
                                      df.format(currentRunDate)));
        sms = sampleManager.fetchByQuery(query.getFields(),
                                         0,
                                         100000,
                                         SampleManager1.Load.ORGANIZATION,
                                         SampleManager1.Load.PROJECT,
                                         SampleManager1.Load.QA,
                                         SampleManager1.Load.AUXDATA,
                                         SampleManager1.Load.RESULT);

        log.fine("Considering " + sms.size() + " cases to run");
        if (sms.size() == 0) {
            systemVariable.abortUpdate(lastRun.getId());
            return;
        }

        out = null;
        tempFile = null;
        try {
            tempFile = ReportUtil.createTempFile("billingReport", ".txt", "billing_directory");
            out = Files.newOutputStream(tempFile);
            outputBilling(out, currentRunDate, sms);

            lastRun.setValue(df.format(now));
            systemVariable.update(lastRun);
        } catch (Exception e) {
            if (out != null) {
                out.close();
                out = null;
            }
            if (tempFile != null)
                Files.delete(tempFile);
            
            log.log(Level.SEVERE, "Could not generate billing report", e);

            //
            // we need to roll back the entire transaction
            //
            systemVariable.abortUpdate(lastRun.getId());
            throw new DatabaseException(e);
        } finally {
            if (out != null)
                out.close();
        }
    }

    /**
     * The method exports billing records for each sample and analysis. All analysis
     * have to reportable to be billed (with exception of two billing test). The routine
     * tracks previously sent billing information and issues credit records if the
     * analysis such as # of analytes, QAevents, etc. is changed. 
     */
    private void outputBilling(OutputStream out, Date currentRunDate,
                               ArrayList<SampleManager1> sms) throws Exception {
        int i;
        HDR hdr;
        PAT pat;
        INS ins;
        DTR charge, credit;
        boolean billSample, outputHeader, cancelled, providerOverride;
        Integer BILLING_PO_GROUPID, BILLING_INS_GROUPID, SECTION_ANALYTE_ID, MALE_ID,
                FEMALE_ID, TYPE_PRELIM_ID, TYPE_DILUTION_ID;
        Datetime currentDate;
        DictionaryDO dictDO;
        PatientDO patientDO;
        ProviderDO providerDO;
        AnalysisReportFlagsDO flags;
        ArrayList<Integer> ids, billingTestIds;
        ArrayList<ResultViewDO> results;
        ArrayList<AnalysisViewDO> analyses;
        HashMap<Integer, AnalysisReportFlagsDO> flagsMap;
        HashMap<Integer, ArrayList<ResultViewDO>> resultMap;
        HashMap<Integer, ArrayList<AnalysisViewDO>> testMap;
        HashSet<Integer> override;
        
        currentDate = Datetime.getInstance(Datetime.YEAR, Datetime.MINUTE, currentRunDate);
                                    
        billingTestIds = new ArrayList<Integer>();
        try {
            BILLING_PO_GROUPID = Integer.valueOf(systemVariable.fetchByName("billing_po_auxgroup_id")
                                                               .getValue());
        } catch (Exception anyE) {
            BILLING_PO_GROUPID = -1;
            log.warning("System variable 'billing_po_auxgroup_id' is not available");
        }
        try {
            BILLING_INS_GROUPID = Integer.valueOf(systemVariable.fetchByName("billing_ins_auxgroup_id")
                                                                .getValue());
        } catch (Exception anyE) {
            BILLING_INS_GROUPID = -1;
            log.warning("System variable 'billing_ins_auxgroup_id' is not available");
        }
        try {
            for (String btid : systemVariable.fetchByName("billing_test_ids").getValue().split(","))
                billingTestIds.add(Integer.valueOf(btid));
        } catch (Exception anyE) {
            log.warning("System variable 'billing_test_ids' is not available");
        }
        try {
            SECTION_ANALYTE_ID = analyte.fetchByExternalId("section", 1).get(0).getId();
        } catch (Exception anyE) {
            log.warning("Analyte with external id 'section' not found");
            SECTION_ANALYTE_ID = -1;
        }
        try {
            MALE_ID = dictionary.getIdBySystemName("gender_male");
        } catch (Exception anyE) {
            log.warning("Dictionary entry for 'gender_male' not found");
            MALE_ID = -1;
        }
        try {
            FEMALE_ID = dictionary.getIdBySystemName("gender_female");
        } catch (Exception anyE) {
            log.warning("Dictionary entry for 'gender_female' not found");
            FEMALE_ID = -1;
        }
        try {
            TYPE_PRELIM_ID = dictionary.getIdBySystemName("analysis_type_prelim");
        } catch (Exception anyE) {
            log.warning("Dictionary entry for 'analysis_type_prelim' not found");
            TYPE_PRELIM_ID = -1;
        }
        try {
            TYPE_DILUTION_ID = dictionary.getIdBySystemName("analysis_type_dilution");
        } catch (Exception anyE) {
            log.warning("Dictionary entry for 'analysis_type_dilution' not found");
            TYPE_DILUTION_ID = -1;
        }
        
        /*
         * setup all the transaction records;
         */
        hdr = new HDR();
        pat = new PAT(hdr);
        ins = new INS(hdr);
        charge = new DTR(hdr);
        credit = new DTR(hdr);
        
        hdr.messaged = currentDate.getDate();

        /*
         * export each billable and update the analysis flags  
         */
        for (SampleManager1 sm : sms) {
            outputHeader = true;
            hdr.clear();
            pat.clear();
            ins.clear();

            /*
             * Exclude PT domain samples from billing as the billing type will
             * not be accurate for these samples
             */
            if (Constants.domain().PT.equals(sm.getSample().getDomain()))
                continue;
            
            /*
             * header information
             */
            hdr.accession = getSample(sm).getAccessionNumber();
            if (getSample(sm).getCollectionDate() != null)
                hdr.service = DataBaseUtil.toDate(getSample(sm).getCollectionDate());
            else if (getSample(sm).getReceivedDate() != null)
                hdr.service = DataBaseUtil.toDate(getSample(sm).getReceivedDate());
            else
                hdr.service = DataBaseUtil.toDate(getSample(sm).getEnteredDate());
            hdr.ref = getSample(sm).getClientReference();

            /*
             * get bill-to or default to report-to organization
             */
            if (getSamplePrivateWell(sm) != null) {
                hdr.organizationId = getSamplePrivateWell(sm).getOrganizationId();
                hdr.organizationName = getSamplePrivateWell(sm).getOrganization().getName();
                hdr.streetAddress = getSamplePrivateWell(sm).getOrganization().getAddress().getStreetAddress();
                hdr.multipleUnit = getSamplePrivateWell(sm).getOrganization().getAddress().getMultipleUnit();
                hdr.city = getSamplePrivateWell(sm).getOrganization().getAddress().getCity();
                hdr.state = getSamplePrivateWell(sm).getOrganization().getAddress().getState();
                hdr.zipCode = getSamplePrivateWell(sm).getOrganization().getAddress().getZipCode();
                hdr.owner = getSamplePrivateWell(sm).getOwner();
                hdr.collector = getSamplePrivateWell(sm).getCollector();
                hdr.location = getSamplePrivateWell(sm).getLocation();
            } else {
                if (getSampleEnvironmental(sm) != null) {
                    hdr.collector = getSampleEnvironmental(sm).getCollector();
                    hdr.location = getSampleEnvironmental(sm).getLocation();
                } else if (getSampleSDWIS(sm) != null) {
                    hdr.collector = getSampleSDWIS(sm).getCollector();
                    hdr.location = getSampleSDWIS(sm).getLocation();
                    hdr.pws = getSampleSDWIS(sm).getPwsNumber0();
                }
                
                for (SampleOrganizationViewDO o : getOrganizations(sm)) {
                    /*
                     * use report-to if no bill-to
                     */
                    if (Constants.dictionary().ORG_BILL_TO.equals(o.getTypeId()) ||
                        Constants.dictionary().ORG_REPORT_TO.equals(o.getTypeId())) {
                        hdr.organizationId = o.getOrganizationId();
                        hdr.organizationName = o.getOrganizationName();
                        hdr.streetAddress = o.getOrganizationStreetAddress();
                        hdr.multipleUnit = o.getOrganizationMultipleUnit();
                        hdr.city = o.getOrganizationCity();
                        hdr.state = o.getOrganizationState();
                        hdr.zipCode = o.getOrganizationZipCode();
                        if (Constants.dictionary().ORG_BILL_TO.equals(o.getTypeId()))
                            break;
                    }
                }
            }                

            /*
             * Project/contract
             */
            if (getProjects(sm) != null) {
                for (SampleProjectViewDO p : getProjects(sm)) {
                    if ("Y".equals(p.getIsPermanent())) {
                        hdr.projectName = p.getProjectName();
                        break;
                    }
                }
            }

            /*
             * parse aux data for insurance, po, ...
             */
            providerDO = null;
            providerOverride = false;
            if (getAuxiliary(sm) != null) {
                for (AuxDataViewDO aux : getAuxiliary(sm)) {
                    if (aux.getAuxFieldGroupId().equals(BILLING_INS_GROUPID) && aux.getAnalyteExternalId() != null) {
                        ins.isValid = true;
                        switch (aux.getAnalyteExternalId()) {
                            case "ins_company":
                                ins.company = aux.getDictionary();
                                break;
                            case "ins_plan_number":
                                ins.planNumber = aux.getValue();
                                break;
                            case "ins_last_name":
                                ins.last = aux.getValue();
                                break;
                            case "ins_first_name":
                                ins.first = aux.getValue();
                                break;
                            case "ins_relationship":
                                ins.relationShip = aux.getDictionary();
                                break;
                            case "clinical_diagnosis":
                                pat.diagnosisCode = aux.getValue();
                                break;
                            case "medipass_npi_number":
                                //
                                // for mediapass insurance, we need patient manager and not
                                // the provider that ordered the test. Override provider by
                                // using the NPI key in the auxdata
                                //
                                if (aux.getValue() != null)
                                    try {
                                        providerOverride = true;
                                        providerDO = provider.fetchByNpi(aux.getValue(), 1).get(0);
                                    } catch (Exception anyE) {
                                        providerDO = null;
                                    }
                        }
                    }
                    if (aux.getAuxFieldGroupId().equals(BILLING_PO_GROUPID) && "purchase_order".equals(aux.getAnalyteExternalId()))
                        hdr.po = aux.getValue();
                }
            }
            
            /*
             * check for clinical/newborn
             */
            patientDO = null;
            if (getSampleClinical(sm) != null) { 
                patientDO = getSampleClinical(sm).getPatient();
                if (! providerOverride)
                    providerDO = getSampleClinical(sm).getProvider();
            } else if (getSampleNeonatal(sm) != null) {
                patientDO = getSampleNeonatal(sm).getPatient();
                if (! providerOverride)
                    providerDO = getSampleNeonatal(sm).getProvider();
            }
            if (patientDO != null) {
                pat.isValid = true;
                pat.patientId = patientDO.getId();
                pat.last = patientDO.getLastName();
                pat.first = patientDO.getFirstName();
                pat.birthDate = DataBaseUtil.toDate(patientDO.getBirthDate());
                if (DataBaseUtil.isSame(patientDO.getGenderId(), MALE_ID))
                    pat.gender = "M";
                else if (DataBaseUtil.isSame(patientDO.getGenderId(), FEMALE_ID))
                    pat.gender = "F";
                pat.streetAddress = patientDO.getAddress().getStreetAddress();
                pat.multipleUnit = patientDO.getAddress().getMultipleUnit();
                pat.city = patientDO.getAddress().getCity();
                pat.state = patientDO.getAddress().getState();
                pat.zipCode = patientDO.getAddress().getZipCode();
                pat.phone = patientDO.getAddress().getHomePhone();

                /*
                 * insurance related info
                 */
                ins.patientId = pat.patientId;
                if (ins.last == null) {
                    ins.last = pat.last;
                    ins.first = pat.first;
                }
                if (providerDO != null) {
                    ins.providerId = providerDO.getId();
                    ins.providerNpi = providerDO.getNpi();
                    ins.providerLast = providerDO.getLastName();
                    ins.providerFirst = providerDO.getFirstName();
                }
            }

            /*
             * billing type
             */
            if (ins.isValid)
                hdr.billingType = "PI";
            else if (pat.isValid)
                hdr.billingType = "CL";
            else
                hdr.billingType = "R";
            
            /*
             * is sample marked not billable
             */
            billSample = true;
            if (getSampleQAs(sm) != null) {
                for (SampleQaEventViewDO qa : getSampleQAs(sm)) {
                    if ("N".equals(qa.getIsBillable())) {
                        billSample = false;
                        break;
                    }
                }
            }

            /* 
             * the charge data contains the number of analytes that are reported
             * because some tests are charged based on analyte count. 
             * So create a specialized analysis to result mapping for counting and 
             * price setting; we only need 1) row analytes, 2) reportable analytes 
             * unless it is section analyte (used by billing misc/rush test)
             */
            resultMap = new HashMap<>();
            for (ResultViewDO r : getResults(sm)) {
                if ("Y".equals(r.getIsColumn()) || ("N".equals(r.getIsReportable()) &&
                    !r.getAnalyteId().equals(SECTION_ANALYTE_ID)))
                    continue;
                results = resultMap.get(r.getAnalysisId());
                if (results == null) {
                    results = new ArrayList<>();
                    resultMap.put(r.getAnalysisId(),  results);
                }
                results.add(r);
            }
            
            /*
             * dilution analyses contain a subset of analytes that were diluted. These
             * analyses need to add their billedAnalytes to the "parent" non-diluted
             * analyses. Create a map of test to analysis mappings and change the resultMap
             * to combine the result of diluted analyes to non-diluted analyses
             */
            testMap = new HashMap<>();
            for (AnalysisViewDO a : getAnalyses(sm)) {
                analyses = testMap.get(a.getTestId());
                if (analyses == null) {
                    analyses = new ArrayList<>();
                    testMap.put(a.getTestId(), analyses);
                    analyses.add(a);
                } else {
                    i = analyses.size()-1;
                    if (DataBaseUtil.isSame(TYPE_DILUTION_ID, a.getTypeId()) && analyses.get(i).getTypeId() == null) {
                        /*
                         * add the current diluted analysis results to previous non-diluted analysis 
                         */
                        results = resultMap.get(a.getId());
                        resultMap.get(analyses.get(i).getId()).addAll(results);
                    } else if (DataBaseUtil.isSame(TYPE_DILUTION_ID, analyses.get(i).getTypeId())) {
                        /*
                         * replace the previous diluted-analysis with current analysis and add the results 
                         */
                        results = resultMap.get(analyses.get(i).getId());
                        resultMap.get(a.getId()).addAll(results);
                        analyses.set(i, a);
                    } else {
                        /*
                         * add the analysis to the list
                         */
                        analyses.add(a);
                    }
                }
            }
            
            /*
             * ReportFlags contain previous billing information for this
             * analysis and is used for comparing billing charges.
             * Since we might update all/some of them, we lookup all of
             * them and lock them
             */
            ids = new ArrayList<>();
            for (AnalysisViewDO a : getAnalyses(sm))
                ids.add(a.getId());
            lock.lock(Constants.table().ANALYSIS_REPORT_FLAGS, ids);

            flagsMap = new HashMap<>();
            for (AnalysisReportFlagsDO f : analysisReportFlags.fetchByAnalysisIds(ids))
                flagsMap.put(f.getAnalysisId(), f);

            /*
             * all the analysis that should not be charged
             */
            override = new HashSet<>();
            if (getAnalysisQAs(sm) != null) {
                for (AnalysisQaEventDO qa : getAnalysisQAs(sm))
                    if ("N".equals(qa.getIsBillable()))
                        override.add(qa.getAnalysisId());
            }
            
            /*
             * bill each test
             */
            for (AnalysisViewDO a : getAnalyses(sm)) {
                charge.clear();
                credit.clear();

                charge.testId = a.getTestId();
                charge.testMethodDescription = a.getTestName() + ", "+ a.getMethodName();
                charge.type = "CH";
                charge.billedAnalytes = resultMap.get(a.getId()) != null ? resultMap.get(a.getId()).size() : 0; 
                charge.labSection = a.getSectionName();

                /*
                 * compute price override
                 */
                if (! billSample || override.contains(a.getId()))
                    charge.billedOverride = 0.0;

                /*
                 * charge reportable + non-prelimnary + not-cancelled + in-combined-results
                 */
                cancelled = Constants.dictionary().ANALYSIS_CANCELLED.equals(a.getStatusId());
                charge.isValid = "Y".equals(a.getIsReportable()) && !TYPE_PRELIM_ID.equals(a.getTypeId())
                                && !cancelled && testMap.get(a.getTestId()).contains(a); 

                /*
                 * billing-misc-test (BILLING_MISC_ID) and billing-rush-test (BILLING_RUSH_ID)
                 * contain price information and are billed regardless of QA override or analysis
                 * reportable flag
                 */
                if (!cancelled && billingTestIds.contains(a.getTestId())) {
                    charge.billedOverride = 0.0;
                    charge.isValid = true;
                    if (resultMap.get(a.getId()) != null) {
                        for (ResultViewDO r : resultMap.get(a.getId())) {
                            if (r.getAnalyteId().equals(SECTION_ANALYTE_ID) && r.getValue() != null) {
                                try {
                                    dictDO = dictionary.getById(Integer.valueOf(r.getValue()));
                                    charge.labSection = dictDO.getEntry();
                                } catch (Exception anyE) {
                                    // log the fact that there was a invalid section
                                    // and continue as if no section was chosen
                                    log.severe("Accession #" + hdr.accession +
                                               " has an invalid section for miscellaneous or rush billing");
                                }
                            } else {
                                try {
                                    charge.billedOverride += Double.parseDouble(r.getValue());
                                } catch (NumberFormatException numE) {
                                    log.severe("Accession #" + hdr.accession +
                                               " has an invalid price for miscellaneous or rush billing");
                                    charge.billedOverride = -9.99;
                                    break;
                                }                         
                            }
                        }
                    }
                }

                /*
                 * skip if no change
                 */
                flags = flagsMap.get(a.getId());
                if (DataBaseUtil.isSame(flags.getBilledAnalytes(), charge.billedAnalytes) &&
                    DataBaseUtil.isSame(flags.getBilledOverride(), charge.billedOverride) &&
                    !TYPE_PRELIM_ID.equals(a.getTypeId()) && !cancelled)
                    continue;

                /*
                 * if billed before, credit
                 */
                if (flags.getBilledDate() != null) {
                    credit.testId = charge.testId;
                    credit.testMethodDescription = charge.testMethodDescription;
                    credit.type = "CR";
                    credit.billedAnalytes = flags.getBilledAnalytes();
                    credit.billedOverride = flags.getBilledOverride();
                    credit.labSection = charge.labSection;
                    /*
                     * do not send credit if price-override was $0 (no charge)
                     */
                    credit.isValid = credit.billedOverride == null || credit.billedOverride != 0.0;
                }

                /*
                 * output; order is important
                 */
                if (credit.isValid || charge.isValid) {
                    if (outputHeader) {
                        outputHeader = false;
                        out.write(hdr.toString().getBytes());
                        out.write(pat.toString().getBytes());
                        out.write(ins.toString().getBytes());
                    }
                    out.write(credit.toString().getBytes());
                    out.write(charge.toString().getBytes());

                    /*
                     * update
                     */
                    flags.setBilledDate(charge.isValid ? currentDate : null);
                    flags.setBilledAnalytes(charge.billedAnalytes);
                    flags.setBilledOverride(charge.billedOverride);
                    analysisReportFlags.update(flags);
                }
            }
            
            lock.unlock(Constants.table().ANALYSIS_REPORT_FLAGS, ids);
        }
    }

    /**
     * Header record -- one for each sample 
     */
    class HDR {
        public Date messaged, service;
        public Integer accession, organizationId; 
        public String billingType, organizationName, streetAddress, 
               multipleUnit, city, state, zipCode, ref, owner, collector,
               location, pws, projectName, po;
        StringBuilder sb;
        SimpleDateFormat df0, df1;
        
        public HDR() {
            sb = new StringBuilder();
            df0 = new SimpleDateFormat(Messages.get().dateTimeCompressedPattern());
            df1 = new SimpleDateFormat(Messages.get().dateCompressedPattern());
        }

        public void clear() {
            service = null;
            accession = null;
            organizationId = null;
            billingType = null;
            organizationName = null;
            streetAddress = null;
            multipleUnit = null;
            city = null;
            state = null;
            zipCode = null;
            ref = null;
            owner = null;
            collector = null;
            location = null;
            pws = null;
            projectName = null;
            po = null;
        }

        public String toString() {
            sb.setLength(0);

            sb.append("HDR").append("|")
              .append(df0.format(messaged)).append("|")
              .append(accession).append("|")
              .append(billingType).append("|")
              .append(df1.format(service)).append("|")
              .append(organizationId).append("|")
              .append("|")
              .append("|")
              .append(organizationName).append("|");
            if (multipleUnit != null && multipleUnit.length() > 0) // always fill first address line
                sb.append(multipleUnit).append("|")
                  .append(DataBaseUtil.toString(streetAddress)).append("|");
            else
                sb.append(DataBaseUtil.toString(streetAddress)).append("|")
                  .append("|");
            sb.append(DataBaseUtil.toString(city)).append("|")
              .append(DataBaseUtil.toString(state)).append("|")
              .append(DataBaseUtil.toString(zipCode)).append("|");
            if (po != null)
                sb.append("PO-").append(po).append(", ");
            if (pws != null)
                sb.append("PWS-").append(pws).append(", ");
            if (ref != null)
                sb.append("REF-").append(ref).append(", ");
            if (owner != null)
                sb.append("OWNER-").append(owner).append(", ");
            if (collector != null)
                sb.append("COL-").append(collector).append(", ");
            if (location != null)
                sb.append("LOC-").append(location);
            if (sb.substring(sb.length()-2).equals(", "))
                sb.setLength(sb.length()-2);
            sb.append("|")
              .append("|")
              .append(DataBaseUtil.toString(projectName).toUpperCase()).append("|")
              .append("\r\n");
            
            return sb.toString();
        }
    }
    
    /**
     * Patient record -- used for clinical and newborn
     */
    class PAT {
        public HDR hdr; 
        public boolean isValid;
        public Date birthDate;
        public Integer patientId;
        public String last, first, gender, streetAddress, 
                      multipleUnit, city, state, zipCode, phone,
                      diagnosisCode;
        StringBuilder sb;
        SimpleDateFormat df;
        
        public PAT(HDR hdr) {
            this.hdr = hdr;
            sb = new StringBuilder();
            df = new SimpleDateFormat(Messages.get().datePattern());
        }

        public void clear() {
            isValid = false;
            birthDate = null;
            patientId = null;
            last = null;
            first = null;
            gender = null;
            streetAddress = null;
            multipleUnit = null;
            city = null;
            state = null;
            zipCode = null;
            phone = null;
            diagnosisCode = null;
        }
        
        public String toString() {
            sb.setLength(0);

            if (isValid) {
                sb.append("PAT").append("|")
                  .append(hdr.df0.format(hdr.messaged)).append("|")
                  .append(hdr.accession).append("|")
                  .append(patientId).append("|")
                  .append(DataBaseUtil.toString(last)).append("|")
                  .append(DataBaseUtil.toString(first)).append("|")
                  .append("|");
                if (birthDate != null)
                    sb.append(df.format(birthDate));
                sb.append("|")
                  .append(DataBaseUtil.toString(gender)).append("|")
                  .append(DataBaseUtil.toString(multipleUnit)).append("|")
                  .append(DataBaseUtil.toString(streetAddress)).append("|")
                  .append(DataBaseUtil.toString(city)).append("|")
                  .append(DataBaseUtil.toString(state)).append("|")
                  .append(DataBaseUtil.toString(zipCode)).append("|")
                  .append(DataBaseUtil.toString(phone)).append("|")
                  .append(DataBaseUtil.toString(diagnosisCode)).append("|")
                  .append("\r\n");
            }

            return sb.toString();
        }
    }
    
    /**
     * Insurance record -- used for clinical and newborn insurance/Medicare/Medicaid
     */
    class INS {
        public HDR hdr; 
        public boolean isValid;
        public Integer patientId, providerId;
        public String company, planNumber, first, last, relationShip,
               providerNpi, providerFirst, providerLast;
        StringBuilder sb;

        public INS(HDR hdr) {
            this.hdr = hdr;
            sb = new StringBuilder();
        }
        
        public void clear() {
            isValid = false;
            patientId = null;
            company = null;
            planNumber = null;
            first = null;
            last = null;
            relationShip = null;
            providerId = null;
            providerNpi = null;
            providerFirst = null;
            providerLast = null;
        }

        public String toString() {
            sb.setLength(0);

            if (isValid) {
                sb.append("INS").append("|")
                  .append(hdr.df0.format(hdr.messaged)).append("|")
                  .append(hdr.accession).append("|")
                  .append(patientId).append("|")
                  .append(DataBaseUtil.toString(company)).append("|")
                  .append(DataBaseUtil.toString(planNumber)).append("|")
                  .append(DataBaseUtil.toString(first)).append("|")
                  .append(DataBaseUtil.toString(last)).append("|")
                  .append(DataBaseUtil.toString(relationShip)).append("|")
                  .append(DataBaseUtil.toString(providerId)).append("|")
                  .append(DataBaseUtil.toString(providerNpi)).append("|")
                  .append(DataBaseUtil.toString(providerLast)).append("|")
                  .append(DataBaseUtil.toString(providerFirst)).append("|")
                  .append("\r\n");
            }

            return sb.toString();
        }
    }
    
    /**
     * Transaction record -- one for each analysis charged and/or refunded
     */
    class DTR {
        public HDR hdr; 
        public boolean isValid;
        public Integer testId, billedAnalytes;
        public Double billedOverride;
        public String testMethodDescription, type, labLocation, labSection; 
        StringBuilder sb;

        public DTR(HDR hdr) {
            this.hdr = hdr;
            sb = new StringBuilder();
        }
        
        public void clear() {
            isValid = false;
            testId = null;
            billedAnalytes = null;
            billedOverride = null;
            testMethodDescription = null;
            type = null;
            labLocation = null;
            labSection = null;
        }
        
        public String toString() {
            String parts[];
            
            sb.setLength(0);

            if (isValid) {
                if (labSection != null) {
                    parts = labSection.split("-");
                    if (parts.length > 1)
                        labLocation = parts[1];
                    else
                        labLocation = labSection;
                }
                sb.append("DTR").append("|")
                  .append(hdr.df0.format(hdr.messaged)).append("|")
                  .append(hdr.accession).append("|")
                  .append(testId).append("|")
                  .append(testMethodDescription).append("|")
                  .append(type).append("|")
                  .append(billedAnalytes).append("|")
                  .append(DataBaseUtil.toString(billedOverride)).append("|")
                  .append(DataBaseUtil.toString(labLocation).toUpperCase()).append("|")
                  .append(labSection).append("|")
                  .append("\r\n");
            }

            return sb.toString();
        }
    }        
}