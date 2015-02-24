package org.openelis.di;

import javax.inject.Singleton;

import org.openelis.client.OpenELIS;
import org.openelis.client.OpenELISViewImpl;
import org.openelis.modules.method.client.MethodPoint;
import org.openelis.modules.method.client.MethodPresenter;
import org.openelis.modules.organization.client.OrganizationPoint;
import org.openelis.modules.organization.client.OrganizationPresenter;
import org.openelis.modules.organization.client.OrganizationViewImpl;

import dagger.Component;

@Component(modules = AppModule.class)
@Singleton
public interface AppDI {
	
	static final AppDI INSTANCE = Dagger_AppDI.builder().appModule(new AppModule()).build();
	
	MethodPoint methodPoint();
	
	OrganizationPoint organizationPoint();
	
	OpenELIS openELIS();
	
	MethodPresenter method();
	
	OrganizationPresenter organization();
	

}
