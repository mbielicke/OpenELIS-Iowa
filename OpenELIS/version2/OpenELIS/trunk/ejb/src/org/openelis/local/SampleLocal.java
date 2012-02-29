package org.openelis.local;

import java.util.ArrayList;
import java.util.Date;

import javax.ejb.Local;

import org.openelis.domain.ClientNotificationVO;
import org.openelis.domain.SampleDO;
import org.openelis.domain.SampleStatusWebReportVO;

@Local
public interface SampleLocal {
	public SampleDO fetchById(Integer sampleId) throws Exception;

	public SampleDO fetchByAccessionNumber(Integer accessionNumber) throws Exception;

    public ArrayList<SampleDO> fetchSDWISByReleased(Date startDate, Date endDate) throws Exception;

    public ArrayList<Object[]> fetchForFinalReportBatch() throws Exception;

    public ArrayList<Object[]> fetchForFinalReportBatchReprint(Date beginPrinted, Date endPrinted) throws Exception;

	public ArrayList<Object[]> fetchForFinalReportSingle(Integer sampleId) throws Exception;

	public ArrayList<Object[]> fetchForFinalReportPreview(Integer sampleId) throws Exception;
	
	public ArrayList<ClientNotificationVO> fetchForClientEmailReceivedReport(Date stDate, Date endDate) throws Exception;
    
	public ArrayList<ClientNotificationVO> fetchForClientEmailReleasedReport(Date stDate, Date endDate) throws Exception;
	
    public ArrayList<Object[]> fetchForBillingReport(Date stDate, Date endDate) throws Exception;
    
    public ArrayList<SampleStatusWebReportVO> fetchForSampleStatusReport(ArrayList<Integer> sampleIdList) throws Exception;

	public SampleDO add(SampleDO data) throws Exception;

	public SampleDO update(SampleDO data) throws Exception;
 }
