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

package org.openelis.modules.auxiliary.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openelis.domain.AuxFieldDO;
import org.openelis.domain.AuxFieldGroupDO;
import org.openelis.domain.AuxFieldValueDO;
import org.openelis.domain.IdNameDO;
import org.openelis.gwt.common.DatetimeRPC;
import org.openelis.gwt.common.EntityLockedException;
import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.FieldType;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.AppScreenFormServiceInt;
import org.openelis.gwt.services.AutoCompleteServiceInt;
import org.openelis.modules.auxiliary.client.AuxiliaryForm;
import org.openelis.modules.auxiliary.client.AuxiliaryRPC;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.AuxiliaryRemote;
import org.openelis.server.constants.Constants;
import org.openelis.util.Datetime;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;

public class AuxiliaryService implements
                             AppScreenFormServiceInt<AuxiliaryRPC, Integer> , AutoCompleteServiceInt{
    
    private static final int leftTableRowsPerPage = 21;
    private UTFResource openElisConstants = UTFResource.getBundle((String)SessionManager.getSession()
                                                                                        .getAttribute("locale")); 
    
    public AuxiliaryRPC abort(AuxiliaryRPC rpcReturn) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public AuxiliaryRPC commitAdd(AuxiliaryRPC rpc) throws RPCException {
        AuxiliaryRemote remote = (AuxiliaryRemote)EJBFactory.lookup("openelis/AuxiliaryBean/remote");
        AuxFieldGroupDO axfgDO = getAuxFieldGroupDOFromRPC(rpc.form);
        Integer axfgId;
        List<AuxFieldDO> axfDOList = null;
        List<AuxFieldValueDO> axfvDOList = null;
        List exceptionList = remote.validateForAdd(axfgDO,axfDOList,axfvDOList);
        if (exceptionList.size() > 0) {
            setRpcErrors(exceptionList, rpc.form);
    
            return rpc;
        }
    
        try {
            axfgId = remote.updateAuxiliary(axfgDO,axfDOList,axfvDOList);
            axfgDO = remote.getAuxFieldGroup(axfgId);
        } catch (Exception e) {
            if (e instanceof EntityLockedException)
                throw new RPCException(e.getMessage());
    
            exceptionList = new ArrayList<Exception>();
            exceptionList.add(e);
    
            setRpcErrors(exceptionList, rpc.form);
    
            return rpc;
        }
    
        axfgDO.setId(axfgId);
        setFieldsInRPC(rpc.form, axfgDO);
        return rpc;
    }


    private void setFieldsInRPC(AuxiliaryForm form,
                                AuxFieldGroupDO axfgDO) {
        form.id.setValue(axfgDO.getId());
        form.activeBegin.setValue(DatetimeRPC.getInstance(Datetime.YEAR,
                                                          Datetime.DAY,
                                                          axfgDO.getActiveBegin()
                                                                       .getDate()));
        form.activeEnd.setValue(DatetimeRPC.getInstance(Datetime.YEAR,
                                                          Datetime.DAY,
                                                          axfgDO.getActiveEnd()
                                                                       .getDate()));
        form.isActive.setValue(axfgDO.getIsActive()); 
        
        form.name.setValue(axfgDO.getName());
        
        form.description.setValue(axfgDO.getDescription());
        
    }

    private void setRpcErrors(List exceptionList, AuxiliaryForm form) {

        
    }

    public AuxiliaryRPC commitDelete(AuxiliaryRPC rpc) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public DataModel<Integer> commitQuery(Form form, DataModel<Integer> model) throws RPCException {
        List auxfgNames;
        // if the rpc is null then we need to get the page
        if (form == null) {

            form = (Form)SessionManager.getSession()
                                                 .getAttribute("AuxiliaryQuery");

            if (form == null)
                throw new RPCException(openElisConstants.getString("queryExpiredException"));

            AuxiliaryRemote remote = (AuxiliaryRemote)EJBFactory.lookup("openelis/AuxiliaryBean/remote");

            try {
                auxfgNames = remote.query(form.getFieldMap(),
                                         (model.getPage() * leftTableRowsPerPage),
                                         leftTableRowsPerPage + 1);
            } catch (Exception e) {
                if (e instanceof LastPageException) {
                    throw new LastPageException(openElisConstants.getString("lastPageException"));
                } else {
                    e.printStackTrace();
                    throw new RPCException(e.getMessage());
                }
            }
        } else {
            AuxiliaryRemote remote = (AuxiliaryRemote)EJBFactory.lookup("openelis/AuxiliaryBean/remote");

            HashMap<String, AbstractField> fields = form.getFieldMap();
            fields.remove("auxiliaryTable");
            fields.remove("auxFieldValueTable");

            try {
                auxfgNames = remote.query(fields, 0, leftTableRowsPerPage);

            } catch (Exception e) {
                e.printStackTrace();
                throw new RPCException(e.getMessage());
            }

            // need to save the rpc used to the encache
            SessionManager.getSession().setAttribute("AuxiliaryQuery", form);
        }

        // fill the model with the query results
        int i = 0;
        if(model == null)
            model = new DataModel<Integer>();
        else 
            model.clear();
        while (i < auxfgNames.size() && i < leftTableRowsPerPage) {
            IdNameDO resultDO = (IdNameDO)auxfgNames.get(i);
            model.add(new DataSet<Integer>(resultDO.getId(),new StringObject(resultDO.getName())));
            i++;
        }

        return model;
    }

    public AuxiliaryRPC commitUpdate(AuxiliaryRPC rpc) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }

    public AuxiliaryRPC fetch(AuxiliaryRPC rpc) throws RPCException {
        AuxiliaryRemote remote = (AuxiliaryRemote)EJBFactory.lookup("openelis/AuxiliaryBean/remote");
        AuxFieldGroupDO axfgDO = remote.getAuxFieldGroup(rpc.key);
        setFieldsInRPC(rpc.form, axfgDO);
        return rpc;
    }

    public AuxiliaryRPC fetchForUpdate(AuxiliaryRPC rpc) throws RPCException {
        AuxiliaryRemote remote = (AuxiliaryRemote)EJBFactory.lookup("openelis/AuxiliaryBean/remote");
        AuxFieldGroupDO axfgDO = new AuxFieldGroupDO(); 
        try{
            axfgDO = remote.getAuxFieldGroup(rpc.key);
        }   catch (Exception e) {
            e.printStackTrace();
            throw new RPCException(e.getMessage());
        }
        setFieldsInRPC(rpc.form, axfgDO);
        return rpc;
    }

    public AuxiliaryRPC getScreen(AuxiliaryRPC rpc) throws RPCException {
        try {
         rpc.xml = ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/auxiliary.xsl");
        }catch(Exception ex) {
            ex.printStackTrace();
            throw new RPCException(ex.getMessage());
        } 
        return rpc;
    }

    public String getXML() throws RPCException {
        return ServiceUtils.getXML(Constants.APP_ROOT + "/Forms/auxiliary.xsl");
    }

    public HashMap<String, FieldType> getXMLData() throws RPCException {
        StringObject xml = new StringObject();
        xml.setValue(ServiceUtils.getXML(Constants.APP_ROOT + "/Forms/auxiliary.xsl"));       

        HashMap<String, FieldType> map = new HashMap<String, FieldType>();
        map.put("xml", xml);        
        return map;
    }

    public HashMap<String, FieldType> getXMLData(HashMap<String, FieldType> args) throws RPCException {
        return null;
    }

    public DataModel getMatches(String cat,
                                DataModel model,
                                String match,
                                HashMap<String, FieldType> params) throws RPCException {
        // TODO Auto-generated method stub
        return null;
    }
    
    private AuxFieldGroupDO getAuxFieldGroupDOFromRPC(AuxiliaryForm form){
        AuxFieldGroupDO auxFieldGroupDO = new AuxFieldGroupDO();
        if(form.id!=null)
         auxFieldGroupDO.setId(form.id.getValue());
        auxFieldGroupDO.setName(((String)form.name.getValue()));   
        DatetimeRPC activeBegin = form.activeBegin.getValue();
        if (activeBegin != null)
            auxFieldGroupDO.setActiveBegin(activeBegin.getDate());

        DatetimeRPC activeEnd = form.activeEnd.getValue();
        if (activeEnd != null)
            auxFieldGroupDO.setActiveEnd(activeEnd.getDate());

        auxFieldGroupDO.setDescription(form.description.getValue());
        auxFieldGroupDO.setIsActive(form.isActive.getValue());
        
        return auxFieldGroupDO;
    }

}
