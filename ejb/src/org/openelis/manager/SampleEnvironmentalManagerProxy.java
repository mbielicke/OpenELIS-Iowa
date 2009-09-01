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

import javax.naming.InitialContext;

import org.openelis.domain.SampleEnvironmentalDO;
import org.openelis.local.SampleEnvironmentalLocal;

public class SampleEnvironmentalManagerProxy {
    public SampleEnvironmentalManager add(SampleEnvironmentalManager man) throws Exception {
        SampleEnvironmentalLocal el = getSampleEnvLocal();
        
        man.getEnvironmental().setSampleId(man.getSampleId());
        el.add(man.getEnvironmental());
        
        return man;
    }

    public SampleEnvironmentalManager update(SampleEnvironmentalManager man) throws Exception {
        SampleEnvironmentalLocal el = getSampleEnvLocal();
        
        man.getEnvironmental().setSampleId(man.getSampleId());
        el.update(man.getEnvironmental());
        
        return man;
    }
    
    public SampleEnvironmentalManager fetch(Integer sampleId) throws Exception {
        SampleEnvironmentalLocal el = getSampleEnvLocal();
        
        SampleEnvironmentalDO envDO = el.fetchBySampleId(sampleId);
        SampleEnvironmentalManager em = SampleEnvironmentalManager.getInstance();
        
        em.setEnvironmental(envDO);
        
        return em;
    }
    
    private SampleEnvironmentalLocal getSampleEnvLocal(){
        try{
            InitialContext ctx = new InitialContext();
            return (SampleEnvironmentalLocal)ctx.lookup("openelis/SampleEnvironmentalBean/local");
        }catch(Exception e){
             System.out.println(e.getMessage());
             return null;
        }
    }
}