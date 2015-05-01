package org.openelis.di;

import javax.inject.Singleton;

import org.openelis.cache.UserCacheService;
import org.openelis.modules.method.client.MethodPresenter;

import dagger.Component;

@Component(modules=ScreensModule.class)
@Singleton
public interface Screens {
	
	static final Screens INSTANCE = DaggerScreens.builder().screensModule(new ScreensModule()).build();
	
	MethodPresenter method();
	
}
