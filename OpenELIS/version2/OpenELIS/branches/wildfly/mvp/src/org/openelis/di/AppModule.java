package org.openelis.di;

import javax.inject.Singleton;

import org.openelis.client.OpenELISEntry;
import org.openelis.client.ScreenBus;
import org.openelis.ui.widget.Browser;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {
	
	@Provides
	Browser provideBrowser() {
		return OpenELISEntry.browser;
	}
	
	@Provides @Singleton ScreenBus providesBus() {
		return new ScreenBus();
	}

}
