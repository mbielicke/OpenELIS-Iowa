package org.openelis.deployment;

import java.io.File;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;

public class Deployment {

	public static WebArchive createBaseDeployment() {
		return ShrinkWrap.create(WebArchive.class)
				.addAsLibraries(jarupCommon())
				.addAsLibraries(new File("../OpenELIS-GWT/war/WEB-INF/lib/gwt-servlet.jar"))
				.addAsLibraries(new File("../OpenELIS-UI/ui.jar"))
				.addAsLibraries(Maven.resolver().resolve("net.sf.ehcache:ehcache:1.2").withTransitivity().asFile())
				.addAsResource("resources/META-INF/test-persistence.xml","META-INF/persistence.xml")
				.addAsResource("resources/META-INF/ehcache.xml","META-INF/ehcache.xml")
				.addAsWebInfResource("resources/META-INF/jbossas-ds.xml","jbossas-ds.xml")
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
	}
	
	public static JavaArchive jarupCommon() {
		return ShrinkWrap.create(JavaArchive.class)
				.addPackage("org.openelis.meta")
				.addPackage("org.openelis.domain")
				.addPackage("org.openelis.constants")
				.addPackage("org.openelis.utils");
	}
}
