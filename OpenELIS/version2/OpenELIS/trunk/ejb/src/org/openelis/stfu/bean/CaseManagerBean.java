package org.openelis.stfu.bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.bean.LockBean;
import org.openelis.bean.OrganizationBean;
import org.openelis.bean.PatientBean;
import org.openelis.bean.UserCacheBean;
import org.openelis.domain.Constants;
import org.openelis.domain.DataObject;
import org.openelis.domain.OrganizationViewDO;
import org.openelis.domain.PatientDO;
import org.openelis.stfu.domain.CaseAnalysisDO;
import org.openelis.stfu.domain.CaseDO;
import org.openelis.stfu.domain.CasePatientDO;
import org.openelis.stfu.domain.CaseProviderDO;
import org.openelis.stfu.domain.CaseResultDO;
import org.openelis.stfu.domain.CaseTagDO;
import org.openelis.stfu.domain.CaseUserDO;
import org.openelis.stfu.manager.CaseManager;
import org.openelis.stfu.manager.CaseManagerAccessor;
import org.openelis.ui.common.DatabaseException;
import org.openelis.ui.common.Datetime;

@Stateless
@SecurityDomain("openelis")
public class CaseManagerBean {
	
	@Inject
	CaseManagerAccessor                  accessor;
	
	@EJB
	CaseBean                             caseBean;
	
	@EJB
	PatientBean                          patient;
	
	@EJB
	CasePatientBean                      casePatient;
	
	@EJB
	CaseUserBean                         user;
	
	@EJB
	OrganizationBean                     organization;
	
	@EJB
	CaseProviderBean                     provider;
	
	@EJB
	CaseAnalysisBean                     analysisBean;
	
	@EJB
	CaseResultBean                       resultBean;
	
	@EJB
	CaseTagBean                          tagBean;
	
	@EJB
	LockBean                             lock;
	
	@EJB
	UserCacheBean                        userCache;
	
	private static final Logger          log = Logger.getLogger("openelis");

	public CaseManager fetchById(Integer caseId, CaseManager.Load... elements) throws Exception {
		ArrayList<Integer> ids;
		ArrayList<CaseManager> cms;
		
		
		ids = new ArrayList<Integer>(1);
		ids.add(caseId);
		cms = fetchByIds(ids,elements);
		return cms.size() == 0 ? null : cms.get(0);
	}
	
	public ArrayList<CaseManager> fetchByIds(ArrayList<Integer> caseIds, CaseManager.Load... elements) throws Exception {
		CaseManager cm;
		ArrayList<CaseManager> cms;
		EnumSet<CaseManager.Load> el;
		HashMap<Integer, CaseManager> map, map2;
		ArrayList<Integer> ids, ids2;
		
		cms = new ArrayList<CaseManager>();
        if (elements != null && elements.length > 0)
            el = EnumSet.copyOf(Arrays.asList(elements));
        else
            el = EnumSet.noneOf(CaseManager.Load.class);
		
        ids = new ArrayList<Integer>();
        map = new HashMap<Integer, CaseManager>();
        
        for(CaseDO data : caseBean.fetchByIds(caseIds)) {
        	cm = new CaseManager();
        	accessor.setCase(cm, data);
        	cms.add(cm);
        	
        	ids.add(data.getId()); // for fetch
            map.put(data.getId(), cm); // for linking
            
            if(el.contains(CaseManager.Load.PATIENT)) {
            	Integer id = data.getCasePatientId();  
            	accessor.setPatient(cm, patient.fetchById(id != null ? id : data.getPatientId()));
            }
            
            if(el.contains(CaseManager.Load.NEXTKIN)) {
            	Integer id = data.getCaseNextkinId();
            	accessor.setNextkin(cm, patient.fetchById(id != null ? id : data.getNextkinId()));
            }
            
            if(el.contains(CaseManager.Load.ORGANIZAITON)) {
            	accessor.setOrganization(cm, organization.fetchById(data.getOrganizationId()));
            }
            
        }
        
        if(el.contains(CaseManager.Load.USER)){
        	for(CaseUserDO userDO : user.fetchByCaseIds(ids)) {
        		cm = map.get(userDO.getCaseId());
        		accessor.setCaseUser(cm, userDO);
        	}
        }
        
        if(el.contains(CaseManager.Load.PROVIDER)) {
        	for(CaseProviderDO providerDO : provider.fetchByCaseIds(ids)){
        		cm = map.get(providerDO.getCaseId());
        		accessor.setCaseProvider(cm,providerDO);
        	}
        }
        
        if(el.contains(CaseManager.Load.TAGS)) {
        	for(CaseTagDO data : tagBean.fetchByCaseIds(ids)) {
        		cm = map.get(data.getCaseId());
        		accessor.addTag(cm, data);
        	}
        }
        
        ids2 = new ArrayList<Integer>();
        map2 = new HashMap<Integer, CaseManager>();
        
        if(el.contains(CaseManager.Load.ANALYSIS)) {
        	for(CaseAnalysisDO data : analysisBean.fetchByCaseIds(ids)) {
        		cm = map.get(data.getCaseSampleId());
        		accessor.addAnalysis(cm,data);
        		ids2.add(data.getId()); // for fetch
                map2.put(data.getId(), cm); // for linking
        	}
        }
        
        if(el.contains(CaseManager.Load.RESULT)) {
        	for(CaseResultDO data : resultBean.fetchByAnalysisIds(ids2)) {
        		cm = map2.get(data.getCaseAnalysisId());
        		accessor.addResult(cm, data);
        	}
        }
		
		return cms;
	}
	
