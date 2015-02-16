package org.openelis.stfu.bean;

import java.util.ArrayList;
import java.util.Date;

import javax.ejb.Stateless;

import org.kie.api.KieServices;
import org.kie.api.command.Command;
import org.kie.api.command.KieCommands;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;
import org.openelis.stfu.domain.Counter;
import org.openelis.stfu.domain.RulesTest;

@Stateless
public class ScannerBean {

	public Counter scan() throws Exception {
		try {
			KieServices ks = KieServices.Factory.get();
		    KieContainer kContainer = ks.getKieClasspathContainer();
	    	StatelessKieSession kSession = kContainer.newStatelessKieSession("ksession-rules");
	    
			KieCommands kCommands = ks.getCommands();
	    	ArrayList<Command<RulesTest>> commands = new ArrayList<>();
	    	ArrayList<RulesTest> dos = new ArrayList<>();
	    	Counter counter = new Counter();
	    	kSession.setGlobal("counter", counter);
	    	
	    	commands.add(kCommands.newStartProcess("CaseFlow"));
	    	
	    	for(int i = 0; i < 100000; i++) {
	    		dos.add(new RulesTest());
	    		if(i % 10000 == 0) {
	    			commands.add(kCommands.newInsertElements(dos));
	    			dos = new ArrayList<>();
	    		}
	    	}
	    	
	    	commands.add(kCommands.newInsertElements(dos));
	    	
	    	Date start = new Date();
	    	
	    	
	    	kSession.execute(kCommands.newBatchExecution(commands));
	    	
	    	System.out.println("time = "+(new Date().getTime() - start.getTime()));
	    	System.out.println("Counter less = "+counter.less);
	    	System.out.println("Counter greater = "+counter.greater);
	    	System.out.println("Counter equals = "+counter.equals);
	    	return counter;
		}catch(Exception e) {
			e.printStackTrace();
			throw(e);
		}
	}
}
