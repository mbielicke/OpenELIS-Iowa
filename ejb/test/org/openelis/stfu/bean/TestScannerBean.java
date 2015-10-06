package org.openelis.stfu.bean;

import java.io.File;

import javax.inject.Inject;

import static org.junit.Assert.*;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.util.file.JarArchiveBrowser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openelis.deployment.Deployments;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.TestDO;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.SampleManager1Accessor;
import org.openelis.stfu.domain.CaseAnalysisDO;
import org.openelis.stfu.domain.CaseDO;
import org.openelis.stfu.domain.CasePatientDO;
import org.openelis.stfu.domain.CaseProviderDO;
import org.openelis.stfu.domain.CaseResultDO;
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
	
	@Deployment
	public static WebArchive deployment() {
		WebArchive arch =  Deployments.createBase()
				.addClasses(ScannerBean.class,SampleManager1.class,SampleManager1Accessor.class,CaseManager.class,CaseManagerAccessor.class,CaseAnalysisDO.class,CaseDO.class,
						    CasePatientDO.class,CaseTagDO.class,CaseResultDO.class,CaseUserDO.class,CaseProviderDO.class,CaseFactory.class,ScannerRecord.class,TestDO.class,DictionaryDO.class)
			    .addAsDirectories("org.openelis.stfu.rules")
				.addAsResource(new File("src/org/openelis/stfu/rules/Case.drl"),"/org/openelis/stfu/rules/Case.drl")
				.addAsResource(new File("src/resources/META-INF/kmodule.xml"),"META-INF/kmodule.xml")
		        .addAsLibraries(Maven.resolver().resolve("org.drools:drools-core:6.2.0.Final").withTransitivity().asFile())
		        .addAsLibraries(Maven.resolver().resolve("org.drools:drools-compiler:6.2.0.Final").withTransitivity().asFile())
		        .addAsLibraries(Maven.resolver().resolve("org.jbpm:jbpm-bpmn2:6.2.0.Final").withTransitivity().asFile());
		System.out.println(arch.toString(true));
		return arch;
		  
	}
	
	@Test
	public void runScanner() throws Exception {
		assertEquals(2,scanner.scan().size());
	}

}
