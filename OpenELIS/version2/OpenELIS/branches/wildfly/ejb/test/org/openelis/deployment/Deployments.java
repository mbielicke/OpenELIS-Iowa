package org.openelis.deployment;

import java.io.File;

import org.eu.ingwar.tools.arquillian.extension.suite.annotations.ArquillianSuiteDeployment;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;

@ArquillianSuiteDeployment
public class Deployments {
	
	@Deployment
	public static WebArchive createIntegration() {
		return createBase()
			    .addPackage("org.openelis.bean")
				.addPackage("org.openelis.entity")
				.addPackage("org.openelis.manager")
				.addPackage("org.openelis.scriptlet")
		        .addPackage("org.openelis.utilcommon")
		        .addPackage("org.openelis.gwt.common")
				.addPackages(true,"org.openelis.report")
				.addAsLibraries(Maven.resolver().resolve("net.sf.jasperreports:jasperreports:4.0.1").withTransitivity().asFile())
				.addAsLibraries(Maven.resolver().resolve("org.apache.poi:poi:3.6").withTransitivity().asFile())
		        .addAsLibraries(Maven.resolver().resolve("net.lightoze.gwt-i18n-server:gwt-i18n-server:0.22").withoutTransitivity().asFile());
	}
	
	public static WebArchive createBase() {
		return ShrinkWrap.create(WebArchive.class)
				.addAsLibraries(jarupCommon())
				.addAsLibraries(new File("../OpenELIS-GWT/war/WEB-INF/lib/gwt-servlet.jar"))
				.addAsLibraries(new File("../OpenELIS-UI/ui.jar"))
				.addAsLibraries(Maven.resolver().resolve("net.sf.ehcache:ehcache:1.2").withoutTransitivity().asFile())
				.addAsResource(new File("src/resources/META-INF/persistence.xml"),"META-INF/persistence.xml")
				.addAsResource(new File("src/resources/META-INF/ehcache.xml"),"META-INF/ehcache.xml")
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
	}
	
	public static JavaArchive jarupCommon() {
		return ShrinkWrap.create(JavaArchive.class)
				.addPackage("org.openelis.meta")
				.addPackage("org.openelis.domain")
				.addPackage("org.openelis.constants")
				.addPackage("org.openelis.exception")
				.addPackage("org.openelis.utils")
				.addPackage("org.openelis.util");
	}
	
}
