package org.openelis.bean;

import java.util.ArrayList;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.IdAccessionVO;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.manager.SampleManager;
import org.openelis.remote.SampleRemote;
import org.openelis.remote.SampleTrackingRemote;

@Stateless

@SecurityDomain("openelis")
@RolesAllowed("sample-select")
public class SampleTrackingBean implements SampleTrackingRemote {

	@PersistenceContext(name = "openelis")
	private EntityManager manager;
	
	@EJB 
	SampleRemote sb;

	@SuppressWarnings("unchecked")
	public ArrayList<SampleManager> query(ArrayList<QueryData> fields, int first, int max) throws Exception {
		ArrayList<SampleManager> managers;
		ArrayList<IdAccessionVO> list;

		list = sb.query(fields, first, max);
		managers = new ArrayList<SampleManager>(max);
		
		for(int i = first; i < list.size(); i++) {
			managers.add(SampleManager.fetchWithItemsAnalyses(list.get(i).getId()));
		}

		return managers;
	}

}
