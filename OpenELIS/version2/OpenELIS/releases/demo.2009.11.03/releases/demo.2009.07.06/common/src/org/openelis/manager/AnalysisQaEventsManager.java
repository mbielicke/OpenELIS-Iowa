/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/

package org.openelis.manager;

import java.io.Serializable;
import java.util.ArrayList;

import javax.persistence.Transient;

import org.openelis.domain.AnalysisQaEventDO;
import org.openelis.domain.NoteDO;
import org.openelis.domain.QaEventDO;


public class AnalysisQaEventsManager implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    protected boolean loaded;
    protected Integer analysisId;
    protected ArrayList<Item> items;
    protected ArrayList<AnalysisQaEventDO> removedAnaQaEvents;
    
    @Transient
    protected AnalysisQaEventsManagerIOInt manager;
    
    class Item {
        public AnalysisQaEventDO analysisQAEvent;
        public QaEventDO         qaEvent;
    }

    /**
     * Creates a new instance of this object.
     */
    public static AnalysisQaEventsManager getInstance() {
        AnalysisQaEventsManager aqm;

        aqm = new AnalysisQaEventsManager();
        aqm.items = new ArrayList<Item>();
        aqm.removedAnaQaEvents = new ArrayList<AnalysisQaEventDO>();

        return aqm;
    }
    
    public void setAnalysisId(Integer analysisId) {
        this.analysisId = analysisId;
    }   
    
    public int count() {
        manager().fetch();
        return items.size();
    }
    
    public AnalysisQaEventDO add(){
        Item item;

        manager().fetch();
        item = new Item();
        item.analysisQAEvent = new AnalysisQaEventDO();
        items.add(item);

        return item.analysisQAEvent;
    }
    
    public void remove(int i) {
        Item item;
        AnalysisQaEventDO aq;

        manager().fetch();
        item = (Item)items.get(i);
        aq = item.analysisQAEvent;
        //
        // save the AnalysisQAEvent record if it exists in the database
        // so we can delete it in store().
        //
        if (aq.getId() != null)
            removedAnaQaEvents.add(aq);

        items.remove(i);
    }
    
    public AnalysisQaEventDO getAnalysisQaEventAt(int i) {
        manager().fetch();
        return items.get(i).analysisQAEvent;
    }
    
    public QaEventDO getQaEventAt(int i) {
        return null;
    }
    
    public void setQaEventAt(QaEventDO qaEvent, int i) {
        Integer anaId = null;
        
        if (qaEvent == null)
            anaId = null;
        else
            anaId = qaEvent.getId();
            
        items.get(i).analysisQAEvent.setQaEventId(anaId);
        items.get(i).qaEvent = qaEvent;
    }
    
    public boolean hasResultOverride() {
        int i;
        
        for (i = 0; i < count(); i++) {
            if ("R".equals(getQaEventAt(i).getTypeId()))
                return true;
        }
        return false;
    }
    
    public String toString() {
        StringBuffer buff = new StringBuffer();
        
        for (int i = 0; i < count(); i++) {
            if (buff.length() > 0)
                buff.append(", ");
            buff.append(getQaEventAt(i).getName());
        }
        return buff.toString();
    }
    
    private AnalysisQaEventsManagerIOInt manager() {
        //manager = ManagerFactory.getAnalysisQaEventsManager();
        return manager;
    }

}
