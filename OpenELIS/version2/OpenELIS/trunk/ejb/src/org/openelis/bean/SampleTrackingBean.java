package org.openelis.bean;

import java.util.ArrayList;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.IdAccessionVO;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.manager.SampleManager;
import org.openelis.remote.SampleRemote;
import org.openelis.remote.SampleTrackingRemote;

@Stateless

@SecurityDomain("openelis")
@RolesAllowed("sampletracking-select")
public class SampleTrackingBean implements SampleTrackingRemote {

	@PersistenceContext(unitName = "openelis")
	private EntityManager manager;
	
	@EJB 
	SampleRemote sb;

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
