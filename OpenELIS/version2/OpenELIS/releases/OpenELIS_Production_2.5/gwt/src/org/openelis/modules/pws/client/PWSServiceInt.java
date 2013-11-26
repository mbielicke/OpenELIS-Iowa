package org.openelis.modules.pws.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.PWSDO;
import org.openelis.manager.PWSAddressManager;
import org.openelis.manager.PWSFacilityManager;
import org.openelis.manager.PWSManager;
import org.openelis.manager.PWSMonitorManager;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.data.Query;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

@RemoteServiceRelativePath("pws")
public interface PWSServiceInt extends XsrfProtectedService {

    PWSManager fetchByTinwsysIsNumber(Integer tinwsysIsNumber) throws Exception;

    PWSManager fetchWithFacilities(Integer tinwsysIsNumber) throws Exception;

    PWSManager fetchWithAddresses(Integer tinwsysIsNumber) throws Exception;

    PWSManager fetchWithMonitors(Integer tinwsysIsNumber) throws Exception;

    ArrayList<IdNameVO> query(Query query) throws Exception;

    PWSFacilityManager fetchFacilityByTinwsysIsNumber(Integer tinwsysIsNumber) throws Exception;

    PWSAddressManager fetchAddressByTinwsysIsNumber(Integer tinwsysIsNumber) throws Exception;

    PWSMonitorManager fetchMonitorByTinwsysIsNumber(Integer tinwsysIsNumber) throws Exception;

    PWSDO fetchPwsByNumber0(String pwsNumber0) throws Exception;

    void importFiles() throws Exception;

    ReportStatus getStatus() throws Exception;
}