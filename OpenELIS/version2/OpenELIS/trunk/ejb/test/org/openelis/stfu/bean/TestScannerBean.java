package org.openelis.stfu.bean;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openelis.deployment.Deployments;
import org.openelis.domain.AnalysisQaEventViewDO;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.OrganizationViewDO;
import org.openelis.domain.SampleDO;
import org.openelis.domain.SampleNeonatalViewDO;
import org.openelis.domain.SampleOrganizationViewDO;
import org.openelis.domain.SampleQaEventViewDO;
import org.openelis.domain.TestDO;
import org.openelis.ui.common.Datetime;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.SampleManager1Accessor;
import org.openelis.stfu.domain.CaseAnalysisDO;
import org.openelis.stfu.domain.CaseDO;
import org.openelis.stfu.domain.CasePatientDO;
import org.openelis.stfu.domain.CaseProviderDO;
import org.openelis.stfu.domain.CaseResultDO;
import org.openelis.stfu.domain.CaseSampleDO;
import org.openelis.stfu.domain.CaseTagDO;
import org.openelis.stfu.domain.CaseUserDO;
import org.openelis.stfu.manager.CaseManager;
import org.openelis.stfu.manager.CaseManagerAccessor;
import org.openelis.stfu.scanner.CaseFactory;
import org.openelis.stfu.scanner.ScannerRecord;


@RunWith(Arquillian.class)
public class TestScannerBean {
	
	@Inject
	ScannerBean scanner;
	
	ArrayList<ScannerRecord> records;
	HashMap<Integer, CaseManager> cases;
	
	@Deployment
	public static WebArchive deployment() {
		WebArchive arch =  Deployments.createBaseWithApplication()
				.addClasses(ScannerBean.class,SampleManager1.class,SampleManager1Accessor.class,CaseManager.class,CaseManagerAccessor.class,CaseAnalysisDO.class,CaseDO.class,
						    CasePatientDO.class,CaseTagDO.class,CaseResultDO.class,CaseSampleDO.class,CaseUserDO.class,CaseProviderDO.class,CaseFactory.class,ScannerRecord.class,TestDO.class,
						    AnalysisViewDO.class,AnalysisQaEventViewDO.class,SampleDO.class,SampleOrganizationViewDO.class)
			    .addAsDirectories("org.openelis.stfu.rules")
				.addAsResource(new File("src/org/openelis/stfu/rules/Case.drl"),"/org/openelis/stfu/rules/Case.drl")
				.addAsResource(new File("src/resources/META-INF/kmodule.xml"),"META-INF/kmodule.xml")
		        .addAsLibraries(Maven.resolver().resolve("org.drools:drools-core:6.2.0.Final").withTransitivity().asFile())
		        .addAsLibraries(Maven.resolver().resolve("org.drools:drools-compiler:6.2.0.Final").withTransitivity().asFile())
		        .addAsLibraries(Maven.resolver().resolve("org.jbpm:jbpm-bpmn2:6.2.0.Final").withTransitivity().asFile());
		System.out.println(arch.toString(true));
		return arch;
		  
	}
	
	@Before
	public void init() {
		cases = new HashMap<Integer,CaseManager>();
		records = new ArrayList<ScannerRecord>();
	}
	
	@Test
	public void transfused() throws Exception {
		ScannerRecord scannerRecord;
    	
		scannerRecord = createScannerRecord(1);
		scannerRecord.getSampleManager().getSampleNeonatal().setIsTransfused("Y");
    	records.add(scannerRecord);
        	
//    	scanner.applyRules(records, cases);
		assertEquals(1,cases.size());
		assertEquals(1,cases.get(1).tag.count());
		CaseManager cm = cases.get(1);
	    assertEquals(new Integer(1),CaseManagerAccessor.getCaseSamples(cm).get(0).getSampleId());
	    assertEquals(Datetime.getInstance(Datetime.YEAR,Datetime.DAY, new Date("2015/10/29")),CaseManagerAccessor.getCaseSamples(cm).get(0).getCollectionDate());
	}
	
    @Test
	public void poorQuality_Sample() throws Exception {
		ScannerRecord scannerRecord;
			
		scannerRecord = createScannerRecord(1);
    	SampleQaEventViewDO qa = new SampleQaEventViewDO();
    	qa.setTypeId(Constants.dictionary().QAEVENT_WARNING);
    	SampleManager1Accessor.addSampleQA(scannerRecord.getSampleManager(), qa);
    	records.add(scannerRecord);
    	
//    	scanner.applyRules(records, cases);
		assertEquals(1,cases.size());
		assertEquals(1,cases.get(1).tag.count());
	}
    
