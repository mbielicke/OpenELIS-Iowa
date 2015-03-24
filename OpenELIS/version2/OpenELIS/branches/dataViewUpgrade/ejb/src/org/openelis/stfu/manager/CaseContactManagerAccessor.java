package org.openelis.stfu.manager;

import java.util.ArrayList;

import org.openelis.domain.DataObject;
import org.openelis.stfu.domain.CaseContactDO;
import org.openelis.stfu.domain.CaseContactLocationDO;

public class CaseContactManagerAccessor {

	public CaseContactDO getCaseContact(CaseContactManager ccm) {
		return ccm.getContact();
	}
	
	public void setCaseContact(CaseContactManager ccm, CaseContactDO contact) {
		ccm.contact = contact;
	}
	
	public ArrayList<CaseContactLocationDO> getLocations(CaseContactManager ccm) {
		return ccm.locations;
	}
	
	public void setLocations(CaseContactManager ccm, ArrayList<CaseContactLocationDO> locations) {
		ccm.locations = locations;
	}
	
	public void addLocation(CaseContactManager ccm, CaseContactLocationDO location) {
		ccm.location.add(location);
	}
	
	public ArrayList<DataObject> getRemoved(CaseContactManager ccm) {
		return ccm.removed;
	}
}
