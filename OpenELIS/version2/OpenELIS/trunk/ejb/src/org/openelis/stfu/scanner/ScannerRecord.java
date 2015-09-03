package org.openelis.stfu.scanner;

import org.openelis.domain.DictionaryDO;
import org.openelis.domain.TestDO;
import org.openelis.manager.SampleManager1;

public class ScannerRecord {
	
	protected SampleManager1 sampleManager;
	protected TestDO test;
	protected DictionaryDO interpretation;
	
	public SampleManager1 getSampleManager() {
		return sampleManager;
	}
	
	public void setSampleManager(SampleManager1 sampleManager) {
		this.sampleManager = sampleManager;
	}
	
	public TestDO getTest() {
		return test;
	}
	
	public void setTest(TestDO test) {
		this.test = test;
	}
	
	public DictionaryDO getInterpratation() {
		return interpretation;
	}
	
	public void setInterpretation(DictionaryDO interpratation) {
		this.interpretation = interpratation;
	}
}
