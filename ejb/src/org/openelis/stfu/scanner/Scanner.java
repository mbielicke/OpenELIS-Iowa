package org.openelis.stfu.scanner;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.openelis.bean.SampleManager1Bean;
import org.openelis.bean.SystemVariableBean;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.SystemVariableDO;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.SampleManager1Accessor;
import org.openelis.meta.SampleMeta;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.data.QueryData;

@Stateless
public class Scanner {
	
	@EJB
	protected SystemVariableBean systemVariable;
	
	@EJB
	protected SampleManager1Bean sampleManagerBean;
	
	@EJB
	protected ProcessorFactory processor;
	

	public void scan() throws Exception {
		
		process(sampleManagerBean.fetchByQuery(createQueryFields(), 0, 1000, SampleManager1.Load.RESULT));
	}
	
	protected Datetime getLastRunDate() throws Exception {
		String date;
	
		date = systemVariable.fetchByName("scanner_last_run").getValue();
		
		return Datetime.getInstance(Datetime.YEAR,Datetime.MINUTE,DateFormat.getDateInstance().parse(date));
	}
	
	protected void updateLastRunDate(Datetime date) throws Exception {
		SystemVariableDO svDO;
		
		svDO = systemVariable.fetchByName("scanner_last_run");
		svDO.setValue(date.toString());
		
		systemVariable.update(svDO);
	}
	
	protected ArrayList<QueryData> createQueryFields() throws Exception {
		ArrayList<QueryData> qds;
		QueryData qd;
		
		qds = new ArrayList<QueryData>();
		
		qd = new QueryData();
		qd.setKey(SampleMeta.getAnalysisReleasedDate());
		qd.setQuery("> "+getLastRunDate().toString());
		qd.setType(QueryData.Type.DATE);
		
		qds.add(qd);
		
		qd = new QueryData();
		qd.setKey(SampleMeta.getAnalysisTestName());
		qd.setQuery("nbs*");
		qd.setType(QueryData.Type.STRING);
		
		qds.add(qd);
		
		return qds;
	}
	
	protected void process(List<SampleManager1> sms) throws Exception {		
		for(SampleManager1 sm : sms) {
			for(AnalysisViewDO analysis : SampleManager1Accessor.getAnalyses(sm)) {
				processor.process(sm, analysis);
			}
		}
	}
	
}
