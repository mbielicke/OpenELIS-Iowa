package org.openelis.stfu.bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import java.util.logging.Logger;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.bean.LockBean;
import org.openelis.domain.Constants;
import org.openelis.domain.DataObject;
import org.openelis.stfu.domain.CaseContactDO;
import org.openelis.stfu.domain.CaseContactLocationDO;
import org.openelis.stfu.manager.CaseContactManager;
import org.openelis.stfu.manager.CaseContactManagerAccessor;
import org.openelis.ui.common.DatabaseException;

@Stateless
@SecurityDomain("openelis")
public class CaseContactManagerBean {
	
	@Inject
	CaseContactManagerAccessor          accessor;
	
	@EJB
	CaseContactBean                     contactBean;
	
	@EJB
	CaseContactLocationBean             locationBean;
	
	@EJB
	private LockBean                    lock;
	
	private static final Logger         log = Logger.getLogger("openelis");
	
	public CaseContactManager fetchById(Integer id, CaseContactManager.Load... elements) throws Exception {
		ArrayList<Integer> ids;
		ArrayList<CaseContactManager> ccms;
		
		ids = new ArrayList<Integer>(1);
		ids.add(id);
		ccms = fetchByIds(ids,elements);
		return ccms.size() == 0 ? null : ccms.get(0);
	}
	
	public ArrayList<CaseContactManager> fetchByIds(ArrayList<Integer> ids, CaseContactManager.Load... elements) throws Exception {
		CaseContactManager ccm;
		ArrayList<CaseContactManager> ccms;
		EnumSet<CaseContactManager.Load> el;
		HashMap<Integer,CaseContactManager> map;
		
		ccms = new ArrayList<CaseContactManager>();
		if(elements != null && elements.length > 0)
			el = EnumSet.copyOf(Arrays.asList(elements));
		else
			el = EnumSet.noneOf(CaseContactManager.Load.class);
		
		map = new HashMap<Integer, CaseContactManager>();
		
		for(CaseContactDO data : contactBean.fetchByIds(ids)) {
			ccm = new CaseContactManager();
			accessor.setCaseContact(ccm,data);
			ccms.add(ccm);
			
			map.put(data.getId(),ccm);	
		}
		
		if(el.contains(CaseContactManager.Load.LOCATION)) {
			for(CaseContactLocationDO locationDO : locationBean.fetchByIds(ids)) {
				ccm = map.get(locationDO.getId());
				accessor.addLocation(ccm,locationDO);
			}
		}
		
		return ccms;
	}
	
	@RolesAllowed("case-update")
	public CaseContactManager fetchForUpdate(Integer id) throws Exception {
		return fetchForUpdate(id, CaseContactManager.Load.LOCATION);
	}
	
	@RolesAllowed("case-update")
	public CaseContactManager fetchForUpdate(Integer id, CaseContactManager.Load... elements) throws Exception {
		ArrayList<Integer> ids;
		ArrayList<CaseContactManager> ccms;
		
		ids = new ArrayList<Integer>(1);
		ids.add(id);
		ccms = fetchForUpdate(ids,elements);
		return ccms.size() > 0 ? ccms.get(0) : null;
	}
	
	@RolesAllowed("case-update") 
	public ArrayList<CaseContactManager> fetchForUpdate(ArrayList<Integer> ids, CaseContactManager.Load... elements) throws Exception {
		lock.lock(Constants.table().CASE_CONTACT, ids);
		return fetchByIds(ids,elements);
	}
	
	@RolesAllowed({"case-add","case-update"})
	public CaseContactManager update(CaseContactManager ccm, boolean ignoreWarnings) throws Exception {
		ArrayList<CaseContactManager> ccms;
		
		ccms = new ArrayList<CaseContactManager>(1);
		ccms.add(ccm);
		update(ccms,ignoreWarnings);
		return ccms.get(0);
	}
	
	@RolesAllowed({"case-add","case-update"})
	public ArrayList<CaseContactManager> update(ArrayList<CaseContactManager> ccms,boolean ignoreWarnings) throws Exception {
		ArrayList<Integer> locks;
		
		validate(ccms);
		locks = checkLocks(ccms);
		
		for (CaseContactManager ccm : ccms) {
			removeDeleted(ccm);
			persistCaseContact(ccm);
			persistCaseContactLocation(ccm);
		}
		
		if (locks != null)
			lock.unlock(Constants.table().CASE_CONTACT, locks);
		
		return ccms;
	} 
	
	protected void validate(ArrayList<CaseContactManager> ccms) throws Exception {
		
	}
	
	protected ArrayList<Integer> checkLocks(ArrayList<CaseContactManager> ccms) throws Exception {
		return null;
	}
	
	protected void removeDeleted(CaseContactManager ccm) throws Exception {
		ArrayList<DataObject> removed;
		
		removed = accessor.getRemoved(ccm);
		if(removed != null) {
			for(DataObject data : removed) {
				if(data instanceof CaseContactLocationDO)
					locationBean.delete((CaseContactLocationDO)data);
				else
					throw new DatabaseException("ERROR: DataObject passed for removal is of unknown type");
			}
		}
	}
	
	protected void persistCaseContact(CaseContactManager ccm) throws Exception {
		CaseContactDO caseContact;
		
		caseContact = accessor.getCaseContact(ccm);
		
		if(caseContact.getId() == null)
			contactBean.add(caseContact);
		else
			contactBean.update(caseContact);
	}
	
	protected void persistCaseContactLocation(CaseContactManager ccm) throws Exception {
		ArrayList<CaseContactLocationDO> locations;
		
		locations = accessor.getLocations(ccm);
		
		if(locations == null)
			return;
		
		for(CaseContactLocationDO location : locations) {
			if(location.getId() == null)
				locationBean.add(location);
			else
				locationBean.update(location);
		}
		
		
	}

}
