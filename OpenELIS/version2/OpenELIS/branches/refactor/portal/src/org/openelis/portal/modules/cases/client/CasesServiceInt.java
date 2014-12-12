package org.openelis.portal.modules.cases.client;

import java.util.ArrayList;

import org.openelis.stfu.manager.CaseManager;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

@RemoteServiceRelativePath("cases")
public interface CasesServiceInt extends XsrfProtectedService {

	public ArrayList<CaseManager> getActiveCases() throws Exception;
}
