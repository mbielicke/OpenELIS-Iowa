package org.openelis.bean;

import java.util.ArrayList;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.domain.IdAccessionVO;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.manager.SampleManager;

@Stateless
@SecurityDomain("openelis")

public class SampleTrackingBean {

	@EJB 
	private SampleBean sb;

	@SuppressWarnings("unchecked")
	public ArrayList<SampleManager> query(ArrayList<QueryData> fields, int first, int max) throws Exception {
		ArrayList<SampleManager> managers;
		ArrayList<IdAccessionVO> list;

		list = sb.query(fields, first, max);
		managers = new ArrayList<SampleManager>(max);
		
		for(int i = 0; i < list.size(); i++) {
			managers.add(SampleManager.fetchWithItemsAnalyses(list.get(i).getId()));
		}

		return managers;
	}

}
