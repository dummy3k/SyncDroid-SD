package de.syncdroid;

import android.app.Application;
import de.syncdroid.db.service.ProfileService;
import de.syncdroid.db.service.impl.ProfileServiceImpl;
import roboguice.config.AbstractAndroidModule;

public class SyncModule extends AbstractAndroidModule {
	private Application application;
	
	public SyncModule(Application application) {
		this.application = application;
	}
	
	
    @Override
    protected void configure() {
        /*
         * Here is the place to write the configuration specific to your application, i.e. your own custom bindings.
         */
        bind(ProfileService.class).to(ProfileServiceImpl.class);
        
        DatabaseHelper helper = new DatabaseHelper(application);
        bind(DatabaseHelper.class).toInstance(helper);
    }

}
