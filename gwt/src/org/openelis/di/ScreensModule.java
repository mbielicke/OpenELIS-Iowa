package org.openelis.di;

import javax.inject.Singleton;

import org.openelis.cache.UserCacheService;
import org.openelis.constants.Messages;
import org.openelis.constants.OpenELISConstants;
import org.openelis.modules.method.client.MethodService;
import org.openelis.modules.method.client.MethodServiceImpl;

import dagger.Module;
import dagger.Provides;

@Module
public class ScreensModule {
	
	@Provides @Singleton MethodServiceImpl provideMethodService() {
		return MethodServiceImpl.INSTANCE;
	}
	
	@Provides @Singleton UserCacheService provideUserCacheService() {
		return UserCacheService.get();
	}
	
	@Provides @Singleton OpenELISConstants provideMessages() {
		return Messages.get();
	}

}
