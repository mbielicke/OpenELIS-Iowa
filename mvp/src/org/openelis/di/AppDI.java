package org.openelis.di;

import javax.inject.Singleton;

import org.openelis.client.OpenELIS;
import org.openelis.modules.method.client.MethodPoint;

import dagger.Component;

@Component(modules = AppModule.class)
@Singleton
public interface AppDI {
	
	static final AppDI INSTANCE = Dagger_AppDI.builder().appModule(new AppModule()).build();
	
	MethodPoint methodPoint();
	
	OpenELIS    openelis();

}
