package org.openelis.local;

import java.util.ArrayList;
import java.util.Date;

import javax.ejb.Local;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.SampleDO;
import org.openelis.domain.SampleStatusWebReportVO;

@Local
public interface SampleLocal {
	public SampleDO fetchById(Integer sampleId) throws Exception;

	public SampleDO fetchByAccessionNumber(Integer accessionNumber) throws Exception;

    public ArrayList<SampleDO> fetchSDWISByReleased(Date startDate, Date endDate) throws Exception;

    public ArrayList<Object[]> fetchSamplesForFinalReportBatch() throws Exception;

    public ArrayList<Object[]> fetchSamplesForFinalReportBatchReprint(Date beginPrinted, Date endPrinted) throws Exception;

	public ArrayList<Object[]> fetchSamplesForFinalReportSingle(Integer sampleId) throws Exception;

	public ArrayList<Object[]> fetchSamplesForFinalReportPreview(Integer sampleId) throws Exception;

	public SampleDO add(SampleDO data) throws Exception;

	public SampleDO update(SampleDO data) throws Exception;
	
	public ArrayList<Object[]> fetchForClientEmailReceivedReport(Date stDate, Date endDate) throws Exception;
    
    public ArrayList<Object[]> fetchForClientEmailReleasedReport(Date stDate, Date endDate) throws Exception;
    
    public ArrayList<Object[]> fetchForBillingReport(Date stDate, Date endDate) throws Exception;
        
    public ArrayList<IdNameVO> fetchProjectsForOrganizations(String clause) throws Exception;   
    
    public ArrayList<SampleStatusWebReportVO> fetchSampleAnalysisInfoForSampleStatusReportEnvironmental(ArrayList<Integer> sampleIdList) throws Exception;
    
    public ArrayList<SampleStatusWebReportVO> fetchSampleAnalysisInfoForSampleStatusReportPrivateWell(ArrayList<Integer> sampleIdList) throws Exception;
    
    public ArrayList<SampleStatusWebReportVO> fetchSampleAnalysisInfoForSampleStatusReportSDWIS(ArrayList<Integer> sampleIdList) throws Exception;
}
