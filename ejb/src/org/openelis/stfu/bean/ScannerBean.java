package org.openelis.stfu.bean;

import java.util.ArrayList;

import javax.ejb.Stateless;

import org.kie.api.KieServices;
import org.kie.api.command.Command;
import org.kie.api.command.KieCommands;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;
import org.openelis.domain.SampleNeonatalViewDO;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.SampleManager1Accessor;
import org.openelis.stfu.manager.CaseManager;

@Stateless
public class ScannerBean {

	public ArrayList<CaseManager> scan() throws Exception {
		try {
			KieServices ks = KieServices.Factory.get();
		    KieContainer kContainer = ks.getKieClasspathContainer();
	    	StatelessKieSession kSession = kContainer.newStatelessKieSession();
	    
			KieCommands kCommands = ks.getCommands();
	    	ArrayList<Command<SampleManager1>> commands = new ArrayList<>();

	    	ArrayList<CaseManager> cases = new ArrayList<CaseManager>();
	    	kSession.setGlobal("cases", cases);
	    	
	    	SampleManager1 sm = new SampleManager1();
	    	SampleNeonatalViewDO neoView = new SampleNeonatalViewDO();
	    	neoView.setIsTransfused("Y");
	    	
	    	SampleManager1Accessor.setSampleNeonatal(sm,neoView);
	    	
	    	commands.add(kCommands.newInsert(sm));
	    	
	    	kSession.execute(kCommands.newBatchExecution(commands));
	    	
	    	return cases;
		}catch(Exception e) {
			e.printStackTrace();
			throw(e);
		}
	}
}