	public ArrayList<CaseManager> fetchActiveCasesByUser() throws Exception {		
		ArrayList<CaseManager> cms;
		CaseManager cm;
		ArrayList<CaseDO> cases;
		
		cases = caseBean.fetchActiveCasesByUser(userCache.getSystemUser().getId());
		cms = new ArrayList<CaseManager>();
		for(CaseDO data : cases) {
			cm = new CaseManager();
        	accessor.setCase(cm, data);
        	cms.add(cm);
		}
		
		return cms;
		
	}
	
	public ArrayList<CaseManager> fetchTestCases() throws Exception {
		ArrayList<CaseManager> cms;
		CaseManager cm;
		CaseDO _case;
		OrganizationViewDO org;
		PatientDO patient;
		CaseUserDO user;
		ArrayList<CaseTagDO> tags;
		CaseTagDO tag;
		
		
		cms = new ArrayList<CaseManager>();
		
		for(int i = 0; i < 20; i++) {
		cm = new CaseManager();
		_case = new CaseDO();
		patient = new PatientDO();
		org = new OrganizationViewDO();
		user = new CaseUserDO();
		tags = new ArrayList<CaseTagDO>();
		tag = new CaseTagDO();
		
		patient.setBirthDate(Datetime.getInstance());
		patient.setFirstName("first"+i);
		patient.setLastName("last"+i);
		org.setName("UIHC");
		user.setSystemUserId(1);
		tag.setTypeId(6358);
		tag.setCreatedDate(Datetime.getInstance());
		tags.add(tag);
		tag = new CaseTagDO();
		tag.setTypeId(6359);
		tag.setCreatedDate(Datetime.getInstance());
		tags.add(tag);
		
		accessor.setCase(cm, _case);
		accessor.setOrganization(cm, org);
		accessor.setPatient(cm, patient);
		accessor.setCaseUser(cm, user);
		accessor.setCaseTags(cm, tags);
		
		cms.add(cm);
		}
		return cms;
	}
	
	@RolesAllowed("case-update")
	public CaseManager fetchForUpdate(Integer id) throws Exception {
		return fetchForUpdate(id,CaseManager.Load.ANALYSIS,
				                 CaseManager.Load.NEXTKIN,
				                 CaseManager.Load.ORGANIZAITON,
				                 CaseManager.Load.PATIENT,
				                 CaseManager.Load.PROVIDER,
				                 CaseManager.Load.RESULT,
				                 CaseManager.Load.TAGS,
				                 CaseManager.Load.USER);
	}
	
	@RolesAllowed("case-update")
	public CaseManager fetchForUpdate(Integer id, CaseManager.Load... elements) throws Exception {
		ArrayList<Integer> ids;
		ArrayList<CaseManager> cms;
		
		ids = new ArrayList<Integer>(1);
		ids.add(id);
		cms = fetchForUpdate(ids,elements);
		return cms.size() > 0 ? cms.get(0) : null;
	}
	
	@RolesAllowed("case-update")
	public ArrayList<CaseManager> fetchForUpdate(ArrayList<Integer> ids, CaseManager.Load... elements) throws Exception {
		lock.lock(Constants.table().CASE, ids);
		return fetchByIds(ids,elements);
	}
	
	@RolesAllowed({"case-add","case-update"})
	public CaseManager update(CaseManager cm, boolean ignoreWarnings) throws Exception {
		ArrayList<CaseManager> cms;
		
		cms = new ArrayList<CaseManager>(1);
		cms.add(cm);
		update(cms,ignoreWarnings);
		return cms.get(0);
	}
	