    @Test
    public void poorQuality_Analysis() throws Exception {
    	ScannerRecord scannerRecord;
    	
    	scannerRecord = createScannerRecord(1);
    	AnalysisViewDO analysis = new AnalysisViewDO();
    	analysis.setId(1);
    	SampleManager1Accessor.addAnalysis(scannerRecord.getSampleManager(), analysis);
    	AnalysisQaEventViewDO analysisQA = new AnalysisQaEventViewDO();
    	analysisQA.setTypeId(Constants.dictionary().QAEVENT_WARNING);
    	analysisQA.setAnalysisId(1);
    	SampleManager1Accessor.addAnalysisQA(scannerRecord.getSampleManager(), analysisQA);
    	records.add(scannerRecord);
    	
//    	scanner.applyRules(records, cases);
    	assertEquals(1,cases.size());
    	assertEquals(1,cases.get(1).tag.count());
    }
    
    @Test
    public void unknownWeight() throws Exception {
    	ScannerRecord scannerRecord;
    	
    	scannerRecord = createScannerRecord(1);
    	scannerRecord.getSampleManager().getSampleNeonatal().setWeight(null);
    	records.add(scannerRecord);
    	
//    	scanner.applyRules(records, cases);
    	assertEquals(1,cases.size());
    	assertEquals(1,cases.get(1).tag.count());
    }
    
    @Test
    public void invalidCollection() throws Exception {
    	ScannerRecord scannerRecord;
    	
    	scannerRecord = createScannerRecord(1);
    	scannerRecord.getSampleManager().getSampleNeonatal().setIsCollectionValid("N");
    	records.add(scannerRecord);
    	
//    	scanner.applyRules(records, cases);
    	assertEquals(1,cases.size());
    	assertEquals(1,cases.get(1).tag.count());
    }
    
    @Test 
    public void onePatientAllSampleLevelTags() throws Exception {
    	ScannerRecord scannerRecord;
    	
    	scannerRecord = createScannerRecord(1);
    	scannerRecord.getSampleManager().getSampleNeonatal().setIsTransfused("Y");
    	scannerRecord.getSampleManager().getSampleNeonatal().setIsCollectionValid("N");
    	scannerRecord.getSampleManager().getSampleNeonatal().setWeight(null);
    	SampleQaEventViewDO qa = new SampleQaEventViewDO();
    	qa.setTypeId(Constants.dictionary().QAEVENT_WARNING);
    	SampleManager1Accessor.addSampleQA(scannerRecord.getSampleManager(), qa);
    	records.add(scannerRecord);
    	
//    	scanner.applyRules(records, cases);
    	assertEquals(1,cases.size());
    	assertEquals(4,cases.get(1).tag.count());
    }
    
    @Test
    public void multipleRecords() throws Exception {
    	ScannerRecord scannerRecord;
    	
    	scannerRecord = createScannerRecord(1);
    	scannerRecord.getSampleManager().getSampleNeonatal().setIsTransfused("Y");
    	records.add(scannerRecord);
    	
    	scannerRecord = createScannerRecord(2);
    	scannerRecord.getSampleManager().getSampleNeonatal().setIsTransfused("Y");
    	records.add(scannerRecord);
    	
//    	scanner.applyRules(records, cases);
    	assertEquals(2, cases.size());
    	assertEquals(1, cases.get(1).tag.count());
    	assertEquals(1, cases.get(2).tag.count());
    }
    
    @Test
    public void currentCaseTransfused() throws Exception {
    	ScannerRecord scannerRecord;
    	
    	scannerRecord = createScannerRecord(1);
    	cases.put(1,CaseFactory.create(scannerRecord.getSampleManager()));
    	scannerRecord.getSampleManager().getSampleNeonatal().setIsTransfused("Y");
    	records.add(scannerRecord);
    	
//    	scanner.applyRules(records,cases);
    	assertEquals(1,cases.size());
    	assertEquals(1,cases.get(1).tag.count());
    }
	
	private ScannerRecord createScannerRecord(Integer patient) {
		ScannerRecord scannerRecord;
		SampleManager1 sampleManager;
		SampleDO sampleDO;
		SampleNeonatalViewDO neoView;
		SampleOrganizationViewDO organization;
		
		sampleManager = new SampleManager1();
		sampleDO = new SampleDO();
		sampleDO.setAccessionNumber(1);
		sampleDO.setId(1);
		sampleDO.setCollectionDate(Datetime.getInstance(Datetime.YEAR,Datetime.DAY, new Date("2015/10/29")));
		SampleManager1Accessor.setSample(sampleManager, sampleDO);
		organization = new SampleOrganizationViewDO();
		organization.setId(1);
		SampleManager1Accessor.addOrganization(sampleManager, organization);
    	neoView = new SampleNeonatalViewDO();
    	neoView.setPatientId(patient);
    	neoView.setIsCollectionValid("Y");
    	neoView.setWeight(6);
    	SampleManager1Accessor.setSampleNeonatal(sampleManager,neoView);
    	scannerRecord = new ScannerRecord();
    	scannerRecord.setSampleManager(sampleManager);
    	
    	return scannerRecord;
	}
}
