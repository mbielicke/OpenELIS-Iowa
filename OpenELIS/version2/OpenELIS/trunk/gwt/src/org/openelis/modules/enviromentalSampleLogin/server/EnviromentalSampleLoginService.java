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
package org.openelis.modules.enviromentalSampleLogin.server;

import java.util.Date;

import org.openelis.domain.SampleDO;
import org.openelis.domain.SampleEnvironmentalDO;
import org.openelis.domain.SampleItemDO;
import org.openelis.gwt.common.Query;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.data.TableDataRow;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.manager.SampleEnvironmentalManager;
import org.openelis.manager.SampleItemsManager;
import org.openelis.manager.SampleManager;
import org.openelis.manager.SampleManagerIOClient;
import org.openelis.modules.enviromentalSampleLogin.client.EnvironmentalSampleLoginForm;
import org.openelis.server.constants.Constants;

public class EnviromentalSampleLoginService implements AppScreenFormServiceInt<EnvironmentalSampleLoginForm,Query<TableDataRow<Integer>>>{

    public EnvironmentalSampleLoginForm abort(EnvironmentalSampleLoginForm rpc) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public EnvironmentalSampleLoginForm commitAdd(EnvironmentalSampleLoginForm rpc) throws RPCException {
        SampleManager manager = SampleManager.getInstance();
        manager.setManager(new SampleManagerIOClient());
        
        //create test sampleDO
        SampleDO smplDO = manager.getSample();
        smplDO.setAccessionNumber(1);
        smplDO.setClientReference("client ref");
        smplDO.setCollectionDate(new Date());
        smplDO.setCollectionTime(new Date());
        smplDO.setEnteredDate(new Date());
        smplDO.setNextItemSequence(2);
        smplDO.setPackageId(3);
        smplDO.setReceivedById(1234);
        smplDO.setReceivedDate(new Date());
        smplDO.setReleasedDate(new Date());
        smplDO.setRevision(12);
        smplDO.setStatusId(344);
        
        //create sample domain
        SampleEnvironmentalManager envManager = SampleEnvironmentalManager.getInstance();
        SampleEnvironmentalDO envDO = envManager.getEnviromental();
        envDO.setAddressId(4);
        envDO.setCollector("Joe Farmer");
        envDO.setCollectorPhone("319-325-3256");
        envDO.setDescription("test env item");
        envDO.setIsHazardous("N");
        envDO.setSamplingLocation("location");
        manager.setAdditonalDomain(envManager);
        
        //create a few sample items
        SampleItemsManager itemsManager = manager.getSampleItems();
        SampleItemDO itemDO = itemsManager.add();
        itemDO.setContainerId(1);
        itemDO.setContainerReferenceId("ref");
        itemDO.setItemSequence(2);
        itemDO.setQuantity(new Double(3));
        itemDO.setSourceOfSampleId(4);
        itemDO.setSourceOther("other");
        itemDO.setTypeOfSampleId(5);
        itemDO.setUnitOfMeasureId(6);
        
        SampleItemDO item2DO = itemsManager.add();
        item2DO.setContainerId(7);
        item2DO.setContainerReferenceId("ref2");
        item2DO.setItemSequence(8);
        item2DO.setQuantity(new Double(9));
        item2DO.setSourceOfSampleId(10);
        item2DO.setSourceOther("other2");
        item2DO.setTypeOfSampleId(11);
        item2DO.setUnitOfMeasureId(12);
        
        System.out.println("before update");
        manager.update();
        System.out.println("after update");
        // TODO Auto-generated method stub
        return null;
    }

    public EnvironmentalSampleLoginForm commitDelete(EnvironmentalSampleLoginForm rpc) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public Query<TableDataRow<Integer>> commitQuery(Query<TableDataRow<Integer>> query) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public EnvironmentalSampleLoginForm commitUpdate(EnvironmentalSampleLoginForm rpc) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public EnvironmentalSampleLoginForm fetch(EnvironmentalSampleLoginForm rpc) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public EnvironmentalSampleLoginForm fetchForUpdate(EnvironmentalSampleLoginForm rpc) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public EnvironmentalSampleLoginForm getScreen(EnvironmentalSampleLoginForm rpc) throws RPCException {
        rpc.xml = ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/enviromentalSampleLogin.xsl");
        return rpc;
    }
}