	@RolesAllowed({"case-add","case-update"})
	public ArrayList<CaseManager> update(ArrayList<CaseManager> cms, boolean ignoreWarnings) throws Exception {
		ArrayList<Integer> locks;
		
		validate(cms);
		locks = checkLocks(cms);
		
		for (CaseManager cm : cms) {
			removeDeleted(cm);
			persistCase(cm);
			persistCasePatient(cm);
			persistCaseNextkin(cm);
			persistCaseUser(cm);
			persistCaseProvider(cm);
			persistCaseAnalysis(cm);
			persistCaseResult(cm);
			persistCaseTag(cm);
		}
		
	    if (locks != null)
	    	lock.unlock(Constants.table().SAMPLE, locks);

	    return cms;
	}
	
	protected void validate(ArrayList<CaseManager> cms) throws Exception {
		
	}
	
	protected ArrayList<Integer> checkLocks(ArrayList<CaseManager> cms) {
		return null;
	}
	
	protected void removeDeleted(CaseManager cm) throws Exception {
		ArrayList<DataObject> removed;
		
		removed = accessor.getRemoved(cm);
		if(removed != null) {
			for(DataObject data : removed) {
				if(data instanceof CaseAnalysisDO)
					analysisBean.delete((CaseAnalysisDO)data);
				if(data instanceof CaseResultDO)
					resultBean.delete((CaseResultDO)data);
				if(data instanceof CaseTagDO)
					tagBean.delete((CaseTagDO)data);
				else
					throw new DatabaseException("ERROR: DataObject passed for removal is of unknown type");
			}
		}
	}
	
	protected void persistCase(CaseManager cm) throws Exception {
		CaseDO _case;
		
		_case = accessor.getCase(cm);
		
		if(_case.getId() == null)
			caseBean.add(_case);
		else
			caseBean.update(_case);
	}
	
	protected void persistCasePatient(CaseManager cm) throws Exception {
		CasePatientDO casePatientDO;
		
		casePatientDO = accessor.getCasePatient(cm);
		
		if(casePatientDO == null) 
			return;
		
		if(casePatientDO.getId() == null)
			casePatient.add(casePatientDO);
		else
			casePatient.update(casePatientDO);

	}
	
	protected void persistCaseNextkin(CaseManager cm) throws Exception {
		CasePatientDO caseNextkin;
		
		caseNextkin = accessor.getCaseNextkin(cm);
		
		if(caseNextkin == null)
			return;
		
		if(caseNextkin.getId() == null)
			casePatient.add(caseNextkin);
		else
			casePatient.update(caseNextkin);
	}

	protected void persistCaseUser(CaseManager cm) throws Exception {
		CaseUserDO caseUser;
		
		caseUser = accessor.getCaseUser(cm);
		
		if(caseUser == null)
			return;
		
		if(caseUser.getId() == null)
			user.add(caseUser);
		else
			user.update(caseUser);
	}
	
	protected void persistCaseProvider(CaseManager cm) throws Exception {
		CaseProviderDO caseProvider;
		
		caseProvider = accessor.getCaseProvider(cm);
		
		if(caseProvider == null)
			return;
		
		if(caseProvider.getId() == null)
			provider.add(caseProvider);
		else
			provider.update(caseProvider);
	}
	
	protected void persistCaseAnalysis(CaseManager cm) throws Exception {
		ArrayList<CaseAnalysisDO> analyses;
		
		analyses = accessor.getCaseAnalyses(cm);
		
		if(analyses == null)
			return;
		
		for(CaseAnalysisDO analysis : analyses) {
			if(analysis.getId() == null)
				analysisBean.add(analysis);
			else
				analysisBean.update(analysis);
		}
	}
	
	protected void persistCaseResult(CaseManager cm) throws Exception {
		ArrayList<CaseResultDO> results;
		
		results = accessor.getCaseResults(cm);
		
		if(results == null)
			return;
		
		for(CaseResultDO result : results) {
			if(result.getId() == null)
				resultBean.add(result);
			else
				resultBean.update(result);
		}
	}
	
	protected void persistCaseTag(CaseManager cm) throws Exception {
		ArrayList<CaseTagDO> tags;
		
		tags = accessor.getCaseTags(cm);
		
		if(tags == null)
			return;
		
		for(CaseTagDO tag : tags) {
			if(tag.getId() == null)
				tagBean.add(tag);
			else
				tagBean.update(tag);
		}
	}
}
