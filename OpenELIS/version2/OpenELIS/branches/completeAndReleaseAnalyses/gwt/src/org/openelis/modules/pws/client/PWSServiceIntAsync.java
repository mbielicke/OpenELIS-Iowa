package org.openelis.modules.pws.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.PWSDO;
import org.openelis.manager.PWSAddressManager;
import org.openelis.manager.PWSFacilityManager;
import org.openelis.manager.PWSManager;
import org.openelis.manager.PWSMonitorManager;
import org.openelis.manager.PWSViolationManager;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.data.Query;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface PWSServiceIntAsync {

    void fetchAddressByTinwsysIsNumber(Integer tinwsysIsNumber,
                                       AsyncCallback<PWSAddressManager> callback);

    void fetchByTinwsysIsNumber(Integer tinwsysIsNumber, AsyncCallback<PWSManager> callback);

    void fetchFacilityByTinwsysIsNumber(Integer tinwsysIsNumber,
                                        AsyncCallback<PWSFacilityManager> callback);

    void fetchMonitorByTinwsysIsNumber(Integer tinwsysIsNumber,
                                       AsyncCallback<PWSMonitorManager> callback);

    void fetchViolationByTinwsysIsNumber(Integer tinwsysIsNumber,
                                         AsyncCallback<PWSViolationManager> callback);

    void fetchPwsByNumber0(String pwsNumber0, AsyncCallback<PWSDO> callback);

    void fetchWithAddresses(Integer tinwsysIsNumber, AsyncCallback<PWSManager> callback);

    void fetchWithFacilities(Integer tinwsysIsNumber, AsyncCallback<PWSManager> callback);

    void fetchWithMonitors(Integer tinwsysIsNumber, AsyncCallback<PWSManager> callback);

    void fetchWithViolations(Integer tinwsysIsNumber, AsyncCallback<PWSManager> callback);

    void query(Query query, AsyncCallback<ArrayList<IdNameVO>> callback);

    void importFiles(AsyncCallback<Void> callback);

    void sdwisViolationScan(AsyncCallback<Void> callback);

    void sdwisAdditionalScan(AsyncCallback<Void> callback);

    void getStatus(AsyncCallback<ReportStatus> callback);
}
