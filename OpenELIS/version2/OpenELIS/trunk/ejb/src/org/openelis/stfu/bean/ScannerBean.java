package org.openelis.stfu.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ejb.Stateless;

import org.kie.api.KieServices;
import org.kie.api.command.Command;
import org.kie.api.command.KieCommands;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;
import org.openelis.stfu.manager.CaseManager;
import org.openelis.stfu.scanner.ScannerRecord;

@Stateless
public class ScannerBean {

	public HashMap<Integer,CaseManager> scan() throws Exception {
		HashMap<Integer,CaseManager> cases = new HashMap<Integer,CaseManager>();
		
		applyRules(null,cases);
		
		return cases;
	}
	
	public void applyRules(List<ScannerRecord> scannerRecords, HashMap<Integer, CaseManager> cases) throws Exception {
		try {
			KieServices ks = KieServices.Factory.get();
		    KieContainer kContainer = ks.getKieClasspathContainer();
	    	StatelessKieSession kSession = kContainer.newStatelessKieSession();
	    
			KieCommands kCommands = ks.getCommands();
	    	ArrayList<Command<?>> commands = new ArrayList<>();

	    	kSession.setGlobal("cases", cases);
	    	
	    	for (ScannerRecord sr : scannerRecords) {
	    		commands.add(kCommands.newInsert(sr));
	    	}
	    		    	
	    	kSession.execute(kCommands.newBatchExecution(commands));
	    	
		}catch(Exception e) {
			e.printStackTrace();
			throw(e);
		}
	}
}
