package org.openelis.stfu.manager;

import java.io.Serializable;
import java.util.ArrayList;

import org.openelis.domain.DataObject;
import org.openelis.stfu.domain.CaseContactDO;
import org.openelis.stfu.domain.CaseContactLocationDO;

public class CaseContactManager implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public enum Load { LOCATION };
	
	protected CaseContactDO                       contact;
	protected ArrayList<CaseContactLocationDO>    locations;
	protected ArrayList<DataObject>               removed;
	
	public transient final Location location = new Location();
	
	public CaseContactManager() {

	}
	
	public CaseContactDO getContact() {
		return contact;
	}
	
	public class Location {
		public void add() {
			add(new CaseContactLocationDO());
		}
		
		public void add(CaseContactLocationDO location) {
			if(locations == null)
				locations =new ArrayList<CaseContactLocationDO>();
			
			locations.add(location);
		}
		
		public void remove(int i) {			
			if(locations != null) 
				remove(locations.get(i));
		}
		
		public void remove(CaseContactLocationDO location) {
			if(locations != null) {
				locations.remove(location);
				dataObjectRemove(location.getId(),location);
			}
		}
		
		public int count() {
			return locations != null ? locations.size() : 0;
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
