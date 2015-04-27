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
package org.openelis.report.chlGCToCDC;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.openelis.bean.DictionaryCacheBean;
import org.openelis.bean.OrganizationParameterBean;
import org.openelis.domain.AddressDO;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.OrganizationParameterDO;
import org.openelis.domain.PatientDO;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.SampleDO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.domain.SampleOrganizationViewDO;
import org.openelis.manager.SampleManager1;
import org.openelis.utils.EJBFactory;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRRewindableDataSource;

public class AnalysisDataSource implements JRRewindableDataSource {

    private int numTotal, numPrint, numUnreleased;
    private ArrayList<HashMap<String, Object>> rows;
    private DictionaryCacheBean dictCache;
    private HashMap<String, Object> currentRow;
    private Iterator<HashMap<String, Object>> iter;
    private OrganizationParameterBean orgParam;
	
	private AnalysisDataSource(ArrayList<SampleManager1> sms, Connection con) throws Exception {
	    boolean sOverride, aOverride;
	    int i, j, k;
	    AddressDO adrDO;
	    AnalysisViewDO aVDO;
        ArrayList<SampleOrganizationViewDO> soVDOs;
        ArrayList<OrganizationParameterDO> opDOs;
	    DictionaryDO dictDO;
	    HashMap<Integer, String> orgTypes;
        HashMap<String, HashMap<String, Integer>> cityCountyMap;
        HashMap<String, Integer> stateMap;
        HashMap<String, Object> row;
        Integer countyNumber;
        PatientDO patDO;
        PreparedStatement ccS;
        ResultSet rs;
        ResultViewDO rVDO;
        SampleDO sDO;
	    SampleItemViewDO siVDO;
	    SampleOrganizationViewDO reportToVDO;
	    String orgType;
	    
        try {
            dictCache = EJBFactory.getDictionaryCache();
            orgParam = EJBFactory.getOrganizationParameter();
            ccS = con.prepareStatement("select county_number from phims@rambaldi_trust:city where name = ? and state = ?");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw e;
        }

        if (sms != null && sms.size() > 0) {
            numTotal = 0;
            numPrint = 0;
            numUnreleased = 0;
            orgTypes = new HashMap<Integer, String>();
            cityCountyMap = new HashMap<String, HashMap<String, Integer>>();
            rows = new ArrayList<HashMap<String, Object>>();
            for (SampleManager1 sm : sms) {
                sDO = sm.getSample();
                sOverride = sm.qaEvent.hasType(Constants.dictionary().QAEVENT_OVERRIDE);
                
                reportToVDO = null;
                soVDOs = sm.organization.getByType(Constants.dictionary().ORG_REPORT_TO);
                if (soVDOs != null && soVDOs.size() == 1)
                    reportToVDO = soVDOs.get(0);
                
                if (reportToVDO == null)
                    throw new Exception("Accession #" + sDO.getAccessionNumber() + "does not have a 'Report To' organization.");
                
                orgType = orgTypes.get(reportToVDO.getOrganizationId());
                if (orgType == null) {
                    opDOs = orgParam.fetchByOrgIdAndDictSystemName(reportToVDO.getOrganizationId(), "org_type");
                    if (opDOs != null && opDOs.size() > 0) {
                        for (OrganizationParameterDO opDO : opDOs) {
                            if ("Family Planning Clinic".equals(opDO.getValue()) ||
                                "STD Clinic".equals(opDO.getValue()) ||
                                "Student Health Services".equals(opDO.getValue()) ||
                                "Correctional Facility".equals(opDO.getValue()) ||
                                "Prenatal Clinic".equals(opDO.getValue()) ||
                                "Indian Health Services".equals(opDO.getValue()) ||
                                "Community Health Center".equals(opDO.getValue()) ||
                                "Other".equals(opDO.getValue())) {
                                orgType = opDO.getValue();
                                orgTypes.put(reportToVDO.getOrganizationId(), orgType);
                                break;
                            }
                        }
                    }
                    if (orgType == null)
                        continue;
                }
                
                for (i = 0; i < sm.item.count(); i++) {
                    siVDO = sm.item.get(i);
                    for (j = 0; j < sm.analysis.count(siVDO); j++) {
                        aVDO = sm.analysis.get(siVDO, j);
                        aOverride = sm.qaEvent.hasType(aVDO, Constants.dictionary().QAEVENT_OVERRIDE);
                        if ("chl-gc cbss".equals(aVDO.getTestName()) && "Y".equals(aVDO.getIsReportable()) &&
                            !Constants.dictionary().ANALYSIS_CANCELLED.equals(aVDO.getStatusId())) {
                            row = new HashMap<String, Object>();
                            row.put("accession_number", sm.getSample().getAccessionNumber());
                            if (sDO.getCollectionDate() != null)
                                row.put("collection_date", sDO.getCollectionDate().getDate());
                            row.put("received_date", sDO.getReceivedDate().getDate());

                            if (sm.getSampleClinical().getPatient() != null) {
                                patDO = sm.getSampleClinical().getPatient();
                                if (patDO.getBirthDate() != null)
                                    row.put("birth_date", patDO.getBirthDate().getDate());
                                if (patDO.getGenderId() != null) {
                                    dictDO = dictCache.getById(patDO.getGenderId());
                                    row.put("gender", dictDO.getCode());
                                }
                                if (patDO.getRaceId() != null) {
                                    dictDO = dictCache.getById(patDO.getRaceId());
                                    row.put("race", dictDO.getCode());
                                }
                                if (patDO.getEthnicityId() != null) {
                                    dictDO = dictCache.getById(patDO.getEthnicityId());
                                    row.put("ethnicity", dictDO.getCode());
                                }
                                adrDO = sm.getSampleClinical().getPatient().getAddress();
                                if (adrDO != null) {
                                    row.put("state", adrDO.getState());
                                    if (adrDO.getCity() != null && adrDO.getState() != null) {
                                        stateMap = cityCountyMap.get(adrDO.getState().toLowerCase());
                                        if (stateMap == null) {
                                            stateMap = new HashMap<String, Integer>();
                                            cityCountyMap.put(adrDO.getState().toLowerCase(), stateMap);
                                        }
                                        countyNumber = stateMap.get(adrDO.getCity().toLowerCase());
                                        if (countyNumber == null) {
                                            ccS.setObject(1, adrDO.getCity().toLowerCase());
                                            ccS.setObject(2, adrDO.getState().toLowerCase());
                                            rs = ccS.executeQuery();
                                            if (rs.next()) {
                                                countyNumber = (Integer)rs.getObject(1);
                                                stateMap.put(adrDO.getCity().toLowerCase(), countyNumber);
                                            }
                                        }
                                        if (countyNumber != null)
                                            row.put("county_number", countyNumber);
                                    }
                                }
                            }
                            
                            row.put("o_id", reportToVDO.getOrganizationId());
                            row.put("zip_code", reportToVDO.getOrganizationZipCode());
                            row.put("org_type", orgType);

                            if (siVDO.getTypeOfSampleId() != null) {
                                dictDO = dictCache.getById(siVDO.getTypeOfSampleId());
                                row.put("source", dictDO.getCode());
                            }
                            
                            if (aVDO.getStatusId() != null) {
                                dictDO = dictCache.getById(aVDO.getStatusId());
                                row.put("status", dictDO.getEntry());
                                if (!Constants.dictionary().ANALYSIS_RELEASED.equals(dictDO.getId()))
                                    numUnreleased++;
                            }
                            if (aVDO.getPrintedDate() != null)
                                row.put("printed_date", aVDO.getPrintedDate().getDate());
                            row.put("method", aVDO.getMethodName().substring(0, 1).toUpperCase());

                            row.put("risk_new", "N");
                            row.put("risk_multiple", "N");
                            row.put("risk_contact", "N");
                            row.put("risk_msm", "N");
                            row.put("risk_none", "N");
                            row.put("sign_cervical", "N");
                            row.put("sign_cervicitis", "N");
                            row.put("sign_pid", "N");
                            row.put("sign_urethritis", "N");
                            row.put("sign_no_exam", "N");
                            row.put("sign_none", "N");
                            for (k = 0; k < sm.result.count(aVDO); k++) {
                                rVDO = sm.result.get(aVDO, k, 0);
                                dictDO = null;
                                if (rVDO.getValue() != null && rVDO.getValue().length() > 0) {
                                    if (Constants.dictionary().TEST_RES_TYPE_DICTIONARY.equals(rVDO.getTypeId())) {
                                        dictDO = dictCache.getById(Integer.valueOf(rVDO.getValue()));
                                        if (dictDO == null)
                                            throw new Exception("Dictionary entry not found for ID "+rVDO.getValue());
                                    }
                                        
                                    if ("visit_reason".equals(rVDO.getAnalyteExternalId())) {
                                        row.put("visit_reason", dictDO.getCode());
                                    } else if ("risk_history".equals(rVDO.getAnalyteExternalId())) {
                                        if (dictDO != null) {
                                            if ("risk_new".equals(dictDO.getSystemName()))
                                                row.put("risk_new", "Y");
                                            else if ("risk_multiple".equals(dictDO.getSystemName()))
                                                row.put("risk_multiple", "Y");
                                            else if ("risk_contact".equals(dictDO.getSystemName()))
                                                row.put("risk_contact", "Y");
                                            else if ("risk_msm".equals(dictDO.getSystemName()))
                                                row.put("risk_msm", "Y");
                                            else if ("none_of_the_above".equals(dictDO.getSystemName()))
                                                row.put("risk_none", "Y");
                                        }
                                    } else if ("symptom".equals(rVDO.getAnalyteExternalId())) {
                                        row.put("symptom", dictDO.getCode());
                                    } else if ("sign".equals(rVDO.getAnalyteExternalId())) {
                                        if (dictDO != null) {
                                            if ("sign_cervical".equals(dictDO.getSystemName()))
                                                row.put("sign_cervical", "Y");
                                            else if ("sign_cervicitis".equals(dictDO.getSystemName()))
                                                row.put("sign_cervicitis", "Y");
                                            else if ("sign_pid".equals(dictDO.getSystemName()))
                                                row.put("sign_pid", "Y");
                                            else if ("sign_urethritis".equals(dictDO.getSystemName()))
                                                row.put("sign_urethritis", "Y");
                                            else if ("sign_no_exam".equals(dictDO.getSystemName()))
                                                row.put("sign_no_exam", "Y");
                                            else if ("none_of_the_above".equals(dictDO.getSystemName()))
                                                row.put("sign_none", "Y");
                                        }
                                    } else if ("insurance_type".equals(rVDO.getAnalyteExternalId())) {
                                        row.put("insurance_type", dictDO.getCode());
                                    } else if ("chl_result".equals(rVDO.getAnalyteExternalId())) {
                                        if (sOverride || aOverride) {
                                            row.put("chl_result", "U");
                                        } else {
                                            if (dictDO.getEntry().startsWith("D"))
                                                row.put("chl_result", "P");
                                            else
                                                row.put("chl_result", dictDO.getEntry().substring(0, 1));
                                        }
                                    } else if ("gc_result".equals(rVDO.getAnalyteExternalId())) {
                                        if (sOverride || aOverride) {
                                            row.put("gc_result", "U");
                                        } else {
                                            if (dictDO.getEntry().startsWith("D"))
                                                row.put("gc_result", "P");
                                            else
                                                row.put("gc_result", dictDO.getEntry().substring(0, 1));
                                        }
                                    }
                                }
                            }
                            rows.add(row);
                            numTotal++;
                            numPrint += 2;
                        }
                    }
                }
            }
            iter = rows.iterator();
        }
	}

	public static AnalysisDataSource getInstance(ArrayList<SampleManager1> sms, Connection con) throws Exception {
		AnalysisDataSource ds;
	
		ds = null;
		try {
			ds = new AnalysisDataSource(sms, con);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		return ds;
	}

    public Object getFieldValue(JRField field) throws JRException {
        return currentRow.get(field.getName());
    }

    public boolean next() throws JRException {
        if (rows != null && iter.hasNext()) {
            currentRow = iter.next();
            return true;
        }
        return false;
    }

    public void moveFirst() throws JRException {
        if (rows != null)
            iter = rows.iterator();
    }

    public String getSummaryMessage() {
        return "Processed = " +  numTotal + " Reported = " + numPrint + " Unreleased = " + numUnreleased;
    }
}