package org.openelis.portal.modules.cases.server;

import javax.ejb.EJB;
import java.util.ArrayList;
import javax.servlet.annotation.WebServlet;

import org.openelis.portal.modules.cases.client.CasesServiceInt;
import org.openelis.stfu.bean.CaseManagerBean;
import org.openelis.stfu.manager.CaseManager;
import org.openelis.ui.server.RemoteServlet;

@WebServlet("/portal/cases")
public class CasesServlet extends RemoteServlet implements CasesServiceInt {

	private static final long serialVersionUID = 1L;
	
	@EJB
	CaseManagerBean manager;
	
	@Override
	public ArrayList<CaseManager> getActiveCases() throws Exception {
		return manager.fetchTestCases();
	}
	

}
