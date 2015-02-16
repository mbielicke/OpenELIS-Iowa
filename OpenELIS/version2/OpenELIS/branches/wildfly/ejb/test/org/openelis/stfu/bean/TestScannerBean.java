package org.openelis.stfu.bean;

import java.io.File;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openelis.deployment.Deployments;
import org.openelis.stfu.domain.Counter;
import org.openelis.stfu.domain.RulesTest;

@RunWith(Arquillian.class)
public class TestScannerBean {
	
	@Inject
	ScannerBean scanner;
	
	@Deployment
	public static WebArchive deployment() {
		WebArchive arch =  Deployments.createBase()
				.addClasses(ScannerBean.class,RulesTest.class,Counter.class,RulesBean.class)
				//.addAsDirectories("org/openelis/stfu/rules/")
				.addAsResource(new File("src/org/openelis/stfu/rules/Case.drl"),"/org/openelis/stfu/rules/Case.drl")
				.addAsResource(new File("src/org/openelis/stfu/rules/CaseFlow.bpmn"),"org/openelis/stfu/rules/CaseFlow.bpmn")
				.addAsResource(new File("src/org/openelis/stfu/rules/Sample.drl"),"org/openelis/stfu/rules/Sample.drl")
				.addAsManifestResource(new File("src/resources/META-INF/kmodule.xml"))
		        .addAsLibraries(Maven.resolver().resolve("org.drools:drools-core:6.1.0.Final").withTransitivity().asFile())
		        .addAsLibraries(Maven.resolver().resolve("org.drools:drools-compiler:6.1.0.Final").withTransitivity().asFile())
		        .addAsLibraries(Maven.resolver().resolve("org.jbpm:jbpm-bpmn2:6.1.0.Final").withTransitivity().asFile());
		System.out.println(arch.toString(true));
		return arch;
		  
	}
	
	@Test
	public void runScanner() throws Exception {
		scanner.scan();
	}

}
