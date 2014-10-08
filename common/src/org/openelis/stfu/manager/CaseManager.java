package org.openelis.stfu.manager;

import java.io.Serializable;
import java.util.ArrayList;

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

public class CaseManager implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public enum Load {
		PATIENT,NEXTKIN,ORGANIZAITON,USER,PROVIDER,ANALYSIS,RESULT,TAGS;
	}
	
	protected CaseDO                        _case;
	protected PatientDO                     patient,nextkin;
	protected CasePatientDO                 casePatient,caseNextkin;
	protected OrganizationViewDO            organization;
	protected CaseUserDO                    caseUser;
	protected CaseProviderDO                caseProvider;
	protected ArrayList<CaseAnalysisDO>     caseAnalyses;
	protected ArrayList<CaseResultDO>       caseResults;
	protected ArrayList<CaseTagDO>          caseTags;
	protected ArrayList<DataObject>         removed;
	
	public transient final Analysis analysis = new Analysis();
	public transient final Result   result   = new Result();
	public transient final Tag      tag      = new Tag();
	
	public CaseManager() {
		
	}
	
	public CaseDO getCase() {
		return _case;
	}
	
	public PatientDO getPatient() {
		return patient;
	}
	
	public PatientDO getNextkin() {
		return nextkin;
	}
	
	public CasePatientDO getCasePatient() {
		return casePatient;
	}
	
	public CasePatientDO getCaseNextkin() {
		return caseNextkin;
	}
	
	public CaseProviderDO getCaseProvider() {
		return caseProvider;
	}
	
	public CaseUserDO getCaseUser() {
		return caseUser;
	}
	
	public class Analysis {
		
		public void add() {
			add(new CaseAnalysisDO());
		}
		
		public void add(CaseAnalysisDO analysis) {
			if(caseAnalyses == null)
				caseAnalyses = new ArrayList<CaseAnalysisDO>();
			
			caseAnalyses.add(analysis);
		}
		
		public CaseAnalysisDO get(int i) {
			return caseAnalyses != null ? caseAnalyses.get(i) : null;
		}
		
		public void remove(int i) {			
			if(caseAnalyses != null) 
				remove(caseAnalyses.get(i));
		}
		
		public void remove(CaseAnalysisDO analysis) {
			if(caseAnalyses != null) {
				caseAnalyses.remove(analysis);
				dataObjectRemove(analysis.getId(),analysis);
			}
		}
		
		public int count() {
			return caseAnalyses != null ? caseAnalyses.size() : 0;
		}
	}
	
	public class Result {
		public void add() {
			add(new CaseResultDO());
		}
		
		public void add(CaseResultDO result) {
			if(caseResults == null)
				caseResults = new ArrayList<CaseResultDO>();
			caseResults.add(result);
		}
		
		public CaseResultDO get(int i) {
			return caseResults != null ? caseResults.get(i) : null;
		}
		
		public void remove(int i) {
			if(caseResults != null) 
				remove(caseResults.get(i));
		}
		
		public void remove(CaseResultDO result) {
			if(caseResults != null) {
				caseResults.remove(result);
				dataObjectRemove(result.getId(),result);
			}
		}
		
		public int count() {
			return caseResults != null ? caseResults.size() : 0;
		}
		
	}
	
	public class Tag {
		public void add() {
			add(new CaseTagDO());
		}
		
		public void add(CaseTagDO tag) {
			if(caseTags == null)
				caseTags = new ArrayList<CaseTagDO>();
			caseTags.add(tag);
		}
		
		public CaseTagDO get(int i) {
			return caseTags != null ? caseTags.get(i) : null;
		}
		
		public void remove(int i) {			
			if(caseTags != null) 
				remove(caseTags.get(i));
		}
		
		public void remove(CaseTagDO tag) {
			if(caseTags != null) {
				caseTags.remove(tag);
				dataObjectRemove(tag.getId(),tag);
			}
			
		}
		
		public int count() {
			return caseTags != null ? caseTags.size() : 0;
		}
	}
	
    /**
     * adds the data object to the list of objects that should be removed from
     * the database
     */
    private void dataObjectRemove(Integer id, DataObject data) {
        if (removed == null)
            removed = new ArrayList<DataObject>();
        if (id > 0)
            removed.add(data);
    }
}
