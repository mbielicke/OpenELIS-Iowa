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
package org.openelis.local;

import javax.ejb.Local;

import org.openelis.domain.AddressDO;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.MethodDO;
import org.openelis.domain.OrganizationDO;
import org.openelis.domain.PWSDO;
import org.openelis.domain.ProjectViewDO;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.SampleDO;
import org.openelis.domain.SampleEnvironmentalDO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.domain.SampleOrganizationViewDO;
import org.openelis.domain.SamplePrivateWellViewDO;
import org.openelis.domain.SampleProjectViewDO;
import org.openelis.domain.SampleSDWISDO;
import org.openelis.domain.TestViewDO;
import org.openelis.gwt.common.SystemUserVO;
import org.openelis.manager.ExchangeCriteriaManager;
import org.openelis.manager.SampleManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@Local
public interface DataExchangeXMLMapperLocal {
    public Document getXML(SampleManager manager,  ExchangeCriteriaManager exchangeCriteriaMan) throws Exception;
    
    public Element getSample(Document document, SampleDO sample);
    
    public Element getSampleEnviromental(Document document, SampleEnvironmentalDO environmental);
    
    public Element getSamplePrivateWell(Document document, SamplePrivateWellViewDO privateWell);
    
    public Element getSampleSDWIS(Document document, SampleSDWISDO sdwis);
    
    public Element getPWS(Document document, PWSDO pws);
    
    public Element getSampleItem(Document document, SampleItemViewDO sampleItem);
    
    public Element getAnalysis(Document document, AnalysisViewDO analysis);
    
    public Element getTest(Document document, TestViewDO test);
    
    public Element getMethod(Document document, MethodDO method);
    
    public Element getSampleProject(Document document, SampleProjectViewDO sampleProject);
    
    public Element getProject(Document document, ProjectViewDO project);
    
    public Element getSampleOrganization(Document document, SampleOrganizationViewDO sampleOrganization);
    
    public Element getOrganization(Document document, OrganizationDO organization);
    
    public Element getSystemUser(Document document, SystemUserVO user);
    
    public Element getDictionary(Document document, DictionaryDO dictionary);
    
    public Element getAddress(Document document, AddressDO address);
    
    public Element getResult(Document document, ResultViewDO result,  boolean showValue);
}
