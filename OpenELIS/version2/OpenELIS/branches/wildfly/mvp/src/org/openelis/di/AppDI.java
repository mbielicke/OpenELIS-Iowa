package org.openelis.di;

import javax.inject.Singleton;

import org.openelis.client.OpenELISPresenter;
import org.openelis.modules.method.client.MethodPoint;
import org.openelis.modules.method.client.MethodPresenter;
import org.openelis.modules.organization.client.OrganizationPoint;
import org.openelis.modules.organization.client.OrganizationPresenter;

import dagger.Component;

@Component(modules = AppModule.class)
@Singleton
public interface AppDI {
	
	static final AppDI INSTANCE = Dagger_AppDI.builder().appModule(new AppModule()).build();
	
	MethodPoint methodPoint();
	
	OrganizationPoint organizationPoint();
	
	OpenELISPresenter openELIS();
	
	MethodPresenter method();
	
	OrganizationPresenter organization();
	

}
