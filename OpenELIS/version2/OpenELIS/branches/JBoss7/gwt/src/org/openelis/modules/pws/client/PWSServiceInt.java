package org.openelis.modules.pws.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.PWSDO;
import org.openelis.gwt.common.data.Query;
import org.openelis.manager.PWSAddressManager;
import org.openelis.manager.PWSFacilityManager;
import org.openelis.manager.PWSManager;
import org.openelis.manager.PWSMonitorManager;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("pws")
public interface PWSServiceInt extends RemoteService {

    PWSManager fetchByTinwsysIsNumber(Integer tinwsysIsNumber) throws Exception;

    PWSManager fetchWithFacilities(Integer tinwsysIsNumber) throws Exception;

    PWSManager fetchWithAddresses(Integer tinwsysIsNumber) throws Exception;

    PWSManager fetchWithMonitors(Integer tinwsysIsNumber) throws Exception;

    ArrayList<IdNameVO> query(Query query) throws Exception;

    //
    // support for OrganizationContactManager and OrganizationParameterManager
    //
    PWSFacilityManager fetchFacilityByTinwsysIsNumber(Integer tinwsysIsNumber) throws Exception;

    PWSAddressManager fetchAddressByTinwsysIsNumber(Integer tinwsysIsNumber) throws Exception;

    PWSMonitorManager fetchMonitorByTinwsysIsNumber(Integer tinwsysIsNumber) throws Exception;

    PWSDO fetchPwsByNumber0(String pwsNumber0) throws Exception;

}