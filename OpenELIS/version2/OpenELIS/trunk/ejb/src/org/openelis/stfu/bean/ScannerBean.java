package org.openelis.stfu.bean;

import java.util.ArrayList;
import java.util.HashMap;

import javax.ejb.Stateless;

import org.kie.api.KieServices;
import org.kie.api.command.Command;
import org.kie.api.command.KieCommands;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;
import org.openelis.domain.AnalysisDO;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.SampleNeonatalViewDO;
import org.openelis.domain.TestDO;
import org.openelis.entity.Analysis;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.SampleManager1Accessor;
import org.openelis.stfu.manager.CaseManager;
import org.openelis.stfu.scanner.ScannerRecord;

@Stateless
public class ScannerBean {

	public HashMap<Integer,CaseManager> scan() throws Exception {
		try {
			KieServices ks = KieServices.Factory.get();
		    KieContainer kContainer = ks.getKieClasspathContainer();
	    	StatelessKieSession kSession = kContainer.newStatelessKieSession();
	    
			KieCommands kCommands = ks.getCommands();
	    	ArrayList<Command<?>> commands = new ArrayList<>();

	    	HashMap<Integer,CaseManager> cases = new HashMap<Integer,CaseManager>();
	    	kSession.setGlobal("cases", cases);
	    	
	    	SampleManager1 sm = new SampleManager1();
	    	SampleNeonatalViewDO neoView = new SampleNeonatalViewDO();
	    	neoView.setPatientId(1);
	    	SampleManager1Accessor.setSampleNeonatal(sm,neoView);
	    	
	    	ScannerRecord sr = new ScannerRecord();
	    	sr.setSampleManager(sm);
	    	TestDO test = new TestDO();
	    	test.setName("nbs galt");
	    	sr.setTest(test);
	    	DictionaryDO interpratation = new DictionaryDO();
	    	interpratation.setSystemName("newborn_inter_tran");
	    	sr.setInterpretation(interpratation);
	    	commands.add(kCommands.newInsert(sr));
	    	
	    	sm = new SampleManager1();
	    	neoView = new SampleNeonatalViewDO();
	    	neoView.setPatientId(2);
	    	SampleManager1Accessor.setSampleNeonatal(sm,neoView);
	    	
	    	sr = new ScannerRecord();
	    	sr.setSampleManager(sm);
	    	test.setName("nbs bt");
	    	sr.setTest(test);
	    	interpratation = new DictionaryDO();
	    	interpratation.setSystemName("newborn_inter_pp_nr");
	    	sr.setInterpretation(interpratation);
	    	commands.add(kCommands.newInsert(sr));
	    	
	    	sr = new ScannerRecord();
	    	sr.setSampleManager(sm);
	    	test = new TestDO();
	    	test.setName("nbs cah");
	    	sr.setTest(test);
	    	interpratation = new DictionaryDO();
	    	interpratation.setSystemName("newborn_inter_pp_nr");
	    	sr.setInterpretation(interpratation);
	    	commands.add(kCommands.newInsert(sr));
	    	
	    	kSession.execute(kCommands.newBatchExecution(commands));
	    	
	    	return cases;
		}catch(Exception e) {
			e.printStackTrace();
			throw(e);
		}
	}
}
